package me.muphy.spring.util;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.variable.VariableListener;
import me.muphy.spring.variable.VariableScope;
import me.muphy.spring.variable.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Remind("变量及变量观察者的操作工具类，分区域统一管理全绝变量、监听值变化以及对ENV区域的持久化存储，包括配置文件配置的属性等各种变量配置，支持模板字符串！")
public class VariableUtils {
    private static Map<VariableScope, List<Variable>> variables = new HashMap<>();

    static {
        for (VariableScope scope : VariableScope.values()) {
            variables.put(scope, new ArrayList<>());
        }
    }

    public static void setEnvVariables(Map<?, ?> properties) {
        setVariables(VariableScope.ENV, properties);
    }

    public static void setPropertyVariables(Map<?, ?> properties) {
        setVariables(VariableScope.PROPERTY, properties);
    }

    public static void setSessionVariables(Map<?, ?> properties) {
        setVariables(VariableScope.SESSION, properties);
    }

    public static Variable setVariable(String key, Object val) {
        return setVariable(VariableScope.ENV, key, val);
    }

    public static Variable setSessionVariable(String key, Object val) {
        return setVariable(VariableScope.SESSION, key, val);
    }

    public static Variable setPropertyVariable(String key, Object val) {
        return setVariable(VariableScope.PROPERTY, key, val);
    }

    public static void setVariables(VariableScope scope, Map<?, ?> properties) {
        Iterator<? extends Map.Entry<?, ?>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> next = iterator.next();
            setVariable(scope, String.valueOf(next.getKey()), next.getValue());
        }
    }

    public static Variable setVariable(VariableScope scope, String key, Object val) {
        if (StringUtils.isEmptyOrWhiteSpace(key)) {
            return null;
        }
        Variable variable = getVariable(scope, key);
        variable.setValue(val);
        return variable;
    }

    public static <T> T getValue(String key) {
        Object value = getVariable(VariableScope.ENV, key).getValue();
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public static <T> T getValue(VariableScope scope, String key) {
        Object value = getVariable(scope, key).getValue();
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    @Remind("key只包含子属性")
    public static Map<String, Object> getSubValues(String key) {
        return getSubValues(VariableScope.ENV, key);
    }

    @Remind("key只包含子属性")
    public static Map<String, Object> getSubValues(VariableScope scope, String key) {
        if (StringUtils.isEmptyOrWhiteSpace(key)) {
            return new HashMap<>();
        }
        Map<String, Object> map = new HashMap<>();
        Variable variable = getVariable(scope, key);
        List<Variable> children = variable.getChildren();
        if (children != null) {
            for (Variable child : children) {
                variable = getVariable(scope, key + "." + child.getProperty());
                if (variable.getValue() != null) {
                    map.put(child.getProperty(), variable.getValue());
                }
            }
        }
        return map;
    }

    @Remind("key只包含子属性")
    public static Map<String, Object> getValues(String key) {
        return getValues(VariableScope.ENV, key);
    }

    @Remind("key包含全路径属性")
    public static Map<String, Object> getValues(VariableScope scope, String key) {
        if (StringUtils.isEmptyOrWhiteSpace(key)) {
            return new HashMap<>();
        }
        Map<String, Object> map = new HashMap<>();
        Variable variable = getVariable(scope, key);
        if (variable.getValue() != null) {
            map.put(key, variable.getValue());
        }
        List<Variable> children = variable.getChildren();
        if (children != null) {
            for (Variable child : children) {
                Map<String, Object> var = getValues(scope, key + "." + child.getProperty());
                map.putAll(var);
            }
        }
        return map;
    }

    public static Variable getVariable(String key) {
        return getVariable(VariableScope.ENV, key);
    }

    public static Variable getSessionVariable(String key) {
        return getVariable(VariableScope.SESSION, key);
    }

    public static Variable getPropertyVariable(String key) {
        return getVariable(VariableScope.PROPERTY, key);
    }

    public static Variable getVariable(VariableScope scope, String key) {
        if (StringUtils.isEmpty(key)) {
            return new Variable();
        }
        List<Variable> list = variables.get(scope);
        Variable variable = null;
        for (String property : key.trim().split("\\.+")) {
            variable = getVariable(property, list);
            list = variable.getChildren();
        }
        return variable;
    }

    private static Variable getVariable(String property, List<Variable> children) {
        for (Variable v : children) {
            if (StringUtils.equals(property, v.getProperty())) {
                return v;
            }
        }
        Variable e = new Variable(property, null);
        children.add(e);
        return e;
    }

    public static Variable registerVariableListeners(String key, VariableListener listener) {
        return registerVariableListeners(VariableScope.ENV, key, listener);
    }

    public static Variable registerVariableListeners(VariableScope scope, String key, VariableListener listener) {
        Variable variable = getVariable(scope, key);
        variable.registerVariableListeners(listener);
        return variable;
    }

    public static Variable clearVariableListeners(String key) {
        return clearVariableListeners(VariableScope.ENV, key);
    }

    public static Variable clearVariableListeners(VariableScope scope, String key) {
        Variable variable = getVariable(scope, key);
        variable.clearVariableListeners();
        return variable;
    }

    public static Variable unregisterVariableListeners(String key, String identity) {
        return unregisterVariableListeners(VariableScope.ENV, key, identity);
    }

    public static Variable unregisterVariableListeners(VariableScope scope, String key, String identity) {
        Variable variable = getVariable(scope, key);
        variable.unregisterVariableListeners(identity);
        return variable;
    }
}
