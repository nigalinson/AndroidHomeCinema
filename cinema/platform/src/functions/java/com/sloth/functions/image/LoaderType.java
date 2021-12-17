package com.sloth.functions.image;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/27 11:56
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/27         Carl            1.0                    1.0
 * Why & What is modified:
 */
public enum LoaderType {
    /**
     * glide加载器
     */
    Glide(1),
    /**
     * 大图加载器
     */
    Picasso(2),
    /**
     * 高性能多图加载器
     * todo 暂未实现
     */
    Volley(3)
    ;

    LoaderType(int val) {
        this.val = val;
    }

    public Integer val;

}
