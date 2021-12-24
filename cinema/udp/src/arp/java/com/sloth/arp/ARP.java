package com.sloth.arp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import com.sloth.tools.util.NetworkUtils;
import com.sloth.tools.util.Utils;
import com.sloth.udp.UDPManager;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/17 13:51
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ARP implements Runnable {
    private static final String TAG = "ARP";

    public static ARP start(){
        ARP arp = new ARP();
        new Thread(arp).start();
        return arp;
    }

    public static void stop(ARP arp){
        if(arp != null){
            arp.stopARP();
        }
    }

    private ARP() { }

    private ARPMainHandler arpMainHandler;

    private static class ARPMainHandler extends Handler {

        //ARP broadcast interval
        private static final long ARP_INTERVAL = 5000;

        private UDPManager udpManager;

        public ARPMainHandler(@NonNull Looper looper) {
            super(looper);
            sendInitMessage();
            sendIntervalMessage();
        }

        private static final int EVENT_INIT = 1;

        private static final int EVENT_INTERVAL = 2;

        private static final int EVENT_EXIT = 99;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == EVENT_INIT){
                udpManager = new UDPManager(Utils.getApp(), getLooper());
            }else if(msg.what == EVENT_INTERVAL){
                udpManager.send(ARPUtils.makeARP(NetworkUtils.getIpAddressByWifi()));
                sendIntervalMessage();
            }else if(msg.what == EVENT_EXIT){
                removeMessages(EVENT_INIT);
                removeMessages(EVENT_INTERVAL);
                removeMessages(EVENT_EXIT);
                if(udpManager != null){
                    udpManager.destroy();
                    udpManager = null;
                }
                getLooper().quitSafely();
            }
        }

        private void sendInitMessage(){
            Message msg = Message.obtain();
            msg.what = ARPMainHandler.EVENT_INIT;
            sendMessage(msg);
        }

        private void sendIntervalMessage(){
            Message msg = Message.obtain();
            msg.what = ARPMainHandler.EVENT_INTERVAL;
            sendMessageDelayed(msg, ARP_INTERVAL);
        }

        private void sendExitMessage(){
            Message msg = Message.obtain();
            msg.what = ARPMainHandler.EVENT_EXIT;
            sendMessage(msg);
        }

    }

    @Override
    public void run() {
        Looper.prepare();
        arpMainHandler = new ARPMainHandler(Looper.myLooper());
        Looper.loop();
    }

    public void stopARP(){
        if(arpMainHandler != null){
            arpMainHandler.sendExitMessage();
            arpMainHandler = null;
        }
    }
}
