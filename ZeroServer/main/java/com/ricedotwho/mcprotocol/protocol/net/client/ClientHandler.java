package com.ricedotwho.mcprotocol.protocol.net.client;

import com.ricedotwho.mcprotocol.data.PlayerListEntry;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.mcprotocol.protocol.packet.common.clientbound.ClientboundTransferPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundPlayerInfoUpdatePacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.*;
import com.ricedotwho.mcprotocol.protocol.packet.login.severbound.ServerboundKeyPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.severbound.ServerboundLoginAcknowledgedPacket;
import com.ricedotwho.mcprotocol.utils.ByteBufUtils;
import com.ricedotwho.mcprotocol.utils.CryptUtil;
import com.ricedotwho.zero.Zero;
import com.ricedotwho.zero.event.packet.PacketContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.auth.SessionService;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;
import org.geysermc.mcprotocollib.protocol.data.UnexpectedEncryptionException;


import java.io.IOException;
import java.util.Objects;

import static com.ricedotwho.mcprotocol.protocol.net.SessionHandler.PROTOCOL_STATE;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {
    private final MinecraftClient client;

    public ClientHandler(MinecraftClient client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        ProtocolState state = ctx.channel().attr(PROTOCOL_STATE).get();

        boolean forwarded = false;

        try {
            switch (state) {
                case LOGIN -> {

                    if (packet instanceof ClientboundHelloPacket hello) {
                        hello.lazyDecode();
                        if ((this.client.getProfile() == null || this.client.getInfo() == null) && hello.isShouldAuthenticate()) {
                            throw new UnexpectedEncryptionException();
                        }

                        this.client.setKey(CryptUtil.generateSharedKey());
                        this.client.setKeyData(new MinecraftClient.KeyData(hello.getPublicKey(), hello.getChallenge()));

                        String serverId = SessionService.getServerId(hello.getServerId(), hello.getPublicKey(), this.client.getKey());

                        if (hello.isShouldAuthenticate()) {
                            this.client.getSession().send(new ClientboundCustomQueryPacket(0, Key.key("zero", "auth/request"), ByteBufUtils.writeString(serverId)));
                        } else {
                            this.client.onAuthCompleted();
                        }
                    }
                    else if (packet instanceof ClientboundLoginFinishedPacket) {
                        this.client.sendPacket(new ServerboundLoginAcknowledgedPacket());
                        forwarded = true;
                        setState(ctx, ProtocolState.CONFIGURATION);
                        this.client.getOtherHandler().onRemoteReady();
                    }
                    else if (packet instanceof ClientboundLoginDisconnectPacket disconnect) {
                        packet.lazyDecode();
                        this.client.disconnect(disconnect.getReason(), true);
                    }
                    else if (packet instanceof ClientboundLoginCompressionPacket compression) {
                        compression.lazyDecode();
                        this.client.setCompressionThreshold(compression.getThreshold());
                    }
                }
                case CONFIGURATION -> {
                    if (packet instanceof ClientboundTransferPacket transfer) {
                        transfer.lazyDecode();
                        this.client.transfer(transfer.getHost(), transfer.getPort());
                        return;
                    }

                    if (!this.client.isReady()) {
                        this.client.getQueue().add(packet);
                        forwarded = true;
                        return;
                    }
                    this.client.getSession().send(packet);
                    forwarded = true;
                }
                case GAME -> {
                    if(packet instanceof ClientboundPlayerInfoUpdatePacket info) {
                        info.lazyDecode();
                        for (PlayerListEntry playerEntry : info.getEntries()) {
                            if (playerEntry.getProfileId() == this.client.getProfile().getId()) {
                                GameProfile profile = playerEntry.getProfile();
                                playerEntry.setProfile(new GameProfile(this.client.getProfile().getId(), profile.getName()));
                                playerEntry.setProfileId(this.client.getProfile().getId());
                                info.setModified(true);
                            }
                        }
                    }

                    PacketContext<?> context = new PacketContext<>(packet, this.client, PacketDirection.CLIENTBOUND);
                    this.client.getEVENT_BUS().getPACKET_BUS().call(context);
                    if (context.isCancelled()) {
                        return;
                    }

                    this.client.getSession().send(context.getPacket());
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

    private void setState(ChannelHandlerContext ctx, ProtocolState state) {
        ctx.channel().attr(PROTOCOL_STATE).set(state);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.client.disconnect(Component.text("Client disconnect"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}