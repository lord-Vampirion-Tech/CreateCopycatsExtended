package net.ziftech.copycats_extended.client.TEST.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;

public class TEST_CopycatBlock_ModelCore extends CopycatModelCore {

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    Direction facing = state.getValue(TEST_CopycatBlock.FACING);

    AssemblyTransform transform = switch (facing) {
      case UP -> AssemblyTransform.IDENTITY;
      case DOWN -> t -> t.rotateX(180);

      case NORTH -> t -> t.rotateX(90);
      case SOUTH -> t -> t.rotateX(270);

      case EAST -> t -> t.rotateZ(90);
      case WEST -> t -> t.rotateZ(270);
    };

//    context.assemblePiece(
//      transform,
//      vec3(0, 0, 0),
//      aabb(16, 16, 16),
//      cull(UP | NORTH | SOUTH | EAST | WEST)
//    );

    context.assemblePiece(
      transform,
      vec3(0, 0, 0),
      aabb(16, 16, 16),
      cull(0),

      // ТЕСТЫ
      // ================================

//      uvScale(Direction.UP, 8,0,1,1),
      updateUV(
        slope(
          Direction.UP,
          (a, b) -> b
        ),
        slope(
          Direction.SOUTH,
          (a, b) -> 8
        )
      ),
      uvTranslate(Direction.UP, 0,1)

    );

    // ================================

  }
}