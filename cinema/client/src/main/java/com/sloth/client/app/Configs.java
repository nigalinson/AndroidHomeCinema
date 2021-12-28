package com.sloth.client.app;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.log.LoggerConfig;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.log.Log;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 14:15
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class Configs {

    public static final String LOG_ENGINE = Strategies.LogEngine.LOGGER;

    @RouterService(interfaces = LoggerConfig.class, singleton = true, defaultImpl = true)
    public static class AppLoggerConfig implements LoggerConfig {
        @Override
        public int level() {
            return Log.V;
        }
    }

}
