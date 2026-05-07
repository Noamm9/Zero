package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.scoreboard;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.chat.numbers.NumberFormat;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetScorePacket extends Packet {
    private @NonNull String owner;
    private @NonNull String objective;
    private int value;
    private @Nullable Component display;
    private @Nullable NumberFormat numberFormat;

    public ClientboundSetScorePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetScorePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.owner = MinecraftTypes.readString(in);
        this.objective = MinecraftTypes.readString(in);
        this.value = MinecraftTypes.readVarInt(in);
        this.display = MinecraftTypes.readNullable(in, MinecraftTypes::readComponent);
        this.numberFormat = MinecraftTypes.readNullable(in, MinecraftTypes::readNumberFormat);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeString(out, this.owner);
        MinecraftTypes.writeString(out, this.objective);
        MinecraftTypes.writeVarInt(out, this.value);
        MinecraftTypes.writeNullable(out, this.display, MinecraftTypes::writeComponent);
        MinecraftTypes.writeNullable(out, this.numberFormat, MinecraftTypes::writeNumberFormat);
    }
}
