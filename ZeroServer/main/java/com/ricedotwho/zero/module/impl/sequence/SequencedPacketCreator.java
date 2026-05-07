package com.ricedotwho.zero.module.impl.sequence;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;

@FunctionalInterface
public interface SequencedPacketCreator {
    Packet create(int sequence);
}