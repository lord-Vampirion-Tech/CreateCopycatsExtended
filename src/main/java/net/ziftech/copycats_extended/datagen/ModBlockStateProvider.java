package net.ziftech.copycats_extended.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.registry.ModBlocks;

public final class ModBlockStateProvider extends BlockStateProvider {

  public ModBlockStateProvider(
    PackOutput output,
    ExistingFileHelper existingFileHelper
  ) {
    super(output, CopycatsExtended.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    registerAirState(ModBlocks.COPYCAT_ADV_SLOPE.get());
    registerAirState(ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get());
    registerAirState(ModBlocks.TEST_COPYCAT_BLOCK.get());
  }

  private void registerAirState(Block block) {
    getVariantBuilder(block)
      .partialState()
      .setModels(
        ConfiguredModel.builder()
          .modelFile(models().getExistingFile(mcLoc("block/air")))
          .build()
      );
  }
}
