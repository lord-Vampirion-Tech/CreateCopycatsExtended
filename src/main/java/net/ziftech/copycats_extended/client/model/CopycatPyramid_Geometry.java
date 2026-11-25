
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

  private static final int BLOCK_SIZE = 16;
  private static final int CENTER = 8;

  // Matches Copycats+ CCShapes.SLOPE_SUBDIVISIONS
  private static final int SUBDIVISIONS = 8;

  private record ShapeKey(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    Direction rotation
  ) {
  }

  private static final ConcurrentHashMap<ShapeKey, OutlinedVoxelShape> CACHE =
    new ConcurrentHashMap<>();

  private CopycatPyramid_Geometry() {
  }

  // MAIN

  public static OutlinedVoxelShape createShape(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    Direction rotation
  ) {
    ShapeKey key = new ShapeKey(type, facing, rotation);

    return CACHE.computeIfAbsent(
      key,
      k -> createOutlinedShape(k.type(), k.facing(), k.rotation())
    );
  }

  private static OutlinedVoxelShape createOutlinedShape(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    Direction rotation
  ) {
    VoxelShape collision = createCollision(type);

    Vec3[] points = createPoints(type);
    int[][] connections = createConnections(type);

    for (int i = 0; i < points.length; i++) {
      points[i] = rotatePointsByRotation(points[i], type, rotation);
      points[i] = rotatePoint(points[i], facing);
    }

    collision = rotateShapeByRotation(collision, type, rotation);
    collision = rotateShape(collision, facing);

    return ModelUtils.createCollisionOutline(collision);

//    return ModelUtils.createOutlinedShape(
//      collision,
//      points,
//      connections
//    );
  }

  private static Vec3[] createPoints(BASE_CopycatPyramidBlock.ShapeType type) {
    return switch (type) {
      case PYRAMID -> createPyramidPoints();
      case SIDE -> createSidePoints();
      case CORNER -> createCornerPoints();
      case SIDE_CORNER -> createSideCornerPoints();
      case OPPOSITE -> createOppositePoints();
      case QUAD -> createQuadPoints();
      case TRIPLE -> createTriplePoints();
      default -> createPyramidPoints();
    };
  }

  private static int[][] createConnections(BASE_CopycatPyramidBlock.ShapeType type) {
    return switch (type) {
      case PYRAMID -> new int[][]{
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 4}, {2, 4}, {3, 4}
      };

      case CORNER -> new int[][]{
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 4},
        {0, 5}, {3, 5},
        {0, 6}, {4, 6}, {5, 6},
        {2, 6}, {1, 6}
      };

      case OPPOSITE -> new int[][]{
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {3, 4}, {1, 5}, {2, 5},
        {4, 5}
      };

      case SIDE, SIDE_CORNER, QUAD, TRIPLE -> new int[][]{};

      default -> new int[][]{};
    };
  }

  private static VoxelShape createCollision(BASE_CopycatPyramidBlock.ShapeType type) {
    return switch (type) {
      case PYRAMID -> createPyramidCollision();
      case SIDE -> createSideCollision();
      case CORNER -> createCornerCollision();
      case SIDE_CORNER -> createSideCornerCollision();
      case OPPOSITE -> createOppositeCollision();
      case QUAD -> createQuadCollision();
      case TRIPLE -> createTripleCollision();
      default -> createPyramidCollision();
    };
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
    return createCenteredCollision(CENTER, CENTER);
  }

  // SIDE

  private static Vec3[] createSidePoints() {
    return new Vec3[]{
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),
      new Vec3(.5, 1, 1)
    };
  }

  private static VoxelShape createSideCollision() {
    return createCenteredCollision(CENTER, 0);
  }

  // CORNER

  private static Vec3[] createCornerPoints() {
    return new Vec3[]{
      new Vec3(0, 0, 0),   // 0: um0f
      new Vec3(1, 0, 0),   // 1: dtfE
      new Vec3(1, 0, 1),   // 2: 7DTy
      new Vec3(0, 0, 1),   // 3: cInI
      new Vec3(.5, 1, 0),  // 4: jCxp
      new Vec3(0, 1, .5),  // 5: 3FOr
      new Vec3(.5, 1, .5)  // 6: zQ7O
    };
  }

    private static VoxelShape createCornerCollision() {
      VoxelShape shape = Shapes.empty();

      for (int i = 0; i < SUBDIVISIONS; i++) {
        double t = i / (double) SUBDIVISIONS;

        double minX = lerp(0, 8, t);
        double maxX = lerp(BLOCK_SIZE, 8, t);

        double minZ = lerp(0, 8, t);
        double maxZ = lerp(BLOCK_SIZE, 8, t);

        VoxelShape section = Shapes.box(
          minX / BLOCK_SIZE,
          getSliceMin(i),
          minZ / BLOCK_SIZE,
          maxX / BLOCK_SIZE,
          getSliceMax(i),
          maxZ / BLOCK_SIZE
        );

        shape = Shapes.or(shape, section);
      }

      return shape.optimize();
    }

  // SIDE_CORNER

  private static Vec3[] createSideCornerPoints() {
    return new Vec3[]{
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),
      new Vec3(.5, 1, .5),
      new Vec3(.5, 1, 1)
    };
  }

  private static VoxelShape createSideCornerCollision() {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < SUBDIVISIONS; i++) {
      double t = i / (double) SUBDIVISIONS;

      double min = lerp(0, BLOCK_SIZE, t);
      double max = BLOCK_SIZE;

      VoxelShape section = Shapes.box(
        1 - max / BLOCK_SIZE,
        getSliceMin(i),
        1 - max / BLOCK_SIZE,
        1 - min / BLOCK_SIZE,
        getSliceMax(i),
        1 - min / BLOCK_SIZE
      );

      shape = Shapes.or(shape, section);
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

    for (int i = 0; i < SUBDIVISIONS; i++) {
      double t = i / (double) SUBDIVISIONS;

      double minZ = lerp(0, CENTER, t);
      double maxZ = lerp(BLOCK_SIZE, CENTER, t);

      VoxelShape section = Shapes.box(
        0,
        getSliceMin(i),
        minZ / BLOCK_SIZE,
        1,
        getSliceMax(i),
        maxZ / BLOCK_SIZE
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

    for (int i = 0; i < SUBDIVISIONS; i++) {
      double t = i / (double) SUBDIVISIONS;

      double min = lerp(0, CENTER, t);
      double max = lerp(BLOCK_SIZE, CENTER, t);

      double y1 = getSliceMin(i);
      double y2 = getSliceMax(i);

      VoxelShape zShape = Shapes.box(
        0, y1, min / BLOCK_SIZE,
        1, y2, max / BLOCK_SIZE
      );

      VoxelShape xShape = Shapes.box(
        min / BLOCK_SIZE, y1, 0,
        max / BLOCK_SIZE, y2, 1
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

    for (int i = 0; i < SUBDIVISIONS; i++) {
      double t = i / (double) SUBDIVISIONS;

      double min = lerp(0, CENTER, t);
      double max = lerp(BLOCK_SIZE, CENTER, t);

      double y1 = getSliceMin(i);
      double y2 = getSliceMax(i);

      VoxelShape main = Shapes.box(
        0, y1, min / BLOCK_SIZE,
        1, y2, max / BLOCK_SIZE
      );

      VoxelShape additional = Shapes.box(
        min / BLOCK_SIZE, y1, 0,
        max / BLOCK_SIZE, y2, 0.5
      );

      shape = Shapes.or(shape, main, additional);
    }

    return shape.optimize();
  }

  // SHARED GEOMETRY

  private static VoxelShape createCenteredCollision(
    int topX,
    int topZ
  ) {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < SUBDIVISIONS; i++) {
      double t = i / (double) SUBDIVISIONS;

      double minX = lerp(0, topX, t);
      double maxX = lerp(BLOCK_SIZE, topX, t);

      double minZ = lerp(0, topZ, t);
      double maxZ = lerp(BLOCK_SIZE, topZ, t);

      VoxelShape section = Shapes.box(
        minX / BLOCK_SIZE,
        getSliceMin(i),
        minZ / BLOCK_SIZE,
        maxX / BLOCK_SIZE,
        getSliceMax(i),
        maxZ / BLOCK_SIZE
      );

      shape = Shapes.or(shape, section);
    }

    return shape.optimize();
  }

  // ROTATION

  private static int getRotationTurns_4(Direction rotation) {
    return switch (rotation) {
      case SOUTH -> 0;
      case WEST -> 1;
      case NORTH -> 2;
      case EAST -> 3;
      default -> 0;
    };
  }

  private static int getRotationTurns_2(Direction rotation) {
    return switch (rotation) {
      case SOUTH, NORTH -> 0;
      case WEST, EAST -> 1;
      default -> 0;
    };
  }

  private static Vec3 rotatePointsByRotation(
    Vec3 point,
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction rotation
  ) {
    int turns = switch (type) {
      case SIDE, CORNER, SIDE_CORNER, TRIPLE -> getRotationTurns_4(rotation);
      case OPPOSITE -> getRotationTurns_2(rotation);
      case PYRAMID, QUAD -> 0;
      default -> 0;
    };

    for (int i = 0; i < turns; i++) {
      point = rotateY90(point);
    }

    return point;
  }

  private static Vec3 rotateY90(Vec3 point) {
    return new Vec3(1 - point.z, point.y, point.x);
  }

  private static VoxelShape rotateShapeByRotation(
    VoxelShape shape,
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction rotation
  ) {
    int turns = switch (type) {
      case SIDE, CORNER, SIDE_CORNER, TRIPLE -> getRotationTurns_4(rotation);
      case OPPOSITE -> getRotationTurns_2(rotation);
      case PYRAMID, QUAD -> 0;
      default -> 0;
    };

    for (int i = 0; i < turns; i++) {
      shape = rotateShapeY90(shape);
    }

    return shape;
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

  // FACING

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

  // SUBDIVISION

  private static double getSliceMin(int index) {
    return index / (double) SUBDIVISIONS;
  }

  private static double getSliceMax(int index) {
    return (index + 1) / (double) SUBDIVISIONS;
  }

  // UTILITY

  private static double lerp(double start, double end, double t) {
    return start + (end - start) * t;
  }
}
