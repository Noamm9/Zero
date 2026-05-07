package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.scoreboard;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundResetScorePacket extends Packet {
    private @NonNull String owner;
    private @Nullable String objective;

    public ClientboundResetScorePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundResetScorePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.owner = MinecraftTypes.readString(in);
        this.objective = MinecraftTypes.readNullable(in, MinecraftTypes::readString);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.owner);
        MinecraftTypes.writeNullable(out, this.objective, MinecraftTypes::writeString);
    }
}
