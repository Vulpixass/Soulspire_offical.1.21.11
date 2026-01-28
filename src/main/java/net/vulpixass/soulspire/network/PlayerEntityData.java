package net.vulpixass.soulspire.network;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerEntityData {
    public int timer;
    public ServerPlayerEntity attacker;
    public PlayerEntityData(int timer, ServerPlayerEntity attacker) {
        this.timer = timer;
        this.attacker = attacker;
    }
}
