
package net.ziftech.copycats_extended.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import net.ziftech.copycats_extended.client.overlay.CopycatKeyOverlay;

public class CopycatKeyItem extends Item {

  public CopycatKeyItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    if (context.getLevel().isClientSide) {
      if (FMLEnvironment.dist == Dist.CLIENT) {
        CopycatKeyOverlay.handleKeyUse(context);
      }

      return InteractionResult.SUCCESS;
    }

    return InteractionResult.CONSUME;
  }
}
