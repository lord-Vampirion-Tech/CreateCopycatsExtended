package net.ziftech.copycats_extended.utility;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.utility.BlockFaceUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;

public final class CopycatFaceOcclusion {

  private CopycatFaceOcclusion() {
  }

  public static Boolean getMaterialFaceResult(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    Direction face,
    BlockPos neighborPos
  ) {
    BlockState neighborState = level.getBlockState(neighborPos);

    // Only handle the face of a normal block adjacent to our advanced slope.
    // The slope's own face remains controlled by Copycats+' native shape logic.
    if (state.getBlock() instanceof CopycatAdvSlopeBlock
      || !(neighborState.getBlock() instanceof CopycatAdvSlopeBlock)) {
      return null;
    }

    BlockState neighborMaterial = ICopycatBlock.getMaterial(level, neighborPos);
    if (neighborMaterial.isAir()) return null;

    // Preserve same-material face hiding, including transparent materials.
    if (state.skipRendering(neighborMaterial, face)) {
      return false;
    }

    // Transparent materials do not occlude neighboring block faces.
    // Returning null lets Copycats+' canOcclude hook handle this case.
    if (!neighborMaterial.canOcclude()) {
      return null;
    }

    // Test whether the slope's material fully covers this neighboring face.
    return !BlockFaceUtils.canOcclude(
      level,
      state,
      pos,
      neighborMaterial,
      neighborPos,
      face
    );
  }
}
