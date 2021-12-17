package com.sloth.banner.transform.agent;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sloth.banner.XBanner;
import com.sloth.banner.transform.DecorTransform;
import com.sloth.functions.viewpager2.widget.ViewPager2;
import com.sloth.functions.widget.AgentView;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.ViewUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/19 16:32
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class BaseAgentTransform<V extends AgentView> implements ViewPager2.PageTransformer, DecorTransform {

    private V agentView;

    /**
     * false - 对整个View截图
     * true - 对嵌套在容器中的video播放器内容单独截图作为封面
     *
     */
    private boolean nestedVideoSnapshotForEnter = false;
    private boolean nestedVideoSnapshotForExit = false;

    public BaseAgentTransform() { }

    public void setNestedVideoSnapshotForEnter(boolean nestedVideoSnapshotForEnter) {
        this.nestedVideoSnapshotForEnter = nestedVideoSnapshotForEnter;
    }

    public void setNestedVideoSnapshotForExit(boolean nestedVideoSnapshotForExit) {
        this.nestedVideoSnapshotForExit = nestedVideoSnapshotForExit;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        transforming(((AgentDecor<V>)view), position);
    }

    protected abstract void transforming(AgentDecor<V> view, float position);

    @Override
    public FrameLayout createDecor(Context context) {
        AgentDecor decor = new AgentDecor(context){
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                onAnimStart();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onAnimEnd();
            }
        };
        if(agentView == null){
            agentView = createAgentView(context);
        }
        decor.setInAgentView(agentView);
        return decor;
    }

    protected void onAnimStart() { }

    protected void onAnimEnd() { }

    protected abstract V createAgentView(Context context);

    protected static class AgentDecor<V extends AgentView> extends FrameLayout implements Animator.AnimatorListener {

        private static final String TAG = AgentDecor.class.getSimpleName();

        private ViewPager2 vp2;
        private V proxyView;

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

            disAllowParentTouch();

            if(isProxyViewPlaying()){
                //已在播放中
                return;
            }

            Rect rect = new Rect();
            getLocalVisibleRect(rect);

            proxyView.play(rect,this, args);

        }

        private boolean isProxyViewPlaying() {
            return ((View)proxyView).getParent() != null;
        }

        public boolean isPlaying(){
            return isProxyViewPlaying();
        }

        public void clear(){ }

        @Override
        public void onAnimationStart(Animator animation) {
            LogUtils.d(TAG, "onAnimationStart");
            disAllowParentTouch();
            addProxyView();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            LogUtils.d(TAG, "onAnimationEnd");
            removeProxyView();
            allowParentTouch();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            LogUtils.d(TAG, "onAnimationCancel");
            removeProxyView();
            allowParentTouch();
        }

        @Override
        public void onAnimationRepeat(Animator animation) { }

        private void initvp2() {
            if(vp2 == null){
                vp2 = (ViewPager2) getParent().getParent();
            }
        }

        private void addProxyView(){
            initvp2();
            FrameLayout home = (FrameLayout) vp2.getParent();
            if(home.indexOfChild(proxyView.view()) == -1){
                //未添加过
                int vpIndex = home.indexOfChild(vp2);
                home.addView(proxyView.view(), vpIndex + 1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
        }

        private void removeProxyView(){
            initvp2();
            ViewGroup home = (ViewGroup) vp2.getParent();

            if(home.indexOfChild(proxyView.view()) != -1){
                home.removeView(proxyView.view());
            }
        }

        private void disAllowParentTouch() {
            initvp2();
            XBanner home = (XBanner) vp2.getParent();
            home.disAllowTouch(true);
        }

        private void allowParentTouch() {
            initvp2();
            XBanner home = (XBanner) vp2.getParent();
            home.disAllowTouch(false);
        }
    }

    protected Bitmap snapExit(AgentDecor view){
        return snap(nestedVideoSnapshotForExit, view);
    }

    protected Bitmap snapEnter(AgentDecor view){
        return snap(nestedVideoSnapshotForEnter, view);
    }

    private Bitmap snap(boolean nestedVideo, AgentDecor view) {
        if(!nestedVideo){
            return ViewUtils.snapshot(view);
        }

        //嵌套获取播放器
        TextureView snapableView = findTexture(view.getChildAt(0));
        return snapableView != null ? snapableView.getBitmap() : ViewUtils.snapshot(view);
    }
    private TextureView findTexture(View view) {
        if(view instanceof ViewGroup){
            for(int i = 0; i < ((ViewGroup)view).getChildCount(); i++){
                TextureView res = findTexture(((ViewGroup)view).getChildAt(i));
                if(res != null){
                    return res;
                }
            }
        }else if(view instanceof TextureView){
            return (TextureView) view;
        }

        return null;
    }

}
