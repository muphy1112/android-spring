package me.muphy.spring.platform.android;

import android.content.Context;

public class AndroidContextHolder {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AndroidContextHolder.context = context;
    }

}
