package com.sloth.functions.image;

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
public interface LoadListener<T> {

    void loadSuccess(T drawable);

    void loadFailed(String msg);

}
