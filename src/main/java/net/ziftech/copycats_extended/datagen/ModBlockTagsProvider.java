
package net.ziftech.copycats_extended.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.registry.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.world.level.block.Block;

public final class ModBlockTagsProvider extends BlockTagsProvider {

  public ModBlockTagsProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> lookupProvider,
    ExistingFileHelper existingFileHelper
  ) {
    super(
      output,
      lookupProvider,
      CopycatsExtended.MOD_ID,
      existingFileHelper
    );
  }

  @Override
  protected void addTags(HolderLookup.@NotNull Provider provider) {
    // Blocks requiring an iron pickaxe or better
    List<Block> pickaxeBlocks = List.of(
      ModBlocks.COPYCAT_ADV_SLOPE.get(),
      ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get(),
      ModBlocks.TEST_COPYCAT_BLOCK.get(),
      ModBlocks.BASE_BLOCK.get()
    );

    // Pickaxe mining
    var pickaxeTag = tag(BlockTags.MINEABLE_WITH_PICKAXE);

    // Iron pickaxe or better
    var ironToolTag = tag(BlockTags.NEEDS_IRON_TOOL);

    for (Block block : pickaxeBlocks) {
      pickaxeTag.add(block);
      ironToolTag.add(block);
    }
  }
}
