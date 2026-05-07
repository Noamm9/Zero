package com.ricedotwho.mcprotocol.protocol.net.pipeline;

import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.net.registry.MinecraftCodec;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundBundlePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundDelimiterPacket;
import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.ArrayList;
import java.util.List;

import static com.ricedotwho.mcprotocol.protocol.net.SessionHandler.PROTOCOL_STATE;

public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final PacketDirection direction;
    private List<Packet> currentPackets;

    public PacketDecoder(PacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int packetId = ByteBufUtils.readVarInt(in);

        ByteBuf data = in.readRetainedSlice(in.readableBytes());

        Packet packet = MinecraftCodec.CODEC.getCodec(ctx.channel().attr(PROTOCOL_STATE).get()).createPacket(direction, packetId, data);

        // Bundled packets are supposed to run on the same tick on the client, since what we are doing does not really need that ill ignore them for now
        //if (checkBundledPacket(packet, out)) return;

        out.add(packet);
    }

    private boolean checkBundledPacket(Packet packet, List<Object> out) {
        if (currentPackets != null) {
            if (packet.getClass() == ClientboundDelimiterPacket.class) {
                out.add(new ClientboundBundlePacket(currentPackets));
                currentPackets = null;
            } else {
                currentPackets.add(packet);
            }
            return true;
        } else {
            if (packet.getClass() == ClientboundDelimiterPacket.class) {
                currentPackets = new ArrayList<>(2);
            }
            return false;
        }
    }
}
