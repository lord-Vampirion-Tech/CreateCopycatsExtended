
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

//  private static final int TOP_X = 8;
//  private static final int TOP_Y = 16;
//  private static final int TOP_Z = 8;

  private record ShapeKey(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    Direction rotation
  ) {
  }

  private static final ConcurrentHashMap<ShapeKey, OutlinedVoxelShape> CACHE =
    new ConcurrentHashMap<>();

  public static OutlinedVoxelShape createShape(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    Direction rotation
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
    Direction rotation
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
      points[i] = rotatePointsByRotation(points[i], shapeType, rotation);
      points[i] = rotatePoint(points[i], facing);
    }

    collision = rotateShapeByRotation(collision, shapeType, rotation);
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

  private static int getRotationTurns(Direction rotation) {
    return switch (rotation) {
      case SOUTH -> 0;
      case WEST -> 1;
      case NORTH -> 2;
      case EAST -> 3;
      default -> 0;
    };
  }

  private static VoxelShape rotateShapeByRotation(
    VoxelShape shape,
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction  rotation
  ) {
    int turns = switch (type) {
      case CORNER, TRIPLE, OPPOSITE -> getRotationTurns(rotation);
      case PYRAMID, QUAD -> 0;
    };

    for (int i = 0; i < turns; i++) {
      shape = rotateShapeY90(shape);
    }

    return shape;
  }

  private static Vec3 rotatePointsByRotation(
    Vec3 point,
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction  rotation
  ) {
    int turns = switch (type) {
      case CORNER, TRIPLE, OPPOSITE -> getRotationTurns(rotation);
      case PYRAMID, QUAD -> 0;
    };

    for (int i = 0; i < turns; i++) {
      point = rotateY90(point);
    }

    return point;
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

    int TOP_X = 8;
    int TOP_Y = 16;
    int TOP_Z = 8;

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
    return new Vec3[]{
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),
      new Vec3(.5, 1, .5)
    };
  }

  private static VoxelShape createCornerCollision() {
    VoxelShape shape = Shapes.empty();

    int TOP_X = 16;
    int TOP_Y = 16;
    int TOP_Z = 16;

    for (int i = 0; i < TOP_Y / 2; i++) {
      double t = i / (double) (TOP_Y / 2);

      double minX = lerp(0, TOP_X, t);
      double maxX = lerp(16, TOP_X, t);
      double minZ = lerp(0, TOP_Z, t);
      double maxZ = lerp(16, TOP_Z, t);

      VoxelShape rotated = Shapes.box(
        1 - maxX / 16.0,
        i * 2 / 16.0,
        1 - maxZ / 16.0,
        1 - minX / 16.0,
        (i + 1) * 2 / 16.0,
        1 - minZ / 16.0
      );

      shape = Shapes.or(shape, rotated);
    }

    return shape.optimize();
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

    int TOP_Y = 16;
    int TOP_Z = 16;

    for (int i = 0; i < TOP_Y / 2; i++) {
      double t = i / (double) (TOP_Y / 2);

      double minZ = lerp(0, TOP_Z / 2.0, t);
      double maxZ = lerp(16, TOP_Z / 2.0, t);

      VoxelShape section = Shapes.box(
        0,
        i * 2 / 16.0,
        minZ / 16.0,
        1,
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
    VoxelShape shape = Shapes.empty();

    int TOP_Y = 16;

    for (int i = 0; i < TOP_Y / 2; i++) {
      double t = i / (double) (TOP_Y / 2);

      double min = lerp(0, 8, t);
      double max = lerp(16, 8, t);

      // Сужение по Z
      VoxelShape zShape = Shapes.box(
        0,
        i * 2 / 16.0,
        min / 16.0,
        1,
        (i + 1) * 2 / 16.0,
        max / 16.0
      );

      // Сужение по X
      VoxelShape xShape = Shapes.box(
        min / 16.0,
        i * 2 / 16.0,
        0,
        max / 16.0,
        (i + 1) * 2 / 16.0,
        1
      );

      shape = Shapes.or(shape, zShape, xShape);
    }

    return shape.optimize();
  }

  // TRIPLE

  private static Vec3[] createTriplePoints() {
    return createPyramidPoints();
  }

  private static VoxelShape createTripleCollision() {
    VoxelShape shape = Shapes.empty();

    int TOP_Y = 16;

    for (int i = 0; i < TOP_Y / 2; i++) {
      double t = i / (double) (TOP_Y / 2);

      double min = lerp(0, 8, t);
      double max = lerp(16, 8, t);

      double y1 = i * 2 / 16.0;
      double y2 = (i + 1) * 2 / 16.0;

      // Основной скос — через весь блок
      VoxelShape main = Shapes.box(
        0, y1, min / 16.0,
        1, y2, max / 16.0
      );

      // Дополнительный скос — только половина блока
      VoxelShape additional = Shapes.box(
        min / 16.0, y1, 0,
        max / 16.0, y2, 0.5
      );

      shape = Shapes.or(shape, main, additional);
    }

    return shape.optimize();
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
