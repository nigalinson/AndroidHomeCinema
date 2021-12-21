package com.sloth.crawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/20 11:45
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class FilmCrawler extends BaseCrawler {
    private static final String URL = "";

    public FilmCrawler(long id, String name, int concurrency) {
        super(URL, id, name, concurrency);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {

        notifyCrawlerResult(null);
    }
}
