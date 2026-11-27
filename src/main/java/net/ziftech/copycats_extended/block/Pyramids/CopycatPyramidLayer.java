
package net.ziftech.copycats_extended.block.Pyramids;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.IStateType;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.context.UseOnContext;

import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.client.model.CopycatPyramid_Geometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CopycatPyramidLayer extends CopycatPyramidBase
  implements SpecialBlockItemRequirement, IStateType {

  public static final IntegerProperty LAYERS =
    IntegerProperty.create("layers", 1, 15);

  public CopycatPyramidLayer(Properties properties) {
    super(properties);

    registerDefaultState(
      defaultBlockState().setValue(LAYERS, 1)
    );
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.@NotNull Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(builder);
    builder.add(LAYERS);
  }

  @Override
  public @NotNull VoxelShape getShape(
    @NotNull BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    return CopycatPyramid_Geometry.createShape(
      ShapeType.PYRAMID_LAYER,
      state.getValue(FACING),
      Direction.SOUTH,
      state
    );
  }

  @Override
  public ShapeType getShapeType() {
    return ShapeType.PYRAMID_LAYER;
  }

  @Override
  public @NotNull BlockState getStateForPlacement(
    @NotNull BlockPlaceContext context
  ) {
    BlockState stateForPlacement = super.getStateForPlacement(context);
    BlockState state = context.getLevel().getBlockState(context.getClickedPos());

    if (state.is(this)) {
      if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
        return stateForPlacement;

      if (state.getValue(LAYERS) < 16)
        return state.setValue(LAYERS, state.getValue(LAYERS) + 1);
    }

    return stateForPlacement;
  }

  @Override
  public boolean canBeReplaced(
    @NotNull BlockState state,
    @NotNull BlockPlaceContext context
  ) {
    if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
      return false;

    return context.getItemInHand().is(this.asItem())
      && state.getValue(LAYERS) < 16;
  }


  @Override
  public @NotNull InteractionResult onSneakWrenched(
    BlockState state,
    @NotNull UseOnContext context
  ) {
    int layers = state.getValue(LAYERS);

    if (layers <= 1)
      return super.onSneakWrenched(state, context);

    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Player player = context.getPlayer();

    if (world instanceof ServerLevel serverLevel) {

      if (player != null && !player.isCreative()) {
        List<ItemStack> drops = Block.getDrops(
          state.setValue(LAYERS, 1),
          serverLevel,
          pos,
          world.getBlockEntity(pos),
          player,
          context.getItemInHand()
        );

        for (ItemStack drop : drops) {
          player.getInventory().placeItemBackInInventory(drop);
        }
      }

      BlockPos up = pos.relative(Direction.UP);

      world.setBlockAndUpdate(
        pos,
        state.setValue(LAYERS, layers - 1)
          .updateShape(
            Direction.UP,
            world.getBlockState(up),
            world,
            pos,
            up
          )
      );

      IWrenchable.playRemoveSound(world, pos);
    }

    return InteractionResult.SUCCESS;
  }

  @Override
  public ItemRequirement getRequiredItems(
    BlockState state,
    @Nullable BlockEntity blockEntity
  ) {
    return ICopycatBlock.getRequiredItemsForLayer(state, LAYERS);
  }
}
