package net.vulpixass.soulspire.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.vulpixass.soulspire.item.ModItems;

public class RuneBlock extends Block {
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");

    public RuneBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(ACTIVATED, false));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {builder.add(ACTIVATED);}
    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() == ModItems.SOUL_FRAGMENT && state.get(ACTIVATED) == false) {
            world.setBlockState(pos, state.with(ACTIVATED, !state.get(ACTIVATED)));
            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.MASTER);
            return ActionResult.SUCCESS;
        }
        world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.MASTER);
        return ActionResult.FAIL;
    }
    public static boolean getState(BlockPos pos, World world) {
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof RuneBlock)) {return false;}
        return state.get(ACTIVATED);
    }
    public static int getLuminace(BlockState currentBlockState) {return currentBlockState.get(ACTIVATED) ? 15 : 0;}
}
