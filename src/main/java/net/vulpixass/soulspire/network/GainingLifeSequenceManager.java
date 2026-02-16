package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class GainingLifeSequenceManager {
    private static GainingLifeSequence active = null;
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (active != null) {
                boolean finished = active.tick();
                if (finished) active = null;
            }
        });
    }
    public static void start(ServerPlayerEntity target) {
        if (active == null) {active = new GainingLifeSequence(target);}
    }
}
