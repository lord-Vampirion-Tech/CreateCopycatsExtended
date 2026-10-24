
package net.ziftech.copycats_extended.client.model;

import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public final class CopycatAdvSlopeRotation {

  private CopycatAdvSlopeRotation() {
  }

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      RadialWrenchMenu.registerRotationProperty(
        CopycatAdvSlopeBlock.HALF,
        "Half"
      );

      RadialWrenchMenu.registerRotationProperty(
        CopycatAdvSlopeBlock.SLOPE_MODE,
        "Slope Mode"
      );
    });

  }
}

