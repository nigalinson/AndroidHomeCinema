package com.sloth.udp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import com.sloth.tools.util.LogUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class UDPReceiver implements UDPReceiverCallback {

    private static final String TAG = "PackReceiver";

    public static final int STATE_RUNNING = 1;
    public static final int STATE_INIT = 0;
    public static final int STATE_CLOSE = -1;

    @Override
    public void receive(String data) {
        if(callback != null){
            callback.receive(data);
        }
    }

    private static final class ReceiverHandler extends Handler {

        private UDPReceiverCallback mCallback;

        public ReceiverHandler(@NonNull Looper looper) {
            super(looper);
        }

        public void setCallback(UDPReceiverCallback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(mCallback != null){
                mCallback.receive((String) msg.obj);
            }
        }
    }

    private MulticastSocket multicastSocket;
    private InetAddress group;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ReceiverHandler receiverHaler;

    private UDPReceiverCallback callback;

    private final AtomicInteger state = new AtomicInteger(STATE_INIT);

    private final AtomicBoolean netAvailable = new AtomicBoolean(true);

    public UDPReceiver(Looper looper) {
        receiverHaler = new ReceiverHandler(looper);
        receiverHaler.setCallback(this);
    }

    public void receive(UDPReceiverCallback callback){
        this.callback = callback;
        executorService.execute(() -> {
                while(state.get() != STATE_CLOSE){
                    boolean needInitNet = (multicastSocket == null || state.get() == STATE_INIT);
                    if(needInitNet && netAvailable.get()){
                        initWlan();
                    }

                    if(multicastSocket == null) {
                        LogUtils.d(TAG, "包监听器未初始化完成");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, UDPManager.PORT);
                    try {
                        multicastSocket.receive(packet);
                        String s = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                        Message msg = Message.obtain();
                        msg.obj = s;
                        receiverHaler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogUtils.d(TAG, e.getMessage());
                    }
                }

                destroyWlan();
        });
    }

    private void initWlan() {
        destroyWlan();
        try {
            group = InetAddress.getByName(UDPManager.HOST);
            multicastSocket = new MulticastSocket(UDPManager.PORT);
            multicastSocket.joinGroup(group);
            multicastSocket.setLoopbackMode(false);
            multicastSocket.setTimeToLive(1);
            multicastSocket.setBroadcast(true);
            multicastSocket.setReuseAddress(true);

            if(state.get() != STATE_CLOSE){
                state.set(STATE_RUNNING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            group = null;
            multicastSocket = null;
            //出错后，清空记录
        }
    }

    private void destroyWlan(){
        if(multicastSocket != null){
            multicastSocket.close();
        }
    }

    public void close(){
        receiverHaler.setCallback(null);
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
