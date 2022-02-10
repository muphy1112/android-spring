package me.muphy.spring.context.impl;

import me.muphy.spring.annotation.Bean;
import me.muphy.spring.mvc.Tomcat;
import me.muphy.spring.util.LogUtils;

import me.muphy.spring.annotation.Autowired;
import me.muphy.spring.annotation.Component;
import me.muphy.spring.annotation.Configuration;
import me.muphy.spring.annotation.Controller;
import me.muphy.spring.annotation.PostConstruct;
import me.muphy.spring.annotation.RuphyApplication;
import me.muphy.spring.annotation.Qualifier;
import me.muphy.spring.annotation.Service;
import me.muphy.spring.context.ApplicationContextAware;
import me.muphy.spring.context.BootContext;
import me.muphy.spring.context.ContextHolder;
import me.muphy.spring.core.Init;
import me.muphy.spring.ioc.factory.SingletonRegistry;
import me.muphy.spring.ioc.scanner.ClassScanner;
import me.muphy.spring.ioc.scanner.impl.ClassScannerImpl;
import me.muphy.spring.util.ExecutorUtils;
import me.muphy.spring.util.LogFileUtils;
import me.muphy.spring.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 标准的BootContext
 */
public class StandardBootContext implements BootContext {

    private final Class<?> clazz;

    public StandardBootContext(Class<?> clazz, String... args) {
        this.clazz = clazz;
        ContextHolder.setContext(this);
        loadBean();
        Tomcat tomcat = new Tomcat();
        tomcat.start();
    }

    @Override
    public Object getBean(String name) {
        return SingletonRegistry.getInstance().getBeanFactory(name);
    }

    @Override
    public <T> T getBean(Class<T> name) {
        Map<String, T> beansOfType = SingletonRegistry.getInstance().getBeansOfType(name);
        for (T value : beansOfType.values()) {
            return value;
        }
        return null;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return SingletonRegistry.getInstance().getBeansOfType(type);
    }

    @Override
    public <T> T getBean(String name, Class<T> tClass) {
        return (T) SingletonRegistry.getInstance().getBeanFactory(name);
    }

    @Override
    public Object registerBean(Object bean) {
        return SingletonRegistry.getInstance().getBeanFactory(bean.getClass().getName(), bean);
    }

    @Override
    public Object registerBean(String name, Object bean) {
        return SingletonRegistry.getInstance().getBeanFactory(name, bean);
    }

    @Override
    public Object registerBean(String name, Class bean) {
        return SingletonRegistry.getInstance().getBeanFactory(name, bean);
    }

    @Override
    public Object registerBean(Class clazz) {
        return SingletonRegistry.getInstance().getBeanFactory(clazz.getName());
    }

    private void loadBean() {
        LogUtils.i(getClass().getSimpleName(), "正在初始化spring容器！");
        //1.获取需要扫描的包
        RuphyApplication applicationInfo = null;
        if (clazz.isAnnotationPresent(RuphyApplication.class)) {
            applicationInfo = (RuphyApplication) clazz.getAnnotation(RuphyApplication.class);
        }
        if (applicationInfo == null) {
            LogUtils.w(getClass().getSimpleName(), "spring容器初始化失败，没有找到" + RuphyApplication.class.getSimpleName() + "注解！");
            return;
        }
        LogUtils.i(getClass().getSimpleName(), "包扫描...");
        String[] packages = applicationInfo.basePackages();
        if (packages == null || packages.length == 0) {
            if (applicationInfo.autoConfiguration()) {
                packages = new String[]{clazz.getPackage().getName()};
            }
        }
        //2.扫描的包获取所有的类名beandefs
        ClassScanner classScanner = new ClassScannerImpl();
        List<String> strings = classScanner.doScan(packages, applicationInfo.excludeNames());
        //3.转换成类
        LogUtils.i(getClass().getSimpleName(), "转换成类...");
        Map<String, Class> classMap = new HashMap<>();
        for (int i = 0; i < strings.size(); i++) {
            try {
                String className = strings.get(i);
                Class scanClass = Class.forName(className);
                classMap.put(className, scanClass);
            } catch (Exception e) {
                processException(e);
            }
        }
        for (String s : strings) {

        }
        //4.注册bean到IOC
        LogUtils.i(getClass().getSimpleName(), "注册bean到IOC...");
        for (Map.Entry<String, Class> entry : classMap.entrySet()) {
            try {
                this.register(entry.getValue());
            } catch (Exception e) {
                processException(e);
            }
        }
        //7.加载并构建环境变量
        LogUtils.i(getClass().getSimpleName(), "加载并构建环境变量...");
        Properties properties = getProperties();
        setProperties(properties);
        //5.依赖注入DI
        LogUtils.i(getClass().getSimpleName(), "依赖注入DI...");
        for (Map.Entry<String, Class> entry : classMap.entrySet()) {
            try {
                Class scanClass = entry.getValue();
                Object src = this.getSrc(scanClass);
                this.setData(scanClass, src);
            } catch (Exception e) {
                processException(e);
            }
        }
        //4.5.触发onCreate事件
        LogUtils.i(getClass().getSimpleName(), "触发onCreate事件...");
        onCreate();
        //6.处理ApplicationContextAware监听
        LogUtils.i(getClass().getSimpleName(), "处理ApplicationContextAware监听...");
        Map<String, ApplicationContextAware> contextAwareMap = getBeansOfType(ApplicationContextAware.class);
        Iterator<Map.Entry<String, ApplicationContextAware>> iterator = contextAwareMap.entrySet().iterator();
        while (iterator.hasNext()) {
            try {
                iterator.next().getValue().setApplicationContext(this);
            } catch (Exception e) {
                processException(e);
            }
        }
        //8.触发onStart事件
        LogUtils.i(getClass().getSimpleName(), "触发onStart事件...");
        onStart();
        //9.启动@PostConstruct相关的方法
        LogUtils.i(getClass().getSimpleName(), "启动@PostConstruct相关的方法...");
        for (Map.Entry<String, Class> entry : classMap.entrySet()) {
            try {
                Class scanClass = entry.getValue();
                this.execPostConstructMethod(scanClass);
            } catch (Exception e) {
                processException(e);
            }
        }
        LogUtils.i(getClass().getSimpleName(), "spring容器初始化完成！");
    }

    private Object getSrc(Class clazz) {
        if (clazz.isAnnotationPresent(Service.class)) {
            Service service = (Service) clazz.getAnnotation(Service.class);
            if (service.value() == null || "".equals(service.value().trim())) {
                return this.getBean(clazz);
            } else {
                return this.getBean(service.value(), clazz);
            }
        }

        if (clazz.isAnnotationPresent(Component.class)) {
            Component component = (Component) clazz.getAnnotation(Component.class);
            if (component.value() == null || "".equals(component.value().trim())) {
                return this.getBean(clazz);
            } else {
                return this.getBean(component.value(), clazz);
            }
        }

        if (clazz.isAnnotationPresent(Configuration.class)) {
            Configuration configuration = (Configuration) clazz.getAnnotation(Configuration.class);
            if (configuration.value() == null || "".equals(configuration.value().trim())) {
                return this.getBean(clazz);
            } else {
                return this.getBean(configuration.value(), clazz);
            }
        }

        if (clazz.isAnnotationPresent(Controller.class)) {
            Controller controller = (Controller) clazz.getAnnotation(Controller.class);
            if (controller.value() == null || "".equals(controller.value().trim())) {
                return this.getBean(clazz);
            } else {
                return this.getBean(controller.value(), clazz);
            }
        }
        return null;
    }

    private void register(Class clazz) {
        if (clazz.isAnnotationPresent(Service.class)) {
            Service service = (Service) clazz.getAnnotation(Service.class);
            if (service.value() == null || "".equals(service.value().trim())) {
                this.registerBean(clazz);
            } else {
                this.registerBean(service.value(), clazz);
            }
            return;
        }

        if (clazz.isAnnotationPresent(Component.class)) {
            Component service = (Component) clazz.getAnnotation(Component.class);
            if (service.value() == null || "".equals(service.value().trim())) {
                this.registerBean(clazz);
            } else {
                this.registerBean(service.value(), clazz);
            }
            return;
        }

        if (clazz.isAnnotationPresent(Configuration.class)) {
            Configuration service = (Configuration) clazz.getAnnotation(Configuration.class);
            Object bean;
            if (service.value() == null || "".equals(service.value().trim())) {
                bean = this.registerBean(clazz);
            } else {
                bean = this.registerBean(service.value(), clazz);
            }
            if (bean == null) {
                return;
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Bean.class)) {
                    try {
                        Object invoke = method.invoke(bean);
                        this.registerBean(invoke);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }

        if (clazz.isAnnotationPresent(Controller.class)) {
            Controller service = (Controller) clazz.getAnnotation(Controller.class);
            if (service.value() == null || "".equals(service.value().trim())) {
                this.registerBean(clazz);
            } else {
                this.registerBean(service.value(), clazz);
            }
            return;
        }

    }

    private void setData(Class clazz, Object src) {
        if (src == null) {
            return;
        }
        List<Field> fields = ReflectUtils.getFieldList(clazz);
        for (Field field : fields) {
            Class t = field.getType();
            Autowired autowired = null;
            if (field.isAnnotationPresent(Autowired.class)) {
                autowired = field.getAnnotation(Autowired.class);
            }
            if (autowired == null) {
                continue;
            }
            Object qualifier = null;
            if (field.isAnnotationPresent(Qualifier.class)) {
                qualifier = field.getAnnotation(Qualifier.class).value();
            }
            if (qualifier == null) {
                Object data = this.getBean(t);
                ReflectUtils.setData(field, src, data);
            }
        }
    }

    private void execPostConstructMethod(Class scanClass) {
        Method[] declaredMethods = scanClass.getDeclaredMethods();
        if (declaredMethods == null || declaredMethods.length == 0) {
            return;
        }
        for (Method method : declaredMethods) {
            PostConstruct postConstruct = method.getAnnotation(PostConstruct.class);
            if (postConstruct != null) {
                try {
                    Object bean = getBean(scanClass);
                    if (bean == null) {
                        bean = scanClass.newInstance();
                    }
                    boolean accessible = method.isAccessible();
                    method.setAccessible(true);
                    Object finalBean = bean;
                    ExecutorUtils.submit(() -> {
                        try {
                            method.invoke(finalBean);
                        } catch (Exception e) {
                            processException(e);
                        } finally {
                            method.setAccessible(accessible);
                        }
                    });
                } catch (Exception e) {
                    processException(e);
                }
            }
        }
    }

    private void onCreate() {
        List<Future<?>> futures = new ArrayList<>();
        Map<String, Init> initMap = getBeansOfType(Init.class);
        for (Init init : initMap.values()) {
            Future<?> future = ExecutorUtils.submit(() -> {
                init.create();
            });
            futures.add(future);
        }
        waitFuture(futures, 3);
    }

    private void onStart() {
        List<Future<?>> futures = new ArrayList<>();
        Map<String, Init> initMap = getBeansOfType(Init.class);
        for (Init init : initMap.values()) {
            Future<?> future = ExecutorUtils.submit(() -> {
                init.start();
            });
            futures.add(future);
        }
        waitFuture(futures, 5);
    }

    private void waitFuture(List<Future<?>> futures, int timeOut) {
        try {
            ExecutorUtils.submit(() -> {
                for (Future<?> future : futures) {
                    if (future == null) {
                        continue;
                    }
                    try {
                        future.get(2, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        processException(e);
                    }
                }
            }).get(timeOut, TimeUnit.SECONDS);
        } catch (Exception e) {
            processException(e);
        }
    }

    private void processException(Throwable throwable) {
        LogFileUtils.printStackTrace(throwable);
    }

}
