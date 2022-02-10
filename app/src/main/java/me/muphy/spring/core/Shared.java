package me.muphy.spring.core;

import me.muphy.spring.util.StringUtils;

import java.util.Map;

public interface Shared extends Identity {
    <T> T loadConfig(String key);

    Map<String, ?> loadAllConfigs();

    default void saveAllConfigs(Map<String, ?> map) {
        if (map != null) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                saveConfig(entry.getKey(), entry.getValue());
            }
        }
    }

    <T> void saveConfig(String key, T val);

    //二级key
    default String loadConfig(String key, String subKey) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(subKey)) {
            return "";
        }
        String value = loadConfig(key);
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        for (String kv : value.split("&")) {
            String[] split = kv.split("=");
            if (split.length > 1 && subKey.equals(split[0])) {
                return split[1];
            }
        }
        return "";
    }

    //二级key
    default <T> void saveConfig(String key, String subKey, T val) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(subKey) || val == null) {
            return;
        }
        String value = loadConfig(key);
        if (StringUtils.isEmpty(value)) {
            value = subKey + "=" + val;
        } else {
            value = (value + "&" + subKey + "=" + val).replaceAll("&+", "&");
        }
        saveConfig(key, value);
    }
}
