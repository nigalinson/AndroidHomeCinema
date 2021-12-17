package com.sloth.banner.transform;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
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
public class FadeScaleTransform implements ViewPager2.PageTransformer, Orientable {

    private static final String TAG = FadeScaleTransform.class.getSimpleName();

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
        //offset keep x or y still
        if(isHorizontal()){
            view.setTranslationX(-view.getWidth() * position);
        }else{
            view.setTranslationY(-view.getHeight() * position);
        }

        if(position > 1 || position <= -1){
            float ratio = 0.8f;
            view.setScaleX(ratio);
            view.setScaleY(ratio);
            view.setAlpha(0.0f);
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                float ratio = 1.0f + (0.2f * position);
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                view.setAlpha(1.0f + position);
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                float ratio = 1.0f - (0.2f * position);
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                view.setAlpha(1.0f - position);
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
    }
}
