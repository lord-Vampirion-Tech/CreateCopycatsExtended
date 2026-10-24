package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeModelCore;
import com.copycatsplus.copycats.content.copycat.slope_layer.CopycatSlopeLayerModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation;

public class CopycatAdvSlopeLayerModelCore extends CopycatSlopeLayerModelCore {

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    WallOrientation orientation =
      state.getValue(CopycatAdvSlopeLayerBlock.WALL_ORIENTATION);

    if (!orientation.isWallMounted()) {
      super.emitCopycatQuads(key, state, context, material);
      return;
    }

    Direction facing =
      state.getValue(CopycatAdvSlopeLayerBlock.FACING);

    Half half =
      state.getValue(CopycatAdvSlopeLayerBlock.HALF);

    int layers =
      state.getValue(CopycatAdvSlopeLayerBlock.LAYERS);

    int angle = CopycatAdvSlopeLayerGeometry.wallAngle(facing, half);

    boolean rotateAroundZ =
      CopycatAdvSlopeLayerGeometry.rotateAroundZ(facing);

    AssemblyTransform transform = t -> {
      t.rotateY((int) facing.toYRot())
        .flipY(half == Half.TOP);

      if (rotateAroundZ) {
        t.rotateZ(angle);
      } else {
        t.rotateX(angle);
      }

      if (orientation.isSideways()) {
        int sidewaysAngle =
          CopycatAdvSlopeLayerGeometry.wallSidewaysAngle(facing);

        if (CopycatAdvSlopeLayerGeometry
          .wallSidewaysRotateAroundZ(facing)) {
          t.rotateZ(sidewaysAngle);
        } else {
          t.rotateX(sidewaysAngle);
        }
      }
    };

    double from;
    double to;

    if (layers <= 4) {
      from = 0;
      to = layers * 4.0;
    } else {
      from = (layers - 4) * 4.0;
      to = 16;
    }

    CopycatSlopeModelCore.assembleSlope(
      context,
      transform,
      from,
      to,
      enhanced
    );
  }
}