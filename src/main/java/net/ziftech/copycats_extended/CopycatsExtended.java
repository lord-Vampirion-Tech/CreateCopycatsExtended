
package net.ziftech.copycats_extended;

import com.copycatsplus.copycats.foundation.tooltip.CopycatCharacteristics;
import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

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
    }
}
