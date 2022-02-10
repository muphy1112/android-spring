package me.muphy.spring.platform.android.util;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import me.muphy.spring.platform.android.AndroidContextHolder;
import me.muphy.spring.context.ContextHolder;
import me.muphy.spring.util.ExecutorUtils;
import me.muphy.spring.util.FileUtils;
import me.muphy.spring.util.LogFileUtils;
import me.muphy.spring.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyViewUtils {

    /**
     * 判断软键盘是否隐藏
     *
     * @param view
     * @param event
     * @return
     */
    public static boolean isShouldHideSoftKeyBoard(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] l = {0, 0};
            view.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + view.getHeight(), right = left
                    + view.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // If click the EditText event ,ignore it
                return false;
            } else {
                return true;
            }
        }
        // if the focus is EditText,ignore it;
        return false;
    }

    /**
     * 打开文件夹
     *
     * @param path
     */
    public static void openAssignFolder(String path) {
        File file = new File(path);
        if (null == file || !file.exists()) {
            Toast.makeText(AndroidContextHolder.getContext(), "文件不存在！", Toast.LENGTH_LONG);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "*/*");
        try {
            AndroidContextHolder.getContext().startActivity(intent);
//            startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            LogFileUtils.printStackTrace(e);
        }
    }

    /**
     * 设置16进制输入
     *
     * @param editText
     */
    public static void setEditTextHexInput(EditText editText) {
        editText.addTextChangedListener(
                new TextWatcher() {
                    private boolean flag = true;
                    private int index = 0;
                    private int beforeLen = 0;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (flag) {
                            beforeLen = s.length();
                            index = start - count + 1;
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        flag = false;
                        String hex = StringUtils.toHexFormal(s.toString());
                        if (hex.equals(s.toString())) {
                            flag = true;
                            if (beforeLen < hex.length()) {
                                index = index + hex.length() - beforeLen - 1;
                            }
                            if (index < 0) index = 0;
                            if (index > hex.length()) index = hex.length();
                            editText.setSelection(index);
                            return;
                        }
                        editText.setText(hex);
                    }
                });
    }

    /**
     * 判断应用是否在运行
     *
     * @return
     */
    public static boolean isApplicationRunning(String packageName) {
        boolean isAppRunning = false;
        try {
            ActivityManager am = (ActivityManager) AndroidContextHolder.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
            //100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                    isAppRunning = true;
                    break;
                }
            }
        } catch (Exception e) {
            LogFileUtils.printStackTrace(e);
        }
        return isAppRunning;
    }

    /**
     * 启动应用程序
     *
     * @param packageName
     * @param className
     * @return
     */
    public static boolean startApplication(String packageName, String className) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            AndroidContextHolder.getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            LogFileUtils.printStackTrace(e);
        }
        return false;
    }

    /**
     * 判断是否有网络连接
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) AndroidContextHolder.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     *
     * @return
     */
    public boolean isWifiConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) AndroidContextHolder.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isAvailable();
        }
        return false;
    }


    /**
     * 判断MOBILE网络是否可用
     *
     * @return
     */
    public boolean isMobileConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) AndroidContextHolder.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null) {
            return mMobileNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 获取当前网络连接的类型信息
     *
     * @return
     */
    public static int getConnectedType() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) AndroidContextHolder.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            return mNetworkInfo.getType();
        }
        return -1;
    }

    /**
     * 获取RAM信息。
     *
     * @return 当前可用内存。
     */
    public static ActivityManager.MemoryInfo getRamMemoryInfo() {
        //获取运行内存的信息
        ActivityManager manager = (ActivityManager) AndroidContextHolder.getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info;
    }

    /**
     * 获取ROM信息。
     *
     * @return 当前可用内存。
     */
    public static StatFs getRomMemoryInfo() {
        StatFs statFs = new StatFs(FileUtils.getFilesPath());
        return statFs;
    }

    /**
     * 调试的时候显示消息
     *
     * @param msg
     */
    public static void showMessage(String msg) {
        if (ContextHolder.isDebug()) {
            try {
                MyHandler handler = MyHandler.getInstance();
                ExecutorUtils.schedule(() -> {
                    Message message = Message.obtain();
                    message.obj = msg;
                    handler.sendMessage(message);
                }, 10, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LogFileUtils.printStackTrace(e);
            }
        }
    }


    private static class MyHandler extends Handler {
        private static MyHandler handler;

        public static MyHandler getInstance() {
            if(handler == null){
                synchronized (MyHandler.class){
                    if(handler == null){
                        handler = new MyHandler();
                    }
                }
            }
            return handler;
        }

        public void handleMessage(Message msg) {
            try {
                Toast.makeText(AndroidContextHolder.getContext(), String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                LogFileUtils.printStackTrace(e);
            }
        }
    }

}
