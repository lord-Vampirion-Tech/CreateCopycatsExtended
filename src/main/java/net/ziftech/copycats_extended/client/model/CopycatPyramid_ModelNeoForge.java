package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.neoforge.CopycatModelNeoForge;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramid_Entity;
import net.ziftech.copycats_extended.block.Pyramids.PyramidModelData;

import org.jetbrains.annotations.NotNull;

public class CopycatPyramid_ModelNeoForge extends CopycatModelNeoForge {

  private static final ModelProperty<PyramidModelData> PYRAMID_DATA =
    new ModelProperty<>();

  public CopycatPyramid_ModelNeoForge(
    BakedModel originalModel,
    CopycatModelCore core,
    boolean disableAO
  ) {
    super(originalModel, core, disableAO);
  }

  @Override
  public ModelData.Builder gatherModelData(
    ModelData.Builder builder,
    BlockAndTintGetter world,
    BlockPos pos,
    BlockState state,
    ModelData blockEntityData
  ) {
    super.gatherModelData(builder, world, pos, state, blockEntityData);

    if (world.getBlockEntity(pos) instanceof CopycatPyramid_Entity pyramid) {
      builder.with(
        PYRAMID_DATA,
        new PyramidModelData(
          pyramid.getTopPointX(),
          pyramid.getTopPointY(),
          pyramid.getTopPointZ()
        )
      );
    }

    return builder;
  }

  @Override
  protected void prepareModelCore(
    @NotNull BlockState state,
    @NotNull RandomSource rand,
    @NotNull ModelData data
  ) {
    super.prepareModelCore(state, rand, data);

    if (core instanceof CopycatModelCore.WithData<?> rawCore) {
      @SuppressWarnings("unchecked")
      CopycatModelCore.WithData<PyramidModelData> pyramidCore =
        (CopycatModelCore.WithData<PyramidModelData>) rawCore;

      PyramidModelData pyramidData = data.get(PYRAMID_DATA);

      if (pyramidData == null) {
        pyramidData = new PyramidModelData(4, 8, 4);
      }

      pyramidCore.setData(pyramidData);
    }
  }
}