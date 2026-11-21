
package net.ziftech.copycats_extended.utility;

import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BlockTransformHelper {

  public static BlockState transform(
    BlockState state,
    StructureTransform transform,
    DirectionProperty facingProperty
  ) {
    if (transform.rotationAxis == null)
      return state;

    Direction facing = transform.rotateFacing(state.getValue(facingProperty));

    return state.setValue(facingProperty, facing);
  }

  public static BlockState transform(
    BlockState state,
    StructureTransform transform,
    DirectionProperty facingProperty,
    DirectionProperty rotationProperty
  ) {
    if (transform.rotationAxis == null)
      return state;

    Direction oldFacing = state.getValue(facingProperty);
    Direction oldRotation = state.getValue(rotationProperty);

    int steps = -transform.angle / 90;

    Direction newFacing = transform.rotateFacing(oldFacing);

    Direction newRotation = switch (transform.rotationAxis) {
      case Z -> oldFacing.getAxis() == Direction.Axis.Z
        ? rotate_Y_steps(oldRotation, oldFacing == Direction.SOUTH, steps)
        : rotate_Z(oldRotation, newFacing, steps);

      case X -> oldFacing.getAxis() == Direction.Axis.X
        ? rotate_Y_steps(oldRotation, oldFacing == Direction.EAST, steps)
        : rotate_X(oldRotation, newFacing, steps);

      case Y -> oldFacing.getAxis() == Direction.Axis.Y
        ? rotate_Y_axis(oldRotation, steps)
        : oldRotation;
    };

    return state
      .setValue(facingProperty, newFacing)
      .setValue(rotationProperty, newRotation);
  }

  private static Direction rotate_Y_steps(
    Direction rotation,
    boolean facingPositive,
    int steps
  ) {
    for (int i = 0; i < Math.abs(steps); i++) {
      rotation = rotate_Y(rotation, facingPositive, steps > 0);
    }

    return rotation;
  }

  private static Direction rotate_Y_axis(
    Direction rotation,
    int steps
  ) {
    for (int i = 0; i < Math.abs(steps); i++) {
      rotation = steps > 0
        ? rotation.getClockWise()
        : rotation.getCounterClockWise();
    }

    return rotation;
  }

  private static Direction rotate_Y(
    Direction rotation,
    boolean facingPositive,
    boolean clockwise
  ) {
    if (rotation.getAxis() == Direction.Axis.Y)
      return rotation;

    boolean cw = clockwise == facingPositive;

    return cw
      ? rotation.getCounterClockWise()
      : rotation.getClockWise();
  }

  private static Direction reflect(
    Direction rotation,
    Direction.Axis axis
  ) {
    return rotation.getAxis() == axis
      ? rotation
      : rotation.getOpposite();
  }

  private static Direction rotate_Z(
    Direction rotation,
    Direction newFacing,
    int steps
  ) {
    if (steps == 0)
      return rotation;

    boolean clockwise = steps > 0;

    return Math.abs(steps) % 2 == 0
      ? rotate_Z_180(rotation, newFacing)
      : rotate_Z_90(rotation, newFacing, clockwise);
  }

  private static Direction rotate_Z_90(
    Direction rotation,
    Direction newFacing,
    boolean clockwise
  ) {
    if (clockwise) {
      return switch (newFacing) {
        case UP -> reflect(rotation, Direction.Axis.Z);
        case EAST -> reflect(rotation, Direction.Axis.X);
        case DOWN, WEST -> rotation.getCounterClockWise();
        default -> rotation;
      };
    }

    return switch (newFacing) {
      case WEST -> reflect(rotation, Direction.Axis.Z);
      case EAST, DOWN -> rotation.getClockWise();
      case UP -> reflect(rotation, Direction.Axis.X);
      default -> rotation;
    };
  }

  private static Direction rotate_Z_180(
    Direction rotation,
    Direction newFacing
  ) {
    return switch (newFacing.getAxis()) {
      case Y -> rotation.getAxis() == Direction.Axis.Z
        ? rotation.getClockWise()
        : rotation.getCounterClockWise();

      case X -> rotation.getOpposite();

      default -> rotation;
    };
  }

  private static Direction rotate_X(
    Direction rotation,
    Direction newFacing,
    int steps
  ) {
    if (steps == 0)
      return rotation;

    boolean clockwise = steps > 0;

    return Math.abs(steps) % 2 == 0
      ? rotate_X_180(rotation, newFacing)
      : rotate_X_90(rotation, newFacing, clockwise);
  }

  private static Direction rotate_X_90(
    Direction rotation,
    Direction newFacing,
    boolean clockwise
  ) {
    if (clockwise) {
      return switch (newFacing) {
        case NORTH -> rotateDiagonal(rotation, true);
        case UP -> rotateDiagonal(rotation, false);
        case DOWN -> rotation.getOpposite();
        default -> rotation;
      };
    }

    return switch (newFacing) {
      case SOUTH -> rotateDiagonal(rotation, false);
      case NORTH -> rotation.getOpposite();
      case UP -> rotateDiagonal(rotation, true);
      default -> rotation;
    };
  }

  private static Direction rotate_X_180(
    Direction rotation,
    Direction newFacing
  ) {
    return switch (newFacing.getAxis()) {
      case Z -> rotation.getOpposite();

      case Y -> rotation.getAxis() == Direction.Axis.Z
        ? rotation.getCounterClockWise()
        : rotation.getClockWise();

      default -> rotation;
    };
  }

  private static Direction rotateDiagonal(
    Direction rotation,
    boolean clockwise
  ) {
    if (rotation.getAxis() == Direction.Axis.Z) {
      return clockwise
        ? rotation.getClockWise()
        : rotation.getCounterClockWise();
    }

    return clockwise
      ? rotation.getCounterClockWise()
      : rotation.getClockWise();
  }
}
