package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

import java.util.OptionalInt;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSetBeaconPacket extends Packet {
    private OptionalInt primaryEffect;
    private OptionalInt secondaryEffect;

    public ServerboundSetBeaconPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSetBeaconPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        if (in.readBoolean()) {
            this.primaryEffect = OptionalInt.of(MinecraftTypes.readVarInt(in));
        } else {
            this.primaryEffect = OptionalInt.empty();
        }

        if (in.readBoolean()) {
            this.secondaryEffect = OptionalInt.of(MinecraftTypes.readVarInt(in));
        } else {
            this.secondaryEffect = OptionalInt.empty();
        }
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeBoolean(this.primaryEffect.isPresent());
        if (this.primaryEffect.isPresent()) {
            MinecraftTypes.writeVarInt(out, this.primaryEffect.getAsInt());
        }

        out.writeBoolean(this.secondaryEffect.isPresent());
        if (this.secondaryEffect.isPresent()) {
            MinecraftTypes.writeVarInt(out, this.secondaryEffect.getAsInt());
        }
    }
}
