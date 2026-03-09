package net.vulpixass.soulspire.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.vulpixass.soulspire.item.ModItems;
import net.vulpixass.soulspire.network.ReviveInputHandler;

public class RitualAltarBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 25, 14);

    public RitualAltarBlock(Settings settings) {super(settings);}

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            activateDust((ServerWorld) world, pos);
            world.playSound(null, pos, SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.MASTER);
            if (player.getMainHandStack().isOf(ModItems.SOUL_TOTEM)) {
                world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.MASTER);
                PlayerEntity nearestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5.0, false);
                ReviveInputHandler.awaitingReviveInput.put(nearestPlayer.getUuid(), pos);
                System.out.println("Fired ReviveInputHandle");
                nearestPlayer.sendMessage(Text.literal("§5Type the Spirit's name so that he shall rise once more"), false);
            }
        }
        return ActionResult.SUCCESS;
    }

    private void activateDust(ServerWorld world, BlockPos altarPos) {
        int radius = 1;
        BlockPos.iterateOutwards(altarPos, radius, radius, radius).forEach(pos -> {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof SoulPowderDustBlock dust) {
                dust.energize(world, pos);
            }
        });
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
