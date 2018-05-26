package com.techexe.crashreporter;

import android.app.Application;

public class CrashApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));
    }
}
