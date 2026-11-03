package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;

import com.simibubi.create.content.contraptions.StructureTransform;

import net.ziftech.copycats_extended.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TEST_CopycatBlock extends CCWaterloggedCopycatBlock {

  private static final VoxelShape TEST_SHAPE = Shapes.or(
    // Центральная вертикальная колонна
    box(6, 0, 6, 10, 16, 10),

    // Горизонтальная часть по X
    box(0, 4, 6, 16, 8, 10),

    // Горизонтальная часть по Z
    box(6, 8, 0, 10, 12, 16),

    // Верхняя дополнительная площадка
    box(3, 12, 3, 13, 16, 13)
  );

  public TEST_CopycatBlock(Properties properties) {
    super(properties);
  }

  /*
   * =====================================================
   * PHYSICAL SHAPE
   * =====================================================
   */

  @Override
  public VoxelShape getShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    return TEST_SHAPE;
  }

  /*
   * Физические столкновения.
   * В данном случае совпадают с формой блока.
   */
  @Override
  public VoxelShape getCollisionShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    return TEST_SHAPE;
  }

  /*
   * =====================================================
   * COPYCATS+ BLOCK ENTITY
   * =====================================================
   */

  @Override
  public Class<CCCopycatBlockEntity> getBlockEntityClass() {
    return CCCopycatBlockEntity.class;
  }

  @Override
  public net.minecraft.world.level.block.entity.BlockEntityType<? extends CCCopycatBlockEntity>
  getBlockEntityType() {
    return ModBlockEntities.TEST_COPYCAT_BLOCK.get();
  }

  /*
   * =====================================================
   * CREATE CONTRAPTION TRANSFORM
   * =====================================================
   */

  @Override
  public BlockState transform(
    BlockState state,
    StructureTransform transform
  ) {
    return state;
  }
}
