package com.sloth.tools.util;

import com.sankuai.waimai.router.Router;
import com.sloth.pinsplatform.mediainfo.MediaInfoRetriever;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 11:44
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class MediaUtils {
    private MediaUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static MediaInfoRetriever getDefaultEngine(){
        return Router.getService(MediaInfoRetriever.class);
    }

    public static MediaInfoRetriever getEngine(String key){
        MediaInfoRetriever retriever = Router.getService(MediaInfoRetriever.class, key);
        return retriever != null ? retriever : getDefaultEngine();
    }

}
