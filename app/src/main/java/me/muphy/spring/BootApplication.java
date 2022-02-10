package me.muphy.spring;


import me.muphy.spring.context.BootContext;
import me.muphy.spring.context.impl.StandardBootContext;

/**
 * 应用
 */
public final class BootApplication {

    public static BootContext run(Class<?> clazz, String... args) {
        StandardBootContext bootContext = new StandardBootContext(clazz, args);
        return bootContext;
    }

}
