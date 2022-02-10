package me.muphy.spring.mvc;

import me.muphy.spring.annotation.RequestParam;
import me.muphy.spring.mvc.servlet.HttpServletRequest;
import me.muphy.spring.mvc.servlet.HttpServletResponse;
import me.muphy.spring.util.LogUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HandlerMapping {
    private Pattern url;
    private Method method;
    private Object controller;
    private Map<String, Integer> paramIndexMapping;

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    private Class<?>[] paramTypes;

    public HandlerMapping(Pattern url, Object controller, Method method) {
        this.url = url;
        this.method = method;
        this.controller = controller;
        this.paramTypes = method.getParameterTypes();
        this.paramIndexMapping = new HashMap<>();
        putParamIndexMapping(method, controller);
    }

    private void putParamIndexMapping(Method method, Object controller) {
        Annotation[][] params = method.getParameterAnnotations();
        for (int i = 0; i < params.length; i++) {
            for (Annotation param : params[i]) {
                if (param instanceof RequestParam) {
                    String paramName = ((RequestParam) param).value();
                    if ("".equals(paramName.trim())) continue;
                    paramIndexMapping.put(paramName, i);
                }
            }
        }
        Class<?>[] paramTypes = this.paramTypes;
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> clazz = paramTypes[i];
            if (HttpServletRequest.class.isAssignableFrom(clazz) || HttpServletResponse.class.isAssignableFrom(clazz)) {
                paramIndexMapping.put(clazz.getName(), i);
            }
        }
        if (paramIndexMapping.size() != paramTypes.length) {
            LogUtils.w(getClass().getSimpleName(), "方法" + controller.getClass().getSimpleName() + "." + method.getName() + "参数必须使用@RequestParam注解！");
        }
    }

    public Pattern getUrl() {
        return url;
    }

    public void setUrl(Pattern url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
        this.paramIndexMapping = paramIndexMapping;
    }
}
