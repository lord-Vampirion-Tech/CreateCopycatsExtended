
package net.ziftech.copycats_extended.client.model;

import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.SlopeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SlopeTextureBakedModel extends BakedModelWrapperWithData {

  private static final long MATERIAL_RANDOM_SEED = 42L;
  private static final double UV_DETERMINANT_EPSILON = 1.0E-8;

  private final BakedModel sourceModel;
  private final BlockState slopeState;
  private final SlopeMode mode;

  public SlopeTextureBakedModel(BakedModel sourceModel, BlockState slopeState, SlopeMode mode) {
    super(sourceModel);

    this.sourceModel = sourceModel;
    this.slopeState = slopeState;
    this.mode = mode;
  }

  // ============================================================
  // ПОЛУЧЕНИЕ QUAD
  // ============================================================

  @Override
  public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource random) {
    List<BakedQuad> quads = sourceModel.getQuads(state, side, random);
    if (state == null || !shouldTransform(side)) return quads;
    return transformSurfaceQuads(quads, getMaterialSideQuads(state));
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource random, @NotNull ModelData data, RenderType renderType) {
    List<BakedQuad> quads = sourceModel.getQuads(state, side, random, data, renderType);
    if (!shouldTransform(side)) return quads;
    return transformSurfaceQuads(quads, getMaterialSideQuads(state, data, renderType));
  }

  // ============================================================
  // ШИРОКАЯ НАКЛОННАЯ ПОВЕРХНОСТЬ
  // ============================================================

  private boolean shouldTransform(@Nullable Direction side) {
    return mode == SlopeMode.SECOND && (side == null || side == getSurfaceFace());
  }

  private Direction getSurfaceFace() {
    return slopeState.getValue(CopycatAdvSlopeBlock.HALF) == Half.TOP ? Direction.DOWN : Direction.UP;
  }

  private Direction getMaterialSide() {
    return slopeState.getValue(CopycatAdvSlopeBlock.FACING).getOpposite();
  }

  private List<BakedQuad> getMaterialSideQuads(BlockState materialState) {
    Direction face = getMaterialSide();
    List<BakedQuad> result = new ArrayList<>(sourceModel.getQuads(materialState, face, RandomSource.create(MATERIAL_RANDOM_SEED)));

    for (BakedQuad quad : sourceModel.getQuads(materialState, null, RandomSource.create(MATERIAL_RANDOM_SEED))) {
      if (quad.getDirection() == face) result.add(quad);
    }
    return result;
  }

  private List<BakedQuad> getMaterialSideQuads(BlockState materialState, ModelData data, RenderType renderType) {
    Direction face = getMaterialSide();
    List<BakedQuad> result = new ArrayList<>(sourceModel.getQuads(materialState, face, RandomSource.create(MATERIAL_RANDOM_SEED), data, renderType));

    for (BakedQuad quad : sourceModel.getQuads(materialState, null, RandomSource.create(MATERIAL_RANDOM_SEED), data, renderType)) {
      if (quad.getDirection() == face) result.add(quad);
    }
    return result;
  }

  private List<BakedQuad> transformSurfaceQuads(List<BakedQuad> quads, List<BakedQuad> sideQuads) {
    if (mode != SlopeMode.SECOND || sideQuads.isEmpty()) return quads;

    Direction surfaceFace = getSurfaceFace();
    Direction materialSide = getMaterialSide();

    boolean ceiling = slopeState.getValue(CopycatAdvSlopeBlock.HALF) == Half.TOP;
    List<BakedQuad> result = new ArrayList<>(quads.size());

    for (BakedQuad quad : quads) {
      if (quad.getDirection() != surfaceFace) {
        result.add(quad);
        continue;
      }
      for (BakedQuad sideQuad : sideQuads) result.add(remapSurfaceFromSideQuad(quad, sideQuad, materialSide, ceiling));
    }
    return result;
  }

  private static BakedQuad remapSurfaceFromSideQuad(BakedQuad surfaceQuad, BakedQuad sideQuad, Direction materialSide, boolean ceiling) {
    int[] vertices = surfaceQuad.getVertices().clone();

    UvPlane uvPlane = UvPlane.from(sideQuad, materialSide, ceiling);

    for (int i = 0; i < 4; i++) {
      Vec3 pos = BakedQuadHelper.getXYZ(vertices, i);

      BakedQuadHelper.setU(vertices, i, uvPlane.u(pos.x, pos.z));
      BakedQuadHelper.setV(vertices, i, uvPlane.v(pos.x, pos.z));
    }

    return new BakedQuad(vertices, sideQuad.getTintIndex(), surfaceQuad.getDirection(), sideQuad.getSprite(), surfaceQuad.isShade());
  }

  // ============================================================
  // MODEL DATA
  // ============================================================

  @Override
  protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
    return builder;
  }

  // ============================================================
  // UV PLANE
  // ============================================================

  private record UvPlane(
    double p0, double q0,
    double u0, double v0,
    double uP, double uQ,
    double vP, double vQ,
    boolean flipU, boolean flipV,
    double minU, double maxU,
    double minV, double maxV
  ) {

    float u(double x, double z) {
      double value = u0 + uP * (x - p0) + uQ * (z - q0);
      if (flipU) value = minU + maxU - value;
      return (float) value;
    }

    float v(double x, double z) {
      double value = v0 + vP * (x - p0) + vQ * (z - q0);
      if (flipV) value = minV + maxV - value;
      return (float) value;
    }

    static UvPlane from(BakedQuad quad, Direction face, boolean ceiling) {
      int[] data = quad.getVertices();

      double[] p = new double[4];
      double[] q = new double[4];
      double[] u = new double[4];
      double[] v = new double[4];

      double minU = Double.POSITIVE_INFINITY;
      double maxU = Double.NEGATIVE_INFINITY;
      double minV = Double.POSITIVE_INFINITY;
      double maxV = Double.NEGATIVE_INFINITY;

      for (int i = 0; i < 4; i++) {
        Vec3 pos = BakedQuadHelper.getXYZ(data, i);

        switch (face.getAxis()) {
          case X -> {
            p[i] = pos.y;
            q[i] = pos.z;
          }
          case Z -> {
            p[i] = pos.x;
            q[i] = pos.y;
          }
          case Y -> {
            p[i] = pos.x;
            q[i] = pos.z;
          }
        }

        u[i] = BakedQuadHelper.getU(data, i);
        v[i] = BakedQuadHelper.getV(data, i);

        minU = Math.min(minU, u[i]);
        maxU = Math.max(maxU, u[i]);
        minV = Math.min(minV, v[i]);
        maxV = Math.max(maxV, v[i]);
      }

      boolean flipU = true;
      boolean flipV = true;

      if (face == Direction.NORTH || face == Direction.WEST) {
        flipU = false;
        flipV = false;
      }

      if (face == Direction.EAST || face == Direction.SOUTH) {
        flipU = false;
      }

      if (ceiling) {
        flipV = !flipV;
      }

      for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 4; j++) {
          for (int k = j + 1; k < 4; k++) {
            double dp1 = p[j] - p[i];
            double dq1 = q[j] - q[i];
            double dp2 = p[k] - p[i];
            double dq2 = q[k] - q[i];

            double determinant = dp1 * dq2 - dp2 * dq1;

            if (Math.abs(determinant) < UV_DETERMINANT_EPSILON) continue;

            double uP = ((u[j] - u[i]) * dq2 - (u[k] - u[i]) * dq1) / determinant;
            double uQ = (dp1 * (u[k] - u[i]) - dp2 * (u[j] - u[i])) / determinant;
            double vP = ((v[j] - v[i]) * dq2 - (v[k] - v[i]) * dq1) / determinant;
            double vQ = (dp1 * (v[k] - v[i]) - dp2 * (v[j] - v[i])) / determinant;

            return new UvPlane(
              p[i], q[i],
              u[i], v[i],
              uP, uQ,
              vP, vQ,
              flipU, flipV,
              minU, maxU,
              minV, maxV
            );
          }
        }
      }

      return new UvPlane(
        0, 0,
        u[0], v[0],
        0, 0,
        0, 0,
        flipU, flipV,
        minU, maxU,
        minV, maxV
      );
    }
  }
}
