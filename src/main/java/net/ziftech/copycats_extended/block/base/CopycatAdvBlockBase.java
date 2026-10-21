
package net.ziftech.copycats_extended.block.base;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.core.Direction.Axis;

  /*
  * Общие функции размещения расширенных блоков Copycats.
  */
public final class CopycatAdvBlockBase {

  private static final Map<Pair<Direction, Integer>, Direction>
    HORIZONTAL_POSITION_MAP = new HashMap<>();

  static {
    for (Direction main : Iterate.horizontalDirections) {
      Direction cross = main.getCounterClockWise();

      int mainOffset = main.getAxisDirection().getStep();
      int crossOffset = cross.getAxisDirection().getStep();

      HORIZONTAL_POSITION_MAP.put(
        Pair.of(main.getOpposite(), crossOffset),
        main
      );

      HORIZONTAL_POSITION_MAP.put(
        Pair.of(cross.getOpposite(), mainOffset),
        main
      );
    }
  }

  private CopycatAdvBlockBase() {
  }

  /**
   * Рассчитывает размещение обычного скоса.
   *
   * Сохраняет ориентацию существующего блока того же типа.
   * При установке на верхнюю или нижнюю грань выбирает
   * горизонтальный вариант, а при установке сбоку —
   * горизонтальный либо настенный вариант.
   */
  public static BlockState getSlopePlacement(
    BlockPlaceContext context,
    BlockState placement,
    Block clickedBlock,
    DirectionProperty facingProperty,
    EnumProperty<Half> halfProperty,
    BooleanProperty wallMountedProperty
  ) {
    BlockState clickedState = context.getLevel()
      .getBlockState(context.getClickedPos());

    // Сохраняем ориентацию такого же блока.
    if (clickedState.is(clickedBlock)) {
      return placement
        .setValue(
          facingProperty,
          clickedState.getValue(facingProperty)
        )
        .setValue(
          halfProperty,
          clickedState.getValue(halfProperty)
        )
        .setValue(
          wallMountedProperty,
          clickedState.getValue(wallMountedProperty)
        );
    }

    Direction clickedFace = context.getClickedFace();

    // Верхняя или нижняя грань.
    if (clickedFace.getAxis() == Axis.Y) {
      Half half = clickedFace == Direction.DOWN
        ? Half.TOP
        : Half.BOTTOM;

      return placement
        .setValue(
          facingProperty,
          context.getHorizontalDirection()
        )
        .setValue(halfProperty, half)
        .setValue(wallMountedProperty, false);
    }

    double localY = context.getClickLocation().y
      - context.getClickedPos().getY();

    double localSide = clickedFace.getAxis() == Axis.X
      ? context.getClickLocation().z
      - context.getClickedPos().getZ()
      : context.getClickLocation().x
      - context.getClickedPos().getX();

    double verticalDistance = Math.abs(localY - 0.5);
    double sideDistance = Math.abs(localSide - 0.5);

    // Верхняя или нижняя зона боковой грани.
    if (verticalDistance >= sideDistance) {
      Half half = localY >= 0.5
        ? Half.TOP
        : Half.BOTTOM;

      return placement
        .setValue(
          facingProperty,
          context.getHorizontalDirection()
        )
        .setValue(halfProperty, half)
        .setValue(wallMountedProperty, false);
    }

    // Левая или правая зона боковой грани.
    int offset = localSide >= 0.5 ? 1 : -1;

    Direction facing = HORIZONTAL_POSITION_MAP.get(
      Pair.of(clickedFace, offset)
    );

    if (facing == null) {
      facing = context.getHorizontalDirection();
    }

    return placement
      .setValue(facingProperty, facing)
      .setValue(wallMountedProperty, true);
  }

  /**
   * Рассчитывает размещение слоистого скоса.
   *
   * Не меняет результат родительского размещения,
   * если наращивается существующий блок: это сохраняет
   * логику свойства LAYERS в CopycatSlopeLayerBlock.
   */
  public static BlockState getLayerPlacement(
    BlockPlaceContext context,
    BlockState placement,
    Block clickedBlock,
    DirectionProperty facingProperty,
    EnumProperty<Half> halfProperty,
    BooleanProperty wallMountedProperty,
    BooleanProperty wallSidewaysProperty
  ) {
    BlockState clickedState = context.getLevel()
      .getBlockState(context.getClickedPos());

    // Родитель уже обработал увеличение LAYERS.
    if (clickedState.is(clickedBlock)) {
      return placement;
    }

    Direction clickedFace = context.getClickedFace();

    // Верхняя или нижняя грань.
    if (clickedFace.getAxis() == Axis.Y) {
      return placement
        .setValue(wallMountedProperty, false)
        .setValue(wallSidewaysProperty, false);
    }

    BlockPos pos = context.getClickedPos();

    double up = context.getClickLocation().y - pos.getY();

    Direction playerRight = clickedFace.getCounterClockWise();

    double along = playerRight.getAxis() == Axis.X
      ? context.getClickLocation().x - pos.getX()
      : context.getClickLocation().z - pos.getZ();

    double right = playerRight.getAxisDirection()
      == Direction.AxisDirection.POSITIVE
      ? along
      : 1.0D - along;

    double verticalOffset = up - 0.5D;
    double horizontalOffset = right - 0.5D;

    // Верхняя или нижняя зона боковой грани.
    if (Math.abs(verticalOffset) >= Math.abs(horizontalOffset)) {
      return placement
        .setValue(facingProperty, clickedFace)
        .setValue(
          halfProperty,
          verticalOffset > 0 ? Half.TOP : Half.BOTTOM
        )
        .setValue(wallMountedProperty, true)
        .setValue(wallSidewaysProperty, false);
    }

    // Левая или правая зона боковой грани.
    return placement
      .setValue(facingProperty, clickedFace)
      .setValue(
        halfProperty,
        horizontalOffset > 0 ? Half.TOP : Half.BOTTOM
      )
      .setValue(wallMountedProperty, true)
      .setValue(wallSidewaysProperty, true);
  }
}
