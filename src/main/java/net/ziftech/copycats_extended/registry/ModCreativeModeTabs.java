package net.ziftech.copycats_extended.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.ziftech.copycats_extended.CopycatsExtended;

import static net.ziftech.copycats_extended.registry.ModRegistryNames.MOD_NAME_MENU;

public class ModCreativeModeTabs {

  public static final DeferredRegister<CreativeModeTab> TABS =
    DeferredRegister.create(
      Registries.CREATIVE_MODE_TAB,
      CopycatsExtended.MOD_ID
    );

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN =
    TABS.register("main", () -> CreativeModeTab.builder()
      .title(Component.translatable("itemGroup.copycats_extended." + MOD_NAME_MENU))
      .icon(() -> new ItemStack(ModItems.COPYCAT_KEY.get()))
      .displayItems((parameters, output) -> {
        ModItems.getCreativeTabItems().forEach(output::accept);
      })
      .build()
    );

}
