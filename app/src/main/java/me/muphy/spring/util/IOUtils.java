package me.muphy.spring.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 流工具类
 */
public class IOUtils {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static final int EOF = -1;

    /**
     * 关闭所有流
     */
    public static void close(Closeable... closeables) {
        if (closeables != null) {
            for (int i = 0; i < closeables.length; i++) {
                if (closeables[i] != null) {
                    try {
                        closeables[i].close();
                    } catch (IOException e) {
                        LogFileUtils.printStackTrace(e);
                    }
                }
            }
        }
    }

    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(final InputStream input, final OutputStream output)
            throws IOException {

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}