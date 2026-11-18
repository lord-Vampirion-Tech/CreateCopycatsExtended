package net.ziftech.copycats_extended.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.item.CopycatKey.CopycatKeyBlockList;
import net.ziftech.copycats_extended.registry.ModItems;
import net.ziftech.copycats_extended.item.CopycatKey.CopycatBlockPropertiesForKey;
import net.ziftech.copycats_extended.item.CopycatKey.CopycatBlockPropertiesForKey.KeyProperty;

import java.util.List;

public record CycleBlockPropertyPayload(
  BlockPos pos,
  String propertyName,
  boolean reverse
) implements CustomPacketPayload {

  public static final Type<CycleBlockPropertyPayload> TYPE =
    new Type<>(ResourceLocation.fromNamespaceAndPath(
      CopycatsExtended.MOD_ID,
      "cycle_block_property"
    ));

  public static final StreamCodec<ByteBuf, CycleBlockPropertyPayload> STREAM_CODEC =
    StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      CycleBlockPropertyPayload::pos,
      ByteBufCodecs.STRING_UTF8,
      CycleBlockPropertyPayload::propertyName,
      ByteBufCodecs.BOOL,
      CycleBlockPropertyPayload::reverse,
      CycleBlockPropertyPayload::new
    );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(
    CycleBlockPropertyPayload payload,
    IPayloadContext context
  ) {
    if (!(context.player() instanceof ServerPlayer player))
      return;

    if (!player.getMainHandItem().is(ModItems.COPYCAT_KEY.get()))
      return;

    Level level = player.level();
    BlockPos pos = payload.pos();

    if (!level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4))
      return;

    if (player.distanceToSqr(Vec3.atCenterOf(pos)) > 36.0)
      return;

    BlockState state = level.getBlockState(pos);

    List<KeyProperty> properties = CopycatKeyBlockList.getProperties(state.getBlock());

    KeyProperty selectedProperty = properties.stream()
      .filter(property -> property.name().equals(payload.propertyName()))
      .findFirst()
      .orElse(null);

    if (selectedProperty == null)
      return;

    CopycatBlockPropertiesForKey.cycleProperty(
      level,
      pos,
      state,
      selectedProperty,
      payload.reverse()
    );
  }
}