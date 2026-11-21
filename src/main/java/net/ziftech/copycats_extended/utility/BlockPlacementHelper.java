package net.ziftech.copycats_extended.utility;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.Vec3;

public class BlockPlacementHelper {

  public record FaceCoordinates(double u, double v) {
  }

  public static FaceCoordinates getFaceCoordinates(BlockPlaceContext context) {
    Vec3 hit = context.getClickLocation()
      .subtract(Vec3.atLowerCornerOf(context.getClickedPos()));

    double u;
    double v;

    switch (context.getClickedFace()) {
      case UP, DOWN -> {
        u = hit.x;
        v = hit.z;
      }
      case NORTH -> {
        u = 1 - hit.x;
        v = hit.y;
      }
      case SOUTH -> {
        u = hit.x;
        v = hit.y;
      }
      case WEST -> {
        u = hit.z;
        v = hit.y;
      }
      case EAST -> {
        u = 1 - hit.z;
        v = hit.y;
      }
      default -> {
        u = 0.5;
        v = 0.5;
      }
    }

    return new FaceCoordinates(u, v);
  }

  // Углы: SOUTH / WEST
  public static Direction getCornerRotation(BlockPlaceContext context) {
    FaceCoordinates point = getFaceCoordinates(context);

    boolean right = point.u() >= 0.5;
    boolean bottom = point.v() >= 0.5;

    return right == bottom ? Direction.SOUTH : Direction.WEST;
  }

  // Углы: SOUTH / WEST / NORTH / EAST
  public static Direction getCornerRotationClockwise(BlockPlaceContext context) {
    FaceCoordinates point = getFaceCoordinates(context);

    boolean right = point.u() >= 0.5;
    boolean bottom = point.v() >= 0.5;

    if (!bottom)
      return right ? Direction.WEST : Direction.SOUTH;

    return right ? Direction.NORTH : Direction.EAST;
  }

  // Рёбра: SOUTH / WEST
  public static Direction getEdgeRotation(BlockPlaceContext context) {
    FaceCoordinates point = getFaceCoordinates(context);

    double dx = point.u() - 0.5;
    double dy = point.v() - 0.5;

    return Math.abs(dy) > Math.abs(dx)
      ? Direction.WEST
      : Direction.SOUTH;
  }

  // Рёбра: SOUTH / WEST / NORTH / EAST
  public static Direction getEdgeRotationClockwise(BlockPlaceContext context) {
    FaceCoordinates point = getFaceCoordinates(context);

    double dx = point.u() - 0.5;
    double dy = point.v() - 0.5;

    if (Math.abs(dy) > Math.abs(dx))
      return dy < 0 ? Direction.SOUTH : Direction.NORTH;

    return dx < 0 ? Direction.EAST : Direction.WEST;
  }
}