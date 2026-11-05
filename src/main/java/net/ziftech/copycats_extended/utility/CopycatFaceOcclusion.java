package net.ziftech.copycats_extended.utility;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.utility.BlockFaceUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock;
import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock;

public final class CopycatFaceOcclusion {

  private CopycatFaceOcclusion() {
  }

  public static boolean shouldRenderFace(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    Direction face,
    BlockPos neighborPos
  ) {
    if (!isOurCopycat(state.getBlock())) {
      return Block.shouldRenderFace(state, level, pos, face, neighborPos);
    }

    BlockState ownMaterial = ICopycatBlock.getMaterial(level, pos);
    if (ownMaterial.isAir()) {
      return Block.shouldRenderFace(state, level, pos, face, neighborPos);
    }

    BlockState neighborState = level.getBlockState(neighborPos);
    BlockState neighborMaterial = neighborState.getBlock() instanceof ICopycatBlock
      ? ICopycatBlock.getMaterial(level, neighborPos)
      : neighborState;

    BlockPos ownPos = pos.immutable();
    BlockPos adjacentPos = neighborPos.immutable();
    BlockGetter materialLevel = new MaterialBlockGetter(
      level,
      ownPos,
      ownMaterial,
      adjacentPos,
      neighborMaterial
    );

    return Block.shouldRenderFace(
      ownMaterial,
      materialLevel,
      ownPos,
      face,
      adjacentPos
    );
  }

  private static boolean isOurCopycat(Block block) {
    return block instanceof TEST_CopycatBlock
      || block instanceof CopycatAdvSlopeBlock
      || block instanceof CopycatAdvSlopeLayerBlock;
  }

  public static boolean hidesNeighborFace(
    BlockGetter level,
    BlockPos pos,
    BlockState neighborState,
    Direction direction
  ) {
    BlockPos neighborPos = pos.relative(direction);
    BlockState ownMaterial = ICopycatBlock.getMaterial(level, pos);
    BlockState neighborMaterial = neighborState.getBlock() instanceof ICopycatBlock
      ? ICopycatBlock.getMaterial(level, neighborPos)
      : neighborState;

    return BlockFaceUtils.canOcclude(
      level,
      ownMaterial,
      pos,
      neighborMaterial,
      neighborPos,
      direction
    ) || ownMaterial.skipRendering(
      neighborMaterial,
      direction
    );
  }

  private static final class MaterialBlockGetter implements BlockGetter {

    private final BlockGetter delegate;
    private final BlockPos ownPos;
    private final BlockState ownMaterial;
    private final BlockPos neighborPos;
    private final BlockState neighborMaterial;

    private MaterialBlockGetter(
      BlockGetter delegate,
      BlockPos ownPos,
      BlockState ownMaterial,
      BlockPos neighborPos,
      BlockState neighborMaterial
    ) {
      this.delegate = delegate;
      this.ownPos = ownPos;
      this.ownMaterial = ownMaterial;
      this.neighborPos = neighborPos;
      this.neighborMaterial = neighborMaterial;
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
      return delegate.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
      if (pos.equals(ownPos)) return ownMaterial;
      if (pos.equals(neighborPos)) return neighborMaterial;
      return delegate.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
      return delegate.getFluidState(pos);
    }

    @Override
    public int getHeight() {
      return delegate.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
      return delegate.getMinBuildHeight();
    }
  }
}
