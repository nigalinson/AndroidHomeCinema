package com.sloth.banner.transform;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sloth.functions.viewpager2.widget.ViewPager2;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/21 10:15
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/21         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class Rotate3DTransform implements ViewPager2.PageTransformer, Orientable {

    private static final String TAG = Rotate3DTransform.class.getSimpleName();

    private int orientation = RecyclerView.HORIZONTAL;

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    public int orientation() {
        return orientation;
    }

    private boolean isHorizontal(){
        return orientation == RecyclerView.HORIZONTAL;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {

        if(position > 1 || position <= -1){
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            if(isHorizontal()){
                view.setRotationY(0.0f);
            }else{
                view.setRotationX(0.0f);
            }
            view.setAlpha(0.0f);
        }else {
            if(position <= 0) {
                float ratio = 1f + 0.5f * position;
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                if(isHorizontal()){
                    view.setRotationY(60f * position);
                }else{
                    view.setRotationX(-60f * position);
                }
                view.setAlpha(1.0f + position);
            }else if(position <= 1) {
                float ratio = 1.0f - 0.2f * position;
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                view.setAlpha(1.0f - position);
            }
        }
    }
}
