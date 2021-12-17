package com.sloth.banner.vh;

import android.view.View;

import com.sloth.functions.adapter.BaseViewHolder;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/9 17:06
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/9         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class PagerViewHolder<T> extends BaseViewHolder<T> {

    /**
     * 未加载状态
     */
    public static final int VH_PAGER_STATE_RECYCLED = 0;
    /**
     * 准备状态
     */
    public static final int VH_PAGER_STATE_PREPARED = 1;
    /**
     * 加载状态
     */
    public static final int VH_PAGER_STATE_LOADED = 2;

    private int state = VH_PAGER_STATE_RECYCLED;

    public PagerViewHolder(View itemView) {
        super(itemView);
    }

    public void preLoad(T data){
        if(state == VH_PAGER_STATE_PREPARED){
            //已经是准备中，不触发
            return;
        }
        state = VH_PAGER_STATE_PREPARED;
        onPreLoad(data);
    }

    public abstract void onPreLoad(T data);

    public void loaded(T data){
        if(state == VH_PAGER_STATE_LOADED){
            //已经是准备中，不触发
            return;
        }

        state = VH_PAGER_STATE_LOADED;
        onLoaded(data);
    }

    public abstract void onLoaded(T data);

    public void close(T data){
        if(state == VH_PAGER_STATE_RECYCLED){
            //已经是销毁，不触发
            return;
        }
        state = VH_PAGER_STATE_RECYCLED;
        onClose(data);
    }

    public abstract void onClose(T data);

}
