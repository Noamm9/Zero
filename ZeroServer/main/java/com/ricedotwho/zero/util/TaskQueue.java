package com.ricedotwho.zero.util;

import com.ricedotwho.zero.Zero;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TaskQueue {
    private static class QueuedTask {
        final Runnable task;
        final long scheduledTime;
        final long delayMs;
        final boolean repeating;
        final long periodMs;
        final AtomicLong nextRun;

        QueuedTask(Runnable task, long delayMs) {
            this.task = task;
            this.delayMs = delayMs;
            this.scheduledTime = System.nanoTime();
            this.repeating = false;
            this.periodMs = 0;
            this.nextRun = new AtomicLong(scheduledTime + TimeUnit.MILLISECONDS.toNanos(delayMs));
        }

        QueuedTask(Runnable task, long delayMs, long periodMs) {
            this.task = task;
            this.delayMs = delayMs;
            this.scheduledTime = System.nanoTime();
            this.repeating = true;
            this.periodMs = periodMs;
            this.nextRun = new AtomicLong(scheduledTime + TimeUnit.MILLISECONDS.toNanos(delayMs));
        }

        long remainingMs() {
            long remainingNanos = nextRun.get() - System.nanoTime();
            return TimeUnit.NANOSECONDS.toMillis(Math.max(remainingNanos, 0));
        }

        void scheduleNext() {
            if (repeating) {
                nextRun.addAndGet(TimeUnit.MILLISECONDS.toNanos(periodMs));
            }
        }
    }

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private static final Map<String, QueuedTask> tasks = new ConcurrentHashMap<>();

    public static void addTask(String id, Runnable task, long delayMs) {
        QueuedTask qt = new QueuedTask(task, delayMs);
        tasks.put(id, qt);
        scheduler.schedule(() -> runTask(id), delayMs, TimeUnit.MILLISECONDS);
    }

    public static void addRepeatingTask(String id, Runnable task, long initialDelayMs, long periodMs) {
        QueuedTask qt = new QueuedTask(task, initialDelayMs, periodMs);
        tasks.put(id, qt);
        scheduler.schedule(() -> runTask(id), initialDelayMs, TimeUnit.MILLISECONDS);
    }

    private static void runTask(String id) {
        QueuedTask qt = tasks.get(id);
        if (qt == null) return;

        try {
            qt.task.run();
        } catch (Throwable t) {
            Zero.getLogger().error("Error while running Queue Task", t);
        }

        if (qt.repeating) {
            qt.scheduleNext();
            scheduler.schedule(() -> runTask(id), qt.periodMs, TimeUnit.MILLISECONDS);
        } else {
            tasks.remove(id);
        }
    }

    public static void cancelTask(String id) {
        tasks.remove(id);
    }
    public static void cancelFirstStarts(String start) {
        for (String e : tasks.keySet()) {
            if (e.startsWith(start)) {
                tasks.remove(e);
                return;
            }
        }
    }

    public static void cancelAllStarts(String start) {
        tasks.forEach((k, v) -> {
            if (k.startsWith(start)) tasks.remove(k);
        });
    }

    public static long getRemainingTime(String id) {
        QueuedTask qt = tasks.get(id);
        return qt != null ? qt.remainingMs() : -1;
    }

    public static boolean hasTask(String id) {
        return tasks.get(id) != null;
    }

    public static void shutdown() {
        scheduler.shutdown();
        tasks.clear();
    }
}