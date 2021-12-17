package com.sloth.banner.adapter;

import com.sloth.functions.viewpager2.widget.ViewPager2;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/16 13:59
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class PageChangeCallbackFix extends ViewPager2.OnPageChangeCallback{
    private int lastPage = -1;

    @Override
    public void onPageSelected(int position) {
        if(lastPage != position){
            lastPage = position;
            onPageSelectedFix(position);
        }
    }

    protected abstract void onPageSelectedFix(int position);

}
