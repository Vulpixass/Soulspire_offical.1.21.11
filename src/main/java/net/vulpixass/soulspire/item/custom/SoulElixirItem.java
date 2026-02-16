package net.vulpixass.soulspire.item.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.vulpixass.soulspire.item.ModItems;
import net.vulpixass.soulspire.network.GainingLifeSequenceManager;
import net.vulpixass.soulspire.network.LivesStore;
import net.vulpixass.soulspire.network.SoulDataS2CPayload;

import java.util.UUID;

public class SoulElixirItem extends Item {
    public SoulElixirItem(Settings settings) {
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
                lives.updatePlayerDisplayName((ServerPlayerEntity) user);
                user.getEntityWorld().getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, (ServerPlayerEntity) user));
                user.sendMessage(Text.literal("You feel your soul strengthen"), true);
                user.getEntityWorld().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_WARDEN_HEARTBEAT, SoundCategory.HOSTILE, 1.0f, 1.0f);
                int updated = LivesStore.get().outputLives(user.getUuid());
                ServerPlayNetworking.send((ServerPlayerEntity) user, new SoulDataS2CPayload(updated));
                if (user.getMainHandStack().getItem() == ModItems.SOUL_ELIXIR) {user.getMainHandStack().decrement(1);}
                else if (user.getOffHandStack().getItem() == ModItems.SOUL_ELIXIR) {user.getOffHandStack().decrement(1);}
                GainingLifeSequenceManager.start((ServerPlayerEntity) user);
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
