package com.ricedotwho.mcprotocol.protocol.net.ping;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.mcprotocol.protocol.packet.ping.clientbound.ClientboundPongResponsePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ping.severbound.ServerboundPingRequestPacket;
import com.ricedotwho.mcprotocol.protocol.packet.status.clientbound.ClientboundStatusResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);
    private final ServerPinger client;

    public PingHandler(ServerPinger client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {

        if (packet instanceof ClientboundStatusResponsePacket) {
            ctx.writeAndFlush(new ServerboundPingRequestPacket(System.currentTimeMillis()));
        }
        else if (packet instanceof ClientboundPongResponsePacket pong) {
            pong.lazyDecode();
            log.info("Ping to {}:{} is {}ms", client.getHost(), client.getPort(), (System.currentTimeMillis() - pong.getPingTime()));
            this.client.disconnect();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.client.disconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}