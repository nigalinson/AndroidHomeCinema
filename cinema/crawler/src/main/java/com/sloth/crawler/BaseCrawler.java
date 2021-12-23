package com.sloth.crawler;

import com.sloth.icrawler.CrawlerManager;

import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import okhttp3.OkHttpClient;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/20 13:47
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class BaseCrawler extends RamCrawler {

    protected final int DEPTH = 10;

    private CrawlerManager.CrawlerListener crawlerListener;

    protected final long id;

    protected final String name;

    public BaseCrawler(long id, String name, int concurrency) {
        super(false);
        this.id = id;
        this.name = name;
//        getConf().setDefaultCookie(cookie);
        getConf().setExecuteInterval(10000);
        getConf().set("title_prefix","PREFIX_");
        getConf().set("content_length_limit", 200);
        setRequester(new OkHttpRequester() {
            @Override
            public OkHttpClient.Builder createOkHttpClientBuilder() {
                OkHttpClient.Builder builder = super.createOkHttpClientBuilder();
                return builder.followRedirects(true).followSslRedirects(true);
            }
        });

        setThreads(concurrency);
        getConf().setTopN(100);
    }

    public BaseCrawler listener(CrawlerManager.CrawlerListener crawlerListener) {
        this.crawlerListener = crawlerListener;
        return this;
    }

    public BaseCrawler run(){
        try {
            start(DEPTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    protected void notifyCrawlerResult(String url){
        if(crawlerListener != null){
            crawlerListener.onCrawlerResult(
                    id,
                    name,
                    url
            );
        }
    }

}
