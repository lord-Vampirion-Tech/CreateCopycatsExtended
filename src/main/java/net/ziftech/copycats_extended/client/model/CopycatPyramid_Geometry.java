
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
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
    ShapeKey key = new ShapeKey(
      type, facing, rotation, x, y, z
    );

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
    VoxelShape collision = createCollisionShape(
      type, facing, rotation, x, y, z
    );

    Vec3[] points = switch (type) {
      case PYRAMID -> new Vec3[] {};
      case CORNER -> new Vec3[] {};
      case OPPOSITE -> new Vec3[] {};
      case QUAD -> new Vec3[] {};
      case TRIPLE -> new Vec3[] {};
    };

    int[][] connections = switch (type) {
      case PYRAMID -> new int[][] {};
      case CORNER -> new int[][] {};
      case OPPOSITE -> new int[][] {};
      case QUAD -> new int[][] {};
      case TRIPLE -> new int[][] {};
    };

    return ModelUtils.createOutlinedShape(
      collision,
      points,
      connections
    );
  }

  private static VoxelShape createCollisionShape(
    BASE_CopycatPyramidBlock.ShapeType type,
    Direction facing,
    int rotation,
    int x,
    int y,
    int z
  ) {
    VoxelShape shape = Shapes.empty();

    int subdivisions = y + 1;

    for (int i = 0; i < subdivisions; i++) {

      double tMin = i / (double) subdivisions;
      double tMax = (i + 1.0) / subdivisions;

      switch (type) {
        case PYRAMID -> {
          // TODO: Pyramid collision
        }

        case CORNER -> {
          // TODO: Corner collision
        }

        case OPPOSITE -> {
          // TODO: Opposite collision
        }

        case QUAD -> {
          // TODO: Quad collision
        }

        case TRIPLE -> {
          // TODO: Triple collision
        }
      }

      // TODO: Add current section to shape
    }

    return shape.optimize();
  }
}
