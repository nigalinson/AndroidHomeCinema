package com.sloth.udp;

import android.os.Looper;

import com.sloth.tools.util.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/29 17:58
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class UDPSender {
    private static final String TAG = "PackSender";

    public static final int STATE_RUNNING = 1;
    public static final int STATE_INIT = 0;
    public static final int STATE_CLOSE = -1;

    private MulticastSocket multicastSocket;
    private InetAddress group;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(15);

    private final AtomicInteger state = new AtomicInteger(STATE_INIT);

    private final AtomicBoolean netAvailable = new AtomicBoolean(true);

    public UDPSender(Looper looper) { }

    public void loop(){
        executor.execute(() -> {
            while(state.get() != STATE_CLOSE){
                boolean needInitNet = (multicastSocket == null || state.get() == STATE_INIT);
                if(needInitNet && netAvailable.get()){
                    initSender();
                }

                if(multicastSocket == null){

                    LogUtils.d(TAG, "广播发射器未初始化");
                    continue;
                }
                String msg = null;
                try {
                    msg = queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(msg == null || "".equals(msg)){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                try {
                    LogUtils.d(TAG, "发包成功：" + msg);
                    DatagramPacket theOutput = new DatagramPacket(msg.getBytes(), msg.length(), group, UDPManager.PORT);
                    multicastSocket.send(theOutput);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            destroySender();
        });
    }

    private void initSender() {
        destroySender();
        try {
            group = InetAddress.getByName(UDPManager.HOST);
            multicastSocket = new MulticastSocket();
            multicastSocket.joinGroup(group);
            multicastSocket.setLoopbackMode(false);
            multicastSocket.setTimeToLive(1);
            multicastSocket.setBroadcast(true);

            if(state.get() != STATE_CLOSE){
                state.set(STATE_RUNNING);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroySender(){
        if(multicastSocket != null){
            multicastSocket.close();
            multicastSocket = null;
        }
    }

    public void send(String data){
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        state.set(STATE_CLOSE);
    }

    public void onNetStateChanged(boolean available) {
        netAvailable.set(available);
        if(!available){
            if(state.get() != STATE_CLOSE){
                state.set(STATE_INIT);
            }
        }
    }

}
