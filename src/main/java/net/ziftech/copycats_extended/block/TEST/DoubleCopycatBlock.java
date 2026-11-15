package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.utility.BlockUtils;
import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.client.model.DoubleCopycat_Geometry;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Set;


public class DoubleCopycatBlock extends MultiStateCopycatBlock {

  public static final String ONE = "one";
  public static final String TWO = "two";

  public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

  public DoubleCopycatBlock(Properties properties) {
    super(properties);
    registerDefaultState(
      defaultBlockState().setValue(AXIS, Direction.Axis.Y)
    );
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(builder);
    builder.add(AXIS);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction.Axis axis = context.getClickedFace().getAxis();

    return defaultBlockState().setValue(AXIS, axis);
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return RotatedPillarBlock.rotatePillar(state, rotation);
  }

  @Override
  public String defaultProperty() {
    return ONE;
  }

  @Override
  public Set<String> storageProperties() {
    return Set.of(ONE, TWO);
  }

  @Override
  public Vec3i vectorScale(BlockState state) {
    return switch (state.getValue(AXIS)) {
      case X -> new Vec3i(2, 1, 1);
      case Y -> new Vec3i(1, 2, 1);
      case Z -> new Vec3i(1, 1, 2);
    };
  }

  @Override
  public int getColorIndex(String property) {
    return property.equals(ONE) ? 0 : 1;
  }

  @Override
  public VoxelShape getShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    return DoubleCopycat_Geometry.createShape(state.getValue(AXIS));
  }

  @Override
  public boolean partExists(BlockState state, String property) {
    return property.equals(ONE) || property.equals(TWO);
  }

  @Override
  public Vec3i getVectorFromProperty(BlockState state, String property) {
    if (property.equals(TWO))
      return Vec3i.ZERO;

    return switch (state.getValue(AXIS)) {
      case X -> new Vec3i(1, 0, 0);
      case Y -> new Vec3i(0, 1, 0);
      case Z -> new Vec3i(0, 0, 1);
    };
  }

  @Override
  public String getPropertyFromInteraction(
    BlockState state,
    BlockGetter level,
    Vec3i hitLocation,
    BlockPos blockPos,
    Direction facing,
    Vec3 unscaledHit
  ) {
    int coordinate = switch (state.getValue(AXIS)) {
      case X -> hitLocation.getX();
      case Y -> hitLocation.getY();
      case Z -> hitLocation.getZ();
    };

    return coordinate > 0 ? ONE : TWO;
  }

  @Override
  public VoxelShape getPartialFaceShape(
    BlockGetter level,
    BlockState state,
    String property,
    Direction face
  ) {
    if (!partExists(state, property))
      return Shapes.empty();

    VoxelShape shape = DoubleCopycat_Geometry.getPartShape(
      state.getValue(AXIS),
      property.equals(ONE)
    );

    return shape.getFaceShape(face);
  }

  @Override
  public BlockState transform(
    BlockState state,
    StructureTransform transform
  ) {
    Direction direction = Direction.get(
      Direction.AxisDirection.POSITIVE,
      state.getValue(AXIS)
    );

    Direction transformedDirection = BlockUtils.transformFacing(
      transform,
      direction
    );

    return state.setValue(
      AXIS,
      transformedDirection.getAxis()
    );
  }

  @Override
  public void transformStorage(
    BlockState state,
    IMultiStateCopycatBlockEntity be,
    StructureTransform transform
  ) {
    Direction originalDirection = Direction.get(
      Direction.AxisDirection.POSITIVE,
      state.getValue(AXIS)
    );

    Direction transformedDirection = transform.rotationAxis == null
      ? originalDirection
      : transform.rotateFacing(originalDirection);

    if (transformedDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
      be.getMaterialItemStorage().remapStorage(property -> switch (property) {
        case ONE -> TWO;
        case TWO -> ONE;
        default -> property;
      });
    }
  }

  @Override
  public BlockEntityType<? extends MultiStateCopycatBlockEntity> getBlockEntityType() {
    return ModBlockEntities.DOUBLE_COPYCAT.get();
  }


}
