package com.sloth.tools.util;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/2/3 10:38
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/2/3         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ExecutorUtils {

    private static final String TAG = ExecutorUtils.class.getSimpleName();

    /**
     * 低优先度线程
     */
    public static final int IDLE_EXECUTOR_KEY = 0;
    public static final String IDLE_EXECUTOR_PREFIX = "ry_idle_thread_";

    /**
     * 普通工作线程
     */
    public static final int NORMAL_EXECUTOR_KEY = 1;
    public static final String NORMAL_EXECUTOR_PREFIX = "ry_nor_thread_";

    /**
     * 紧急线程
     */
    public static final int EMERGENCY_EXECUTOR_KEY = 2;
    public static final String EMERGENCY_EXECUTOR_PREFIX = "ry_emg_thread_";

    private final SparseArray<ExecutorService> executors = new SparseArray<>();

    static class Holder {
        static ExecutorUtils ins = new ExecutorUtils();
    }

    public ExecutorUtils() {
        executors.put(IDLE_EXECUTOR_KEY,
                new ThreadPoolExecutor(1, 2, 5,
                        TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100),
                        new DefaultThreadFactory(IDLE_EXECUTOR_PREFIX, Thread.MIN_PRIORITY),
                        REJECT_POLICY));

        executors.put(NORMAL_EXECUTOR_KEY,
                new ThreadPoolExecutor(10, 20, 30,
                        TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20),
                        new DefaultThreadFactory(NORMAL_EXECUTOR_PREFIX),
                        REJECT_POLICY));

        executors.put(EMERGENCY_EXECUTOR_KEY,
                new ThreadPoolExecutor(5, 5, 0,
                        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                        new DefaultThreadFactory(EMERGENCY_EXECUTOR_PREFIX, Thread.MAX_PRIORITY),
                        REJECT_POLICY));
    }

    public static ExecutorService getIdle(){
        return getService(IDLE_EXECUTOR_KEY);
    }

    public static ExecutorService getNormal(){
        return getService(NORMAL_EXECUTOR_KEY);
    }


    public static ExecutorService getEmergency(){
        return getService(EMERGENCY_EXECUTOR_KEY);
    }

    public static ExecutorService getService(int key){
        ExecutorService exe = Holder.ins.executors.get(key);
        if(exe == null){
            throw new RuntimeException(key + ":线程池已销毁");
        }
        return exe;
    }

    public static ExecutorService newService(int key, String preFix){
        ExecutorService exe = Holder.ins.executors.get(key);
        if(exe == null){
            Holder.ins.executors.put(key, new ThreadPoolExecutor(10, 20, 30,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20),
                    new DefaultThreadFactory(preFix),
                    REJECT_POLICY));
        }
        return Holder.ins.executors.get(key);
    }

    public static void stopAll(){
        for(int i = 0; i < Holder.ins.executors.size(); i++){
            ExecutorService exe = Holder.ins.executors.get(i);
            if(exe == null){ continue; }
            if(!exe.isShutdown()){
                exe.shutdownNow();
            }
        }
    }

    public void shutDown(int key){
        ExecutorService exe = Holder.ins.executors.get(key);
        if(exe != null && !exe.isShutdown()){
            exe.shutdown();
        }
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int priority;

        DefaultThreadFactory(String threadPreFix) {
            this(threadPreFix, Thread.NORM_PRIORITY);
        }

        DefaultThreadFactory(String threadPreFix, int prio) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = threadPreFix +
                    poolNumber.getAndIncrement() +
                    "-thread-";
            priority = prio;
        }


        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != priority) {
                t.setPriority(priority);
            }
            return t;
        }
    }

    public static abstract class WorkRunnable implements Runnable {
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

        public void runOnUiThread(Runnable runnable){
            mainHandler.post(runnable);
        }

    }

    private static final RejectedExecutionHandler REJECT_POLICY = new ThreadPoolExecutor.DiscardPolicy(){
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            super.rejectedExecution(r, e);
            LogUtils.e(TAG, "线程池已满，丢弃任务！");
        }
    };

}
