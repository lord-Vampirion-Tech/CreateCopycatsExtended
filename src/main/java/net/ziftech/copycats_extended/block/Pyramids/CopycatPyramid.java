package net.ziftech.copycats_extended.block.Pyramids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.client.model.CopycatPyramid_Geometry;
import org.jetbrains.annotations.NotNull;

public class CopycatPyramid extends BASE_CopycatPyramidBlock {

  public CopycatPyramid(Properties properties) {
    super(properties);
  }

  @Override
  public @NotNull VoxelShape getShape(
    BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    return CopycatPyramid_Geometry.createShape(
      ShapeType.PYRAMID,
      state.getValue(FACING),
      0
    );
  }

  @Override
  public ShapeType getShapeType() {
    return ShapeType.PYRAMID;
  }
}
