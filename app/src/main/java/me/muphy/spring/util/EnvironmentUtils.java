package me.muphy.spring.util;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.variable.VariableScope;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Remind(value = "环境变量工具", notice = "涉及的变量均是ENV环境变量变量，要获取SESSION、PROPERTY等所有变量请使用VariableUtils工具")
public class EnvironmentUtils {

    public static String getProperty(String key) {
        Object value = VariableUtils.getVariable(VariableScope.ENV, key).getValue();
        if (StringUtils.isEmpty(value)) {
            value = SharedPreferencesUtils.loadConfig(key);
            if (StringUtils.isEmpty(value)) {
                value = PropertyFileUtils.getProperty(key);
                if (StringUtils.isEmpty(value)) {
                    value = VariableUtils.getVariable(VariableScope.PROPERTY, key).getValue();
                    if (StringUtils.isEmpty(value)) {
                        return null;
                    }
                }
            }
        }
        String propertyValue = String.valueOf(value);
        if (propertyValue.contains("${")) {
            Properties properties = getProperties();
            propertyValue = StringUtils.parseTemplate(String.valueOf(value), properties);
        }
        return propertyValue;
    }

    @Remind("这里有多种方式，可以从VariableUtils的ENV内存区域里面查，下面采用直接查询持久化配置的方式")
    public static Properties getProperties() {
        Properties properties = new Properties();
        Map<String, ?> val = SharedPreferencesUtils.loadAllConfigs();
        if (val != null) {
            for (Map.Entry<String, ?> entry : val.entrySet()) {
                if (!entry.getKey().endsWith("@file")) {
                    properties.put(entry.getKey(), entry.getValue());
                }
            }
        }
        Properties props = PropertyFileUtils.getProperties();
        if (props != null) {
            if (val == null) {
                properties.putAll(props);
            } else {
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    Object key = entry.getKey();
                    if (key == null) {
                        continue;
                    }
                    Object o = val.get(key.toString() + "@file");
                    if (!StringUtils.equals(entry.getValue(), o)) {
                        properties.put(key.toString(), entry.getValue());
                    }
                }
            }
        }
        //需要在这里处理模板字符串？
        return properties;
    }

    public static Properties getProperties(String key) {
        Properties properties = new Properties();
        for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
            String k = String.valueOf(entry.getKey());
            if (k.startsWith(key + ".")) {
                properties.put(k.replace(key + ".", ""), entry.getValue());
            }
        }
        return properties;
    }

    public static float getFloatProperty(String key) {
        String property = getProperty(key);
        if (ValidateUtils.isRealNumber(property)) {
            return Float.valueOf(property);
        }
        return 0.0f;
    }

    public static double getDoubleProperty(String key) {
        String property = getProperty(key);
        if (ValidateUtils.isRealNumber(property)) {
            return Double.valueOf(property);
        }
        return 0.0;
    }

    public static int getIntegerProperty(String key) {
        String property = getProperty(key);
        if (ValidateUtils.isRealNumber(property)) {
            return Math.round(Float.valueOf(property));
        }
        return 0;
    }

    public static long getLongProperty(String key) {
        String property = getProperty(key);
        if (ValidateUtils.isRealNumber(property)) {
            return Math.round(Double.valueOf(property));
        }
        return 0l;
    }

    public static String getPropertyWithDefaultValue(String key, String defaultValue) {
        String val = getProperty(key);
        if (StringUtils.isEmpty(val) && !StringUtils.isEmpty(defaultValue)) {
            val = defaultValue;
        }
        return val;
    }

    public static String getProperty(String key, String subKey) {
        return SharedPreferencesUtils.loadConfig(key, subKey);
    }

    public static void setProperty(String key, Object val) {
        if (StringUtils.isEmptyOrWhiteSpace(key)) {
            return;
        }
        if (!key.endsWith("@file")) {
            Object fv = SharedPreferencesUtils.loadConfig(key + "@file");
            String property = PropertyFileUtils.getProperty(key);
            if (fv == null) {
                SharedPreferencesUtils.saveConfig(key + "@file", val);
            }
            if (!StringUtils.equals(property, fv)) {
                Properties properties = PropertyFileUtils.getProperties();
                if (properties != null && properties.containsKey(key)) {
                    val = property;
                    SharedPreferencesUtils.saveConfig(key + "@file", val);
                }
            }
        }
        VariableUtils.setVariable(VariableScope.ENV, key, val);
        SharedPreferencesUtils.saveConfig(key, val);
    }

    public static void setProperty(String key, String subKey, Objects val) {
        VariableUtils.setVariable(VariableScope.ENV, key + "." + subKey, val);
        SharedPreferencesUtils.saveConfig(key, subKey, val);
    }

    public static <T extends Map<? extends Object, ? extends Object>> void setProperties(T properties) {
        if (properties != null) {
            for (Map.Entry<? extends Object, ? extends Object> entry : properties.entrySet()) {
                if (entry.getKey() != null) {
                    Object value = entry.getValue();
//                    if (value != null && value instanceof String) {
//                        String s = String.valueOf(value);
//                        value = StringUtils.parseTemplate(s, properties);
//                    }
                    setProperty(entry.getKey().toString(), value);
                }
            }
        }
    }
}
