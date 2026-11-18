package net.ziftech.copycats_extended.client.model;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoubleBlock_Geometry {

  public static VoxelShape getPartShape(
    Direction.Axis axis,
    int slice,
    boolean top
  ) {
    double split = slice / 8.0;

    return switch (axis) {
      case X -> top
        ? Shapes.box(split, 0, 0, 1, 1, 1)
        : Shapes.box(0, 0, 0, split, 1, 1);

      case Y -> top
        ? Shapes.box(0, split, 0, 1, 1, 1)
        : Shapes.box(0, 0, 0, 1, split, 1);

      case Z -> top
        ? Shapes.box(0, 0, split, 1, 1, 1)
        : Shapes.box(0, 0, 0, 1, 1, split);
    };
  }
}