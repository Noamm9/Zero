package com.ricedotwho.mcprotocol.protocol.net.registry;

import org.geysermc.mcprotocollib.protocol.data.ProtocolState;

import java.util.EnumMap;

public record PacketCodec(int protocolVersion, String minecraftVersion,
                          EnumMap<ProtocolState, PacketRegistry> stateProtocols) {

    public PacketRegistry getCodec(ProtocolState protocolState) {
        return this.stateProtocols.get(protocolState);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        Builder builder = new Builder();

        builder.protocolVersion = this.protocolVersion;
        builder.stateProtocols = this.stateProtocols;
        builder.minecraftVersion = this.minecraftVersion;

        return builder;
    }

    public static class Builder {
        private int protocolVersion = -1;
        private String minecraftVersion = null;
        private EnumMap<ProtocolState, PacketRegistry> stateProtocols = new EnumMap<>(ProtocolState.class);

        public Builder protocolVersion(int protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Builder minecraftVersion(String minecraftVersion) {
            this.minecraftVersion = minecraftVersion;
            return this;
        }

        public Builder state(ProtocolState state, MinecraftPacketRegistry protocol) {
            this.stateProtocols.put(state, protocol.build());
            return this;
        }

        public PacketCodec build() {
            return new PacketCodec(this.protocolVersion, this.minecraftVersion, this.stateProtocols);
        }
    }
}