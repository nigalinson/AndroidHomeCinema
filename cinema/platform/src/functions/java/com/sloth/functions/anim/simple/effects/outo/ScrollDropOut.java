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
public class ScrollDropOut implements Effect {

    public ScrollDropOut() { }

    @Override
    public Animator create(View target, long dur) {
        target.post(() -> {
            int width = target.getWidth();
            int height = target.getHeight();
            target.setPivotX(0);
            target.setPivotY(height);
        });
        ValueAnimator rotatePart = ValueAnimator.ofFloat(
                0f, 140f, 100f, 130f, 110f, 120f
        );
        rotatePart.setInterpolator(new AccelerateDecelerateInterpolator());
        rotatePart.addUpdateListener(animation -> target.setRotation((Float) animation.getAnimatedValue()));

        ValueAnimator fallPart = ValueAnimator.ofFloat(
                0f, 1920f
        );
        fallPart.setInterpolator(new AccelerateDecelerateInterpolator());
        fallPart.addUpdateListener(animation -> target.setTranslationY((Float) animation.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.setDuration(dur);
        set.playSequentially(rotatePart, fallPart);

        return set;
    }
}
