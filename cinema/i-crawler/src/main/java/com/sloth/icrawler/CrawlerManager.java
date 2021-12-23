package com.sloth.icrawler;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/20 10:36
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface CrawlerManager {

    void crawler(long id, String name, CrawlerListener crawlerListener);

    void clear();

    interface CrawlerListener{
        void onCrawlerResult(long id, String name, String url);
    }
}
