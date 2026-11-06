package net.ziftech.copycats_extended.utility;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;

public final class CopycatFaceOcclusion {

  private static final ThreadLocal<Boolean> MATERIAL_CULLING = ThreadLocal.withInitial(() -> false);

  private CopycatFaceOcclusion() {
  }

  public static Boolean getMaterialFaceResult(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    Direction face,
    BlockPos neighborPos
  ) {
    if (MATERIAL_CULLING.get()) return null;

    BlockState neighborState = level.getBlockState(neighborPos);
    boolean ownIsOurs = isOurCopycat(state);
    boolean neighborIsOurs = isOurCopycat(neighborState);

    if (!ownIsOurs && !neighborIsOurs) return null;

    BlockState ownMaterial = ownIsOurs ? ICopycatBlock.getMaterial(level, pos) : state;
    BlockState neighborMaterial = neighborIsOurs
      ? ICopycatBlock.getMaterial(level, neighborPos)
      : neighborState;

    if ((ownIsOurs && ownMaterial.isAir()) || (neighborIsOurs && neighborMaterial.isAir())) {
      return null;
    }

    BlockPos ownPos = pos.immutable();
    BlockPos adjacentPos = neighborPos.immutable();
    BlockGetter materialLevel = new MaterialBlockGetter(
      level,
      ownPos,
      ownMaterial,
      adjacentPos,
      neighborMaterial
    );

    MATERIAL_CULLING.set(true);
    try {
      return Block.shouldRenderFace(
        ownMaterial,
        materialLevel,
        ownPos,
        face,
        adjacentPos
      );
    } finally {
      MATERIAL_CULLING.remove();
    }
  }

  private static boolean isOurCopycat(BlockState state) {
    return state.getBlock() instanceof CopycatAdvSlopeBlock;
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
