package com.ricedotwho.zero.util;

import com.ricedotwho.zero.module.impl.task.ScheduledTask;
import com.ricedotwho.zero.module.impl.task.TickTask;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class Scheduler {
    private static final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public void schedule(String id, long millis, int ticks, Runnable run) {
        Task task = new Task(millis, ticks, id, run);
        tasks.put(id, task);
        task.start();
    }

    public void cancel(String id) {
        if (tasks.containsKey(id)) {
            tasks.get(id).cancel();
        }
    }

    @RequiredArgsConstructor
    private static class Task {
        private final long millis;
        private final int ticks;
        private final String id;
        private final Runnable run;
        private boolean other = false;

        public void onCompleted() {
            if (other) {
                tasks.remove(this.id);
                run.run();
            }
            other = true;
        }

        public void start() {
            TaskQueue.addTask(this.id, this::onCompleted, millis);
            TickTask.onServerTick(this.id, ticks, this::onCompleted);
        }

        public void cancel() {
            TaskQueue.cancelTask(this.id);
            TickTask.cancelTask(this.id, ScheduledTask.TaskType.SERVER_TICK);
        }
    }
}
