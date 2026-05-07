package com.ricedotwho.zero.module.impl.task;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.common.clientbound.ClientboundPingPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level.ClientboundSetTimePacket;
import com.ricedotwho.mcprotocol.protocol.packet.login.clientbound.ClientboundLoginFinishedPacket;
import com.ricedotwho.zero.event.custom.events.ServerTickEvent;
import com.ricedotwho.zero.event.packet.PacketContext;
import com.ricedotwho.zero.event.packet.PacketEvent;
import com.ricedotwho.zero.module.Module;
import com.ricedotwho.zero.util.TaskQueue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TickTask extends Module {
    @Getter
    private static long serverTime = 0L;
    private static final Map<String, ScheduledTask> serverTickTasks = new ConcurrentHashMap<>();

    public TickTask(MinecraftClient proxy) {
        super("TickTask", proxy);
        this.enabled = true;
        this.canDisable = false;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onServerTick(PacketContext<ClientboundPingPacket> ctx) {
        ClientboundPingPacket packet = ctx.getPacket();
        packet.lazyDecode();
        if (packet.getPingId() != 0) {
            serverTime++;
            ctx.getProxy().getEVENT_BUS().getCUSTOM_BUS().call(new ServerTickEvent(ctx.getProxy(), serverTime));
            removeIf(serverTickTasks, serverTime);
        }
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onTimeSet(PacketContext<ClientboundSetTimePacket> ctx) {
        ClientboundSetTimePacket packet = ctx.getPacket();
        packet.lazyDecode();
        serverTime = packet.getGameTime();
    }

    private void removeIf(Map<String, ? extends ScheduledTask> tasks, long time) {
        if (tasks.isEmpty()) return;
        List<Runnable> actions = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, ? extends ScheduledTask> t : tasks.entrySet()) {
            if (t == null) {
                toRemove.add(null);
                continue;
            }

            if (t.getValue().shouldRun(time)) {
                actions.add(t.getValue().getTask());
                toRemove.add(t.getKey());
            }
        }

        toRemove.forEach(tasks::remove);

        for (Runnable run : actions) {
            run.run();
        }
    }

    public static boolean cancelTask(String id, ScheduledTask.TaskType type) {
        if (id == null || id.isBlank()) return false;
        return switch (type) {
            case SERVER_TICK -> serverTickTasks.remove(id) != null;
            default -> false;
        };
    }

    public static void onServerTick(long delay, Runnable run) {
        addTask(new ScheduledTask(delay, serverTime, ScheduledTask.TaskType.SERVER_TICK, run));
    }

    public static void onServerTick(String id, long delay, Runnable run) {
        addTask(new ScheduledTask(id, delay, serverTime, ScheduledTask.TaskType.SERVER_TICK, run));
    }

    public static void addTask(ScheduledTask task) {
        if (task == null) return;
        switch (task.getType()) {
            case SERVER_TICK:
                serverTickTasks.put(task.getId(), task);
                break;
        }
    }

    private void reset() {
        serverTime = 0;
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onRespawn(PacketContext<ClientboundRespawnPacket> ctx) {
        reset();
    }

    @PacketEvent(direction = PacketDirection.CLIENTBOUND)
    public void onLogin(PacketContext<ClientboundLoginFinishedPacket> ctx) {
        reset();
    }
}
