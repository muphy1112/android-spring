package me.muphy.spring.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogFileUtils {

    private static String HOME_FILE = FileUtils.getLogPath();
    private static long lastClearTime = System.currentTimeMillis();

    static {
        File homeDir = new File(HOME_FILE);
        if (!homeDir.exists()) {
            homeDir.mkdirs();
        }
        clearOldLog();
    }

    /**
     * 打印到文件
     *
     * @param fileName
     * @param message
     */
    public static void printToFile(String fileName, String message) {
        if (System.currentTimeMillis() - lastClearTime > 86400000) {
            clearOldLog();
        }
        File file = new File(HOME_FILE, fileName);
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file, true);
            fw.write("\n");
            fw.write(message);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fw);
        }
    }

    /**
     * 打印到文件
     *
     * @param fileName
     * @param sw
     */
    public static void printToFile(String fileName, StringWriter sw) {
        printToFile(fileName, sw.toString());
    }

    /**
     * 记录一些消息到文件
     *
     * @param tag
     * @param data
     */
    public static void recordToFile(String tag, String data) {
        String timeString = DateTimeUtils.getTimeString2();
        String logName = "record-" + DateTimeUtils.getNowDate(DateTimeUtils.DatePattern.ONLY_DAY) + ".txt";
        printToFile(logName, timeString + "@" + tag + ">: " + data);
    }

    /**
     * 记录一些消息到文件
     *
     * @param data
     */
    public static void recordToFile(String data) {
        recordToFile("", data);
    }

    public static void printStackTrace(Throwable ex) {
        printStackTrace(ex, "");
    }

    public static void printStackTrace(Throwable ex, String info) {
        ex.printStackTrace();
        LogUtils.e(LogFileUtils.class.getSimpleName(), info, ex);
        String logName = DateTimeUtils.getNowDate(DateTimeUtils.DatePattern.ONLY_DAY) + ".log";
        String date = DateTimeUtils.getTimeString2();
        StringWriter sw = new StringWriter();
        sw.append(date).append("\n");
        ex.printStackTrace(new PrintWriter(sw));
        sw.append("*******************************************************").append("\n");
        printToFile(logName, sw);
    }

    public static void clearOldLog() {
        File homeDir = new File(HOME_FILE);
        for (File file : homeDir.listFiles()) {
            if (System.currentTimeMillis() - file.lastModified() > 86400000L * 10 || file.length() > 100 * 1024 * 1024) {
                file.delete();
            }
        }
    }
}
