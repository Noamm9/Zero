package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.border;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundInitializeBorderPacket extends Packet {
    private double newCenterX;
    private double newCenterZ;
    private double oldSize;
    private double newSize;
    private long lerpTime;
    private int newAbsoluteMaxSize;
    private int warningBlocks;
    private int warningTime;

    public ClientboundInitializeBorderPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundInitializeBorderPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.newCenterX = in.readDouble();
        this.newCenterZ = in.readDouble();
        this.oldSize = in.readDouble();
        this.newSize = in.readDouble();
        this.lerpTime = MinecraftTypes.readVarLong(in);
        this.newAbsoluteMaxSize = MinecraftTypes.readVarInt(in);
        this.warningBlocks = MinecraftTypes.readVarInt(in);
        this.warningTime = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeDouble(this.newCenterX);
        out.writeDouble(this.newCenterZ);
        out.writeDouble(this.oldSize);
        out.writeDouble(this.newSize);
        MinecraftTypes.writeVarLong(out, this.lerpTime);
        MinecraftTypes.writeVarInt(out, this.newAbsoluteMaxSize);
        MinecraftTypes.writeVarInt(out, this.warningBlocks);
        MinecraftTypes.writeVarInt(out, this.warningTime);
    }
}
