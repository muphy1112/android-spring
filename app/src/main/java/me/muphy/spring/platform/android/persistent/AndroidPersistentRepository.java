package me.muphy.spring.platform.android.persistent;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.platform.EntityId;
import me.muphy.spring.platform.PersistentRepository;
import me.muphy.spring.util.ExecutorUtils;
import me.muphy.spring.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class AndroidPersistentRepository implements PersistentRepository {
    private final PersistentDao dao;
    private List<PersistentEntity> entityList;

    public AndroidPersistentRepository() {
        PersistentDatabase database = PersistentDatabase.getDatabase();
        dao = database.getDao();
    }

    public <T extends EntityId> Future<?> asyncInserts(List<T> entities) {
        return ExecutorUtils.submit(() -> {
            if (entities == null || entities.isEmpty()) {
                return;
            }
            List<PersistentEntity> persistentEntities = new ArrayList<>();
            for (EntityId entity : entities) {
                persistentEntities.add(new PersistentEntity(entity));
            }
            dao.inserts(persistentEntities);
        });
    }

    public Future<?> asyncDeleteAll() {
        return ExecutorUtils.submit(() -> dao.deleteAll());
    }

    public <T extends EntityId> Future<?> asyncDeleteAll(Class<T> category) {
        return ExecutorUtils.submit(() -> dao.deleteAll(category.getName()));
    }

    public Future<?> asyncDeleteById(String id) {
        return ExecutorUtils.submit(() -> dao.deleteById(id));
    }

    public <T extends EntityId> Future<?> asyncDeleteByCategoryAndDataId(Class<T> category, String id) {
        return ExecutorUtils.submit(() -> dao.deleteByCategoryAndDataId(category.getName(), id));
    }

    @Remind("返回值不确定，只能PersistentEntity列表")
    public Future<List<PersistentEntity>> asyncSelectAll() {
        return ExecutorUtils.submit(() -> {
            if (entityList != null && !entityList.isEmpty()) {
                return entityList;
            }
            entityList = dao.selectAll();
            return entityList;
        });
    }

    public <T extends EntityId> Future<List<T>> asyncSelectAll(Class<T> category) {
        return ExecutorUtils.submit(() -> {
            List<PersistentEntity> persistentEntities = dao.selectAll(category.getName());
            return PersistentEntity.parse(persistentEntities, category);
        });
    }

    public <T extends EntityId> Future<T> asyncSelectById(String id) {
        return ExecutorUtils.submit(() -> {
            PersistentEntity persistentEntity = dao.selectById(id);
            return PersistentEntity.parse(persistentEntity);
        });
    }

    public <T extends EntityId> Future<T> asyncSelectByCategoryAndDataId(Class<T> category, String dataId) {
        return ExecutorUtils.submit(() -> {
            PersistentEntity persistentEntity = dao.selectByCategoryAndDataId(category.getName(), dataId);
            if (persistentEntity == null || StringUtils.isEmptyOrWhiteSpace(persistentEntity.getJsonData())) {
                return null;
            }
            T t = PersistentEntity.parse(persistentEntity, category);
            return t;
        });
    }

}