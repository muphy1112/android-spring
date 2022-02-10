package me.muphy.spring.platform.android.util;

import me.muphy.spring.platform.android.AndroidContextHolder;
import me.muphy.spring.util.CacheUtils;
import me.muphy.spring.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class AndroidClassUtils {

    public static List<String> getClassNames(String packageName) throws IOException {
        List<String> classes = new ArrayList<>();
        if (StringUtils.isEmpty(packageName)) {
            return classes;
        }
        Object o = CacheUtils.get("classNames-" + packageName);
        if (o != null) {
            return (List<String>) o;
        }
        DexFile df = new DexFile(AndroidContextHolder.getContext().getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
        Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
        while (enumeration.hasMoreElements()) {//遍历
            String className = enumeration.nextElement();
            if (className.startsWith(packageName)) {
                classes.add(className);
            }
        }
        CacheUtils.set("classNames-" + packageName, classes, 30000);
        return classes;
    }

}