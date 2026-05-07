package com.ricedotwho.mcprotocol.protocol.net;

import com.ricedotwho.mcprotocol.Constants;
import com.ricedotwho.mcprotocol.data.ServerStatusInfo;
import com.ricedotwho.mcprotocol.protocol.MinecraftProtocol;
import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.pipeline.TcpPacketCompression;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.net.session.ClientSession;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.mcprotocol.protocol.packet.common.severbound.ServerboundCustomPayloadPacket;
import com.ricedotwho.mcprotocol.protocol.packet.configuration.severbound.ServerboundFinishConfigurationPacket;
import com.ricedotwho.mcprotocol.protocol.packet.handshake.serverbound.ServerboundClientIntentionPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundCustomQueryPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginCompressionPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginDisconnectPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.severbound.ServerboundCustomQueryAnswerPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.severbound.ServerboundHelloPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.severbound.ServerboundKeyPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.severbound.ServerboundLoginAcknowledgedPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ping.clientbound.ClientboundPongResponsePacket;
import com.ricedotwho.mcprotocol.protocol.packet.ping.severbound.ServerboundPingRequestPacket;
import com.ricedotwho.mcprotocol.protocol.packet.status.clientbound.ClientboundStatusResponsePacket;
import com.ricedotwho.mcprotocol.protocol.packet.status.severbound.ServerboundStatusRequestPacket;
import com.ricedotwho.mcprotocol.utils.CryptUtil;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.event.custom.events.ZeroPayloadEvent;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.util.ConnectionInfo;
import com.ricedotwho.zero.util.DataLoader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.auth.SessionService;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;
import org.geysermc.mcprotocollib.protocol.data.handshake.HandshakeIntent;
import org.geysermc.mcprotocollib.protocol.data.status.PlayerInfo;
import org.geysermc.mcprotocollib.protocol.data.status.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.*;

public class SessionHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Logger log = LoggerFactory.getLogger(SessionHandler.class);
    public static final AttributeKey<ProtocolState> PROTOCOL_STATE = AttributeKey.valueOf("protocol_state");

    private final Channel channel;

    private GameProfile profile = null;
    private final byte[] challenge = new byte[4];
    private final KeyPair pair = CryptUtil.generateKeyPair();
    private ConnectionInfo info;
    private String username = "Unknown";

    private MinecraftClient remoteClient;
    private final ClientSession connectedSession;

    public SessionHandler(Channel channel) {
        this.channel = channel;
        new Random().nextBytes(this.challenge);
        this.connectedSession = new ClientSession("", 25565);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        ProtocolState state = ctx.channel().attr(PROTOCOL_STATE).get();

        boolean forwarded = false;

        try {
            switch (state) {
                case HANDSHAKE -> {
                    if (packet instanceof ServerboundClientIntentionPacket handshake) {
                        handshake.lazyDecode();
                        if (handshake.getIntent() == HandshakeIntent.STATUS) {
                            setState(ctx, ProtocolState.STATUS);
                        } else {
                            setState(ctx, ProtocolState.LOGIN);
                            if (handshake.getProtocolVersion() != Constants.PROTOCOL_VERSION) {
                                ctx.writeAndFlush(new ClientboundLoginDisconnectPacket(Component.text("Please use version " + Constants.MC_VERSION + "!")));
                            }
                        }
                    }
                }

                case STATUS -> {
                    if (packet instanceof ServerboundStatusRequestPacket) {
                        Packet response = new ClientboundStatusResponsePacket(
                                new ServerStatusInfo(
                                        Component.text(DataLoader.getData().getMotd()),
                                        new PlayerInfo(DataLoader.getData().getMaxPlayers(), MinecraftProtocol.getPlayerCount(), List.of()),
                                        new VersionInfo(Constants.MC_VERSION, Constants.PROTOCOL_VERSION),
                                        DataLoader.getData().getIconPng(),
                                        null,
                                        false
                                )
                        );
                        ctx.writeAndFlush(response);
                    } else if (packet instanceof ServerboundPingRequestPacket pingPacket) {
                        pingPacket.lazyDecode();
                        ClientboundPongResponsePacket pong = new ClientboundPongResponsePacket(pingPacket.getPingTime());
                        ctx.writeAndFlush(pong);
                    }
                }

                case LOGIN -> {
                    if (packet instanceof ServerboundHelloPacket login) {
                        login.lazyDecode();
                        this.username = login.getUsername();

                        if (DataLoader.getData().isWhitelist() && !DataLoader.isWhitelistedUUID(login.getProfileId())) {
                            ctx.writeAndFlush(new ClientboundLoginDisconnectPacket(Component.text("You are not whitelisted.")));
                            this.connectedSession.disconnect(Component.text("Whitelist fail (" + login.getUsername() + ", " + login.getProfileId() + ")"), true);
                            return;
                        }
                        this.connectedSession.send(new ClientboundCustomQueryPacket(0, Key.key("zero", "auth/query_server"), new byte[]{}));
                    }
                    else if (packet instanceof ServerboundCustomQueryAnswerPacket query) {
                        query.lazyDecode();
                        if (query.getTransactionId() == Constants.SERVER_INFO) {
                            ByteBuf buf = Unpooled.wrappedBuffer(query.getData());
                            UUID uuid = MinecraftTypes.readUUID(buf);
                            String targetIp = MinecraftTypes.readString(buf);

                            int port = 25565;
                            if (targetIp.contains(":")) {
                                String[] temp = targetIp.split(":");
                                try {
                                    port = Integer.parseInt(temp[1]);
                                } catch (NumberFormatException e) {
                                    log.info("{} sent an invalid port: {}", ctx.channel().remoteAddress(), temp[1]);
                                } catch (IndexOutOfBoundsException e) {
                                    log.info("{} sent a malformed ip! {}", ctx.channel().remoteAddress(), targetIp);
                                }
                                targetIp = temp[0];
                            }

                            if (DataLoader.getData().isWhitelist() && !DataLoader.isWhitelistedUUID(uuid)) {
                                ctx.writeAndFlush(new ClientboundLoginDisconnectPacket(Component.translatable("multiplayer.disconnect.not_whitelisted")));
                                this.connectedSession.disconnect(Component.text("Whitelist fail (" + uuid + ", " + this.username + ")"), true);
                                return;
                            }

                            this.info = new ConnectionInfo(uuid, targetIp, port);
                            this.connectedSession.send(new ClientboundCustomQueryPacket(0, Key.key("zero", "auth/transferring"), new byte[]{}));
                            startRemoteClient(this.info);
                        } else if (query.getTransactionId() == Constants.AUTH_SUCCEEDED) {
                            this.remoteClient.onAuthCompleted();
                        }
                    }
                    else if (packet instanceof ServerboundKeyPacket keyPacket) {
                        PrivateKey privateKey = pair.getPrivate();

                        if (!Arrays.equals(this.challenge, keyPacket.getEncryptedChallenge(privateKey))) {
                            throw new IllegalStateException("Protocol error");
                        }
//                        key = keyPacket.getSecretKey(privateKey);
                    }
                    else if (packet instanceof ServerboundLoginAcknowledgedPacket) {
                        setState(ctx, ProtocolState.CONFIGURATION);
                        this.remoteClient.onSessionReady();
                    }
                }

                case CONFIGURATION -> {
                    if (packet instanceof ServerboundFinishConfigurationPacket) {
                        setState(ctx, ProtocolState.GAME);
                        this.remoteClient.getRemoteSession().send(packet, () -> {
                            this.remoteClient.getRemoteSession().setState(ProtocolState.GAME);
                            this.remoteClient.onLoginDone();
                        });
                        forwarded = true;
                        return;
                    }
                    this.remoteClient.getRemoteSession().send(packet);
                    forwarded = true;
                }

                case GAME -> {
                    PacketContext<?> context = new PacketContext<>(packet, this.remoteClient, PacketDirection.SERVERBOUND);

                    if (context.getPacket() instanceof ServerboundCustomPayloadPacket payload) {
                        payload.lazyDecode();
                        if (payload.getChannel().namespace().equals("zero")) {
                            context.setCancelled(true);
                            this.remoteClient.getEVENT_BUS().getCUSTOM_BUS().call(new ZeroPayloadEvent(this.remoteClient, payload.getChannel(), payload.getData()));
                        }
                    }

                    this.remoteClient.getEVENT_BUS().getPACKET_BUS().call(context);

                    if (context.isCancelled()) {
                        return;
                    }

                    this.remoteClient.getRemoteSession().send(context.getPacket());
                    context.complete();
                    forwarded = true;
                }
            }
        } finally {
            if (!forwarded) {
                packet.release();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        setState(ctx, ProtocolState.HANDSHAKE);
        this.connectedSession.setChannel(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (getState(ctx) == ProtocolState.GAME) {
            log.info("Client disconnected: {} ({})", this.profile.getName(), ctx.channel().remoteAddress());
            if (this.remoteClient != null) {
                this.remoteClient.getRemoteSession().disconnect(Component.text("Client disconnected: " + this.profile.getName() +" (" + ctx.channel().remoteAddress() + ")"));
                if (this.profile != null) MinecraftProtocol.playerLeft(this.profile, this.remoteClient);
            }
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            log.warn("Connection closed: {}", cause.getMessage());
        } else {
            cause.printStackTrace();
        }
        ctx.close();
    }

    private void setState(ChannelHandlerContext ctx, ProtocolState state) {
        ctx.channel().attr(PROTOCOL_STATE).set(state);
    }

    private ProtocolState getState(ChannelHandlerContext ctx) {
        return ctx.channel().attr(PROTOCOL_STATE).get();
    }

    public void setCompressionThreshold() {
        if (this.channel != null) {
            if (DataLoader.getData().getCompressionThreshold() >= 0) {
                if (this.channel.pipeline().get("compression") == null) {
                    this.channel.pipeline().addBefore("decoder", "compression", new TcpPacketCompression(DataLoader.getData().getCompressionThreshold(), DataLoader.getData().isValidateDecompression()));
                } else {
                    this.channel.pipeline().remove("compression");
                    this.channel.pipeline().addBefore("decoder", "compression", new TcpPacketCompression(DataLoader.getData().getCompressionThreshold(), DataLoader.getData().isValidateDecompression()));
                }
            } else if (this.channel.pipeline().get("compression") != null) {
                this.channel.pipeline().remove("compression");
            }
        }
    }

    private void startRemoteClient(ConnectionInfo info) {
        remoteClient = new MinecraftClient(info, this);
        remoteClient.setSession(this.connectedSession);
        remoteClient.connect();
    }

    public void onRemoteReady() {
        this.profile = new GameProfile(this.info.getUuid(), this.username);
        this.connectedSession.setProfile(profile);
        MinecraftProtocol.playerJoined(profile, remoteClient);
        this.connectedSession.send(new ClientboundLoginCompressionPacket(DataLoader.getData().getCompressionThreshold()));
        setCompressionThreshold();
        this.connectedSession.send(new ClientboundLoginFinishedPacket(profile));
    }
}
