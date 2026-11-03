package net.ziftech.copycats_extended.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.registry.ModBlocks;

import net.minecraft.core.registries.BuiltInRegistries;

public final class ModItemModelProvider extends ItemModelProvider {

  public ModItemModelProvider(
    PackOutput output,
    ExistingFileHelper existingFileHelper
  ) {
    super(output, CopycatsExtended.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    registerBlockItem(ModBlocks.COPYCAT_ADV_SLOPE.get());
    registerBlockItem(ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get());
    registerBlockItem(ModBlocks.TEST_COPYCAT_BLOCK.get());
  }

  private void registerBlockItem(Block block) {
    String name = BuiltInRegistries.BLOCK
      .getKey(block)
      .getPath();

    withExistingParent(name, mcLoc("block/cube_all"))
      .texture("all", mcLoc("block/stone"));
  }
}
