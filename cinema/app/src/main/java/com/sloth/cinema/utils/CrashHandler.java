package com.sloth.cinema.utils;

import com.sloth.tools.util.LogUtils;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public CrashHandler() { }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        LogUtils.e("CrashHandler", e.getMessage() != null ? e.getMessage() : "crashed !");
    }

}
