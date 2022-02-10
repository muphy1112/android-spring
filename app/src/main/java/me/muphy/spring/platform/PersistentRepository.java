package me.muphy.spring.platform;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface PersistentRepository {

    default int getTimeout() {
        return 30;
    }

    <T extends EntityId> Future<?> asyncInserts(List<T> entities);

    default <T extends EntityId> void inserts(List<T> entities) throws Exception {
        asyncInserts(entities).get(getTimeout(), TimeUnit.SECONDS);
    }

    Future<?> asyncDeleteAll();

    default void deleteAll() throws Exception {
        asyncDeleteAll().get(getTimeout(), TimeUnit.SECONDS);
    }

    <T extends EntityId> Future<?> asyncDeleteAll(Class<T> category);

    default <T extends EntityId> void deleteAll(Class<T> category) throws Exception {
        asyncDeleteAll(category).get(getTimeout(), TimeUnit.SECONDS);
    }

    Future<?> asyncDeleteById(String id);

    default void deleteById(String id) throws Exception {
        asyncDeleteById(id).get(getTimeout(), TimeUnit.SECONDS);
    }

    <T extends EntityId> Future<?> asyncDeleteByCategoryAndDataId(Class<T> category, String id);

    default <T extends EntityId> void deleteByCategoryAndDataId(Class<T> category, String id) throws Exception {
        asyncDeleteByCategoryAndDataId(category, id).get(getTimeout(), TimeUnit.SECONDS);
    }

    <T extends EntityId> Future<List<T>> asyncSelectAll(Class<T> category);

    default <T extends EntityId> List<T> selectAll(Class<T> category) throws Exception {
        return asyncSelectAll(category).get(getTimeout(), TimeUnit.SECONDS);
    }

    <T extends EntityId> Future<T> asyncSelectById(String id);

    default <T extends EntityId> T selectById(String id) throws Exception {
        Future<T> objectFuture = asyncSelectById(id);
        return objectFuture.get(getTimeout(), TimeUnit.SECONDS);
    }

    <T extends EntityId> Future<T> asyncSelectByCategoryAndDataId(Class<T> category, String dataId);

    default <T extends EntityId> T selectByCategoryAndDataId(Class<T> category, String dataId) throws Exception {
        return asyncSelectByCategoryAndDataId(category, dataId).get(getTimeout(), TimeUnit.SECONDS);
    }

}