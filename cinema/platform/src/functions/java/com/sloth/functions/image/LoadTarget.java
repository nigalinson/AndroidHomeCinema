package com.sloth.functions.image;

import android.graphics.drawable.Drawable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/27 11:36
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/27         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface LoadTarget<T> {

    void prepared(Drawable placeHolder);

    void loadSuccess(T drawable);

    void loadFailed(Drawable error);

}
