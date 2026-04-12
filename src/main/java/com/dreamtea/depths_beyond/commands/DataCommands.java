package com.dreamtea.depths_beyond.commands;

import com.dreamtea.depths_beyond.cards.Card;
import com.dreamtea.depths_beyond.cards.CardRegistry;
import com.dreamtea.depths_beyond.cards.text.Keyword;
import com.dreamtea.depths_beyond.cards.text.KeywordRegistry;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.permissions.Permissions;

import java.util.Collection;

public class DataCommands {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("data")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                            .then(Commands.literal("cards").executes(DataCommands::executeListCards))
                            .then(Commands.literal("keywords").executes(DataCommands::executeListKeywords)
                            )
                        );
        });
    }

    private static int executeListCards(CommandContext<CommandSourceStack> c) {
        Collection<Card> allCards = CardRegistry.get().getAllCards();
        CommandSourceStack source = c.getSource();
        String output = allCards.stream().map(Card::toString).reduce((a, b) -> a + "\n" + b).orElse("");
        OutgoingChatMessage tracked = OutgoingChatMessage.create(PlayerChatMessage.system(output));
        ChatType.Bound outgoingChatType = ChatType.bind(ChatType.SAY_COMMAND, source);

        c.getSource().sendChatMessage(tracked, false, outgoingChatType);
        return allCards.size();
    }
    private static int executeListKeywords(CommandContext<CommandSourceStack> c) {
        Collection<Keyword> allKeywords = KeywordRegistry.getAllKeywords();
        CommandSourceStack source = c.getSource();
        String output = allKeywords.stream().map(Keyword::toString).reduce((a, b) -> a + "\n" + b).orElse("");
        OutgoingChatMessage tracked = OutgoingChatMessage.create(PlayerChatMessage.system(output));
        ChatType.Bound outgoingChatType = ChatType.bind(ChatType.SAY_COMMAND, source);

        c.getSource().sendChatMessage(tracked, false, outgoingChatType);
        return allKeywords.size();
    }
}
