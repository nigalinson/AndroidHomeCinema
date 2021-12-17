package com.sloth.banner.transform.agent;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sloth.functions.viewpager2.widget.ViewPager2;
import com.sloth.functions.widget.AgentView;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/14 11:53
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/14         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class AgentDecor<V extends AgentView> extends FrameLayout implements Animator.AnimatorListener {

    private ViewPager2 vp2;
    private V proxyView;

    private boolean isRunning = false;

    public AgentDecor(@NonNull Context context) {
        super(context);
        init(context, null, -1);
    }

    public AgentDecor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }

    public AgentDecor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) { }

    public void setInAgentView(V agentView){
        this.proxyView = agentView;
    }

    public void play(Object... args) {

        if(isRunning){
            //已有进行中的动画
            return;
        }

        Rect rect = new Rect();
        getLocalVisibleRect(rect);

        proxyView.play(rect,this, args);

    }

    public void clear(){ }

    @Override
    public void onAnimationStart(Animator animation) {
        addProxyView();
        disAllowParentTouch();
        isRunning = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        isRunning = false;
        removeProxyView();
        allowParentTouch();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        isRunning = false;
        removeProxyView();
        allowParentTouch();
    }

    @Override
    public void onAnimationRepeat(Animator animation) { }

    private void addProxyView(){
        vp2 = (ViewPager2) getParent().getParent();
        FrameLayout home = (FrameLayout) vp2.getParent();
        if(home.indexOfChild(proxyView.view()) == -1){
            //未添加过
            int vpIndex = home.indexOfChild(vp2);
            home.addView(proxyView.view(), vpIndex + 1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }

    private void removeProxyView(){
        ViewGroup home = (ViewGroup) vp2.getParent();

        if(home.indexOfChild(proxyView.view()) != -1){
            home.removeView(proxyView.view());
        }
    }

    private void disAllowParentTouch() {
        ViewGroup home = (ViewGroup) vp2.getParent();
        home.setOnTouchListener(INTERCEPT_TOUCH_DELEGATE);
    }

    private void allowParentTouch() {
        ViewGroup home = (ViewGroup) vp2.getParent();
        home.setOnTouchListener(null);
    }

    private final OnTouchListener INTERCEPT_TOUCH_DELEGATE = (v, event) -> true;

}
