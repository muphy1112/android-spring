package me.muphy.spring.util;

import me.muphy.spring.annotation.Remind;
import me.muphy.spring.common.Callback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyCollectionUtils {

    public static <T> List<T> toList(T[] array) {
        List<T> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        for (T t : array) {
            list.add(t);
        }
        return list;
    }

    public static <T> List<T> toList(Collection<T> collection) {
        List<T> list = new ArrayList<>();
        if (collection == null) {
            return list;
        }
        for (T t : collection) {
            list.add(t);
        }
        return list;
    }

    public static <R, T> List<R> toList(Collection<T> collection, Callback<R, T> callback) {
        List<R> list = new ArrayList<>();
        if (collection == null) {
            return list;
        }
        for (T t : collection) {
            R r = callback.call(t);
            list.add(r);
        }
        return list;
    }

    public static <K, T> Map<K, T> toMap(Collection<T> collection, Callback<K, T> callback) {
        Map<K, T> map = new HashMap<>();
        if (collection == null) {
            return map;
        }
        for (T t : collection) {
            K k = callback.call(t);
            map.put(k, t);
        }
        return map;
    }

    public static byte[] bytesCollectionToBytes(Collection<byte[]> bytes) {
        if (bytes == null) {
            return new byte[0];
        }
        int len = 0;
        for (byte[] bys : bytes) {
            if (bys != null) {
                len += bys.length;
            }
        }
        byte[] buf = new byte[len];
        len = 0;
        for (byte[] bys : bytes) {
            if (bys != null) {
                System.arraycopy(bys, 0, buf, len, bys.length);
                len += bys.length;
            }
        }
        return buf;
    }

    public static class ByteArrayListIterable implements Iterable<List<byte[]>> {
        private final int limitLength;
        private List<byte[]> byteArrayList = new ArrayList<>();
        private int currentLastIndex = 0;

        public ByteArrayListIterable(List<byte[]> byteArrayList, int limitLength) {
            if (byteArrayList != null) {
                this.byteArrayList.addAll(byteArrayList);
            }
            this.limitLength = limitLength;
        }

        @Override
        public Iterator<List<byte[]>> iterator() {
            return new Iterator<List<byte[]>>() {

                private int index = -1;

                @Override
                public boolean hasNext() {
                    return byteArrayList != null && index + 1 < byteArrayList.size();
                }

                @Override
                public List<byte[]> next() {
                    index++;
                    int len = 0;
                    List<byte[]> bytes = new LinkedList<>();
                    for (currentLastIndex = index; currentLastIndex < byteArrayList.size(); currentLastIndex++) {
                        len += byteArrayList.get(currentLastIndex).length;
                        if (len > limitLength) {
                            currentLastIndex--;
                            return bytes;
                        }
                        bytes.add(byteArrayList.get(currentLastIndex));
                    }
                    return bytes;
                }
            };
        }

        public int getCurrentLastIndex() {
            return currentLastIndex;
        }
    }

    /**
     * 将对象转结合
     *
     * @param o
     * @return
     */
    public static Map<String, Object> toMap(Object o) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (o != null) {
            Method[] methods = o.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                String name = methods[i].getName();
                if ((name.startsWith("get") || name.startsWith("is")) && methods[i].getTypeParameters().length == 0 && !name.equals("getClass")) {
                    Class<?> returnType = methods[i].getReturnType();
                    if (returnType.isPrimitive() || Number.class.isAssignableFrom(returnType) || Boolean.class.isAssignableFrom(returnType)
                            || Character.class.isAssignableFrom(returnType) || CharSequence.class.isAssignableFrom(returnType)) {
                        try {
                            Object value = methods[i].invoke(o);
                            name = StringUtils.getFirstLowerCase(name.replaceAll("^(get|is)", ""));
                            hashMap.put(name, value);
                        } catch (Exception e) {
                            LogFileUtils.printStackTrace(e);
                        }
                    }
                }
            }
        }
        return hashMap;
    }

    public static <T> Map<String, List<T>> groupBy(List<T> list, Callback<String, T> callback) {
        if (list == null || callback == null) {
            return new HashMap<>();
        }
        Map<String, List<T>> map = new HashMap<>();
        for (T t : list) {
            String key = callback.call(t);
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(t);
        }
        return map;
    }

    @Remind("所有满足")
    public static <T> boolean allMatch(Collection<T> list, Callback<Boolean, T> callback) {
        if (list == null || callback == null || list.isEmpty()) {
            return false;
        }
        for (T t : list) {
            boolean key = callback.call(t);
            if (!key) {
                return false;
            }
        }
        return true;
    }

    @Remind("其中一个满足")
    public static <T> boolean anyMatch(Collection<T> list, Callback<Boolean, T> callback) {
        if (list == null || callback == null || list.isEmpty()) {
            return false;
        }
        for (T t : list) {
            boolean key = callback.call(t);
            if (key) {
                return true;
            }
        }
        return false;
    }
}
