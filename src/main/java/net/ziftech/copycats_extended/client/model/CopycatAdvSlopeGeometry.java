
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.CCShapes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CopycatAdvSlopeGeometry {

  private CopycatAdvSlopeGeometry() {
  }

  /**
   * Возвращает угол поворота скоса для установки на боковую грань.
   * HALF определяет, с какой стороны находится наклон.
   */
  private static int wallAngle(Direction facing, Half half) {
    return switch (facing) {
      case NORTH -> half == Half.TOP ? 270 : 90;
      case SOUTH -> half == Half.TOP ? 90 : 270;
      case WEST  -> half == Half.TOP ? 90 : 270;
      case EAST  -> half == Half.TOP ? 270 : 90;
      default -> throw new IllegalArgumentException(
        "Wall slope requires horizontal facing: " + facing
      );
    };
  }

  /**
   * Поворот вокруг Z нужен для стен, нормаль которых направлена по X.
   */
  private static boolean rotateAroundZ(Direction facing) {
    return facing.getAxis() == Direction.Axis.X;
  }

  /**
   * Создаёт геометрию скоса, установленного на боковую грань блока.
   */
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

    int angle = wallAngle(facing, half);

    if (rotateAroundZ(facing)) {
      shape.rotateZ(angle);
    } else {
      shape.rotateX(angle);
    }

    return shape.toShape();
  }
}
