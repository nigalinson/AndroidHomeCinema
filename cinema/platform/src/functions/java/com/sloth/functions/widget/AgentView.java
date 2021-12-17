package com.sloth.functions.widget;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;

/**
 * 代理动画类
 * @Author carl
 * @Date 2021/11/19 17:38
 */
public interface AgentView {

    void play(Rect rect, Animator.AnimatorListener animatorListener, Object... args);

    View view();

}
