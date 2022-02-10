package me.muphy.spring.util;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.platform.java.JavaLogListener;
import me.muphy.spring.context.BootContext;
import me.muphy.spring.context.ContextHolder;
import me.muphy.spring.core.LogListener;

import java.util.ArrayList;
import java.util.Collection;

@Remind("跨平台代理日志")
public class LogUtils {

    private static volatile Collection<LogListener> logListeners = null;
    private static Collection<LogListener> defaultLogListeners;

    private LogUtils() {

    }

    @Remind("主要是为了跨平台")
    private static Collection<LogListener> getLogListeners() {
        if (logListeners == null) {
            synchronized (LogUtils.class) {
                if(logListeners == null){
                    BootContext context = ContextHolder.getContext();
                    if (context != null) {
                        Collection<LogListener> listeners = new ArrayList<>();
                        for (LogListener listener : context.getBeansOfType(LogListener.class).values()) {
                            //安卓需要实现AdapterPlatformLogListener来代理调用android的控制台，而不是直接使用
                            listeners.add(listener);
                        }
                        if (!listeners.isEmpty()) {
                            logListeners = listeners;
                            defaultLogListeners = null;
                        }
                    }
                    if (logListeners == null || logListeners.isEmpty()) {
                        if (defaultLogListeners == null) {
                            defaultLogListeners = new ArrayList<>();
                            defaultLogListeners.add(new JavaLogListener());
                        }
                        return defaultLogListeners;
                    }
                }
            }
        }
        return logListeners;
    }

    public static int v(String tag, String msg) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.v(tag, msg);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.v(tag, msg, tr);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int d(String tag, String msg) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.d(tag, msg);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.d(tag, msg, tr);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int i(String tag, String msg) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.i(tag, msg);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.i(tag, msg, tr);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int w(String tag, String msg) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.w(tag, msg);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.w(tag, msg, tr);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int w(String tag, Throwable tr) {
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.w(tag, tr);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int e(String tag, String msg) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.e(tag, msg);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (StringUtils.isEmpty(msg)) {
            return 0;
        }
        int number = 0;
        for (LogListener logListener : getLogListeners()) {
            int n = logListener.e(tag, msg, tr);
            if (n > 0) {
                number = n;
            }
        }
        return number;
    }
}
