package me.muphy.spring.ioc.registrar;

import me.muphy.spring.ioc.factory.SingletonRegistry;

/**
 * 注册
 */
public interface BootRegistrar {
    void registerBeanDefinitions(Class clazz, SingletonRegistry registry);
}
