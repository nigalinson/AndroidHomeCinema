package com.sloth.arp;

import androidx.annotation.NonNull;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.NetworkUtils;
import com.sloth.tools.util.StringUtils;
import com.sloth.tools.util.Utils;
import com.sloth.udp.UDPManager;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
public class ARP {
    private static final String TAG = "ARP";

    //ARP broadcast interval
    private static final long ARP_INTERVAL = 5000;

    private Disposable loop;

    private UDPManager udpManager;

    public void stopARP(){
        if(loop != null && !loop.isDisposed()){
            loop.dispose();
            loop = null;
        }
    }

    public void startARP(){
        stopARP();
        Observable.interval(0, ARP_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        loop = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        getUDP().send(makeARP(NetworkUtils.getIpAddressByWifi()));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e(TAG, e.getMessage() != null ? e.getMessage() : "ARP send failed !");
                    }

                    @Override
                    public void onComplete() { }
                });

    }

    private UDPManager getUDP(){
        if(udpManager == null){
            udpManager = new UDPManager(Utils.getApp());
        }
        return udpManager;
    }

    public static String makeARP(String ip){
        return "ARP-" + ip;
    }

    public static String parseARP(String arpPack){
        if(StringUtils.isEmpty(arpPack) || !arpPack.contains("-")){
            return null;
        }
        String[] split = arpPack.split("-");
        return split.length > 1 ? split[1] : null;
    }

}
