package com.sloth.functions.anim.simple.effects.into;

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
public class BigIn implements Effect {

    @Override
    public Animator create(View target, long dur) {
        target.post(() -> {
            int width = target.getWidth();
            int height = target.getHeight();
            target.setPivotX(width / 2f);
            target.setPivotY(height / 2f);
        });

        ValueAnimator scaleXPart = ValueAnimator.ofFloat(
                2f, 1f
        );
        scaleXPart.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXPart.addUpdateListener(animation -> target.setScaleX((Float) animation.getAnimatedValue()));

        ValueAnimator scaleYPart = ValueAnimator.ofFloat(
                2f, 1f
        );
        scaleYPart.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYPart.addUpdateListener(animation -> target.setScaleY((Float) animation.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.setDuration(dur);
        set.playTogether(scaleXPart, scaleYPart);

        return set;
    }
}
