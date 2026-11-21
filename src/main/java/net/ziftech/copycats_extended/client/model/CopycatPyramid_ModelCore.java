package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramidCorner;

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
      case SOUTH -> 0;
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
      default -> t -> {};
    };

    return rotate.andThen(flip).andThen(TRANSFORMS[facing.ordinal()]);
  }

//  private static final AssemblyTransform[] OPPOSITE_TRANSFORMS = new AssemblyTransform[]{
//    // Index 0
//    t -> {
//      t.flipY(true);
//      t.rotateY(270);
//    },
//    t -> t.rotateY(90),
//    t -> {
//      t.rotateX(90);
//      t.rotateZ(90);
//    },
//    t -> {
//      t.rotateX(270);
//      t.rotateZ(90);
//    },
//    t -> t.rotateZ(270),
//    t -> t.rotateZ(90),
//
//    // Index 1
//    t -> t.flipY(true),
//    t -> {
//    },
//    t -> t.rotateX(90),
//    t -> t.rotateX(270),
//    t -> {
//      t.rotateZ(270);
//      t.rotateX(90);
//    },
//    t -> {
//      t.rotateY(90);
//      t.rotateZ(90);
//    }
//  };

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
      case CORNER -> context.assemblePiece(
        getTransform(
          facing,
          state.getValue(CopycatPyramidCorner.ROTATION)
        ),
        vec3(0, 0, 0),
        aabb(16, 16, 16),
        cull(0),
        updateUV(
          slope(EAST, (a, b) -> 16 - a),
          slope(SOUTH, (a, b) -> 16 - b)
        )
      );
//      case OPPOSITE -> {
//        context.assemblePiece(
//          getTransform(
//            facing,
//            getOppositeRotation(
//              state.getValue(CopycatPyramidOpposite.ROTATION)
//            )
//          ),
//          vec3(0, 0, 0),
//          aabb(16, 16, 16),
//          cull(0),
//          updateUV(
//            symmetricSlope(Direction.Axis.X, (a, b) -> 16 - a)
//          )
//        );
//      }
//      case QUAD -> {
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, 0, 0),
//          aabb(16, 16, 16),
//          cull(0),
//          updateUV(
//            symmetricSlope(Axis.X, (a, b) -> 16 - a)
//          )
//        );
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, 0, 0),
//          aabb(16, 16, 16),
//          cull(0),
//          updateUV(
//            symmetricSlope(Axis.Z, (a, b) -> 16 - b)
//          )
//        );
//      }
//      case TRIPLE -> context.assemblePiece(
//        getTransform(
//          facing,
//          state.getValue(CopycatPyramidTriple.ROTATION)
//        ),
//        vec3(0, 0, 0),
//        aabb(16, 16, 16),
//        cull(0),
//        updateUV(
//          slope(WEST, (a, b) -> 16 - a),
//          slope(NORTH, (a, b) -> 16 - b)
//        )
//      );
    }
  }

//  private static Direction getOppositeRotation(Direction rotation) {
//    return switch (rotation) {
//      case SOUTH -> WEST;
//      case WEST -> SOUTH;
//      default -> rotation;
//    };
//  }
}