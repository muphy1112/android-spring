package me.muphy.spring.env;

import me.muphy.spring.util.EnvironmentUtils;
import me.muphy.spring.util.PropertyFileUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 环境变量
 */
public interface Environment {
    /**
     * 获取配置
     *
     * @return 系统环境
     */
    default String getProperty(String key) {
        return EnvironmentUtils.getProperty(key);
    }

    default Properties getProperties() {
        return EnvironmentUtils.getProperties();
    }

    default Properties getProperties(String key) {
        return EnvironmentUtils.getProperties(key);
    }

    default float getFloatProperty(String key) {
        return EnvironmentUtils.getFloatProperty(key);
    }

    default double getDoubleProperty(String key) {
        return EnvironmentUtils.getDoubleProperty(key);
    }

    default int getIntegerProperty(String key) {
        return EnvironmentUtils.getIntegerProperty(key);
    }

    default long getLongProperty(String key) {
        return EnvironmentUtils.getLongProperty(key);
    }

    default String getPropertyWithDefaultValue(String key, String defaultValue) {
        return EnvironmentUtils.getPropertyWithDefaultValue(key, defaultValue);
    }

    default String getProperty(String key, String subKey) {
        return EnvironmentUtils.getProperty(key, subKey);
    }

    default void setProperties() {
        Properties properties = PropertyFileUtils.getProperties();
        setProperties(properties);
    }

    default void setProperty(String key, Object val) {
        EnvironmentUtils.setProperty(key, val);
    }

    default void setProperty(String key, String subKey, Objects val) {
        EnvironmentUtils.setProperty(key, subKey, val);
    }

    default <T extends Map<Object, Object>> void setProperties(T properties) {
        EnvironmentUtils.setProperties(properties);
    }

}
