
package net.ziftech.copycats_extended.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModPayloads {

  private ModPayloads() {}

  public static void register(IEventBus modEventBus) {
    modEventBus.addListener(ModPayloads::registerPayloads);
  }

  private static void registerPayloads(RegisterPayloadHandlersEvent event) {
    event.registrar("1").playToServer(
      CycleBlockPropertyPayload.TYPE,
      CycleBlockPropertyPayload.STREAM_CODEC,
      CycleBlockPropertyPayload::handle
    );
  }
}
