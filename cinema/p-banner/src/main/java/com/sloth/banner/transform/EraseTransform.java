package com.sloth.banner.transform;

import android.graphics.Rect;
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
public class EraseTransform implements ViewPager2.PageTransformer, Orientable {

    private static final String TAG = EraseTransform.class.getSimpleName();

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

        Rect rect = new Rect();
        rect.set(0,0, view.getWidth(), view.getHeight());

        if(position > 1 || position <= -1){
            //others still
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                //erase out
                if(isHorizontal()){
                    rect.right = view.getWidth() + (int) (view.getWidth() * position);
                }else{
                    rect.bottom = view.getHeight() + (int) (view.getHeight() * position);
                }
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                //in
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
        ViewCompat.setClipBounds(view, rect);
    }
}
