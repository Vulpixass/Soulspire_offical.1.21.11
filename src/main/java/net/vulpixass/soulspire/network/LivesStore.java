package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.UUID;

public class LivesStore extends PersistentState {
    public final HashMap<UUID, Integer> playerLives = new HashMap<>();
    public static LivesStore INSTANCE = new LivesStore();
    public void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            UUID uuid = player.getUuid();
            if (LivesStore.get().playerLives.get(uuid) == 0) {return ActionResult.FAIL;}
            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            UUID uuid = player.getUuid();
            if (LivesStore.get().playerLives.get(uuid) == 0) {return ActionResult.FAIL;}
            return ActionResult.PASS;
        });
        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                UUID uuid = player.getUuid();
                if (playerLives.containsKey(uuid)) {
                    if (playerLives.get(uuid) == 0) {
                        if (!player.isAlive()) continue;
                        if (player.isDisconnected()) continue;
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 20, 0,false, false));
                        player.getAbilities().allowFlying = true;
                        player.getAbilities().flying = true;
                        player.getAbilities().invulnerable = true;
                        player.sendAbilitiesUpdate();
                        player.changeGameMode(GameMode.ADVENTURE);
                    } else {
                        if (player.getGameMode() == GameMode.ADVENTURE) {
                            player.changeGameMode(GameMode.DEFAULT);
                            player.removeStatusEffect(StatusEffects.INVISIBILITY);
                            player.getAbilities().allowFlying = false;
                            player.getAbilities().flying = false;
                            player.getAbilities().invulnerable = false;
                            player.sendAbilitiesUpdate();
                        }
                    }
                } else {
                    playerLives.put(uuid, 3);
                    markDirty();
                    System.out.println("Added: \"" + uuid + "\" aka: \"" + player.getName() + "\"to the Lives map");
                }
            }
        });
    }
    public static LivesStore get() {
        return INSTANCE;
    }
    public void addLife(UUID uuid) {
        playerLives.put(uuid, playerLives.get(uuid) + 1);
        markDirty();
    }
    public void removeLife(UUID uuid) {
        playerLives.put(uuid, playerLives.get(uuid) - 1);
        markDirty();
    }
    public void revive(UUID uuid) {
        playerLives.put(uuid, 3);
        markDirty();
    }
    public int outputLives(UUID uuid) {
        return playerLives.get(uuid);
    }
}
