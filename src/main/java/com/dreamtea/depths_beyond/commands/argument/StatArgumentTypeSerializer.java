package com.dreamtea.depths_beyond.commands.argument;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public class StatArgumentTypeSerializer implements ArgumentTypeInfo<StatArgumentType, StatArgumentTypeSerializer.Template> {
    public void serializeToNetwork(final StatArgumentTypeSerializer.Template template, final FriendlyByteBuf out) {
    }

    public StatArgumentTypeSerializer.Template deserializeFromNetwork(final FriendlyByteBuf in) {
        return new StatArgumentTypeSerializer.Template();
    }

    public void serializeToJson(final StatArgumentTypeSerializer.Template template, final JsonObject out) {}

    public StatArgumentTypeSerializer.Template unpack(final StatArgumentType argument) {
        return new StatArgumentTypeSerializer.Template();
    }

    public final class Template implements ArgumentTypeInfo.Template<StatArgumentType> {
        public Template() {
            super();
        }

        public StatArgumentType instantiate(final CommandBuildContext context) {
            return StatArgumentType.statType();
        }

        @Override
        public ArgumentTypeInfo<StatArgumentType, ?> type() {
            return StatArgumentTypeSerializer.this;
        }
    }
}
