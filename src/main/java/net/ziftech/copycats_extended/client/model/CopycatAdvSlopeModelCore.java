
package net.ziftech.copycats_extended.client.model;

import java.util.List;

import com.copycatsplus.copycats.content.copycat.slope.CopycatSlopeModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadTransform;

import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.SlopeMode;
import net.ziftech.copycats_extended.registry.ModBlocks;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.MutableCullFace.*;
import static com.copycatsplus.copycats.foundation.copycat.model.assembly.quad.QuadSlope.map;

public class CopycatAdvSlopeModelCore extends CopycatSlopeModelCore {
//
//  @Override
//  public void registerModels(List<ModelEntry> entries) {
//    entries.add(new ModelEntry(
//      MATERIAL_KEY,
//      (state, material) -> {
//        BakedModel model = getModelOf(material);
//
//        SlopeMode mode = state.getValue(CopycatAdvSlopeBlock.SLOPE_MODE);
//
//        if (mode == SlopeMode.FIRST) {
//          return model;
//        }
//
//        return new SlopeTextureBakedModel(model, state, mode);
//      },
//      this,
//      EntryType.COPYCAT
//    ));
//  }

  private static final String ITEM_GEOMETRY_KEY = "item_geometry";

  @Override
  public void registerModels(List<ModelEntry> entries) {
    super.registerModels(entries);

    entries.add(new ModelEntry(ITEM_GEOMETRY_KEY, null, this, EntryType.STATIC));
  }

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    if (ITEM_GEOMETRY_KEY.equals(key)) {
      if (state != null)
        return;

      state = ModBlocks.COPYCAT_ADV_SLOPE.get().defaultBlockState();

    } else if (state == null) {
      return;
    }

    if (!MATERIAL_KEY.equals(key) && !ITEM_GEOMETRY_KEY.equals(key))
      return;

    boolean wallMounted = state.getValue(CopycatAdvSlopeBlock.WALL_ORIENTATION);
    Direction facing = state.getValue(CopycatAdvSlopeBlock.FACING);
    Half half = state.getValue(CopycatAdvSlopeBlock.HALF);
    SlopeMode mode = state.getValue(CopycatAdvSlopeBlock.SLOPE_MODE);
    int angle = CopycatAdvSlopeGeometry.wallAngle(facing, half);
    int sidewaysAngle = CopycatAdvSlopeGeometry.wallSidewaysAngle(facing);

    // Обычный скос
    if (!wallMounted) {
      emitNormalSlope(context, facing, half, mode);
      return;
    }
    emitWallSlope(context, facing, half);
  }

  /**
   * Обычный скос с поддержкой SlopeMode.
   */
  private void emitNormalSlope(
    CopycatRenderContext context,
    Direction facing,
    Half half,
    SlopeMode mode
  ) {

    AssemblyTransform transform = t -> {
      t.rotateY((int) facing.toYRot())
        .flipY(half == Half.TOP);
    };

    switch (mode) {
      case FIRST -> {
        // Полностью оригинальная геометрия Copycats+
        CopycatSlopeModelCore.assembleSlope(
          context,
          transform,
          0,
          16,
          enhanced
        );
      }

      case SECOND -> {
        // Наша геометрия с боковыми текстурами
        //assembleTriangularSide(context, transform);
        //assembleWideSide(context, transform);


        CopycatSlopeModelCore.assembleSlope(
          context,
          transform,
          0,
          16,
          enhanced
        );
      }

      case THIRD -> {
        // Заглушка
      }
    }
  }

  /**
   * Настенный скос.
   * Текущая реализация оставлена без изменений.
   */
  private void emitWallSlope(CopycatRenderContext context, Direction facing, Half half) {
    int angle = CopycatAdvSlopeGeometry.wallAngle(facing, half);
    int sidewaysAngle = CopycatAdvSlopeGeometry.wallSidewaysAngle(facing);

    AssemblyTransform transform = t -> {
      t.rotateY((int) facing.toYRot())
        .flipY(half == Half.TOP);

      if (CopycatAdvSlopeGeometry.rotateAroundZ(facing)) {
        t.rotateZ(angle);
      } else {
        t.rotateX(angle);
      }

      if (CopycatAdvSlopeGeometry.wallSidewaysRotateAroundZ(facing)) {
        t.rotateZ(sidewaysAngle);
      } else {
        t.rotateX(sidewaysAngle);
      }
    };

    CopycatSlopeModelCore.assembleSlope(
      context,
      transform,
      0,
      16,
      enhanced
    );
  }

  private void assembleTriangularSide(
    CopycatRenderContext context,
    AssemblyTransform transform
  ) {

    final double maxHeight = 16;
    final double margin = 3;

    final double angleBottom = Math.toDegrees(Math.atan2(maxHeight, 16));
    final double marginAdjBottom =
      margin / Math.tan(Math.toRadians(angleBottom) / 2);

    final double angleTop = Math.toDegrees(Math.atan2(16, maxHeight));
    final double marginAdjTop =
      margin / Math.tan(Math.toRadians(angleTop) / 2);

    final double halfLength =
      Math.sqrt(maxHeight * maxHeight + 16 * 16) / 2;

    final double midLengthBottom = halfLength - marginAdjBottom;
    final double marginAdjExcessBottom =
      marginAdjBottom - Math.floor(marginAdjBottom);

    final double midLengthTop = halfLength - marginAdjTop;
    final double marginAdjExcessTop =
      marginAdjTop - Math.floor(marginAdjTop);

    final double alignedLengthBottom =
      Math.abs(
        Math.floor(midLengthBottom + marginAdjExcessBottom)
          - marginAdjExcessBottom
      );

    final double alignedLengthTop =
      Math.floor(midLengthTop + marginAdjExcessTop)
        - marginAdjExcessTop;

//    // Нижняя часть
//    context.assemblePiece(
//      transform,
//      vec3(0, 0, 0),
//      aabb(16, 16, marginAdjBottom),
//      cull(UP | NORTH | SOUTH),
//      updateUV(slope(
//        Direction.UP,
//        (a, b) -> map(0, marginAdjBottom, 0, margin, b)
//      ))
//    );
//
//    // Центральная часть
//    context.assemblePiece(
//      transform,
//      vec3(0, 0, marginAdjBottom),
//      aabb(16, 16, 16 - margin - marginAdjBottom)
//        .move(0, 0, marginAdjBottom),
//      cull(UP | NORTH | SOUTH),
//      updateUV(slope(
//        Direction.UP,
//        (a, b) -> map(
//          marginAdjBottom,
//          16 - margin,
//          margin,
//          maxHeight - marginAdjTop,
//          b
//        )
//      ))
//    );
//
//    // Верхняя часть
//    context.assemblePiece(
//      transform,
//      vec3(0, 0, 16 - margin),
//      aabb(16, 16, margin)
//        .move(0, 0, 16 - margin),
//      cull(UP | NORTH),
//      updateUV(slope(
//        Direction.UP,
//        (a, b) -> map(
//          16 - margin,
//          16,
//          maxHeight - marginAdjTop,
//          maxHeight,
//          b
//        )
//      ))
//    );

//    context.assemblePiece(
//      transform,
//      vec3(0, 0, 16 - marginAdjTop),
//      aabb(16, 16, marginAdjTop)
//        .move(0, 0, 16 - marginAdjTop),
//      cull(DOWN | NORTH | SOUTH),
//      updateUV(slope(
//        Direction.DOWN,
//        (a, b) -> map(
//          16 - marginAdjTop,
//          16,
//          margin,
//          0,
//          b
//        )
//      )),
//      translate(0, -16 + maxHeight, 0),
//      rotate(
//        pivot(16, maxHeight, 16),
//        angle(-angleBottom, 0, 0)
//      )
//    );

    // 7. Верхняя закрывающая часть
    context.assemblePiece(
      transform,
      vec3(0, 16 - marginAdjTop, 0),
      aabb(16, marginAdjTop, 16),
      cull(UP | DOWN | SOUTH),
      updateUV(slope(
        Direction.NORTH,
        (a, b) -> map(
          16,
          16 - marginAdjTop,
          margin,
          0,
          b
        )
      ))
    );

    context.assemblePiece(
      transform,
      vec3(0, 0, 0),
      aabb(16, marginAdjBottom, 16),
      cull(UP | DOWN | SOUTH),
      updateUV(slope(
        Direction.SOUTH,
        (a, b) -> map(
          0,
          marginAdjBottom,
          0,
          margin,
          b
        )
      )),
      rotate(
        pivot(0, 0, 0),
        angle(angleBottom, 0, 0)
      )
    );

    // 1. Нижняя часть широкой наклонной поверхности
    context.assemblePiece(
      transform,
      vec3(0, 0, 0),
      aabb(16, 16, marginAdjBottom),
      cull(UP | NORTH | SOUTH),
      updateUV(slope(
        Direction.UP,
        (a, b) -> map(
          0,
          marginAdjBottom,
          0,
          margin,
          b
        )
      ))
    );

    // 2. Центральная часть широкой наклонной поверхности
    context.assemblePiece(
      transform,
      vec3(0, 0, marginAdjBottom),
      aabb(16, 16, 16 - margin - marginAdjBottom)
        .move(0, 0, marginAdjBottom),
      cull(UP | NORTH | SOUTH),
      updateUV(slope(
        Direction.UP,
        (a, b) -> map(
          marginAdjBottom,
          16 - margin,
          margin,
          maxHeight - marginAdjTop,
          b
        )
      ))
    );

    // 3. Верхняя часть широкой наклонной поверхности
    context.assemblePiece(
      transform,
      vec3(0, 0, 16 - margin),
      aabb(16, 16, margin)
        .move(0, 0, 16 - margin),
      cull(UP | NORTH),
      updateUV(slope(
        Direction.UP,
        (a, b) -> map(
          16 - margin,
          16,
          maxHeight - marginAdjTop,
          maxHeight,
          b
        )
      ))
    );
  }

  private QuadTransform fixVertices() {
    return (quad, sprite) -> {
      for (var vertex : quad.vertices) {
        var v = vertex.xyz;

        if (Math.abs(v.y - 0.00125) < 0.00001) {
          v.y = 0.0;
        }
      }

      return true;
    };
  }

  @Override
  public void prepareForRender() {
    super.prepareForRender();
    colorize = false;
  }
}
