package com.sloth.crawler;

import com.sloth.icrawler.InfoFinder;
import com.sloth.tools.util.EncodeUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.StringUtils;
import org.jsoup.nodes.Element;
import java.util.HashMap;
import java.util.Map;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;
import okhttp3.OkHttpClient;

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
public class InfoCrawler extends RamCrawler {
    private static final String TAG = "InfoCrawler";

    private static final String URL = "https://search.douban.com";

    protected final int DEPTH = 3;

    private InfoFinder.Listener infoFinderListener;

    private final long id;

    private final String name;

    private final Map<String, String> infos = new HashMap<>();

    public InfoCrawler(long id, String name) {
        super(false);
        this.id = id;
        this.name = name;
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

        setThreads(1);
        getConf().setTopN(100);

        String formName = formedName(name);
        if(StringUtils.notEmpty(formName)){
            String url = URL + "/movie/subject_search?search_text=" + formName;
            LogUtils.d(TAG, "add seeds: " + url);
            addSeedAndReturn(url).type("list");
        }
    }

    public InfoCrawler listener(InfoFinder.Listener listener) {
        this.infoFinderListener = listener;
        return this;
    }

    public InfoCrawler run(){
        try {
            start(DEPTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    private void notifyCrawlerResult(){
        if(infoFinderListener != null){
            infoFinderListener.findInfo(id, infos);
        }
    }

    private String formedName(String name) {
        return EncodeUtils.urlEncode(name).toUpperCase();
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        if(page.matchType("list")){
            LogUtils.d(TAG, "list page: " + page.url());
            Element linkElement = page.select(".sc-bZQynM .kLDhtO .sc-bxivhb .jDZFxE")
                    .select("detail").select("title").select("a").first();
            String link = linkElement.attr("href");
            String name = linkElement.text();
            infos.put("name", name);
            String content = linkElement.text();
            LogUtils.d(TAG, "find movie: " + content + "\n" + link);
            next.addAndReturn(link).type("detail");
        }else if(page.matchType("detail")){
            LogUtils.d(TAG, "detail page: " + page.url());
            Element imgElement = page.select(".article").select("img").first();
            String img = imgElement.attr("src");
            infos.put("img", img);

            Element introElement = page.select(".related-info").select("span").first();
            String intro = introElement.text();
            infos.put("intro", intro);
            notifyCrawlerResult();
        }
    }

}
