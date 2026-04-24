package com.dreamtea.depths_beyond.effects;

import com.dreamtea.depths_beyond.DepthsBeyondMod;
import com.dreamtea.depths_beyond.dungeon.DepthsBeyondGame;
import com.dreamtea.depths_beyond.dungeon.DungeonRun;
import com.dreamtea.depths_beyond.effects.on_going.Trigger;
import com.dreamtea.depths_beyond.effects.on_going.TriggeredExecutable;
import com.dreamtea.depths_beyond.effects.on_going.TriggeredExecutableType;
import com.dreamtea.depths_beyond.effects.on_going.contexts.EntityHitContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.EntitySummonContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerContext;
import com.dreamtea.depths_beyond.effects.on_going.contexts.TriggerHistory;
import com.dreamtea.depths_beyond.effects.types.DungeonIntegerProvider;
import com.dreamtea.depths_beyond.effects.types.ExecutableType;
import com.dreamtea.depths_beyond.effects.types.FloatComparison;
import com.dreamtea.depths_beyond.effects.types.PredicateType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandEffects {
    public record IfCommand(String command, String name, IntProvider number, FloatComparison comparison) implements CardPredicate {
        public static final MapCodec<IfCommand> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("command").forGetter(IfCommand::command),
                Codec.STRING.optionalFieldOf("name", "Execute Command Predicate").forGetter(IfCommand::name),
                IntProviders.CODEC.optionalFieldOf("number", ConstantInt.ZERO).forGetter(IfCommand::number),
                FloatComparison.CODEC.optionalFieldOf("comparison", FloatComparison.GREATER_THEN).forGetter(IfCommand::comparison)
        ).apply(instance, IfCommand::new));
        @Override
        public boolean check(DungeonRun executingPlayer, DepthsBeyondGame game) {
            int output = execute(executingPlayer.getPlayer(), executingPlayer.getPlayer().level(), command, name);
            return comparison.test(output, DungeonIntegerProvider.sample(number, executingPlayer.getRandom(), executingPlayer, game));
        }
        public static final String DESCRIPTION = """
                Executes the given command, then returns true if output is 'comparison' 'number'.
                'name' is an optional field for debugging.
                """;
        @Override
        public PredicateType<?> getType() {
            return PredicateType.IF_COMMAND;
        }
    }
    public record ExecuteCommand(String command, String name) implements CardExecutable {
        public static final MapCodec<ExecuteCommand> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("command").forGetter(ExecuteCommand::command),
                Codec.STRING.optionalFieldOf("name", "Execute Command Card").forGetter(ExecuteCommand::name)
        ).apply(instance, ExecuteCommand::new));
        public static final String DESCRIPTION = """
                Executes the given 'command'.
                'name' is an optional field for debugging.
                """;
        @Override
        public void cast(DungeonRun executingPlayer, DepthsBeyondGame game) {
            execute(executingPlayer.getPlayer(), executingPlayer.getPlayer().level(), command, name);
        }

        @Override
        public ExecutableType<?> getType() {
            return ExecutableType.EXECUTE_COMMAND;
        }
    }

    public record ExecuteTriggeredCommand(String command, String name) implements TriggeredExecutable {
        public static final MapCodec<ExecuteTriggeredCommand> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("command").forGetter(ExecuteTriggeredCommand::command),
                Codec.STRING.optionalFieldOf("name", "Execute Command Card").forGetter(ExecuteTriggeredCommand::name)
        ).apply(instance, ExecuteTriggeredCommand::new));
        public static final String DESCRIPTION = """
                Executes the given 'command' as the entity hit or summoned.
                'name' is an optional field for debugging.
                """;

        @Override
        public void execute(Trigger trigger, TriggerContext context, TriggerHistory history) {
            if(context instanceof EntityHitContext damageContext){
                CommandEffects.execute(damageContext.hit, context.game.level(), command, name);
            } else if (context instanceof EntitySummonContext damageContext) {
                CommandEffects.execute(damageContext.entity, context.game.level(), command, name);
            }

        }

        @Override
        public TriggeredExecutableType<?> getType() {
            return TriggeredExecutableType.COMMAND;
        }
    }

    private static int execute(Entity player, ServerLevel world, String command, String name) {
        MinecraftServer minecraftServer = world.getServer();
        var executor = new EffectCommandExecutor(player, name);
        AtomicInteger output = new AtomicInteger();
        if (!StringUtils.isEmpty(command)) {
            try {
                CommandSourceStack serverCommandSource = executor.getSource().withCallback((successful, returnValue) -> {
                    output.set(returnValue);
                });
                minecraftServer.getCommands().performPrefixedCommand(serverCommandSource, command);
            } catch (Throwable var6) {
                CrashReport crashReport = CrashReport.forThrowable(var6, "Failed to run Tigger Command");
                CrashReportCategory commandDetails = crashReport.addCategory("Region");
                commandDetails.setDetail("Name", name );
                commandDetails.setDetail("command", command);
                CrashReportCategory playerDetails = crashReport.addCategory("Player");
                playerDetails.setDetail("playerName", player.getDisplayName());
                playerDetails.setDetail("player UUID", player.getUUID());
                throw new ReportedException(crashReport);
            }
        }
        return output.get();
    }

    static class EffectCommandExecutor implements CommandSource {
        private final Entity player;
        private final String name;
        public EffectCommandExecutor(Entity player, String name){
            this.player = player;
            this.name = name;
        }

        public CommandSourceStack getSource() {
            return new CommandSourceStack(
                    this,
                    player.position(),
                    player.getRotationVector(),
                    (ServerLevel) player.level(),
                    LevelBasedPermissionSet.MODERATOR,
                    name,
                    Component.literal(name),
                    Objects.requireNonNull(player.level().getServer()),
                    player
            );
        }

        @Override
        public void sendSystemMessage(Component message) {
            DepthsBeyondMod.LOGGER.info(message.getString());
        }

        @Override
        public boolean acceptsSuccess() {
            return true;
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }
    }
}
