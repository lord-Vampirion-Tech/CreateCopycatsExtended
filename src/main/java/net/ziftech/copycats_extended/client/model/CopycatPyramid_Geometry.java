
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramidLayer;

import java.util.concurrent.ConcurrentHashMap;

public class CopycatPyramid_Geometry {

  private static final int BLOCK_SIZE = 16;
  private static final int CENTER = 8;

  // Matches Copycats+ CCShapes.SLOPE_SUBDIVISIONS
  private static final int SUBDIVISIONS = 8;

  private record ShapeKey(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    Direction rotation,
    int layer
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
    Direction rotation,
    BlockState state
  ) {
    int layer = type == BASE_CopycatPyramidBlock.ShapeType.PYRAMID_LAYER
      ? state.getValue(CopycatPyramidLayer.LAYERS)
      : 0;

    ShapeKey key = new ShapeKey(type, facing, rotation, layer);

    return CACHE.computeIfAbsent(
      key,
      k -> createOutlinedShape(
        k.type(),
        k.facing(),
        k.rotation(),
        k.layer()
      )
    );
  }

  private static OutlinedVoxelShape createOutlinedShape(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    Direction rotation,
    int layer
  ) {
    VoxelShape collision = createCollision(type, layer);

    Vec3[] points = createPoints(type);
    int[][] connections = createConnections(type);

    for (int i = 0; i < points.length; i++) {
      points[i] = rotatePointsByRotation(points[i], type, rotation);
      points[i] = rotatePoint(points[i], facing);
    }

    collision = rotateShapeByRotation(collision, type, rotation);
    collision = rotateShape(collision, facing);

    return ModelUtils.createCollisionOutline(collision);
//  return ModelUtils.createOutlinedShape(
//    collision,
//    points,
//    connections
//  );
  }

  // POINTS

  private static Vec3[] createPoints(
    BASE_CopycatPyramidBlock.ShapeType type
  ) {
    return switch (type) {
      case PYRAMID -> createPyramidPoints();
      default -> createCubePoints();
    };
  }

  private static int[][] createConnections(
    BASE_CopycatPyramidBlock.ShapeType type
  ) {
    return switch (type) {
      case PYRAMID -> new int[][]{
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {0, 4}, {1, 4}, {2, 4}, {3, 4}
      };

      default -> new int[][]{
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}
      };
    };
  }

  private static Vec3[] createCubePoints() {
    return new Vec3[]{
      new Vec3(0, 0, 0),
      new Vec3(1, 0, 0),
      new Vec3(1, 0, 1),
      new Vec3(0, 0, 1),

      new Vec3(0, 1, 0),
      new Vec3(1, 1, 0),
      new Vec3(1, 1, 1),
      new Vec3(0, 1, 1)
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
    return createPyramidCollision(CENTER, CENTER);
  }
  private static VoxelShape createPyramidLayerCollision(int layer) {
    return createPyramidCollision(layer, CENTER, CENTER);
  }

  // COLLISION

  private static VoxelShape createCollision(
    BASE_CopycatPyramidBlock.ShapeType type,
    int layer
  ) {
    return switch (type) {
      case PYRAMID -> createPyramidCollision();
      case PYRAMID_LAYER -> createPyramidLayerCollision(layer);
      default -> Shapes.block();
    };
  }

  // SHARED GEOMETRY

  private static VoxelShape createPyramidCollision(
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


  private static VoxelShape createPyramidCollision(
    int layer,
    double topX,
    double topZ
  ) {
    VoxelShape shape = Shapes.empty();

    layer = Mth.clamp(layer, 0, 16);

    if (layer == 0)
      return Shapes.empty();

    int n = layer <= 8 ? layer : 16 - layer;

    if (n == 0)
      return Shapes.block();

    double blockHeight;
    double offsetY;

    if (layer <= 8) {
      blockHeight = layer / (double) n;
      offsetY = 0;
    } else {
      blockHeight = (16 - layer) / (double) n;
      offsetY = layer - 8;
    }

    for (int i = 0; i < n; i++) {
      double minY = (i * blockHeight + offsetY) * 2;
      double maxY = ((i + 1) * blockHeight + offsetY) * 2;

      if (layer > 8 && i == 0)
        minY = 0;

      double minX = (i * topX) / n;
      double maxX = 16 - (i * (16 - topX)) / n;

      double minZ = (i * topZ) / n;
      double maxZ = 16 - (i * (16 - topZ)) / n;

      VoxelShape section = Shapes.box(
        minX / 16.0,
        minY / 16.0,
        minZ / 16.0,
        maxX / 16.0,
        maxY / 16.0,
        maxZ / 16.0
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
      case PYRAMID -> 0;
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
      case PYRAMID -> 0;
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

  private static VoxelShape rotateShape(
    VoxelShape shape,
    Direction facing
  ) {
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

  private static Vec3 rotatePoint(
    Vec3 point,
    Direction facing
  ) {
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

  private static double lerp(
    double start,
    double end,
    double t
  ) {
    return start + (end - start) * t;
  }
}
