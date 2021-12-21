package com.sloth.cinema.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import com.sankuai.waimai.router.Router;
import com.sloth.arp.ARP;
import com.sloth.cinema.IWebServiceCallbackInterface;
import com.sloth.cinema.IWebServiceInterface;
import com.sloth.cinema.R;
import com.sloth.cinema.push.PushConstants;
import com.sloth.ifilm.FilmManager;
import com.sloth.ifilm.Strategy;
import com.sloth.push.PushData;
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
public class WebService extends Service {

    private static final String TAG = "WebService";

    private static final int NOTIFICATION_ID = 1;

    private Server server;

    private ARP arp;

    private FilmManager filmManager;

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
        stopServer();
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

        arp = new ARP();
        arp.startARP();

        registerPush();

        filmManager = Router.getService(FilmManager.class, Strategy._DEFAULT);
        filmManager.openEngine(true);
    }

    private void stopServer(){
        if(filmManager != null){
            filmManager.openEngine(false);
            filmManager = null;
        }

        unregisterPush();

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

    private void registerPush() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PushConstants.BROADCAST_ACTION);
        registerReceiver(pushReceiver, intentFilter);
    }

    private void unregisterPush() {
        unregisterReceiver(pushReceiver);
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

    private final BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PushData pushData = intent.getParcelableExtra("data");
            LogUtils.d(TAG, "received push: " + GsonUtils.toJson(pushData));
        }
    };

}
