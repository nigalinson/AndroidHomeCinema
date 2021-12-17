package com.sloth.functions.dynamicbean;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/9/29 10:59
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/9/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Opt<T> {
    void execute(T arg);
}
