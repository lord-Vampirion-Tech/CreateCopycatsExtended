package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.createmod.catnip.data.Pair;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class ModelUtils {

  private ModelUtils() {
  }

  public static OutlinedVoxelShape createOutlinedShape(
    VoxelShape collision,
    Vec3[] points,
    int[][] connections
  ) {
    List<Pair<Vec3, Vec3>> outline = new ArrayList<>();

    for (int[] connection : connections) {
      if (connection.length != 2)
        throw new IllegalArgumentException("Each connection must contain exactly 2 indices");

      int start = connection[0];
      int end = connection[1];

      if (start < 0 || start >= points.length
        || end < 0 || end >= points.length) {
        throw new IllegalArgumentException("Connection index out of bounds");
      }

      outline.add(
        Pair.of(points[start], points[end])
      );
    }

    return new OutlinedVoxelShape(collision, outline);
  }

  public static OutlinedVoxelShape createCollisionOutline(VoxelShape collision) {
    List<Pair<Vec3, Vec3>> outline = new ArrayList<>();

    collision.forAllEdges((x1, y1, z1, x2, y2, z2) ->
      outline.add(
        Pair.of(
          new Vec3(x1, y1, z1),
          new Vec3(x2, y2, z2)
        )
      )
    );

    return new OutlinedVoxelShape(collision, outline);
  }
}
