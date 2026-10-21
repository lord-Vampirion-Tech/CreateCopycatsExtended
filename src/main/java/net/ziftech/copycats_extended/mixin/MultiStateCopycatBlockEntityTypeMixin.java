package net.ziftech.copycats_extended.mixin;

import com.copycatsplus.copycats.CCBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.ziftech.copycats_extended.registry.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public abstract class MultiStateCopycatBlockEntityTypeMixin {

  @Inject(
    method = "isValid",
    at = @At("RETURN"),
    cancellable = true
  )
  private void copycats_extended$allowStackedHalfLayerSlope(
    BlockState state,
    CallbackInfoReturnable<Boolean> cir
  ){
    if (CCBlockEntityTypes.MULTI_STATE_COPYCAT.get() == (Object) this
      && state.is(ModBlocks.COPYCAT_STACKED_HALF_LAYER_SLOPE.get())) {
      cir.setReturnValue(true);
    }
  }
}
