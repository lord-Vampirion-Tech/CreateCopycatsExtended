
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
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation;

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
   * <p>
   * Сохраняет ориентацию существующего блока того же типа.
   * При установке на верхнюю или нижнюю грань выбирает
   * горизонтальный вариант, а при установке сбоку —
   * горизонтальный либо настенный вариант.
   */

  /**
   * Рассчитывает размещение обычного расширенного скоса.
   *
   * WALL_ORIENTATION:
   * false — горизонтальный скос (пол или потолок);
   * true  — скос, установленный на боковую грань.
   *
   * При установке на существующий такой же блок сохраняет
   * его ориентацию для последующего наращивания.
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

    // Наращивание существующего блока: сохраняем ориентацию.
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

    // Установка на пол или потолок.
    if (clickedFace.getAxis() == Axis.Y) {
      Half half = clickedFace == Direction.DOWN
        ? Half.TOP
        : Half.BOTTOM;

      return placement
        .setValue(facingProperty, context.getHorizontalDirection())
        .setValue(halfProperty, half)
        .setValue(wallMountedProperty, false);
    }

    BlockPos pos = context.getClickedPos();

    double localY =
      context.getClickLocation().y - pos.getY();

    double localSide = clickedFace.getAxis() == Axis.X
      ? context.getClickLocation().z - pos.getZ()
      : context.getClickLocation().x - pos.getX();

    double verticalDistance = Math.abs(localY - 0.5D);
    double sideDistance = Math.abs(localSide - 0.5D);

    // Верхняя или нижняя зона боковой грани:
    // размещаем обычный горизонтальный скос.
    if (verticalDistance >= sideDistance) {
      Half half = localY >= 0.5D
        ? Half.TOP
        : Half.BOTTOM;

      return placement
        .setValue(facingProperty, context.getHorizontalDirection())
        .setValue(halfProperty, half)
        .setValue(wallMountedProperty, false);
    }

    // Левая или правая зона боковой грани:
    // размещаем настенный скос.
    int offset = localSide >= 0.5D ? 1 : -1;

    Direction facing = HORIZONTAL_POSITION_MAP.get(
      Pair.of(clickedFace, offset)
    );

    if (facing == null) {
      facing = context.getHorizontalDirection();
    }

    // Для настенного скоса FACING указывает нормаль стены.
    // HALF различает левую и правую стороны.
    boolean right = switch (clickedFace) {
      case SOUTH -> localSide >= 0.5D;
      case NORTH -> localSide < 0.5D;
      case EAST  -> localSide < 0.5D;
      case WEST  -> localSide >= 0.5D;
      default    -> false;
    };

    return placement
      .setValue(facingProperty, facing)
      .setValue(halfProperty, right ? Half.TOP : Half.BOTTOM)
      .setValue(wallMountedProperty, true);
  }


  public static BlockState getSlopeLayerPlacement(
    BlockPlaceContext context,
    BlockState placement,
    Block clickedBlock,
    DirectionProperty facingProperty,
    EnumProperty<Half> halfProperty,
    EnumProperty<net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation> orientationProperty
  ) {
    BlockState clickedState = context.getLevel()
      .getBlockState(context.getClickedPos());

    if (clickedState.is(clickedBlock)) {
      return placement
        .setValue(halfProperty, clickedState.getValue(halfProperty))
        .setValue(orientationProperty, clickedState.getValue(orientationProperty));
    }

    Direction clickedFace = context.getClickedFace();

    if (clickedFace.getAxis() == Axis.Y) {
      return placement
        .setValue(facingProperty, context.getHorizontalDirection())
        .setValue(halfProperty, clickedFace == Direction.DOWN ? Half.TOP : Half.BOTTOM)
        .setValue(orientationProperty,
          net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation.NORMAL);
    }

    BlockPos pos = context.getClickedPos();

    double localY = context.getClickLocation().y - pos.getY();
    double localSide = clickedFace.getAxis() == Axis.X
      ? context.getClickLocation().z - pos.getZ()
      : context.getClickLocation().x - pos.getX();

    double verticalOffset = localY - 0.5D;
    double horizontalOffset = localSide - 0.5D;

    placement = placement.setValue(facingProperty, clickedFace);

    if (Math.abs(verticalOffset) >= Math.abs(horizontalOffset)) {
      return placement
        .setValue(halfProperty, localY >= 0.5D ? Half.TOP : Half.BOTTOM)
        .setValue(orientationProperty,
          localY >= 0.5D
            ? net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation.UP
            : net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation.DOWN);
    }

    boolean right = switch (clickedFace) {
      case SOUTH -> localSide >= 0.5D;
      case NORTH -> localSide < 0.5D;
      case EAST -> localSide < 0.5D;
      case WEST -> localSide >= 0.5D;
      default -> false;
    };

    return placement
      .setValue(halfProperty, right ? Half.TOP : Half.BOTTOM)
      .setValue(orientationProperty,
        right
          ? net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation.LEFT
          : net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock.WallOrientation.RIGHT);
  }

  /**
   * Рассчитывает размещение слоистого скоса.
   * <p>
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
    EnumProperty<WallOrientation> orientationProperty
  ) {
    BlockState clickedState = context.getLevel()
      .getBlockState(context.getClickedPos());

    // Сохраняем ориентацию при наращивании существующего блока.
    if (clickedState.is(clickedBlock)) {
      return placement
        .setValue(
          halfProperty,
          clickedState.getValue(halfProperty)
        )
        .setValue(
          orientationProperty,
          clickedState.getValue(orientationProperty)
        );
    }

    Direction clickedFace = context.getClickedFace();

    // Верхняя или нижняя грань — обычное размещение.
    if (clickedFace.getAxis() == Axis.Y) {
      return placement
        .setValue(
          facingProperty,
          context.getHorizontalDirection()
        )
        .setValue(
          halfProperty,
          clickedFace == Direction.DOWN
            ? Half.TOP
            : Half.BOTTOM
        )
        .setValue(
          orientationProperty,
          WallOrientation.NORMAL
        );
    }

    BlockPos pos = context.getClickedPos();

    double localY =
      context.getClickLocation().y - pos.getY();

    double localSide = clickedFace.getAxis() == Axis.X
      ? context.getClickLocation().z - pos.getZ()
      : context.getClickLocation().x - pos.getX();

    double verticalOffset = localY - 0.5D;
    double horizontalOffset = localSide - 0.5D;

    placement = placement.setValue(
      facingProperty,
      clickedFace
    );

    // Выбираем ближайшую из четырёх зон боковой грани.
    if (Math.abs(verticalOffset) >= Math.abs(horizontalOffset)) {
      // Верхняя / нижняя зона.
      return placement
        .setValue(
          halfProperty,
          localY >= 0.5D ? Half.TOP : Half.BOTTOM
        )
        .setValue(
          orientationProperty,
          localY >= 0.5D
            ? WallOrientation.UP
            : WallOrientation.DOWN
        );
    }


    boolean right = switch (clickedFace) {
      case SOUTH -> localSide >= 0.5D;    // +Z
      case NORTH -> localSide < 0.5D;     // -Z
      case EAST  -> localSide < 0.5D;     // +X
      case WEST  -> localSide >= 0.5D;    // -X
      default    -> false;
    };

    return placement
      .setValue(
        halfProperty,
        right ? Half.TOP : Half.BOTTOM
      )
      .setValue(
        orientationProperty,
        right ? WallOrientation.LEFT : WallOrientation.RIGHT
      );

  }

}
