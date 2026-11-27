package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.block.Pyramids.*;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static net.minecraft.core.Direction.*;
import static net.ziftech.copycats_extended.utility.QuadTransformHelper.*;

public class CopycatPyramid_ModelCore extends CopycatModelCore {

  private static final AssemblyTransform[] TRANSFORMS = {
    t -> t.flipY(true), // DOWN
    t -> {
    }, // UP
    t -> t.rotateX(90), // NORTH
    t -> {
      t.rotateX(-90);
      t.rotateZ(180);
    }, // SOUTH
    t -> {
      t.rotateZ(-90);
      t.rotateX(90);

    }, // WEST
    t -> {
      t.rotateZ(90);
      t.rotateX(90);
    } // EAST
  };

  private static int getRotationDegrees(Direction rotation) {
    return switch (rotation) {
      case WEST -> 90;
      case NORTH -> 180;
      case EAST -> 270;
      default -> 0;
    };
  }

  private static AssemblyTransform getTransform(Direction facing, Direction rotation) {
    AssemblyTransform rotate = t -> t.rotateY(getRotationDegrees(rotation));

    AssemblyTransform flip = switch (facing) {
      case WEST, EAST -> t -> {
        t.flipZ(true);
        t.rotateY(180);
      };
      case SOUTH, NORTH -> t -> t.flipX(true);
      default -> t -> {
      };
    };

    return rotate.andThen(flip).andThen(TRANSFORMS[facing.ordinal()]);
  }

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
          symmetricSlope(Axis.X, (a, b) -> 16 - a),
          symmetricSlope(Axis.Z, (a, b) -> 16 - b)
        )
      );
      case PYRAMID_LAYER -> {
        int layers = state.getValue(CopycatPyramidLayer.LAYERS);

        int slopeLayers = layers <= 8 ? layers : 16 - layers;
        double offsetY = Math.max(0, layers - 8) * 2.0;

        context.assemblePiece(
          TRANSFORMS[facing.ordinal()],
          vec3(0, 0, 0),
          aabb(16, 16, 16),
          cull(0),
          updateUV(
            symmetricSlope(Axis.X, (a, b) -> 16 - a),
            symmetricSlope(Axis.Z, (a, b) -> 16 - b),
            slope(Direction.UP, (a, b) -> slopeLayers*2)
          ),
          translate(0,offsetY,0)
        );
      }
    }
  }
}