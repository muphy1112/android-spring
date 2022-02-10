package me.muphy.spring.context;

import me.muphy.spring.common.Constants;
import me.muphy.spring.util.EnvironmentUtils;
import me.muphy.spring.util.LogFileUtils;
import me.muphy.spring.util.StringUtils;

public class ContextHolder {
    private static BootContext context;

    public static BootContext getContext() {
        return context;
    }

    public static void setContext(BootContext context) {
        ContextHolder.context = context;
    }

    public static boolean isDebug() {
        try {
            String environment = EnvironmentUtils.getProperty(Constants.LOG_LEVEL);
            if (!StringUtils.isEmpty(environment)) {
                return "debug".equals(environment.toLowerCase());
            }
        } catch (Exception e) {
            LogFileUtils.printStackTrace(e);
        }
        return true;
    }
}
