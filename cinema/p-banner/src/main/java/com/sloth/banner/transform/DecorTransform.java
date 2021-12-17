package com.sloth.banner.transform;

import android.content.Context;
import android.widget.FrameLayout;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/19 15:27
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/19         Carl            1.0                    1.0
 * Why & What is modified:
 * 需要依赖代理类来完成动效绘制的transform
 */
public interface DecorTransform {
    FrameLayout createDecor(Context context);
}
