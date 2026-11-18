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
//import static net.ziftech.copycats_extended.client.model.WIP.TEST.CustomQuadTransform.symmetricSlope;
//
//public class TEST_CopycatBlock_ModelCore_V4 extends CopycatModelCore {
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
//    if (enhanced) {
//
//      QuadScale Scale = scale(
//        pivot(0, 0, 8),
//        scale(1, coefficientALL, 1)
//      );
//
//      QuadRotate Rotate = rotate(
//        pivot(0, 0, 8),
//        angle(0, 0, -angleALL)
//      );
//
//      final double offset = 1 - 0.001;
//
//      context.assemblePiece(
//        TRANSFORMS[facing.ordinal()],
//        vec3(0, 0, 0),
//        aabb(16, 16, 16),
//        cull(0),
//        updateUV(
//          symmetricSlope(Axis.Z, (a, b) -> Math.clamp(16 - b, 0, 16), TOP_Z / 16),
//          symmetricSlope(Axis.X, (a, b) -> Math.clamp(16 - a, 0, 16), TOP_X / 16)
//        ),
//        scale(
//          pivot(8, 8, 8),
//          scale(offset, offset, offset)
//        )
//      );
//
//      if (true) {
//
//        // 1
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, 16 - marginAdjT, 0),
//          aabb(0, marginAdjT, 16).move(0, 16 - marginAdjT, 0),
//          cull(0),
//          updateUV(
//            slope(Direction.SOUTH, (a, b) -> map(0, marginAdjT, 0, margin, 16 - b))
//          ),
//          translate(0, -(16 - marginAdjT) + 16 * coefficient_L - marginAdjT, 0),
//          rotate(
//            pivot(0, 0, 0),
//            angle(-angleBL + 90, 0, 0)
//          ),
//          Scale,
//          Rotate
//        );
//
//
//        // 2
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, (marginAdjBL + ((L - marginAdjBL - marginAdjT) / 2) / coefficient_L), 0),
//          aabb(0, ((L - marginAdjBL - marginAdjT) / 2) * coefficient_L, margin).move(0, 16 - marginAdjT - ((L - marginAdjBL - marginAdjT) / 2) * coefficient_L, 0),
//          cull(0),
//          rotate(
//            pivot(0, 0, 0),
//            angle(-angleBL + 90, 0, 0)
//          ),
//          Scale,
//          Rotate
//        );
//
//        // 3
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, marginAdjBL, 0),
//          aabb(0, ((L - marginAdjBL - marginAdjT) / 2) / coefficient_L, margin).move(0, marginAdjBL, 0),
//          cull(0),
//          rotate(
//            pivot(0, 0, 0),
//            angle(-angleBL + 90, 0, 0)
//          ),
//          Scale,
//          Rotate
//        );
//
//        //4
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, 0, 0),
//          aabb(0, marginAdjBL, 16),
//          cull(0),
//          updateUV(
//            slope(Direction.SOUTH, (a, b) -> map(0, marginAdjBL, 0, margin, b))
//          ),
//          rotate(
//            pivot(0, 0, 0),
//            angle(-angleBL + 90, 0, 0)
//          ),
//          Scale,
//          Rotate
//        );
//      }
//
//
//      if (true) {
//        // 1
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, 16 - marginAdjT, 0),
//          aabb(0, marginAdjT, 16).move(0, 16 - marginAdjT, 0),
//          cull(0),
//          updateUV(
//            slope(Direction.NORTH, (a, b) -> map(0, marginAdjT, 0, margin, 16 - b))
//          ),
//          translate(0, -(16 - marginAdjT) + 16 * coefficient_R - marginAdjT, 0),
//          rotate(
//            pivot(0, 0, 16),
//            angle(angleBR - 90, 0, 0)
//          ),
//          Scale,
//          Rotate
//        );
//
//        // 2
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, marginAdjBR + (R - marginAdjBR - marginAdjT) / 2, 16 - margin),
//          aabb(0, (R - marginAdjBR - marginAdjT) / 2, margin).move(0, 16 - marginAdjT - (R - marginAdjBR - marginAdjT) / 2, 16 - margin),
//          cull(0),
//          rotate(
//            pivot(0, 0, 16),
//            angle(angleBR - 90, 0, 0)
//          ),
//          Scale,
//          Rotate
//        );
//
//        // 3
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, marginAdjBR, 16 - margin),
//          aabb(0, (R - marginAdjBR - marginAdjT) / 2, margin).move(0, marginAdjBR, 16 - margin),
//          cull(0),
//          rotate(
//            pivot(0, 0, 16),
//            angle(angleBR - 90, 0, 0)
//          ),
//          Scale,
//          Rotate
//        );
//
//        // 4
//        context.assemblePiece(
//          TRANSFORMS[facing.ordinal()],
//          vec3(0, 0, 0),
//          aabb(0, marginAdjBR, 16),
//          cull(0),
//          updateUV(
//            slope(Direction.NORTH, (a, b) -> map(0, marginAdjBR, 0, margin, b))
//          ),
//          rotate(
//            pivot(0, 0, 16),
//            angle(angleBR - 90, 0, 0)
//          ),
//          Scale,
//          Rotate
//        );
//      }
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