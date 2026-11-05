package net.ziftech.copycats_extended.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.ziftech.copycats_extended.utility.CopycatFaceOcclusion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class CopycatModelNeoForgeMixin {

  @Inject(
    method = "shouldRenderFace",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void copycatsExtended$useMaterialOcclusion(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    Direction face,
    BlockPos neighborPos,
    CallbackInfoReturnable<Boolean> cir
  ) {
    Boolean result = CopycatFaceOcclusion.getMaterialFaceResult(
      state,
      level,
      pos,
      face,
      neighborPos
    );

    if (result != null) {
      cir.setReturnValue(result);
    }
  }
}
