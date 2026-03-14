package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ReviveSequenceManager {
    private static ReviveSequence active = null;

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (active != null) {
                boolean finished = active.tick();
                if (finished) active = null;
            }
        });
    }

    public static void start(ServerPlayerEntity target, BlockPos blockPos, String typedName, ServerPlayerEntity sender) {
        if (active == null) {active = new ReviveSequence(target, blockPos, typedName, sender);}
    }
}
