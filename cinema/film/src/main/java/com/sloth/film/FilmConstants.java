package com.sloth.film;

import com.sloth.ifilm.FilmCachePolicy;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/17 11:29
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class FilmConstants {

    public static final class SP{
        public static final String KEY_FILM_CACHE_POLICY = "KEY_FILM_CACHE_POLICY";
        public static final String KEY_FILM_DOWNLOAD_CONCURRENCY = "KEY_FILM_DOWNLOAD_CONCURRENCY";
    }

    public static final int DEF_FILM_CACHE_POLICY = FilmCachePolicy.ALWAYS_DOWNLOAD;
    public static final int DEF_FILM_DOWNLOAD_CONCURRENCY = 5;

}
