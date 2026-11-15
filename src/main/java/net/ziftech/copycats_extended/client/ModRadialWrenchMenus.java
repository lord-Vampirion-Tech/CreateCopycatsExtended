package net.ziftech.copycats_extended.client;

import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlock;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlock;
import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock;
import net.ziftech.copycats_extended.block.CopycatPyramid;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public class ModRadialWrenchMenus {

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {

      // TEST Copycat Block
      RadialWrenchMenu.registerRotationProperty(
        TEST_CopycatBlock.ENHANCED,
        "Enchanted"
      );

      // Copycat Pyramid
      RadialWrenchMenu.registerRotationProperty(
        CopycatPyramid.FACING,
        "Facing"
      );

      RadialWrenchMenu.registerRotationProperty(
        CopycatAdvSlopeLayerBlock.WALL_ORIENTATION,
        "Wall_Orientation"
      );

      RadialWrenchMenu.registerRotationProperty(
        CopycatAdvSlopeBlock.SLOPE_MODE,
        "Slope Mode"
      );
    });
  }
}