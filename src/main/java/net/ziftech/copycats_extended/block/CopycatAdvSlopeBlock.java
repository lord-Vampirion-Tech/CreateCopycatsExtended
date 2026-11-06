package net.ziftech.copycats_extended.block;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.copycatsplus.copycats.utility.BlockUtils;
import com.copycatsplus.copycats.utility.InteractionUtils;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.placement.PoleHelper;

import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.ziftech.copycats_extended.block.base.CopycatAdvBlockBase;
import net.ziftech.copycats_extended.client.model.CopycatAdvSlopeGeometry;
import net.ziftech.copycats_extended.registry.ModBlockEntities;
import net.ziftech.copycats_extended.registry.ModBlocks;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class CopycatAdvSlopeBlock extends CopycatSlopeBlock {

  // ─────────────────────────────────────────────
  // Block State Properties
  // ─────────────────────────────────────────────

  public static final BooleanProperty WALL_ORIENTATION =
    BooleanProperty.create("wall_orientation");

  public static final EnumProperty<SlopeMode> SLOPE_MODE =
    EnumProperty.create("slope_mode", SlopeMode.class);

  // ─────────────────────────────────────────────
  // Placement Helper
  // ─────────────────────────────────────────────

  private static final int PLACEMENT_HELPER_ID =
    PlacementHelpers.register(new PlacementHelper());

  // ─────────────────────────────────────────────
  // Constructor
  // ─────────────────────────────────────────────

  public CopycatAdvSlopeBlock(Properties properties) {
    super(properties);

    registerDefaultState(defaultBlockState()
      .setValue(WALL_ORIENTATION, false)
      .setValue(SLOPE_MODE, SlopeMode.FIRST)
    );
  }

  // ─────────────────────────────────────────────
  // Block State Definition
  // ─────────────────────────────────────────────

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(
      builder.add(WALL_ORIENTATION, SLOPE_MODE)
    );
  }

  // ─────────────────────────────────────────────
  // Block Placement
  // ─────────────────────────────────────────────

  @Override
  public @NotNull BlockState getStateForPlacement(
    @NotNull BlockPlaceContext context
  ) {
    BlockState placement = super.getStateForPlacement(context);

    return CopycatAdvBlockBase.getSlopePlacement(
      context,
      placement,
      this,
      FACING,
      HALF,
      WALL_ORIENTATION
    );
  }

  // ─────────────────────────────────────────────
  // Block Shape & Collision
  // ─────────────────────────────────────────────

  @Override
  public @NotNull VoxelShape getShape(
    BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    if (!state.getValue(WALL_ORIENTATION)) {
      return super.getShape(state, level, pos, context);
    }

    return CopycatAdvSlopeGeometry.createWallShape(
      state.getValue(FACING),
      state.getValue(HALF)
    );
  }

  @Override
  public @NotNull VoxelShape getCollisionShape(
    @NotNull BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    return getShape(state, level, pos, context);
  }

  // ─────────────────────────────────────────────
  // Player Interaction
  // ─────────────────────────────────────────────

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

  // ─────────────────────────────────────────────
  // Structure Transformation
  // ─────────────────────────────────────────────

  @Override
  public @NotNull BlockState transform(
    BlockState state,
    @NotNull StructureTransform transform
  ) {
    if (!state.getValue(WALL_ORIENTATION)) {
      return BlockUtils.transformStepLikeHorizontal(
          state,
          transform,
          defaultBlockState()
        )
        .setValue(WALL_ORIENTATION, false)
        .setValue(SLOPE_MODE, state.getValue(SLOPE_MODE));
    }

    return BlockUtils.transformStepLikeVertical(
        state,
        transform,
        defaultBlockState()
      )
      .setValue(WALL_ORIENTATION, true)
      .setValue(SLOPE_MODE, state.getValue(SLOPE_MODE));
  }

  // ─────────────────────────────────────────────
  // Block Entity
  // ─────────────────────────────────────────────

  @Override
  public @NotNull Class<CCCopycatBlockEntity> getBlockEntityClass() {
    return CCCopycatBlockEntity.class;
  }

  @Override
  public @NotNull BlockEntityType<? extends CCCopycatBlockEntity>
  getBlockEntityType() {
    return ModBlockEntities.COPYCAT_ADV_SLOPE.get();
  }

  // ─────────────────────────────────────────────
  // Placement Helper Implementation
  // ─────────────────────────────────────────────

  private static class PlacementHelper extends PoleHelper<Direction> {

    private PlacementHelper() {
      super(
        state -> state.is(ModBlocks.COPYCAT_ADV_SLOPE.get()),
        state -> state.getValue(WALL_ORIENTATION)
          ? Axis.Y
          : state.getValue(FACING).getClockWise().getAxis(),
        FACING
      );
    }

    @Override
    public @NotNull Predicate<ItemStack> getItemPredicate() {
      return stack ->
        stack.getItem() instanceof BlockItem item
          && item.getBlock() == ModBlocks.COPYCAT_ADV_SLOPE.get();
    }

    @Override
    public @NotNull PlacementOffset getOffset(
      Player player,
      Level world,
      BlockState state,
      BlockPos pos,
      BlockHitResult ray
    ) {
      PlacementOffset offset = super.getOffset(
        player,
        world,
        state,
        pos,
        ray
      );

      offset.withTransform(offset.getTransform()
        .andThen(placementState -> placementState
          .setValue(FACING, state.getValue(FACING))
          .setValue(HALF, state.getValue(HALF))
          .setValue(WALL_ORIENTATION, state.getValue(WALL_ORIENTATION))
          .setValue(SLOPE_MODE, state.getValue(SLOPE_MODE))
        )
      );

      return offset;
    }
  }
}