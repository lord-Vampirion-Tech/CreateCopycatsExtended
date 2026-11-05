
package net.ziftech.copycats_extended.client.model.TEST;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeModelCore;

import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

import com.copycatsplus.copycats.utility.shape.OutlinedVoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope.map;

import net.createmod.catnip.data.Pair;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


import java.util.List;

public final class TEST_CopycatBlockGeometry {

  // ======================

  private static final int SUBDIVISIONS = 8;

  public static VoxelShape create() {
    VoxelShape collision = createCollisionShape();

    List<Pair<Vec3, Vec3>> outline = List.of(
      edge(0, 0, 0, 16, 0, 0),
      edge(0, 0, 16, 16, 0, 16),
      edge(0, 0, 0, 0, 0, 16),
      edge(16, 0, 0, 16, 0, 16),
      edge(0, 0, 16, 0, 16, 16),
      edge(16, 0, 16, 16, 16, 16),
      edge(0, 16, 16, 16, 16, 16),
      edge(0, 0, 0, 0, 16, 16),
      edge(16, 0, 0, 16, 16, 16)
    );

    return new OutlinedVoxelShape(collision, outline);
  }

  private static VoxelShape createCollisionShape() {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < SUBDIVISIONS; i++) {
      double zMin = i / (double) SUBDIVISIONS;
      double zMax = (i + 1.0) / SUBDIVISIONS;
      double yMax = (i + 1.0) / SUBDIVISIONS;

      VoxelShape section = Shapes.box(
        0, 0,
        zMin, 1,
        yMax, zMax
      );

      shape = Shapes.joinUnoptimized(shape, section, BooleanOp.OR);
    }

    return shape.optimize();
  }

  private static Pair<Vec3, Vec3> edge(
    double x1, double y1, double z1,
    double x2, double y2, double z2
  ) {
    double offset = 16.0;

    return Pair.of(
      new Vec3(x1 / offset, y1 / offset, z1 / offset),
      new Vec3(x2 / offset, y2 / offset, z2 / offset)
    );
  }

  // ======================

  public static final double MIN_HEIGHT = 0;
  public static final double MAX_HEIGHT = 16;

  public static void assemble(
    CopycatRenderContext context,
    AssemblyTransform transform,
    boolean enhanced
  ) {

    if (enhanced) {
      assembleEnhancedSlope(context, transform);
    } else {
      assembleBasicSlope(context, transform);
    }
  }

  public static void assembleBasicSlope(
    CopycatRenderContext context,
    AssemblyTransform transform
  ) {

    context.assemblePiece(
      transform,
      vec3(0, 0, 0),
      aabb(16, 16, 16),
      cull(NORTH),
      updateUV(
        slope(Direction.UP,
          (a, b) -> map(
            0, 16,
            MIN_HEIGHT, MAX_HEIGHT,
            b
          )
        )
      )
    );
  }

  public static void assembleEnhancedSlope(
    CopycatRenderContext context,
    AssemblyTransform transform
  ) {

    CopycatSlopeModelCore.assembleSlope(
      context,
      transform,
      MIN_HEIGHT,
      MAX_HEIGHT,
      false
    );
  }
}
