package com.ricedotwho.mcprotocol.protocol.net.pipeline;

import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class VarIntFrameEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int bodyLength = msg.readableBytes();

        ByteBufUtils.writeVarInt(out, bodyLength);
        out.writeBytes(msg);
    }
}
