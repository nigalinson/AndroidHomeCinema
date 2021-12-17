package com.sloth.functions.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2018/10/25 15:27
 * Description:旋转loadingView
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/10/25      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class RotateView extends AppCompatImageView {

    private int mDrawableRes;
    private Bitmap mBitmap;
    private ObjectAnimator mObjectAnimator;
    private final Matrix mMatrix = new Matrix();
    private final int mAnimatorDuration = 50000;

    public RotateView(Context context) {
        this(context, null);
    }

    public RotateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){ }

    private void rotate(boolean rotate){
        if (mObjectAnimator == null){
            mObjectAnimator =  ObjectAnimator.ofFloat(this, "rotation", 0f, 3600f);
            mObjectAnimator.setDuration(mAnimatorDuration);
            mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        if (rotate && !mObjectAnimator.isRunning()){
            mObjectAnimator.start();
        }else if(!rotate && mObjectAnimator.isRunning()){
            mObjectAnimator.end();
        }
    }

}
