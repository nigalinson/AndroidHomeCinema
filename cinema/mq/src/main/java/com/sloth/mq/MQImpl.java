package com.sloth.mq;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.imq.IMQ;
import com.sloth.imq.MqMsg;
import com.sloth.imq.Strategy;
import com.sloth.tools.util.GsonUtils;
import com.sloth.tools.util.LogUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/17 14:23
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = IMQ.class, key = Strategy.DEF, singleton = true, defaultImpl = true)
public class MQImpl implements IMQ {
    private static final String TAG = "MQImpl";

    private final List<Listener> listeners = new ArrayList<>();

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void addListener(Listener listener) {
        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void send(int code, String json) {

    }

    public void notifyMessage(String msg){
        if(listeners.isEmpty()){
            return;
        }
        MqMsg mqMsg = GsonUtils.fromJson(msg, MqMsg.class);

        if(mqMsg == null){
            LogUtils.e(TAG, "mq msg is null !");
            return;
        }

        for(Listener listener: listeners){
            listener.onMqReceived(mqMsg);
        }
    }

}
