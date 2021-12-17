package com.sloth.functions;

import android.view.View;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/27 15:29
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/27         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class DisallowFastClickListener implements View.OnClickListener {
    private long lastClickTime = 0;

    @Override
    public void onClick(View view) {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return;
        }
        lastClickTime = time;
        validClick(view);
    }

    protected abstract void validClick(View view);
}
