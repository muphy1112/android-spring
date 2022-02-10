package me.muphy.spring.util;

import me.muphy.spring.core.Shared;
import me.muphy.spring.platform.android.shared.AndroidShared;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesUtils {

    private static Shared shared = null;

    private static Shared getShared() {
        if (shared == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (shared == null) {
                    shared = AndroidShared.getInstance();
                }
            }
        }
        return shared;
    }

    public static <T> T loadConfig(String key) {
        Shared shared = getShared();
        if (shared != null) {
            return shared.loadConfig(key);
        }
        return null;
    }

    public static Map<String, ?> loadAllConfigs() {
        Shared shared = getShared();
        if (shared != null) {
            return shared.loadAllConfigs();
        }
        return new HashMap<>();
    }

    //二级key
    public static String loadConfig(String key, String subKey) {
        Shared shared = getShared();
        if (shared != null) {
            return shared.loadConfig(key, subKey);
        }
        return null;
    }

    //二级key
    public static <T> void saveConfig(String key, String subKey, T val) {
        Shared shared = getShared();
        if (shared != null) {
            shared.saveConfig(key, subKey, val);
        }
    }

    public static <T> void saveConfig(String key, T val) {
        Shared shared = getShared();
        if (shared != null) {
            shared.saveConfig(key, val);
        }
    }
}
