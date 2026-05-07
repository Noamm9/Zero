package com.ricedotwho.mcprotocol.protocol.net.pipeline;

import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.net.registry.MinecraftCodec;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

import static com.ricedotwho.mcprotocol.protocol.net.SessionHandler.PROTOCOL_STATE;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final PacketDirection direction;

    public PacketEncoder(PacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {

        int id = MinecraftCodec.CODEC.getCodec(ctx.channel().attr(PROTOCOL_STATE).get()).getPacketId(direction, packet);

        if (!packet.isModified() && packet.getRawData() != null) {
            ByteBufUtils.writeVarInt(out, id);

            ByteBuf raw = packet.getRawData().retain();
            try {
                out.writeBytes(raw);
            } finally {
                ReferenceCountUtil.release(raw);
            }
            return;
        }

        ByteBufUtils.writeVarInt(out, id);
        packet.encode(out);
    }
}
