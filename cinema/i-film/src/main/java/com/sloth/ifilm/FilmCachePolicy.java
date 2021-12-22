package com.sloth.ifilm;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/16 18:58
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
@IntDef({FilmCachePolicy.ALWAYS_DOWNLOAD, FilmCachePolicy.NEVER_DOWNLOAD })
@Retention(RetentionPolicy.SOURCE)
public @interface FilmCachePolicy {

    int ALWAYS_DOWNLOAD = 0;

    int NEVER_DOWNLOAD = 2;

}
