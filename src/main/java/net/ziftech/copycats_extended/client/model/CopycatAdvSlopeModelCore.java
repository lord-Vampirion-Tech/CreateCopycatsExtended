
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;

public class CopycatAdvSlopeModelCore extends CopycatModelCore {

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    Direction facing = state.getValue(
      CopycatAdvSlopeBlock.FACING
    );

    int rotation = (int) facing.toYRot();

    boolean wallMounted = state.getValue(
      CopycatAdvSlopeBlock.WALL_MOUNTED
    );

    AssemblyTransform transform;

    if (wallMounted) {
      // Боковой скос: геометрия Copycat Vertical Slope.
      transform = t -> t.rotateZ(-90).rotateY(rotation);
    } else {
      // Обычный скос: учитываем верхнюю или нижнюю половину.
      Half half = state.getValue(
        CopycatAdvSlopeBlock.HALF
      );

      boolean flipY = half == Half.TOP;

      transform = t -> t.flipY(flipY).rotateY(rotation);
    }

    // Общая геометрия скоса Copycats+ для обоих вариантов.
    CopycatSlopeModelCore.assembleSlope(
      context,
      transform,
      0,
      16,
      enhanced
    );
  }
}
