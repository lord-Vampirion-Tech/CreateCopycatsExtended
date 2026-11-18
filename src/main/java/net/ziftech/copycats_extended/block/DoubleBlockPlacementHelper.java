package net.ziftech.copycats_extended.block;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.function.Predicate;

public class DoubleBlockPlacementHelper implements IPlacementHelper {

  @Override
  public Predicate<BlockState> getStatePredicate() {
    return state -> state.getBlock() instanceof DoubleBlock;
  }

  @Override
  public Predicate<ItemStack> getItemPredicate() {
    return stack ->
      stack.getItem() instanceof BlockItem item
        && item.getBlock() instanceof DoubleBlock;
  }

  @Override
  public PlacementOffset getOffset(
    Player player,
    Level world,
    BlockState state,
    BlockPos pos,
    BlockHitResult ray
  ) {
    Axis axis = state.getValue(DoubleBlock.AXIS);

    List<Direction> directions =
      IPlacementHelper.orderedByDistanceOnlyAxis(
        pos,
        ray.getLocation(),
        axis
      );

    for (Direction direction : directions) {
      BlockPos targetPos = pos.relative(direction);

      if (!world.getBlockState(targetPos).canBeReplaced())
        continue;

      return PlacementOffset.success(
        targetPos,
        placementState -> placementState
          .setValue(DoubleBlock.AXIS, axis)
          .setValue(DoubleBlock.SLICE_Y,
            state.getValue(DoubleBlock.SLICE_Y))
          .setValue(DoubleBlock.CONNECT_HALF,
            state.getValue(DoubleBlock.CONNECT_HALF))
      );
    }

    return PlacementOffset.fail();
  }
}
