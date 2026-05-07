package com.ricedotwho.zero.event.packet;

import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import com.ricedotwho.zero.module.Module;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketEventBus {
    private static final ExecutorService asyncExecutor = Executors.newCachedThreadPool();
    private final Map<Class<? extends Packet>, List<HandlerMethod>> handlers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public void register(Module listener) {
        for (Method m : listener.getClass().getDeclaredMethods()) {
            PacketEvent ann = m.getAnnotation(PacketEvent.class);
            if (ann != null) {
                Class<?> paramType = m.getParameterTypes()[0];
                if (!PacketContext.class.isAssignableFrom(paramType)) continue;

                Class<? extends Packet> packetClass = ann.value() != Packet.class
                        ? ann.value()
                        : (Class<? extends Packet>) ((ParameterizedType)m.getGenericParameterTypes()[0])
                           .getActualTypeArguments()[0];

                handlers.computeIfAbsent(packetClass, k -> new CopyOnWriteArrayList<>())
                        .add(new HandlerMethod(listener, m, ann.async(), ann.direction()));
            }
        }
    }

    public void unregister(Module listener) {
        handlers.forEach((packetClass, list) -> {
            list.removeIf(h -> h.target() == listener);
            if (list.isEmpty()) {
                handlers.remove(packetClass, list);
            }
        });
    }

    public <T extends Packet> void call(PacketContext<T> context) {
        List<HandlerMethod> list = handlers.get(context.getPacket().getClass());
        if (list == null) return;

        for (HandlerMethod handler : list) {
            Runnable task = () -> {
                try {
                    handler.method.invoke(handler.target, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            if (handler.async) {
                asyncExecutor.submit(task);
            } else {
                task.run();
            }
        }
    }

    private record HandlerMethod(Module target, Method method, boolean async, PacketDirection direction) {
    }
}