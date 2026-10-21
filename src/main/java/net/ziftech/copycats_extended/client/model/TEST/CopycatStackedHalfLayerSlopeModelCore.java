package net.ziftech.copycats_extended.client.model.TEST;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.ziftech.copycats_extended.block.TEST.CopycatStackedHalfLayerSlopeBlock;

public class CopycatStackedHalfLayerSlopeModelCore extends CopycatModelCore {

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    boolean top;

    if (CopycatStackedHalfLayerSlopeBlock.TOP.equals(key)) {
      top = true;

      if (!state.getValue(CopycatStackedHalfLayerSlopeBlock.HAS_TOP))
        return;
    } else if (CopycatStackedHalfLayerSlopeBlock.BOTTOM.equals(key)) {
      top = false;

      if (!state.getValue(CopycatStackedHalfLayerSlopeBlock.HAS_BOTTOM))
        return;
    } else {
      return;
    }

    Direction facing = state.getValue(
      top
        ? CopycatStackedHalfLayerSlopeBlock.TOP_FACING
        : CopycatStackedHalfLayerSlopeBlock.BOTTOM_FACING
    );

    Half half = state.getValue(
      top
        ? CopycatStackedHalfLayerSlopeBlock.TOP_HALF
        : CopycatStackedHalfLayerSlopeBlock.BOTTOM_HALF
    );

    boolean wallMounted = state.getValue(
      top
        ? CopycatStackedHalfLayerSlopeBlock.TOP_WALL_MOUNTED
        : CopycatStackedHalfLayerSlopeBlock.BOTTOM_WALL_MOUNTED
    );

    int rotation = (int) facing.toYRot();

    AssemblyTransform transform;

    if (wallMounted) {
      transform = t -> t.rotateZ(-90).rotateY(rotation);
    } else {
      boolean flipY = half == Half.TOP;
      transform = t -> t.flipY(flipY).rotateY(rotation);
    }

    CopycatSlopeModelCore.assembleSlope(
      context,
      transform,
      0,
      16,
      enhanced
    );
  }
}
