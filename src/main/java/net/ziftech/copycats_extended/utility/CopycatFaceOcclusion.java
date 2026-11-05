package net.ziftech.copycats_extended.utility;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.utility.BlockFaceUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CopycatFaceOcclusion {

  private CopycatFaceOcclusion() {
  }

  public static VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
    BlockState material = ICopycatBlock.getMaterial(level, pos);
    if (material.isAir()) return Shapes.empty();

    return Shapes.joinUnoptimized(
      state.getShape(level, pos),
      material.getOcclusionShape(level, pos),
      BooleanOp.AND
    );
  }

  public static boolean hidesNeighborFace(
    BlockGetter level,
    BlockPos pos,
    BlockState neighborState,
    Direction direction
  ) {
    BlockPos neighborPos = pos.relative(direction);
    BlockState ownState = level.getBlockState(pos);
    BlockState ownMaterial = ICopycatBlock.getMaterial(level, pos);
    BlockState neighborMaterial = neighborState.getBlock() instanceof ICopycatBlock
      ? ICopycatBlock.getMaterial(level, neighborPos)
      : neighborState;

    return BlockFaceUtils.canOcclude(
      level,
      neighborMaterial,
      neighborPos,
      ownState,
      pos,
      direction.getOpposite()
    ) || ownMaterial.skipRendering(
      neighborMaterial,
      direction.getOpposite()
    );
  }
}
