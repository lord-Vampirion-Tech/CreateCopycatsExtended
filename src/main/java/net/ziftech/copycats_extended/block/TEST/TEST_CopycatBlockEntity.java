package net.ziftech.copycats_extended.block.TEST;


import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class TEST_CopycatBlockEntity extends CCCopycatBlockEntity {

  public TEST_CopycatBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.TEST_COPYCAT_BLOCK.get(), pos, state);
  }
}
