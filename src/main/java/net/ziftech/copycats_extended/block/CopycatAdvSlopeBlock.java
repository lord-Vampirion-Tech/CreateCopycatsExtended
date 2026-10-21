
package net.ziftech.copycats_extended.block;

import com.copycatsplus.copycats.CCShapes;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.utility.BlockUtils;
import com.copycatsplus.copycats.utility.InteractionUtils;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.placement.PoleHelper;

import net.createmod.catnip.placement.PlacementHelpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.ziftech.copycats_extended.block.base.CopycatAdvBlockBase;
import net.ziftech.copycats_extended.registry.ModBlockEntities;
import net.ziftech.copycats_extended.registry.ModBlocks;

import java.util.function.Predicate;

import java.util.List;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static net.minecraft.core.Direction.Axis;

public class CopycatAdvSlopeBlock extends CCWaterloggedCopycatBlock {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
  public static final BooleanProperty WALL_MOUNTED = BooleanProperty.create("wall_mounted");

  private static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new PlacementHelper());

  public CopycatAdvSlopeBlock(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState()
        .setValue(FACING, Direction.NORTH)
        .setValue(HALF, Half.BOTTOM)
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
    if (state.getValue(WALL_MOUNTED)) {
      return CCShapes.VERTICAL_SLOPE
        .get(state.getValue(FACING))
        .toShape();
    }

    return CCShapes.SLOPE
      .get(state.getValue(FACING))
      .get(state.getValue(HALF))
      .toShape();
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(
      builder.add(FACING, HALF, WALL_MOUNTED)
    );
  }


  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);

    if (state == null) return null;

    return CopycatAdvBlockBase.getSlopePlacement(context, state, this, FACING, HALF, WALL_MOUNTED);
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
    return ModBlockEntities.COPYCAT_ADV_SLOPE.get();
  }

  @Override
  public ItemInteractionResult useItemOn(
    ItemStack stack,
    BlockState state,
    Level level,
    BlockPos pos,
    Player player,
    InteractionHand hand,
    BlockHitResult hitResult
  ) {
    return InteractionUtils.sequentialItem(
      () -> InteractionUtils.usePlacementHelper(
        PLACEMENT_HELPER_ID,
        stack,
        state,
        level,
        pos,
        player,
        hand,
        hitResult
      ),
      () -> super.useItemOn(
        stack,
        state,
        level,
        pos,
        player,
        hand,
        hitResult
      )
    );
  }


  private static class PlacementHelper extends PoleHelper<Direction> {

    private PlacementHelper() {
      super(
        state -> state.is(ModBlocks.COPYCAT_ADV_SLOPE.get()),
        state -> state.getValue(WALL_MOUNTED)
          ? Axis.Y
          : state.getValue(FACING)
          .getClockWise()
          .getAxis(),
        FACING
      );
    }

    @Override
    public Predicate<ItemStack> getItemPredicate() {
      return stack ->
        stack.getItem() instanceof BlockItem item
          && item.getBlock().getClass() == CopycatAdvSlopeBlock.class;
    }

    @Override
    public PlacementOffset getOffset(
      Player player,
      Level world,
      BlockState state,
      BlockPos pos,
      BlockHitResult ray
    ) {
      List<Direction> directions = IPlacementHelper.orderedByDistance(
        pos,
        ray.getLocation(),
        dir -> dir.getAxis() == axisFunction.apply(state)
      );

      for (Direction dir : directions) {
        int range = AllConfigs.server().equipment.placementAssistRange.get();

        if (player != null) {
          AttributeInstance reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);

          if (reach != null && reach.hasModifier(
            ExtendoGripItem.singleRangeAttributeModifier.id()
          )) {
            range += 4;
          }
        }

        int poles = attachedPoles(world, pos, dir);

        if (poles >= range) continue;

        BlockPos newPos = pos.relative(dir, poles + 1);
        BlockState newState = world.getBlockState(newPos);

        if (newState.canBeReplaced()) {
          return PlacementOffset.success(
            newPos,
            newStateForPlacement -> newStateForPlacement
              .setValue(FACING, state.getValue(FACING))
              .setValue(HALF, state.getValue(HALF))
              .setValue(
                WALL_MOUNTED,
                state.getValue(WALL_MOUNTED)
              )
          );
        }
      }

      return PlacementOffset.fail();
    }
  }
}
