package net.ziftech.copycats_extended.block.Pyramids;

import com.copycatsplus.copycats.foundation.copycat.CCWaterloggedCopycatBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class TEST_CopycatPyramidBlock extends CCWaterloggedCopycatBlock {

  public static final DirectionProperty FACING =
    BlockStateProperties.FACING;

  public static final IntegerProperty ROTATION =
    IntegerProperty.create("rotation", 0, 1);

  public TEST_CopycatPyramidBlock(Properties properties) {
    super(properties);

    registerDefaultState(defaultBlockState()
      .setValue(FACING, Direction.UP)
      .setValue(ROTATION, 0));
  }

  @Override
  public BlockState transform(BlockState blockState, StructureTransform structureTransform) {
    return null;
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.Builder<Block, BlockState> builder
  ) {
    super.createBlockStateDefinition(builder);
    builder.add(FACING, ROTATION);
  }
}