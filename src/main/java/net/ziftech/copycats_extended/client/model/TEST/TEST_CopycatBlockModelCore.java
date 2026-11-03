package net.ziftech.copycats_extended.client.model.TEST;

import java.util.List;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.AssemblyTransform;
import com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext;

import net.minecraft.world.level.block.state.BlockState;

import static com.copycatsplus.copycats.foundation.copycat.model.assembly.CopycatRenderContext.*;

public class TEST_CopycatBlockModelCore extends CopycatModelCore {

  private static final String ITEM_GEOMETRY_KEY = "item_geometry";

  @Override
  public void registerModels(List<ModelEntry> entries) {
    // Обычная модель Copycat+: материал блока
    super.registerModels(entries);

    // Дополнительная модель для предмета
    entries.add(new ModelEntry(ITEM_GEOMETRY_KEY, null, this, EntryType.STATIC));
  }

  @Override
  public void emitCopycatQuads(
    String key,
    BlockState state,
    CopycatRenderContext context,
    BlockState material
  ) {
    /*
     * В мире state != null:
     * используем только запись материала.
     *
     * У предмета state == null:
     * используем только статическую геометрию.
     */
    if (state == null && !key.equals(ITEM_GEOMETRY_KEY))
      return;

    if (state != null && !key.equals(MATERIAL_KEY))
      return;

    AssemblyTransform transform = t -> {
    };

    // Центральная вертикальная колонна
    context.assemblePiece(
      transform,
      vec3(6, 0, 6),
      aabb(4, 16, 4),
      cull(0)
    );

    // Горизонтальная часть по X
    context.assemblePiece(
      transform,
      vec3(0, 4, 6),
      aabb(16, 4, 4),
      cull(0)
    );

    // Горизонтальная часть по Z
    context.assemblePiece(
      transform,
      vec3(6, 8, 0),
      aabb(4, 4, 16),
      cull(0)
    );

    // Верхняя дополнительная площадка
    context.assemblePiece(
      transform,
      vec3(3, 12, 3),
      aabb(10, 4, 10),
      cull(0)
    );
  }

  @Override
  public void prepareForRender() {
    super.prepareForRender();

    // У этой модели нет многосоставной раскраски.
    // Также исключаем обращение к BlockState при рендере предмета.
    colorize = false;
  }
}