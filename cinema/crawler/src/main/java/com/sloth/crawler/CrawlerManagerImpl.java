package com.sloth.crawler;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.icrawler.CrawlerConstants;
import com.sloth.icrawler.CrawlerManager;
import com.sloth.icrawler.Strategy;
import com.sloth.tools.util.ExecutorUtils;
import com.sloth.tools.util.SPUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/20 10:40
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = CrawlerManager.class, key = Strategy._DEFAULT, singleton = true, defaultImpl = true)
public class CrawlerManagerImpl implements CrawlerManager, CrawlerManager.CrawlerListener {

    private final AtomicInteger concurrency = new AtomicInteger();

    private final Map<Long, List<BaseCrawler>> crawlerMap = new ConcurrentHashMap<>();

    private final Map<Long, CrawlerListener> listenerMap = new ConcurrentHashMap<>();

    public CrawlerManagerImpl() {
        int crawlerConcurrency = SPUtils.getInstance().getInt(CrawlerConstants.SP.KEY_FILM_CRAWLER_CONCURRENCY, CrawlerConstants.DEF_FILM_CRAWLER_CONCURRENCY);
        concurrency.set(crawlerConcurrency);
    }

    @Override
    public void setPolicy(int concur){
        this.concurrency.set(concur);
        SPUtils.getInstance().put(CrawlerConstants.SP.KEY_FILM_CRAWLER_CONCURRENCY, concur);
    }

    @Override
    public void crawler(long id, String name, CrawlerListener crawlerListener) {
        stop(id);
        listenerMap.put(id, crawlerListener);
        List<BaseCrawler> list = new ArrayList<>();
        crawlerMap.put(id, list);

        BaseCrawler filmCrawler = new MovieParadiseCrawler(id, name, concurrency.get()).listener(this);
        list.add(filmCrawler);
        ExecutorUtils.getNormal().execute(new ExecutorUtils.WorkRunnable() {
            @Override
            public void run() {
                filmCrawler.run();
            }
        });
    }

    @Override
    public void onCrawlerResult(long id, String name, String url) {
        List<BaseCrawler> crawlers = crawlerMap.get(id);
        if(crawlers == null){
            return;
        }

        CrawlerListener listener = listenerMap.get(id);
        if(listener != null){
            listener.onCrawlerResult(id, name, url);
        }
    }

    @Override
    public void clear() {
        if(crawlerMap.isEmpty()){
            return;
        }
        for(Long id: crawlerMap.keySet()){
            stop(id);
        }

        crawlerMap.clear();
    }

    private void stop(Long id){
        if(id == null){
            return;
        }
        List<BaseCrawler> crawlers = crawlerMap.get(id);

        if(crawlers == null){
            return;
        }

        for(BaseCrawler crawler: crawlers){
            crawler.listener(null);
            crawler.stop();
        }
        crawlers.clear();
    }

}
