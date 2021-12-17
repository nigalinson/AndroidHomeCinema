package com.sloth.functions.anim.simple.repeat;

import android.animation.Animator;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/6/4 18:10
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/6/4         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class RepeatTime implements Animator.AnimatorListener {

    public static void repeat(Animator animator, int times){
        animator.addListener(new RepeatTime(animator, times));
    }

    public RepeatTime(Animator animator, int repeatTimes) {
        this.animator = animator;
        this.repeatTimes = repeatTimes;
    }

    private Animator animator;

    private int repeatTimes = -1;

    private int playTimes = -1;

    @Override
    public void onAnimationStart(Animator animation) {
        playTimes++;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(repeatTimes == -1){
            //无限循环
            if(animator != null){
                animator.start();
            }
        }else if(playTimes < repeatTimes){
            if(animator != null){
                animator.start();
            }
        }else{
            if(animator != null){
                animator.removeListener(this);
                animator = null;
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if(animator != null){
            animator.removeListener(this);
            animator = null;
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) { }
}
