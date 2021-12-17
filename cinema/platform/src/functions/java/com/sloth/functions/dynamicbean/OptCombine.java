package com.sloth.functions.dynamicbean;

import com.sloth.tools.util.ReflectUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/9/29 16:01
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/9/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class OptCombine implements Opt<Object[]> {
    private final Class<?>[] opts;

    public OptCombine(Class<?>[] opts) {
        this.opts = opts;
    }

    public Opt<?> newOptAt(int index){
        return (Opt<?>) ReflectUtils.reflect(opts[index]).get();
    }

}
