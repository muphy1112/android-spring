package me.muphy.spring.platform.android.persistent;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.alibaba.fastjson.JSON;
import me.muphy.spring.annotation.Remind;
import me.muphy.spring.core.Identity;
import me.muphy.spring.platform.EntityId;
import me.muphy.spring.util.LogFileUtils;
import me.muphy.spring.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Remind("命令配置实体类")
@Entity(tableName = "persistent_entity")
public class PersistentEntity implements Identity {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @Remind("id")
    private int id;
    @Remind("记录的ID")
    @ColumnInfo(name = "data_id")
    private String dataId;
    @Remind("类型")
    @ColumnInfo(name = "category")
    private String category;
    @Remind("JSON数据")
    @ColumnInfo(name = "json_data")
    private String jsonData;

    public PersistentEntity() {
    }

    public PersistentEntity(EntityId entityId) {
        this.dataId = entityId.getId();
        this.category = entityId.getClass().getName();
        this.jsonData = JSON.toJSONString(entityId);
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public <T extends EntityId> T parse() {
        return parse(this);
    }

    public <T extends EntityId> T parse(Class<T> clazz) {
        return parse(this, clazz);
    }

    public static <T extends EntityId> T parse(PersistentEntity persistentEntity) {
        try {
            Class<? extends EntityId> aClass = Class.forName(persistentEntity.getCategory()).asSubclass(EntityId.class);
            String jsonData = persistentEntity.getJsonData();
            EntityId entity = JSON.parseObject(jsonData, aClass);
            return (T) entity;
        } catch (ClassNotFoundException e) {
            LogFileUtils.printStackTrace(e);
        }
        return null;
    }

    public static <T extends EntityId> T parse(PersistentEntity entity, Class<T> tClass) {
        if (entity == null || StringUtils.isEmptyOrWhiteSpace(entity.getJsonData())) {
            return null;
        }
        String jsonData = entity.getJsonData();
        return JSON.parseObject(jsonData, tClass);
    }

    public static <T extends EntityId> List<T> parse(List<PersistentEntity> entities, Class<T> tClass) {
        List<T> tList = new ArrayList<>();
        if (entities != null) {
            for (PersistentEntity entity : entities) {
                T parse = parse(entity, tClass);
                if (parse != null) {
                    tList.add(parse);
                }
            }
        }
        return tList;
    }
}
