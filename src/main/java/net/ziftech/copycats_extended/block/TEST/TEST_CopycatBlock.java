package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.CCShapes;
import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;

import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.ziftech.copycats_extended.client.model.TEST.TEST_CopycatBlockGeometry;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import net.minecraft.core.Direction;
import com.copycatsplus.copycats.utility.BlockFaceUtils;
import java.util.Optional;

public class TEST_CopycatBlock extends CCWaterloggedCopycatBlock {

  private static final VoxelShape TEST_SHAPE = TEST_CopycatBlockGeometry.create();

//  private static final VoxelShape TEST_SHAPE = Shapes.or(
//    // Центральная вертикальная колонна
//    box(6, 0, 6, 10, 16, 10),
//
//    // Горизонтальная часть по X
//    box(0, 4, 6, 16, 8, 10),
//
//    // Горизонтальная часть по Z
//    box(6, 8, 0, 10, 12, 16),
//
//    // Верхняя дополнительная площадка
//    box(3, 12, 3, 13, 16, 13)
//  );

  public TEST_CopycatBlock(Properties properties) {
    super(properties);
  }

  @Override
  public VoxelShape getShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    return TEST_SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    CollisionContext context
  ) {
    return TEST_SHAPE;
  }

  @Override
  public Class<CCCopycatBlockEntity> getBlockEntityClass() {
    return CCCopycatBlockEntity.class;
  }

  @Override
  public net.minecraft.world.level.block.entity.BlockEntityType<? extends CCCopycatBlockEntity>
  getBlockEntityType() {
    return ModBlockEntities.TEST_COPYCAT_BLOCK.get();
  }

  @Override
  public BlockState transform(
    BlockState state,
    StructureTransform transform
  ) {
    return state;
  }

  @Override
  public boolean supportsExternalFaceHiding(BlockState state) {
    return true;
  }

  @Override
  public boolean hidesNeighborFace(
    BlockGetter level,
    BlockPos pos,
    BlockState state,
    BlockState neighborState,
    Direction direction
  ) {
    BlockPos neighborPos = pos.relative(direction);
    BlockState ownMaterial = ICopycatBlock.getMaterial(level, pos);

    BlockState neighborMaterial = neighborState.getBlock() instanceof ICopycatBlock
      ? ICopycatBlock.getMaterial(level, neighborPos)
      : neighborState;

    return BlockFaceUtils.canOcclude(
      level,
      neighborMaterial,
      neighborPos,
      ownMaterial,
      pos,
      direction.getOpposite()
    ) || ownMaterial.skipRendering(
      neighborMaterial,
      direction.getOpposite()
    );
  }
}
