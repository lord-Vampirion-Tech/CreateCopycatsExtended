package net.ziftech.copycats_extended.registry.tooltip;

import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;
import com.copycatsplus.copycats.utility.TooltipUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import net.ziftech.copycats_extended.CopycatsExtended;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public final class CopycatSlopeTooltip {

  private static final Map<Item, TooltipModifier> MODIFIERS = new HashMap<>();

  @SubscribeEvent
  public static void onItemTooltip(ItemTooltipEvent event) {
    Item item = event.getItemStack().getItem();

    boolean hasCopycatCharacteristics = CopycatDescription.ITEM_CHARACTERISTICS.containsKey(item);
    boolean hasModCharacteristics = ModCharacteristics.has(item);

    if (!hasCopycatCharacteristics && !hasModCharacteristics) {
      return;
    }

    TooltipModifier modifier = MODIFIERS.computeIfAbsent(item, CopycatSlopeTooltip::createModifier);

    appendModCharacteristics(event, item);
    modifier.modify(event);
  }

  private static TooltipModifier createModifier(Item item) {
    boolean hasCopycatCharacteristics = CopycatDescription.ITEM_CHARACTERISTICS.containsKey(item);

    if (hasCopycatCharacteristics) {
      return TooltipUtils.sequential(
        CopycatDescription.create(item),
        new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
      );
    }

    return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE);
  }

  private static void appendModCharacteristics(
    ItemTooltipEvent event,
    Item item
  ) {
    List<ModCharacteristics> characteristics =
      ModCharacteristics.get(item);

    if (characteristics.isEmpty()) {
      return;
    }

    Language language = Language.getInstance();
    List<Component> lines = new ArrayList<>();

    for (ModCharacteristics characteristic : characteristics) {
      String titleKey = characteristic.getTitleKey();
      String descriptionKey = characteristic.getDescriptionKey();

      if (!language.has(titleKey) || !language.has(descriptionKey)) {
        continue;
      }

      lines.add(Component.literal("- " + language.getOrDefault(titleKey)).withStyle(ChatFormatting.GRAY));

      if (Screen.hasShiftDown()) {
        Object[] args = java.util.Arrays.stream(characteristic.getArgs())
          .map(Supplier::get)
          .toArray();

        String description = String.format(language.getOrDefault(descriptionKey), args);

        lines.addAll(TooltipHelper.cutStringTextComponent(description, FontHelper.Palette.STANDARD_CREATE));
      }
    }

    event.getToolTip().addAll(1, lines);
  }
}
