package net.ziftech.copycats_extended.block;

import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class DoubleBlock_Entity extends MultiStateCopycatBlockEntity {

  public DoubleBlock_Entity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.DOUBLE_COPYCAT.get(), pos, state);
  }
}