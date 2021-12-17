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
public class Card3DSlipTransform implements ViewPager2.PageTransformer, Orientable {

    private static final String TAG = Card3DSlipTransform.class.getSimpleName();

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
            view.setPivotX(view.getWidth());
            view.setPivotY(view.getHeight() / 2f);
        }else{
            view.setTranslationY(-view.getHeight() * position);
            view.setPivotX(view.getWidth() / 2f);
            view.setPivotY(view.getHeight());
        }
        
        if(position > 1 || position <= -1){
            float ratio = 1.0f;
            view.setScaleX(ratio);
            view.setScaleY(ratio);
            if(isHorizontal()){
                view.setRotationY(0f);
            }else{
                view.setRotationX(0f);
            }
            view.setAlpha(0);
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                if(isHorizontal()){
                    view.setTranslationX(0f);
                }else{
                    view.setTranslationY(0f);
                }
                view.setAlpha(1 + position);
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                float ratio = 1f - 0.5f * position;
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                if(isHorizontal()){
                    view.setRotationY(-30f * position);
                }else{
                    view.setRotationX(-30f * position);
                }
                view.setAlpha(1 - position);
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
    }
}
