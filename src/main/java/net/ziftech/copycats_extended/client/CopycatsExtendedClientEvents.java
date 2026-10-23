package net.ziftech.copycats_extended.client;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.client.model.CopycatAdvSlopeModelCore;
import net.ziftech.copycats_extended.client.model.CopycatAdvSlopeLayerModelCore;
import net.ziftech.copycats_extended.registry.ModBlocks;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public class CopycatsExtendedClientEvents {

  private CopycatsExtendedClientEvents() {
  }

  @SubscribeEvent
  public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
    var slope = ModBlocks.COPYCAT_ADV_SLOPE.get();
    for (var state : slope.getStateDefinition().getPossibleStates()) {
      ModelResourceLocation modelLocation =
        BlockModelShaper.stateToModelLocation(state);

      event.getModels().computeIfPresent(
        modelLocation,
        (location, originalModel) ->
          CopycatModelCore.createModel(
            originalModel,
            new CopycatAdvSlopeModelCore()
          )
      );
    }

    var slopeLayer = ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get();
    for (var state : slopeLayer.getStateDefinition().getPossibleStates()) {
      ModelResourceLocation modelLocation =
        BlockModelShaper.stateToModelLocation(state);

      event.getModels().computeIfPresent(
        modelLocation,
        (location, originalModel) ->
          CopycatModelCore.createModel(
            originalModel,
            new CopycatAdvSlopeLayerModelCore()
          )
      );
    }
  }

  @SubscribeEvent
  public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
    event.register(
      ICopycatBlock.wrappedColor(),
      ModBlocks.COPYCAT_ADV_SLOPE.get(),
      ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get()
    );
  }
}