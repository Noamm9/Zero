package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundCustomReportDetailsPacket extends Packet {
    private Map<String, String> details;

    public ClientboundCustomReportDetailsPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundCustomReportDetailsPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.details = new HashMap<>();

        int count = MinecraftTypes.readVarInt(in);
        for (int i = 0; i < count; i++) {
            this.details.put(MinecraftTypes.readString(in, 128), MinecraftTypes.readString(in, 4096));
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.details.size());
        for (Map.Entry<String, String> entry : this.details.entrySet()) {
            MinecraftTypes.writeString(out, entry.getKey());
            MinecraftTypes.writeString(out, entry.getValue());
        }
    }
}
