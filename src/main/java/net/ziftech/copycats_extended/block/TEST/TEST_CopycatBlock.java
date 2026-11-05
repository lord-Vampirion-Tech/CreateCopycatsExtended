package net.ziftech.copycats_extended.block.TEST;

import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;

import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.world.phys.shapes.BooleanOp;
import net.ziftech.copycats_extended.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TEST_CopycatBlock extends CCWaterloggedCopycatBlock {

  private static final int SLOPE_SUBDIVISIONS = 4;

  private static VoxelShape createSlopeHitbox() {
    VoxelShape shape = Shapes.empty();

    for (int i = 0; i < SLOPE_SUBDIVISIONS; i++) {

      double zMin = i / (double) SLOPE_SUBDIVISIONS;
      double zMax = (i + 1.0) / SLOPE_SUBDIVISIONS;
      double yMax = (i + 1.0) / SLOPE_SUBDIVISIONS;

      VoxelShape section = Shapes.box(
        0,
        0,
        zMin,
        1,
        yMax,
        zMax
      );

      shape = Shapes.joinUnoptimized(shape, section, BooleanOp.OR);
    }

    return shape;
  }

  private static final VoxelShape TEST_SHAPE = createSlopeHitbox();

  public TEST_CopycatBlock(Properties properties) {
    super(properties);
  }

  @Override
  public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
    return TEST_SHAPE;
  }

  @Override
  public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
    return TEST_SHAPE;
  }

  @Override
  public @NotNull Class<CCCopycatBlockEntity> getBlockEntityClass() {
    return CCCopycatBlockEntity.class;
  }

  @Override
  public net.minecraft.world.level.block.entity.@NotNull BlockEntityType<? extends CCCopycatBlockEntity> getBlockEntityType() {
    return ModBlockEntities.TEST_COPYCAT_BLOCK.get();
  }

  @Override
  public @NotNull BlockState transform(@NotNull BlockState state, @NotNull StructureTransform transform) {
    return state;
  }
}
