package com.sloth.banner.player;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.VideoView;
import com.sloth.tools.util.LogUtils;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/10 10:49
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/10         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class AttachableProxy {

    private static final String TAG = AttachableProxy.class.getSimpleName();
    /**
     * 是否已经添加到View
     */
    private final AtomicBoolean attached = new AtomicBoolean(false);

    public boolean isAttached(){
        return attached.get();
    }

    public abstract View attachingView();

    public void attach(FrameLayout parent) {
        attach(0, parent);
    }

    public void attach(int index, FrameLayout parent){

        if(parent.indexOfChild(attachingView()) != -1){
            //已经添加
            return;
        }

        int attemptTimes = 0;
        while(attached.get()){
            //如果准备添加时已经添加到其他布局，先从其他布局去除
            //尝试3次
            if(attemptTimes >= 3){
                //三次尝试后依然无法去除上一次的View-判定失败
                LogUtils.e(TAG, "attach失败，detach上一次的View异常");
                return;
            }
            detach();
            attemptTimes++;
        }

        attemptTimes = 0;
        while(parentAlreadyHasVideoView(parent)){
            //如果准备添加时已经添加到其他布局，先从其他布局去除
            //尝试3次
            if(attemptTimes >= 3){
                //三次尝试后依然无法去除parent中的video
                LogUtils.e(TAG, "remove parent video failed");
                return;
            }
            attemptTimes++;
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        parent.addView(attachingView(), index, lp);
        LogUtils.e(TAG, "attach view");
        attached.set(true);
    }

    public void detach(){
        View v = attachingView();
        ViewParent parent = v.getParent();
        if(parent == null){
            LogUtils.e(TAG, "video已经detach");
            return;
        }
        ((ViewGroup)parent).removeView(v);
        LogUtils.e(TAG, "video detach");
        attached.set(false);
    }

    private boolean parentAlreadyHasVideoView(ViewGroup parent) {
        for(int i = 0; i < parent.getChildCount(); i++){
            View v = parent.getChildAt(i);
            if(v instanceof VideoView){
                parent.removeView(v);
                return true;
            }
        }
        return false;
    }
}
