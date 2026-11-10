package net.ziftech.copycats_extended.client.TEST.model;

import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public class TEST_CopycatBlock_RotateMenu {
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      RadialWrenchMenu.registerRotationProperty(
        TEST_CopycatBlock.ENHANCED,
        "Enchanded"
      );
    });
  }
}
