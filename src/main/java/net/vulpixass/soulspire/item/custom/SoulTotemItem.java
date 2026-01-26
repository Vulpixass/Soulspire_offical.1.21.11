package net.vulpixass.soulspire.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.vulpixass.soulspire.network.ReviveInputHandler;

public class SoulTotemItem extends Item {
    public SoulTotemItem(Settings settings) {super(settings);}
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            ReviveInputHandler.awaitingReviveInput.add(user.getUuid());
            user.sendMessage(Text.literal("§5Type the name of the player you want to revive."), false);
        }
        return ActionResult.SUCCESS;
    }
}
