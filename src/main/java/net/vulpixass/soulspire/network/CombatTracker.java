package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.vulpixass.soulspire.item.ModItems;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CombatTracker {

    // victim UUID -> combat data
    private static final Map<UUID, PlayerEntityData> COMBAT = new HashMap<>();

    // how long a player stays "in combat" after being hit (in ticks)
    private static final int COMBAT_TICKS = 200; // 10 seconds

    public static void register() {

        // 1. When a player is damaged by another player, start/reset their combat timer
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity victim)) return true;

            Entity attackerEntity = source.getAttacker();
            if (!(attackerEntity instanceof ServerPlayerEntity attacker)) return true;

            if (attacker.getUuid().equals(victim.getUuid())) return true; // ignore self-damage

            COMBAT.put(victim.getUuid(), new PlayerEntityData(COMBAT_TICKS, attacker));
            return true; // allow damage
        });

        // 2. Tick down all combat timers once per server tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, PlayerEntityData>> it = COMBAT.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, PlayerEntityData> entry = it.next();
                PlayerEntityData data = entry.getValue();
                data.timer--;
                if (data.timer <= 0) {
                    it.remove();
                }
            }
        });
        // 3. When a player dies, check if they were in combat recently
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayerEntity victim)) return;

            UUID id = victim.getUuid();
            PlayerEntityData data = COMBAT.remove(id); // also clears combat state

            if (data != null) {
                // victim died while in combat -> treat as PvP death
                LivesStore.get().removeLife(id);

                if (!data.attacker.getOffHandStack().isOf(ModItems.SOUL_AMULET) && !data.attacker.getMainHandStack().isOf(ModItems.SOUL_AMULET)) {
                    victim.getEntityWorld().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);
                    ItemEntity soulfragment = new ItemEntity(victim.getEntityWorld().toServerWorld(), victim.getX(), victim.getY() + 0.5, victim.getZ(), new ItemStack(ModItems.SOUL_FRAGMENT));
                    victim.getEntityWorld().toServerWorld().spawnEntity(soulfragment);
                    System.out.println("CombatTracker PvP death for: " + victim.getName().getString());
                }
                int updated = LivesStore.get().outputLives(id);
                ServerPlayNetworking.send(victim, new SoulDataS2CPayload(updated));
            }
        });
    }
}
