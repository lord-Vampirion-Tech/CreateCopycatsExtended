package net.ziftech.copycats_extended.client.model.TEST;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

import net.minecraft.world.level.block.state.BlockState;

public class TEST_CopycatBlockModelCore extends CopycatModelCore {

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    AssemblyTransform transform = t -> {
    };

    TEST_CopycatBlockGeometry.assemble(
      context,
      transform
    );
  }


  @Override
  public void prepareForRender() {
    super.prepareForRender();
    colorize = false;
  }
}