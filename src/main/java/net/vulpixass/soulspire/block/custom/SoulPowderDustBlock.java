package net.vulpixass.soulspire.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import net.vulpixass.soulspire.block.ModBlocks;
import org.jspecify.annotations.Nullable;

public class SoulPowderDustBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    public static final EnumProperty<WireConnection> NORTH = EnumProperty.of("north", WireConnection.class);
    public static final EnumProperty<WireConnection> EAST  = EnumProperty.of("east", WireConnection.class);
    public static final EnumProperty<WireConnection> SOUTH = EnumProperty.of("south", WireConnection.class);
    public static final EnumProperty<WireConnection> WEST  = EnumProperty.of("west", WireConnection.class);

    public static final IntProperty LEVEL = IntProperty.of("level", 0, 7);
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");

    public SoulPowderDustBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(NORTH, WireConnection.NONE).with(EAST, WireConnection.NONE).with(SOUTH, WireConnection.NONE)
                        .with(WEST, WireConnection.NONE).with(LEVEL, 0).with(ACTIVATED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {return SHAPE;}
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {return VoxelShapes.empty();}
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !world.getBlockState(pos.down()).isAir() && !world.getBlockState(pos.down()).isOf(ModBlocks.SOUL_POWDER);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {builder.add(NORTH, EAST, SOUTH, WEST, LEVEL, ACTIVATED);}

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) {world.scheduleBlockTick(pos, this, 2);}
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        setCurrentLevel(world, pos, state);
        world.scheduleBlockTick(pos, this, 2);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction,
                                                   BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!canPlaceAt(state, world, pos)) {return Blocks.AIR.getDefaultState();}
        if (direction.getAxis().isHorizontal()) {
            WireConnection connection = getConnectionTo(world, pos, direction);
            state = state.with(getPropertyFor(direction), connection);
        }
        return state;
    }

    private EnumProperty<WireConnection> getPropertyFor(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case EAST  -> EAST;
            case SOUTH -> SOUTH;
            case WEST  -> WEST;
            default -> throw new IllegalArgumentException("Invalid direction for dust connection: " + dir);
        };
    }

    public static int getLuminace(BlockState currentBlockState) {return currentBlockState.get(ACTIVATED) ? 15 : 0;}
    private boolean isDust(WorldView world, BlockPos pos) {return world.getBlockState(pos).getBlock() instanceof SoulPowderDustBlock;}

    private WireConnection getConnectionTo(WorldView world, BlockPos pos, Direction dir) {
        BlockPos offset = pos.offset(dir);
        if (isDust(world, offset)) {return WireConnection.SIDE;}
        if (isDust(world, offset.up())) {return WireConnection.UP;}
        return WireConnection.NONE;
    }

    private void setCurrentLevel(ServerWorld world, BlockPos pos, BlockState state) {
        int max = 0;
        for (BlockPos offset : NEIGHBORS_8) {
            BlockPos neighborPos = pos.add(offset);
            max = Math.max(max, getLevelOf(neighborPos, world));
        }
        if (max <= 1) {
            world.setBlockState(pos, state.with(ACTIVATED, false).with(LEVEL, 0));
            return;
        }
        world.setBlockState(pos, state.with(LEVEL, max - 1).with(ACTIVATED, true));
    }

    public int getLevelOf(BlockPos pos, World world) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof SoulPowderDustBlock) {
            return state.get(LEVEL);
        }
        return 0;
    }

    private static final BlockPos[] NEIGHBORS_8 = new BlockPos[]{
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, -1)
    };

    @Override
    protected MapCodec<? extends ConnectingBlock> getCodec() {return null;}

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side,
                                    @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }
    public void energize(ServerWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof SoulPowderDustBlock) {
            world.setBlockState(pos, state.with(LEVEL, 7).with(ACTIVATED, true));
            world.scheduleBlockTick(pos, this, 1);
        }
    }
}
