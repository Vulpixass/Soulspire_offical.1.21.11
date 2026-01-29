package net.vulpixass.soulspire.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.vulpixass.soulspire.network.LivesStore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerNameMixin {
    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        int lives = LivesStore.get().outputLives(player.getUuid());

        // Re-build the name with the lives counter
        MutableText newName = player.getName().copy()
                .append(Text.literal(" [" + lives + "]").formatted(Formatting.DARK_PURPLE));

        cir.setReturnValue(newName);
    }
}
