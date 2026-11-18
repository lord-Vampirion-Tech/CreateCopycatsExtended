package net.ziftech.copycats_extended.client.model;

import com.copycatsplus.copycats.foundation.copycat.model.CopycatModelCore;
import com.copycatsplus.copycats.foundation.copycat.model.neoforge.CopycatModelNeoForge;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.ziftech.copycats_extended.block.Pyramids.PyramidModelData;
import net.ziftech.copycats_extended.block.Pyramids.CopycatPyramid_Entity;

public class CopycatPyramidModelNeoForge extends CopycatModelNeoForge {

  private static final ModelProperty<PyramidModelData> PYRAMID_PROPERTY =
    new ModelProperty<>();

  public CopycatPyramidModelNeoForge(
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

    BlockEntity blockEntity = world.getBlockEntity(pos);

    PyramidModelData pyramidData =
      blockEntity instanceof CopycatPyramid_Entity entity
        ? new PyramidModelData(
        entity.getTopPointX(),
        entity.getTopPointY(),
        entity.getTopPointZ()
      )
        : new PyramidModelData(4, 8, 4);

    return builder.with(PYRAMID_PROPERTY, pyramidData);
  }

  @Override
  protected void prepareModelCore(
    BlockState state,
    RandomSource rand,
    ModelData data
  ) {
    super.prepareModelCore(state, rand, data);

    if (core instanceof CopycatModelCore.WithData<?> dataCore) {
      @SuppressWarnings("unchecked")
      CopycatModelCore.WithData<PyramidModelData> pyramidCore =
        (CopycatModelCore.WithData<PyramidModelData>) dataCore;

      PyramidModelData pyramidData = data.get(PYRAMID_PROPERTY);

      if (pyramidData == null)
        pyramidData = new PyramidModelData(4, 8, 4);

      pyramidCore.setData(pyramidData);
    }
  }
}