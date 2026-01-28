package net.vulpixass.soulspire.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.vulpixass.soulspire.item.ModItems;
import net.vulpixass.soulspire.network.LivesStore;
import net.vulpixass.soulspire.network.SoulDataS2CPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1. Target ServerPlayerEntity instead of LivingEntity
@Mixin(ServerPlayerEntity.class)
public abstract class PlayerDeathMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        // Cast 'this' to ServerPlayerEntity safely
        ServerPlayerEntity victim = (ServerPlayerEntity) (Object) this;
        ServerWorld serverWorld = victim.getEntityWorld().toServerWorld();
        // Ensure we are on the server
        if (victim.getEntityWorld().isClient()) return;

        // Check if killed by a player
        if (damageSource.getAttacker() instanceof PlayerEntity killer) {

            // Remove life
            LivesStore.get().removeLife(victim.getUuid());
            victim.getEntityWorld().playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);
            int updated = LivesStore.get().outputLives(victim.getUuid());
            ServerPlayNetworking.send(victim, new SoulDataS2CPayload(updated));
            System.out.println("PlayerDeathMixin fired for: " + victim.getName().getString());

            // Check for Amulet in either hand
            if (killer.getOffHandStack().isOf(ModItems.SOUL_AMULET) ||
                    killer.getMainHandStack().isOf(ModItems.SOUL_AMULET)) {

                BannedPlayerList bannedPlayerList = serverWorld.getServer().getPlayerManager().getUserBanList();

                // Play Effects
                serverWorld.playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0f, 1.0f);
                serverWorld.playSound(null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                serverWorld.createExplosion(null, victim.getX(), victim.getY(), victim.getZ(), 8.0f, World.ExplosionSourceType.TNT);

                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, serverWorld);
                lightning.refreshPositionAndAngles(victim.getX(), victim.getY(), victim.getZ(), 0.0f, 0.0f);
                serverWorld.spawnEntity(lightning);

                // Ban and Disconnect
                bannedPlayerList.add(new BannedPlayerEntry(victim.getPlayerConfigEntry(), null, "Soul Amulet", null, "Your Soul got Overloaded"));
                victim.networkHandler.disconnect(Text.literal("§5§kkhj§5Your Soul got Overloaded§kkhj"));

                // Consume Totem
                if (killer.getMainHandStack().isOf(ModItems.SOUL_AMULET)) {
                    killer.getMainHandStack().decrement(1);
                } else if (killer.getOffHandStack().isOf(ModItems.SOUL_AMULET)) {
                    killer.getOffHandStack().decrement(1);
                }
            } else {
                ItemEntity soulfragment = new ItemEntity(serverWorld, victim.getX(), victim.getY() + 0.5, victim.getZ(), new ItemStack(ModItems.SOUL_TOKEN));
                serverWorld.spawnEntity(soulfragment);
            }
        }
    }
}
