package net.vulpixass.soulspire.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.permission.Permissions;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.vulpixass.soulspire.network.LivesStore;
import net.vulpixass.soulspire.network.PlayerSoulData;
import net.vulpixass.soulspire.network.SoulDataS2CPayload;

public class LivesCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(CommandManager.literal("setlives")
                .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> {
                                    ServerCommandSource source = ctx.getSource();
                                    var executor = source.getPlayer();
                                    if (executor == null || !source.getServer().getPlayerManager().isOperator(executor.getPlayerConfigEntry())) {
                                        source.sendError(Text.literal("§cYou must be an operator to use this command."));
                                        return 0;
                                    }
                                    ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                    PlayerSoulData data = LivesStore.get().playerLives.get(target.getUuid());
                                    if (data == null) {
                                        data = new PlayerSoulData(amount);
                                        LivesStore.get().playerLives.put(target.getUuid(), data);
                                    } else {
                                        data.lives = amount;
                                        LivesStore.get().updatePlayerDisplayName(target);
                                        target.getEntityWorld().getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, target));
                                    }

                                    target.sendMessage(Text.literal("Your lives have been set to " + amount), false);
                                    source.sendFeedback(() -> Text.literal("Set " + target.getName().getString() + "'s lives to " + amount),true);

                                    int updated = LivesStore.get().outputLives(target.getUuid());
                                    ServerPlayNetworking.send(target, new SoulDataS2CPayload(updated));
                                    return 1;
                                })
                        ))
        );
        dispatcher.register(CommandManager.literal("getlives")
                .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerCommandSource source = ctx.getSource();
                            var executor = source.getPlayer();
                            if (executor == null || !source.getServer().getPlayerManager().isOperator(executor.getPlayerConfigEntry())) {
                                source.sendError(Text.literal("§cYou must be an operator to use this command."));
                                return 0;
                            }
                            ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            PlayerSoulData data = LivesStore.get().playerLives.get(target.getUuid());
                            if (data != null) {
                                source.sendFeedback(() -> Text.literal(target.getName().getString() + "'s lives are " + data.lives), true);
                            } else {
                                source.sendFeedback(() -> Text.literal("§c" + target.getName().getString() + "'s lives are undefined, please make sure the player has joined the Server"), true);
                            }
                            return 1;
                        })
                ));
        dispatcher.register(CommandManager.literal("hasCatalyst")
                .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("trueorfalse", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    ServerCommandSource source = ctx.getSource();
                                    var executor = source.getPlayer();
                                    if (executor == null || !source.getServer().getPlayerManager().isOperator(executor.getPlayerConfigEntry())) {
                                        source.sendError(Text.literal("§cYou must be an operator to use this command."));
                                        return 0;
                                    }
                                    ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                                    PlayerSoulData data = LivesStore.get().playerLives.get(target.getUuid());

                                    boolean hasCatalystBoolean = BoolArgumentType.getBool(ctx, "trueorfalse");
                                    data.hasCatalyst = hasCatalystBoolean;

                                    target.sendMessage(Text.literal("Your hasCatalyst value has been set to " + hasCatalystBoolean), false);
                                    source.sendFeedback(() -> Text.literal(target.getName().getLiteralString() + "'s hasCatalyst value has been set to " + hasCatalystBoolean),true);
                                    return 1;
                                })
                        )
        ));
        dispatcher.register(CommandManager.literal("getHasCatalyst")
                .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerCommandSource source = ctx.getSource();
                            var executor = source.getPlayer();
                            if (executor == null || !source.getServer().getPlayerManager().isOperator(executor.getPlayerConfigEntry())) {
                                source.sendError(Text.literal("§cYou must be an operator to use this command."));
                                return 0;
                            }
                            ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                            PlayerSoulData data = LivesStore.get().playerLives.get(target.getUuid());
                            if (data != null) {
                                source.sendFeedback(() -> Text.literal(target.getName().getString() + "'s hasCatalyst Value is " + data.hasCatalyst), true);
                            } else {
                                source.sendFeedback(() -> Text.literal("§c" + target.getName().getString() + "'s hasCatalyst Value is undefined, please make sure the player has joined the Server"), true);
                            }
                            return 1;
                        })
                ));
    }
}
