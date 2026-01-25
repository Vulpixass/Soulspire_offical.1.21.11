package net.vulpixass.soulspire.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
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

            int currentLives = lives.playerLives.get(uuid);
            if (currentLives != 3) {
                lives.addLife(uuid);
                user.sendMessage(Text.literal("You feel your soul strengthen"), true);
                return ActionResult.SUCCESS;
            } else {
                System.out.println("Adding 1 Life to: " + user.getName() + " failed, target already has 3 lives");
                user.sendMessage(Text.literal("Your Soul is rejecting more life"), true);
                return ActionResult.FAIL;
            }
        }
        return super.use(world, user, hand);
    }
}
