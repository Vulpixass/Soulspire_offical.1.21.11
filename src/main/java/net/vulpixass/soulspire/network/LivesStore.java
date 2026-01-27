package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import net.vulpixass.soulspire.config.LivesConfig;
import net.vulpixass.soulspire.item.ModItems;

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
            if (world.isClient()) return ActionResult.PASS;
            UUID uuid = player.getUuid();
            if (!LivesStore.get().playerLives.containsKey(uuid)) return ActionResult.PASS;
            if (LivesStore.get().playerLives.get(uuid).lives == 0) {return ActionResult.FAIL;}
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;
            UUID uuid = player.getUuid();
            if (!LivesStore.get().playerLives.containsKey(uuid)) return ActionResult.PASS;
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
                    player.getEntityWorld().getServer().getPlayerManager().broadcast(Text.literal("§5" + player.getName().getLiteralString() + " has joined the Realm, may their Soul be with them"), false);
                    player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 1.0f);
                }
                if (!playerLives.containsKey(uuid)) continue;

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
        if (data == null) return; // ADDED
        data.lives++;
        LivesConfig.save(playerLives);
    }
    public void removeLife(UUID uuid) {
        PlayerSoulData data = playerLives.get(uuid);
        if (data == null) return; // ADDED
        data.lives--;
        LivesConfig.save(playerLives);
    }
    public void revive(UUID uuid, String typedName, PlayerEntity sender) {
        PlayerSoulData data = playerLives.get(uuid);
        if (data == null) return; // ADDED
        if (!data.hasUsedRevive && !data.hasCatalyst) {
            data.lives = 3;
            LivesConfig.save(playerLives);
            sender.getEntityWorld().getServer().getPlayerManager().broadcast(Text.literal("§5§kkhj§5" + typedName + " has risen from the Dead§kkhj"), false);
            if (sender.getMainHandStack().getItem() == ModItems.SOUL_TOTEM) {sender.getMainHandStack().decrement(1);}
            else if (sender.getOffHandStack().getItem() == ModItems.SOUL_TOTEM) {sender.getOffHandStack().decrement(1);}
        } else if (!data.hasUsedRevive && data.hasCatalyst) {
            data.lives = 3;
            data.hasUsedRevive = true;
            LivesConfig.save(playerLives);
            sender.getEntityWorld().getServer().getPlayerManager().broadcast(Text.literal("§5§kkhj§5" + typedName + " has risen from the Dead§kkhj"), false);
            if (sender.getMainHandStack().getItem() == ModItems.SOUL_TOTEM) {sender.getMainHandStack().decrement(1);}
            else if (sender.getOffHandStack().getItem() == ModItems.SOUL_TOTEM) {sender.getOffHandStack().decrement(1);}
        } else {
            sender.getEntityWorld().getServer().getPlayerManager().broadcast(Text.literal("§5§kkhj§5" + typedName + " has failed to return to the World of the living§kkhj"), false);
            System.out.println("Player: " + uuid + " can't be revived");
        }

    }
    public int outputLives(UUID uuid) {
        return playerLives.get(uuid).lives;
    }
    public void sacrificeSoul(UUID uuid) {
        if (!playerLives.containsKey(uuid)) return; // ADDED
        playerLives.get(uuid).hasCatalyst = true;
        LivesConfig.save(playerLives);
    }
}
