package com.ricedotwho.mcprotocol.protocol.packet.configuration.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundUpdateEnabledFeaturesPacket extends Packet {
    private Key[] features;

    public ClientboundUpdateEnabledFeaturesPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundUpdateEnabledFeaturesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.features = new Key[MinecraftTypes.readVarInt(in)];
        for (int i = 0; i < this.features.length; i++) {
            this.features[i] = MinecraftTypes.readResourceLocation(in);
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.features.length);
        for (Key feature : this.features) {
            MinecraftTypes.writeResourceLocation(out, feature);
        }
    }
}
