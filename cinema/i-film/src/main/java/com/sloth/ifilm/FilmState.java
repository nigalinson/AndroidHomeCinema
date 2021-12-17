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
@IntDef({FilmState.DISABLE, FilmState.WAITING, FilmState.DOWNLOADING, FilmState.OK })
@Retention(RetentionPolicy.SOURCE)
public @interface FilmState {

    /**
     * will not be operated until user take it
     */
    int DISABLE = 2;

    /**
     * will be start download if prepared
     */
    int WAITING = 0;

    int DOWNLOADING = 100;

    int OK = 200;

}
