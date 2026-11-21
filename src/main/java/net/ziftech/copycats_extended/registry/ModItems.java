
package net.ziftech.copycats_extended.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.copycatsplus.copycats.foundation.tooltip.CopycatCharacteristics;
import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.item.CopycatKeyItem;
import net.ziftech.copycats_extended.registry.tooltip.CopycatSlopeTooltip;
import net.ziftech.copycats_extended.registry.tooltip.ModCharacteristics;

public final class ModItems {

  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CopycatsExtended.MOD_ID);

  // Item registration helper
  private static DeferredItem<BlockItem> newItem(
    String name,
    Supplier<? extends Block> block,
    Object... characteristics
  ) {
    return ITEMS.register(name, () -> {
      BlockItem item = new BlockItem(
        block.get(),
        new Item.Properties()
      );

      List<CopycatCharacteristics> copycatCharacteristics = new ArrayList<>();
      List<ModCharacteristics> modCharacteristics = new ArrayList<>();

      for (Object characteristic : characteristics) {
        if (characteristic instanceof CopycatCharacteristics copycat) {
          copycatCharacteristics.add(copycat);
        } else if (characteristic instanceof ModCharacteristics mod) {
          modCharacteristics.add(mod);
        } else {
          throw new IllegalArgumentException("Unsupported characteristic: " + characteristic);
        }
      }


      if (!copycatCharacteristics.isEmpty() || !modCharacteristics.isEmpty()) {
        if (!modCharacteristics.isEmpty()) {
          ModCharacteristics.register(
            item,
            modCharacteristics.toArray(new ModCharacteristics[0])
          );
        }

        CopycatDescription.register(
          item,
          copycatCharacteristics.toArray(new CopycatCharacteristics[0])
        );

        CopycatSlopeTooltip.register(item);
      }


      return item;
    });
  }

  // Items
  public static final DeferredItem<BlockItem> BASE_BLOCK =
    newItem("base_block", ModBlocks.BASE_BLOCK);

//  public static final DeferredItem<BlockItem> COPYCAT_ADV_SLOPE =
//    newItem(
//      "copycat_adv_slope",
//      ModBlocks.COPYCAT_ADV_SLOPE,
//      CopycatCharacteristics.COPYCAT,
//      CopycatCharacteristics.CT_TOGGLE
//    );
//
//  public static final DeferredItem<BlockItem> COPYCAT_ADV_SLOPE_LAYER =
//    newItem(
//      "copycat_adv_slope_layer",
//      ModBlocks.COPYCAT_ADV_SLOPE_LAYER,
//      CopycatCharacteristics.COPYCAT,
//      CopycatCharacteristics.CT_TOGGLE,
//      CopycatCharacteristics.STACKABLE
//    );
//
//  public static final DeferredItem<BlockItem> TEST_COPYCAT_BLOCK =
//    newItem(
//      "test_copycat_block",
//      ModBlocks.TEST_COPYCAT_BLOCK,
//      CopycatCharacteristics.CT_TOGGLE,
//      ModCharacteristics.TEST_INFO,
//      ModCharacteristics.TEST_KEY
//    );

  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID =
    newItem(
      "copycat_pyramid",
      ModBlocks.COPYCAT_PYRAMID,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE
    );
  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID_CORNER =
    newItem(
      "copycat_pyramid_corner",
      ModBlocks.COPYCAT_PYRAMID_CORNER,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE
    );
//  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID_OPPOSITE =
//    newItem(
//      "copycat_pyramid_opposite",
//      ModBlocks.COPYCAT_PYRAMID_OPPOSITE,
//      CopycatCharacteristics.COPYCAT,
//      CopycatCharacteristics.CT_TOGGLE
//    );
//  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID_QUAD =
//    newItem(
//      "copycat_pyramid_quad",
//      ModBlocks.COPYCAT_PYRAMID_QUAD,
//      CopycatCharacteristics.COPYCAT,
//      CopycatCharacteristics.CT_TOGGLE
//    );
//  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID_TRIPLE =
//    newItem(
//      "copycat_pyramid_triple",
//      ModBlocks.COPYCAT_PYRAMID_TRIPLE,
//      CopycatCharacteristics.COPYCAT,
//      CopycatCharacteristics.CT_TOGGLE
//    );

  public static final DeferredItem<BlockItem> DOUBLE_COPYCAT =
    newItem(
      "double_copycat",
      ModBlocks.DOUBLE_BLOCK,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE
    );


  // ITEM'S
  public static final DeferredItem<CopycatKeyItem> COPYCAT_KEY =
    ITEMS.register(
      "copycat_key",
      () -> new CopycatKeyItem(new Item.Properties().stacksTo(1))
    );
}
