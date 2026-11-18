
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.ziftech.copycats_extended.CopycatsExtended;
import net.ziftech.copycats_extended.registry.ModItems;
import net.ziftech.copycats_extended.registry.CopycatBlockPropertiesForKey;

import java.util.List;

public record CycleBlockPropertyPayload(
  BlockPos pos,
  String propertyName,
  boolean reverse
) implements CustomPacketPayload {

  public static final Type<CycleBlockPropertyPayload> TYPE =
    new Type<>(ResourceLocation.fromNamespaceAndPath(CopycatsExtended.MOD_ID, "cycle_block_property"));

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

    if (!level.hasChunk(
      pos.getX() >> 4,
      pos.getZ() >> 4
    )) {
      return;
    }

    if (player.distanceToSqr(Vec3.atCenterOf(pos)) > 36.0)
      return;

    BlockState state = level.getBlockState(pos);
    List<Property<?>> properties = CopycatBlockPropertiesForKey.getProperties(state.getBlock());

    Property<?> selectedProperty = properties.stream()
      .filter(property -> property.getName().equals(payload.propertyName()))
      .findFirst()
      .orElse(null);

    if (selectedProperty == null || !state.hasProperty(selectedProperty))
      return;

    BlockState newState = cycleProperty(
      state,
      selectedProperty,
      payload.reverse()
    );

    if (newState != state) {
      level.setBlock(pos, newState, 3);
    }
  }

  private static BlockState cycleProperty(
    BlockState state,
    Property<?> property,
    boolean reverse
  ) {
    return cyclePropertyUnchecked(state, property, reverse);
  }

  private static <T extends Comparable<T>> BlockState cyclePropertyUnchecked(
    BlockState state,
    Property<T> property,
    boolean reverse
  ) {
    List<T> values = List.copyOf(property.getPossibleValues());

    if (values.isEmpty())
      return state;

    T currentValue = state.getValue(property);

    int currentIndex = values.indexOf(currentValue);
    int direction = reverse ? -1 : 1;
    int nextIndex = Math.floorMod(currentIndex + direction, values.size());

    return state.setValue(property, values.get(nextIndex));
  }
}
