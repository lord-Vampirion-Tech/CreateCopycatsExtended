//package net.ziftech.copycats_extended.client.model.WIP.TEST.model;
//
//import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableAABB;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableVec3;
//import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;
//
//import net.minecraft.core.Direction;
//import net.minecraft.core.Direction.Axis;
//import net.minecraft.world.level.block.state.BlockState;
//import net.ziftech.copycats_extended.block.WIP.TEST_CopycatBlock;
//
//import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
//import static net.ziftech.copycats_extended.client.model.WIP.TEST.CustomQuadTransform.*;
//
//public class TEST_CopycatBlock_ModelCore extends CopycatModelCore {
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
//
//    Scratch scratch = SCRATCH.get();
//    scratch.reset(true);
//
//    context.assemblePiece(
//      TRANSFORMS[facing.ordinal()],
//      scratch.offset,
//      scratch.selection,
//      scratch.cull,
//      NORMAL_TRANSFORMS
//    );
//  }
//}