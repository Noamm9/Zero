package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.particle.Particle;
import org.geysermc.mcprotocollib.protocol.data.game.level.particle.ParticleType;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundLevelParticlesPacket extends Packet {
    private @NonNull Particle particle;
    private boolean longDistance;
    private boolean alwaysShow;
    private double x;
    private double y;
    private double z;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float velocityOffset;
    private int amount;

    public ClientboundLevelParticlesPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundLevelParticlesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.longDistance = in.readBoolean();
        this.alwaysShow = in.readBoolean();
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.offsetX = in.readFloat();
        this.offsetY = in.readFloat();
        this.offsetZ = in.readFloat();
        this.velocityOffset = in.readFloat();
        this.amount = in.readInt();
        ParticleType type = MinecraftTypes.readParticleType(in);
        this.particle = new Particle(type, MinecraftTypes.readParticleData(in, type));
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeBoolean(this.longDistance);
        out.writeBoolean(this.alwaysShow);
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeFloat(this.offsetX);
        out.writeFloat(this.offsetY);
        out.writeFloat(this.offsetZ);
        out.writeFloat(this.velocityOffset);
        out.writeInt(this.amount);
        MinecraftTypes.writeParticleType(out, this.particle.getType());
        MinecraftTypes.writeParticleData(out, this.particle.getType(), this.particle.getData());
    }
}
