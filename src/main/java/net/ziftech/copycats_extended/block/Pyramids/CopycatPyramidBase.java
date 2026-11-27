package net.ziftech.copycats_extended.block.Pyramids;

import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.client.model.CopycatPyramid_Geometry;
import net.ziftech.copycats_extended.utility.BlockTransformHelper;
import org.jetbrains.annotations.NotNull;

public class CopycatPyramidBase extends BASE_CopycatPyramidBlock {

  public CopycatPyramidBase(Properties properties) {
    super(properties);
  }

  @Override
  public BlockState transform(BlockState state, StructureTransform transform) {
    return BlockTransformHelper.transform(
      state,
      transform,
      FACING
    );
  }

  @Override
  public @NotNull VoxelShape getShape(
    @NotNull BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    return CopycatPyramid_Geometry.createShape(
      ShapeType.PYRAMID,
      state.getValue(FACING),
      Direction.SOUTH,
      state
    );
  }

  @Override
  public ShapeType getShapeType() {
    return ShapeType.PYRAMID;
  }
}
