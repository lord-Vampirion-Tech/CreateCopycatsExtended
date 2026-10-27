package net.ziftech.copycats_extended.client.model;

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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

import net.neoforged.neoforge.client.model.data.ModelData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.SlopeMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlopeTextureBakedModel extends BakedModelWrapperWithData {

  private static final double SPLIT_X = 0.5;
  private static final double EPSILON = 1.0E-7;

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

  @Override
  public @NotNull List<BakedQuad> getQuads(
    @Nullable BlockState state,
    @Nullable Direction side,
    @NotNull RandomSource random
  ) {
    List<BakedQuad> quads = sourceModel.getQuads(state, side, random);
    debugQuads(state, side, quads);

    if (state == null || (side != null && side != getSurfaceFace())) {
      return quads;
    }

    return transformSurfaceQuads(quads, getMaterialSideQuads(state));
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
      sourceModel.getQuads(state, side, random, data, renderType);

    debugQuads(state, side, quads);

    if (side != null && side != getSurfaceFace()) {
      return quads;
    }

    return transformSurfaceQuads(
      quads,
      getMaterialSideQuads(state, data, renderType)
    );
  }

  /**
   * Определяет исходную грань материала, которая станет наклонной:
   * пол использует UP, потолок — DOWN после переворота модели.
   */
  private Direction getSurfaceFace() {
    return slopeState.getValue(CopycatAdvSlopeBlock.HALF) == Half.TOP ? Direction.DOWN : Direction.UP;
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
        RandomSource.create(42L)
      )
    );

    for (BakedQuad quad : sourceModel.getQuads(
      materialState,
      null,
      RandomSource.create(42L)
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
        RandomSource.create(42L),
        data,
        renderType
      )
    );

    for (BakedQuad quad : sourceModel.getQuads(
      materialState,
      null,
      RandomSource.create(42L),
      data,
      renderType
    )) {
      if (quad.getDirection() == face) {
        result.add(quad);
      }
    }

    return result;
  }

  private List<BakedQuad> getMaterialSideQuads(
    BlockState materialState,
    RandomSource random,
    ModelData data,
    RenderType renderType
  ) {
    Direction face = getMaterialSide();

    List<BakedQuad> quads = sourceModel.getQuads(
      materialState,
      face,
      RandomSource.create(42L),
      data,
      renderType
    );

    if (!quads.isEmpty()) {
      return quads;
    }

    return sourceModel.getQuads(
        materialState,
        null,
        RandomSource.create(42L),
        data,
        renderType
      ).stream()
      .filter(quad -> quad.getDirection() == face)
      .toList();
  }

  private List<BakedQuad> transformSurfaceQuads(
    List<BakedQuad> quads,
    List<BakedQuad> sideQuads
  ) {
    System.out.println(
      "[SlopeTexture] transform: mode=" + mode
        + ", inputQuads=" + quads.size()
        + ", sideQuads=" + sideQuads.size()
        + ", surfaceFace=" + getSurfaceFace()
        + ", materialSide=" + getMaterialSide()
    );

    if (mode != SlopeMode.SECOND || sideQuads.isEmpty()) {
      System.out.println("[SlopeTexture] transform skipped");
      return quads;
    }

    Direction surfaceFace = getSurfaceFace();
    Direction materialSide = getMaterialSide();

    List<BakedQuad> result = new ArrayList<>();

    for (BakedQuad quad : quads) {
      System.out.println(
        "[SlopeTexture] quad direction=" + quad.getDirection()
          + ", sprite=" + quad.getSprite().contents().name()
      );

      if (quad.getDirection() != surfaceFace) {
        result.add(quad);
        continue;
      }

      for (BakedQuad sideQuad : sideQuads) {
        System.out.println(
          "[SlopeTexture] source sprite="
            + sideQuad.getSprite().contents().name()
        );

        result.add(
          remapSurfaceFromSideQuad(
            quad,
            sideQuad,
            materialSide,
            slopeState.getValue(CopycatAdvSlopeBlock.HALF) == Half.TOP
          )
        );
      }
    }

    System.out.println("[SlopeTexture] outputQuads=" + result.size());
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

      // На исходной верхней грани X и Z задают координаты
      // широкой поверхности скоса.
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
      sideQuad.isShade()
    );
  }

  /**
   * Восстанавливает зависимость UV от координат на боковой грани.
   * <p>
   * Для X-грани её координаты — Y и Z.
   * Для Z-грани её координаты — X и Y.
   * <p>
   * Мы проецируем координаты X и Z верхней грани скоса
   * на эти две оси. Поэтому текстура поворачивается согласно
   * реальной ориентации грани материала, а не условию FACING.Axis.
   * <p>
   * Используются абсолютные UV из исходного quad-а, поэтому
   * сдвиги текстурных координат CTM не теряются.
   */
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
      double value = u0 + uP * (x - p0) + uQ * (z - q0);

      if (flipU) {
        value = minU + maxU - value;
      }

      return (float) value;
    }

    float v(double x, double z) {
      double value = v0 + vP * (x - p0) + vQ * (z - q0);

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

      // По умолчанию поворачиваем текстуру на 180°.
      boolean flipU = true;
      boolean flipV = true;

      if (face == Direction.NORTH || face == Direction.WEST) {
        flipU = false;
        flipV = false;
      }
      if (face == Direction.EAST || face == Direction.SOUTH) {
        flipU = false;
      }

      // Потолочный скос: инвертируем V относительно напольного.
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

            double det = dp1 * dq2 - dp2 * dq1;

            if (Math.abs(det) < 1.0E-8) {
              continue;
            }

            double uP =
              ((u[j] - u[i]) * dq2
                - (u[k] - u[i]) * dq1) / det;

            double uQ =
              (dp1 * (u[k] - u[i])
                - dp2 * (u[j] - u[i])) / det;

            double vP =
              ((v[j] - v[i]) * dq2
                - (v[k] - v[i]) * dq1) / det;

            double vQ =
              (dp1 * (v[k] - v[i])
                - dp2 * (v[j] - v[i])) / det;

            return new UvPlane(
              p[i], q[i], u[i], v[i],
              uP, uQ, vP, vQ,
              flipU, flipV,
              minU, maxU, minV, maxV
            );
          }
        }
      }

      return new UvPlane(
        0, 0, u[0], v[0],
        0, 0, 0, 0,
        flipU, flipV,
        minU, maxU, minV, maxV
      );
    }
  }

  private void debugQuads(
    BlockState state,
    Direction side,
    List<BakedQuad> quads
  ) {
    System.out.println("[SlopeTexture] debugQuads CALLED");
    System.out.println("[SlopeTexture] render state = " + state);
    System.out.println("[SlopeTexture] mode = " + mode);
    System.out.println("[SlopeTexture] slopeState = " + slopeState);

    if (mode != SlopeMode.SECOND) {
      return;
    }

    if (slopeState == null
      || !slopeState.hasProperty(CopycatAdvSlopeBlock.FACING)
      || !slopeState.hasProperty(CopycatAdvSlopeBlock.HALF)) {
      System.out.println("[SlopeTexture] EXIT: invalid slopeState");
      return;
    }

    System.out.println(
      "[SlopeTexture] FACING="
        + slopeState.getValue(CopycatAdvSlopeBlock.FACING)
        + ", HALF="
        + slopeState.getValue(CopycatAdvSlopeBlock.HALF)
        + ", requestedSide="
        + side
        + ", quads="
        + quads.size()
    );
  }

  /**
   * Ищет боковую текстуру в направлении скоса.
   * Если подходящей грани нет, пробует остальные горизонтальные грани.
   */
  @Nullable
  private TextureAtlasSprite findSideSprite(
    QuadLookup lookup,
    List<BakedQuad> generalQuads
  ) {
    Direction facing =
      slopeState.getValue(CopycatAdvSlopeBlock.FACING);

    Direction[] directions = {
      facing,
      facing.getOpposite(),
      facing.getClockWise(),
      facing.getCounterClockWise()
    };

    for (Direction direction : directions) {
      List<BakedQuad> candidates = lookup.get(direction);

      for (BakedQuad quad : candidates) {
        if (quad.getSprite() != null) {
          return quad.getSprite();
        }
      }
    }

    // Резервный вариант для моделей, которые хранят грани
    // в общем списке quads без отдельного cull-face.
    for (BakedQuad quad : generalQuads) {
      if (quad.getDirection().getAxis() != Direction.Axis.Y) {
        return quad.getSprite();
      }
    }

    return null;
  }

  /**
   * Меняет спрайт, сохраняя относительное положение UV внутри текстуры.
   */
  private static BakedQuad remapSprite(
    BakedQuad surfaceQuad,
    BakedQuad sideQuad,
    boolean rotate90
  ) {
    TextureAtlasSprite sourceSprite = surfaceQuad.getSprite();
    TextureAtlasSprite targetSprite = sideQuad.getSprite();

    List<Vertex> vertices = readVertices(surfaceQuad);

    int[] packed = packVertices(
      vertices,
      surfaceQuad,
      sourceSprite,
      targetSprite,
      rotate90
    );

    return new BakedQuad(
      packed,
      sideQuad.getTintIndex(),
      surfaceQuad.getDirection(),
      targetSprite,
      sideQuad.isShade()
    );
  }

  /**
   * Делит исходный quad по вертикальной линии X = 0.5.
   * <p>
   * Левая половина использует боковую текстуру.
   * Правая половина сохраняет оригинальный спрайт.
   */
  private static List<BakedQuad> splitQuad(
    BakedQuad quad,
    TextureAtlasSprite sideSprite
  ) {
    List<Vertex> original = readVertices(quad);

    List<Vertex> left = clipByX(original, true);
    List<Vertex> right = clipByX(original, false);

    List<BakedQuad> result = new ArrayList<>();

    result.addAll(
      makePolygonQuads(left, quad, sideSprite)
    );

    result.addAll(
      makePolygonQuads(right, quad, quad.getSprite())
    );

    return result;
  }

  /**
   * Обрезает полигон по координате X = 0.5.
   * <p>
   * keepLeft == true  : X <= 0.5
   * keepLeft == false : X >= 0.5
   */
  private static List<Vertex> clipByX(
    List<Vertex> input,
    boolean keepLeft
  ) {
    List<Vertex> output = new ArrayList<>();

    if (input.isEmpty()) {
      return output;
    }

    Vertex previous = input.get(input.size() - 1);
    boolean previousInside = isInside(previous, keepLeft);

    for (Vertex current : input) {
      boolean currentInside = isInside(current, keepLeft);

      if (currentInside) {
        if (!previousInside) {
          addDistinct(
            output,
            intersection(previous, current)
          );
        }

        addDistinct(output, current);
      } else if (previousInside) {
        addDistinct(
          output,
          intersection(previous, current)
        );
      }

      previous = current;
      previousInside = currentInside;
    }

    // Последняя точка может совпасть с первой после обрезки.
    if (output.size() > 1
      && samePosition(
      output.get(0),
      output.get(output.size() - 1)
    )) {
      output.remove(output.size() - 1);
    }

    return output;
  }

  private static boolean isInside(
    Vertex vertex,
    boolean keepLeft
  ) {
    return keepLeft
      ? vertex.position.x <= SPLIT_X + EPSILON
      : vertex.position.x >= SPLIT_X - EPSILON;
  }

  private static Vertex intersection(
    Vertex a,
    Vertex b
  ) {
    double dx = b.position.x - a.position.x;

    double t = Math.abs(dx) < EPSILON
      ? 0
      : (SPLIT_X - a.position.x) / dx;

    t = Math.max(0, Math.min(1, t));

    Vec3 position = new Vec3(
      SPLIT_X,
      lerp(a.position.y, b.position.y, t),
      lerp(a.position.z, b.position.z, t)
    );

    float u = (float) lerp(a.u, b.u, t);
    float v = (float) lerp(a.v, b.v, t);

    // Цвет, освещение и остальные атрибуты берём с ближайшей
    // вершины; XYZ и UV запишутся отдельно при сборке quad.
    int[] packed = (t < 0.5 ? a.packed : b.packed).clone();

    return new Vertex(position, u, v, packed);
  }

  private static double lerp(
    double a,
    double b,
    double t
  ) {
    return a + (b - a) * t;
  }

  private static void addDistinct(
    List<Vertex> vertices,
    Vertex vertex
  ) {
    if (vertices.isEmpty()
      || !samePosition(vertices.get(vertices.size() - 1), vertex)) {
      vertices.add(vertex);
    }
  }

  private static boolean samePosition(Vertex a, Vertex b) {
    return a.position.distanceToSqr(b.position) < 1.0E-12;
  }

  /**
   * Превращает обрезанный полигон в один или несколько BakedQuad.
   * Обычная прямоугольная грань даёт по quad на каждую половину.
   * Для нестандартной геометрии поддерживаются треугольные фрагменты.
   */
  private static List<BakedQuad> makePolygonQuads(
    List<Vertex> polygon,
    BakedQuad template,
    TextureAtlasSprite targetSprite
  ) {
    List<BakedQuad> result = new ArrayList<>();

    if (polygon.size() < 3) {
      return result;
    }

    if (polygon.size() == 4) {
      result.add(
        buildQuad(
          polygon,
          template,
          targetSprite
        )
      );

      return result;
    }

    // Три вершины образуют треугольник. Пять вершин и более
    // разбиваются на несколько треугольников веерным способом.
    for (int i = 1; i < polygon.size() - 1; i++) {
      List<Vertex> triangle = List.of(
        polygon.get(0),
        polygon.get(i),
        polygon.get(i + 1),
        polygon.get(i + 1)
      );

      result.add(
        buildQuad(
          triangle,
          template,
          targetSprite
        )
      );
    }

    return result;
  }

  private static BakedQuad buildQuad(
    List<Vertex> vertices,
    BakedQuad template,
    TextureAtlasSprite targetSprite
  ) {
    int[] packed = packVertices(
      vertices,
      template,
      template.getSprite(),
      targetSprite
    );

    return new BakedQuad(
      packed,
      template.getTintIndex(),
      template.getDirection(),
      targetSprite,
      template.isShade()
    );
  }

  private static List<Vertex> readVertices(BakedQuad quad) {
    int[] data = quad.getVertices();
    int stride = data.length / 4;

    List<Vertex> result = new ArrayList<>(4);

    for (int i = 0; i < 4; i++) {
      int start = i * stride;

      int[] packedVertex = Arrays.copyOfRange(
        data,
        start,
        start + stride
      );

      result.add(
        new Vertex(
          BakedQuadHelper.getXYZ(data, i),
          BakedQuadHelper.getU(data, i),
          BakedQuadHelper.getV(data, i),
          packedVertex
        )
      );
    }

    return result;
  }

  /**
   * Собирает данные вершин и переносит UV из одного атласа
   * в координаты другого спрайта, сохраняя относительную выборку.
   */
  private static int[] packVertices(
    List<Vertex> vertices,
    BakedQuad template,
    TextureAtlasSprite sourceSprite,
    TextureAtlasSprite targetSprite
  ) {
    int[] original = template.getVertices();
    int stride = original.length / 4;

    int[] result = new int[stride * 4];

    for (int i = 0; i < 4; i++) {
      Vertex vertex = vertices.get(i);

      System.arraycopy(
        vertex.packed,
        0,
        result,
        i * stride,
        Math.min(stride, vertex.packed.length)
      );

      BakedQuadHelper.setXYZ(
        result,
        i,
        vertex.position
      );

      BakedQuadHelper.setU(
        result,
        i,
        remapU(vertex.u, sourceSprite, targetSprite)
      );

      BakedQuadHelper.setV(
        result,
        i,
        remapV(vertex.v, sourceSprite, targetSprite)
      );
    }

    return result;
  }

  private static int[] packVertices(
    List<Vertex> vertices,
    BakedQuad template,
    TextureAtlasSprite sourceSprite,
    TextureAtlasSprite targetSprite,
    boolean rotate90
  ) {
    int[] original = template.getVertices();
    int stride = original.length / 4;
    int[] result = new int[stride * 4];

    for (int i = 0; i < 4; i++) {
      Vertex vertex = vertices.get(i);

      System.arraycopy(
        vertex.packed,
        0,
        result,
        i * stride,
        Math.min(stride, vertex.packed.length)
      );

      BakedQuadHelper.setXYZ(result, i, vertex.position);

      float sourceU = normalizeUV(
        vertex.u,
        sourceSprite.getU0(),
        sourceSprite.getU1()
      );

      float sourceV = normalizeUV(
        vertex.v,
        sourceSprite.getV0(),
        sourceSprite.getV1()
      );

      float targetU;
      float targetV;

      if (rotate90) {
        // Поворот UV на 90° по часовой стрелке.
        targetU = sourceU;
        targetV = sourceV;
      } else {
        targetU = sourceV;
        targetV = 1.0F - sourceU;
      }

      float atlasU = targetSprite.getU0()
        + targetU * (targetSprite.getU1() - targetSprite.getU0());

      float atlasV = targetSprite.getV0()
        + targetV * (targetSprite.getV1() - targetSprite.getV0());

      BakedQuadHelper.setU(result, i, atlasU);
      BakedQuadHelper.setV(result, i, atlasV);
    }

    return result;
  }

  private static float normalizeUV(
    float value,
    float min,
    float max
  ) {
    float range = max - min;

    if (Math.abs(range) < 1.0E-7F) {
      return 0.0F;
    }

    return (value - min) / range;
  }

  private static float remapU(
    float u,
    TextureAtlasSprite source,
    TextureAtlasSprite target
  ) {
    float range = source.getU1() - source.getU0();

    if (Math.abs(range) < 1.0E-7F) {
      return target.getU0();
    }

    float normalized = (u - source.getU0()) / range;

    return target.getU0()
      + normalized * (target.getU1() - target.getU0());
  }

  private static float remapV(
    float v,
    TextureAtlasSprite source,
    TextureAtlasSprite target
  ) {
    float range = source.getV1() - source.getV0();

    if (Math.abs(range) < 1.0E-7F) {
      return target.getV0();
    }

    float normalized = (v - source.getV0()) / range;

    return target.getV0()
      + normalized * (target.getV1() - target.getV0());
  }

  @Override
  protected ModelData.Builder gatherModelData(ModelData.Builder builder, BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData blockEntityData) {
    return builder;
  }

  private record Vertex(
    Vec3 position,
    float u,
    float v,
    int[] packed
  ) {
  }

  @FunctionalInterface
  private interface QuadLookup {
    List<BakedQuad> get(Direction direction);
  }
}