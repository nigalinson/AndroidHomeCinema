package com.sloth.udp;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/3/30 16:47
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/3/30         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface UDPReceiverCallback {
    void receive(String data);
}
