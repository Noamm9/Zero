package com.ricedotwho.zero.event.custom.events;

import com.ricedotwho.zero.event.custom.Event;
import com.ricedotwho.zero.module.Module;
import lombok.Getter;

public class ModuleEvent extends Event {
    @Getter
    public static class Enabled extends ModuleEvent {
        private final Module module;
        public Enabled(Module module) {
            this.module = module;
        }
    }

    @Getter
    public static class Disabled extends ModuleEvent {
        private final Module module;
        public Disabled(Module module) {
            this.module = module;
        }
    }
}
