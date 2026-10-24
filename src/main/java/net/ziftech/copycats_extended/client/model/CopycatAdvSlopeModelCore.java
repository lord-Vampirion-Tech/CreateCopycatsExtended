
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.SlopeMode;

public class CopycatAdvSlopeModelCore extends CopycatSlopeModelCore {

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    SlopeMode mode = state.getValue(CopycatAdvSlopeBlock.SLOPE_MODE);

    boolean wallMounted =
      state.getValue(CopycatAdvSlopeBlock.WALL_ORIENTATION);

    // Обычный скос пола или потолка.
    if (!wallMounted) {
      switch (mode) {
        case FIRST:
          // Стандартное поведение Copycats+.
          super.emitCopycatQuads(key, state, context, material);
          return;

        case SECOND:
          // TODO: наклонная поверхность должна использовать
          // боковую текстуру материала.
          super.emitCopycatQuads(key, state, context, material);
          return;

        case THIRD:
          // TODO: разделить наклонную поверхность на две части:
          // одна использует боковую текстуру,
          // вторая — верхнюю/нижнюю текстуру.
          super.emitCopycatQuads(key, state, context, material);
          return;
      }
    }

    // Настенный скос оставляем без изменений.
    Direction facing = state.getValue(CopycatAdvSlopeBlock.FACING);
    Half half = state.getValue(CopycatAdvSlopeBlock.HALF);

    int angle = CopycatAdvSlopeGeometry.wallAngle(facing, half);
    int sidewaysAngle =
      CopycatAdvSlopeGeometry.wallSidewaysAngle(facing);

    AssemblyTransform transform = t -> {
      t.rotateY((int) facing.toYRot())
        .flipY(half == Half.TOP);

      // Первый поворот: наклон на стену.
      if (CopycatAdvSlopeGeometry.rotateAroundZ(facing)) {
        t.rotateZ(angle);
      } else {
        t.rotateX(angle);
      }

      // Второй поворот: ориентация вдоль стены.
      if (CopycatAdvSlopeGeometry.wallSidewaysRotateAroundZ(facing)) {
        t.rotateZ(sidewaysAngle);
      } else {
        t.rotateX(sidewaysAngle);
      }
    };

    CopycatSlopeModelCore.assembleSlope(
      context,
      transform,
      0,
      16,
      enhanced
    );
  }
}
