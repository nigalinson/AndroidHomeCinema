package com.sloth.pinsplatform.player;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/14 18:30
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/14         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface PlayerListener {

    void onPlayerPrepared();

    void onPlayerSizeChanged(int width, int height);

    void onPlayerEnd();

    void onPlayerError(int code, String msg);

}
