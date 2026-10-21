package net.ziftech.copycats_extended.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.ziftech.copycats_extended.CopycatsExtended;

public class ModCreativeModeTabs {

  public static final DeferredRegister<CreativeModeTab> TABS =
    DeferredRegister.create(
      Registries.CREATIVE_MODE_TAB,
      CopycatsExtended.MOD_ID
    );

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN =
    TABS.register(
      "main",
      () -> CreativeModeTab.builder()
        .title(Component.translatable(
          "itemGroup.copycats_extended.main"
        ))
        .icon(() -> new ItemStack(
          ModItems.COPYCAT_ADV_SLOPE.get()
        ))
        .displayItems((parameters, output) -> {
          output.accept(ModItems.COPYCAT_ADV_SLOPE.get());
          output.accept(ModItems.COPYCAT_ADV_SLOPE_LAYER.get());
        })
        .build()
    );

  private ModCreativeModeTabs() {
  }
}
