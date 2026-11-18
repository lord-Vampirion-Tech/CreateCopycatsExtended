
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;

import java.util.concurrent.ConcurrentHashMap;

public class CopycatPyramid_Geometry {

  private record ShapeKey(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    int rotation,
    int x,
    int y,
    int z
  ) {}

  private static final ConcurrentHashMap<ShapeKey, OutlinedVoxelShape> CACHE =
    new ConcurrentHashMap<>();

  public static OutlinedVoxelShape createShape(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    int rotation,
    int x,
    int y,
    int z
  ) {
    ShapeKey key = new ShapeKey(type, facing, rotation, x, y, z);

    return CACHE.computeIfAbsent(
      key,
      k -> createOutlinedShape(
        k.type(),
        k.facing(),
        k.rotation(),
        k.x(),
        k.y(),
        k.z()
      )
    );
  }

  private static OutlinedVoxelShape createOutlinedShape(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    int rotation,
    int x,
    int y,
    int z
  ) {
    VoxelShape collision = switch (type) {
      case PYRAMID -> createPyramidCollision(x, y, z);
      case CORNER -> createCornerCollision(x, y, z);
      case OPPOSITE -> createOppositeCollision(x, y, z);
      case QUAD -> createQuadCollision(x, y, z);
      case TRIPLE -> createTripleCollision(x, y, z);
    };

    Vec3[] points = switch (type) {
      case PYRAMID -> createPyramidPoints(x, y, z);
      case CORNER -> createCornerPoints(x, y, z);
      case OPPOSITE -> createOppositePoints(x, y, z);
      case QUAD -> createQuadPoints(x, y, z);
      case TRIPLE -> createTriplePoints(x, y, z);
    };

    int[][] connections = {
      {0, 1}, {1, 2}, {2, 3}, {3, 0},
      {0, 4}, {1, 4}, {2, 4}, {3, 4}
    };

    for (int i = 0; i < points.length; i++) {
      points[i] = rotatePoint(points[i], facing);
    }

    collision = rotateShape(collision, facing);

    return ModelUtils.createOutlinedShape(
      collision,
      points,
      connections
    );
  }

  // PYRAMID

  private static Vec3[] createPyramidPoints(int x, int y, int z) {
    return new Vec3[] {
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),
      new Vec3(x / 8.0, y / 8.0, z / 8.0)
    };
  }

  private static VoxelShape createPyramidCollision(int x, int y, int z) {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < y; i++) {
      double t = i / (double) y;

      double minX = lerp(0, x * 2, t);
      double maxX = lerp(16, x * 2, t);
      double minZ = lerp(0, z * 2, t);
      double maxZ = lerp(16, z * 2, t);

      VoxelShape section = Shapes.box(
        minX / 16.0,
        i * 2 / 16.0,
        minZ / 16.0,
        maxX / 16.0,
        (i + 1) * 2 / 16.0,
        maxZ / 16.0
      );

      shape = Shapes.or(shape, section);
    }

    return shape.optimize();
  }

  // CORNER

  private static Vec3[] createCornerPoints(int x, int y, int z) {
    return createPyramidPoints(x, y, z);
  }

  private static VoxelShape createCornerCollision(int x, int y, int z) {
    return createPyramidCollision(x, y, z);
  }

  // OPPOSITE

  private static Vec3[] createOppositePoints(int x, int y, int z) {
    return createPyramidPoints(x, y, z);
  }

  private static VoxelShape createOppositeCollision(int x, int y, int z) {
    return createPyramidCollision(x, y, z);
  }

  // QUAD

  private static Vec3[] createQuadPoints(int x, int y, int z) {
    return createPyramidPoints(x, y, z);
  }

  private static VoxelShape createQuadCollision(int x, int y, int z) {
    return createPyramidCollision(x, y, z);
  }

  // TRIPLE

  private static Vec3[] createTriplePoints(int x, int y, int z) {
    return createPyramidPoints(x, y, z);
  }

  private static VoxelShape createTripleCollision(int x, int y, int z) {
    return createPyramidCollision(x, y, z);
  }

  // TRANSFORMS

  private static double lerp(double start, double end, double t) {
    return start + (end - start) * t;
  }

  private static Vec3 rotatePoint(Vec3 point, Direction facing) {
    double x = point.x;
    double y = point.y;
    double z = point.z;

    return switch (facing) {
      case UP -> new Vec3(x, y, z);
      case DOWN -> new Vec3(x, 1 - y, 1 - z);
      case NORTH -> new Vec3(x, 1 - z, y);
      case SOUTH -> new Vec3(x, z, 1 - y);
      case WEST -> new Vec3(1 - y, x, z);
      case EAST -> new Vec3(y, 1 - x, z);
    };
  }

  private static VoxelShape rotateShape(VoxelShape shape, Direction facing) {
    if (facing == Direction.UP)
      return shape;

    VoxelShape result = Shapes.empty();

    for (AABB box : shape.toAabbs()) {
      Vec3[] corners = {
        new Vec3(box.minX, box.minY, box.minZ),
        new Vec3(box.minX, box.minY, box.maxZ),
        new Vec3(box.minX, box.maxY, box.minZ),
        new Vec3(box.minX, box.maxY, box.maxZ),
        new Vec3(box.maxX, box.minY, box.minZ),
        new Vec3(box.maxX, box.minY, box.maxZ),
        new Vec3(box.maxX, box.maxY, box.minZ),
        new Vec3(box.maxX, box.maxY, box.maxZ)
      };

      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double minZ = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      double maxZ = Double.NEGATIVE_INFINITY;

      for (Vec3 corner : corners) {
        Vec3 rotated = rotatePoint(corner, facing);

        minX = Math.min(minX, rotated.x);
        minY = Math.min(minY, rotated.y);
        minZ = Math.min(minZ, rotated.z);

        maxX = Math.max(maxX, rotated.x);
        maxY = Math.max(maxY, rotated.y);
        maxZ = Math.max(maxZ, rotated.z);
      }

      result = Shapes.or(
        result,
        Shapes.box(minX, minY, minZ, maxX, maxY, maxZ)
      );
    }

    return result.optimize();
  }
}
