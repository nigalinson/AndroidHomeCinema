package com.sloth.functions.test;

import android.os.Handler;
import android.os.Looper;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/9 13:54
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/9         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class AsyncTestUnit {

    public static void test(TestRunnable runnable) throws InterruptedException {
         new AsyncTestUnit().run(runnable);
    }

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Object lock = new Object();

    private AsyncTestUnit() { }

    private void run(TestRunnable runnable) throws InterruptedException {
        runnable.setLock(lock);
        handler.post(runnable);
        synchronized (lock){
            lock.wait();
        }
    }

    public abstract static class TestRunnable implements Runnable {
        private Object lock;


        public TestRunnable() { }

        public void setLock(Object lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                runTest();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("test interrupted !");
            }
        }

        protected abstract void runTest() throws Exception;

        public void endTest(){
            synchronized (lock){
                lock.notifyAll();
            }
        }

        public void testFailed(){
            endTest();
            throw new RuntimeException("test failed");
        }
    }

}
