  package net.ziftech.copycats_extended.block.Pyramids;

  import com.simibubi.create.content.contraptions.StructureTransform;
  import net.minecraft.core.BlockPos;
  import net.minecraft.core.Direction;
  import net.minecraft.world.item.context.BlockPlaceContext;
  import net.minecraft.world.level.BlockGetter;
  import net.minecraft.world.level.block.Block;
  import net.minecraft.world.level.block.state.BlockState;
  import net.minecraft.world.level.block.state.StateDefinition;
  import net.minecraft.world.level.block.state.properties.DirectionProperty;
  import net.minecraft.world.phys.shapes.CollisionContext;
  import net.minecraft.world.phys.shapes.VoxelShape;
  import net.ziftech.copycats_extended.client.model.CopycatPyramid_Geometry;
  import net.ziftech.copycats_extended.utility.BlockPlacementHelper;
  import net.ziftech.copycats_extended.utility.BlockTransformHelper;
  import org.jetbrains.annotations.NotNull;

  public class CopycatPyramidSide extends BASE_CopycatPyramidBlock {

    public static final DirectionProperty ROTATION =
      DirectionProperty.create("rotation", Direction.Plane.HORIZONTAL);

    public CopycatPyramidSide(Properties properties) {
      super(properties);
      registerDefaultState(defaultBlockState()
        .setValue(ROTATION, Direction.SOUTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(ROTATION);
    }

    @Override
    public @NotNull BlockState transform(@NotNull BlockState state, @NotNull StructureTransform transform) {
      return BlockTransformHelper.transform(
        state,
        transform,
        FACING,
        ROTATION
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
        ShapeType.SIDE,
        state.getValue(FACING),
        state.getValue(ROTATION)
      );
    }

    @Override
    public ShapeType getShapeType() {
      return ShapeType.SIDE;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);

      return state.setValue(
        ROTATION,
        BlockPlacementHelper.getEdgeRotationClockwise(context)
      );
    }
  }
