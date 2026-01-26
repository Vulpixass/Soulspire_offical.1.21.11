package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import net.vulpixass.soulspire.config.LivesConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class LivesStore{
    public final HashMap<UUID, PlayerSoulData> playerLives = new HashMap<>();
    public static HashSet<UUID> awaitingReviveInput = new HashSet<>();
    public static LivesStore INSTANCE = new LivesStore();
    public LivesStore() {this.playerLives.putAll(LivesConfig.load());}
    public void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            UUID uuid = player.getUuid();
            if (LivesStore.get().playerLives.get(uuid).lives == 0) {return ActionResult.FAIL;}
            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            UUID uuid = player.getUuid();
            if (LivesStore.get().playerLives.get(uuid).lives == 0) {return ActionResult.FAIL;}
            return ActionResult.PASS;
        });
        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                UUID uuid = player.getUuid();
                if (!playerLives.containsKey(uuid)) {
                    playerLives.put(uuid, new PlayerSoulData(3));
                    LivesConfig.save(playerLives);
                    System.out.println("Added: \"" + uuid + "\" aka: \"" + player.getName() + "\"to the Lives map");
                }

                if (playerLives.get(uuid).lives == 0) {
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
            }
        });
    }
    public static LivesStore get() {
        return INSTANCE;
    }
    public void addLife(UUID uuid) {
        PlayerSoulData data = playerLives.get(uuid);
        data.lives++;
        LivesConfig.save(playerLives);

    }
    public void removeLife(UUID uuid) {
        PlayerSoulData data = playerLives.get(uuid);
        data.lives--;
        LivesConfig.save(playerLives);
    }
    public void revive(UUID uuid) {
        PlayerSoulData data = playerLives.get(uuid);
        if (!data.hasUsedRevive && !data.hasCatalyst) {
            data.lives = 3;
            LivesConfig.save(playerLives);
        } else if (!data.hasUsedRevive && data.hasCatalyst) {
            data.lives = 3;
            data.hasUsedRevive = true;
            LivesConfig.save(playerLives);
        } else {
            System.out.println("Player: " + uuid + " can't be revived");
        }

    }
    public int outputLives(UUID uuid) {
        return playerLives.get(uuid).lives;
    }
    public void sacrificeSoul(UUID uuid) {
        playerLives.get(uuid).hasCatalyst = true;
        LivesConfig.save(playerLives);
    }
}
