package me.muphy.spring.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * 对象相似度算法
 * getMaxSimilarity 重集合中获取最接近的两个对象
 * getMinSimilarity 重集合中获取最不接近的两个对象
 * similarity 比较两个对象的相似度
 */
public class SimilarityAlgorithmUtils {

    /**
     * 相似度接口
     *
     * @param <T>
     */
    public interface Similarity<T> {
        double calculate(T t);
    }

    /**
     * 相似度最大计算
     *
     * @return
     */
    public static <T> T getMaxSimilarity(Collection<T> base, T t) {
        if (base == null || t == null || base.size() == 0) {
            return null;
        }
        T max = null;
        double maxSimilarity = 0;
        for (T obj : base) {
            double v = similarity(t, obj);
            if (maxSimilarity <= v || max == null) {
                max = obj;
                maxSimilarity = v;
            }
        }
        return max;
    }

    /**
     * 相似度最小计算
     *
     * @return
     */
    public static <T> T getMinSimilarity(Collection<T> base, T t) {
        if (base == null || t == null || base.size() == 0) {
            return null;
        }
        T min = null;
        double minSimilarity = 0;
        for (T obj : base) {
            double v = similarity(t, obj);
            if (minSimilarity >= v || min == null) {
                min = obj;
                minSimilarity = v;
            }
        }
        return min;
    }

    /**
     * 相似度计算
     *
     * @return
     */
    public static double similarity(Object base, Object obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        if (!base.getClass().equals(obj.getClass())) {
            return 0;
        }
        //相似度接口
        if (base instanceof Similarity) {
            return ((Similarity) base).calculate(obj);
        }
        if (base instanceof Boolean) {
            return booleanSimilarity((Boolean) base, ((Boolean) obj));
        }
        if (base instanceof Character) {
            return numberSimilarity((int) base, (int) obj);
        }
        if (base instanceof Number) {
            return numberSimilarity((Number) base, (Number) obj);
        }
        if (base instanceof CharSequence) {
            return stringSimilarity(base.toString(), obj.toString());
        }
        if (base instanceof Collection) {
            return collectionSimilarity((Collection) base, (Collection) obj);
        }
        if (base instanceof Map) {
            return mapSimilarity((Map) base, (Map) obj);
        }
        if (base.getClass().isArray()) {
            return arraySimilarity(base, obj);
        }
        return objectSimilarity(base, obj);
    }

    /**
     * 数值相似度
     *
     * @param base
     * @param obj
     * @return
     */
    private static double arraySimilarity(Object base, Object obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        int baseLength = Array.getLength(base);
        int objLength = Array.getLength(obj);
        double k = 0;
        for (int i = 0; i < baseLength; i++) {
            for (int j = 0; j < objLength; j++) {
                k += objectSimilarity(Array.get(base, i), Array.get(obj, j)); //取出数组中每个值
            }
        }
        return numberSimilarity(baseLength, objLength) * numberSimilarity(baseLength, k);
    }

    /**
     * 数值相似度
     *
     * @param base
     * @param obj
     * @return
     */
    private static double mapSimilarity(Map base, Map obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        double k = 0;
        for (Object key : base.keySet()) {
            if (obj.containsKey(key)) {
                k += objectSimilarity(base.get(key), obj.get(key));
            }
        }
        return numberSimilarity(base.size(), obj.size()) * numberSimilarity(base.size(), k);
    }

    /**
     * 数值相似度
     *
     * @param base
     * @param obj
     * @return
     */
    private static double collectionSimilarity(Collection base, Collection obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        int k = 0;
        for (Object o1 : base) {
            for (Object o2 : obj) {
                k += objectSimilarity(o1, o2);
            }
        }
        return numberSimilarity(base.size(), obj.size()) * numberSimilarity(base.size(), k);
    }

    /**
     * 数值相似度
     *
     * @param base
     * @param obj
     * @return
     */
    private static double objectSimilarity(Object base, Object obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        if (base.equals(obj)) {
            return 1;
        }
        Field[] fields = base.getClass().getFields();
        double sum = 0, k = 0;
        for (Field field : fields) {
            Object o1 = null, o2 = null;
            try {
                o1 = field.get(base);
                o2 = field.get(obj);
            } catch (IllegalAccessException e) {
                LogFileUtils.printStackTrace(e);
            }
            k++;
            sum += similarity(o1, o2);
        }
        if (k == 0) {
            return 0;
        }
        return sum / k;
    }

    /**
     * 数值相似度
     *
     * @param base
     * @param obj
     * @return
     */
    private static double booleanSimilarity(Boolean base, Boolean obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        return base.booleanValue() == obj.booleanValue() ? 1 : 0;
    }

    /**
     * 数值相似度
     *
     * @param base
     * @param obj
     * @return
     */
    private static double charSimilarity(Character base, Character obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        return numberSimilarity((int) base.charValue(), (int) obj.charValue());
    }

    /**
     * 数值相似度
     *
     * @param base
     * @param obj
     * @return
     */
    private static double numberSimilarity(Number base, Number obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        double b = base.doubleValue();
        double o = obj.doubleValue();
        double sum = b + o;
        if (sum < 0.00001) {
            if (b < 0.00001) {
                return 1;
            }
        }
        if (o >= b) {
            return b * 2 / sum;
        }
        return o * 2 / sum;
    }

    /**
     * 字符串相似度
     *
     * @param base
     * @param obj
     * @return
     */
    private static double stringSimilarity(String base, String obj) {
        if (base == obj) {
            return 1;
        }
        if (base == null || obj == null) {
            return 0;
        }
        int k = 0;
        for (int i = 0; i < base.length(); i++) {
            for (int j = 0; j < obj.length(); j++) {
                if (base.charAt(i) == obj.charAt(j)) {
                    k++;
                    break;
                }
            }
        }
        if (base.length() != obj.length()) {
            return numberSimilarity(base.length(), obj.length()) * numberSimilarity(base.length(), k);
        }
        return numberSimilarity(base.length(), k);
    }

}
