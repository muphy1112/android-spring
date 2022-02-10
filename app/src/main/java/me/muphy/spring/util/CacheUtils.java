package me.muphy.spring.util;

import com.alibaba.fastjson.JSON;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存
 */
public class CacheUtils {

    /**
     * 缓存默认失效时间(毫秒)
     */
    private static final long DEFAULT_TIMEOUT = 3600 * 1000;
    /**
     * 缓存存储的map
     */
    private static final ConcurrentHashMap<String, CacheEntity> cacheMap = new ConcurrentHashMap<>();

    /**
     * 存储单元
     */
    private static class CacheEntity {
        /**
         * 值
         */
        private Object value;
        /**
         * 过期时间(毫秒)
         */
        private long expire;
        /**
         * 创建时的时间戳
         */
        private long timeStamp;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = JSON.parseObject(JSON.toJSONString(value), value.getClass());
        }

        public long getExpire() {
            return expire;
        }

        public void setExpire(long expire) {
            this.expire = expire;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

    public static boolean set(String key, Object value, long expire) {
        cacheMap.put(key, setEntity(key, value, expire));
        return true;
    }

    public static boolean set(String key, Object value) {
        cacheMap.put(key, setEntity(key, value, DEFAULT_TIMEOUT));
        return true;
    }

    private static CacheEntity setEntity(String key, Object value, long expire) {
        CacheEntity entity = new CacheEntity();
        entity.setValue(value);
        entity.setExpire(expire);
        entity.setTimeStamp(System.currentTimeMillis());
        return entity;
    }

    public static Object get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        removeTimeoutKey();
        CacheEntity entity = cacheMap.get(key);
        return entity == null ? null : entity.getValue();
    }

    public static boolean containKey(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return cacheMap.containsKey(key);
    }

    public synchronized static void remove(String key) {
        if (!StringUtils.isEmpty(key) && cacheMap.containsKey(key)) {
            cacheMap.remove(key);
        }
    }

    private synchronized static void removeTimeoutKey() {
        Iterator<Map.Entry<String, CacheEntity>> iterator = cacheMap.entrySet().iterator();
        while (iterator.hasNext()) {
            CacheEntity entity = iterator.next().getValue();
            if (entity.getExpire() == 0l) {
                continue;
            }
            long now = System.currentTimeMillis();
            if ((now - entity.getTimeStamp()) >= entity.getExpire()) {
                iterator.remove();
            }
        }
    }

}
