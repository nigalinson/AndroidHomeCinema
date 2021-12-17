package com.sloth.functions.anim.simple;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import com.sloth.functions.anim.simple.repeat.RepeatTime;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/6/2 10:50
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/6/2         Carl            1.0                    1.0
 * Why & What is modified:
 * 提示动画
 */
public class RemindAnimate {

    public static Builder build(View view){
        return new Builder(view);
    }

    private static Animator apply(Builder builder){
        Animator animator = builder.effect.create(builder.target, builder.duration);
        if(animator instanceof ValueAnimator){
            ((ValueAnimator)animator).setRepeatCount(builder.repeat);
        }else{
            RepeatTime.repeat(animator, builder.repeat);
        }
        animator.setStartDelay(100);
        if(builder.listener != null){
            animator.addListener(builder.listener);
        }
        if(builder.delay == 0){
            animator.start();
        }else{
            builder.target.postDelayed(()->{
                if(builder.target == null){ return; }
                animator.start();
            }, builder.delay);
        }
        return animator;
    }

    private RemindAnimate() { }

    public static final class Builder {
        private final View target;
        private long delay = 0;
        private long duration = 400;
        //循环次数
        private int repeat = -1;
        private Effect effect;
        private Animator.AnimatorListener listener;

        public Builder(View target) {
            this.target = target;
        }

        public Builder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder repeat(int repeat) {
            this.repeat = repeat;
            return this;
        }

        public Builder effect(Effect effect) {
            this.effect = effect;
            return this;
        }

        public Builder listener(Animator.AnimatorListener lis){
            this.listener = lis;
            return this;
        }

        public Animator apply(){
            return RemindAnimate.apply(this);
        }
    }



}

