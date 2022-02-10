package me.muphy.spring.util;

import me.muphy.spring.platform.android.util.AndroidClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {

    public static boolean isBaseObject(Object o) {
        if (o == null) {
            return true;
        }
        Class<?> returnType = o.getClass();
        return isBaseObject(returnType);
    }

    public static boolean isBaseObject(Class<?> returnType) {
        if (returnType == null) {
            return true;
        }
        return returnType.isPrimitive() || Number.class.isAssignableFrom(returnType) || Boolean.class.isAssignableFrom(returnType)
                || Character.class.isAssignableFrom(returnType) || CharSequence.class.isAssignableFrom(returnType);
    }

    //给一个接口，返回这个接口的所有实现类
    public static <T> List<Class<? extends T>> getAllClassByInterface(Class<T> c) {
        //如果不是一个接口，则不做处理
        if (c.isInterface()) {
            return getAllClassByType(c);
        }
        return new ArrayList<>();
    }

    public static <T> List<Class<? extends T>> getAllClassByInterface(Class<T> c, String packageName) {
        //如果不是一个接口，则不做处理
        if (c.isInterface()) {
            return getClasses(c, packageName);
        }
        return new ArrayList<>();
    }

    //给一个类，返回这个类的所有实现类
    public static <T> List<Class<? extends T>> getAllClassByType(Class<T> c) {
        //如果不是一个接口，则不做处理
        String packageName = c.getPackage().getName(); //获得当前的包名
        List<Class<? extends T>> classes = getClasses(c, packageName);
        return classes;
    }

    //给一个类，返回这个类的所有实现类
    public static <T> List<Class<? extends T>> getClasses(Class<T> c, String packageName) {
        List<Class<? extends T>> returnClassList = new ArrayList<>(); //返回结果
        try {
            List<Class> allClass = getClasses(packageName); //获得当前包下以及子包下的所有类
            //判断是否是同一个接口
            for (int i = 0; i < allClass.size(); i++) {
                if (c.isAssignableFrom(allClass.get(i))) { //判断是不是一个接口
                    if (!c.equals(allClass.get(i))) { //本身不加进去
                        returnClassList.add(allClass.get(i));
                    }
                }
            }
        } catch (Exception e) {
            LogFileUtils.printStackTrace(e);
        }
        return returnClassList;
    }

    //从一个包中查找出所有的类，在jar包中不能查找
    private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        List<String> dexFileClassNames = getClassNames(packageName);
        ArrayList<Class> classes = new ArrayList<>();
        for (String s : dexFileClassNames) {
            try {
                Class scanClass = Class.forName(s);
                classes.add(scanClass);
            } catch (Exception e) {
                continue;
            }
        }
        return classes;
    }

    public static List<String> getClassNames(List<String> packageNames) throws IOException {
        List<String> classes = new ArrayList<>();
        for (String packageName : packageNames) {
            classes.addAll(getClassNames(packageName));
        }
        return classes;
    }

    public static List<String> getClassNames(String packageName) throws IOException {
        List<String> classes = AndroidClassUtils.getClassNames(packageName);
        return classes;
    }

}