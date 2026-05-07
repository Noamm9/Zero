package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSetCommandMinecartPacket extends Packet {
    private int entityId;
    private @NonNull String command;
    private boolean doesTrackOutput;

    public ServerboundSetCommandMinecartPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSetCommandMinecartPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.command = MinecraftTypes.readString(in);
        this.doesTrackOutput = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeString(out, this.command);
        out.writeBoolean(this.doesTrackOutput);
    }
}
