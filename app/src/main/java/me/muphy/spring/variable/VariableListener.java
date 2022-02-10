package me.muphy.spring.variable;

import me.muphy.spring.core.Identity;

public interface VariableListener extends Identity {
    boolean onChange(VariableEventArgs event);

    default int getVariableListenerType() {
        return VariableListenerType.always;
    }

    interface VariableListenerType {
        int always = 0;//循环监听
        int once = 1;//监听一次
    }
}
