
package net.ziftech.copycats_extended.registry.tooltip;

import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class CopycatSlopeTooltip {

  public static void register(Item item) {
    TooltipModifier.REGISTRY.register(
      item,
      createModCharacteristicsModifier(item)
        .andThen(CopycatDescription.create(item))
        .andThen(
          new ItemDescription.Modifier(
            item,
            FontHelper.Palette.STANDARD_CREATE
          )
        )
        .andThen(
          TooltipModifier.mapNull(KineticStats.create(item))
        )
    );
  }

  private static TooltipModifier createModCharacteristicsModifier(Item item) {
    return event -> {
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

        lines.add(
          Component.literal("- " + language.getOrDefault(titleKey))
            .withStyle(ChatFormatting.GRAY)
        );

        if (Screen.hasShiftDown()) {
          Object[] args = Arrays.stream(characteristic.getArgs())
            .map(Supplier::get)
            .toArray();

          String description = String.format(
            language.getOrDefault(descriptionKey),
            args
          );

          lines.addAll(
            TooltipHelper.cutStringTextComponent(
              description,
              FontHelper.Palette.STANDARD_CREATE
            )
          );
        }
      }

      event.getToolTip().addAll(1, lines);
    };
  }
}
