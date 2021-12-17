package com.sloth.functions.anim.simple.effects.into;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sloth.functions.anim.simple.Effect;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/6/4 17:58
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/6/4         Carl            1.0                    1.0
 * Why & What is modified:
 * todo 倾斜形变 需要使用 matrix  polygon，比较复杂， 和这里一般使用的原生动画不匹配
 */
public class RunIn implements Effect {

    @Override
    public Animator create(View target, long dur) {
        ValueAnimator animator = ValueAnimator.ofFloat(
                1080f, 0f
        );
        animator.setDuration(dur * 2);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> target.setTranslationX((Float) animation.getAnimatedValue()));

        return animator;
    }
}
