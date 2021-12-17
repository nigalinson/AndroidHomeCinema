package com.sloth.banner.transform.drawer;

import android.graphics.Canvas;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/1/26 16:17
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/1/26         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Drawer {

    void advance(Canvas canvas, float fraction);

}
