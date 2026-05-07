package com.ricedotwho.zero.event;

import com.ricedotwho.zero.event.custom.CustomEventBus;
import com.ricedotwho.zero.event.custom.events.ModuleEvent;
import com.ricedotwho.zero.event.packet.PacketEventBus;
import com.ricedotwho.zero.module.Module;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);
    private final List<com.ricedotwho.zero.module.Module> subscribers = new ArrayList<>();
    @Getter
    private final PacketEventBus PACKET_BUS = new PacketEventBus();
    @Getter
    private final CustomEventBus CUSTOM_BUS = new CustomEventBus();

    public void easyRegister(Module module) {
        if (subscribers.contains(module)) {
            PACKET_BUS.unregister(module);
            CUSTOM_BUS.unregister(module);
            subscribers.remove(module);
            module.onDisable();
            CUSTOM_BUS.call(new ModuleEvent.Disabled(module));
            log.info("Removed listener: {}", module.getClass().getSimpleName());
        } else {
            PACKET_BUS.register(module);
            CUSTOM_BUS.register(module);
            subscribers.add(module);
            module.onEnable();
            CUSTOM_BUS.call(new ModuleEvent.Enabled(module));
            log.info("Added listener: {}", module.getClass().getSimpleName());
        }
    }
}
