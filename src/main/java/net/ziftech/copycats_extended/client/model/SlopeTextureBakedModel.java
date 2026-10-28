
package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.assembly.*;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableQuad;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadUVUpdate;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.SlopeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlopeTextureBakedModel extends BakedModelWrapperWithData {

  private static final long MATERIAL_RANDOM_SEED = 42L;
  private static final double UV_DETERMINANT_EPSILON = 1.0E-8;

  private final BakedModel sourceModel;
  private final BlockState slopeState;
  private final SlopeMode mode;

  public SlopeTextureBakedModel(
    BakedModel sourceModel,
    BlockState slopeState,
    SlopeMode mode
  ) {
    super(sourceModel);
    this.sourceModel = sourceModel;
    this.slopeState = slopeState;
    this.mode = mode;
  }

  /**
   * Оборачивает контекст сборки геометрии скоса.
   */
  public static CopycatRenderContext wrapAssemblyContext(
    CopycatRenderContext delegate,
    Direction facing
  ) {
    return new TriangleSideUvContext(delegate, facing.getAxis());
  }

  /**
   * Проверяет, содержит ли UV-трансформация наклон верхней грани.
   * Только такие секции участвуют в дополнительном remap боковых UV.
   */
  private static boolean hasUpperSlopeTransform(QuadUVUpdate uvUpdate) {
    return Arrays.stream(uvUpdate.transforms())
      .anyMatch(transform ->
        transform instanceof QuadSlope slope
          && slope.face() == Direction.UP
      );
  }

  /**
   * Целевые боковые грани лежат на плоскостях x = 0 и x = 1.
   */
  private static boolean isTargetTriangleSide(
    MutableQuad quad,
    Direction.Axis sideAxis
  ) {
    MutableVec3 normal = quad.computeFaceNormal();

    if (sideAxis == Direction.Axis.X) {
      return Math.abs(normal.x) > 0.99
        && quad.vertices.stream()
        .allMatch(vertex ->
          Math.min(
            Math.abs(vertex.xyz.x),
            Math.abs(vertex.xyz.x - 1.0)
          ) < 1.0E-5
        );
    }

    if (sideAxis == Direction.Axis.Z) {
      return Math.abs(normal.z) > 0.99
        && quad.vertices.stream()
        .allMatch(vertex ->
          Math.min(
            Math.abs(vertex.xyz.z),
            Math.abs(vertex.xyz.z - 1.0)
          ) < 1.0E-5
        );
    }

    return false;
  }

  /**
   * Применяет геометрические трансформации один раз.
   *
   * Для боковых граней UV пересчитываются по аффинной UV-плоскости,
   * построенной из исходных координат и UV вершин этой же грани.
   * Для остальных граней сохраняется стандартный алгоритм QuadUVUpdate.
   */
  private static QuadTransform remapSideUv(
    QuadUVUpdate original,
    Direction.Axis sideAxis
  ) {
    return (quad, sprite) -> {
      List<MutableVec3> originalPositions = new ArrayList<>(4);
      for (var vertex : quad.vertices) {
        originalPositions.add(vertex.xyz.copy());
      }

      for (QuadTransform transform : original.transforms()) {
        transform.transformQuad(quad, sprite);
      }

      if (isTargetTriangleSide(quad, sideAxis)) {
        SideUvPlane plane = SideUvPlane.from(
          originalPositions,
          quad,
          sideAxis
        );

        if (plane != null) {
          for (var vertex : quad.vertices) {
            MutableVec3 position = vertex.xyz;
            double p = sideAxis == Direction.Axis.X
              ? position.y
              : position.x;
            double q = sideAxis == Direction.Axis.X
              ? position.z
              : position.y;

            vertex.uv.set(plane.u(p, q), plane.v(p, q));
          }

          return true;
        }
      }

      // Вне целевой боковой грани оставляем стандартный UV-update.
      QuadUVUpdate.updateUV(quad, sprite, originalPositions);
      return true;
    };
  }

  private static QuadTransform[] adjustTransforms(
    QuadTransform[] transforms,
    Direction.Axis sideAxis
  ) {
    if (transforms.length == 1
      && transforms[0] instanceof QuadUVUpdate uvUpdate
      && hasUpperSlopeTransform(uvUpdate)) {
      return new QuadTransform[] {
        remapSideUv(uvUpdate, sideAxis)
      };
    }

    return transforms;
  }

  private static class TriangleSideUvContext
    implements CopycatRenderContext {

    private final CopycatRenderContext delegate;
    private final Direction.Axis sideAxis;

    private TriangleSideUvContext(
      CopycatRenderContext delegate,
      Direction.Axis sideAxis
    ) {
      this.delegate = delegate;
      this.sideAxis = sideAxis;
    }

    @Override
    public void assemblePiece(
      AssemblyTransform assemblyTransform,
      MutableVec3 offset,
      MutableAABB select,
      MutableCullFace cull
    ) {
      delegate.assemblePiece(assemblyTransform, offset, select, cull);
    }

    @Override
    public void assemblePiece(
      AssemblyTransform assemblyTransform,
      MutableVec3 offset,
      MutableAABB select,
      MutableCullFace cull,
      QuadTransform... transforms
    ) {
      delegate.assemblePiece(
        assemblyTransform,
        offset,
        select,
        cull,
        adjustTransforms(transforms, sideAxis)
      );
    }

    @Override
    public void assembleAll() {
      delegate.assembleAll();
    }

    @Override
    public void assembleRaw(AABB crop, Vec3 move) {
      delegate.assembleRaw(crop, move);
    }

    @Override
    public void assembleRaw(
      AABB crop,
      Vec3 move,
      QuadTransform... transforms
    ) {
      delegate.assembleRaw(crop, move, transforms);
    }
  }

  /**
   * Аффинное соответствие UV координатам Y/Z боковой плоскости.
   * UV берутся из исходных вершин до изменения геометрии.
   */
  private record SideUvPlane(
    double y0,
    double z0,
    double u0,
    double v0,
    double uY,
    double uZ,
    double vY,
    double vZ
  ) {
    float u(double y, double z) {
      return (float) (u0 + uY * (y - y0) + uZ * (z - z0));
    }

    float v(double y, double z) {
      return (float) (v0 + vY * (y - y0) + vZ * (z - z0));
    }

    @Nullable
    static SideUvPlane from(
      List<MutableVec3> originalPositions,
      MutableQuad quad,
      Direction.Axis sideAxis
    ) {
      // Решаем линейную систему по любой тройке неколлинеарных вершин.
      for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 4; j++) {
          for (int k = j + 1; k < 4; k++) {
            MutableVec3 a = originalPositions.get(i);
            MutableVec3 b = originalPositions.get(j);
            MutableVec3 c = originalPositions.get(k);

            double p1a = sideAxis == Direction.Axis.X ? a.y : a.x;
            double q1a = sideAxis == Direction.Axis.X ? a.z : a.y;
            double p1b = sideAxis == Direction.Axis.X ? b.y : b.x;
            double q1b = sideAxis == Direction.Axis.X ? b.z : b.y;
            double p1c = sideAxis == Direction.Axis.X ? c.y : c.x;
            double q1c = sideAxis == Direction.Axis.X ? c.z : c.y;

            double dp1 = p1b - p1a;
            double dq1 = q1b - q1a;
            double dp2 = p1c - p1a;
            double dq2 = q1c - q1a;

            double determinant = dp1 * dq2 - dp2 * dq1;
            if (Math.abs(determinant) < UV_DETERMINANT_EPSILON) {
              continue;
            }

            double u0 = quad.vertices.get(i).uv.u;
            double v0 = quad.vertices.get(i).uv.v;
            double u1 = quad.vertices.get(j).uv.u;
            double v1 = quad.vertices.get(j).uv.v;
            double u2 = quad.vertices.get(k).uv.u;
            double v2 = quad.vertices.get(k).uv.v;

            double uP =
              ((u1 - u0) * dq2 - (u2 - u0) * dq1) / determinant;
            double uQ =
              (dp1 * (u2 - u0) - dp2 * (u1 - u0)) / determinant;
            double vP =
              ((v1 - v0) * dq2 - (v2 - v0) * dq1) / determinant;
            double vQ =
              (dp1 * (v2 - v0) - dp2 * (v1 - v0)) / determinant;

            return new SideUvPlane(
              p1a, q1a, u0, v0, uP, uQ, vP, vQ
            );
          }
        }
      }

      return null;
    }
  }

  @Override
  public @NotNull List<BakedQuad> getQuads(
    @Nullable BlockState state,
    @Nullable Direction side,
    @NotNull RandomSource random
  ) {
    List<BakedQuad> quads =
      sourceModel.getQuads(state, side, random);

    if (state == null) {
      return quads;
    }

    if (shouldTransformSideSourceQuads(side)) {
      quads = transformSideSourceQuads(
        quads,
        getMaterialSideQuads(state)
      );
    }

    if (!shouldTransform(side)) {
      return quads;
    }

    return transformSurfaceQuads(
      quads,
      getMaterialSideQuads(state)
    );
  }

  @Override
  public List<BakedQuad> getQuads(
    BlockState state,
    Direction side,
    @NotNull RandomSource random,
    @NotNull ModelData data,
    RenderType renderType
  ) {
    List<BakedQuad> quads =
      sourceModel.getQuads(
        state,
        side,
        random,
        data,
        renderType
      );

    if (shouldTransformSideSourceQuads(side)) {
      quads = transformSideSourceQuads(
        quads,
        getMaterialSideQuads(
          state,
          data,
          renderType
        )
      );
    }

    if (!shouldTransform(side)) {
      return quads;
    }

    return transformSurfaceQuads(
      quads,
      getMaterialSideQuads(
        state,
        data,
        renderType
      )
    );
  }

  /**
   * В исходной геометрии assembleSlope боковые плоскости находятся
   * на WEST/EAST (X = 0 и X = 1). AssemblyTransform поворачивает
   * их вместе с остальной геометрией, поэтому FACING здесь не нужен.
   */
  private boolean shouldTransformSideSourceQuads(
    @Nullable Direction side
  ) {
    return mode == SlopeMode.SECOND
      && (side == null
        || side.getAxis() == slopeState
          .getValue(CopycatAdvSlopeBlock.FACING)
          .getAxis());
  }

  /**
   * Переносит UV и спрайт с исходной грани материала на шаблонные
   * боковые квады. Геометрическая обёртка затем применяет этот UV-map
   * только к секциям, содержащим QuadSlope(UP).
   */
  private List<BakedQuad> transformSideSourceQuads(
    List<BakedQuad> quads,
    List<BakedQuad> materialSideQuads
  ) {
    if (materialSideQuads.isEmpty()) {
      return quads;
    }

    Direction facing = slopeState.getValue(CopycatAdvSlopeBlock.FACING);
    Direction clockwise = facing.getClockWise();
    Direction counterClockwise = facing.getCounterClockWise();
    boolean ceiling =
      slopeState.getValue(CopycatAdvSlopeBlock.HALF) == Half.TOP;
    Direction materialSide = getMaterialSide();

    List<BakedQuad> result = new ArrayList<>(quads.size());

    for (BakedQuad quad : quads) {
      if (quad.getDirection() != clockwise
        && quad.getDirection() != counterClockwise) {
        result.add(quad);
        continue;
      }

      for (BakedQuad materialQuad : materialSideQuads) {
        int[] vertices = quad.getVertices().clone();
        UvPlane uvPlane = UvPlane.from(
          materialQuad,
          materialSide,
          ceiling
        );

        for (int i = 0; i < 4; i++) {
          Vec3 pos = BakedQuadHelper.getXYZ(vertices, i);
          double p = materialSide.getAxis() == Direction.Axis.X
            ? pos.y
            : pos.x;
          double q = materialSide.getAxis() == Direction.Axis.X
            ? pos.z
            : pos.y;
          BakedQuadHelper.setU(vertices, i, uvPlane.u(p, q));
          BakedQuadHelper.setV(vertices, i, uvPlane.v(p, q));
        }

        result.add(new BakedQuad(
          vertices,
          materialQuad.getTintIndex(),
          quad.getDirection(),
          materialQuad.getSprite(),
          quad.isShade()
        ));
      }
    }

    return result;
  }

  /**
   * Выбирает горизонтальную грань, используемую
   * как широкая наклонная поверхность.
   */
  private boolean shouldTransform(
    @Nullable Direction side
  ) {
    return mode == SlopeMode.SECOND
      && (side == null || side == getSurfaceFace());
  }

  private Direction getSurfaceFace() {
    return slopeState.getValue(
      CopycatAdvSlopeBlock.HALF
    ) == Half.TOP
      ? Direction.DOWN
      : Direction.UP;
  }

  private Direction getMaterialSide() {
    return slopeState
      .getValue(CopycatAdvSlopeBlock.FACING)
      .getOpposite();
  }

  private List<BakedQuad> getMaterialSideQuads(
    BlockState materialState
  ) {
    Direction face = getMaterialSide();

    List<BakedQuad> result = new ArrayList<>(
      sourceModel.getQuads(
        materialState,
        face,
        RandomSource.create(MATERIAL_RANDOM_SEED)
      )
    );

    for (BakedQuad quad : sourceModel.getQuads(
      materialState,
      null,
      RandomSource.create(MATERIAL_RANDOM_SEED)
    )) {
      if (quad.getDirection() == face) {
        result.add(quad);
      }
    }

    return result;
  }

  private List<BakedQuad> getMaterialSideQuads(
    BlockState materialState,
    ModelData data,
    RenderType renderType
  ) {
    Direction face = getMaterialSide();

    List<BakedQuad> result = new ArrayList<>(
      sourceModel.getQuads(
        materialState,
        face,
        RandomSource.create(MATERIAL_RANDOM_SEED),
        data,
        renderType
      )
    );

    for (BakedQuad quad : sourceModel.getQuads(
      materialState,
      null,
      RandomSource.create(MATERIAL_RANDOM_SEED),
      data,
      renderType
    )) {
      if (quad.getDirection() == face) {
        result.add(quad);
      }
    }

    return result;
  }

  /**
   * Переносит UV боковой грани материала на широкую наклонную поверхность.
   * Не участвует в диагностическом изменении UV торцевых секций.
   */
  private List<BakedQuad> transformSurfaceQuads(
    List<BakedQuad> quads,
    List<BakedQuad> sideQuads
  ) {
    if (mode != SlopeMode.SECOND || sideQuads.isEmpty()) {
      return quads;
    }

    Direction surfaceFace = getSurfaceFace();
    Direction materialSide = getMaterialSide();

    boolean ceiling =
      slopeState.getValue(
        CopycatAdvSlopeBlock.HALF
      ) == Half.TOP;

    List<BakedQuad> result =
      new ArrayList<>(quads.size());

    for (BakedQuad quad : quads) {
      if (quad.getDirection() != surfaceFace) {
        result.add(quad);
        continue;
      }

      for (BakedQuad sideQuad : sideQuads) {
        result.add(
          remapSurfaceFromSideQuad(
            quad,
            sideQuad,
            materialSide,
            ceiling
          )
        );
      }
    }

    return result;
  }

  private static BakedQuad remapSurfaceFromSideQuad(
    BakedQuad surfaceQuad,
    BakedQuad sideQuad,
    Direction materialSide,
    boolean ceiling
  ) {
    int[] vertices =
      surfaceQuad.getVertices().clone();

    UvPlane uvPlane =
      UvPlane.from(
        sideQuad,
        materialSide,
        ceiling
      );

    for (int i = 0; i < 4; i++) {
      Vec3 pos =
        BakedQuadHelper.getXYZ(vertices, i);

      BakedQuadHelper.setU(
        vertices,
        i,
        uvPlane.u(pos.x, pos.z)
      );

      BakedQuadHelper.setV(
        vertices,
        i,
        uvPlane.v(pos.x, pos.z)
      );
    }

    return new BakedQuad(
      vertices,
      sideQuad.getTintIndex(),
      surfaceQuad.getDirection(),
      sideQuad.getSprite(),
      surfaceQuad.isShade()
    );
  }

  @Override
  protected ModelData.Builder gatherModelData(
    ModelData.Builder builder,
    BlockAndTintGetter world,
    BlockPos pos,
    BlockState state,
    ModelData blockEntityData
  ) {
    return builder;
  }

  private record UvPlane(
    double p0,
    double q0,
    double u0,
    double v0,
    double uP,
    double uQ,
    double vP,
    double vQ,
    boolean flipU,
    boolean flipV,
    double minU,
    double maxU,
    double minV,
    double maxV
  ) {
    float u(double x, double z) {
      double value =
        u0 + uP * (x - p0) + uQ * (z - q0);

      if (flipU) {
        value = minU + maxU - value;
      }

      return (float) value;
    }

    float v(double x, double z) {
      double value =
        v0 + vP * (x - p0) + vQ * (z - q0);

      if (flipV) {
        value = minV + maxV - value;
      }

      return (float) value;
    }

    static UvPlane from(
      BakedQuad quad,
      Direction face,
      boolean ceiling
    ) {
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
        Vec3 pos =
          BakedQuadHelper.getXYZ(data, i);

        if (face.getAxis() == Direction.Axis.X) {
          p[i] = pos.y;
          q[i] = pos.z;
        } else if (face.getAxis() == Direction.Axis.Z) {
          p[i] = pos.x;
          q[i] = pos.y;
        } else {
          p[i] = pos.x;
          q[i] = pos.z;
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

            double determinant =
              dp1 * dq2 - dp2 * dq1;

            if (Math.abs(determinant) < UV_DETERMINANT_EPSILON) {
              continue;
            }

            double uP =
              ((u[j] - u[i]) * dq2
                - (u[k] - u[i]) * dq1)
                / determinant;

            double uQ =
              (dp1 * (u[k] - u[i])
                - dp2 * (u[j] - u[i]))
                / determinant;

            double vP =
              ((v[j] - v[i]) * dq2
                - (v[k] - v[i]) * dq1)
                / determinant;

            double vQ =
              (dp1 * (v[k] - v[i])
                - dp2 * (v[j] - v[i]))
                / determinant;

            return new UvPlane(
              p[i],
              q[i],
              u[i],
              v[i],
              uP,
              uQ,
              vP,
              vQ,
              flipU,
              flipV,
              minU,
              maxU,
              minV,
              maxV
            );
          }
        }
      }

      return new UvPlane(
        0,
        0,
        u[0],
        v[0],
        0,
        0,
        0,
        0,
        flipU,
        flipV,
        minU,
        maxU,
        minV,
        maxV
      );
    }
  }
}
