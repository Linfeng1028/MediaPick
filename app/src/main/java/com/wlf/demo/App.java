package com.wlf.demo;

import android.app.Application;

import com.wlf.mediapick.crash.CrashHandler;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 获取崩溃日志
        CrashHandler handler = new CrashHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}
