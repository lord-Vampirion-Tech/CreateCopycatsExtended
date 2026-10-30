
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore.EntryType;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore.ModelEntry;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.SlopeMode;

import java.util.List;

import static com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore.MATERIAL_KEY;
import static com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore.getModelOf;

public class CopycatAdvSlopeModelCore extends CopycatSlopeModelCore {

  @Override
  public void registerModels(List<ModelEntry> entries) {
    entries.add(new ModelEntry(
      MATERIAL_KEY,
      (state, material) -> {
        BakedModel model = getModelOf(material);

        if (state.getValue(CopycatAdvSlopeBlock.WALL_ORIENTATION)) {
          return model;
        }

        SlopeMode mode = state.getValue(CopycatAdvSlopeBlock.SLOPE_MODE);

        if (mode == SlopeMode.FIRST) {
          return model;
        }

        return new SlopeTextureBakedModel(model, state, mode);
      },
      this,
      EntryType.COPYCAT
    ));
  }

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    boolean wallMounted =
      state.getValue(CopycatAdvSlopeBlock.WALL_ORIENTATION);

    if (!wallMounted) {
      super.emitCopycatQuads(key, state, context, material);
      return;
    }

    Direction facing = state.getValue(CopycatAdvSlopeBlock.FACING);
    Half half = state.getValue(CopycatAdvSlopeBlock.HALF);

    int angle = CopycatAdvSlopeGeometry.wallAngle(facing, half);
    int sidewaysAngle = CopycatAdvSlopeGeometry.wallSidewaysAngle(facing);

    AssemblyTransform transform = t -> {
      t.rotateY((int) facing.toYRot())
        .flipY(half == Half.TOP);

      if (CopycatAdvSlopeGeometry.rotateAroundZ(facing)) {
        t.rotateZ(angle);
      } else {
        t.rotateX(angle);
      }

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
