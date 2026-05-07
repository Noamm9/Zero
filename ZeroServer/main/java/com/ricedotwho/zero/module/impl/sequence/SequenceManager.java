package com.ricedotwho.zero.module.impl.sequence;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity.player.ClientboundBlockChangedAckPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundPlayerActionPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundUseItemOnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player.ServerboundUseItemPacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ping.severbound.ServerboundPingRequestPacket;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.module.impl.Command;
import com.ricedotwho.zero.util.ChatUtil;
import com.ricedotwho.zero.util.Utils;
import com.ricedotwho.zero.util.command.CommandBase;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;

import java.util.*;

public class SequenceManager extends Module {
    private int sequence = 0;
    private int instantSequence = 0;
    private final Map<Integer, Integer> clientSequence = new HashMap<>();
    private static final Map<State, Queue<Runnable>> actions = new HashMap<>();

    public SequenceManager(MinecraftClient proxy) {
        super("SequenceManager", proxy);
        this.enabled = true;
        this.canDisable = false;

        for (State state : State.values()) {
            actions.put(state,  new ArrayDeque<>());
        }

        this.getProxy().getModule(Command.class).register("fix", new CommandBase("fix sequence", "", args -> {
            clientSequence.clear();
            ChatUtil.prefix(this.getProxy(), "Fixed");
        }));
    }

    public void sendServerSequenced(SequencedPacketCreator creator) {
        this.sendServer(creator.create(getNextSequence()));
    }

    private int getNextSequence() {
        sequence++;
        return instantSequence + sequence;
    }

    public static void register(State state, Runnable runnable) {
        actions.get(state).add(runnable);
    }

    public enum State {
        USE,
        ATTACK
    }

    // Use Item
    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onServerPing(PacketContext<ServerboundPingRequestPacket> ctx) {
        ctx.after(() -> {
            pollTasks(State.ATTACK);
            pollTasks(State.USE);
        });
    }

    private void pollTasks(State state) {
        Queue<Runnable> tasks = actions.get(state);
        while (!tasks.isEmpty()) {
            tasks.poll().run();
        }
    }


    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onDigging(PacketContext<ServerboundPlayerActionPacket> ctx) {
        ServerboundPlayerActionPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (!Utils.equalsOneOf(packet.getAction(), PlayerAction.START_DIGGING, PlayerAction.FINISH_DIGGING)) return;
        instantSequence = packet.getSequence();
        int next = instantSequence + sequence;
        clientSequence.put(next, instantSequence);
        if (sequence > 0) {
            packet.setSequence(next);
            packet.setModified(true);
        }
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onUse(PacketContext<ServerboundUseItemPacket> ctx) {
        ServerboundUseItemPacket packet = ctx.getPacket();
        packet.lazyDecode();
        instantSequence = packet.getSequence();
        int next = instantSequence + sequence;
        clientSequence.put(next, instantSequence);
        if (sequence > 0) {
            packet.setSequence(next);
            packet.setModified(true);
        }
    }

    @PacketEvent(direction = PacketDirection.SERVERBOUND)
    public void onUseOn(PacketContext<ServerboundUseItemOnPacket> ctx) {
        ServerboundUseItemOnPacket packet = ctx.getPacket();
        packet.lazyDecode();
        instantSequence = packet.getSequence();
        int next = instantSequence + sequence;
        clientSequence.put(next, instantSequence);
        if (sequence > 0) {
            packet.setSequence(next);
            packet.setModified(true);
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onBlockChangeAck(PacketContext<ClientboundBlockChangedAckPacket> ctx) {
        ClientboundBlockChangedAckPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (clientSequence.containsKey(packet.getSequence())) {
            int seq = clientSequence.remove(packet.getSequence());
            packet.setSequence(seq);
            packet.setModified(true);
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onRespawn(PacketContext<ClientboundRespawnPacket> ctx) {
        reset();
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onLogin(PacketContext<ClientboundLoginFinishedPacket> ctx) {
        reset();
    }

    private void reset() {
        sequence = 0;
        clientSequence.clear();
        actions.values().forEach(Queue::clear);
    }
}
