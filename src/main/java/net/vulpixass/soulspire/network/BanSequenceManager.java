package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class BanSequenceManager {
    private static BanSequence active = null;

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (active != null) {
                boolean finished = active.tick();
                if (finished) active = null;
            }
            Scoreboard scoreboard = server.getScoreboard();
            Team team = scoreboard.getTeam("soul_glow");
            if (team == null) {
                team = scoreboard.addTeam("soul_glow");
                team.setColor(Formatting.DARK_PURPLE);
            }
        });
    }

    public static void start(ServerPlayerEntity victim, ServerPlayerEntity attacker) {
        if (active == null) {active = new BanSequence(victim, attacker);}
    }
}

