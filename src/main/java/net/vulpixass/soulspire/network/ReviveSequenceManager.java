package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
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

    public static void start(Entity target, BlockPos blockPos) {
        if (active == null) {active = new ReviveSequence(target, blockPos);}
    }
}
