package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3d;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.MinecartStep;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundMoveMinecartPacket extends Packet {
    private int entityId;
    private List<MinecartStep> lerpSteps;

    public ClientboundMoveMinecartPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundMoveMinecartPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.lerpSteps = MinecraftTypes.readList(in, (input) -> {
            Vector3d position = Vector3d.from(input.readDouble(), input.readDouble(), input.readDouble());
            Vector3d movement = Vector3d.from(input.readDouble(), input.readDouble(), input.readDouble());
            float yRot = input.readByte() * 360F / 256F;
            float xRot = input.readByte() * 360F / 256F;
            float weight = input.readFloat();
            return new MinecartStep(position, movement, yRot, xRot, weight);
        });
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeList(out, this.lerpSteps, (output, lerpStep) -> {
            output.writeDouble(lerpStep.position().getX());
            output.writeDouble(lerpStep.position().getY());
            output.writeDouble(lerpStep.position().getZ());

            output.writeDouble(lerpStep.movement().getX());
            output.writeDouble(lerpStep.movement().getY());
            output.writeDouble(lerpStep.movement().getZ());

            float yRot = lerpStep.yRot() * 256F / 360F;
            output.writeByte(yRot < (int)yRot ? (int)yRot - 1 : (int)yRot);
            float xRot = lerpStep.xRot() * 256F / 360F;
            output.writeByte(xRot < (int)xRot ? (int)xRot - 1 : (int)xRot);

            output.writeFloat(lerpStep.weight());
        });
    }
}
