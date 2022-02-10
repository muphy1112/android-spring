package me.muphy.spring.platform.android.log;

import me.muphy.spring.core.LogListener;
import me.muphy.spring.context.ContextHolder;

import android.util.Log;

public class AndroidPlatformLogListener implements LogListener {
    @Override
    public String getIdentity() {
        return "Android";
    }

    @Override
    public int printf(Level level, String tag, String msg, Throwable tr) {
        return d(tag, msg, tr);
    }

    @Override
    public int v(String tag, String msg) {
        return Log.v(tag, msg);
    }

    @Override
    public int v(String tag, String msg, Throwable tr) {
        return Log.v(tag, msg, tr);
    }

    @Override
    public int d(String tag, String msg) {
        if (ContextHolder.isDebug()) {
            return Log.d(tag, msg);
        }
        return 0;
    }

    @Override
    public int d(String tag, String msg, Throwable tr) {
        if (ContextHolder.isDebug()) {
            return Log.d(tag, msg, tr);
        }
        return 0;
    }

    @Override
    public int i(String tag, String msg) {
        return Log.i(tag, msg);
    }

    @Override
    public int i(String tag, String msg, Throwable tr) {
        return Log.i(tag, msg, tr);
    }

    @Override
    public int w(String tag, String msg) {
        return Log.w(tag, msg);
    }

    @Override
    public int w(String tag, String msg, Throwable tr) {
        return Log.w(tag, msg, tr);
    }

    @Override
    public int w(String tag, Throwable tr) {
        return Log.w(tag, tr);
    }

    @Override
    public int e(String tag, String msg) {
        return Log.e(tag, msg);
    }

    @Override
    public int e(String tag, String msg, Throwable tr) {
        return Log.e(tag, msg, tr);
    }
}
