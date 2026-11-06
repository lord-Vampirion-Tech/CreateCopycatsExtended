package net.ziftech.copycats_extended.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ModDataGenerators {
  public static void gatherData(GatherDataEvent event) {

    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

    // Client resources
    generator.addProvider(
      event.includeClient(),
      new ModBlockStateProvider(output, existingFileHelper)
    );

    generator.addProvider(
      event.includeClient(),
      new ModItemModelProvider(output, existingFileHelper)
    );

    ModLanguageProvider.register(generator, output, event.includeClient());

    // Server resources
    generator.addProvider(
      event.includeServer(),
      new ModLootTableProvider(output, event.getLookupProvider())
    );

    generator.addProvider(
      event.includeServer(),
      new ModBlockTagsProvider(output, event.getLookupProvider(), existingFileHelper)
    );
  }
}
