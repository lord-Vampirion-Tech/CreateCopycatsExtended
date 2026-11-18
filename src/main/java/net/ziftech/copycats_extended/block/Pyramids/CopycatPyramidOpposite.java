package net.ziftech.copycats_extended.block.Pyramids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.client.model.CopycatPyramid_Geometry;
import org.jetbrains.annotations.NotNull;

public class CopycatPyramidOpposite extends BASE_CopycatPyramidBlock {

  public static final IntegerProperty ROTATION =
    IntegerProperty.create("rotation", 0, 1);

  public CopycatPyramidOpposite(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState()
      .setValue(ROTATION, 0));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(ROTATION);
  }

  @Override
  public @NotNull VoxelShape getShape(
    BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    return CopycatPyramid_Geometry.createShape(
      state.getValue(FACING),
      state.getValue(TOP_POINT_X),
      state.getValue(TOP_POINT_Y),
      state.getValue(TOP_POINT_Z)
    );
  }

  @Override
  public ShapeType getShapeType() {
    return ShapeType.OPPOSITE;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);

    Vec3 hit = context.getClickLocation()
      .subtract(Vec3.atLowerCornerOf(context.getClickedPos()));

    double u;
    double v;

    switch (context.getClickedFace()) {
      case UP -> {
        u = hit.x;
        v = hit.z;
      }
      case DOWN -> {
        u = 1 - hit.x;
        v = hit.z;
      }
      case NORTH -> {
        u = 1 - hit.x;
        v = hit.y;
      }
      case SOUTH -> {
        u = hit.x;
        v = hit.y;
      }
      case WEST -> {
        u = hit.z;
        v = hit.y;
      }
      case EAST -> {
        u = 1 - hit.z;
        v = hit.y;
      }
      default -> {
        u = 0.5;
        v = 0.5;
      }
    }

    int rotation = Math.abs(v - 0.5) > Math.abs(u - 0.5) ? 1 : 0;

    return state.setValue(ROTATION, rotation);
  }
}
