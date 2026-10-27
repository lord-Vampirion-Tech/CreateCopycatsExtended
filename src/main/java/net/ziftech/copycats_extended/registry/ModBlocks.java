package net.ziftech.copycats_extended.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock;

public class ModBlocks {

  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CopycatsExtended.MOD_ID);

  public static final DeferredBlock<Block> BASE_BLOCK =
    BLOCKS.register(
      "base_block",
      () -> new Block(
        BlockBehaviour.Properties.of()
          .strength(1.5F, 6.0F)
      )
    );
  public static final DeferredBlock<CopycatAdvSlopeBlock> COPYCAT_ADV_SLOPE =
    BLOCKS.register(
      "copycat_adv_slope",
      () -> new CopycatAdvSlopeBlock(BlockBehaviour.Properties.of()
        .strength(1.5F, 6.0F))
    );
  public static final DeferredBlock<CopycatAdvSlopeLayerBlock> COPYCAT_ADV_SLOPE_LAYER =
    BLOCKS.register(
      "copycat_adv_slope_layer",
      () -> new CopycatAdvSlopeLayerBlock(BlockBehaviour.Properties.of()
        .strength(1.5F, 6.0F))
    );
}
