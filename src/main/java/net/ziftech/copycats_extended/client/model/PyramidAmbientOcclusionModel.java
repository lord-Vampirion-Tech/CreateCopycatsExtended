
package net.ziftech.copycats_extended.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.ziftech.copycats_extended.block.Pyramids.BASE_CopycatPyramidBlock;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramidCorner;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PyramidAmbientOcclusionModel extends BakedModelWrapper<BakedModel> {

  private static final int VERTEX_STRIDE =
    DefaultVertexFormat.BLOCK.getVertexSize() / Integer.BYTES;

  private static final double EPSILON = 0.00001;

  public PyramidAmbientOcclusionModel(BakedModel originalModel) {
    super(originalModel);
  }

  @Override
  public List<BakedQuad> getQuads(
    @Nullable BlockState state,
    @Nullable Direction side,
    RandomSource random
  ) {
    return processQuads(super.getQuads(state, side, random), state);
  }

  @Override
  public List<BakedQuad> getQuads(
    @Nullable BlockState state,
    @Nullable Direction side,
    RandomSource random,
    ModelData data,
    @Nullable RenderType renderType
  ) {
    return processQuads(
      super.getQuads(state, side, random, data, renderType),
      state
    );
  }

  private static List<BakedQuad> processQuads(
    List<BakedQuad> quads,
    @Nullable BlockState state
  ) {
    List<BakedQuad> result = new ArrayList<>(quads.size());

    for (BakedQuad quad : quads) {
      if (isSloped(quad, state)) {
        result.add(new BakedQuad(
          quad.getVertices(),
          quad.getTintIndex(),
          quad.getDirection(),
          quad.getSprite(),
          quad.isShade(),
          false
        ));
      } else {
        result.add(quad);
      }
    }

    return result;
  }

  private static boolean isSloped(
    BakedQuad quad,
    @Nullable BlockState state
  ) {
    int[] vertices = quad.getVertices();

    double x1 = Float.intBitsToFloat(vertices[0]);
    double y1 = Float.intBitsToFloat(vertices[1]);
    double z1 = Float.intBitsToFloat(vertices[2]);

    double x2 = Float.intBitsToFloat(vertices[VERTEX_STRIDE]);
    double y2 = Float.intBitsToFloat(vertices[VERTEX_STRIDE + 1]);
    double z2 = Float.intBitsToFloat(vertices[VERTEX_STRIDE + 2]);

    double x3 = Float.intBitsToFloat(vertices[VERTEX_STRIDE * 2]);
    double y3 = Float.intBitsToFloat(vertices[VERTEX_STRIDE * 2 + 1]);
    double z3 = Float.intBitsToFloat(vertices[VERTEX_STRIDE * 2 + 2]);

    double ax = x2 - x1;
    double ay = y2 - y1;
    double az = z2 - z1;

    double bx = x3 - x1;
    double by = y3 - y1;
    double bz = z3 - z1;

    double nx = ay * bz - az * by;
    double ny = az * bx - ax * bz;
    double nz = ax * by - ay * bx;

    double length = Math.sqrt(nx * nx + ny * ny + nz * nz);

    if (length < 1.0E-6) {
      return false;
    }

    nx /= length;
    ny /= length;
    nz /= length;

    return
      (Math.abs(nx) > EPSILON && Math.abs(ny) > EPSILON) ||
        (Math.abs(nx) > EPSILON && Math.abs(nz) > EPSILON) ||
        (Math.abs(ny) > EPSILON && Math.abs(nz) > EPSILON);
  }
}
