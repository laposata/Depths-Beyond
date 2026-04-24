package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.cards.CardRegistry;
import com.dreamtea.depths_beyond.cards.text.Keyword;
import com.dreamtea.depths_beyond.cards.text.KeywordRegistry;
import com.dreamtea.depths_beyond.cards.text.SpellbookWriter;
import com.dreamtea.depths_beyond.config.DepthsBeyondConfig;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.CardExecutable;
import com.dreamtea.depths_beyond.imixin.ITrackGameRuns;
import com.dreamtea.depths_beyond.temp.GameSpace;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DataCommands {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("generated")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                            .then(Commands.literal("cards").executes(DataCommands::executeListCards))
                            .then(Commands.literal("keywords").executes(DataCommands::executeListKeywords))
                            .then(Commands.literal("books").executes(DataCommands::executeBooks))
                            .then(Commands.literal("run").executes(DataCommands::startRun))
                            .then(Commands.literal("start").executes(DataCommands::start))
                        );
        });
    }

    private static int executeListCards(CommandContext<CommandSourceStack> c) {
        Collection<Card> allCards = CardRegistry.get().getAllCards();
        Component output = allCards.stream()
                .map(card -> (MutableComponent)(card.processDescription()))
                .reduce((a, b) -> a.append(Component.literal("\n").append(b)))
                .orElse(Component.empty());
        c.getSource().sendSystemMessage(output);
        return allCards.size();
    }
    private static int executeListKeywords(CommandContext<CommandSourceStack> c) {
        Collection<Keyword> allKeywords = KeywordRegistry.getAllKeywords();
        MutableComponent output = allKeywords.stream()
                .map(Keyword::description)
                .reduce((a, b) -> a.append(Component.literal("\n").append(b)))
                .orElse(Component.empty());
        c.getSource().sendSystemMessage(output);
        return allKeywords.size();
    }
    private static int executeBooks(CommandContext<CommandSourceStack> c) {
        Collection<Card> allCards = CardRegistry.get().getAllCards();
        ServerPlayer player = c.getSource().getPlayer();
        Vec3 pos = player.position();
        ServerLevel level = c.getSource().getLevel();
        allCards.stream().map(SpellbookWriter::createBook).forEach(s -> {
            level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, s));
        });

        return 1;
    }
    private static int startRun(CommandContext<CommandSourceStack> c){
        ServerPlayer player = c.getSource().getPlayer();
        ServerLevel world = c.getSource().getLevel();
        if(world instanceof ITrackGameRuns itgr){
            DepthsBeyondGame g = new DepthsBeyondGame(
                    new DepthsBeyondConfig("", Map.of()),
                    new GameSpace(),
                    List.of(),
                    world
            );
            itgr.setGame(g);
            g.addPlayer(player);
            return 1;
        }
        return 0;
    }
    private static int start(CommandContext<CommandSourceStack> c){
        ServerPlayer player = c.getSource().getPlayer();
        ServerLevel world = c.getSource().getLevel();
        if(world instanceof ITrackGameRuns itgr){
            DepthsBeyondGame g = itgr.getGame();
            List<DungeonRun> players = g.getPlayers(List.of(player.getUUID()));
            players.get(0).startRun(g.getGameTime());
            return 1;
        }
        return 0;
    }

}
