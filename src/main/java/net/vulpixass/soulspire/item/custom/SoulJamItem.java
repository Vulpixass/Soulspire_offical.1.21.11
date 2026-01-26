package net.vulpixass.soulspire.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.vulpixass.soulspire.item.ModItems;
import net.vulpixass.soulspire.network.LivesStore;

import java.util.UUID;

public class SoulJamItem extends Item {
    public SoulJamItem(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            UUID uuid = user.getUuid();
            LivesStore lives = LivesStore.get();

            int currentLives = lives.playerLives.get(uuid).lives;
            if (currentLives != 3 && !lives.playerLives.get(uuid).hasCatalyst) {
                lives.addLife(uuid);
                user.sendMessage(Text.literal("You feel your soul strengthen"), true);
                user.getEntityWorld().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WARDEN_HEARTBEAT, SoundCategory.HOSTILE, 1.0f, 1.0f);
                if (user.getMainHandStack().getItem() == ModItems.SOUL_JAM) {user.getMainHandStack().decrement(1);}
                else if (user.getOffHandStack().getItem() == ModItems.SOUL_JAM) {user.getOffHandStack().decrement(1);}
                return ActionResult.SUCCESS;
            } else {
                System.out.println("Adding 1 Life to: " + user.getName() + " failed, target already has 3 lives or has the Soul Catalyst crafted");
                user.sendMessage(Text.literal("Your Soul is rejecting more life"), true);
                user.getEntityWorld().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WARDEN_HEARTBEAT, SoundCategory.HOSTILE, 1.0f, 1.0f);
                user.getEntityWorld().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.HOSTILE, 1.0f, 1.0f);
                return ActionResult.FAIL;
            }
        }
        return super.use(world, user, hand);
    }
}
