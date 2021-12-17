package com.sloth.banner.vh;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/15 14:39
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/15         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface VideoStatusListener {

    void onPrepared(int pos);

    void onEnd(int pos);

    void onError(int pos);

}
