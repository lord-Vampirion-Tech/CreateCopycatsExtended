
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.createmod.catnip.data.Pair;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class DoubleCopycat_Geometry {

  // OUTLINE / COLLISION
  // ==================================================

  public static VoxelShape createShape(Direction.Axis axis) {

    VoxelShape collision = createCollisionShape(axis);

    List<Pair<Vec3, Vec3>> outline = List.of(
      // Основание
      edge(axis, 0, 0, 0, 16, 0, 0),
      edge(axis, 16, 0, 0, 16, 0, 16),
      edge(axis, 16, 0, 16, 0, 0, 16),
      edge(axis, 0, 0, 16, 0, 0, 0),

      // Разделительная плоскость
      edge(axis, 0, 8, 0, 16, 8, 0),
      edge(axis, 16, 8, 0, 16, 8, 16),
      edge(axis, 16, 8, 16, 0, 8, 16),
      edge(axis, 0, 8, 16, 0, 8, 0),

      // Верхняя часть
      edge(axis, 0, 16, 0, 16, 16, 0),
      edge(axis, 16, 16, 0, 16, 16, 16),
      edge(axis, 16, 16, 16, 0, 16, 16),
      edge(axis, 0, 16, 16, 0, 16, 0),

      // Вертикальные грани
      edge(axis, 0, 0, 0, 0, 16, 0),
      edge(axis, 16, 0, 0, 16, 16, 0),
      edge(axis, 0, 0, 16, 0, 16, 16),
      edge(axis, 16, 0, 16, 16, 16, 16)
    );

    return new OutlinedVoxelShape(collision, outline);
  }

  private static VoxelShape createCollisionShape(Direction.Axis axis) {
    return Shapes.joinUnoptimized(
      getPartShape(axis, false),
      getPartShape(axis, true),
      BooleanOp.OR
    ).optimize();
  }

  // PART SHAPES
  // ==================================================

  public static VoxelShape getPartShape(
    Direction.Axis axis,
    boolean one
  ) {
    return switch (axis) {
      case X -> one
        ? Shapes.box(0.5, 0, 0, 1, 1, 1)
        : Shapes.box(0, 0, 0, 0.5, 1, 1);

      case Y -> one
        ? Shapes.box(0, 0.5, 0, 1, 1, 1)
        : Shapes.box(0, 0, 0, 1, 0.5, 1);

      case Z -> one
        ? Shapes.box(0, 0, 0.5, 1, 1, 1)
        : Shapes.box(0, 0, 0, 1, 1, 0.5);
    };
  }

  // ROTATION
  // ==================================================

  private static Vec3 rotatePoint(
    Direction.Axis axis,
    Vec3 point
  ) {
    double x = point.x;
    double y = point.y;
    double z = point.z;

    return switch (axis) {
      case Y -> new Vec3(x, y, z);
      case X -> new Vec3(y, 1 - x, z);
      case Z -> new Vec3(x, 1 - z, y);
    };
  }

  // OUTLINE HELPERS
  // ==================================================

  private static Pair<Vec3, Vec3> edge(
    Direction.Axis axis,
    double x1, double y1, double z1,
    double x2, double y2, double z2
  ) {
    return Pair.of(
      rotatePoint(
        axis,
        new Vec3(x1 / 16.0, y1 / 16.0, z1 / 16.0)
      ),
      rotatePoint(
        axis,
        new Vec3(x2 / 16.0, y2 / 16.0, z2 / 16.0)
      )
    );
  }
}
