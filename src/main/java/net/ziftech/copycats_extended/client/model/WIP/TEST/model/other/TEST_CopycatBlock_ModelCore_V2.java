//package net.ziftech.copycats_extended.client.TEST.model;
//
//import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.*;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadRotate;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadScale;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;
//import net.minecraft.core.Direction;
//import net.minecraft.core.Direction.Axis;
//import net.minecraft.world.level.block.state.BlockState;
//import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock;
//
//import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
//import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;
//import static com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope.map;
//import static net.ziftech.copycats_extended.client.TEST.CustomQuadTransform.symmetricSlope;
//
//public class TEST_CopycatBlock_ModelCore_V2 extends CopycatModelCore {
//
//  private static final AssemblyTransform[] TRANSFORMS = {
//    t -> t.rotateX(180), // DOWN
//    AssemblyTransform.IDENTITY, // UP
//    t -> t.rotateX(90), // NORTH
//    t -> t.rotateX(270), // SOUTH
//    t -> t.rotateZ(270), // WEST
//    t -> t.rotateZ(90) // EAST
//  };
//
//  private static final QuadTransform[] NORMAL_TRANSFORMS = {
//    updateUV(
//      symmetricSlope(Axis.Z, (x, y) -> 16 - y),
//      symmetricSlope(Axis.X, (y, z) -> 16 - y)
//    )
//  };
//
//  private static final ThreadLocal<Scratch> SCRATCH =
//    ThreadLocal.withInitial(Scratch::new);
//
//  private static final class Scratch {
//
//    final MutableVec3 offset = new MutableVec3(0, 0, 0);
//    final MutableAABB selection = new MutableAABB(1, 1, 1);
//    final MutableCullFace cull = new MutableCullFace(0);
//
//    void reset(boolean cullUp) {
//      offset.set(0, 0, 0);
//
//      selection.set(
//        0, 0, 0,
//        1, 1, 1
//      );
//
//      cull.set(
//        cullUp,
//        false,
//        false,
//        false,
//        false,
//        false
//      );
//    }
//  }
//
//  @Override
//  public void emitCopycatQuads(
//    String key,
//    BlockState state,
//    CopycatRenderContext context,
//    BlockState material
//  ) {
//    Direction facing = state.getValue(TEST_CopycatBlock.FACING);
//    boolean enhanced = state.getValue(TEST_CopycatBlock.ENHANCED);
//
//
//    double _Size_Z_TOP = 16;
//
//    double _Size_Y_LEFT = 16;
//    double _Size_Y_RIGHT = 16;
//
//    double _Size_Z_BOTTOM = 16;
//
//    double offset = (Math.sqrt(5) / 2);
//    double maxHeight = 16;
//    double margin = 3;
//
//
//    double coefficient = Math.sqrt(16 * 16 + _Size_Z_TOP * _Size_Z_TOP) / 16;
//    double coefficient_2 = Math.sqrt(16 * 16 + (16 - _Size_Z_TOP) * (16 - _Size_Z_TOP)) / 16;
//
//    double angle = Math.toDegrees(Math.atan2(_Size_Z_TOP, maxHeight));
//    double angle_2 = Math.toDegrees(Math.atan2(16 - _Size_Z_TOP, maxHeight));
//    double angle_3 = Math.toDegrees(Math.atan2(maxHeight, _Size_Z_TOP));
//    double angle_4 = Math.toDegrees(Math.atan2(maxHeight, 16 - _Size_Z_TOP));
//
//    MutableCullFace cullFace = cull(UP | DOWN | SOUTH | NORTH | EAST);
//
//    final double angleBottom =
//      Math.toDegrees(Math.atan2(maxHeight, _Size_Z_TOP));
//
//    final double marginAdjBottom =
//      margin / Math.tan(Math.toRadians(angleBottom) / 2);
//
//    final double angleBottom_2 =
//      Math.toDegrees(Math.atan2(maxHeight, 16 - _Size_Z_TOP));
//
//    final double marginAdjBottom_2 =
//      margin / Math.tan(Math.toRadians(angleBottom_2) / 2);
//
//    final double angleTop =
//      Math.toDegrees(Math.atan2(8, maxHeight));
//
//    final double marginAdjTop =
//      margin / Math.tan(Math.toRadians(angleTop) / 2);
////
//    final double halfLength =
//      Math.sqrt(maxHeight * maxHeight + 16 * 16) / 2;
////
//    final double midLengthBottom =
//      halfLength - marginAdjBottom;
////
//    final double marginAdjExcessBottom =
//      marginAdjBottom - Math.floor(marginAdjBottom);
////
//    final double midLengthTop =
//      halfLength - marginAdjTop;
////
//    final double marginAdjExcessTop =
//      marginAdjTop - Math.floor(marginAdjTop);
////
//    final double alignedLengthBottom =
//      Math.abs(
//        Math.floor(midLengthBottom + marginAdjExcessBottom)
//          - marginAdjExcessBottom
//      );
////
//    final double alignedLengthTop =
//      Math.floor(midLengthTop + marginAdjExcessTop)
//        - marginAdjExcessTop;
//
//    if (enhanced) {
//
//      QuadRotate Rotate = rotate(
//        pivot(0, 0, 8),
//        angle(0, 0, 0)
//      );
//
//      QuadScale Scale = scale(
//        pivot(0, 0, 8),
//        scale(1, 1, 1)
//      );
//
//      //// НИЖНЯЯ ГРАНЬ
//      // лево
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, 16, marginAdjBottom),
//        cull(0),
//        updateUV(
//          slope(Direction.UP, (a, b) -> map(0, marginAdjBottom, 0, margin, b))
//        ),
//        Rotate,
//        Scale
//      );
//
//      // право
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 16 - marginAdjBottom_2),
//        aabb(0, 16, marginAdjBottom_2).move(0, 0, 16 - marginAdjBottom_2),
//        cull(0),
//        updateUV(
//          slope(Direction.UP, (a, b) -> map(0, marginAdjBottom_2, 0, margin, 16 - b))
//        ),
//        Rotate,
//        Scale
//      );
//
//      // середина
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, marginAdjBottom),
//        aabb(0, margin, 16 - marginAdjBottom - marginAdjBottom_2).move(0, 0, marginAdjBottom),
//        cull(0),
//        Rotate,
//        Scale
//      );
//
//      //// ЛЕВАЯ ГРАНЬ
//      // низ
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, marginAdjBottom, 16),
//        cull(0),
//        updateUV(
//          slope(Direction.SOUTH, (a, b) -> map(0, marginAdjBottom, 0, margin, b))
//        ),
//        rotate(
//          pivot(0, 0, 0),
//          angle(angle, 0, 0)
//        ),
//        Scale
//      );
//
//      // середина
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, marginAdjBottom / coefficient, 0),
//        aabb(0, 16 - marginAdjBottom - marginAdjBottom_2, margin).move(0, marginAdjBottom, 0),
//        cull(0),
//        scale(
//          pivot(0, 0, 0),
//          scale(1, coefficient, 1)
//        ),
//        rotate(
//          pivot(0, 0, 0),
//          angle(angle, 0, 0)
//        ),
//        Scale
//      );
//
//      // вверх
//
//
//      //// ПРАВАЯ ГРАНЬ
//      // низ
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, marginAdjBottom_2, 16),
//        cull(0),
//        updateUV(
//          slope(Direction.NORTH, (a, b) -> map(0, marginAdjBottom_2, 0, margin, b))
//        ),
//        rotate(
//          pivot(0, 0, 16),
//          angle(-angle_2, 0, 0)
//        ),
//        Scale
//      );
//
//      // середина
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, marginAdjBottom_2, 16 - margin),
//        aabb(0, 16 - marginAdjBottom - marginAdjBottom_2, margin).move(0, marginAdjBottom_2, 16 - margin),
//        cull(0),
//        rotate(
//          pivot(0, 0, 16),
//          angle(-angle_2, 0, 0)
//        ),
//        Scale
//      );
//
//      // вверх
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 16 - marginAdjBottom, 16 - margin),
//        aabb(0, marginAdjBottom, margin)
//          .move(0, 16 - marginAdjBottom, 16 - margin),
//        cull(0),
//        updateUV(
//          slope(Direction.NORTH, (a, b) -> map(0, marginAdjBottom_2, 0, margin, 16 - b))
//        ),
//        rotate(
//          pivot(0, 0, 16),
//          angle(-angle_2, 0, 0)
//        ),
//        Scale
//      );
//
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 16 - margin),
//        aabb(0, marginAdjTop, margin).move(0, 0, 16 - margin),
//        cull(0),
//        updateUV(
//          slope(Direction.NORTH,
//            (a, b) -> map(0, 16, 0, 8, 16 - b)
//          )
//        )
//      );
//
////
////
////
////
////
////
////
////
////
////
////
////
////
////
////
////
////
////
//
//
//    } else {
//      Scratch scratch = SCRATCH.get();
//      scratch.reset(true);
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        scratch.offset,
//        scratch.selection,
//        scratch.cull,
//        NORMAL_TRANSFORMS
//      );
//    }
//  }
//}