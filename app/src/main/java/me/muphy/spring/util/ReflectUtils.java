package me.muphy.spring.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectUtils {

    public static List<Field> getFieldList(Class<?> tClass) {
        return ReflectUtils.getFieldList(tClass, false, null);
    }

    public static List<Field> getAllFieldList(Class<?> tClass) {
        return ReflectUtils.getFieldList(tClass, true, null);
    }

    public static List<Field> getFieldList(Class<?> tClass, boolean superClass, List<Field> fieldSet) {
        if (fieldSet == null) {
            fieldSet = new ArrayList<>();
        }
        fieldSet.addAll(Arrays.asList(tClass.getFields()));
        fieldSet.addAll(Arrays.asList(tClass.getDeclaredFields()));

        if (superClass) {
            Class supperClass = tClass.getSuperclass();
            if (!Object.class.equals(supperClass)) {
                return getFieldList(supperClass, superClass, fieldSet);
            }
        }
        return fieldSet;
    }

    public static String getType(String name) {
        return name.substring(0, name.indexOf("."));
    }

//    public static int getResourceId(String paramString, String type) {
//        Context paramContext = ContextHolder.getAndroidContext();
//        return paramContext.getResources().getIdentifier(paramString, type, paramContext.getPackageName());
//    }

    public static void setData(Field field, Object src, Object o) {
        boolean temp = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(src, o);
        } catch (IllegalAccessException e) {
            LogFileUtils.printStackTrace(e);
        }
        field.setAccessible(temp);
    }

    public static String getName(String name) {
        return name.substring(name.indexOf(".") + 1);
    }

    /**
     * 判断是否是基础数据类型的包装类型
     *
     * @param clz
     * @return
     */
    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否是基础数据类型，即 int,double,long等类似格式
     */
    public static boolean isCommonDataType(Class clazz) {
        return clazz.isPrimitive();
    }
}
