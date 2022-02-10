package me.muphy.spring.platform.java;

import me.muphy.spring.context.ContextHolder;
import me.muphy.spring.core.LogListener;

public class JavaLogListener implements LogListener {
    @Override
    public String getIdentity() {
        return "Default";
    }

    @Override
    public int printf(Level level, String tag, String msg, Throwable tr) {
        return d(tag, msg, tr);
    }

    @Override
    public int v(String tag, String msg) {
        System.out.println(tag + ">" + msg);
        return 0;
    }

    @Override
    public int v(String tag, String msg, Throwable tr) {
        System.out.println(tag + ">" + msg);
        if (tr != null) {
            tr.printStackTrace();
        }
        return 0;
    }

    @Override
    public int d(String tag, String msg) {
        if (ContextHolder.isDebug()) {
            System.out.println(tag + ">" + msg);
        }
        return 0;
    }

    @Override
    public int d(String tag, String msg, Throwable tr) {
        if (ContextHolder.isDebug()) {
            System.out.println(tag + ">" + msg);
            if (tr != null) {
                tr.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int i(String tag, String msg) {
        System.out.println(tag + ">" + msg);
        return 0;
    }

    @Override
    public int i(String tag, String msg, Throwable tr) {
        System.out.println(tag + ">" + msg);
        if (tr != null) {
            tr.printStackTrace();
        }
        return 0;
    }

    @Override
    public int w(String tag, String msg) {
        System.out.println(tag + ">" + msg);
        return 0;
    }

    @Override
    public int w(String tag, String msg, Throwable tr) {
        System.out.println(tag + ">" + msg);
        if (tr != null) {
            tr.printStackTrace();
        }
        return 0;
    }

    @Override
    public int w(String tag, Throwable tr) {
        System.out.println(tag);
        if (tr != null) {
            tr.printStackTrace();
        }
        return 0;
    }

    @Override
    public int e(String tag, String msg) {
        System.out.println(tag + ">" + msg);
        return 0;
    }

    @Override
    public int e(String tag, String msg, Throwable tr) {
        System.out.println(tag + ">" + msg);
        if (tr != null) {
            tr.printStackTrace();
        }
        return 0;
    }
}
