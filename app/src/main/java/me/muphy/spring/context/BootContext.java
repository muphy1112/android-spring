package me.muphy.spring.context;

import me.muphy.spring.env.Environment;

import java.util.Map;

/**
 * 上下文
 */
public interface BootContext extends Environment {

    /**
     * 根据名称获得Bean
     *
     * @param name Bean 名称
     * @return bean
     */
    Object getBean(String name);

    /**
     * 根据类型获得Bean
     *
     * @param name Bean 名称
     * @param <T>  类型
     * @return bean
     */
    <T> T getBean(Class<T> name);

    /**
     * 根据类型获得Bean
     *
     * @param <T> 类型
     * @return bean
     */
    <T> Map<String, T> getBeansOfType(Class<T> type);

    /**
     * 根据类型获得Bean
     *
     * @param name   Bean 名称
     * @param tClass 类型
     * @param <T>    类型
     * @return bean
     */
    <T> T getBean(String name, Class<T> tClass);

    /**
     * 注册bean
     *
     * @param bean bean内容
     * @return
     */
    Object registerBean(Object bean);

    /**
     * 根据名称注册Bean
     *  @param name 名称
     * @param bean bean内容
     * @return
     */
    Object registerBean(String name, Object bean);

    /**
     * 根据名称注册Bean
     *  @param name 名称
     * @param bean bean内容
     * @return
     */
    Object registerBean(String name, Class bean);


    /**
     * 注册bean
     *
     * @param clazz 类
     * @return
     */
    Object registerBean(Class clazz);
}
