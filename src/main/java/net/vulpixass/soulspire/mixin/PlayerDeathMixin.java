package net.vulpixass.soulspire.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.vulpixass.soulspire.item.ModItems;
import net.vulpixass.soulspire.network.LivesStore;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerDeathMixin extends LivingEntity {

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    protected PlayerDeathMixin(EntityType<? extends LivingEntity> type, World world) {super(type, world);}

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getAttacker() instanceof PlayerEntity killer) {
            PlayerEntity victim = (PlayerEntity) (Object) this;
            ServerPlayerEntity serverVictim = (ServerPlayerEntity)(Object)this;
            ServerWorld serverWorld = serverVictim.getEntityWorld().toServerWorld();
            LivesStore.get().removeLife(victim.getUuid());
            if (killer.getOffHandStack().isOf(ModItems.SOUL_AMULET) || killer.getMainHandStack().isOf(ModItems.SOUL_AMULET)) {
                MinecraftServer server = killer.getEntityWorld().getServer();
                BannedPlayerList bannedPlayerList = killer.getEntityWorld().getServer().getPlayerManager().getUserBanList();
                serverWorld.playSound(null, serverVictim.getX(), serverVictim.getY(), serverVictim.getZ(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0f, 1.0f);
                serverWorld.playSound(null, serverVictim.getX(), serverVictim.getY(), serverVictim.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                serverWorld.playSound(null, serverVictim.getX(), serverVictim.getY(), serverVictim.getZ(), SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                serverWorld.createExplosion(null, serverVictim.getX(), serverVictim.getY(), serverVictim.getZ(), 8.0f, World.ExplosionSourceType.TNT);

                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, serverWorld);
                lightning.refreshPositionAndAngles(serverVictim.getX(), serverVictim.getY(), serverVictim.getZ(), 0.0f,0.0f);
                serverWorld.spawnEntity(lightning);

                bannedPlayerList.add(new BannedPlayerEntry(victim.getPlayerConfigEntry(), null, "Soul Amulet", null, "Your Soul got Overloaded"));
                serverVictim.networkHandler.disconnect(Text.literal("Your Soul got Overloaded"));
                killer.getOffHandStack().decrement(1);
            }
            System.out.println(killer.getName() + " killed " + victim.getName());
        }
    }
}
