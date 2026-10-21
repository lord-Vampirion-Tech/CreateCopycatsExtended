
package net.ziftech.copycats_extended.client;

import com.copycatsplus.copycats.foundation.tooltip.CopycatDescription;
import com.copycatsplus.copycats.utility.TooltipUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.registry.ModItems;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public final class CopycatSlopeTooltip {

  private CopycatSlopeTooltip() {
  }

  @SubscribeEvent
  public static void onItemTooltip(ItemTooltipEvent event) {
    var stack = event.getItemStack();

    var slope = ModItems.COPYCAT_ADV_SLOPE.get();
    var slopeLayer = ModItems.COPYCAT_ADV_SLOPE_LAYER.get();

    var item = stack.is(slope)
      ? slope
      : stack.is(slopeLayer)
      ? slopeLayer
      : null;

    if (item == null) {
      return;
    }

    TooltipUtils.sequential(
      CopycatDescription.create(item),
      new ItemDescription.Modifier(
        item,
        FontHelper.Palette.STANDARD_CREATE
      )
    ).modify(event);
  }
}