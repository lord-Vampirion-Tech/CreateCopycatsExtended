package net.ziftech.copycats_extended.client.model;

import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public final class CopycatAdvSlopeLayerRotation {

  private CopycatAdvSlopeLayerRotation() {
  }

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      RadialWrenchMenu.registerRotationProperty(
        CopycatAdvSlopeLayerBlock.HALF,
        "Half"
      );
    });
  }
}