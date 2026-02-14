package net.vulpixass.soulspire.network;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEntityData {
    public int timer;
    public int ban_timer;
    public boolean lethal;
    public ServerPlayerEntity victim;
    public ServerPlayerEntity attacker;
    public PlayerEntityData(int timer, ServerPlayerEntity attacker,ServerPlayerEntity victim, int ban_timer, boolean lethal) {
        this.ban_timer = ban_timer;
        this.timer = timer;
        this.lethal = lethal;
        this.attacker = attacker;
        this.victim = victim;
    }
}
