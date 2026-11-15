package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.ziftech.copycats_extended.registry.ModBlockEntities;

public class DoubleCopycatBlockEntity extends MultiStateCopycatBlockEntity {

  public DoubleCopycatBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.DOUBLE_COPYCAT.get(), pos, state);
  }
}