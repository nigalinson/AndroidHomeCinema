package com.xunlei.downloadlib.android;

import android.util.Log;

public class XLLog {

    public static void i(String str, String str2) {
        Log.i(str, str2);
    }

    public static void d(String str, String str2) {
        Log.d(str, str2);
    }

    public static void w(String str, String str2) {
        Log.w(str, str2);
    }

    public static void e(String str, String str2) {
        Log.e(str, str2);
    }

    public static void w(String str, String str2, Throwable th) {
        Log.w(str, str2, th);
    }

    public static void v(String str, String str2) {
        d(str, str2);
    }

}
