package com.sloth.banner.transform;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

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
public class GalleryTransform implements ViewPager2.PageTransformer, DecorTransform, Orientable {

    private static final String TAG = GalleryTransform.class.getSimpleName();

    private int orientation = RecyclerView.HORIZONTAL;

    //viewpager2子视图必须和父控件大小一致，因此如果需要同时显示多个item需要缩小子视图的视觉效果
    //默认同屏显示3个元素，即缩小1/3
    private float viewPort = 0.33f;

    private final float itemScale;

    private final float itemAlpha;

    private final float itemOffset;

    public static final int ALIGN_ITEMS_START = 1;
    public static final int ALIGN_ITEMS_CENTER = 2;
    public static final int ALIGN_ITEMS_END = 3;

    //item 排列方式
    private int align = ALIGN_ITEMS_CENTER;

    //垂直偏移
    private float verticalOffset = 0f;

    private boolean valueAnimator = false;

    //无意义 - 动画用到的参数
    private FrameLayout.LayoutParams lp;

    public GalleryTransform(float itemScale, float itemAlpha, float itemOffset) {
        this.itemScale = itemScale;
        this.itemAlpha = itemAlpha;
        this.itemOffset = itemOffset;
    }

    public GalleryTransform(float viewPort, float itemScale, float itemAlpha, float itemOffset) {
        this.viewPort = viewPort;
        this.itemScale = itemScale;
        this.itemAlpha = itemAlpha;
        this.itemOffset = itemOffset;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public void setVerticalOffset(float verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    public void setIsValueAnimator(boolean b){
        this.valueAnimator = b;
    }

    @Override
    public boolean valueAnimator() {
        return valueAnimator;
    }

    @Override
    public FrameLayout createDecor(Context context) {
        //如果使用值动画，套一层frameLayout 对内部视图进行缩放
        return valueAnimator() ? new FrameLayout(context) : null;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        int viewSize = isHorizontal() ? view.getWidth() : view.getHeight();
        float viewPortSize = viewPort * viewSize;
        float trans = position * itemOffset;
        trans += (-position * (viewSize - viewPortSize));

        if(position > 1 || position <= -1){
            float scale = itemScale * viewPort;
            view.setAlpha(itemAlpha);
            if(isHorizontal()){
                view.setTranslationX(trans);
            }else{
                view.setTranslationY(trans);
            }
            scaleView(view, scale);
            ViewCompat.setTranslationZ(view, 0f);
        }else {
            float alpha = 1 - (Math.abs(position) * (1 - itemAlpha));
            float scale = 1 - (Math.abs(position) * (1 - itemScale));
            scale*= viewPort;
            if(position <= 0){
                view.setAlpha(alpha);
                if(isHorizontal()){
                    view.setTranslationX(trans);
                }else{
                    view.setTranslationY(trans);
                }
                scaleView(view, scale);
                if(position > -0.5f){
                    ViewCompat.setTranslationZ(view, 2f);
                }else{
                    ViewCompat.setTranslationZ(view, 1f);
                }
            }else if(position <= 1){
                view.setAlpha(alpha);
                if(isHorizontal()){
                    view.setTranslationX(trans);
                }else{
                    view.setTranslationY(trans);
                }
                scaleView(view, scale);
                if(position < 0.5f){
                    ViewCompat.setTranslationZ(view, 2f);
                }else{
                    ViewCompat.setTranslationZ(view, 1f);
                }
            }
        }
        adjustAlign(view, Math.abs(position));
    }

    private void adjustAlign(View view, float position) {

        if(orientation == RecyclerView.HORIZONTAL && align == ALIGN_ITEMS_START){
            //上对齐
            float w1 = view.getHeight() * viewPort;
            float w2 = w1 * itemScale;
            float verTrans = (w1 - w2) / 2;
            view.setTranslationY((-verTrans + verticalOffset) * Math.min(1.0f, position));
        }else if(orientation == RecyclerView.HORIZONTAL && align == ALIGN_ITEMS_END){
            //下对齐
            float w1 = view.getHeight() * viewPort;
            float w2 = w1 * itemScale;
            float verTrans = (w1 - w2) / 2;
            view.setTranslationY((verTrans + verticalOffset) * Math.min(1.0f, position));
        }else if(orientation != RecyclerView.HORIZONTAL && align == ALIGN_ITEMS_START){
            //左对齐
            float w1 = view.getWidth() * viewPort;
            float w2 = w1 * itemScale;
            float verTrans = (w1 - w2) / 2;
            view.setTranslationX((-verTrans + verticalOffset) * Math.min(1.0f, position));

        }else if(orientation != RecyclerView.HORIZONTAL && align == ALIGN_ITEMS_END){
            //右对齐
            float w1 = view.getWidth() * viewPort;
            float w2 = w1 * itemScale;
            float verTrans = (w1 - w2) / 2;
            view.setTranslationX((verTrans + verticalOffset) * Math.min(1.0f, position));
        }else if(orientation == RecyclerView.HORIZONTAL && align == ALIGN_ITEMS_CENTER){
            //横向居中
            view.setTranslationY(verticalOffset * Math.min(1.0f, position));
        }else if(orientation != RecyclerView.HORIZONTAL && align == ALIGN_ITEMS_CENTER){
            //垂直居中
            view.setTranslationX(verticalOffset * Math.min(1.0f, position));
        }
    }

    private boolean isHorizontal(){
        return orientation == RecyclerView.HORIZONTAL;
    }

    private void scaleView(View view, float scale) {
        if(valueAnimator()){
            FrameLayout parent = (FrameLayout) view;
            View child = parent.getChildAt(0);
            lp = (FrameLayout.LayoutParams) child.getLayoutParams();
            lp.width = (int) (parent.getWidth() * scale);
            lp.height = (int) (parent.getHeight() * scale);
            child.setLayoutParams(lp);
        }else{
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    }

    @Override
    public int orientation() {
        return orientation;
    }
}
