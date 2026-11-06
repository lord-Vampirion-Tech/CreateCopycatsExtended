package net.ziftech.copycats_extended.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.registry.ModBlocks;

public final class ModItemModelProvider extends ItemModelProvider {

  private static final ResourceLocation COPYCAT_BLOCK_MODEL =
    ResourceLocation.fromNamespaceAndPath("copycats", "item/copycat_block");

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

    getBuilder(name)
      .parent(new ModelFile.UncheckedModelFile(COPYCAT_BLOCK_MODEL));
  }
}