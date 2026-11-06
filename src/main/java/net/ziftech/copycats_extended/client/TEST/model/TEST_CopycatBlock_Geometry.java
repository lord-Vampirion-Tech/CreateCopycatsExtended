package net.ziftech.copycats_extended.client.TEST.model;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.createmod.catnip.data.Pair;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class TEST_CopycatBlock_Geometry {

  private static final int SUBDIVISIONS = 8;

  // OUTLINE / COLLISION
  // ==================================================

  public static VoxelShape createShape(Direction facing) {
    VoxelShape collision = createCollisionShape(facing);

    List<Pair<Vec3, Vec3>> outline = List.of(
      // Основание
      edge(facing, 0, 0, 0, 16, 0, 0),
      edge(facing, 16, 0, 0, 16, 0, 16),
      edge(facing, 16, 0, 16, 0, 0, 16),
      edge(facing, 0, 0, 16, 0, 0, 0),

      // Рёбра к вершине
      edge(facing, 0, 0, 0, 8, 16, 8),
      edge(facing, 16, 0, 0, 8, 16, 8),
      edge(facing, 16, 0, 16, 8, 16, 8),
      edge(facing, 0, 0, 16, 8, 16, 8)
    );

    return new OutlinedVoxelShape(collision, outline);
  }

  private static VoxelShape createCollisionShape(Direction facing) {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < SUBDIVISIONS; i++) {
      double tMin = i / (double) SUBDIVISIONS;
      double tMax = (i + 1.0) / SUBDIVISIONS;

      double halfMin = 0.5 * (1.0 - tMin);

      VoxelShape section = Shapes.box(
        0.5 - halfMin,
        tMin,
        0.5 - halfMin,
        0.5 + halfMin,
        tMax,
        0.5 + halfMin
      );

      shape = Shapes.joinUnoptimized(
        shape,
        rotateShape(section, facing),
        BooleanOp.OR
      );
    }

    return shape.optimize();
  }

  // ROTATION
  // ==================================================

  private static VoxelShape rotateShape(
    VoxelShape shape,
    Direction facing
  ) {
    if (facing == Direction.UP)
      return shape;

    VoxelShape result = Shapes.empty();

    for (var box : shape.toAabbs()) {
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double minZ = Double.POSITIVE_INFINITY;

      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      double maxZ = Double.NEGATIVE_INFINITY;

      for (int x = 0; x <= 1; x++) {
        for (int y = 0; y <= 1; y++) {
          for (int z = 0; z <= 1; z++) {

            Vec3 point = rotatePoint(
              facing,
              new Vec3(
                x == 0 ? box.minX : box.maxX,
                y == 0 ? box.minY : box.maxY,
                z == 0 ? box.minZ : box.maxZ
              )
            );

            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            minZ = Math.min(minZ, point.z);

            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
            maxZ = Math.max(maxZ, point.z);
          }
        }
      }

      result = Shapes.joinUnoptimized(
        result,
        Shapes.box(minX, minY, minZ, maxX, maxY, maxZ),
        BooleanOp.OR
      );
    }

    return result.optimize();
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

  // OUTLINE HELPERS
  // ==================================================

  private static Pair<Vec3, Vec3> edge(
    Direction facing,
    double x1, double y1, double z1,
    double x2, double y2, double z2
  ) {
    return Pair.of(
      rotatePoint(
        facing,
        new Vec3(x1 / 16.0, y1 / 16.0, z1 / 16.0)
      ),
      rotatePoint(
        facing,
        new Vec3(x2 / 16.0, y2 / 16.0, z2 / 16.0)
      )
    );
  }
}