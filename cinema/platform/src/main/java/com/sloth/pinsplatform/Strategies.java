package com.sloth.pinsplatform;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 14:41
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class Strategies {

    public static final class LogEngine {
        public static final String LOGGER = "LOGGER";
        public static final String XLOG = "XLOG";
    }

    public static final class MediaInfoRetrieverEngine {
        public static final String _DEFAULT = "AndroidDefault";
        public static final String FFMPEG = "FFMPEG";
    }

    public static final class DownloadEngine {
        public static final String URL_CONNECTION = "urlConnection";
        public static final String LIU_LI_SHO = "liuLiShuo";
    }

}
