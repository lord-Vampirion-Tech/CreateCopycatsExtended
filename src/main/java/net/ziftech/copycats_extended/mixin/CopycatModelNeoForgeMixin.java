package net.ziftech.copycats_extended.mixin;

import com.copycatsplus.copycats.foundation.copycat.model.neoforge.CopycatModelNeoForge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.ziftech.copycats_extended.utility.CopycatFaceOcclusion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CopycatModelNeoForge.class)
public abstract class CopycatModelNeoForgeMixin {

  @Redirect(
    method = "gatherOcclusionData",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/level/block/Block;shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z"
    )
  )
  private boolean copycatsExtended$shouldRenderMaterialFace(
    BlockState state,
    BlockGetter level,
    BlockPos pos,
    Direction face,
    BlockPos neighborPos
  ) {
    return CopycatFaceOcclusion.shouldRenderFace(
      state,
      level,
      pos,
      face,
      neighborPos
    );
  }
}
