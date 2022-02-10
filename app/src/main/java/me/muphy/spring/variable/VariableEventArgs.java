package me.muphy.spring.variable;

public class VariableEventArgs {
    private Variable variable;
    private Object oldValue;

    public VariableEventArgs(Variable variable, Object oldValue) {
        this.variable = variable;
        this.oldValue = oldValue;
    }

    public Variable getVariable() {
        return variable;
    }

    public <T> T getValue() {
        return (T) variable.getValue();
    }

    public Object getOldValue() {
        return oldValue;
    }
}
