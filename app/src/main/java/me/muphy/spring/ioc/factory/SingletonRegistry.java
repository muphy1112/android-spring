package me.muphy.spring.ioc.factory;

import me.muphy.spring.util.LogFileUtils;

import java.util.HashMap;
import java.util.Map;

public class SingletonRegistry {

    private static SingletonRegistry instance;
    private static Map<String, Object> factoryMap = new HashMap();

    //私有化构造方法
    private SingletonRegistry() {

    }

    public static Map<String, Object> getFactoryMap() {
        return factoryMap;
    }

    public synchronized static SingletonRegistry getInstance() {
        if (instance == null) {
            instance = new SingletonRegistry();
        }
        return instance;
    }

    public synchronized <T> Map<String, T> getBeansOfType(Class<T> f) {
        Map<String, T> map = new HashMap<>();
        Object o = factoryMap.get(f.getName());
        if (o != null) {
            map.put(f.getName(), (T) o);
        }
        for (String key : factoryMap.keySet()) {
            Object value = factoryMap.get(key);
            if (value == null) {
                continue;
            }
            if (f.isAssignableFrom(value.getClass())) {
                map.put(key, (T) value);
            }
        }
        return map;
    }

    public synchronized Object getBeanFactory(String factoryName, Class f) {
        Object bean = null;
        try {
            bean = f.newInstance();
            //将Bean工厂注册到注册表
            factoryMap.put(factoryName, bean);
        } catch (Exception ex) {
            LogFileUtils.printStackTrace(ex);
        }
        return bean;
    }

    public synchronized Object getBeanFactory(String factoryName, Object bean) {
        try {
            //将Bean工厂注册到注册表
            factoryMap.put(factoryName, bean);
        } catch (Exception ex) {
            LogFileUtils.printStackTrace(ex);
        }
        return bean;
    }

    public synchronized Object getBean(String factoryName) {
        //在注册表中查找看是否有这个BeanFactory的实例
        Object o = factoryMap.get(factoryName);
        return o;

    }

    public synchronized Object getBeanFactory(String factoryName) {
        Object f = null;
        //在注册表中查找看是否有这个BeanFactory的实例
        Object o = factoryMap.get(factoryName);

        if (o != null) {
            return o;
        }

        try {
            Class<?> aClass = Class.forName(factoryName);
            if (aClass.isAnnotation()) {
                return null;
            }
            f = aClass.newInstance();
            //将Bean工厂注册到注册表
            factoryMap.put(factoryName, f);

        } catch (Exception ex) {
            LogFileUtils.printStackTrace(ex);
        }
        return f;

    }
}

