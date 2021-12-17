package com.sloth.functions;

import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;

import java.lang.ref.WeakReference;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/18 18:25
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class IDLE {

    private static class IDLEHandler implements MessageQueue.IdleHandler {

        private final WeakReference<Runnable> wkRunnable;

        public IDLEHandler(Runnable runnable) {
            this.wkRunnable = new WeakReference<>(runnable);
        }

        @Override
        public boolean queueIdle() {
            if(wkRunnable == null || wkRunnable.get() == null){
                return false;
            }

            wkRunnable.get().run();

            //return false - remove after executed
            return false;
        }
    }

    public static void runIdle(Runnable runnable) {
        runIdle(Looper.getMainLooper(), runnable);
    }

    public static void runIdle(Looper looper, Runnable runnable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //等待线程空闲时执行
            looper.getQueue().addIdleHandler(new IDLEHandler(runnable));
        }else{
            //低版本直接执行
            runnable.run();
        }
    }
}
