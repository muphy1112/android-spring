package me.muphy.spring.util;

import me.muphy.spring.common.Constants;

import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Matcher;

public class StringUtils {

    public static boolean isEmpty(Object content) {
        return content == null || "".equals(content);
    }

    public static boolean isNotEmpty(String content) {
        return content != null && content.length() > 0;
    }

    public static boolean equalsIgnoreCase(Object s1, Object s2) {
        if (s1 == null) {
            return s2 == null || String.valueOf(s2).isEmpty();
        }
        if (s2 == null) {
            return String.valueOf(s1).isEmpty();
        }
        return String.valueOf(s1).equalsIgnoreCase(String.valueOf(s2));
    }

    public static boolean equals(Object s1, Object s2) {
        if (s1 == null) {
            return s2 == null || String.valueOf(s2).isEmpty();
        }
        if (s2 == null) {
            return String.valueOf(s1).isEmpty();
        }
        return String.valueOf(s1).equals(s2);
    }

    public static boolean isEmptyOrWhiteSpace(String content) {
        return content == null || content.replaceAll("\\s", "").isEmpty();
    }

    public static boolean isNotEmptyOrWhiteSpace(String content) {
        return !isEmptyOrWhiteSpace(content);
    }

    public static String fillString(String src, String c, int len, boolean left) {
        if (StringUtils.isEmpty(src) || len <= src.length()) {
            return src;
        }
        int l = src.length();
        for (int i = 0; i < len - l; i++) {
            if (left) {
                src = c + src;
            } else {
                src += c;
            }
        }
        return src;
    }

    public static String toHexFormal(String src) {
        if (isEmpty(src)) {
            return "";
        }
        src = src.replaceAll("\\s+", "").replaceAll("[^0-9a-fA-FX]", "");
        char[] chars = src.toCharArray();
        StringBuilder sb = new StringBuilder();
        int i = 0, len = chars.length % 2 == 0 ? chars.length : chars.length - 1;
        for (; i < len; i++) {
            sb.append(chars[i]);
            sb.append(chars[++i]);
            sb.append(" ");
        }
        if (i < chars.length) {
            sb.append(chars[i]);
        }
        return sb.toString().trim().toUpperCase();
    }

    public static String getSuitableString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String suitableParseString = new String(bytes);
        if (Constants.ASCII_PATTERN.matcher(suitableParseString).matches()) {
            return "ASCII>" + suitableParseString;
        }
        suitableParseString = new BigInteger(1, bytes).toString(16);
        if (Constants.ASCII_PATTERN.matcher(suitableParseString).matches()) {
            suitableParseString = toHexFormal(suitableParseString);
            return "HEX>" + suitableParseString;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append(String.valueOf(bytes[i]));
            if (i < bytes.length - 1) {
                stringBuilder.append(" ");
            }
        }
        return "UNKNOWN>" + stringBuilder.toString();
    }

    /**
     * 转化为大驼峰
     *
     * @param name
     * @return
     */
    public static String getUpperCamelCase(String name) {
        if (name == null || "".equals(name)) {
            return "";
        }
        String[] strings = name.split("[^a-zA-Z0-9]+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(getTitleCase(strings[i]));
        }
        return sb.toString();
    }

    public static String getFirstLowerCase(Class clazz) {
        if (clazz == null) {
            return null;
        }
        return getFirstLowerCase(clazz.getSimpleName());
    }

    /**
     * 转化为小驼峰
     *
     * @param name
     * @return
     */
    public static String getLowerCamelCase(String name) {
        name = getUpperCamelCase(name);
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static String getTitleCase(String name) {
        if (name == null || "".equals(name)) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public static String getFirstLowerCase(String name) {
        if (name == null || "".equals(name)) {
            return "";
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static String getFirstUpperCase(String name) {
        if (name == null || "".equals(name)) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static <T extends Map<Object, Object>> String parseTemplate(String template, T properties) {
        Matcher matcher = Constants.PROPERTY_PATTERN.matcher(template);
        while (matcher.find()) {
            String group = matcher.group();
            String key = matcher.group(1);
            Object val = properties.get(key);
            if (val != null) {
                template = template.replace(group, String.valueOf(val));
            } else {
                template = template.replace(group, matcher.group(2));
            }
        }
        return template;
    }

    public static String getTemplateKey(String template) {
        if (isEmptyOrWhiteSpace(template)) {
            return template;
        }
        return template.replaceAll(Constants.TEMPLATE_FORMAL, "$1");
    }

    public static String getTemplateValue(String template) {
        if (isEmptyOrWhiteSpace(template)) {
            return "";
        }
        return template.replaceAll(Constants.TEMPLATE_FORMAL, "$2");
    }

    public static String valueOf(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    public static String nvl(Object value, String defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        return String.valueOf(value);
    }
}
