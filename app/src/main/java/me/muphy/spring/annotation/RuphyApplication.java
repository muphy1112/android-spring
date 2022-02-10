package me.muphy.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RuphyApplication {

    /**
     * 扫描包
     *
     * @return 路径
     */
    String[] basePackages() default {};

    /**
     * 扫描包
     *
     * @return 路径
     */
    String[] excludeNames() default {};

    /**
     * 自动配置
     *
     * @return 自动配置
     */
    boolean autoConfiguration() default true;

}
