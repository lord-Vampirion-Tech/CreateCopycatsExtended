package net.ziftech.copycats_extended.block;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class CopycatAdvSlopeLayerBlockEntity extends CCCopycatBlockEntity {

  public CopycatAdvSlopeLayerBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.COPYCAT_ADV_SLOPE_LAYER.get(), pos, state);
  }
}