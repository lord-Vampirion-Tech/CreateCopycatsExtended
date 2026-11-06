package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.block.base.ModBlockPlacement;
import net.ziftech.copycats_extended.client.TEST.model.TEST_CopycatBlock_Geometry;
import net.ziftech.copycats_extended.registry.ModBlockEntities;
import org.jetbrains.annotations.NotNull;

public class TEST_CopycatBlock extends CCWaterloggedCopycatBlock {

  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  public TEST_CopycatBlock(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public BlockState transform(BlockState blockState, StructureTransform structureTransform) {
    return null;
  }

  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(builder.add(FACING));
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);

    return ModBlockPlacement.placeOnClickedFace(
      context,
      state,
      FACING
    );
  }

  @Override
  public @NotNull VoxelShape getShape(
    BlockState state,
    @NotNull BlockGetter level,
    @NotNull BlockPos pos,
    @NotNull CollisionContext context
  ) {
    return TEST_CopycatBlock_Geometry.createShape(
      state.getValue(FACING)
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

  @Override
  public @NotNull Class<CCCopycatBlockEntity> getBlockEntityClass() {
    return CCCopycatBlockEntity.class;
  }

  @Override
  public @NotNull BlockEntityType<? extends CCCopycatBlockEntity> getBlockEntityType() {
    return ModBlockEntities.TEST_COPYCAT_BLOCK.get();
  }
}
