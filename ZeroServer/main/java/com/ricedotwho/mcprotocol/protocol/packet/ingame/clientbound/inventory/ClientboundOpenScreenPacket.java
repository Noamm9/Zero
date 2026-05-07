package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.ContainerType;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundOpenScreenPacket extends Packet {
    private int containerId;
    private @NonNull ContainerType type;
    private @NonNull Component title;

    public ClientboundOpenScreenPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundOpenScreenPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.containerId = MinecraftTypes.readVarInt(in);
        this.type = ContainerType.from(MinecraftTypes.readVarInt(in));
        this.title = MinecraftTypes.readComponent(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.containerId);
        MinecraftTypes.writeVarInt(out, this.type.ordinal());
        MinecraftTypes.writeComponent(out, this.title);
    }
}
