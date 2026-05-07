package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundCommandSuggestionsPacket extends Packet {
    private int transactionId;
    private int start;
    private int length;
    private @NonNull String @NonNull [] matches;
    private Component @NonNull [] tooltips;

    public ClientboundCommandSuggestionsPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundCommandSuggestionsPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.transactionId = MinecraftTypes.readVarInt(in);
        this.start = MinecraftTypes.readVarInt(in);
        this.length = MinecraftTypes.readVarInt(in);
        this.matches = new String[MinecraftTypes.readVarInt(in)];
        this.tooltips = new Component[this.matches.length];
        for (int index = 0; index < this.matches.length; index++) {
            this.matches[index] = MinecraftTypes.readString(in);
            if (in.readBoolean()) {
                this.tooltips[index] = MinecraftTypes.readComponent(in);
            }
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.transactionId);
        MinecraftTypes.writeVarInt(out, this.start);
        MinecraftTypes.writeVarInt(out, this.length);
        MinecraftTypes.writeVarInt(out, this.matches.length);
        for (int index = 0; index < this.matches.length; index++) {
            MinecraftTypes.writeString(out, this.matches[index]);
            Component tooltip = this.tooltips[index];
            if (tooltip != null) {
                out.writeBoolean(true);
                MinecraftTypes.writeComponent(out, tooltip);
            } else {
                out.writeBoolean(false);
            }
        }
    }
}
