package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.vulpixass.soulspire.item.ModItems;

import java.util.HashSet;
import java.util.UUID;

public class ReviveInputHandler {

    public static final HashSet<UUID> awaitingReviveInput = new HashSet<>();
    public static final HashSet<UUID> voidDeaths = new HashSet<>();

    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            handleChat(message.getSignedContent(), sender);
        });
    }
    public static void handleChat(String typedName, ServerPlayerEntity sender) {
        UUID senderId = sender.getUuid();
        if (!awaitingReviveInput.contains(senderId)) {
            return;
        }
        awaitingReviveInput.remove(senderId);

        ServerPlayerEntity target = sender.getEntityWorld().getServer().getPlayerManager().getPlayer(typedName);

        if (target == null) {
            sender.sendMessage(Text.literal("§5No such player exists."), false);
            return;
        }

        UUID targetId = target.getUuid();

        if (LivesStore.get().outputLives(targetId) > 0) {
            sender.sendMessage(Text.literal("§5That player is not dead."), false);
            return;
        }

        LivesStore.get().revive(targetId, typedName, sender);
        LivesStore.get().updatePlayerDisplayName(target);
        target.getEntityWorld().getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, target));

        int updated = LivesStore.get().outputLives(targetId);
        ServerPlayNetworking.send(target, new SoulDataS2CPayload(updated));

        for (ServerPlayerEntity p : sender.getEntityWorld().getServer().getPlayerManager().getPlayerList()) {p.getEntityWorld().playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f);}
        ServerWorld world = target.getEntityWorld();
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world, SpawnReason.TRIGGERED);
        if (lightning != null) {
            lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
            lightning.setCosmetic(true);
            world.spawnEntity(lightning);
        }
    }
}
