
package net.ziftech.copycats_extended;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import net.ziftech.copycats_extended.datagen.ModDataGenerators;
import net.ziftech.copycats_extended.registry.ModBlockEntities;
import net.ziftech.copycats_extended.registry.ModBlocks;
import net.ziftech.copycats_extended.registry.ModCreativeModeTabs;
import net.ziftech.copycats_extended.registry.ModItems;

@Mod(CopycatsExtended.MOD_ID)
public final class CopycatsExtended {

    public static final String MOD_ID = "copycats_extended";

    public CopycatsExtended(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeModeTabs.TABS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(ModDataGenerators::gatherData);
    }
}
