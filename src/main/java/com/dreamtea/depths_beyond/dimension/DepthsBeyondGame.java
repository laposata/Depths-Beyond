package com.dreamtea.depths_beyond.dimension;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.dungeon.regions.*;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.imixin.IPlayDepthsBelow;
import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import com.dreamtea.depths_beyond.items.DungeonCompass;
import com.dreamtea.depths_beyond.items.DungeonTool;
import com.dreamtea.depths_beyond.nucleoid.DBGamePlayerEvents;
import com.dreamtea.depths_beyond.stats.GameConstants;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.MapTemplateSerializer;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.player.JoinOfferResult;
import xyz.nucleoid.plasmid.api.game.world.generator.TemplateChunkGenerator;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.dreamtea.depths_beyond.DepthsBeyondMod.ofDB;


public class DepthsBeyondGame {
    private final DepthsBeyondConfig config;
    private final GameSpace gameSpace;
    private final ServerWorld world;
    private final Map<UUID, DungeonRun> playerStates;
    private final Map<RegionType, List<Region>> regions;

    public DepthsBeyondGame(DepthsBeyondConfig config, GameSpace gameSpace,Map<RegionType, List<Region>> regions, ServerWorld world) {
        this.config = config;
        this.gameSpace = gameSpace;
        this.world = world;
        playerStates = new HashMap<>();
        this.regions = regions;

    }

    public static GameOpenProcedure open(GameOpenContext<DepthsBeyondConfig> context) {
        DepthsBeyondConfig config = context.config();
        MapTemplate template;
        try {
            template = MapTemplateSerializer.loadFromResource(context.server(), ofDB("test"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        template.setBlockState(new BlockPos(0, 64, 0), Blocks.STONE.getDefaultState());

        TemplateChunkGenerator generator = new TemplateChunkGenerator(context.server(), template);
        RuntimeWorldConfig worldConfig = new RuntimeWorldConfig().setGenerator(generator).setTimeOfDay(6000);
        return context.openWithWorld(worldConfig, (activity, world) -> {
            var regions = RegionType.sortRegions(template.getMetadata().getRegions(), world, config);
            DepthsBeyondGame game = new DepthsBeyondGame(config, activity.getGameSpace(), regions, world);
            activity.listen(GamePlayerEvents.OFFER, game::onPlayerOffer);
            activity.listen(GamePlayerEvents.ADD, game::onPlayerAdd);
            activity.listen(GamePlayerEvents.ACCEPT, game::onPlayerAccept);
            activity.listen(GameActivityEvents.TICK, game::onTick);
            activity.listen(GamePlayerEvents.LEAVE, game::onPlayerLeave);
            activity.listen(GamePlayerEvents.REMOVE, game::onPlayerLeave);
            activity.listen(DBGamePlayerEvents.LAND, game::onPlayerLand);
            if(world instanceof ITrackGameRuns itgr){
                itgr.setGame(game);
            }
        });
    }

    private Region start(){
        return regions.get(RegionType.START).getFirst();
    }
    private void onPlayerLeave(ServerPlayerEntity player) {
        DungeonRun playerState = playerStates.get(player.getUuid());
        playerState.finalizePlayerInventory(player);
        if(player instanceof IPlayDepthsBelow ipdb){
            ipdb.leaveRun();
        }
    }

    private JoinAcceptorResult onPlayerAccept(JoinAcceptor acceptor) {
        PlayerManager playerManager = world.getServer().getPlayerManager();
        acceptor.playerIds().forEach(p -> {
            var player = playerManager.getPlayer(p);
            var playerState =  new DungeonRun(player);
            playerStates.put(player.getUuid(), playerState);
            playerState.cachePlayerInventory();
            if(player instanceof IPlayDepthsBelow ipdb){
                ipdb.joinRun(playerState);
            }
        });
        return acceptor.teleport(world,
                start().getRegion().getBounds().center());
    }
    private JoinOfferResult onPlayerOffer(JoinOffer offer) {
        world.setSpawnPos(new BlockPos(0, 64, 0), 0);
        return offer.accept();
    }

    private void onPlayerAdd(ServerPlayerEntity player) {
        player.sendMessage(Text.of(config.message()));
        var random = player.getRandom();
        var randomGoal = regions.get(RegionType.GOAL).get(random.nextBetween(0, regions.get(RegionType.GOAL).size() - 1));
        if(player.getGameMode().equals(GameMode.SURVIVAL)){
            player.changeGameMode(GameMode.ADVENTURE);
        }
        playerStates.get(player.getUuid()).initPlayerInventory();
        var compass = DungeonCompass.createCompass(randomGoal.getRegion().getBounds().min(), randomGoal.getRegion().getBounds().max());
        player.getInventory().setStack(PlayerInventory.OFF_HAND_SLOT, compass);
        DungeonCompass.updateLocation(player, world.getRandom(), 0, world);
        player.getInventory().setStack(1, DungeonTool.createTool(Items.IRON_PICKAXE.getDefaultStack()));
        player.getInventory().setStack(2, DungeonTool.createBlock(32));

    }

    public void onTick(){
       world.getPlayers().forEach(p -> {
           var pos = p.getPos();
           var state = playerStates.get(p.getUuid());
           if (!state.hasStartedRun()){
               boolean insideStart = start().getRegion().getBounds().asBox().contains(p.getPos());
               if(insideStart) return;
               state.startRun();
           }
           tickLoot(p, state);
           regions.get(RegionType.HUNTER).forEach(r -> ((HunterRegion)r).tickHunter(p, world));
           state.tickPlayer(p);
           if(DungeonCompass.completeCompass(p)){
               world.spawnParticles(DustParticleEffect.DEFAULT, pos.x, pos.y, pos.z, 10, 2, 2, 2, 5);
               state.findGoal();
           }
           if (start().getRegion().getBounds().asBox().contains(pos)) {
               if(state.hasFoundGoal()){
                   gameSpace.getPlayers().kick(p);
               }
           }
           if(world.getRandom().nextInt(GameConstants.BASE_CHANCE_SCALE) < GameConstants.BASE_COMPASS_WAVER_CHANCE){
               DungeonCompass.updateLocation(p, world.getRandom(), 10, world);
           }
           if(p.age % 20 == 0){
               regions.get(RegionType.ENEMY).forEach(r -> ((MobSpawnerRegion)r).summonMob(p, world));
           }
       });
    }

    private void tickLoot(ServerPlayerEntity player, DungeonRun run){
        LootRegion.tickLoot(regions.get(RegionType.LOOT), player, run);
    }

    public int openGates(String name){
        return regions.get(RegionType.GATE).stream().filter(r -> ((GateRegion)r).name.equals(name)).peek(r -> ((GateRegion)r).openGate()).collect(Collectors.toSet()).size();
    }
    public int closeGate(String name){
        return regions.get(RegionType.GATE).stream().filter(r -> ((GateRegion)r).name.equals(name)).peek(r -> ((GateRegion)r).closeGate()).collect(Collectors.toSet()).size();
    }
    public List<DungeonRun> getPlayers(Collection<UUID> players){
        return players.stream().map(this.playerStates::get).toList();
    }

    public void onPlayerLand(ServerPlayerEntity player){
        playerStates.get(player.getUuid()).resetPlayerState(player);
    }
}
