package com.sloth.arp;

import com.sloth.tools.util.StringUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/24 15:27
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/24         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ARPUtils {

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
