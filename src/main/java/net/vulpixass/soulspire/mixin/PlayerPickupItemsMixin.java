package net.vulpixass.soulspire.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.vulpixass.soulspire.network.LivesStore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class PlayerPickupItemsMixin {
    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPickup(PlayerEntity player, CallbackInfo info) {
        UUID uuid = player.getUuid();
        var data = LivesStore.get().playerLives.get(uuid);
        if (data != null && data.lives == 0) {
            info.cancel();
        }
    }
}
