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
public class DifferentialTransform implements ViewPager2.PageTransformer, Orientable {

    private static final String TAG = DifferentialTransform.class.getSimpleName();

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
        float stillDistance = 0;
        if(isHorizontal()){
            stillDistance = -view.getWidth() * position;
        }else{
            stillDistance = -view.getHeight() * position;
        }

        if(position > 1 || position <= -1){
            view.setAlpha(0f);
            if(isHorizontal()){
                stillDistance += (view.getWidth() * 0.3f);
                view.setTranslationX(stillDistance);
            }else{
                stillDistance += (view.getHeight() * 0.3f);
                view.setTranslationY(stillDistance);
            }
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                view.setAlpha(1.0f + position);
                if(isHorizontal()){
                    stillDistance += (view.getWidth() * position);
                    view.setTranslationX(stillDistance);
                }else{
                    stillDistance += (view.getHeight() * position);
                    view.setTranslationY(stillDistance);
                }
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                view.setAlpha(1.0f - position);
                if(isHorizontal()){
                    stillDistance += (view.getWidth() * 0.3f * position);
                    view.setTranslationX(stillDistance);
                }else{
                    stillDistance += (view.getHeight() * 0.3f * position);
                    view.setTranslationY(stillDistance);
                }
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
    }
}
