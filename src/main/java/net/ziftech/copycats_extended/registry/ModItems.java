
package net.ziftech.copycats_extended.registry;

import com.copycatsplus.copycats.foundation.tooltip.CopycatCharacteristics;
import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.ziftech.copycats_extended.CopycatsExtended;

public final class ModItems {

  public static final DeferredRegister.Items ITEMS =
    DeferredRegister.createItems(CopycatsExtended.MOD_ID);

  public static final DeferredItem<BlockItem> COPYCAT_ADV_SLOPE =
    ITEMS.register("copycat_adv_slope", () -> {
        BlockItem item = new BlockItem(
          ModBlocks.COPYCAT_ADV_SLOPE.get(),
          new Item.Properties()
        );

        CopycatDescription.register(
          item,
          CopycatCharacteristics.COPYCAT,
          CopycatCharacteristics.CT_TOGGLE
        );

        return item;
      }
    );

  public static final DeferredItem<BlockItem> COPYCAT_ADV_SLOPE_LAYER =
    ITEMS.register("copycat_adv_slope_layer", () -> {
        BlockItem item = new BlockItem(
          ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get(),
          new Item.Properties()
        );

        CopycatDescription.register(
          item,
          CopycatCharacteristics.COPYCAT,
          CopycatCharacteristics.CT_TOGGLE,
          CopycatCharacteristics.STACKABLE
        );

        return item;
      }
    );

  private ModItems() {
  }
}
