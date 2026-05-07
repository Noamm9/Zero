package com.ricedotwho.zero.event.custom;

import com.ricedotwho.zero.module.Module;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomEventBus {
    private static final ExecutorService asyncExecutor = Executors.newCachedThreadPool();
    private final Map<Class<? extends Event>, List<HandlerMethod>> handlers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public void register(Module listener) {
        for (Method m : listener.getClass().getDeclaredMethods()) {
            EventHandler ann = m.getAnnotation(EventHandler.class);
            if (ann != null) {
                Class<?> paramType = m.getParameterTypes()[0];
                if (!Event.class.isAssignableFrom(paramType)) continue;

                handlers.computeIfAbsent((Class<? extends Event>) paramType, k -> new CopyOnWriteArrayList<>())
                        .add(new HandlerMethod(listener, m, ann.async()));
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

    public boolean call(Event event) {
        List<HandlerMethod> list = handlers.get(event.getClass());
        if (list == null) return false;

        for (HandlerMethod handler : list) {
            Runnable task = () -> {
                try {
                    handler.method.invoke(handler.target, event);
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
        return event.isCancelled();
    }

    private record HandlerMethod(Module target, Method method, boolean async) {
    }
}