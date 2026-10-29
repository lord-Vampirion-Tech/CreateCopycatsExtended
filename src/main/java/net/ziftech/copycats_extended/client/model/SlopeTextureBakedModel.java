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
  private static final double GEOMETRY_EPSILON = 1.0E-5;

  private final BakedModel sourceModel;
  private final BlockState slopeState;
  private final SlopeMode mode;

  // ============================================================
  // ============================================================
// UV НАСТРОЙКИ: НАПРАВЛЕНИЕ БОКОВОЙ СТОРОНЫ × LEFT / RIGHT
//
// FLIP_U / FLIP_V:
// false = без отражения
// true  = отражение по соответствующей оси
//
// ROTATION:
// 0, 90, 180, 270
// ============================================================

  private static final boolean NORTH_LEFT_FLIP_U = false;
  private static final boolean NORTH_LEFT_FLIP_V = false;
  private static final int NORTH_LEFT_ROTATION = 0;

  private static final boolean NORTH_RIGHT_FLIP_U = false;
  private static final boolean NORTH_RIGHT_FLIP_V = false;
  private static final int NORTH_RIGHT_ROTATION = 0;


  private static final boolean SOUTH_LEFT_FLIP_U = false;
  private static final boolean SOUTH_LEFT_FLIP_V = false;
  private static final int SOUTH_LEFT_ROTATION = 0;

  private static final boolean SOUTH_RIGHT_FLIP_U = false;
  private static final boolean SOUTH_RIGHT_FLIP_V = false;
  private static final int SOUTH_RIGHT_ROTATION = 0;


  private static final boolean EAST_LEFT_FLIP_U = false;
  private static final boolean EAST_LEFT_FLIP_V = false;
  private static final int EAST_LEFT_ROTATION = 0;

  private static final boolean EAST_RIGHT_FLIP_U = false;
  private static final boolean EAST_RIGHT_FLIP_V = false;
  private static final int EAST_RIGHT_ROTATION = 0;


  private static final boolean WEST_LEFT_FLIP_U = false;
  private static final boolean WEST_LEFT_FLIP_V = false;
  private static final int WEST_LEFT_ROTATION = 0;

  private static final boolean WEST_RIGHT_FLIP_U = false;
  private static final boolean WEST_RIGHT_FLIP_V = false;
  private static final int WEST_RIGHT_ROTATION = 0;

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

  // ============================================================
  // НАСТРОЙКИ ДЛЯ КОНКРЕТНОГО QUAD
  // ============================================================

  private record UvSettings(
    boolean flipU,
    boolean flipV,
    int rotation
  ) {
  }

  private enum SlopeSide {
    LEFT,
    RIGHT
  }

  private static UvSettings getUvSettings(
    @Nullable Direction side,
    SlopeSide slopeSide
  ) {
    if (side == null) {
      return new UvSettings(false, false, 0);
    }

    return switch (side) {
      case NORTH -> slopeSide == SlopeSide.LEFT
        ? new UvSettings(NORTH_LEFT_FLIP_U, NORTH_LEFT_FLIP_V, NORTH_LEFT_ROTATION)
        : new UvSettings(NORTH_RIGHT_FLIP_U, NORTH_RIGHT_FLIP_V, NORTH_RIGHT_ROTATION);

      case SOUTH -> slopeSide == SlopeSide.LEFT
        ? new UvSettings(SOUTH_LEFT_FLIP_U, SOUTH_LEFT_FLIP_V, SOUTH_LEFT_ROTATION)
        : new UvSettings(SOUTH_RIGHT_FLIP_U, SOUTH_RIGHT_FLIP_V, SOUTH_RIGHT_ROTATION);

      case EAST -> slopeSide == SlopeSide.LEFT
        ? new UvSettings(EAST_LEFT_FLIP_U, EAST_LEFT_FLIP_V, EAST_LEFT_ROTATION)
        : new UvSettings(EAST_RIGHT_FLIP_U, EAST_RIGHT_FLIP_V, EAST_RIGHT_ROTATION);

      case WEST -> slopeSide == SlopeSide.LEFT
        ? new UvSettings(WEST_LEFT_FLIP_U, WEST_LEFT_FLIP_V, WEST_LEFT_ROTATION)
        : new UvSettings(WEST_RIGHT_FLIP_U, WEST_RIGHT_FLIP_V, WEST_RIGHT_ROTATION);

      default -> new UvSettings(false, false, 0);
    };
  }

  /**
   * Применяет отражение и поворот к нормализованным UV.
   * Координаты UV здесь локальные: примерно от 0 до 1.
   */
  private static float[] transformUv(
    double u,
    double v,
    UvSettings settings
  ) {
    if (settings.flipU()) {
      u = 1.0 - u;
    }

    if (settings.flipV()) {
      v = 1.0 - v;
    }

    // Поворот выполняется вокруг центра UV-квадрата.
    double x = u - 0.5;
    double y = v - 0.5;

    int rotation = Math.floorMod(settings.rotation(), 360);

    double rotatedU;
    double rotatedV;

    switch (rotation) {
      case 90 -> {
        rotatedU = 0.5 - y;
        rotatedV = 0.5 + x;
      }

      case 180 -> {
        rotatedU = 0.5 - x;
        rotatedV = 0.5 - y;
      }

      case 270 -> {
        rotatedU = 0.5 + y;
        rotatedV = 0.5 - x;
      }

      default -> {
        rotatedU = u;
        rotatedV = v;
      }
    }

    return new float[]{
      (float) rotatedU,
      (float) rotatedV
    };
  }

  // ============================================================
  // ОПРЕДЕЛЕНИЕ ТРАПЕЦИЕВИДНОГО QUAD
  // ============================================================

  /**
   * Проверяет, является ли QUAD трапециевидной частью боковой
   * поверхности.
   * <p>
   * На боковой плоскости сравнивается горизонтальная ширина
   * фигуры на нижнем и верхнем уровнях.
   * <p>
   * Если ширина различается, фигура классифицируется как трапеция.
   * Прямоугольные QUAD относятся к OTHER.
   * <p>
   * Важно: классификация зависит от фактической геометрии модели.
   */
  private static boolean isTrapezoidQuad(
    MutableQuad quad,
    Direction side
  ) {
    if (side == null || quad.vertices.size() != 4) {
      return false;
    }

    List<MutableVec3> transformedPositions = new ArrayList<>(4);

    for (var vertex : quad.vertices) {
      transformedPositions.add(vertex.xyz);
    }

    return isTrapezoidQuad(transformedPositions, side);
  }

  private static boolean isTrapezoidQuad(
    List<MutableVec3> positions,
    Direction side
  ) {
    if (side == null || positions.size() != 4) {
      return false;
    }

    if (side.getAxis() != Direction.Axis.X
      && side.getAxis() != Direction.Axis.Z) {
      return false;
    }

    double minY = Double.POSITIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;

    for (MutableVec3 pos : positions) {
      minY = Math.min(minY, pos.y);
      maxY = Math.max(maxY, pos.y);
    }

    if (maxY - minY < GEOMETRY_EPSILON) {
      return false;
    }

    double lowerMin = Double.POSITIVE_INFINITY;
    double lowerMax = Double.NEGATIVE_INFINITY;
    double upperMin = Double.POSITIVE_INFINITY;
    double upperMax = Double.NEGATIVE_INFINITY;

    int lowerCount = 0;
    int upperCount = 0;

    for (MutableVec3 pos : positions) {
      double horizontal = side.getAxis() == Direction.Axis.X
        ? pos.z
        : pos.x;

      if (Math.abs(pos.y - minY) < GEOMETRY_EPSILON) {
        lowerMin = Math.min(lowerMin, horizontal);
        lowerMax = Math.max(lowerMax, horizontal);
        lowerCount++;
      }

      if (Math.abs(pos.y - maxY) < GEOMETRY_EPSILON) {
        upperMin = Math.min(upperMin, horizontal);
        upperMax = Math.max(upperMax, horizontal);
        upperCount++;
      }
    }

    if (lowerCount < 2 || upperCount < 2) {
      return false;
    }

    double lowerWidth = lowerMax - lowerMin;
    double upperWidth = upperMax - upperMin;

    return Math.abs(lowerWidth - upperWidth) > GEOMETRY_EPSILON;
  }

  private static boolean isTrapezoidQuad(
    BakedQuad quad,
    Direction side
  ) {
    if (side == null) {
      return false;
    }

    int[] vertices = quad.getVertices();
    List<MutableVec3> positions = new ArrayList<>(4);

    for (int i = 0; i < 4; i++) {
      Vec3 pos = BakedQuadHelper.getXYZ(vertices, i);

      positions.add(new MutableVec3(pos.x, pos.y, pos.z));
    }

    return isTrapezoidQuad(positions, side);
  }

  // ============================================================
  // КОНТЕКСТ СБОРКИ ГЕОМЕТРИИ СКОСА
  // ============================================================

  public static CopycatRenderContext wrapAssemblyContext(
    CopycatRenderContext delegate,
    Direction facing
  ) {
    return new TriangleSideUvContext(delegate, facing);
  }

  private static boolean hasSlopeTransform(QuadUVUpdate uvUpdate) {
    return Arrays.stream(uvUpdate.transforms())
      .anyMatch(transform ->
        transform instanceof QuadSlope slope
          && (slope.face() == Direction.UP
          || slope.face() == Direction.DOWN)
      );
  }

  @Nullable
  private static Direction.Axis getTargetSideAxis(MutableQuad quad) {
    MutableVec3 normal = quad.computeFaceNormal();

    if (Math.abs(normal.x) > 0.99
      && Math.abs(normal.z) < 0.05) {
      return Direction.Axis.X;
    }

    if (Math.abs(normal.z) > 0.99
      && Math.abs(normal.x) < 0.05) {
      return Direction.Axis.Z;
    }

    return null;
  }

  @Nullable
  private static Direction.Axis getSourceSideAxis(
    List<MutableVec3> positions
  ) {
    double minX = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    double minZ = Double.POSITIVE_INFINITY;
    double maxZ = Double.NEGATIVE_INFINITY;

    for (MutableVec3 position : positions) {
      minX = Math.min(minX, position.x);
      maxX = Math.max(maxX, position.x);
      minZ = Math.min(minZ, position.z);
      maxZ = Math.max(maxZ, position.z);
    }

    boolean xPlane = maxX - minX < GEOMETRY_EPSILON;
    boolean zPlane = maxZ - minZ < GEOMETRY_EPSILON;

    if (xPlane == zPlane) {
      return null;
    }

    return xPlane ? Direction.Axis.X : Direction.Axis.Z;
  }

  private static QuadTransform remapSideUv(
    QuadUVUpdate original,
    Direction facing
  ) {
    return (quad, sprite) -> {
      List<MutableVec3> originalPositions = new ArrayList<>(4);
      float[] originalU = new float[4];
      float[] originalV = new float[4];

      for (int i = 0; i < 4; i++) {
        var vertex = quad.vertices.get(i);

        originalPositions.add(vertex.xyz.copy());
        originalU[i] = vertex.uv.u;
        originalV[i] = vertex.uv.v;
      }

      for (QuadTransform transform : original.transforms()) {
        transform.transformQuad(quad, sprite);
      }

      Direction side = getQuadSide(quad);

      if (side == null
        || (side.getAxis() != Direction.Axis.X
        && side.getAxis() != Direction.Axis.Z)
        || !isTrapezoidQuad(quad, side)) {
        return true;
      }

      Direction.Axis targetAxis = getTargetSideAxis(quad);
      Direction.Axis sourceAxis = getSourceSideAxis(originalPositions);

      if (targetAxis == null || sourceAxis == null) {
        return true;
      }

      SideUvPlane plane = SideUvPlane.from(
        originalPositions,
        originalU,
        originalV,
        sourceAxis
      );

      if (plane == null) {
        return true;
      }

      UvSettings settings = getUvSettings(
        side,
        getSlopeSide(side, facing)
      );

      double[] us = new double[4];
      double[] vs = new double[4];

      double minU = Double.POSITIVE_INFINITY;
      double maxU = Double.NEGATIVE_INFINITY;
      double minV = Double.POSITIVE_INFINITY;
      double maxV = Double.NEGATIVE_INFINITY;

      for (int i = 0; i < 4; i++) {
        MutableVec3 position = quad.vertices.get(i).xyz;

        double p = targetAxis == Direction.Axis.X
          ? position.y
          : position.x;

        double q = targetAxis == Direction.Axis.X
          ? position.z
          : position.y;

        us[i] = plane.u(p, q);
        vs[i] = plane.v(p, q);

        minU = Math.min(minU, us[i]);
        maxU = Math.max(maxU, us[i]);
        minV = Math.min(minV, vs[i]);
        maxV = Math.max(maxV, vs[i]);
      }

      double width = maxU - minU;
      double height = maxV - minV;

      if (width < 1.0E-8 || height < 1.0E-8) {
        return true;
      }

      for (int i = 0; i < 4; i++) {
        double u = (us[i] - minU) / width;
        double v = (vs[i] - minV) / height;

        float[] transformed = transformUv(u, v, settings);

        quad.vertices.get(i).uv.set(
          (float) (minU + transformed[0] * width),
          (float) (minV + transformed[1] * height)
        );
      }

      return true;
    };
  }
  private static QuadTransform[] adjustTransforms(
    QuadTransform[] transforms,
    Direction facing,
    AssemblyPieceType pieceType
  ) {
    // Для обычных участков исходные трансформации остаются без изменений.
    if (pieceType != AssemblyPieceType.SLOPE_SIDE_CANDIDATE) {
      return transforms;
    }

    QuadTransform[] adjusted = transforms.clone();
    boolean changed = false;

    for (int i = 0; i < adjusted.length; i++) {
      if (adjusted[i] instanceof QuadUVUpdate uvUpdate
        && hasSlopeTransform(uvUpdate)) {

        adjusted[i] = remapSideUv(uvUpdate, facing);
        changed = true;
      }
    }

    return changed ? adjusted : transforms;
  }

  private enum AssemblyPieceType {
    /**
     * Участок не предназначен для специальной коррекции UV.
     */
    DEFAULT,

    /**
     * Участок содержит преобразование наклонной поверхности.
     * Коррекция дополнительно проверяет направление и геометрию QUAD.
     */
    SLOPE_SIDE_CANDIDATE
  }

  private static class TriangleSideUvContext
    implements CopycatRenderContext {

    private final CopycatRenderContext delegate;
    private final Direction facing;

    private TriangleSideUvContext(
      CopycatRenderContext delegate,
      Direction facing
    ) {
      this.delegate = delegate;
      this.facing = facing;
    }

    @Override
    public void assemblePiece(
      AssemblyTransform assemblyTransform,
      MutableVec3 offset,
      MutableAABB select,
      MutableCullFace cull
    ) {
      // Вызов без дополнительных QuadTransform не перехватываем.
      delegate.assemblePiece(
        assemblyTransform,
        offset,
        select,
        cull
      );
    }

    @Override
    public void assemblePiece(
      AssemblyTransform assemblyTransform,
      MutableVec3 offset,
      MutableAABB select,
      MutableCullFace cull,
      QuadTransform... transforms
    ) {
      AssemblyPieceType pieceType = classifyPiece(transforms);

      delegate.assemblePiece(
        assemblyTransform,
        offset,
        select,
        cull,
        adjustTransforms(
          transforms,
          facing,
          pieceType
        )
      );
    }

    /**
     * Определяет, может ли участок содержать боковые грани,
     * которым требуется специальная обработка UV.
     *
     * Это предварительная классификация участка, а не утверждение,
     * что все его QUAD нужно преобразовывать.
     */
    private static AssemblyPieceType classifyPiece(
      QuadTransform[] transforms
    ) {
      for (QuadTransform transform : transforms) {
        if (transform instanceof QuadUVUpdate uvUpdate
          && hasSlopeTransform(uvUpdate)) {
          return AssemblyPieceType.SLOPE_SIDE_CANDIDATE;
        }
      }

      return AssemblyPieceType.DEFAULT;
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
      return (float) (
        u0 + uY * (y - y0) + uZ * (z - z0)
      );
    }

    float v(double y, double z) {
      return (float) (
        v0 + vY * (y - y0) + vZ * (z - z0)
      );
    }

    @Nullable
    static SideUvPlane from(
      List<MutableVec3> originalPositions,
      float[] originalU,
      float[] originalV,
      Direction.Axis sourceAxis
    ) {
      for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 4; j++) {
          for (int k = j + 1; k < 4; k++) {
            MutableVec3 a = originalPositions.get(i);
            MutableVec3 b = originalPositions.get(j);
            MutableVec3 c = originalPositions.get(k);

            double pA = sourceAxis == Direction.Axis.X ? a.y : a.x;
            double qA = sourceAxis == Direction.Axis.X ? a.z : a.y;
            double pB = sourceAxis == Direction.Axis.X ? b.y : b.x;
            double qB = sourceAxis == Direction.Axis.X ? b.z : b.y;
            double pC = sourceAxis == Direction.Axis.X ? c.y : c.x;
            double qC = sourceAxis == Direction.Axis.X ? c.z : c.y;

            double dp1 = pB - pA;
            double dq1 = qB - qA;
            double dp2 = pC - pA;
            double dq2 = qC - qA;

            double determinant = dp1 * dq2 - dp2 * dq1;

            if (Math.abs(determinant) < UV_DETERMINANT_EPSILON) {
              continue;
            }

            double uA = originalU[i];
            double vA = originalV[i];
            double uB = originalU[j];
            double vB = originalV[j];
            double uC = originalU[k];
            double vC = originalV[k];

            double uP =
              ((uB - uA) * dq2 - (uC - uA) * dq1) / determinant;

            double uQ =
              (dp1 * (uC - uA) - dp2 * (uB - uA)) / determinant;

            double vP =
              ((vB - vA) * dq2 - (vC - vA) * dq1) / determinant;

            double vQ =
              (dp1 * (vC - vA) - dp2 * (vB - vA)) / determinant;

            return new SideUvPlane(
              pA, qA, uA, vA, uP, uQ, vP, vQ
            );
          }
        }
      }

      return null;
    }
  }

  // ============================================================
  // ПОЛУЧЕНИЕ QUAD
  // ============================================================

  @Override
  public @NotNull List<BakedQuad> getQuads(
    @Nullable BlockState state,
    @Nullable Direction side,
    @NotNull RandomSource random
  ) {
    List<BakedQuad> quads = sourceModel.getQuads(
      state,
      side,
      random
    );

    if (state == null) {
      return quads;

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
    List<BakedQuad> quads = sourceModel.getQuads(
      state,
      side,
      random,
      data,
      renderType
    );

    if (!shouldTransform(side)) {
      return quads;
    }

    return transformSurfaceQuads(
      quads,
      getMaterialSideQuads(state, data, renderType)
    );
  }

  private boolean shouldTransformSideSourceQuads(
    @Nullable Direction side
  ) {
    if (mode != SlopeMode.SECOND || side == null) {
      return mode == SlopeMode.SECOND;
    }

    Direction facing = slopeState.getValue(
      CopycatAdvSlopeBlock.FACING
    );

    return side == facing.getClockWise()
      || side == facing.getCounterClockWise();
  }

  // ============================================================
  // ПРЕОБРАЗОВАНИЕ БОКОВЫХ QUAD
  // ============================================================

  private List<BakedQuad> transformSideSourceQuads(
    List<BakedQuad> quads,
    List<BakedQuad> materialSideQuads
  ) {
    if (materialSideQuads.isEmpty()) {
      return quads;
    }

    Direction facing = slopeState.getValue(
      CopycatAdvSlopeBlock.FACING
    );

    Direction clockwise = facing.getClockWise();
    Direction counterClockwise = facing.getCounterClockWise();

    Direction materialSide = getMaterialSide();

    BakedQuad materialQuad = materialSideQuads.get(0);

    SideMaterialUvPlane uvPlane = SideMaterialUvPlane.from(
      materialQuad,
      materialSide
    );

    if (uvPlane == null) {
      return quads;
    }

    List<BakedQuad> result = new ArrayList<>(quads.size());

    for (BakedQuad quad : quads) {
      Direction quadDirection = quad.getDirection();

      if (quadDirection != clockwise
        && quadDirection != counterClockwise) {
        result.add(quad);
        continue;
      }

      int[] vertices = quad.getVertices().clone();

      // Сохраняем исходный спрайт этого QUAD.
      TextureAtlasSprite targetSprite = quad.getSprite();

      double spriteU0 = targetSprite.getU0();
      double spriteUSize = targetSprite.getU1() - targetSprite.getU0();

      double spriteV0 = targetSprite.getV0();
      double spriteVSize = targetSprite.getV1() - targetSprite.getV0();

      Direction.Axis targetAxis = quadDirection.getAxis();

      SlopeSide slopeSide = getSlopeSide(
        quadDirection,
        slopeState.getValue(CopycatAdvSlopeBlock.FACING)
      );

      UvSettings settings = getUvSettings(
        quadDirection,
        slopeSide
      );

      for (int i = 0; i < 4; i++) {
        Vec3 pos = BakedQuadHelper.getXYZ(vertices, i);

        double p;
        double q;

        if (materialSide.getAxis() == Direction.Axis.X) {
          p = pos.y;
          q = targetAxis == Direction.Axis.X
            ? pos.z
            : pos.x;
        } else {
          p = targetAxis == Direction.Axis.Z
            ? pos.x
            : pos.z;
          q = pos.y;
        }

        double localU = uvPlane.u(p, q);
        double localV = uvPlane.v(p, q);

        // Только отражение и поворот — без масштаба и смещения.
        float[] transformed = transformUv(
          localU,
          localV,
          settings
        );

        float atlasU = (float) (
          spriteU0 + transformed[0] * spriteUSize
        );

        float atlasV = (float) (
          spriteV0 + transformed[1] * spriteVSize
        );

        BakedQuadHelper.setU(vertices, i, atlasU);
        BakedQuadHelper.setV(vertices, i, atlasV);
      }

      result.add(new BakedQuad(
        vertices,
        quad.getTintIndex(),
        quad.getDirection(),
        targetSprite,
        quad.isShade()
      ));
    }

    return result;
  }

  private record SideMaterialUvPlane(
    double p0,
    double q0,
    double u0,
    double v0,
    double uP,
    double uQ,
    double vP,
    double vQ
  ) {
    float u(double p, double q) {
      return (float) (
        u0 + uP * (p - p0) + uQ * (q - q0)
      );
    }

    float v(double p, double q) {
      return (float) (
        v0 + vP * (p - p0) + vQ * (q - q0)
      );
    }

    @Nullable
    static SideMaterialUvPlane from(
      BakedQuad quad,
      Direction face
    ) {
      int[] data = quad.getVertices();
      TextureAtlasSprite sprite = quad.getSprite();

      double[] p = new double[4];
      double[] q = new double[4];
      double[] u = new double[4];
      double[] v = new double[4];

      double uSize = sprite.getU1() - sprite.getU0();
      double vSize = sprite.getV1() - sprite.getV0();

      if (Math.abs(uSize) < 1.0E-12
        || Math.abs(vSize) < 1.0E-12) {
        return null;
      }

      for (int i = 0; i < 4; i++) {
        Vec3 pos = BakedQuadHelper.getXYZ(data, i);

        if (face.getAxis() == Direction.Axis.X) {
          p[i] = pos.y;
          q[i] = pos.z;
        } else if (face.getAxis() == Direction.Axis.Z) {
          p[i] = pos.x;
          q[i] = pos.y;
        } else {
          return null;
        }

        u[i] = (
          BakedQuadHelper.getU(data, i) - sprite.getU0()
        ) / uSize;

        v[i] = (
          BakedQuadHelper.getV(data, i) - sprite.getV0()
        ) / vSize;
      }

      for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 4; j++) {
          for (int k = j + 1; k < 4; k++) {
            double dp1 = p[j] - p[i];
            double dq1 = q[j] - q[i];
            double dp2 = p[k] - p[i];
            double dq2 = q[k] - q[i];

            double determinant = dp1 * dq2 - dp2 * dq1;

            if (Math.abs(determinant) < UV_DETERMINANT_EPSILON) {
              continue;
            }

            double uP =
              ((u[j] - u[i]) * dq2
                - (u[k] - u[i]) * dq1) / determinant;

            double uQ =
              (dp1 * (u[k] - u[i])
                - dp2 * (u[j] - u[i])) / determinant;

            double vP =
              ((v[j] - v[i]) * dq2
                - (v[k] - v[i]) * dq1) / determinant;

            double vQ =
              (dp1 * (v[k] - v[i])
                - dp2 * (v[j] - v[i])) / determinant;

            return new SideMaterialUvPlane(
              p[i],
              q[i],
              u[i],
              v[i],
              uP,
              uQ,
              vP,
              vQ
            );
          }
        }
      }

      return null;
    }
  }

  // ============================================================
  // ШИРОКАЯ НАКЛОННАЯ ПОВЕРХНОСТЬ
  // ============================================================

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
    return slopeState.getValue(
      CopycatAdvSlopeBlock.FACING
    ).getOpposite();
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

  private List<BakedQuad> transformSurfaceQuads(
    List<BakedQuad> quads,
    List<BakedQuad> sideQuads
  ) {
    if (mode != SlopeMode.SECOND || sideQuads.isEmpty()) {
      return quads;
    }

    Direction surfaceFace = getSurfaceFace();
    Direction materialSide = getMaterialSide();

    boolean ceiling = slopeState.getValue(
      CopycatAdvSlopeBlock.HALF
    ) == Half.TOP;

    List<BakedQuad> result = new ArrayList<>(quads.size());

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
    int[] vertices = surfaceQuad.getVertices().clone();

    UvPlane uvPlane = UvPlane.from(
      sideQuad,
      materialSide,
      ceiling
    );

    for (int i = 0; i < 4; i++) {
      Vec3 pos = BakedQuadHelper.getXYZ(vertices, i);

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
        Vec3 pos = BakedQuadHelper.getXYZ(data, i);

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

            double determinant = dp1 * dq2 - dp2 * dq1;

            if (Math.abs(determinant) < UV_DETERMINANT_EPSILON) {
              continue;
            }

            double uP =
              ((u[j] - u[i]) * dq2
                - (u[k] - u[i]) * dq1) / determinant;

            double uQ =
              (dp1 * (u[k] - u[i])
                - dp2 * (u[j] - u[i])) / determinant;

            double vP =
              ((v[j] - v[i]) * dq2
                - (v[k] - v[i]) * dq1) / determinant;

            double vQ =
              (dp1 * (v[k] - v[i])
                - dp2 * (v[j] - v[i])) / determinant;

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

  private static SlopeSide getSlopeSide(
    Direction side,
    Direction facing
  ) {
    if (side == null || facing == null) {
      return SlopeSide.LEFT;
    }

    Direction broadFacing = facing.getOpposite();

    if (side == broadFacing.getCounterClockWise()) {
      return SlopeSide.LEFT;
    }

    if (side == broadFacing.getClockWise()) {
      return SlopeSide.RIGHT;
    }

    return SlopeSide.LEFT;
  }

  private static Direction getQuadSide(MutableQuad quad) {
    MutableVec3 normal = quad.computeFaceNormal();

    if (Math.abs(normal.x) > 0.99 && Math.abs(normal.z) < 0.05) {
      return normal.x > 0 ? Direction.EAST : Direction.WEST;
    }

    if (Math.abs(normal.z) > 0.99 && Math.abs(normal.x) < 0.05) {
      return normal.z > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    return null;
  }
}