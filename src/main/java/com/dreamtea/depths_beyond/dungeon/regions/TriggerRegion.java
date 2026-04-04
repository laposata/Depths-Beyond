package com.dreamtea.depths_beyond.dungeon.regions;

import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.data.region_data.TriggerRegionData;
import com.dreamtea.depths_beyond.temp.TemplateRegion;
import com.dreamtea.depths_beyond.utils.RegionUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import org.apache.commons.lang3.StringUtils;

//import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TriggerRegion extends Region {

    private final int maxTriggers;
    private final int initialDelay;
    private final int recurringDelay;
    private final boolean playerUnique;
    private final String command;

    private final Map<UUID, Long> playerTriggers;
    private final Map<UUID, Long> playerEnters;
    private final Map<UUID, TriggerCommandExecutor> executors;
    private int totalTriggers;

    public TriggerRegion(
            TemplateRegion region,
            ServerLevel world,
            String regionName,
            String groupName,
            DepthsBeyondConfig config
    ) {
        super(region, world, regionName, groupName, config);
        var data = RegionUtils.getData(TriggerRegionData.CODEC, region);
        if(data == null ){
            data = TriggerRegionData.BLANK;
        }
        maxTriggers = data.maxTriggers().sample(world.getRandom());
        initialDelay = data.initialDelay().sample(world.getRandom());
        recurringDelay = data.recurringDelay().sample(world.getRandom());
        playerUnique = data.playerUnique();
        command = data.command();
        playerTriggers = new HashMap<>();
        executors = new HashMap<>();
        playerEnters = new HashMap<>();
    }

    private void execute(ServerPlayer player) {
        MinecraftServer minecraftServer = world.getServer();
        var executor = executors.computeIfAbsent(player.getUUID(), k -> new TriggerCommandExecutor(player));
        if (!StringUtils.isEmpty(this.command)) {
            try {
                CommandSourceStack serverCommandSource = executor.getSource().withCallback((successful, returnValue) -> {
                    if (successful) {
                        totalTriggers ++;
                        playerTriggers.put(player.getUUID(), world.getGameTime());
                    }
                });
                minecraftServer.getCommands().performPrefixedCommand(serverCommandSource, this.command);
            } catch (Throwable var6) {
                CrashReport crashReport = CrashReport.forThrowable(var6, "Failed to run Tigger Command");
                CrashReportCategory commandDetails = crashReport.addCategory("Region");
                commandDetails.setDetail("Name", regionName );
                commandDetails.setDetail("command", command);
                CrashReportCategory playerDetails = crashReport.addCategory("Player");
                playerDetails.setDetail("playerName", player.getDisplayName());
                playerDetails.setDetail("player UUID", player.getUUID());
                CrashReportCategory triggerDetails = crashReport.addCategory("Trigger");
                triggerDetails.setDetail("totalTriggers", totalTriggers);
                triggerDetails.setDetail("triggers by this player", playerTriggers.get(player.getUUID()));
                throw new ReportedException(crashReport);
            }
        }
    }

    public void tick(ServerPlayer player) {
        var lastTrigger = playerTriggers.get(player.getUUID()
        );
            if(playerUnique && lastTrigger != null) {
            //If this can only trigger once per person, and it has triggered for this person, stop all tick processing
            return;
        }
        if(maxTriggers > -1
                && totalTriggers >= maxTriggers){
            return;
        }
        var lastEntered = playerEnters.get(player.getUUID());
        var playerPresent = getRegion().getBounds().asBox().contains(player.position());
        if(!playerPresent){
            if(lastEntered != null){
                //If the player has min record of entering the area but currently is not in it, remove the entry
                playerEnters.remove(player.getUUID());
                playerTriggers.remove(player.getUUID());
            }
            //If the player is not present stop any remaining actions;
            return;
        }

        if(lastEntered == null){
            playerEnters.put(player.getUUID(), world.getGameTime());
            lastEntered = world.getGameTime();
        }
        if(lastTrigger == null){
            if(world.getGameTime() - lastEntered >= initialDelay){
                execute(player);
            }
        } else {
            if(recurringDelay > 0){
                if(world.getGameTime() - lastTrigger >= recurringDelay){
                    execute(player);
                }
            }
        }
    }


    class TriggerCommandExecutor implements CommandSource {

        private final ServerPlayer player;
        public TriggerCommandExecutor(ServerPlayer player){
            this.player = player;
        }

        public CommandSourceStack getSource() {
            return new CommandSourceStack(
                    this,
                    player.position(),
                    player.getRotationVector(),
                    world,
                    LevelBasedPermissionSet.MODERATOR,
                    regionName,
                    Component.literal(regionName),
                    world.getServer(),
                    player
            );
        }

        @Override
        public void sendSystemMessage(Component message) {

        }

        @Override
        public boolean acceptsSuccess() {
            return false;
        }

        @Override
        public boolean acceptsFailure() {
            return false;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }
    }
}
