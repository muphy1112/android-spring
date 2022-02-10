package com.muphy.example.lifecycle;

import me.muphy.spring.annotation.Component;
import me.muphy.spring.annotation.PostConstruct;
import me.muphy.spring.common.Result;
import me.muphy.spring.context.ApplicationContextAware;
import me.muphy.spring.context.BootContext;
import me.muphy.spring.core.Init;

@Component
public class SpringLifeCycleTest implements Init, ApplicationContextAware {
    @Override
    public void setApplicationContext(BootContext context) {
        System.out.println("ApplicationContextAware run!");
    }

    @Override
    public Result create() {
        System.out.println("create run!");
        return Init.super.create();
    }

    @Override
    public Result start() {
        System.out.println("start run!");
        return Init.super.start();
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("PostConstruct run!");
    }

}
