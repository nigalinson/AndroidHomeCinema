package com.sloth.ifilm;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/16 18:37
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class FilmQueryParam {

    private int pageIndex;

    private int pageSize;

    private String name;

    private FilmQueryParam() { }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Builder {
        private int pageIndex = 0;

        private int pageSize = 15;

        private String name;

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public void setName(String name) {
            this.name = name;
        }

        public FilmQueryParam build(){
            FilmQueryParam param = new FilmQueryParam();
            param.setPageIndex(pageIndex);
            param.setPageSize(pageSize);
            param.setName(name);
            return param;
        }
    }

}
