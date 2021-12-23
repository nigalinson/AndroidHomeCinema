package com.sloth.crawler;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.icrawler.InfoFinder;
import com.sloth.icrawler.Strategy;
import com.sloth.tools.util.ExecutorUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/23 16:54
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/23         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = InfoFinder.class, key = Strategy._DEFAULT_INFO_FINDER, singleton = true, defaultImpl = true)
public class InfoFinderImpl implements InfoFinder {

    @Override
    public void find(Long id, String name, Listener listener) {
        ExecutorUtils.getIdle().execute(new ExecutorUtils.WorkRunnable() {
            @Override
            public void run() {
                new InfoCrawler(id, name).listener((id1, infoMap) ->
                        runOnUiThread(() -> listener.findInfo(id1, infoMap))).run();
            }
        });
    }

}
