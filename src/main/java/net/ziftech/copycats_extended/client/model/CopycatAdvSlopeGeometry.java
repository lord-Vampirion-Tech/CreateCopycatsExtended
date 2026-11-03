
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.CCShapes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CopycatAdvSlopeGeometry {

  public static int wallAngle(Direction facing, Half half) {
    return switch (facing) {
      case NORTH, EAST -> half == Half.TOP ? 270 : 90;
      case SOUTH, WEST -> half == Half.TOP ? 90 : 270;
      default -> throw new IllegalArgumentException(
        "Wall slope requires horizontal facing: " + facing
      );
    };
  }

  public static boolean rotateAroundZ(Direction facing) {
    return facing.getAxis() == Direction.Axis.X;
  }

  public static int wallSidewaysAngle(Direction facing) {
    return switch (facing) {
      case NORTH, WEST -> 270;
      case SOUTH, EAST -> 90;
      default -> throw new IllegalArgumentException(
        "Wall slope requires horizontal facing: " + facing
      );
    };
  }

  public static boolean wallSidewaysRotateAroundZ(Direction facing) {
    return facing.getAxis() == Direction.Axis.Z;
  }

  public static VoxelShape createWallShape(
    Direction facing,
    Half half
  ) {
    if (facing.getAxis() == Direction.Axis.Y) {
      throw new IllegalArgumentException(
        "Wall slope requires horizontal facing: " + facing
      );
    }

    CCShapes.MutableShape shape = CCShapes.SLOPE
      .get(facing)
      .get(half)
      .copy();

    // Первый поворот: устанавливаем скос на стену.
    int angle = wallAngle(facing, half);

    if (rotateAroundZ(facing)) {
      shape.rotateZ(angle);
    } else {
      shape.rotateX(angle);
    }

    // Второй поворот: разворачиваем скос вдоль стены.
    int sidewaysAngle = wallSidewaysAngle(facing);

    if (wallSidewaysRotateAroundZ(facing)) {
      shape.rotateZ(sidewaysAngle);
    } else {
      shape.rotateX(sidewaysAngle);
    }

    return shape.toShape();
  }
}
