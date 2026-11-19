package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadScale;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramidOpposite;
import net.ziftech.copycats_extended.block.Pyramids.PyramidModelData;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.updateUV;
import static net.ziftech.copycats_extended.client.model.CustomQuadTransform.*;

public class CopycatPyramid_ModelCore extends CopycatModelCore.WithData<PyramidModelData> {

  private static final AssemblyTransform[] TRANSFORMS = {
    t -> t.flipY(true), // DOWN
    t -> {
    }, // UP
    t -> {
      t.rotateX(90);
      t.flipX(true);
    }, // NORTH
    t -> {
      t.rotateX(90);
      t.flipZ(true);
    }, // SOUTH
    t -> {
      t.rotateX(90);
      t.rotateY(90);
      t.flipX(true);
    }, // WEST
    t -> {
      t.rotateX(90);
      t.rotateY(90);
      t.flipZ(true);
    } // EAST
  };

  private static final AssemblyTransform[] OPPOSITE_TRANSFORMS = new AssemblyTransform[]{
    // Index 0
    t -> {
      t.flipY(true);
      t.rotateY(270);
    },
    t -> t.rotateY(90),
    t -> {
      t.rotateX(90);
      t.rotateZ(90);
    },
    t -> {
      t.rotateX(270);
      t.rotateZ(90);
    },
    t -> t.rotateZ(270),
    t -> t.rotateZ(90),

    // Index 1
    t -> t.flipY(true),
    t -> {
    },
    t -> t.rotateX(90),
    t -> t.rotateX(270),
    t -> {
      t.rotateZ(270);
      t.rotateX(90);
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
    Direction facing = state.getValue(BASE_CopycatPyramidBlock.FACING);

    switch (((BASE_CopycatPyramidBlock) state.getBlock()).getShapeType()) {
      case PYRAMID -> context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 0),
        aabb(16, 16, 16),
        cull(0),
        updateUV(
          symmetricSlope(Direction.Axis.X, (a, b) -> 16 - a),
          symmetricSlope(Direction.Axis.Z, (a, b) -> 16 - b)
        )
      );
      case CORNER -> context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 0),
        aabb(16, 16, 16),
        cull(0)
      );
      case OPPOSITE -> {
        int rotation = state.getValue(CopycatPyramidOpposite.ROTATION);

        context.assemblePiece(
          OPPOSITE_TRANSFORMS[facing.ordinal() + rotation * 6],
          vec3(0, 0, 0),
          aabb(16, 16, 16),
          cull(0),
          updateUV(
            symmetricSlope(Direction.Axis.X, (a, b) -> 16 - a)
          )
        );
      }
      case QUAD -> context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 0),
        aabb(16, 16, 16),
        cull(0)
      );
      case TRIPLE -> context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 0),
        aabb(16, 16, 16),
        cull(0)
      );
    }
  }
}