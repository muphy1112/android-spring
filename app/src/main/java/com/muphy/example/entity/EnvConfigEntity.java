package com.muphy.example.entity;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.platform.EntityId;
import me.muphy.spring.util.StringUtils;

@Remind("云端环境配置配置实体类，只用于同步保存")
public class EnvConfigEntity implements EntityId {
    @Remind("id")
    private String id;
    @Remind("属性")
    private String property;
    @Remind("名称")
    private String name;
    @Remind("值")
    private String value;
    @Remind("数据类型")
    private String dataType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getName() {
        if (StringUtils.isEmpty(name)) {
            return property;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
