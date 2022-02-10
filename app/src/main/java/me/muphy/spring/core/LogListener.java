package me.muphy.spring.core;

import me.muphy.spring.context.ContextHolder;

public interface LogListener extends Identity {
    enum Level {
        D, I, W, V, E
    }

    int printf(Level level, String tag, String msg, Throwable tr);

    default int printf(Level level, String tag, String msg) {
        return printf(level, tag, msg, null);
    }

    default int printf(Level level, String tag, Throwable tr) {
        return printf(level, tag, null, tr);
    }

    default int v(String tag, String msg) {
        return printf(Level.D, tag, msg);
    }

    default int v(String tag, String msg, Throwable tr) {
        return printf(Level.V, tag, msg, tr);
    }

    default int d(String tag, String msg) {
        if (ContextHolder.isDebug()) {
            return printf(Level.D, tag, msg);
        }
        return 0;
    }

    default int d(String tag, String msg, Throwable tr) {
        if (ContextHolder.isDebug()) {
            printf(Level.D, tag, msg, tr);
        }
        return 0;
    }

    default int i(String tag, String msg) {
        return printf(Level.I, tag, msg);
    }

    default int i(String tag, String msg, Throwable tr) {
        return printf(Level.I, tag, msg, tr);
    }

    default int w(String tag, String msg) {
        return printf(Level.W, tag, msg);
    }

    default int w(String tag, String msg, Throwable tr) {
        return printf(Level.W, tag, msg, tr);
    }

    default int w(String tag, Throwable tr) {
        return printf(Level.W, tag, tr);
    }

    default int e(String tag, String msg) {
        return printf(Level.E, tag, msg);
    }

    default int e(String tag, String msg, Throwable tr) {
        return printf(Level.E, tag, msg, tr);
    }
}
