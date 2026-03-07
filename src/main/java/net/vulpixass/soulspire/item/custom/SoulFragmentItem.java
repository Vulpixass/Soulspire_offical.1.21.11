package net.vulpixass.soulspire.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.vulpixass.soulspire.block.ModBlocks;

public class SoulFragmentItem extends Item {
    public SoulFragmentItem(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Block clickedBlock = world.getBlockState(pos.up()).getBlock();
        if (canPlaceOnTop(world, pos) && clickedBlock == Blocks.AIR) {
            world.setBlockState(pos.up(), ModBlocks.SOUL_POWDER.getDefaultState());
            world.playSound(context.getPlayer(), pos.up(), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.MASTER);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    private boolean canPlaceOnTop(BlockView world, BlockPos pos) {
        BlockState floor = world.getBlockState(pos);
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }
}
