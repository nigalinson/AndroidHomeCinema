package com.sloth.udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Looper;

import com.sloth.tools.util.NetworkUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/29 17:59
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class UDPManager implements NetworkUtils.OnNetworkStatusChangedListener {

    public static final int PORT = 8092;
    public static final String HOST = "239.0.0.3";

    private UDPReceiver UDPReceiver;

    private UDPSender UDPSender;

    private WifiManager.MulticastLock lock;

    public UDPManager(Context context, Looper looper){
        NetworkUtils.registerNetworkStatusChangedListener(this);
        UDPReceiver = new UDPReceiver(looper);
        UDPSender = new UDPSender(looper);
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        lock = wifiMgr.createMulticastLock(UDPManager.class.getSimpleName());
        lock.setReferenceCounted(false);
        lock.acquire();
    }

    public void listen(UDPReceiverCallback tmp){
        if(UDPReceiver != null){
            UDPReceiver.receive(tmp);
        }
        if(UDPSender != null){
            UDPSender.loop();
        }
    }

    public void send(String data){
        if(UDPSender != null){
            UDPSender.send(data);
        }
    }

    public void destroy(){
        NetworkUtils.unregisterNetworkStatusChangedListener(this);

        if(UDPReceiver != null){
            UDPReceiver.close();
            UDPReceiver = null;
        }
        if(UDPSender != null){
            UDPSender.close();
            UDPSender = null;
        }
        if(lock != null){
            lock.release();
        }
    }

    @Override
    public void onDisconnected() {
        if(UDPSender != null){
            UDPSender.onNetStateChanged(false);
        }
        if(UDPReceiver != null){
            UDPReceiver.onNetStateChanged(false);
        }
    }

    @Override
    public void onConnected(NetworkUtils.NetworkType networkType) {
        if(UDPSender != null){
            UDPSender.onNetStateChanged(true);
        }
        if(UDPReceiver != null){
            UDPReceiver.onNetStateChanged(true);
        }
    }
}
