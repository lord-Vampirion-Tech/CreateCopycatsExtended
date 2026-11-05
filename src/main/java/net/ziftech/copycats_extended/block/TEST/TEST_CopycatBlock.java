package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;

import com.simibubi.create.content.contraptions.StructureTransform;

import net.ziftech.copycats_extended.client.model.TEST.TEST_CopycatBlockGeometry;
import net.ziftech.copycats_extended.registry.ModBlockEntities;
import net.ziftech.copycats_extended.utility.CopycatFaceOcclusion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TEST_CopycatBlock extends CCWaterloggedCopycatBlock {

  private static final VoxelShape TEST_SHAPE = TEST_CopycatBlockGeometry.create();

  public TEST_CopycatBlock(Properties properties) {
    super(properties);
  }

  @Override
  public VoxelShape getShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    return TEST_SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    return TEST_SHAPE;
  }

  @Override
  public Class<CCCopycatBlockEntity> getBlockEntityClass() {
    return CCCopycatBlockEntity.class;
  }

  @Override
  public net.minecraft.world.level.block.entity.BlockEntityType<? extends CCCopycatBlockEntity>
  getBlockEntityType() {
    return ModBlockEntities.TEST_COPYCAT_BLOCK.get();
  }

  @Override
  public BlockState transform(
    BlockState state,
    StructureTransform transform
  ) {
    return state;
  }

  @Override
  public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
    return CopycatFaceOcclusion.getOcclusionShape(state, level, pos);
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
    return CopycatFaceOcclusion.hidesNeighborFace(level, pos, neighborState, direction);
  }
}
