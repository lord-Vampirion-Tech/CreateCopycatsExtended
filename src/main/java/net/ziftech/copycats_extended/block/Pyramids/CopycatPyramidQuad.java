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

public class CopycatPyramidQuad extends BASE_CopycatPyramidBlock {

  public CopycatPyramidQuad(Properties properties) {
    super(properties);
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
      0,
      data[0], data[1], data[2]
    );
  }

  @Override
  public ShapeType getShapeType() {
    return ShapeType.QUAD;
  }
}
