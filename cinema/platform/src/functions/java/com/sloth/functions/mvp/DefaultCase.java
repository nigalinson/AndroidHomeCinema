package com.sloth.functions.mvp;

import android.content.Context;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/30 16:02
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/30         Carl            1.0                    1.0
 * Why & What is modified:
 * 如果需要直接从presenter访问model，不想新建case时
 * 实际presenter会默认生成一个DefaultCase实例，用于访问model
 */
public class DefaultCase extends RYBaseCase {

    public DefaultCase() { }

    public DefaultCase(RYBaseView ryBaseView) {
        super(ryBaseView);
    }

    public DefaultCase(Context context, RYBaseView ryBaseView) {
        super(context, ryBaseView);
    }

}
