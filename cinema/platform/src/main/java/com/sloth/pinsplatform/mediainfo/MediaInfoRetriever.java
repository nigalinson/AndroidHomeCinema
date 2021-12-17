package com.sloth.pinsplatform.mediainfo;

import android.graphics.Bitmap;
import android.util.Pair;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 16:04
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface MediaInfoRetriever {

    MediaInfo mediaInfo(String path);

    Pair<Integer, Integer> videoSize(String path);

    int rotation(String path);

    long duration(String path);

    Bitmap cover(String path);

    Bitmap cover(String path, long position);

}
