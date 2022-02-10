package me.muphy.spring.variable;

import me.muphy.spring.annotation.Remind;

@Remind("变量所属区域")
public enum VariableScope {
    @Remind("此区域包括配置文件，MIS同步以及动态构建的全绝属性配置，持久化存储")
    ENV,
    @Remind("此区域保存各设备的各种属性的当前值，不需要持久化存储")
    PROPERTY,
    @Remind("此区域主要记录一些会话或整个运行过程中不需要持久化存储且很少发生变化的变量")
    SESSION;
}
