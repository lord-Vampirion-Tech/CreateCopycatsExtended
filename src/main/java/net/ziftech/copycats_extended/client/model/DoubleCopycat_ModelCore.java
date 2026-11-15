package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import net.ziftech.copycats_extended.block.TEST.DoubleCopycatBlock;
import net.ziftech.copycats_extended.registry.ModBlocks;

import java.util.List;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;

public class DoubleCopycat_ModelCore extends CopycatModelCore {

  @Override
  public void registerModels(List<ModelEntry> entries) {
    registerForMultiState(
      entries,
      ModBlocks.DOUBLE_COPYCAT.get(),
      false
    );
  }

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    Direction.Axis axis = state.getValue(DoubleCopycatBlock.AXIS);

    AssemblyTransform transform = switch (axis) {
      case Y -> AssemblyTransform.IDENTITY;
      case X -> t -> t.rotateZ(90);
      case Z -> t -> t.rotateX(270);
    };

    switch (key) {

      case "two" -> context.assemblePiece(
        transform,
        vec3(0, 0, 0),
        aabb(16, 8, 16),
        cull(0)
      );

      case "one" -> context.assemblePiece(
        transform,
        vec3(0, 8, 0),
        aabb(16, 8, 16).move(0, 8, 0),
        cull(0)
      );

    }
  }
}