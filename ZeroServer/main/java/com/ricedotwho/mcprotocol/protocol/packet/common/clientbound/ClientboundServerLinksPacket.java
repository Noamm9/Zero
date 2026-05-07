package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.ServerLink;
import org.geysermc.mcprotocollib.protocol.data.game.ServerLinkType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundServerLinksPacket extends Packet {
    private List<ServerLink> links;

    public ClientboundServerLinksPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundServerLinksPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.links = new ArrayList<>();

        int length = MinecraftTypes.readVarInt(in);
        for (int i = 0; i < length; i++) {
            ServerLinkType knownType = null;
            Component unknownType = null;
            if (in.readBoolean()) {
                knownType = ServerLinkType.from(MinecraftTypes.readVarInt(in));
            } else {
                unknownType = MinecraftTypes.readComponent(in);
            }

            String link = MinecraftTypes.readString(in);
            this.links.add(new ServerLink(knownType, unknownType, link));
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.links.size());
        for (ServerLink link : this.links) {
            out.writeBoolean(link.knownType() != null);
            if (link.knownType() != null) {
                MinecraftTypes.writeVarInt(out, link.knownType().ordinal());
            } else {
                MinecraftTypes.writeComponent(out, link.unknownType());
            }

            MinecraftTypes.writeString(out, link.link());
        }
    }
}
