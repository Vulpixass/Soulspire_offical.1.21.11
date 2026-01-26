package net.vulpixass.soulspire.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.permission.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.vulpixass.soulspire.network.LivesStore;
import net.vulpixass.soulspire.network.PlayerSoulData;

public class LivesCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(CommandManager.literal("setlives")
                .then(CommandManager.argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 3))
                                .executes(ctx -> {
                                    ServerCommandSource source = ctx.getSource();
                                    var executor = source.getPlayer();
                                    if (executor == null || !source.getServer().getPlayerManager().isOperator(executor.getPlayerConfigEntry())) {
                                        source.sendError(Text.literal("You must be an operator to use this command."));
                                        return 0;
                                    }
                                    ServerPlayerEntity target = net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                    PlayerSoulData data = LivesStore.get().playerLives.get(target.getUuid());
                                    if (data == null) {
                                        data = new PlayerSoulData(amount);
                                        LivesStore.get().playerLives.put(target.getUuid(), data);
                                    } else {data.lives = amount;}

                                    target.sendMessage(Text.literal("Your lives have been set to " + amount), false);

                                    source.sendFeedback(() -> Text.literal("Set " + target.getName().getString() + "'s lives to " + amount),true);

                                    return 1;
                                })
                        ))
        );
    }
}
