package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.CCShapes;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation;

public final class CopycatAdvSlopeLayerGeometry {

  private CopycatAdvSlopeLayerGeometry() {
  }

  public static int wallAngle(Direction facing, Half half) {
    return switch (facing) {
      case NORTH -> half == Half.TOP ? 270 : 90;
      case SOUTH -> half == Half.TOP ? 90 : 270;
      case WEST -> half == Half.TOP ? 90 : 270;
      case EAST -> half == Half.TOP ? 270 : 90;
      default -> throw new IllegalArgumentException(
        "Wall slope requires horizontal facing: " + facing
      );
    };
  }

  public static boolean rotateAroundZ(Direction facing) {
    return facing.getAxis() == Direction.Axis.X;
  }

  public static VoxelShape createWallShape(
    Direction facing,
    Half half,
    int layers,
    WallOrientation orientation
  ) {
    CCShapes.MutableShape shape = CCShapes.SLOPE_LAYER
      .get(facing)
      .get(half)
      .get(layers)
      .copy();

    int angle = wallAngle(facing, half);

    if (rotateAroundZ(facing)) {
      shape.rotateZ(angle);
    } else {
      shape.rotateX(angle);
    }

    if (orientation.isSideways()) {
      int sidewaysAngle = wallSidewaysAngle(facing);

      if (wallSidewaysRotateAroundZ(facing)) {
        shape.rotateZ(sidewaysAngle);
      } else {
        shape.rotateX(sidewaysAngle);
      }
    }

    return shape.toShape();
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
}