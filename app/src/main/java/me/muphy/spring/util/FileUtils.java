package me.muphy.spring.util;

import me.muphy.spring.platform.android.util.AndroidFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileUtils {

    /**
     * 获取日志目录
     *
     * @return
     */
    public static String getLogPath() {
        String filesPath = getFilesPath();
        return filesPath + File.separator + "log" + File.separator;
    }

    /**
     * 获取文件流
     *
     * @return
     */
    public static InputStream getHttpStaticFileInputStream(String fileName) throws IOException {
        InputStream open = AndroidFileUtils.getAssetInputStream(fileName);
        return open;
    }

    /**
     * 获取文件列表
     *
     * @return
     */
    public static List<String> getHttpStaticFileList(String fileName) {
        List<String> list = AndroidFileUtils.getAssetList(fileName);
        return list;
    }

    /**
     * 获取文件路径
     *
     * @return
     */
    public static String getFilesPath(String fileName) {
        String filesPath = getFilesPath();
        return filesPath + File.separator + fileName + File.separator;
    }

    /**
     * 获取文件后缀
     *
     * @return
     */
    public static String getFileExtension(String fileName) {
        if(StringUtils.isEmpty(fileName)){
            return "";
        }
        int index = fileName.lastIndexOf('.');
        if(index < 1){
            return "";
        }
        return fileName.substring(index);
    }

    /**
     * 获取文件路径
     *
     * @return
     */
    public static String getFilesPath() {
        String filePath = AndroidFileUtils.getFilesPath();
        return filePath;
    }

    /**
     * 获取缓存目录
     *
     * @return
     */
    public static String getCachePath() {
        String cachePath = AndroidFileUtils.getCachePath();
        return cachePath;
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 文件下载
     *
     * @return
     */
    public static long downloadFile(String url, String savePath, String title, String desc) {
        return AndroidFileUtils.downloadFile(url, savePath, title, desc);
    }
}
