package com.sloth.functions.mvp;

import android.content.Context;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/30 15:36
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/30         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class RYBasePageCase<V extends RYBasePageView> extends RYBaseCase<V> {

    protected final Paging paging = new Paging();

    public RYBasePageCase() { }

    public RYBasePageCase(V v) {
        super(v);
    }

    public RYBasePageCase(Context context, V v) {
        super(context, v);
    }

    public void refresh(){
        paging.head();
        loadData(true, paging.getNowPageIndex(), paging.getPageSize());
    }

    public void loadMore(){
        paging.next();
        loadData(false, paging.getNowPageIndex(), paging.getPageSize());
    }

    protected abstract void loadData(boolean refresh, int pageIndex, int pageSize);

}
