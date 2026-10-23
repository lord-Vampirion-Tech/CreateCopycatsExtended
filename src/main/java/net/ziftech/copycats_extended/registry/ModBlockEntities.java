package net.ziftech.copycats_extended.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlockEntity;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlockEntity;

public class ModBlockEntities {

  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
    DeferredRegister.create(
      Registries.BLOCK_ENTITY_TYPE,
      CopycatsExtended.MOD_ID
    );

  public static final DeferredHolder<BlockEntityType<?>,
    BlockEntityType<CopycatAdvSlopeBlockEntity>> COPYCAT_ADV_SLOPE =
    BLOCK_ENTITIES.register(
    "copycat_adv_slope",
    () -> BlockEntityType.Builder.of(
      CopycatAdvSlopeBlockEntity::new,
      ModBlocks.COPYCAT_ADV_SLOPE.get()
    ).build(null)
  );

  public static final DeferredHolder<BlockEntityType<?>,
    BlockEntityType<CopycatAdvSlopeLayerBlockEntity>> COPYCAT_ADV_SLOPE_LAYER =
    BLOCK_ENTITIES.register(
    "copycat_adv_slope_layer",
    () -> BlockEntityType.Builder.of(
      CopycatAdvSlopeLayerBlockEntity::new,
      ModBlocks.COPYCAT_ADV_SLOPE_LAYER.get()
    ).build(null)
  );
}
