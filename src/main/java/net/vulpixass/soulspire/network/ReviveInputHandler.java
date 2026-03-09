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
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ReviveInputHandler {

    public static final HashMap<UUID, BlockPos> awaitingReviveInput = new HashMap<>();
    public static final HashSet<UUID> voidDeaths = new HashSet<>();

    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            handleChat(message.getSignedContent(), sender);
        });
    }
    public static void handleChat(String typedName, ServerPlayerEntity sender) {
        UUID senderId = sender.getUuid();
        ServerPlayerEntity target = sender.getEntityWorld().getServer().getPlayerManager().getPlayer(typedName);

        if (!awaitingReviveInput.containsKey(senderId)) {return;}

        if (target == null) {
            sender.sendMessage(Text.literal("§5This spirit hasn't entered the World"), false);
            return;
        }

        UUID targetId = target.getUuid();

        if (LivesStore.get().outputLives(targetId) > 0) {
            sender.sendMessage(Text.literal("§5This spirit still controls their physical form"), false);
            return;
        }
        if(!LivesStore.get().playerLives.get(targetId).hasUsedRevive) {ReviveSequenceManager.start(target, awaitingReviveInput.get(senderId));}
        awaitingReviveInput.remove(senderId);
        LivesStore.get().revive(targetId, typedName, sender);
        LivesStore.get().updatePlayerDisplayName(target);
        target.getEntityWorld().getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(
                PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, target));

        int updated = LivesStore.get().outputLives(targetId);
        ServerPlayNetworking.send(target, new SoulDataS2CPayload(updated));
    }
}
