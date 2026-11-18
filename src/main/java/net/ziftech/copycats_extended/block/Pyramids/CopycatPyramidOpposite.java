
package net.ziftech.copycats_extended.block.Pyramids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.client.model.CopycatPyramid_Geometry;
import org.jetbrains.annotations.NotNull;

public class CopycatPyramidOpposite extends BASE_CopycatPyramidBlock {

  public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 1);

  public CopycatPyramidOpposite(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState()
        .setValue(ROTATION, 0)
    );
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(builder);
    builder.add(ROTATION);
  }

  @Override
  public ShapeType getShapeType() {
    return ShapeType.OPPOSITE;
  }

  @Override
  public @NotNull VoxelShape getShape(
    BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    int[] data = getPyramidData(level, pos);

    return CopycatPyramid_Geometry.createShape(
      ShapeType.PYRAMID,
      state.getValue(FACING),
      state.getValue(ROTATION),
      data[0], data[1], data[2]
    );
  }
}
