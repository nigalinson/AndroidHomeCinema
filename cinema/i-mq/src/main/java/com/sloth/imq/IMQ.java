package com.sloth.imq;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/17 14:14
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface IMQ {

    void init();

    void stop();

    void addListener(Listener listener);

    void removeListener(Listener listener);

    void send(@MqCode int code, String json);

    interface Listener {
        void onMqReceived(MqMsg msg);
    }
}
