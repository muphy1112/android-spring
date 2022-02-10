package me.muphy.spring.platform.android.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import me.muphy.spring.platform.android.AndroidContextHolder;
import me.muphy.spring.util.ExecutorUtils;
import me.muphy.spring.util.LogFileUtils;

import java.util.List;

public class AndroidLocationUtils {

    private static Location location = null;
    private static boolean isInit = false;

    public static void init() {
        LocationManager locationManager = (LocationManager) AndroidContextHolder.getContext().getSystemService(Context.LOCATION_SERVICE);
        //添加权限检查
        if (ActivityCompat.checkSelfPermission(AndroidContextHolder.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(AndroidContextHolder.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //从GPS获取最新的定位信息
        LocationListener locationListener = new LocationListener() {
            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            // Provider被enable时触发此函数，比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {
            }

            // Provider被disable时触发此函数，比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {
            }

            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    //不为空,显示地理位置经纬度
                    AndroidLocationUtils.location = location;
                }
            }
        };
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                //输入经纬度
                AndroidLocationUtils.location = location;
            }
        } catch (Exception e) {
            LogFileUtils.printStackTrace(e);
            List<String> providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network 从Network获取最新的定位信息
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, locationListener);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        //输入经纬度
                        AndroidLocationUtils.location = location;
                    }
                } catch (Exception e1) {
                    LogFileUtils.printStackTrace(e1);
                    if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
                        //如果是Network 从Network获取最新的定位信息
                        try {
                            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000, 1, locationListener);
                            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                            if (location != null) {
                                //输入经纬度
                                AndroidLocationUtils.location = location;
                            }
                        } catch (Exception e2) {
                            LogFileUtils.printStackTrace(e2);
                        }
                    }
                }
            }
        }
    }

    public static Location getLocation() {
        if (!isInit) {
            isInit = true;
            ExecutorUtils.submit(() -> {
                Looper.prepare();//增加部分
                init();
                Looper.loop();//增加部分
            });
        }
        return location;
    }
}
