package net.ziftech.copycats_extended.mixin;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.utility.BlockFaceUtils;

import com.copycatsplus.copycats.foundation.copycat.model.neoforge.CopycatModelNeoForge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
    BlockState copycatState,
    BlockGetter level,
    BlockPos copycatPos,
    Direction face,
    BlockPos neighborPos,
    BlockState state,
    BlockGetter world,
    BlockPos pos,
    BlockState material
  ) {
    BlockState neighborState = level.getBlockState(neighborPos);

    BlockState neighborMaterial = neighborState.getBlock() instanceof ICopycatBlock
      ? ICopycatBlock.getMaterial(level, neighborPos)
      : neighborState;

    if (BlockFaceUtils.canOcclude(
      level,
      material,
      copycatPos,
      neighborMaterial,
      neighborPos,
      face
    )) {
      return false;
    }

    if (material.skipRendering(neighborMaterial, face)) {
      return false;
    }

    return Block.shouldRenderFace(
      copycatState,
      level,
      copycatPos,
      face,
      neighborPos
    );
  }
}