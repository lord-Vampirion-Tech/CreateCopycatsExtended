package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class TEST_CopycatBlock_Entity extends CCCopycatBlockEntity {

  public TEST_CopycatBlock_Entity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.TEST_COPYCAT_BLOCK.get(), pos, state);
  }
}