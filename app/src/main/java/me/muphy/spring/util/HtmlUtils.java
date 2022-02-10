package me.muphy.spring.util;

import me.muphy.spring.annotation.Remind;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlUtils {
    public static <T> String getTablePageHtml(List<T> list) {
        if (list == null) {
            return "<h1 style='width: 100%;text-align: center'>没有数据！</h1>";
        }
        String v = "<!DOCTYPE html><html lang='en'><html><head><meta charset='UTF-8'><title>数据同步</title>" +
                "</head><body style='width:100%'><div style='width:100%;font-size:14px' id='createtable'>" +
                getTable(list, false) +
                "</div></body></html>";
        return v;
    }

    public static HashMap<String, String> getTableHead(Class<?> aClass) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (aClass == null) {
            return hashMap;
        }
        List<Field> fieldList = ReflectUtils.getFieldList(aClass);
        for (Field field : fieldList) {
            Remind annotation = field.getAnnotation(Remind.class);
            if (annotation == null || annotation.deprecated()) {
                hashMap.put(field.getName(), field.getName());
                continue;
            }
            hashMap.put(field.getName(), annotation.value());
        }
        return hashMap;
    }

    public static <T> String getTable(List<T> list) {
        return getTable(list, false);
    }

    public static <T> String getTable(List<T> list, boolean all) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        Class<?> aClass = list.get(0).getClass();
        List<Field> fieldList = ReflectUtils.getFieldList(aClass);
        HashMap<Field, String> hashMap = new HashMap<>();
        HashMap<Field, Boolean> flags = new HashMap<>();
        for (Field field : fieldList) {
            Remind annotation = field.getAnnotation(Remind.class);
            if (!all || annotation == null || annotation.deprecated()) {
                continue;
            }
            flags.put(field, field.isAccessible());
            field.setAccessible(true);
            hashMap.put(field, annotation.value());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<table style='color:#909399;width:100%'><thead style='width:100%'><tr>");
        for (Map.Entry<Field, String> entry : hashMap.entrySet()) {
            stringBuilder.append("<th style='border-bottom: 1px solid #ebeef5;text-align:left;padding:12px 0;'>").append(entry.getValue()).append("</th>");
        }
        stringBuilder.append("</tr></thead><tbody>");
        for (T t : list) {
            stringBuilder.append("<tr>");
            for (Map.Entry<Field, String> entry : hashMap.entrySet()) {
                try {
                    Object o = entry.getKey().get(t);
                    stringBuilder.append("<td style='border-bottom: 1px solid #ebeef5;text-align:left;padding:12px 0;'>").append(StringUtils.valueOf(o)).append("</td>");
                } catch (IllegalAccessException e) {
                    stringBuilder.append("<td style='border-bottom: 1px solid #ebeef5;text-align:left;padding:12px 0;'>").append("").append("</td>");
                }
            }
            stringBuilder.append("</tr>");
        }
        for (Map.Entry<Field, String> entry : hashMap.entrySet()) {
            entry.getKey().setAccessible(flags.get(entry.getKey()));
        }
        stringBuilder.append("</tbody></table>");
        return stringBuilder.toString();
    }
}
