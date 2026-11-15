package net.ziftech.copycats_extended.client.TEST.model.other;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.*;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadRotate;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadScale;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope.map;
import static net.ziftech.copycats_extended.client.TEST.CustomQuadTransform.symmetricSlope;

public class TEST_CopycatBlock_ModelCore_V3_FIN extends CopycatModelCore {

  private static final AssemblyTransform[] TRANSFORMS = {
    t -> t.rotateX(180), // DOWN
    AssemblyTransform.IDENTITY, // UP
    t -> t.rotateX(90), // NORTH
    t -> t.rotateX(270), // SOUTH
    t -> t.rotateZ(270), // WEST
    t -> t.rotateZ(90) // EAST
  };

  private static final QuadTransform[] NORMAL_TRANSFORMS = {
    updateUV(
      symmetricSlope(Axis.Z, (x, y) -> 16 - y),
      symmetricSlope(Axis.X, (y, z) -> 16 - y)
    )
  };

  private static final ThreadLocal<Scratch> SCRATCH =
    ThreadLocal.withInitial(Scratch::new);

  private static final class Scratch {

    final MutableVec3 offset = new MutableVec3(0, 0, 0);
    final MutableAABB selection = new MutableAABB(1, 1, 1);
    final MutableCullFace cull = new MutableCullFace(0);

    void reset(boolean cullUp) {
      offset.set(0, 0, 0);

      selection.set(
        0, 0, 0,
        1, 1, 1
      );

      cull.set(
        cullUp,
        false,
        false,
        false,
        false,
        false
      );
    }
  }

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    Direction facing = state.getValue(TEST_CopycatBlock.FACING);
    boolean enhanced = state.getValue(TEST_CopycatBlock.ENHANCED);


    double UP = 8;
    double UP_2 = 8;

    double margin = 2;

    final double angleBottom =
      Math.toDegrees(Math.atan2(16, UP));

    final double marginAdjBottom =
      margin / Math.tan(Math.toRadians(angleBottom) / 2);

    final double angleBottom_2 =
      Math.toDegrees(Math.atan2(16, 16 - UP));

    final double marginAdjBottom_2 =
      margin / Math.tan(Math.toRadians(angleBottom_2) / 2);

    final double A = Math.hypot(16, UP);
    final double B = Math.hypot(16, 16 - UP);

    final double angleBottom_3 = Math.toDegrees(
      Math.acos(
        (A * A + B * B - 16 * 16) / (2 * A * B)
      )
    );

    final double marginAdjBottom_3 =
      margin / Math.tan(Math.toRadians(angleBottom_3) / 2);

    double coefficient_1 = A / 16;
    double coefficient_2 = B / 16;


    double coefficient_3 = Math.hypot(UP_2, 16) / 16;

    final double angle_2 =
      Math.toDegrees(Math.atan2(UP_2, 16));


    if (enhanced) {

      QuadScale Scale = scale(
        pivot(0, 0, 8),
        scale(1, coefficient_3, 1)
      );

      QuadRotate Rotate = rotate(
        pivot(0, 0, 8),
        angle(0, 0, -angle_2)
      );

//      for (int i = 0; i < 4; i++) {
//        int angle = i * 90;

      context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 0),
        aabb(16, 16, 16),
        cull(0),
        updateUV(
          symmetricSlope(Axis.Z, (a, b) -> Math.clamp(16 - b, 0, 15.999), UP / 16),
          symmetricSlope(Axis.X, (a, b) -> Math.clamp(16 - a, 0, 15.999), UP_2 / 16)
        )

        //, Scale, Rotate
      );

      context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 0),
        aabb(0, marginAdjBottom, 16),
        cull(0),
        updateUV(
          slope(Direction.SOUTH,
            (a, b) -> map(0, marginAdjBottom, 0, margin, b)
          )
        ),
        rotate(
          pivot(0, 0, 0),
          angle(-angleBottom + 90, 0, 0)
        )

        , Scale, Rotate
      );


//      }


//
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0.001, 0, 0),
//        aabb(0, 16, 16),
//        cull(0),
//        updateUV(
//          symmetricSlope(Axis.Z, (a, b) -> 16 - b, UP / 16)
//        )
//
//        , Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, 16, marginAdjBottom),
//        cull(0),
//        updateUV(
//          slope(Direction.UP,
//            (a, b) -> map(0, marginAdjBottom, 0, margin, b)
//          )
//        )
//
//        , Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, marginAdjBottom),
//        aabb(0, margin, 16 - marginAdjBottom - marginAdjBottom_2).move(0, 0, marginAdjBottom),
//        cull(0)
//
//        , Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 16 - marginAdjBottom_2),
//        aabb(0, 16, marginAdjBottom_2).move(0, 0, 16 - marginAdjBottom_2),
//        cull(0),
//        updateUV(
//          slope(Direction.UP,
//            (a, b) -> map(0, marginAdjBottom_2, 0, margin, 16 - b)
//          )
//        )
//
//        , Scale, Rotate
//      );
//
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, marginAdjBottom, 16),
//        cull(0),
//        updateUV(
//          slope(Direction.SOUTH,
//            (a, b) -> map(0, marginAdjBottom, 0, margin, b)
//          )
//        ),
//        rotate(
//          pivot(0, 0, 0),
//          angle(-angleBottom + 90, 0, 0)
//        )
//
//        , Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 16 - marginAdjBottom_3, 0),
//        aabb(0, marginAdjBottom_3, 16).move(0, 16 - marginAdjBottom_3, 0),
//        cull(0),
//        updateUV(
//          slope(Direction.SOUTH,
//            (a, b) -> map(0, marginAdjBottom_3, 0, margin, 16 - b)
//          )
//        ),
//        translate(0, -(16 - marginAdjBottom_3) + 16 * coefficient_1 - marginAdjBottom_3, 0)
//        ,
//        rotate(
//          pivot(0, 0, 0),
//          angle(-angleBottom + 90, 0, 0)
//        )
//
//        , Scale, Rotate
//      );
////
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, marginAdjBottom_2, 16),
//        cull(0),
//        updateUV(
//          slope(Direction.NORTH,
//            (a, b) -> map(0, marginAdjBottom_2, 0, margin, b)
//          )
//        ),
//        rotate(
//          pivot(0, 0, 16),
//          angle(angleBottom_2 - 90, 0, 0)
//        )
//
//        , Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, marginAdjBottom / coefficient_1, 0),
//        aabb(0, 16 - marginAdjBottom - marginAdjBottom_2, margin).move(0, marginAdjBottom, 0),
//        cull(0),
//        scale(
//          pivot(0, 0, 0),
//          scale(1, coefficient_1, 1)
//        ),
//        rotate(
//          pivot(0, 0, 0),
//          angle(-angleBottom + 90, 0, 0)
//        )
//
//        , Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, marginAdjBottom_2 / coefficient_2, 16 - margin),
//        aabb(0, 16 - marginAdjBottom - marginAdjBottom_2, margin).move(0, marginAdjBottom_2, 16 - margin),
//        cull(0),
//        scale(
//          pivot(0, 0, 0),
//          scale(1, coefficient_2, 1)
//        ),
//        rotate(
//          pivot(0, 0, 16),
//          angle(angleBottom_2 - 90, 0, 0)
//        )
//
//        , Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 16 - marginAdjBottom_3, 0),
//        aabb(0, marginAdjBottom_3, 16).move(0, 16 - marginAdjBottom_3, 0),
//        cull(0),
//        updateUV(
//          slope(Direction.NORTH,
//            (a, b) -> map(0, marginAdjBottom_3, 0, margin, 16 - b)
//          )
//        ),
//        translate(0, -(16 - marginAdjBottom_3) + 16 * coefficient_2 - marginAdjBottom_3, 0)
//        ,
//        rotate(
//          pivot(0, 0, 16),
//          angle(angleBottom_2 - 90, 0, 0)
//        )
//
//        , Scale, Rotate
//      );


    } else {
      Scratch scratch = SCRATCH.get();
      scratch.reset(true);

      context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        scratch.offset,
        scratch.selection,
        scratch.cull,
        NORMAL_TRANSFORMS
      );
    }
  }
}