package com.sloth.crawler.utils;

import com.sloth.tools.util.StringUtils;
import com.xunlei.downloadlib.XLLoader;
import com.xunlei.downloadlib.parameter.ThunderUrlInfo;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

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

    public static String parserDownloadUrl(String url) {
        if(StringUtils.isEmpty(url)){
            return null;
        }

        if(url.startsWith("http")){
            if(isBigFileUrl(url)){
                return url;
            }
            return null;
        }else if(url.startsWith("thunder")){
            return parserThunderUrl(url);
        }else{
            return url;
        }
    }

    private static boolean isBigFileUrl(String url) {

        String body = url.replace("http://", "").replace("https://", "");
        String[] parts = body.split("/");
        if(parts.length < 2){
            return false;
        }
        if(StringUtils.isEmpty(parts[1])){
            return false;
        }

        if(contentBody(url) < 20 * 1024 * 1024){
            return false;
        }

        return true;
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

    private static long contentBody(String link){
        URLConnection connection = null;
        try {
            URL url = new URL(link);
            connection = url.openConnection();
            return connection.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }finally {
            if(connection != null){
                try {
                    connection.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
