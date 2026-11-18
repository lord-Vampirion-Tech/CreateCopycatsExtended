
package net.ziftech.copycats_extended.block;

import com.copycatsplus.copycats.foundation.copycat.CopycatExternalContext;
import com.copycatsplus.copycats.foundation.copycat.CopycatTransformableState;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.utility.BlockEntityUtils;
import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.ziftech.copycats_extended.client.model.DoubleBlock_Geometry;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

import com.copycatsplus.copycats.utility.InteractionUtils;

import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import net.ziftech.copycats_extended.block.DoubleBlockPlacementHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DoubleBlock extends MultiStateCopycatBlock {

  public static final String ONE = "one";
  public static final String TWO = "two";

  public static final BooleanProperty CONNECT_HALF = BooleanProperty.create("connect_half");
  public static final IntegerProperty SLICE_Y = IntegerProperty.create("slice_y", 1, 7);
  public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
  private static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new DoubleBlockPlacementHelper());

  public DoubleBlock(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState()
        .setValue(AXIS, Direction.Axis.Y)
        .setValue(SLICE_Y, 4)
        .setValue(CONNECT_HALF, false)
    );
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(builder);
    builder.add(AXIS, SLICE_Y, CONNECT_HALF);
  }

  // MultiState
  @Override
  public @NotNull String defaultProperty() {
    return TWO;
  }

  @Override
  public @NotNull Set<String> storageProperties() {
    return Set.of(ONE, TWO);
  }

  @Override
  public boolean partExists(@NotNull BlockState state, String property) {
    return property.equals(ONE) || property.equals(TWO);
  }

  @Override
  public int getColorIndex(String property) {
    return property.equals(ONE) ? 0 : 1;
  }

  @Override
  public @NotNull Vec3i vectorScale(BlockState state) {
    return switch (state.getValue(AXIS)) {
      case X -> new Vec3i(8, 1, 1);
      case Y -> new Vec3i(1, 8, 1);
      case Z -> new Vec3i(1, 1, 8);
    };
  }

  @Override
  public @NotNull Vec3i getVectorFromProperty(
    @NotNull BlockState state,
    String property
  ) {
    if (property.equals(ONE))
      return Vec3i.ZERO;

    return switch (state.getValue(AXIS)) {
      case X -> new Vec3i(7, 0, 0);
      case Y -> new Vec3i(0, 7, 0);
      case Z -> new Vec3i(0, 0, 7);
    };
  }

  @Override
  public @NotNull String getPropertyFromInteraction(
    BlockState state,
    @NotNull BlockGetter level,
    @NotNull Vec3i hitLocation,
    @NotNull BlockPos blockPos,
    @NotNull Direction facing,
    @NotNull Vec3 unscaledHit
  ) {
    double coordinate = switch (state.getValue(AXIS)) {
      case X -> unscaledHit.x;
      case Y -> unscaledHit.y;
      case Z -> unscaledHit.z;
    };

    return coordinate >= state.getValue(SLICE_Y) / 8.0 ? TWO : ONE;
  }

  @Override
  public @NotNull String getPropertyFromRender(
    String renderingProperty,
    BlockState state,
    BlockGetter level,
    Vec3i vector,
    BlockPos blockPos
  ) {
    int coordinate = switch (state.getValue(AXIS)) {
      case X -> vector.getX();
      case Y -> vector.getY();
      case Z -> vector.getZ();
    };

    return coordinate >= state.getValue(SLICE_Y) ? TWO : ONE;
  }

  // Geometry
  @Override
  public @NotNull VoxelShape getShape(
    @NotNull BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    return Shapes.block();
  }

  @Override
  public @NotNull VoxelShape getPartialFaceShape(
    @NotNull BlockGetter level,
    @NotNull BlockState state,
    @NotNull String property,
    @NotNull Direction face
  ) {
    return partExists(state, property) ? DoubleBlock_Geometry.getPartShape(
      state.getValue(AXIS),
      state.getValue(SLICE_Y),
      property.equals(TWO)
    ).getFaceShape(face) : Shapes.empty();
  }

  // Placement and rotation
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
  }

  @Override
  public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
    return RotatedPillarBlock.rotatePillar(state, rotation);
  }

  @Override
  public @NotNull BlockState transform(@NotNull BlockState state, @NotNull StructureTransform transform) {
    Direction direction = getTransformedDirection(state, transform);

    state = state.setValue(AXIS, direction.getAxis());

    if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
      state = state.setValue(SLICE_Y, 8 - state.getValue(SLICE_Y));

    return state;
  }

  @Override
  public void transformStorage(BlockState state, @NotNull IMultiStateCopycatBlockEntity be, @NotNull StructureTransform transform) {
    int split = state.getValue(SLICE_Y) * 2;

    CopycatTransformableState.Part<Void> one = createPart(getPartCenter(state, split / 2));
    CopycatTransformableState.Part<Void> two = createPart(getPartCenter(state, (split + 16) / 2));

    one.untransform(transform);
    two.untransform(transform);

    Vec3i delta = two.vector.subtract(one.vector);

    int axis = Math.abs(delta.getX()) > Math.abs(delta.getY())
      && Math.abs(delta.getX()) > Math.abs(delta.getZ()) ? 0
      : Math.abs(delta.getY()) > Math.abs(delta.getZ()) ? 1 : 2;

    int oneCoordinate = getCoordinate(one.vector, axis);
    int twoCoordinate = getCoordinate(two.vector, axis);

    if (oneCoordinate > twoCoordinate) {
      be.getMaterialItemStorage().remapStorage(property -> switch (property) {
        case ONE -> TWO;
        case TWO -> ONE;
        default -> property;
      });
    }
  }

  // CT
  @Override
  public @NotNull InteractionResult toggleCT(
    @NotNull BlockState state,
    @NotNull Level level,
    @NotNull BlockPos pos,
    Player player,
    @NotNull BlockHitResult hitResult
  ) {
    if (!player.isShiftKeyDown() || !canToggleCT(state, level, pos))
      return InteractionResult.PASS;

    IMultiStateCopycatBlockEntity be = getCopycatBlockEntity(level, pos);

    if (be == null)
      return InteractionResult.PASS;

    boolean enableCT = !be.getMaterialItemStorage()
      .getMaterialItem(defaultProperty())
      .enableCT();

    be.setEnableCT(ONE, enableCT);
    be.setEnableCT(TWO, enableCT);

    BlockEntityUtils.redraw((BlockEntity) be);

    return InteractionResult.SUCCESS;
  }

  @Override
  public boolean canConnectTexturesToward(
    @NotNull String property,
    @NotNull BlockAndTintGetter reader,
    @NotNull BlockPos fromPos,
    @NotNull BlockPos toPos,
    @NotNull BlockState state
  ) {
    if (!isCTEnabled(state, reader, fromPos))
      return false;

    return super.canConnectTexturesToward(reader, fromPos, toPos, state);
  }

  @Override
  public boolean isIgnoredConnectivitySide(
    @NotNull BlockAndTintGetter reader,
    @NotNull BlockState state,
    @NotNull Direction face,
    @NotNull BlockPos fromPos,
    @Nullable BlockPos toPos,
    @Nullable BlockState toState
  ) {
    if (CopycatExternalContext.isForBlockingLogic())
      return super.isIgnoredConnectivitySide(
        reader, state, face, fromPos, toPos, toState
      );

    if (!isCTEnabled(state, reader, fromPos))
      return true;

    return super.isIgnoredConnectivitySide(
      reader, state, face, fromPos, toPos, toState
    );
  }

  // Block Entity
  @Override
  public @NotNull BlockEntityType<? extends MultiStateCopycatBlockEntity> getBlockEntityType() {
    return ModBlockEntities.DOUBLE_COPYCAT.get();
  }

  public boolean supportsExternalFaceHiding(@NotNull BlockState state) {
    return true;
  }

  public boolean hidesNeighborFace(
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull BlockState state,
    @NotNull BlockState neighborState,
    @NotNull Direction dir
  ) {
    return ICopycatBlock.hidesNeighborFace(level, pos, state, neighborState, dir);
  }

  // Helpers
  private Direction getTransformedDirection(
    BlockState state,
    StructureTransform transform
  ) {
    Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(AXIS));

    if (transform.mirror != null)
      direction = transform.mirrorFacing(direction);

    if (transform.rotationAxis != null)
      direction = transform.rotateFacing(direction);

    return direction;
  }

  private Vec3i getPartCenter(BlockState state, int coordinate) {
    return switch (state.getValue(AXIS)) {
      case X -> new Vec3i(coordinate, 8, 8);
      case Y -> new Vec3i(8, coordinate, 8);
      case Z -> new Vec3i(8, 8, coordinate);
    };
  }

  private CopycatTransformableState.Part<Void> createPart(Vec3i center) {
    return new CopycatTransformableState.Part<>(
      center.getX(),
      center.getY(),
      center.getZ()
    );
  }

  private int getCoordinate(Vec3i vector, int axis) {
    return switch (axis) {
      case 0 -> vector.getX();
      case 1 -> vector.getY();
      default -> vector.getZ();
    };
  }

  @Override
  public @NotNull ItemInteractionResult useItemOn(
    @NotNull ItemStack stack,
    @NotNull BlockState state,
    @NotNull Level level,
    @NotNull BlockPos pos,
    @NotNull Player player,
    @NotNull InteractionHand hand,
    @NotNull BlockHitResult hitResult
  ) {
    return InteractionUtils.sequentialItem(
      () -> InteractionUtils.usePlacementHelper(PLACEMENT_HELPER_ID, stack, state, level, pos, player, hand, hitResult),
      () -> super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    );
  }
}
