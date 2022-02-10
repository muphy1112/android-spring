# android-spring

#### 介绍
安卓版spring，支持安卓前后端分离或者单体架构开发方式，前端采用熟悉的html、js甚至vue开发，java开发后台接口，好处是client可以是嵌入的webView，也可以是可访问安卓IP的浏览器，抛弃安卓原本的ui界面开发方式

#### 软件架构
基于http服务的方式开发安卓应用程序；
包含ioc、mvc、orm、lifecycle、全局变量监听和各种工具等；
1.  代码结构
![代码结构](https://images.gitee.com/uploads/images/2022/0210/151313_b14e44de_8516774.png "屏幕截图.png")
2.  webview显示页面
![webview显示页面](https://images.gitee.com/uploads/images/2022/0210/151411_626d4c3f_8516774.png "屏幕截图.png")
3.  浏览器显示
![浏览器显示](https://images.gitee.com/uploads/images/2022/0210/151519_484f01b3_8516774.png "屏幕截图.png")

#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  AndroidManifest.xml
```
    <?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.muphy.example"
    android:exported="true"
    android:installLocation="preferExternal">

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
    <uses-permission android:name="android.permission.INTERNET" />
    <permission android:name="com.machinebook.customer.permission.JPUSH_MESSAGE" android:protectionLevel="signature" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Example">

        <service android:name="org.eclipse.paho.android.service.MqttService" /> <!--MqttService-->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="com.muphy.example.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths" />
        </provider>

        <activity
            android:name=".MainActivity"
            android:label="@string/app_name">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```
2.  依赖
```
    def room_version = "2.2.5"
    implementation "androidx.room:room-runtime:$room_version"
    annotationProcessor "androidx.room:room-compiler:$room_version"
    implementation 'com.alibaba:fastjson:1.1.54.android'
    implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'
    implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
    implementation 'com.faendir.rhino:rhino-android:1.6.0'
```
3.  xxxx
4.  MainActivity.java
![activity](https://images.gitee.com/uploads/images/2022/0210/151743_4de03c55_8516774.png "屏幕截图.png")

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
