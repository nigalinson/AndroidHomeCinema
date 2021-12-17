package com.sloth.functions.anim.simple.effects.outo;

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
 * 中心旋转（逆时针）
 */
public class RollOut implements Effect {

    private boolean clockwise = true;

    public RollOut() {
    }

    public RollOut(boolean clockwise) {
        this.clockwise = clockwise;
    }

    @Override
    public Animator create(View target, long dur) {
        target.post(() -> {
            int width = target.getWidth();
            int height = target.getHeight();
            target.setPivotX(0);
            target.setPivotY(height + 20);
        });
        ValueAnimator rotatePart = ValueAnimator.ofFloat(
                0f, (clockwise ? 120f : -120f)
        );
        rotatePart.setInterpolator(new AccelerateDecelerateInterpolator());
        rotatePart.addUpdateListener(animation -> target.setRotation((Float) animation.getAnimatedValue()));

        ValueAnimator fadePart = ValueAnimator.ofFloat(
                1.0f, 0f
                );
        fadePart.setInterpolator(new AccelerateDecelerateInterpolator());
        fadePart.addUpdateListener(animation -> target.setAlpha((Float) animation.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.setDuration(dur);
        set.playTogether(rotatePart, fadePart);

        return set;
    }
}
