package com.sloth.tools.util;

import androidx.annotation.NonNull;
import com.sloth.pinsplatform.log.Log;


public final class LogUtils {

    private static Log sLogImpl = null;

    public static void init(Log logImpl) {
        sLogImpl = logImpl;
    }

    public static void d(String msg) {
        if(sLogImpl != null){
            sLogImpl.d("D", msg);
        }
    }

    public static void d(String tag, String msg) {
        if(sLogImpl != null){
            sLogImpl.d(tag, msg);
        }
    }

    public static void v(String msg) {
        if(sLogImpl != null){
            sLogImpl.v("V", msg);
        }
    }

    public static void v(String tag, String msg) {
        if(sLogImpl != null){
            sLogImpl.v(tag, msg);
        }
    }

    public static void i(String msg) {
        if(sLogImpl != null){
            sLogImpl.i("I", msg);
        }
    }

    public static void i(String tag, String msg) {
        if(sLogImpl != null){
            sLogImpl.i(tag, msg);
        }
    }

    public static void w(String msg) {
        if(sLogImpl != null){
            sLogImpl.w("W", msg);
        }
    }

    public static void w(String tag, String msg) {
        if(sLogImpl != null){
            sLogImpl.w(tag, msg);
        }
    }

    public static void e(@NonNull String msg) {
        if(sLogImpl != null){
            sLogImpl.e("E", msg);
        }
    }

    public static void e(String tag, @NonNull String msg) {
        if(sLogImpl != null){
            sLogImpl.e(tag, msg);
        }
    }

    public static void json(String msg) {
        if(sLogImpl != null){
            sLogImpl.json("JSON", msg);
        }
    }

    public static void json(String tag, String msg) {
        if(sLogImpl != null){
            sLogImpl.json(tag, msg);
        }
    }

    public static void flush(boolean sync) {
        if(sLogImpl != null){
            sLogImpl.flush(sync);
        }
    }

    public static void exit() {
        if(sLogImpl != null){
            sLogImpl.exit();
        }
    }
}
