
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

  private static final int TOP_X = 8;
  private static final int TOP_Y = 16;
  private static final int TOP_Z = 8;

  private record ShapeKey(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    int rotation
  ) {
  }

  private static final ConcurrentHashMap<ShapeKey, OutlinedVoxelShape> CACHE =
    new ConcurrentHashMap<>();

  public static OutlinedVoxelShape createShape(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    int rotation
  ) {
    ShapeKey key = new ShapeKey(type, facing, rotation);

    return CACHE.computeIfAbsent(
      key,
      k -> createOutlinedShape(
        k.type(),
        k.facing(),
        k.rotation()
      )
    );
  }

  private static OutlinedVoxelShape createOutlinedShape(
    BASE_CopycatPyramidBlock.ShapeType shapeType,
    Direction facing,
    int rotation
  ) {
    VoxelShape collision = switch (shapeType) {
      case PYRAMID -> createPyramidCollision();
      case CORNER -> createCornerCollision();
      case OPPOSITE -> createOppositeCollision();
      case QUAD -> createQuadCollision();
      case TRIPLE -> createTripleCollision();
    };

    Vec3[] points = switch (shapeType) {
      case PYRAMID -> createPyramidPoints();
      case CORNER -> createCornerPoints();
      case OPPOSITE -> createOppositePoints();
      case QUAD -> createQuadPoints();
      case TRIPLE -> createTriplePoints();
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

  private static Vec3[] createPyramidPoints() {
    return new Vec3[]{
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),
      new Vec3(.5, 1, .5)
    };
  }

  private static VoxelShape createPyramidCollision() {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < TOP_Y / 2; i++) {
      double t = i / (double) (TOP_Y / 2);

      double minX = lerp(0, TOP_X, t);
      double maxX = lerp(16, TOP_X, t);
      double minZ = lerp(0, TOP_Z, t);
      double maxZ = lerp(16, TOP_Z, t);

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

  private static Vec3[] createCornerPoints() {
    return createPyramidPoints();
  }

  private static VoxelShape createCornerCollision() {
    return createPyramidCollision();
  }

  // OPPOSITE

  private static Vec3[] createOppositePoints() {
    return new Vec3[]{
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),
      new Vec3(0, 1, .5),
      new Vec3(1, 1, .5)
    };
  }

  private static VoxelShape createOppositeCollision() {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < TOP_Y; i++) {
      double t = i / (double) TOP_Y;

      double minX = lerp(0, 0, t);
      double maxX = lerp(16, 16, t);

      double minZ = lerp(0, 0, t);
      double maxZ = lerp(16, 16 * 2, t);

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

  private static Vec3[] createQuadPoints() {
    return createPyramidPoints();
  }

  private static VoxelShape createQuadCollision() {
    return createPyramidCollision();
  }

  // TRIPLE

  private static Vec3[] createTriplePoints() {
    return createPyramidPoints();
  }

  private static VoxelShape createTripleCollision() {
    return createPyramidCollision();
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
