package net.ziftech.copycats_extended.block;

import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.WaterloggedMultiStateCopycatBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Set;


public abstract class CopycatAdvDoubleMaterialBlock extends WaterloggedMultiStateCopycatBlock {

  public static final String TOP = "top";
  public static final String BOTTOM = "bottom";

  private static final Set<String> MATERIAL_PARTS =
    Set.of(TOP, BOTTOM);

  protected CopycatAdvDoubleMaterialBlock(Properties properties) {
    super(properties);
  }

  // Часть, используемая по умолчанию, когда
  // конкретную часть определить невозможно.
  @Override
  public String defaultProperty() {
    return TOP;
  }

  // Ключи материалов, которые сохраняются в material_data.
  @Override
  public Set<String> storageProperties() {
    return MATERIAL_PARTS;
  }

  // Индексы для различения частей при отображении.
  @Override
  public int getColorIndex(String property) {
    return switch (property) {
      case TOP -> 0;
      case BOTTOM -> 1;
      default -> -1;
    };
  }

  // В базовом варианте обе части всегда существуют.
  @Override
  public boolean partExists(BlockState state, String property) {
    return MATERIAL_PARTS.contains(property);
  }

  /*
   * Геометрические методы намеренно оставлены
   * конкретному блоку.
   *
   * Разные формы могут по-разному определять:
   * - масштаб виртуального мира;
   * - положение частей;
   * - выбранную часть при клике;
   * - форму граней;
   * - перестановку материалов при трансформации.
   */

  @Override
  public abstract Vec3i vectorScale(BlockState state);

  @Override
  public abstract Vec3i getVectorFromProperty(
    BlockState state,
    String property
  );

  @Override
  public abstract String getPropertyFromInteraction(
    BlockState state,
    BlockGetter level,
    Vec3i hitLocation,
    BlockPos blockPos,
    Direction facing,
    Vec3 unscaledHit
  );

  @Override
  public abstract VoxelShape getPartialFaceShape(
    BlockGetter level,
    BlockState state,
    String property,
    Direction face
  );

  @Override
  public abstract void transformStorage(
    BlockState state,
    IMultiStateCopycatBlockEntity blockEntity,
    StructureTransform transform
  );
}
