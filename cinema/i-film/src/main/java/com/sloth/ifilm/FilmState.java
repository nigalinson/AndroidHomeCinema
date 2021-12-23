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
@IntDef({FilmState.WAIT, FilmState.OK })
@Retention(RetentionPolicy.SOURCE)
public @interface FilmState {

    /**
     * can't watch yet
     * searching for resources, try to download links
     */
    int WAIT = 0;

    /**
     * has found link and download movie success
     */
    int OK = 1;

}
