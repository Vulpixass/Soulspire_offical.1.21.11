package net.vulpixass.soulspire.network;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.vulpixass.soulspire.Soulspire;
import net.vulpixass.soulspire.block.ModBlocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static net.vulpixass.soulspire.block.custom.RuneBlock.ACTIVATED;

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
        ServerWorld world = sender.getEntityWorld().toServerWorld();
        ServerPlayerEntity target = sender.getEntityWorld().getServer().getPlayerManager().getPlayer(typedName);

        if (!awaitingReviveInput.containsKey(senderId)) {return;}

        BlockPattern pattern = BlockPatternBuilder.start()
                // Z = 0
                .aisle(
                        "...............",  // Y = 0
                        ".......N......."   // Y = 1
                )
                // Z = 1
                .aisle(
                        "...............",
                        "..............."
                )
                // Z = 2
                .aisle(
                        "......PPP......",
                        "..............."
                )
                // Z = 3
                .aisle(
                        ".....P.P.P.....",
                        "..............."
                )
                // Z = 4
                .aisle(
                        "....P..P..P....",
                        "..............."
                )
                // Z = 5
                .aisle(
                        "...P.P.P.P.P...",
                        "..............."
                )
                // Z = 6
                .aisle(
                        "..P...PPP...P..",
                        "..............."
                )
                // Z = 7
                .aisle(
                        "..PPPPPAPPPPP..",
                        "W.............E"
                )
                // Z = 8
                .aisle(
                        "..P...PPP...P..",
                        "..............."
                )
                // Z = 9
                .aisle(
                        "...P.P.P.P.P...",
                        "..............."
                )
                // Z = 10
                .aisle(
                        "....P..P..P....",
                        "..............."
                )
                // Z = 11
                .aisle(
                        ".....P.P.P.....",
                        "..............."
                )
                // Z = 12
                .aisle(
                        "......PPP......",
                        "..............."
                )
                // Z = 13
                .aisle(
                        "...............",
                        "..............."
                )
                // Z = 14
                .aisle(
                        "...............",
                        ".......S......."
                )
                .where('A', pos -> pos.getBlockState().isOf(ModBlocks.RITUAL_ALTAR))
                .where('P', pos -> pos.getBlockState().isOf(ModBlocks.SOUL_POWDER))
                .where('W', pos -> pos.getBlockState().isOf(ModBlocks.RUNE_WEST)  && pos.getBlockState().get(ACTIVATED))
                .where('N', pos -> pos.getBlockState().isOf(ModBlocks.RUNE_NORTH) && pos.getBlockState().get(ACTIVATED))
                .where('S', pos -> pos.getBlockState().isOf(ModBlocks.RUNE_SOUTH) && pos.getBlockState().get(ACTIVATED))
                .where('E', pos -> pos.getBlockState().isOf(ModBlocks.RUNE_EAST)  && pos.getBlockState().get(ACTIVATED))
                .where('.', pos -> true)

                .build();

        BlockPos origin = awaitingReviveInput.get(senderId);
        BlockPattern.Result result = pattern.searchAround(world, origin.add(-7, 0, -7));

        if (result == null) {
            sender.sendMessage(Text.literal("§5The Ritual is incomplete"));
            sequenceFailure(sender, world);
            return;
        }

        if (target == null) {
            sender.sendMessage(Text.literal("§5This spirit hasn't entered the World"), false);
            sequenceFailure(sender, world);
            return;
        }

        UUID targetId = target.getUuid();

        if (LivesStore.get().outputLives(targetId) > 0) {
            sender.sendMessage(Text.literal("§5This spirit still controls their physical form"), false);
            sequenceFailure(sender, world);
            return;
        }

        if(!LivesStore.get().playerLives.get(targetId).hasUsedRevive) {ReviveSequenceManager.start(target, awaitingReviveInput.get(senderId), typedName, sender);}
        awaitingReviveInput.remove(senderId);
    }
    private static void sequenceFailure(ServerPlayerEntity sender, ServerWorld world) {
        if (!Soulspire.RitualRetaliates) {return;}
        UUID senderId = sender.getUuid();

        for (int i = 0; i < 40; i++) {
            if (i >= 1 && i <= 3) {
                ReviveSequence.spawnBeam(world, awaitingReviveInput.get(senderId), sender.getBlockPos());
            }
            if (i % 13 == 0) {
                world.playSound(null, sender.getBlockPos(), SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.MASTER);
                world.playSound(null, sender.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.MASTER);
            }
        }
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world, null);
        if (lightning != null) {
            lightning.refreshPositionAndAngles(sender.getBlockPos(), 0, 0);
            world.spawnEntity(lightning);
            world.playSound(null, sender.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                    SoundCategory.MASTER, 2.0f, 1.0f);
        }
        sender.sendMessage(Text.literal("§5§kkhj§5Your Soul couldn't handle the redirection§kkhj"));
        LivesStore.get().removeLife(senderId);

        sender.getEntityWorld().getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, sender));
        int updated = LivesStore.get().outputLives(senderId);
        ServerPlayNetworking.send(sender, new SoulDataS2CPayload(updated));
    }

}
