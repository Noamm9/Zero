//package com.ricedotwho.zero.module.impl;
//
//import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
//import com.ricedotwho.mcprotocol.protocol.net.registry.Direction;
//import com.ricedotwho.mcprotocol.protocol.packet.play.client.player.ClientPlayerMovementPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.client.player.ClientPlayerPositionPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.client.player.ClientPlayerPositionRotationPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.client.player.ClientPlayerRotationPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.server.window.ServerConfirmTransactionPacket;
//import com.ricedotwho.mcprotocol.protocol.packet.play.server.window.ServerSetSlotPacket;
//import com.ricedotwho.zero.event.custom.events.ClientTickEvent;
//import com.ricedotwho.zero.event.custom.events.ServerTickEvent;
//import com.ricedotwho.zero.event.packet.PacketContext;
//import com.ricedotwho.zero.event.packet.PacketEvent;
//import com.ricedotwho.zero.module.Module;
//import com.ricedotwho.zero.util.ChatUtil;
//
//public class EventHandler extends Module {
//    public EventHandler(MinecraftClient proxy) {
//        super("EventHandler", proxy);
//        this.enabled = true;
//        this.canDisable = false;
//    }
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onMovement(PacketContext<ClientPlayerMovementPacket> ctx) {
//        onClientTick(ctx.getProxy());
//    }
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onPos(PacketContext<ClientPlayerPositionPacket> ctx) {
//        onClientTick(ctx.getProxy());
//    }
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onRot(PacketContext<ClientPlayerRotationPacket> ctx) {
//        onClientTick(ctx.getProxy());
//    }
//
//    @PacketEvent(direction = Direction.SERVERBOUND)
//    public void onPosRot(PacketContext<ClientPlayerPositionRotationPacket> ctx) {
//        onClientTick(ctx.getProxy());
//    }
//
//    public void onClientTick(MinecraftClient client) {
//        client.getEVENT_BUS().getCUSTOM_BUS().call(new ClientTickEvent(client));
//    }
//
//    @PacketEvent(direction = Direction.CLIENTBOUND)
//    public void onTransaction(PacketContext<ServerConfirmTransactionPacket> ctx) {
//        ServerConfirmTransactionPacket packet = ctx.getPacket();
//        packet.lazyDecode();
//        if (packet.getActionId() < 0 && !packet.isAccepted()) {
//            ctx.getProxy().getEVENT_BUS().getCUSTOM_BUS().call(new ServerTickEvent(ctx.getProxy()));
//        }
//    }
//
////    @PacketEvent(direction = Direction.CLIENTBOUND)
////    public void onSetSlot(PacketContext<ServerSetSlotPacket> ctx) {
////        ServerSetSlotPacket packet = ctx.getPacket();
////        packet.lazyDecode();
////        if (packet.getWindowId() == -1) {
////            this.getProxy().getInventory().getInventorySlots().put(packet.getSlot(), packet.getItem());
////        }
////    }
//}
