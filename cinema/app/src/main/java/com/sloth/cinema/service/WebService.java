package com.sloth.cinema.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.sankuai.waimai.router.Router;
import com.sloth.arp.ARP;
import com.sloth.cinema.IWebServiceCallbackInterface;
import com.sloth.cinema.IWebServiceInterface;
import com.sloth.cinema.R;
import com.sloth.imq.IMQ;
import com.sloth.imq.MqCode;
import com.sloth.imq.MqMsg;
import com.sloth.tools.util.GsonUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.NetworkUtils;
import com.sloth.tools.util.NotificationUtils;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/16 12:04
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class WebService extends Service implements IMQ.Listener {

    private static final String TAG = "WebService";

    private static final int NOTIFICATION_ID = 1;

    private PowerManager.WakeLock wakeLock;
    private Server server;

    private ARP arp;
    private IMQ mq;

    private final List<IWebServiceCallbackInterface> callbackInterfaceList = new ArrayList<>();

    private final WebServiceBinder iBinder = new WebServiceBinder();

    private final class WebServiceBinder extends IWebServiceInterface.Stub {

        @Override
        public void addCallback(IWebServiceCallbackInterface callback) throws RemoteException {
            if(!callbackInterfaceList.contains(callback)){
                callbackInterfaceList.add(callback);
            }
        }

        @Override
        public void removeCallback(IWebServiceCallbackInterface callback) throws RemoteException {
            callbackInterfaceList.remove(callback);
        }

        @Override
        public void start() throws RemoteException {
            LogUtils.d(TAG, "web service is running !");
            openServer();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "web service onStart !");
        setNotification();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "web service is destroyed !");
//        releaseLock();
        stopServer();
    }

    @Override
    public void onMqReceived(MqMsg msg) {
        LogUtils.d(TAG, "MQ received: " + GsonUtils.toJson(msg));
        if(msg.getMqCode() == MqCode.ADD){

        }
    }

    private void openServer() {
        if(server != null){
            LogUtils.d(TAG, "already running !");
            return;
        }

        int port = 8888;
        server = AndServer.webServer(this)
                .port(port)
                .timeout(10, TimeUnit.SECONDS)
                .build();
        server.startup();

        String msg = "server is running at : " + NetworkUtils.getIpAddressByWifi() + ", on port : " + port;
        LogUtils.d(TAG, msg);
        notifyCallbacks(msg);
//        acquireLock();

        arp = new ARP();
        arp.startARP();

        mq = Router.getService(IMQ.class);
        mq.init();
        mq.addListener(this);
    }

    private void stopServer(){
        if(mq != null){
            mq.removeListener(this);
            mq.stop();
            mq = null;
        }

        if(arp != null){
            arp.stopARP();
            arp = null;
        }

        if(server != null){
            server.shutdown();
            server = null;
        }

        String shutDownMsg = "server shutdown !";

        LogUtils.d(TAG, shutDownMsg);
        notifyCallbacks(shutDownMsg);
    }

    private void acquireLock(){
        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WebService:MyWakeLock");
        wakeLock.acquire();
    }

    private void releaseLock(){
        if(wakeLock != null){
            wakeLock.release();
            wakeLock = null;
        }
    }

    public void setNotification() {
        startForeground(NOTIFICATION_ID, NotificationUtils.getNotification(NotificationUtils.ChannelConfig.DEFAULT_CHANNEL_CONFIG, builder -> {
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText(getString(R.string.cinema_is_running));
        }));
    }

    private void notifyCallbacks(String msg){
        for(IWebServiceCallbackInterface callbackInterface: callbackInterfaceList){
            try {
                callbackInterface.onServiceMessage(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
