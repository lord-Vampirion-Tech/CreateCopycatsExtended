package net.ziftech.copycats_extended.utility;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.utility.BlockFaceUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class CopycatFaceOcclusion {

  private CopycatFaceOcclusion() {
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
    ) || neighborMaterial.skipRendering(
      ownMaterial,
      direction.getOpposite()
    );
  }
}
