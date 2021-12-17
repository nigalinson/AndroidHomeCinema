package com.sloth.functions.anim.simple.effects;

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
 * 摇摆
 */
public class Swing implements Effect {

    @Override
    public Animator create(View target, long dur) {
        target.post(() -> {
            int width = target.getWidth();
            int height = target.getHeight();
            target.setPivotX(width / 2f);
            target.setPivotY(height);
        });
        ValueAnimator animator = ValueAnimator.ofFloat(
                0f, -10f, 0, 10f,
                0f, -15f, 0, 15f,
                0f, -10f, 0f, 10f, 0f
        );
        animator.setDuration(dur * 2);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> target.setRotation((Float) animation.getAnimatedValue()));
        return animator;
    }
}
