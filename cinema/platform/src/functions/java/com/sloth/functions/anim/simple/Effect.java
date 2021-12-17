package com.sloth.functions.anim.simple;

import android.animation.Animator;
import android.view.View;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/6/4 18:02
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/6/4         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Effect {

    Animator create(View target, long dur);

}
