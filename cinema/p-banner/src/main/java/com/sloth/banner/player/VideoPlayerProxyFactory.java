package com.sloth.banner.player;

import android.content.Context;

import com.sloth.player.PlayerConst;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/10 13:43
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/10         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class VideoPlayerProxyFactory {

    public static PlayerProxy generate(Context context, PlayerConfig playerConfig){
        if(playerConfig != null && playerConfig.getPlayerType() == PlayerConst.PlayerType.NATIVE.type){
            return new NativeVideoProxy(context, playerConfig);
        }else if(playerConfig != null && playerConfig.getPlayerType() == PlayerConst.PlayerType.EXO.type){
            return new ExoProxy(context, playerConfig);
        }
        return new ExoProxy(context, playerConfig);
    }
}
