package me.muphy.spring.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import me.muphy.spring.context.ContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

public class JsonFileUtils {
    public static final <T> T parseObject(String fileName, Type clazz) {
        byte[] input = readFile(fileName);
        return JSON.parseObject(new String(input, Charset.forName("UTF-8")), clazz);
    }

    public static final Object parse(String fileName) {
        byte[] input = readFile(fileName);
        return JSON.parse(new String(input, Charset.forName("UTF-8")));
    }


    public static final JSONObject parseObject(String fileName) {
        byte[] input = readFile(fileName);
        return JSON.parseObject(new String(input, Charset.forName("UTF-8")));
    }

    public static final <T> List<T> parseArray(String fileName, Class<T> clazz) {
        byte[] input = readFile(fileName);
        return JSON.parseArray(new String(input, Charset.forName("UTF-8")), clazz);
    }

    public static final JSONArray parseArray(String fileName) {
        byte[] input = readFile(fileName);
        return JSON.parseArray(new String(input, Charset.forName("UTF-8")));
    }

    private static byte[] readFile(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        InputStream inputStream = null;
        if (ContextHolder.isDebug()) {
            try {
                inputStream = FileUtils.getHttpStaticFileInputStream("debug_" + fileName);
            } catch (IOException e) {
                LogFileUtils.printStackTrace(e);
            }
        }
        if (inputStream == null) {
            try {
                inputStream = FileUtils.getHttpStaticFileInputStream(fileName);
            } catch (IOException e) {
                LogFileUtils.printStackTrace(e);
            }
        }
        if (inputStream == null) {
            return null;
        }
        try {
            byte[] buf = new byte[inputStream.available()];
            inputStream.read(buf);
            return buf;
        } catch (IOException e) {
            LogFileUtils.printStackTrace(e);
        }
        return null;
    }

}
