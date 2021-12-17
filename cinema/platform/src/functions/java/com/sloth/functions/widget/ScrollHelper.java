package com.sloth.functions.widget;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sloth.pinsplatform.R;

/**
 * Author:    CaoKang
 * Version    V1.0
 * Date:      2018/7/25
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/7/25      CaoKang            1.0                    1.0
 * Why & What is modified:
 */

public class ScrollHelper {

    public static void smoothScrollToPosition(RecyclerView recyclerView, int targetPosition) {
        smoothScrollToPosition(recyclerView, targetPosition, RecyclerView.UNDEFINED_DURATION);
    }

    public static void smoothScrollToPosition(RecyclerView recyclerView, int targetPosition, int duration) {
        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            recyclerView.setTag(R.integer.vp2_scroll_duration, duration);
            ((LinearLayoutManager)recyclerView.getLayoutManager()).smoothScrollToPosition(recyclerView, null, targetPosition);
        }
    }

    public static void scrollToPosition(RecyclerView recyclerView, int targetPosition) {
        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPosition(targetPosition);
        }
    }

}