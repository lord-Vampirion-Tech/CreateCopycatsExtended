package net.ziftech.copycats_extended.registry;

import java.util.function.Function;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock;
import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock;

public class ModBlocks {

  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CopycatsExtended.MOD_ID);

  // Block registration helper
  private static <T extends Block> DeferredBlock<T> registerBlock(
    String name,
    Function<BlockBehaviour.Properties, T> factory
  ) {
    return BLOCKS.register(
      name,
      () -> factory.apply(
        BlockBehaviour.Properties.of()
          .strength(1.5F, 6.0F)
      )
    );
  }

  private static <T extends Block> DeferredBlock<T> registerCopycatBlock(
    String name,
    Function<BlockBehaviour.Properties, T> factory
  ) {
    return BLOCKS.register(
      name,
      () -> factory.apply(
        BlockBehaviour.Properties.of()
          .strength(1.5F, 6.0F)
          .noOcclusion()
      )
    );
  }

  // Blocks
  public static final DeferredBlock<Block> BASE_BLOCK =
    registerCopycatBlock("base_block", Block::new);

  public static final DeferredBlock<CopycatAdvSlopeBlock> COPYCAT_ADV_SLOPE =
    registerCopycatBlock("copycat_adv_slope", CopycatAdvSlopeBlock::new);

  public static final DeferredBlock<CopycatAdvSlopeLayerBlock> COPYCAT_ADV_SLOPE_LAYER =
    registerCopycatBlock("copycat_adv_slope_layer", CopycatAdvSlopeLayerBlock::new);

  public static final DeferredBlock<TEST_CopycatBlock> TEST_COPYCAT_BLOCK =
    registerCopycatBlock("TEST_copycat_block", TEST_CopycatBlock::new);
}
