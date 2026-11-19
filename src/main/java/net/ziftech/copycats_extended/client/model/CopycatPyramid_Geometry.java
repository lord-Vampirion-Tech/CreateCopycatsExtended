
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CopycatPyramid_Geometry {

  private record ShapeKey(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    int rotation,
    int x,
    int y,
    int z
  ) {
  }

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
    BASE_CopycatPyramidBlock.ShapeType shapeType,
    Direction facing,
    int rotation,
    int x,
    int y,
    int z
  ) {
    VoxelShape collision = switch (shapeType) {
      case PYRAMID -> createPyramidCollision(x, y, z, facing);
      case CORNER -> createCornerCollision(x, y, z, facing);
      case OPPOSITE -> createOppositeCollision(x, y, z, facing);
      case QUAD -> createQuadCollision(x, y, z, facing);
      case TRIPLE -> createTripleCollision(x, y, z, facing);
    };

    Vec3[] points = switch (shapeType) {
      case PYRAMID -> createPyramidPoints(x, y, z, facing);
      case CORNER -> createCornerPoints(x, y, z, facing);
      case OPPOSITE -> createOppositePoints(x, y, z, facing);
      case QUAD -> createQuadPoints(x, y, z, facing);
      case TRIPLE -> createTriplePoints(x, y, z, facing);
    };

    int[][] connections = switch (shapeType) {
      case PYRAMID -> new int[][]{
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 4}, {2, 4}, {3, 4}
      };

      case CORNER -> new int[][]{
        // Определим после согласования вершин
      };

      case OPPOSITE -> new int[][]{
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {3, 4}, {1, 5}, {2, 5},
        {4, 5}
      };

      case QUAD -> new int[][]{
        // Определим после согласования вершин
      };

      case TRIPLE -> new int[][]{
        // Определим после согласования вершин
      };
    };

    for (int i = 0; i < points.length; i++) {
      if (shapeType == BASE_CopycatPyramidBlock.ShapeType.OPPOSITE && rotation == 1) {
        points[i] = rotateY90(points[i]);
      }

      points[i] = rotatePoint(points[i], facing);
    }

    if (shapeType == BASE_CopycatPyramidBlock.ShapeType.OPPOSITE && rotation == 1) {
      collision = rotateShapeY90(collision);
    }

    collision = rotateShape(collision, facing);

    return ModelUtils.createCollisionOutline(collision);
//    return ModelUtils.createOutlinedShape(
//      collision,
//      points,
//      connections
//    );
  }

  private static Vec3 rotateY90(Vec3 point) {
    return new Vec3(1 - point.z, point.y, point.x);
  }

  private static VoxelShape rotateShapeY90(VoxelShape shape) {
    VoxelShape result = Shapes.empty();

    for (AABB box : shape.toAabbs()) {
      double minX = 1 - box.maxZ;
      double maxX = 1 - box.minZ;

      double minZ = box.minX;
      double maxZ = box.maxX;

      result = Shapes.or(
        result,
        Shapes.box(
          minX, box.minY, minZ,
          maxX, box.maxY, maxZ
        )
      );
    }

    return result.optimize();
  }

  // PYRAMID

  private static Vec3[] createPyramidPoints(int x, int y, int z, Direction facing) {
    return new Vec3[]{
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),
      new Vec3(x / 8.0, y / 8.0, z / 8.0)
    };
  }

  private static VoxelShape createPyramidCollision(int x, int y, int z, Direction facing) {
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

  private static Vec3[] createCornerPoints(int x, int y, int z, Direction facing) {
    return createPyramidPoints(x, y, z, facing);
  }

  private static VoxelShape createCornerCollision(int x, int y, int z, Direction facing) {
    return createPyramidCollision(x, y, z, facing);
  }

  // OPPOSITE

  private static Vec3[] createOppositePoints(int x, int y, int z, Direction facing) {
    return new Vec3[]{
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),
      new Vec3(0, y / 8.0, z / 8.0),
      new Vec3(1, y / 8.0, z / 8.0)
    };
  }

  private static VoxelShape createOppositeCollision(int x, int y, int z, Direction facing) {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < y; i++) {
      double t = i / (double) y;

      double minX = lerp(0, 0, t);
      double maxX = lerp(16, 16, t);

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

  // QUAD

  private static Vec3[] createQuadPoints(int x, int y, int z, Direction facing) {
    return createPyramidPoints(x, y, z, facing);
  }

  private static VoxelShape createQuadCollision(int x, int y, int z, Direction facing) {
    return createPyramidCollision(x, y, z, facing);
  }

  // TRIPLE

  private static Vec3[] createTriplePoints(int x, int y, int z, Direction facing) {
    return createPyramidPoints(x, y, z, facing);
  }

  private static VoxelShape createTripleCollision(int x, int y, int z, Direction facing) {
    return createPyramidCollision(x, y, z, facing);
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
      case DOWN -> new Vec3(x, 1 - y, z);

      case NORTH -> new Vec3(1 - x, z, 1 - y);
      case SOUTH -> new Vec3(x, z, y);

      case WEST -> new Vec3(1 - y, z, x);
      case EAST -> new Vec3(y, z, 1 - x);
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
