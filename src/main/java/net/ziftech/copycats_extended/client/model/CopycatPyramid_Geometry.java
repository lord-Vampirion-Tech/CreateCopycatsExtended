
package net.ziftech.copycats_extended.client.model;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ConcurrentHashMap;

public class CopycatPyramid_Geometry {

  private record ShapeKey(
    Direction facing,
    int x,
    int y,
    int z
  ) {}

  private static final ConcurrentHashMap<ShapeKey, OutlinedVoxelShape> CACHE =
    new ConcurrentHashMap<>();

  private static OutlinedVoxelShape createOutlinedShape(
    Direction facing,
    int x,
    int y,
    int z
  ) {
    VoxelShape collision = createCollisionShape(facing, x, y, z);

    Vec3[] points = {
      rotatePoint(facing, new Vec3(0, 0, 0)),
      rotatePoint(facing, new Vec3(1, 0, 0)),
      rotatePoint(facing, new Vec3(1, 0, 1)),
      rotatePoint(facing, new Vec3(0, 0, 1)),
      rotatePoint(
        facing,
        new Vec3(x / 8.0, y / 8.0, z / 8.0)
      )
    };

    int[][] connections = {
      {0, 1}, {1, 2}, {2, 3}, {3, 0},
      {0, 4}, {1, 4}, {2, 4}, {3, 4}
    };

    return ModelUtils.createOutlinedShape(
      collision,
      points,
      connections
    );
  }

  private static Vec3 rotatePoint(
    Direction facing,
    Vec3 point
  ) {
    double x = point.x;
    double y = point.y;
    double z = point.z;

    return switch (facing) {
      case UP -> new Vec3(x, y, z);
      case DOWN -> new Vec3(x, 1 - y, 1 - z);
      case NORTH -> new Vec3(x, z, 1 - y);
      case SOUTH -> new Vec3(x, 1 - z, y);
      case WEST -> new Vec3(1 - y, x, z);
      case EAST -> new Vec3(y, 1 - x, z);
    };
  }

  public static OutlinedVoxelShape createShape(
    Direction facing,
    int topPointX,
    int topPointY,
    int topPointZ
  ) {
    ShapeKey key = new ShapeKey(
      facing,
      topPointX,
      topPointY,
      topPointZ
    );

    return (OutlinedVoxelShape) CACHE.computeIfAbsent(
      key,
      k -> createOutlinedShape(
        k.facing(),
        k.x(),
        k.y(),
        k.z()
      )
    );
  }

  private static VoxelShape createCollisionShape(
    Direction facing,
    int topPointX,
    int topPointY,
    int topPointZ
  ) {
    VoxelShape shape = Shapes.empty();

    double apexX = topPointX / 8.0;
    double apexZ = topPointZ / 8.0;
    double height = topPointY / 8.0;

    int subdivisions = topPointY + 1;

    for (int i = 0; i < subdivisions; i++) {
      double tMin = i / (double) subdivisions;
      double tMax = (i + 1.0) / subdivisions;

      double minX = apexX * tMin;
      double maxX = 1 - (1 - apexX) * tMin;

      double minZ = apexZ * tMin;
      double maxZ = 1 - (1 - apexZ) * tMin;

      double minY = height * tMin;
      double maxY = height * tMax;

      VoxelShape section = rotateBox(
        minX, minY, minZ,
        maxX, maxY, maxZ,
        facing
      );

      shape = Shapes.joinUnoptimized(
        shape,
        section,
        BooleanOp.OR
      );
    }

    return shape.optimize();
  }

  private static VoxelShape rotateBox(
    double minX,
    double minY,
    double minZ,
    double maxX,
    double maxY,
    double maxZ,
    Direction facing
  ) {
    return switch (facing) {
      case UP -> Shapes.box(
        minX, minY, minZ,
        maxX, maxY, maxZ
      );

      case DOWN -> Shapes.box(
        minX, 1 - maxY, 1 - maxZ,
        maxX, 1 - minY, 1 - minZ
      );

      case NORTH -> Shapes.box(
        minX, minZ, 1 - maxY,
        maxX, maxZ, 1 - minY
      );

      case SOUTH -> Shapes.box(
        minX, 1 - maxZ, minY,
        maxX, 1 - minZ, maxY
      );

      case WEST -> Shapes.box(
        1 - maxY, minX, minZ,
        1 - minY, maxX, maxZ
      );

      case EAST -> Shapes.box(
        minY, 1 - maxX, minZ,
        maxY, 1 - minX, maxZ
      );
    };
  }
}
