package com.ricedotwho.mcprotocol.protocol.event.events;

import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;

public record PacketReceivedEvent(Packet packet,
                                  ProtocolState state,
                                  PacketDirection direction,
                                  MinecraftClient proxy) {}