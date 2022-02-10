package me.muphy.spring.util;

import me.muphy.spring.common.Constants;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertyFileUtils {

    /**
     * 默认值配置文件
     */
    public static final String DEFAULT_PROPERTY_FILE = Constants.DEFAULT_PROPERTY_FILE;

    public static String getProperty(String key) {
        String property = getProperty(DEFAULT_PROPERTY_FILE, key);
        return property;
    }

    public static String getProperty(String fileName, String key) {
        if (StringUtils.isEmpty(key)) {
            LogUtils.e(PropertyFileUtils.class.getSimpleName(), "getProperty: key is null");
            return "";
        }
        Properties properties = getProperties(fileName);
        if (properties != null && properties.containsKey(key)) {
            String property = properties.getProperty(key);
            if (StringUtils.isEmpty(property)) {
                return "";
            }
            return property;
        }
        return "";
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

    public static Properties getDefaultProperties() {
        return getProperties(DEFAULT_PROPERTY_FILE);
    }

    public static Properties getProperties() {
        Properties properties = getProperties(DEFAULT_PROPERTY_FILE);
        return properties;
    }

    public static Properties getProperties(String fileName) {
        if (CacheUtils.containKey("property-file-" + fileName)) {
            return (Properties) CacheUtils.get("property-file-" + fileName);
        }
        Properties properties = load(fileName);
        String active = properties.getProperty(Constants.ENVIRONMENT);
        if (!StringUtils.isEmptyOrWhiteSpace(active)) {
            properties.putAll(load(active + "_" + fileName));
        }
        CacheUtils.set("property-file-" + fileName, properties);
        return properties;
    }

    private static Properties load(String fileName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            inputStream = FileUtils.getHttpStaticFileInputStream(fileName);
            reader = new InputStreamReader(inputStream);
            properties.load(reader);
        } catch (Exception e) {
            LogUtils.d(PropertyFileUtils.class.getSimpleName(), "load: " + e.getMessage());
            LogFileUtils.printStackTrace(e);
        } finally {
            IOUtils.close(reader, inputStream);
        }
        return properties;
    }
}
