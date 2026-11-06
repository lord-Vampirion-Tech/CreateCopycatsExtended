
package net.ziftech.copycats_extended.registry;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeBlockEntity;
import net.ziftech.copycats_extended.block.CopycatAdvSlopeLayerBlockEntity;
import net.ziftech.copycats_extended.block.TEST.TEST_CopycatBlock_Entity;

public final class ModBlockEntities {

  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
    DeferredRegister.create(
      Registries.BLOCK_ENTITY_TYPE,
      CopycatsExtended.MOD_ID
    );

  private ModBlockEntities() {
  }

  @SafeVarargs
  private static <T extends BlockEntity>
  DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntity(
    String name,
    BlockEntitySupplier<T> factory,
    Supplier<? extends Block>... validBlocks
  ) {
    return BLOCK_ENTITIES.register(
      name,
      () -> {
        Block[] blocks = new Block[validBlocks.length];

        for (int i = 0; i < validBlocks.length; i++) {
          blocks[i] = validBlocks[i].get();
        }

        return BlockEntityType.Builder
          .of(factory, blocks)
          .build(null);
      }
    );
  }

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopycatAdvSlopeBlockEntity>>
    COPYCAT_ADV_SLOPE = registerBlockEntity(
    "copycat_adv_slope",
    CopycatAdvSlopeBlockEntity::new,
    ModBlocks.COPYCAT_ADV_SLOPE
  );

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopycatAdvSlopeLayerBlockEntity>>
    COPYCAT_ADV_SLOPE_LAYER = registerBlockEntity(
    "copycat_adv_slope_layer",
    CopycatAdvSlopeLayerBlockEntity::new,
    ModBlocks.COPYCAT_ADV_SLOPE_LAYER
  );

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TEST_CopycatBlock_Entity>>
    TEST_COPYCAT_BLOCK = registerBlockEntity(
    "test_copycat_block",
    TEST_CopycatBlock_Entity::new,
    ModBlocks.TEST_COPYCAT_BLOCK
  );

}
