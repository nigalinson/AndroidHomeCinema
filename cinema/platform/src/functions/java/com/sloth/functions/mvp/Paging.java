package com.sloth.functions.mvp;

import com.sloth.functions.http.options.APIInfos;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/11/24 10:34
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/11/24         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class Paging {

    private int firstPageIndex;
    private int nowPageIndex;
    private int pageSize;

    public Paging() {
        this(APIInfos.DEFAULT_CURRENT_PAGE, APIInfos.DEFAULT_CURRENT_PAGE, APIInfos.DEFAULT_PAGE_SIZE);
    }

    public Paging(int pageSize) {
        this(APIInfos.DEFAULT_CURRENT_PAGE, APIInfos.DEFAULT_CURRENT_PAGE, pageSize);
    }

    public Paging(int firstPageIndex, int pageSize) {
        this(firstPageIndex, firstPageIndex, pageSize);
    }

    public Paging(int firstPageIndex, int nowPageIndex, int pageSize) {
        this.firstPageIndex = firstPageIndex;
        this.nowPageIndex = nowPageIndex;
        this.pageSize = pageSize;
    }

    public int getFirstPageIndex() {
        return firstPageIndex;
    }

    public void setFirstPageIndex(int firstPageIndex) {
        this.firstPageIndex = firstPageIndex;
    }

    public int getNowPageIndex() {
        return nowPageIndex;
    }

    public void setNowPageIndex(int nowPageIndex) {
        this.nowPageIndex = nowPageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int head(){
        nowPageIndex = firstPageIndex;
        return nowPageIndex;
    }

    public int next(){
        return ++nowPageIndex;
    }

}
