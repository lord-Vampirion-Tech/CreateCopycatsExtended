package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import net.ziftech.copycats_extended.block.DoubleBlock;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;
import net.ziftech.copycats_extended.registry.ModBlocks;

import java.util.List;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;

public class DoubleBlock_ModelCore extends CopycatModelCore {

  @Override
  public void registerModels(List<ModelEntry> entries) {
    registerForMultiState(
      entries,
      ModBlocks.DOUBLE_BLOCK.get(),
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
    Direction.Axis axis = state.getValue(DoubleBlock.AXIS);

    AssemblyTransform transform = switch (axis) {
      case Y -> AssemblyTransform.IDENTITY;
      case X -> t -> t.rotateZ(90);
      case Z -> t -> t.rotateX(270);
    };



    double split = state.getValue(DoubleBlock.SLICE_Y);
    boolean connect = state.getValue(DoubleBlock.CONNECT_HALF);

    switch (key) {
      case DoubleBlock.TWO -> {
        if (connect) {
          context.assemblePiece(
            transform,
            vec3(0, split * 2, 0),
            aabb(16, 16 - split * 2, 16).move(0, split * 2, 0),
            cull(DOWN)
          );
        } else {
          context.assemblePiece(
            transform,
            vec3(0, split * 2, 0),
            aabb(16, 8 - split, 16),
            cull(UP)
          );
//
          context.assemblePiece(
            transform,
            vec3(0, 8 + split, 0),
            aabb(16, 8 - split, 16).move(0, 8 + split, 0),
            cull(DOWN)
          );
        }
      }

      case DoubleBlock.ONE -> {
        if (connect) {
          context.assemblePiece(
            transform,
            vec3(0, 0, 0),
            aabb(16, split * 2, 16),
            cull(UP)
          );
        } else {

          context.assemblePiece(
            transform,
            vec3(0, 0, 0),
            aabb(16, split, 16),
            cull(UP)
          );
          context.assemblePiece(
            transform,
            vec3(0, split, 0),
            aabb(16, split, 16).move(0, 16 - split, 0),
            cull(DOWN)
          );
        }
      }
    }
  }
}