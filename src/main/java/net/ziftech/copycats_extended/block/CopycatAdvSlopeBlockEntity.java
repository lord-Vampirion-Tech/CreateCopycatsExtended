
package net.ziftech.copycats_extended.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;

import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class CopycatAdvSlopeBlockEntity extends CCCopycatBlockEntity {

  public CopycatAdvSlopeBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.COPYCAT_ADV_SLOPE.get(), pos, state);
  }
}
