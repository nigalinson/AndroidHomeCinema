package com.sloth.crawler.utils;

import com.xunlei.downloadlib.XLDownloadManager;

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
public class LinkUtils {

    public static String transformThunder(String thunder){
        return XLDownloadManager.getInstance().parserThunderUrl(thunder);
    }
}
