package com.sloth.functions.anim.simple.effects;

import android.animation.Animator;
import android.animation.AnimatorSet;
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
 * 中心晃动
 */
public class HorizFlip implements Effect {

    @Override
    public Animator create(View target, long dur) {
        target.post(() -> {
            int width = target.getWidth();
            target.setPivotX(width / 2f);
        });
        ValueAnimator shakePart = ValueAnimator.ofFloat(
                0f, 180f, 360f
        );
        shakePart.setInterpolator(new AccelerateDecelerateInterpolator());
        shakePart.addUpdateListener(animation -> target.setRotationY((Float) animation.getAnimatedValue()));

        ValueAnimator scalePart = ValueAnimator.ofFloat(
                1f, 1.5f, 1f
        );
        scalePart.setInterpolator(new AccelerateDecelerateInterpolator());
        scalePart.addUpdateListener(animation -> {
            target.setScaleX((Float) animation.getAnimatedValue());
            target.setScaleY((Float) animation.getAnimatedValue());
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(dur);
        set.playTogether(shakePart, scalePart);

        return set;
    }
}
