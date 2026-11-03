
package net.ziftech.copycats_extended.datagen;

import java.util.List;
import java.util.Set;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.BlockLootSubProvider;

import net.ziftech.copycats_extended.registry.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class ModLootTableProvider extends LootTableProvider {

  public ModLootTableProvider(
    PackOutput output,
    CompletableFuture<HolderLookup.Provider> lookupProvider
  ) {
    super(
      output,
      Set.of(),
      List.of(
        new SubProviderEntry(
          ModBlockLootTables::new,
          LootContextParamSets.BLOCK
        )
      ),
      lookupProvider
    );
  }

  private static final class ModBlockLootTables
    extends BlockLootSubProvider {

    private ModBlockLootTables(
      HolderLookup.Provider lookupProvider
    ) {
      super(
        Set.of(),
        FeatureFlags.DEFAULT_FLAGS,
        lookupProvider
      );
    }

    @Override
    protected void generate() {
      dropSelf(ModBlocks.COPYCAT_ADV_SLOPE.get());
      dropSelf(ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get());
      dropSelf(ModBlocks.TEST_COPYCAT_BLOCK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
      return List.of(
        ModBlocks.COPYCAT_ADV_SLOPE.get(),
        ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get(),
        ModBlocks.TEST_COPYCAT_BLOCK.get()
      );
    }
  }
}
