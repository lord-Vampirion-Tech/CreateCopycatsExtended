package net.ziftech.copycats_extended.client.TEST.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableAABB;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableVec3;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope.map;
import static net.ziftech.copycats_extended.client.TEST.CustomQuadTransform.*;

public class TEST_CopycatBlock_ModelCore extends CopycatModelCore {

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

    double offset = (Math.sqrt(5) / 2);
    double maxHeight = 16 * offset;
    double margin = 3;

    final double angleBottom =
      Math.toDegrees(Math.atan2(maxHeight, 8));

    final double marginAdjBottom =
      margin / Math.tan(Math.toRadians(angleBottom) / 2);

    final double angleTop =
      Math.toDegrees(Math.atan2(8, maxHeight));

    final double marginAdjTop =
      margin / Math.tan(Math.toRadians(angleTop) / 2);

    final double halfLength =
      Math.sqrt(maxHeight * maxHeight + 16 * 16) / 2;

    final double midLengthBottom =
      halfLength - marginAdjBottom;

    final double marginAdjExcessBottom =
      marginAdjBottom - Math.floor(marginAdjBottom);

    final double midLengthTop =
      halfLength - marginAdjTop;

    final double marginAdjExcessTop =
      marginAdjTop - Math.floor(marginAdjTop);

    final double alignedLengthBottom =
      Math.abs(
        Math.floor(midLengthBottom + marginAdjExcessBottom)
          - marginAdjExcessBottom
      );

    final double alignedLengthTop =
      Math.floor(midLengthTop + marginAdjExcessTop)
        - marginAdjExcessTop;


    if (enhanced) {

// ================================================

// ================================================


//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(16, midLengthBottom, 16),
//        cull(0),
//        updateUV(
//          slope(Direction.SOUTH, (a, b) -> Math.clamp(b, 0, margin)),
//          slope(Direction.EAST, (a, b) -> Math.clamp(a, 0, margin))
////          slope(Direction.SOUTH, (a, b) -> Math.clamp(b, 0, margin)),
////          slope(Direction.EAST, (a, b) -> Math.clamp(a, 0, margin))
//        ),
//        rotate(
//          pivot(0, 0, 0),
//          angle(angleTop, 0, -angleTop)
//        )
//      );

      double correctedMargin = margin * 16 / maxHeight;

      double angle = Math.toDegrees(Math.atan2(8, 16));


      // =================================

      context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 0),
        aabb(16, 16, marginAdjBottom),
        cull(UP | NORTH | SOUTH),
        updateUV(
          slope(Direction.UP, (a, b) -> map(0, marginAdjBottom, 0, margin, b))
//          ,
//          symmetricSlope(Axis.X, (y, z) -> 16 - y)
        )
      );

      context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 16 - marginAdjBottom),
        aabb(16, 16, marginAdjBottom).move(0, 0, 16 - marginAdjBottom),
        cull(UP | NORTH | SOUTH),
        updateUV(
          slope(Direction.UP, (a, b) -> map(0, marginAdjBottom, 0, margin, 16 - b))
//          ,
//          symmetricSlope(Axis.X, (y, z) -> 16 - y)
        )
      );

      context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, marginAdjBottom),
        aabb(16, margin, 16 - marginAdjBottom * 2).move(0, 0, marginAdjBottom),
        cull(UP | NORTH | SOUTH),
        updateUV(
//          symmetricSlope(Axis.X, (y, z) -> 16 - y)
        )
      );

      context.assemblePiece(
        TRANSFORMS[facing.ordinal()],
        vec3(0, 0, 0),
        aabb(16, 16, 16),
        cull(0),
        updateUV(
          symmetricSlope(Axis.Z, (z, y) -> marginAdjBottom),
          symmetricSlope(Axis.Z, (z, y) -> map(0, 16, 0, 16 - margin, 16 - y)),
          slope(Direction.DOWN, (a, b) -> 16 - margin)
        )
      );

      // =================================


      // =================================

//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(16, 16, 16),
//        cull(0),
//
//        updateUV(
////          slope(Direction.SOUTH, (x, y) -> margin),
////          symmetricSlope(Axis.Z, (x, y) -> 16 - y * margin),
//          symmetricSlope(Axis.X, (y, z) -> 16 - y)
//        ),
//        scale(
//          pivot(8, 0, 0),
//          scale(1, offset, 1)
//        )
//        ,
//        rotate(
//          pivot(8, 0, 0),
//          angle(angle, 0, 0)
//        )
//      );


//

// ================================================

// ================================================

//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, margin, 0),
//        aabb(16, 8 - margin, 16).move(0, margin, 0),
//        cull(UP | DOWN | SOUTH | EAST),
//        updateUV(
//          slope(Direction.SOUTH, (a, b) -> margin),
//          slope(Direction.EAST, (a, b) -> margin)
//        )
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 8, 0),
//        aabb(16, 8 - margin, 16).move(0, 8, 0),
//        cull(UP | DOWN | SOUTH | EAST),
//        updateUV(
//          slope(Direction.SOUTH, (a, b) -> margin),
//          slope(Direction.EAST, (a, b) -> margin)
//        )
//      );
//
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 16 - margin, 0),
//        aabb(16, margin, 16).move(0, 16 - margin, 0),
//        cull(UP | DOWN | SOUTH | EAST),
//        updateUV(
//          slope(Direction.SOUTH, (a, b) -> 16 - b),
//          slope(Direction.EAST, (a, b) -> 16 - a)
//        )
//      );

// ================================================

//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(16, 16, marginAdjBottom),
//        cull(DOWN | NORTH | SOUTH),
//        updateUV(
//          slope(Direction.DOWN,
//            (a, b) -> map(0, marginAdjBottom, 0, margin, b)
//          )
//        ),
//        translate(0, -16, 0),
//        rotate(
//          pivot(0, 0, 0),
//          angle(-angleBottom, 0, 0)
//        )
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(16, 16, marginAdjBottom),
//        cull(DOWN | NORTH | SOUTH),
//        updateUV(
//          slope(Direction.DOWN,
//            (a, b) -> map(0, marginAdjBottom, 0, margin, b)
//          )
//        ),
//        translate(0, -16, 0),
//        rotate(
//          pivot(0, 0, 0),
//          angle(0, 0, angleBottom)
//        )
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