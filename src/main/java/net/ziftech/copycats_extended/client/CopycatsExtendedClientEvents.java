package net.ziftech.copycats_extended.client;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;

import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.client.model.*;
import net.ziftech.copycats_extended.registry.ModBlocks;

@EventBusSubscriber(
  modid = CopycatsExtended.MOD_ID,
  value = Dist.CLIENT
)
public final class CopycatsExtendedClientEvents {

  @SubscribeEvent
  public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
//    registerModel(
//      event,
//      ModBlocks.COPYCAT_ADV_SLOPE.get(),
//      new CopycatAdvSlopeModelCore()
//    );
//
//    registerModel(
//      event,
//      ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get(),
//      new CopycatAdvSlopeLayerModelCore()
//    );
//
//    registerModel(
//      event,
//      ModBlocks.TEST_COPYCAT_BLOCK.get(),
//      new TEST_CopycatBlock_ModelCore()
//    );

//    registerModel(event, ModBlocks.COPYCAT_PYRAMID_OPPOSITE.get(), pyramidModel);
//    registerModel(event, ModBlocks.COPYCAT_PYRAMID_QUAD.get(), pyramidModel);
//    registerModel(event, ModBlocks.COPYCAT_PYRAMID_TRIPLE.get(), pyramidModel);

    registerModel(event, ModBlocks.DOUBLE_BLOCK.get(), new DoubleBlock_ModelCore());


    CopycatModelCore pyramidModel = new CopycatPyramid_ModelCore();

    registerModel(event, ModBlocks.COPYCAT_PYRAMID.get(), pyramidModel, true);
    registerModel(event, ModBlocks.COPYCAT_PYRAMID_CORNER.get(), pyramidModel, true);

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

  private static void registerModel(
    ModelEvent.ModifyBakingResult event,
    Block block,
    CopycatModelCore modelCore,
    boolean adjustSlopeAO
  ) {
    for (var state : block.getStateDefinition().getPossibleStates()) {
      var modelLocation = BlockModelShaper.stateToModelLocation(state);

      event.getModels().computeIfPresent(
        modelLocation,
        (location, originalModel) -> {
          BakedModel model = CopycatModelCore.createModel(originalModel, modelCore);

          if (adjustSlopeAO) {
            model = new PyramidAmbientOcclusionModel(model);
            model = new QuadDebugModel(model);
          }

          return model;
        }
      );
    }
  }

  @SubscribeEvent
  public static void registerBlockColors(
    RegisterColorHandlersEvent.Block event
  ) {
    event.register(
      ICopycatBlock.wrappedColor(),
//      ModBlocks.COPYCAT_ADV_SLOPE.get(),
//      ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get(),
//      ModBlocks.TEST_COPYCAT_BLOCK.get(),
      ModBlocks.COPYCAT_PYRAMID.get(),
      ModBlocks.COPYCAT_PYRAMID_CORNER.get()
//      ModBlocks.COPYCAT_PYRAMID_OPPOSITE.get(),
//      ModBlocks.COPYCAT_PYRAMID_QUAD.get(),
//      ModBlocks.COPYCAT_PYRAMID_TRIPLE.get()
    );

    // MultiState Copycat-блоки
    event.register(
      IMultiStateCopycatBlock.wrappedColor(),
      ModBlocks.DOUBLE_BLOCK.get()
    );
  }
}