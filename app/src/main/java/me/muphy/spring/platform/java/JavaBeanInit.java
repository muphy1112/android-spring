package me.muphy.spring.platform.java;

import me.muphy.spring.annotation.Bean;

import org.mozilla.javascript.Context;

//@Configuration
public class JavaBeanInit {

    @Bean
    public Context getContext() {
        return Context.enter();
    }

    @Bean
    public JavaLogListener getJavaLogListener() {
        return new JavaLogListener();
    }

}
