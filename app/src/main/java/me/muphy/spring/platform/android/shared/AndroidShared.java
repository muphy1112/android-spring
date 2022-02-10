package me.muphy.spring.platform.android.shared;

import android.content.Context;
import android.content.SharedPreferences;

import me.muphy.spring.platform.android.AndroidContextHolder;
import me.muphy.spring.core.Shared;
import me.muphy.spring.util.CacheUtils;
import me.muphy.spring.util.StringUtils;

import java.util.Map;
import java.util.Set;

public class AndroidShared implements Shared {

    private static AndroidShared androidShared;

    private static final SharedPreferences sharedPreferences;

    static {
        sharedPreferences = AndroidContextHolder.getContext().getSharedPreferences("shp_property_config", Context.MODE_PRIVATE);
    }

    private AndroidShared() {
    }

    public static AndroidShared getInstance() {
        if (androidShared == null) {
            synchronized (AndroidShared.class) {
                if (androidShared == null) {
                    androidShared = new AndroidShared();
                }
            }
        }
        return androidShared;
    }

    @Override
    public <T> T loadConfig(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object val = CacheUtils.get("spf-" + key);
        if (val == null) {
            Map<String, ?> all = sharedPreferences.getAll();
            if (all != null && all.containsKey(key)) {
                val = all.get(key);
            }
        }
        if (val == null) {
            return null;
        }
        CacheUtils.set("spf-" + key, val);
        return (T) val;
    }

    @Override
    public Map<String, ?> loadAllConfigs() {
        Map<String, ?> all = sharedPreferences.getAll();
        return all;
    }

    @Override
    public <T> void saveConfig(String key, T val) {
        if (StringUtils.isEmpty(key) || val == null) {
            return;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if (val instanceof Long) {
            edit.putLong(key, (Long) val);
        } else if (val instanceof Integer) {
            edit.putInt(key, (Integer) val);
        } else if (val instanceof String) {
            edit.putString(key, (String) val);
        } else if (val instanceof Boolean) {
            edit.putBoolean(key, (Boolean) val);
        } else if (val instanceof Float) {
            edit.putFloat(key, (Float) val);
        } else if (val instanceof Set<?>) {
            edit.putStringSet(key, (Set<String>) val);
        }
        edit.apply();
        CacheUtils.set("spf-" + key, val);
    }
}
