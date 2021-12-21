package com.sloth.crawler;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.icrawler.CrawlerConstants;
import com.sloth.icrawler.CrawlerManager;
import com.sloth.icrawler.Strategy;
import com.sloth.tools.util.SPUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class CrawlerManagerImpl implements CrawlerManager {

    private final AtomicInteger concurrency = new AtomicInteger();

    private final List<BaseCrawler> crawlers = new ArrayList<>();

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
        BaseCrawler filmCrawler = new FilmCrawler(id, name, concurrency.get()).listener(crawlerListener);
        crawlers.add(filmCrawler.run());
    }

    @Override
    public void clear() {
        if(crawlers.isEmpty()){
            return;
        }
        Iterator<BaseCrawler> crawlerIterator = crawlers.iterator();
        while(crawlerIterator.hasNext()){
            BaseCrawler crawler = crawlerIterator.next();
            crawler.listener(null);
            crawler.stop();
            crawlerIterator.remove();
        }
    }
}
