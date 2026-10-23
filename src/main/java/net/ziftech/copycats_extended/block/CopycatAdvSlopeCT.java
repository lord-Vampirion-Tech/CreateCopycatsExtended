
package net.ziftech.copycats_extended.block;

import java.util.Optional;

import javax.annotation.Nullable;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICustomCTBlocking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Исправляет соединение текстур Copycat Advanced Slope
 * с обычными блоками, используемыми в качестве материала.
 *
 * Соединение разрешается на гранях, которые полностью заполнены
 * и находятся в одной плоскости.
 *
 * Наклонные и треугольные грани не должны автоматически
 * соединяться с боковыми гранями соседних полных блоков.
 */
public interface CopycatAdvSlopeCT
  extends ICopycatBlock, ICustomCTBlocking {

  /**
   * Позволяет CT-системе учитывать соседние блоки,
   * совпадающие с материалом скоса.
   */
  @Override
  default boolean canConnectTexturesToward(
    BlockAndTintGetter reader,
    BlockPos fromPos,
    BlockPos toPos,
    BlockState fromState
  ) {
    return isMaterialBlock(reader, fromPos, toPos)
      || ICopycatBlock.super.canConnectTexturesToward(
      reader, fromPos, toPos, fromState
    );
  }

  /**
   * Управляет тем, должна ли сторона игнорироваться
   * при расчёте соединения текстур.
   */
  @Override
  default boolean isIgnoredConnectivitySide(
    BlockAndTintGetter reader,
    BlockState fromState,
    Direction face,
    BlockPos fromPos,
    @Nullable BlockPos toPos,
    @Nullable BlockState toState
  ) {
    if (toPos != null
      && isCTEnabled(fromState, reader, fromPos)
      && connectsFlush(
      reader, fromState, fromPos, toPos, face
    )) {
      return false;
    }

    return ICopycatBlock.super.isIgnoredConnectivitySide(
      reader, fromState, face, fromPos, toPos, toState
    );
  }

  /**
   * Блокирует соединение на гранях, которые не подходят
   * для плоского стыка с блоком материала.
   */
  @Override
  default Optional<Boolean> isCTBlocked(
    BlockAndTintGetter reader,
    BlockState state,
    BlockPos pos,
    BlockPos connectingPos,
    BlockPos blockingPos,
    Direction face
  ) {
    return blockedTowardMaterial(
      reader, state, pos, connectingPos, face
    );
  }

  /**
   * Определяет, требуется ли блокировать соединение
   * с настоящим блоком, совпадающим с материалом скоса.
   */
  static Optional<Boolean> blockedTowardMaterial(
    BlockAndTintGetter reader,
    BlockState state,
    BlockPos pos,
    BlockPos connectingPos,
    Direction face
  ) {
    if (!inPlane(pos, connectingPos, face)
      || !isMaterialBlock(reader, pos, connectingPos)
      || connectsFlush(
      reader, state, pos, connectingPos, face
    )) {
      return Optional.empty();
    }

    // Сохраняем соединение, если исходная логика
    // Copycats+ уже разрешает его для этой пары блоков.
    boolean upstreamConnects =
      state.getBlock() instanceof ICopycatBlock copycat
        && copycat.checkConnection(
        reader, pos, connectingPos, state
      );

    return upstreamConnects
      ? Optional.empty()
      : Optional.of(true);
  }

  /**
   * Проверяет, является ли сосед настоящим блоком материала
   * копиката, а не другим копикатом.
   */
  static boolean isMaterialBlock(
    BlockAndTintGetter reader,
    BlockPos copycatPos,
    BlockPos otherPos
  ) {
    if (otherPos.equals(copycatPos)) {
      return false;
    }

    BlockState other = reader.getBlockState(otherPos);

    if (other.isAir()
      || other.getBlock() instanceof ICopycatBlock) {
      return false;
    }

    BlockState material =
      ICopycatBlock.getMaterial(reader, copycatPos);

    return !material.isAir()
      && other.is(material.getBlock());
  }

  /**
   * Проверяет, что грань копиката и грань материала
   * представляют собой полные плоские квадраты.
   */
  static boolean connectsFlush(
    BlockAndTintGetter reader,
    BlockState copycatState,
    BlockPos copycatPos,
    BlockPos otherPos,
    @Nullable Direction face
  ) {
    if (face == null
      || !inPlane(copycatPos, otherPos, face)
      || !isMaterialBlock(reader, copycatPos, otherPos)) {
      return false;
    }

    BlockState other = reader.getBlockState(otherPos);

    return Block.isFaceFull(
      copycatState.getShape(reader, copycatPos),
      face
    )
      && Block.isFaceFull(
      other.getShape(reader, otherPos),
      face
    );
  }

  /**
   * Проверяет, находятся ли блоки в одной плоскости
   * относительно указанной оси грани.
   */
  private static boolean inPlane(
    BlockPos pos,
    BlockPos otherPos,
    Direction face
  ) {
    return !otherPos.equals(pos)
      && face.getAxis().choose(
      pos.getX(), pos.getY(), pos.getZ()
    ) == face.getAxis().choose(
      otherPos.getX(),
      otherPos.getY(),
      otherPos.getZ()
    );
  }
}
