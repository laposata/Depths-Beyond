package com.dreamtea.depths_beyond.commands.argument;

import com.dreamtea.depths_beyond.stats.StatType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.resources.Identifier.ERROR_INVALID;

public class StatArgumentType implements ArgumentType<StatType> {
    private static final Collection<String> EXAMPLES = Stream.of(StatType.FEAR, StatType.FOCUS, StatType.DECADENCE)
            .map(StatType::getName)
            .collect(Collectors.toList());
    private static final StatType[] VALUES = StatType.values();

    public StatType parse(final StringReader reader) throws CommandSyntaxException {
        String statTypeString = reader.readUnquotedString();
        StatType statType = StatType.byName(statTypeString);
        if (statType == null) {
            throw ERROR_INVALID.createWithContext(reader);
        } else {
            return statType;
        }
    }
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return context.getSource() instanceof SharedSuggestionProvider
                ? SharedSuggestionProvider.suggest(Arrays.stream(VALUES).map(StatType::getName), builder)
                : Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static StatArgumentType statType() {
        return new StatArgumentType();
    }

    public static StatType getStatType(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
        return context.getArgument(name, StatType.class);
    }
}
