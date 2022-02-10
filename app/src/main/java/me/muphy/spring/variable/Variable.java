package me.muphy.spring.variable;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.util.EnvironmentUtils;
import me.muphy.spring.util.LogFileUtils;
import me.muphy.spring.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

@Remind("此变量用于多级存储变量数据并支持监听，操作和功能有点类似于MutableLiveData，但功能更好用")
public class Variable {

    private String property;
    private Object value;
    private List<VariableListener> listeners;
    private List<Variable> children = new ArrayList<>();

    public Variable() {
    }

    public Variable(String property, Object value) {
        this.property = property;
        setValue(value);
    }

    public Variable(String property, Object value, List<VariableListener> listeners) {
        this(property, value);
        this.listeners = listeners;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getValue() {
        if (value == null || !(value instanceof String)) {
            return value;
        }
        String propertyValue = String.valueOf(value);
        if (propertyValue.contains("${")) {
            Properties properties = EnvironmentUtils.getProperties();
            propertyValue = StringUtils.parseTemplate(propertyValue, properties);
        }
        return propertyValue;
    }

    public List<Variable> getChildren() {
        return children;
    }

    public void setChildren(List<Variable> children) {
        this.children = children;
    }

    public void registerVariableListeners(VariableListener listener) {
        if (listener == null) {
            return;
        }
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        unregisterVariableListeners(listener.getIdentity());
        listeners.add(listener);
        if (value != null) {
            notifyDataChanged();
        }
    }

    public void notifyDataChanged() {
        VariableEventArgs event = new VariableEventArgs(this, this.value);
        trigger(event);
    }

    public void unregisterVariableListeners(String identity) {
        if (StringUtils.isEmpty(identity)) {
            return;
        }
        if (listeners != null) {
            Iterator<VariableListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                VariableListener next = iterator.next();
                if (identity.equals(next.getIdentity())) {
                    iterator.remove();
                }
            }
        }
    }

    public void clearVariableListeners() {
        if (listeners != null) {
            listeners = new ArrayList<>();
        }
    }

    public void setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;
        VariableEventArgs event = new VariableEventArgs(this, oldValue);
        trigger(event);
    }

    private void trigger(VariableEventArgs event) {
        if (listeners == null) {
            return;
        }
        Iterator<VariableListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            VariableListener listener = iterator.next();
            boolean success = false;
            try {
                success = listener.onChange(event);
            } catch (Exception e) {
                LogFileUtils.printStackTrace(e);
            }
            //成功后将执行一次的移除
            if (success && listener.getVariableListenerType() == VariableListener.VariableListenerType.once) {
                iterator.remove();
            }
        }
    }
}
