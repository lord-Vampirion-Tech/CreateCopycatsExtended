package net.ziftech.copycats_extended.block.base;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class ModBlockPlacement {

  public static BlockState placeOnClickedFace(
    BlockPlaceContext context,
    BlockState state,
    DirectionProperty facingProperty
  ) {
    Direction facing = context.getClickedFace();

    return state.setValue(facingProperty, facing);
  }
}
