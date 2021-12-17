package com.sloth.banner.data;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/18 19:55
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Playable {

    enum MediaType {
        Image(1),
        Video(2),
        Web(3),
        Others(99)
        ;

        public int type;

        MediaType(int type) {
            this.type = type;
        }
    }

    int mediaType();

    String localPath();

    String webUrl();

    int resId();

    long playDuration();
}
