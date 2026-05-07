package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.RotationOrigin;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundPlayerLookAtPacket extends Packet {
    private @NonNull RotationOrigin origin;
    private double x;
    private double y;
    private double z;

    private int targetEntityId;
    private RotationOrigin targetEntityOrigin;

    public ClientboundPlayerLookAtPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundPlayerLookAtPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.origin = RotationOrigin.from(MinecraftTypes.readVarInt(in));
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();

        if (in.readBoolean()) {
            this.targetEntityId = MinecraftTypes.readVarInt(in);
            this.targetEntityOrigin = RotationOrigin.from(MinecraftTypes.readVarInt(in));
        } else {
            this.targetEntityId = 0;
            this.targetEntityOrigin = null;
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.origin.ordinal());
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);

        if (this.targetEntityOrigin != null) {
            out.writeBoolean(true);
            MinecraftTypes.writeVarInt(out, this.targetEntityId);
            MinecraftTypes.writeVarInt(out, this.origin.ordinal());
        } else {
            out.writeBoolean(false);
        }
    }
}
