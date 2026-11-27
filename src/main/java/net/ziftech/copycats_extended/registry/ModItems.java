
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
  public static final DeferredItem<BlockItem> BASE_BLOCK = newItem(ModRegistryNames.BASE_BLOCK, ModBlocks.BASE_BLOCK);

  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID =
    newItem(
      ModRegistryNames.COPYCAT_PYRAMID,
      ModBlocks.COPYCAT_PYRAMID,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE
    );

  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID_LAYER =
    newItem(
      ModRegistryNames.COPYCAT_PYRAMID_LAYER,
      ModBlocks.COPYCAT_PYRAMID_LAYER,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE,
      CopycatCharacteristics.STACKABLE
    );

  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID_SIDE =
    newItem(
      ModRegistryNames.COPYCAT_PYRAMID_SIDE,
      ModBlocks.COPYCAT_PYRAMID_SIDE,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE
    );

  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID_CORNER =
    newItem(
      ModRegistryNames.COPYCAT_PYRAMID_CORNER,
      ModBlocks.COPYCAT_PYRAMID_CORNER,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE
    );

  public static final DeferredItem<BlockItem> COPYCAT_PYRAMID_SIDE_CORNER =
    newItem(
      ModRegistryNames.COPYCAT_PYRAMID_SIDE_CORNER,
      ModBlocks.COPYCAT_PYRAMID_SIDE_CORNER,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE
    );

  public static final DeferredItem<BlockItem> DOUBLE_COPYCAT =
    newItem(
      ModRegistryNames.DOUBLE_COPYCAT,
      ModBlocks.DOUBLE_BLOCK,
      CopycatCharacteristics.COPYCAT,
      CopycatCharacteristics.CT_TOGGLE
    );

  // ITEM'S
  public static final DeferredItem<CopycatKeyItem> COPYCAT_KEY =
    ITEMS.register(
      ModRegistryNames.COPYCAT_KEY,
      () -> new CopycatKeyItem(new Item.Properties().stacksTo(1))
    );

  public static List<Item> getCreativeTabItems() {
    return List.of(
      COPYCAT_KEY.get(),

      BASE_BLOCK.get(),

      COPYCAT_PYRAMID.get(),
      COPYCAT_PYRAMID_SIDE.get(),
      COPYCAT_PYRAMID_CORNER.get(),
      COPYCAT_PYRAMID_SIDE_CORNER.get(),

      COPYCAT_PYRAMID_LAYER.get(),

      DOUBLE_COPYCAT.get()
    );
  }
}
