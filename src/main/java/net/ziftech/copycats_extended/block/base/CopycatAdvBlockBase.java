
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

public final class CopycatAdvBlockBase {

  private static final Map<Pair<Direction, Integer>, Direction> HORIZONTAL_POSITION_MAP = new HashMap<>();

  static {
    for (Direction main : Iterate.horizontalDirections) {
      Direction cross = main.getCounterClockWise();

      HORIZONTAL_POSITION_MAP.put(Pair.of(main.getOpposite(), cross.getAxisDirection().getStep()), main);
      HORIZONTAL_POSITION_MAP.put(Pair.of(cross.getOpposite(), main.getAxisDirection().getStep()), main);
    }
  }

  private CopycatAdvBlockBase() {
  }

  /**
   * Определяет направление скоса по точке клика на горизонтальной
   * поверхности относительно направления взгляда игрока.
   */
  private static Direction getHorizontalSlopeFacing(
    BlockPlaceContext context
  ) {
    BlockPos pos = context.getClickedPos();

    double localX = context.getClickLocation().x - pos.getX() - 0.5D;
    double localZ = context.getClickLocation().z - pos.getZ() - 0.5D;

    Direction playerFacing = context.getHorizontalDirection();
    Direction playerRight = playerFacing.getClockWise();

    double rightOffset = localX * playerRight.getStepX() + localZ * playerRight.getStepZ();
    double forwardOffset = localX * playerFacing.getStepX() + localZ * playerFacing.getStepZ();

    if (Math.abs(rightOffset) >= Math.abs(forwardOffset))
      return rightOffset < 0 ? playerRight.getOpposite() : playerRight;

    return forwardOffset > 0 ? playerFacing : playerFacing.getOpposite();
  }

  /**
   * Рассчитывает размещение расширенного скоса.
   * <p>
   * wallMountedProperty:
   * false — горизонтальный скос;
   * true  — настенный скос.
   */
  public static BlockState getSlopePlacement(
    BlockPlaceContext context,
    BlockState placement,
    Block clickedBlock,
    DirectionProperty facingProperty,
    EnumProperty<Half> halfProperty,
    BooleanProperty wallMountedProperty
  ) {
    BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());

    // Наращивание существующего блока.
    if (clickedState.is(clickedBlock)) {
      return placement
        .setValue(facingProperty, clickedState.getValue(facingProperty))
        .setValue(halfProperty, clickedState.getValue(halfProperty))
        .setValue(wallMountedProperty, clickedState.getValue(wallMountedProperty));
    }

    Direction clickedFace = context.getClickedFace();

    // Верхняя или нижняя грань.
    if (clickedFace.getAxis() == Axis.Y) {
      return placement
        .setValue(facingProperty, getHorizontalSlopeFacing(context))
        .setValue(halfProperty, clickedFace == Direction.DOWN ? Half.TOP : Half.BOTTOM)
        .setValue(wallMountedProperty, false);
    }

    BlockPos pos = context.getClickedPos();

    double localY = context.getClickLocation().y - pos.getY();

    double localSide = clickedFace.getAxis() == Axis.X
      ? context.getClickLocation().z - pos.getZ()
      : context.getClickLocation().x - pos.getX();

    // Определяем ближайшую зону боковой грани.
    if (Math.abs(localY - 0.5D) >= Math.abs(localSide - 0.5D)) {
      return placement
        .setValue(facingProperty, context.getHorizontalDirection())
        .setValue(halfProperty, localY >= 0.5D ? Half.TOP : Half.BOTTOM)
        .setValue(wallMountedProperty, false);
    }

    // Левая или правая зона боковой грани.
    int offset = localSide >= 0.5D ? 1 : -1;

    Direction facing = HORIZONTAL_POSITION_MAP.get(Pair.of(clickedFace, offset));

    if (facing == null) facing = context.getHorizontalDirection();

    boolean right = switch (clickedFace) {
      case SOUTH -> localSide >= 0.5D;
      case NORTH -> localSide < 0.5D;
      case EAST -> localSide < 0.5D;
      case WEST -> localSide >= 0.5D;
      default -> false;
    };

    BlockState wallPlacement = placement
      .setValue(facingProperty, facing)
      .setValue(halfProperty, right ? Half.TOP : Half.BOTTOM)
      .setValue(wallMountedProperty, true);

    return normalizeWallSlope(
      wallPlacement,
      facingProperty,
      halfProperty
    );
  }

  /**
   * Нормализует состояние настенного скоса.
   * Правила нормализации можно менять независимо от размещения.
   */
  private static BlockState normalizeWallSlope(
    BlockState state,
    DirectionProperty facingProperty,
    EnumProperty<Half> halfProperty
  ) {
    Direction facing = state.getValue(facingProperty);

    return switch (facing) {
      case SOUTH -> state
        .setValue(facingProperty, Direction.WEST)
        .setValue(halfProperty, Half.TOP);

      case WEST -> state
        .setValue(facingProperty, Direction.NORTH)
        .setValue(halfProperty, Half.TOP);

      case NORTH -> state
        .setValue(facingProperty, Direction.EAST)
        .setValue(halfProperty, Half.TOP);

      case EAST -> state
        .setValue(facingProperty, Direction.SOUTH)
        .setValue(halfProperty, Half.TOP);

      default -> state;
    };
  }

  /**
   * Рассчитывает размещение слоистого скоса.
   */
  public static BlockState getLayerPlacement(
    BlockPlaceContext context,
    BlockState placement,
    Block clickedBlock,
    DirectionProperty facingProperty,
    EnumProperty<Half> halfProperty,
    EnumProperty<WallOrientation> orientationProperty
  ) {
    BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());

    // Сохраняем ориентацию при наращивании.
    if (clickedState.is(clickedBlock)) {
      return placement
        .setValue(halfProperty, clickedState.getValue(halfProperty))
        .setValue(orientationProperty, clickedState.getValue(orientationProperty));
    }

    Direction clickedFace = context.getClickedFace();

    // Верхняя или нижняя грань.
    if (clickedFace.getAxis() == Axis.Y) {
      return placement
        .setValue(facingProperty, getHorizontalSlopeFacing(context))
        .setValue(halfProperty, clickedFace == Direction.DOWN ? Half.TOP : Half.BOTTOM)
        .setValue(orientationProperty, WallOrientation.NORMAL);
    }

    BlockPos pos = context.getClickedPos();

    double localY = context.getClickLocation().y - pos.getY();
    double localSide = clickedFace.getAxis() == Axis.X
      ? context.getClickLocation().z - pos.getZ()
      : context.getClickLocation().x - pos.getX();

    double verticalOffset = localY - 0.5D;
    double horizontalOffset = localSide - 0.5D;

    placement = placement.setValue(facingProperty, clickedFace);

    // Верхняя или нижняя зона боковой грани.
    if (Math.abs(verticalOffset) >= Math.abs(horizontalOffset)) {
      return placement
        .setValue(
          halfProperty,
          localY >= 0.5D ? Half.TOP : Half.BOTTOM
        )
        .setValue(
          orientationProperty,
          localY >= 0.5D ? WallOrientation.UP : WallOrientation.DOWN
        );
    }

    // Левая или правая зона боковой грани.
    boolean right = switch (clickedFace) {
      case SOUTH -> localSide >= 0.5D;
      case NORTH -> localSide < 0.5D;
      case EAST -> localSide < 0.5D;
      case WEST -> localSide >= 0.5D;
      default -> false;
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
