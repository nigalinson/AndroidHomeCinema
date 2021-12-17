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
 * 滑轨
 */
public class FlyIn implements Effect {

    @Override
    public Animator create(View target, long dur) {
        ValueAnimator slidePart = ValueAnimator.ofFloat(
                -200f, 10f, 0f
        );
        slidePart.setInterpolator(new AccelerateDecelerateInterpolator());
        slidePart.addUpdateListener(animation -> target.setTranslationX((Float) animation.getAnimatedValue()));

        ValueAnimator scalePart = ValueAnimator.ofFloat(
                0f, 1f
        );
        scalePart.setInterpolator(new AccelerateDecelerateInterpolator());
        scalePart.addUpdateListener(animation -> {
            target.setScaleX((Float) animation.getAnimatedValue());
            target.setScaleY((Float) animation.getAnimatedValue());
        });


        AnimatorSet set = new AnimatorSet();
        set.setDuration(dur);
        set.playTogether(slidePart, scalePart);
        return set;
    }
}
