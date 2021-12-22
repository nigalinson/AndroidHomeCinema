package com.sloth.crawler.utils;

import com.sloth.tools.util.StringUtils;
import com.xunlei.downloadlib.XLLoader;
import com.xunlei.downloadlib.parameter.ThunderUrlInfo;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/21 19:35
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/21         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class CrawlerUtils {

    public static String parserUrl(String url) {
        if(StringUtils.isEmpty(url)){
            return null;
        }

        if(url.startsWith("thunder")){
            return parserThunderUrl(url);
        }else{
            return url;
        }
    }

    public static String parserThunderUrl(String str) {
        int i = 9900;
        ThunderUrlInfo thunderUrlInfo = new ThunderUrlInfo();
        if (str != null) {
            i = XLLoader.Holder.INSTANCE.parserThunderUrl(str, thunderUrlInfo);
        }
        if (9000 == i) {
            return thunderUrlInfo.mUrl;
        }
        return null;
    }

}
