package com.ricedotwho.mcprotocol.protocol.net.pipeline;

import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import com.ricedotwho.zero.Zero;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class VarIntFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        in.markReaderIndex();

        int length = ByteBufUtils.readVarInt(in);
        if (length == -1) {
            in.resetReaderIndex();
            return;
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf buf = in.readRetainedSlice(length);
        out.add(buf);
    }
}
