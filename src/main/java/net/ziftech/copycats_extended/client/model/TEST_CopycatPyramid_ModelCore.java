package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramidOpposite;
import net.ziftech.copycats_extended.block.Pyramids.PyramidModelData;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.updateUV;
import static net.ziftech.copycats_extended.client.model.CustomQuadTransform.symmetricSlope;

public class TEST_CopycatPyramid_ModelCore  extends CopycatModelCore.WithData<PyramidModelData> {

  private static final AssemblyTransform[] OPPOSITE_TRANSFORMS = {
    t -> t.rotateX(180),
    t -> {},
    t -> t.rotateX(90),
    t -> t.rotateX(270),
    t -> t.rotateZ(270),
    t -> t.rotateZ(90),

    t -> {
      t.rotateY(90);
      t.rotateX(180);
    },
    t -> t.rotateY(90),
    t -> {
      t.rotateY(90);
      t.rotateX(90);
    },
    t -> {
      t.rotateY(90);
      t.rotateX(270);
    },
    t -> {
      t.rotateY(90);
      t.rotateZ(270);
    },
    t -> {
      t.rotateY(90);
      t.rotateZ(90);
    }
  };

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    PyramidModelData data = getData();

    if (data == null)
      return;

    double x = data.x();
    double y = data.y();
    double z = data.z();

    int rotation = state.getValue(CopycatPyramidOpposite.ROTATION);
    Direction facing = state.getValue(BASE_CopycatPyramidBlock.FACING);

    context.assemblePiece(
      OPPOSITE_TRANSFORMS[facing.ordinal() + rotation * 6],
      vec3(0, 0, 0),
      aabb(16, 16, 16),
      cull(0),
      updateUV(
        symmetricSlope(Direction.Axis.X, (a, b) -> 16 - a, x / 8)
      )
    );
  }
}