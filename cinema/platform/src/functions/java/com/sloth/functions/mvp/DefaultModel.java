package com.sloth.functions.mvp;

import android.content.Context;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/11/24 13:57
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/11/24         Carl            1.0                    1.0
 * Why & What is modified:
 * 用来直接处理生命周期任务的工具Model
 * 本身可能不具备数据处理能力，只是借用observable的生命周期管理能力
 */
public class DefaultModel extends RYBaseModel {

    public DefaultModel() { }

    public DefaultModel(Context context) {
        super(context);
    }
}
