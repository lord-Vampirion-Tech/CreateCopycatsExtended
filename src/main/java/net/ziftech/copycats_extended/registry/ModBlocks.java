package net.ziftech.copycats_extended.registry;

import java.util.function.Function;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.Pyramids.*;
import net.ziftech.copycats_extended.block.DoubleBlock;

public class ModBlocks {

  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CopycatsExtended.MOD_ID);

  // Block registration helper
  private static <T extends Block> DeferredBlock<T> registerBlock(
    String name,
    Function<BlockBehaviour.Properties, T> factory
  ) {
    return BLOCKS.register(
      name,
      () -> factory.apply(BlockBehaviour.Properties.of().strength(1.5F, 6.0F))
    );
  }

  private static <T extends Block> DeferredBlock<T> registerCopycatBlock(
    String name,
    Function<BlockBehaviour.Properties, T> factory
  ) {
    return BLOCKS.register(
      name,
      () -> factory.apply(
        BlockBehaviour.Properties.of().strength(1.5F, 6.0F).noOcclusion()
      )
    );
  }

  // Blocks
  public static final DeferredBlock<Block> BASE_BLOCK =
    registerBlock(ModRegistryNames.BASE_BLOCK, Block::new);

  public static final DeferredBlock<BASE_CopycatPyramidBlock> COPYCAT_PYRAMID =
    registerCopycatBlock(ModRegistryNames.COPYCAT_PYRAMID, CopycatPyramidBase::new);
  public static final DeferredBlock<BASE_CopycatPyramidBlock> COPYCAT_PYRAMID_SIDE =
    registerCopycatBlock(ModRegistryNames.COPYCAT_PYRAMID_SIDE, CopycatPyramidSide::new);
  public static final DeferredBlock<BASE_CopycatPyramidBlock> COPYCAT_PYRAMID_CORNER =
    registerCopycatBlock(ModRegistryNames.COPYCAT_PYRAMID_CORNER, CopycatPyramidCorner::new);
  public static final DeferredBlock<BASE_CopycatPyramidBlock> COPYCAT_PYRAMID_SIDE_CORNER =
    registerCopycatBlock(ModRegistryNames.COPYCAT_PYRAMID_SIDE_CORNER, CopycatPyramidSideCorner::new);

  public static final DeferredBlock<DoubleBlock> DOUBLE_BLOCK =
    registerCopycatBlock(ModRegistryNames.DOUBLE_COPYCAT, DoubleBlock::new);

}
