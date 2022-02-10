package me.muphy.spring.platform.android.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;

import me.muphy.spring.platform.android.AndroidContextHolder;
import me.muphy.spring.util.LogFileUtils;
import me.muphy.spring.util.LogUtils;
import me.muphy.spring.util.MyCollectionUtils;
import me.muphy.spring.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AndroidFileUtils {

    /**
     * 获取日志目录
     *
     * @return
     */
    public static InputStream getAssetInputStream(String fileName) throws IOException {
        AssetManager assetManager = AndroidContextHolder.getContext().getAssets();
        InputStream open = assetManager.open(fileName);
        return open;
    }

    /**
     * 获取日志目录
     *
     * @return
     */
    public static List<String> getAssetList(String fileName) {
        AssetManager assetManager = AndroidContextHolder.getContext().getAssets();
        String[] list = new String[0];
        try {
            list = assetManager.list(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> files = MyCollectionUtils.toList(list);
        return files;
    }

    /**
     * 获取文件路径
     *
     * @return
     */
    public static String getFilesPath() {
        Context context = AndroidContextHolder.getContext();
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            filePath = context.getExternalFilesDir(null).getPath();
        } else {
            //外部存储不可用
            filePath = context.getFilesDir().getPath();
        }
        return filePath;
    }

    /**
     * 获取缓存目录
     *
     * @return
     */
    public static String getCachePath() {
        Context context = AndroidContextHolder.getContext();
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath();
        }
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
    public static long downloadFile(String url, String filePath, String notificationTitle, String describeInfo) {
        if (StringUtils.isEmpty(url)) {
            LogFileUtils.recordToFile("文件下载", "url为空!");
            return -1;
        }
        try {
            Uri uri = Uri.parse(url);
            DownloadManager downloadManager = (DownloadManager) AndroidContextHolder.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 在通知栏中显示
            request.setVisibleInDownloadsUi(true);
            if (StringUtils.isEmpty(notificationTitle)) {
                request.setTitle(notificationTitle);
            }
            if (StringUtils.isEmpty(describeInfo)) {
                request.setDescription(describeInfo);
            }
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            // 若存在，则删除
            deleteFile(filePath);
            LogUtils.d("文件下载", "路径: " + filePath);
            Uri fileUri = Uri.parse("file://" + filePath);
            request.setDestinationUri(fileUri);
            return downloadManager.enqueue(request);
        } catch (Exception e) {
            LogFileUtils.printStackTrace(e);
        }
        return -1;
    }
}
