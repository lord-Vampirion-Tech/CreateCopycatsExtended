package net.ziftech.copycats_extended.client;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.world.level.block.Block;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.client.model.CopycatAdvSlopeModelCore;
import net.ziftech.copycats_extended.client.model.CopycatAdvSlopeLayerModelCore;
import net.ziftech.copycats_extended.client.model.TEST.TEST_CopycatBlockModelCore;
import net.ziftech.copycats_extended.registry.ModBlocks;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public final class CopycatsExtendedClientEvents {

  @SubscribeEvent
  public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
    registerModel(
      event,
      ModBlocks.COPYCAT_ADV_SLOPE.get(),
      new CopycatAdvSlopeModelCore()
    );

    registerModel(
      event,
      ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get(),
      new CopycatAdvSlopeLayerModelCore()
    );

    registerModel(
      event,
      ModBlocks.TEST_COPYCAT_BLOCK.get(),
      new TEST_CopycatBlockModelCore()
    );
  }

  private static void registerModel(
    ModelEvent.ModifyBakingResult event,
    Block block,
    CopycatModelCore modelCore
  ) {
    for (var state : block.getStateDefinition().getPossibleStates()) {
      var modelLocation = BlockModelShaper.stateToModelLocation(state);

      event.getModels().computeIfPresent(
        modelLocation,
        (location, originalModel) ->
          CopycatModelCore.createModel(originalModel, modelCore)
      );
    }
  }

  @SubscribeEvent
  public static void registerBlockColors(
    RegisterColorHandlersEvent.Block event
  ) {
    event.register(
      ICopycatBlock.wrappedColor(),
      ModBlocks.COPYCAT_ADV_SLOPE.get(),
      ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get()
    );
  }
}