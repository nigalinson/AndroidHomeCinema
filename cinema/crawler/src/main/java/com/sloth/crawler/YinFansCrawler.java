package com.sloth.crawler;

import com.sloth.crawler.utils.CrawlerUtils;
import com.sloth.tools.util.EncodeUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class YinFansCrawler extends BaseCrawler {
    private static final String TAG = "YinFansCrawler";

    private static final String URL = "https://www.yinfans.net";

    public YinFansCrawler(long id, String name, int concurrency) {
        super(id, name, concurrency);

        String formName = formedName(name);
        if(StringUtils.notEmpty(formName)){
            String url = URL + "/?s=" + formName;
            LogUtils.d(TAG, "add seeds: " + url);
            addSeedAndReturn(url).type("list");
        }
    }

    private String formedName(String name) {
        return EncodeUtils.urlEncode(name).toUpperCase();
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        if(page.matchType("list")){
            LogUtils.d(TAG, "list page: " + page.url());
            Elements linkElements = page.select("#post_container").select("li").select("a");
            for(Element element: linkElements){
                String link = element.attr("href");
                String content = element.text();
                LogUtils.d(TAG, "find movie: " + content + "\n" + link);
                next.addAndReturn(link).type("detail");
            }
        }else if(page.matchType("detail")){
            LogUtils.d(TAG, "detail page: " + page.url());
            Elements downloadLinkElements = page.select("#cili").select("a");
            for(Element element: downloadLinkElements){
                String link = element.attr("href");
                link = CrawlerUtils.parserDownloadUrl(link);
                LogUtils.d(TAG, "find download url: " + link);
                if(link != null){
                    notifyCrawlerResult(element.text(), link);
                }
            }
        }
    }

}
