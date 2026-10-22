package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableVec3;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.block.CopycatPrismCornerBlock;

public class CopycatPrismCornerModelCore extends CopycatModelCore {

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    Direction facing = state.getValue(CopycatPrismCornerBlock.FACING);
    int rotation = (int) facing.toYRot();

    boolean wallMounted =
      state.getValue(CopycatPrismCornerBlock.WALL_MOUNTED);

    AssemblyTransform transform;

    if (wallMounted) {
      transform = t -> t.rotateZ(-90).rotateY(rotation);
    } else {
      transform = t -> t.rotateY(rotation);
    }

    context.assemblePiece(
      transform,
      CopycatRenderContext.vec3(0, 0, 0),
      CopycatRenderContext.aabb(16, 16, 16),
      CopycatRenderContext.cull(0),
      (quad, sprite) -> {
        for (var vertex : quad.vertices) {
          MutableVec3 pos = vertex.xyz;

          boolean top = pos.y >= 0.5;
          boolean east = pos.x >= 0.5;
          boolean south = pos.z >= 0.5;

          if (top) {
            // A: верхняя вершина (16, 16, 16).
            pos.set(1, 1, 1);
          } else if (east && south) {
            // B: (16, 0, 16).
            pos.set(1, 0, 1);
          } else if (east) {
            // C: (16, 0, 0).
            pos.set(1, 0, 0);
          } else if (south) {
            // D: (0, 0, 16).
            pos.set(0, 0, 1);
          } else {
            // Нижний угол (0, 0, 0) совмещается с C.
            pos.set(1, 0, 0);
          }
        }

        // Верхняя грань и одна боковая грань исходного куба
        // превращаются в вырожденные поверхности.
        return quad.computeFaceNormal().lengthSqr() > 1.0e-8;
      }
    );
  }
}