//package net.ziftech.copycats_extended.client.model.WIP.TEST.model.other;
//
//import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.*;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadRotate;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadScale;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;
//import net.minecraft.core.Direction;
//import net.minecraft.core.Direction.Axis;
//import net.minecraft.world.level.block.state.BlockState;
//import net.ziftech.copycats_extended.block.WIP.TEST_CopycatBlock;
//
//import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
//import static com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope.map;
//import static net.ziftech.copycats_extended.client.model.WIP.TEST.CustomQuadTransform.*;
//
//public class TEST_CopycatBlock_ModelCore_V5 extends CopycatModelCore {
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
//
//  private static double safeAcos(double x) {
//    return Math.acos(Math.clamp(x, -1.0, 1.0));
//  }
//
//  static double angle_(double a, double b, double c) {
//    return Math.acos((b * b + c * c - a * a) / (2 * b * c));
//  }
//
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
//    double TOP_Z = 16;
//    double TOP_X = 0;
//
//    double margin = 3;
//
//    final double angleBL = Math.toDegrees(Math.atan2(16, TOP_Z));
//    final double marginAdjBL = margin / Math.tan(Math.toRadians(angleBL) / 2);
//
//    final double angleBR = Math.toDegrees(Math.atan2(16, 16 - TOP_Z));
//    final double marginAdjBR = margin / Math.tan(Math.toRadians(angleBR) / 2);
//
//    final double L = Math.hypot(TOP_Z, 16);
//    final double R = Math.hypot(16, 16 - TOP_Z);
//
//    final double angleT_1 = Math.toDegrees(Math.acos((L * L + R * R - 16 * 16) / (2 * L * R)));
//
//    final double marginAdjT = margin / Math.tan(Math.toRadians(angleT_1) / 2);
//    double coefficient_L = L / 16;
//    double coefficient_R = R / 16;
//
//
//    double coefficientALL = Math.hypot(TOP_X, 16) / 16;
//    final double angleALL = Math.toDegrees(Math.atan2(TOP_X, 16));
//
//    // =======
//
//    final double maxHeight = 16;
//
//    final double angleBottom =
//      Math.toDegrees(Math.atan2(maxHeight, 16));
//
//    final double marginAdjBottom =
//      margin / Math.tan(Math.toRadians(angleBottom) / 2);
//
//    final double angleTop =
//      Math.toDegrees(Math.atan2(16, maxHeight));
//
//    final double marginAdjTop =
//      margin / Math.tan(Math.toRadians(angleTop) / 2);
//
//    final double halfLength =
//      Math.sqrt(maxHeight * maxHeight + 16 * 16) / 2;
//
//    final double midLengthBottom =
//      halfLength - marginAdjBottom;
//
//
//    final double marginAdjExcessBottom =
//      marginAdjBottom - Math.floor(marginAdjBottom);
//
//    final double midLengthTop =
//      halfLength - marginAdjTop;
//
//    final double marginAdjExcessTop =
//      marginAdjTop - Math.floor(marginAdjTop);
//
//    final double alignedLengthBottom =
//      Math.abs(
//        Math.floor(midLengthBottom + marginAdjExcessBottom)
//          - marginAdjExcessBottom
//      );
//
//    final double alignedLengthTop =
//      Math.floor(midLengthTop + marginAdjExcessTop)
//        - marginAdjExcessTop;
//
//    if (enhanced) {
//
//
//      final double offset = 1 - 0.001;
//
//      // final double angle_a = Math.toDegrees(Math.atan2(16, TOP_Z));
//      // final double marginAdjBL = margin / Math.tan(Math.toRadians(angleBL) / 2);
//
//      final double square_size = 16;
//      final double z_top_point_offset = 0; // 0..16 square_size
//
//      double leng_a = square_size; // нижняя сторона
//      double leng_b = Math.hypot(z_top_point_offset, square_size); // левая сторона
//      double leng_c = Math.hypot(16 - z_top_point_offset, square_size); // правая сторона
//
//      // верхний угол
//      double angle_a = Math.toDegrees(safeAcos((leng_b * leng_b + leng_c * leng_c - leng_a * leng_a) / (2 * leng_b * leng_c)));
//      // правый угол
//      double angle_l = Math.toDegrees(Math.atan2(16, z_top_point_offset));
//      final double margin_r_bottom = margin / Math.tan(Math.toRadians(angle_l) / 2);
//      final double margin_r_top = margin / Math.tan(Math.toRadians(angle_a) / 2);
//      // левый угол
//      double angle_c = Math.toDegrees(Math.atan2(16, 16 - z_top_point_offset));
//
//      final double halfLength_l = Math.sqrt(maxHeight * maxHeight + 16 * 16) / 2;
//      final double midLength_l = halfLength - margin_r_bottom;
//
//      final double marginAdjExcess_l =
//        margin_r_bottom - Math.floor(margin_r_bottom);
//
//      final double alignedLength_l =
//        Math.abs(
//          Math.floor(midLength_l + marginAdjExcess_l)
//            - marginAdjExcess_l
//        );
//
//
////      final double angleBottom =
////        Math.toDegrees(Math.atan2(maxHeight, 16));
////
////      final double marginAdjBottom =
////        margin / Math.tan(Math.toRadians(angleBottom) / 2);
////
//
//      final double TEST_Z = /*state.getValue(TEST_CopycatBlock.TEST_VALUE_Z)*/ 3;
//      final double TEST_X = /*state.getValue(TEST_CopycatBlock.TEST_VALUE_X)*/ 3;
//
//      double MARGIN = 3;
//      final double OFFSET = 1 - 0.005;
//
//      final double ANGLE_RZ = Math.toDegrees(Math.atan2(16, TEST_Z));
//      final double ANGLE_LZ = Math.toDegrees(Math.atan2(16, 16 - TEST_Z));
//
//      final double ANGLE_RX = Math.toDegrees(Math.atan2(16, TEST_X));
//      final double ANGLE_LX = Math.toDegrees(Math.atan2(16, 16 - TEST_X));
//
//      final double LENGTH_RZ = Math.hypot(16, TEST_Z);
//      final double LENGTH_LZ = Math.hypot(16 - TEST_Z, 16);
//
//      final double ANGLE_TZ = Math.toDegrees(Math.acos((LENGTH_LZ * LENGTH_LZ + LENGTH_RZ * LENGTH_RZ - 16 * 16) / (2 * LENGTH_LZ * LENGTH_RZ)));
//
//      final double MARGIN_R = MARGIN / Math.tan(Math.toRadians(ANGLE_RZ) / 2);
//      final double MARGIN_L = MARGIN / Math.tan(Math.toRadians(ANGLE_LZ) / 2);
//      final double MARGIN_T = MARGIN / Math.tan(Math.toRadians(ANGLE_TZ) / 2);
//
//      double COFF_R = MARGIN_R / 16;
//      double COFF_L = MARGIN_L / 16;
//
//      double COFF_ALL = Math.hypot(TEST_X, 16) / 16;
//      final double ANGLE_ALL = Math.toDegrees(Math.atan2(TEST_X, 16));
//
//      QuadScale Scale = scale(
//        pivot(0, 0, 8),
//        scale(1, 1, 1)
//      );
//
//      QuadRotate Rotate = rotate(
//        pivot(0, 0, 8),
//        angle(0, 0, 0)
//      );
//
//      // TEST
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(16, 16, 16),
//        cull(0),
//        updateUV(
//          symmetricSlope(Axis.Z, (a, b) -> 16 - b, TEST_Z / 16),
//          symmetricSlope(Axis.X, (a, b) -> 16 - a, TEST_X / 16),
//          slope(Direction.UP, (a, b) -> 16 - (OFFSET * 2 - 1))
//        ),
//        scale(pivot(8, 8, 8), scale(OFFSET, OFFSET, OFFSET))
//      );
//
//      double t1 = Math.hypot(TEST_Z,16);
//      double t2 = Math.toDegrees(Math.atan(t1/TEST_X));
//
//
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, MARGIN_R, 16),
//        cull(0),
//        updateUV(slope(Direction.SOUTH, (a, b) -> map(0, MARGIN_R, 0, MARGIN, b))),
////        rotate(pivot(0, 0, 0), angle(90 - ANGLE_RZ, 0, 0)),
//        axisRot(
//          pivot(0, 0, 0),
//          Z(t2-90)
//        )
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, 16, MARGIN_R),
//        cull(0),
//        updateUV(slope(Direction.UP, (a, b) -> map(0, MARGIN_R, 0, MARGIN, b))),
//        axisRot(
//          pivot(0, 0, 8),
//          Z(t2-90)
//        )
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(0, MARGIN_L, 16).move(0, 0, 0),
//        cull(0),
//        updateUV(slope(Direction.NORTH, (a, b) -> map(0, MARGIN_L, 0, MARGIN, b))),
//        rotate(pivot(0, 0, 16), angle(ANGLE_LZ - 90, 0, 0)),
//        Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 16 - MARGIN_L),
//        aabb(0, 16, MARGIN_L).move(0, 0, 16 - MARGIN_L),
//        cull(0),
//        updateUV(slope(Direction.UP, (a, b) -> map(0, MARGIN_L, 0, MARGIN, 16 - b))),
//        Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 16 - MARGIN_T, 0),
//        aabb(0, MARGIN_T, 16).move(0, 16 - MARGIN_T, 0),
//        cull(0),
//        updateUV(slope(Direction.SOUTH, (a, b) -> map(0, MARGIN_T, 0, MARGIN, 16 - b))),
//        translate(0, LENGTH_RZ - 16, 0),
//        rotate(pivot(0, 0, 0), angle(90 - ANGLE_RZ, 0, 0)),
//        Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 16 - MARGIN_T, 0),
//        aabb(0, MARGIN_T, 16).move(0, 16 - MARGIN_T, 0),
//        cull(0),
//        updateUV(slope(Direction.NORTH, (a, b) -> map(0, MARGIN_T, 0, MARGIN, 16 - b))),
//        translate(0, LENGTH_LZ - 16, 0),
//        rotate(pivot(0, 0, 16), angle(ANGLE_LZ - 90, 0, 0)),
//        Scale, Rotate
//      );
//
//      // ================================================
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, MARGIN_R),
//        aabb(0, MARGIN, 16 - MARGIN_R - MARGIN_L).move(0, 0, MARGIN_R),
//        cull(0),
//        axisRot(
//          pivot(0, 0, 8),
//          Z(-ANGLE_RX)
//        )
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, MARGIN_R, 0),
//        aabb(0, ((LENGTH_RZ - MARGIN_R - MARGIN_T) / 2), MARGIN).move(0, (MARGIN_R), 0),
//        cull(0),
//        scale(pivot(0, MARGIN_R, 0), scale(1, 1, 1)),
//        rotate(pivot(0, 0, 0), angle(90 - ANGLE_RZ, 0, 0)),
//        Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, MARGIN_R + (LENGTH_RZ - MARGIN_R - MARGIN_T) / 2, 0),
//        aabb(0, ((LENGTH_RZ - MARGIN_R - MARGIN_T) / 2), MARGIN).move(0, (16 - MARGIN_T - (LENGTH_RZ - MARGIN_R - MARGIN_T) / 2), 0),
//        cull(0),
//        rotate(pivot(0, 0, 0), angle(90 - ANGLE_RZ, 0, 0)),
//        Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, MARGIN_L, 16 - MARGIN),
//        aabb(0, (LENGTH_LZ - MARGIN_L - MARGIN_T) / 2, 16).move(0, MARGIN_L, 16 - MARGIN),
//        cull(0),
//        rotate(pivot(0, 0, 16), angle(ANGLE_LZ - 90, 0, 0)),
//        Scale, Rotate
//      );
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, (LENGTH_LZ + MARGIN_L - MARGIN_T) / 2, 16 - MARGIN),
//        aabb(0, (LENGTH_LZ - MARGIN_L - MARGIN_T) / 2, MARGIN).move(0, 16 - (LENGTH_LZ - MARGIN_L + MARGIN_T) / 2, 16 - MARGIN),
//        cull(0),
//        rotate(pivot(0, 0, 16), angle(ANGLE_LZ - 90, 0, 0)),
//        Scale, Rotate
//      );
//
//      // ================================================
//
////      context.assemblePiece(
////        TRANSFORMS[facing.ordinal()],
////        vec3(-16, 0, 0),
////        aabb(16, marginAdjBottom, 16),
////        cull(0),
////        updateUV(
////          slope(Direction.SOUTH,
////            (a, b) -> map(0, marginAdjBottom, 0, MARGIN, b)
////          )
////        ),
////        rotate(
////          pivot(0, 0, 0),
////          angle(angleBottom, 0, 0)
////        )
////      );
////
////      context.assemblePiece(
////        TRANSFORMS[facing.ordinal()],
////        vec3(-16, 16 - alignedLengthBottom, maxHeight),
////        aabb(16, alignedLengthBottom, MARGIN)
////          .move(0, marginAdjBottom, 0),
////        cull(0),
////        scale(
////          pivot(16, maxHeight, 16),
////          scale(1, midLengthBottom / alignedLengthBottom, 1)
////        ),
////        translate(0, -halfLength, 0),
////        rotate(
////          pivot(16, maxHeight, 16),
////          angle(angleBottom, 0, 0)
////        )
////      );
////
////      context.assemblePiece(
////        TRANSFORMS[facing.ordinal()],
////        vec3(-16, 16 - alignedLengthTop, maxHeight),
////        aabb(16, alignedLengthTop, MARGIN)
////          .move(0, 16 - marginAdjTop - alignedLengthTop, 0),
////        cull(0),
////        scale(
////          pivot(16, maxHeight, 16),
////          scale(1, midLengthTop / alignedLengthTop, 1)
////        ),
////        translate(0, -marginAdjTop, 0),
////        rotate(
////          pivot(16, maxHeight, 16),
////          angle(angleBottom, 0, 0)
////        )
////      );
////
////      context.assemblePiece(
////        TRANSFORMS[facing.ordinal()],
////        vec3(-16, 16 - marginAdjTop, 0),
////        aabb(16, marginAdjTop, 16)
////          .move(0, 16 - marginAdjTop, 0),
////        cull(0),
////        updateUV(
////          slope(Direction.SOUTH,
////            (a, b) -> map(16 - marginAdjTop, 16, MARGIN, 0, b)
////          )
////        ),
////        translate(0, 0, maxHeight),
////        rotate(
////          pivot(16, maxHeight, 16),
////          angle(angleBottom, 0, 0)
////        )
////      );
////
////      // ===============================================
////
////      context.assemblePiece(
////        TRANSFORMS[facing.ordinal()],
////        vec3(0, 0, 0),
////        aabb(16, 16, marginAdjBottom),
////        cull(DOWN | NORTH | SOUTH),
////        updateUV(
////          slope(Direction.DOWN,
////            (a, b) -> map(0, marginAdjBottom, 0, MARGIN, b)
////          )
////        ),
////        translate(0, -16, 0),
////        rotate(
////          pivot(0, 0, 0),
////          angle(-angleBottom, 0, 0)
////        )
////      );
////
////      context.assemblePiece(
////        TRANSFORMS[facing.ordinal()],
////        vec3(0, maxHeight - MARGIN, 16 - alignedLengthBottom),
////        aabb(16, MARGIN, alignedLengthBottom)
////          .move(0, 16 - MARGIN, marginAdjBottom),
////        cull(DOWN | NORTH | SOUTH),
////        scale(
////          pivot(16, maxHeight, 16),
////          scale(1, 1, midLengthBottom / alignedLengthBottom)
////        ),
////        translate(0, 0, -halfLength),
////        rotate(
////          pivot(16, maxHeight, 16),
////          angle(-angleBottom, 0, 0)
////        )
////      );
////
////      context.assemblePiece(
////        TRANSFORMS[facing.ordinal()],
////        vec3(0, maxHeight - MARGIN, 16 - alignedLengthTop),
////        aabb(16, MARGIN, alignedLengthTop)
////          .move(0, 16 - MARGIN, 16 - marginAdjTop - alignedLengthTop),
////        cull(DOWN | NORTH | SOUTH),
////        scale(
////          pivot(16, maxHeight, 16),
////          scale(1, 1, midLengthTop / alignedLengthTop)
////        ),
////        translate(0, 0, -marginAdjTop),
////        rotate(
////          pivot(16, maxHeight, 16),
////          angle(-angleBottom, 0, 0)
////        )
////      );
////
////      context.assemblePiece(
////        TRANSFORMS[facing.ordinal()],
////        vec3(0, 0, 16 - marginAdjTop),
////        aabb(16, 16, marginAdjTop)
////          .move(0, 0, 16 - marginAdjTop),
////        cull(DOWN | NORTH | SOUTH),
////        updateUV(
////          slope(Direction.DOWN,
////            (a, b) -> map(
////              16 - marginAdjTop,
////              16,
////              MARGIN,
////              0,
////              b
////            )
////          )
////        ),
////        translate(0, -16 + maxHeight, 0),
////        rotate(
////          pivot(16, maxHeight, 16),
////          angle(-angleBottom, 0, 0)
////        )
////      );
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