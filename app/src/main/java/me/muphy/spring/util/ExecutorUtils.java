package me.muphy.spring.util;

import me.muphy.spring.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ExecutorUtils {

    //参数初始化
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public final static int TIME_SLICE = 100;

    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE;
    private final static ScheduledExecutorService SCHEDULE_AT_FIXED_RATE_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private final static Map<Long, List<Callback<Boolean, Long>>> SCHEDULE_AT_FIXED_RATE_RUNNABLE_MAP = new HashMap<>();
    private final static ScheduledExecutorService SCHEDULE_WITH_FIXED_RATE_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private final static Map<Long, List<Callback<Boolean, Long>>> SCHEDULE_WITH_FIXED_RATE_RUNNABLE_MAP = new HashMap<>();

    static {
        //核心线程数量大小
        int corePoolSize = Math.max(2 * CPU_COUNT - 1, 30);
        LogUtils.d(ExecutorUtils.class.getSimpleName(), "CPU数量:" + CPU_COUNT + ",corePoolSize:" + corePoolSize);
        SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(corePoolSize);
        AtomicLong atomicLong = new AtomicLong();
        //固定频率执行
        SCHEDULE_AT_FIXED_RATE_EXECUTOR.scheduleAtFixedRate(() -> {
            long l = atomicLong.incrementAndGet();
            for (long c : SCHEDULE_AT_FIXED_RATE_RUNNABLE_MAP.keySet()) {
                if (l % c == 0) {
                    for (Callback<Boolean, Long> runnable : SCHEDULE_AT_FIXED_RATE_RUNNABLE_MAP.get(c)) {
                        try {
                            runnable.call(l);
                        } catch (Exception e) {
                            LogFileUtils.printStackTrace(e);
                        }
                    }
                }
            }
        }, TIME_SLICE, TIME_SLICE, TimeUnit.MILLISECONDS);
        //与上一次执行之间的固定间隔执行
        SCHEDULE_WITH_FIXED_RATE_EXECUTOR.scheduleWithFixedDelay(() -> {
            long l = atomicLong.incrementAndGet();
            for (long c : SCHEDULE_WITH_FIXED_RATE_RUNNABLE_MAP.keySet()) {
                if (l % c == 0) {
                    for (Callback<Boolean, Long> runnable : SCHEDULE_WITH_FIXED_RATE_RUNNABLE_MAP.get(c)) {
                        try {
                            runnable.call(l);
                        } catch (Exception e) {
                            LogFileUtils.printStackTrace(e);
                        }
                    }
                }
            }
        }, TIME_SLICE, TIME_SLICE, TimeUnit.MILLISECONDS);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return SCHEDULED_EXECUTOR_SERVICE.submit(task);
    }

    public static Future<?> submit(Runnable task) {
        return SCHEDULED_EXECUTOR_SERVICE.submit(task);
    }

    public static void execute(Runnable command) {
        SCHEDULED_EXECUTOR_SERVICE.execute(command);
    }

    //这里的任务尽量小
    public static void scheduleAtFixedRate(Callback<Boolean, Long> command, long millisecond) {
        if (command == null) {
            return;
        }
        long c = millisecond / TIME_SLICE;
        if (c <= 0) {
            c = 1;
        }
        List<Callback<Boolean, Long>> runnables = SCHEDULE_AT_FIXED_RATE_RUNNABLE_MAP.get(c);
        if (runnables == null) {
            runnables = new ArrayList<>();
        }
        runnables.add(command);
        SCHEDULE_AT_FIXED_RATE_RUNNABLE_MAP.put(c, runnables);
    }

    //这里的任务可以不那么及时大有时限的大任务
    public static void scheduleWithFixedRate(Callback<Boolean, Long> command, long millisecond) {
        if (command == null) {
            return;
        }
        long c = millisecond / TIME_SLICE;
        if (c <= 0) {
            c = 1;
        }
        List<Callback<Boolean, Long>> runnables = SCHEDULE_WITH_FIXED_RATE_RUNNABLE_MAP.get(c);
        if (runnables == null) {
            runnables = new ArrayList<>();
        }
        runnables.add(command);
        SCHEDULE_WITH_FIXED_RATE_RUNNABLE_MAP.put(c, runnables);
    }

    //这里是无时限的长任务，如与设备的连接
    public static Thread executeInNewThread(Runnable command) {
        if (command == null) {
            return null;
        }
        Thread thread = new Thread(command);
        thread.start();
        return thread;
    }

    public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return SCHEDULED_EXECUTOR_SERVICE.schedule(callable, delay, unit);
    }

    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return SCHEDULED_EXECUTOR_SERVICE.schedule(command, delay, unit);
    }

    public static void sleepMilliseconds(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            LogFileUtils.printStackTrace(e);
        }
    }

    public static void sleepSeconds(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            LogFileUtils.printStackTrace(e);
        }
    }
}
