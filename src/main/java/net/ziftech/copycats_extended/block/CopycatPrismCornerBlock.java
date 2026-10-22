
package net.ziftech.copycats_extended.block;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.utility.BlockUtils;
import com.copycatsplus.copycats.utility.InteractionUtils;
import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class CopycatPrismCornerBlock extends CCWaterloggedCopycatBlock {

  public static final DirectionProperty FACING =
    BlockStateProperties.HORIZONTAL_FACING;

  public static final BooleanProperty WALL_MOUNTED =
    BooleanProperty.create("wall_mounted");

  public CopycatPrismCornerBlock(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState()
        .setValue(FACING, Direction.NORTH)
        .setValue(WALL_MOUNTED, false)
    );
  }

  @Override
  public VoxelShape getShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    // Временная форма столкновения.
    // Визуальная геометрия задаётся в CopycatPrismCornerModelCore.
    return Shapes.block();
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(
      builder.add(FACING, WALL_MOUNTED)
    );
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);

    if (state == null) {
      return null;
    }

    Direction clickedFace = context.getClickedFace();

    if (clickedFace.getAxis().isVertical()) {
      return state
        .setValue(FACING, context.getHorizontalDirection().getOpposite())
        .setValue(WALL_MOUNTED, false);
    }

    return state
      .setValue(FACING, clickedFace.getOpposite())
      .setValue(WALL_MOUNTED, true);
  }

  @Override
  public boolean supportsExternalFaceHiding(BlockState state) {
    return true;
  }

  @Override
  public boolean hidesNeighborFace(
    BlockGetter level,
    BlockPos pos,
    BlockState state,
    BlockState neighborState,
    Direction direction
  ) {
    return ICopycatBlock.hidesNeighborFace(
      level,
      pos,
      state,
      neighborState,
      direction
    );
  }

  @Override
  public BlockState transform(
    BlockState state,
    StructureTransform transform
  ) {
    if (state.getValue(WALL_MOUNTED)) {
      return BlockUtils.transformStepLikeVertical(
        state,
        transform,
        defaultBlockState()
      );
    }

    return BlockUtils.transformStepLikeHorizontal(
      state,
      transform,
      defaultBlockState()
    );
  }

  @Override
  public BlockEntityType<? extends CCCopycatBlockEntity>
  getBlockEntityType() {
    return ModBlockEntities.COPYCAT_PRISM_CORNER.get();
  }
}
