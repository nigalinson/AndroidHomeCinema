package com.sloth.functions.widget.particle.fall;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;


/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/9/23
 * Description:粒子坠落动画
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/23      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class ParticleFallAnimator {
    private static final String TAG = ParticleFallAnimator.class.getSimpleName();

    public static final int DEFAULT_DURATION = 3000;
    private final ParticleModel[][] mParticles;
    private final Paint mPaint;
    private View mContainer;
    private final int mRadius;
    /**
     * 横纵粒子个数如果不设置，则默认使用长宽除以radius
     */
    private int xCount = -1;
    private int yCount = -1;

    private final ValueAnimator proxyAnimator;

    private float percentage = 0.0f;

    public ParticleFallAnimator(View view, Bitmap bitmap, Rect bound, int radius) {
        proxyAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        proxyAnimator.setDuration(DEFAULT_DURATION);
        proxyAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        proxyAnimator.addUpdateListener(valueAnimator -> {
            percentage = valueAnimator.getAnimatedFraction();
            mContainer.invalidate();
        });
        mRadius = radius;
        mPaint = new Paint();
        mContainer = view;
        xCount = bound.width() / mRadius;
        yCount = bound.height() / mRadius;
        mParticles = generateParticles(bitmap, bound);
    }

    public ParticleFallAnimator(View view, Bitmap bitmap, Rect bound, int radius, int x, int y) {
        proxyAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        proxyAnimator.setDuration(DEFAULT_DURATION);
        proxyAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        proxyAnimator.addUpdateListener(valueAnimator -> {
            percentage = valueAnimator.getAnimatedFraction();
            mContainer.invalidate();
        });
        mRadius = radius;
        mPaint = new Paint();
        mContainer = view;
        xCount = x;
        yCount = y;
        mParticles = generateParticles(bitmap, bound);
    }

    public void setDuration(long dur){
        if(proxyAnimator != null){
            proxyAnimator.setDuration(dur);
        }
    }

    // 生成粒子，按行按列生成全部粒子
    private ParticleModel[][] generateParticles(Bitmap bitmap, Rect bound) {
        // 取粒子宽度
        int virtualItemWidth = bound.width() / xCount;
        // 取粒子高度
        int virtualItemHeight = bound.height() / yCount;

        ParticleModel[][] particles = new ParticleModel[yCount][xCount];
        for (int row = 0; row < yCount; row++) {
            for (int column = 0; column < xCount; column++) {
                //取得当前粒子所在位置的颜色
                int color = -1;
                if (bitmap != null) {
                    int x = column * virtualItemWidth;
                    int y = row * virtualItemHeight;
                    color = bitmap.getPixel(x, y);
                }else{
                    Color.parseColor("#298df1");
                }
                Point point = new Point(column, row);
                particles[row][column] = new ParticleModel(color, mRadius, virtualItemWidth, virtualItemHeight, bound, point);
            }
        }
        return particles;
    }

    // 由view调用，在View上绘制全部的粒子
    public void draw(Canvas canvas) {
        // 动画结束时停止
        if (proxyAnimator == null || !proxyAnimator.isStarted()) {
            return;
        }
        // 遍历粒子，并绘制在View上
        for (ParticleModel[] particle : mParticles) {
            for (ParticleModel p : particle) {
                p.advance(percentage);
                if (p.isValid()) {
                    mPaint.setColor(p.getColor());
                    // 错误的设置方式只是这样设置，透明色会显示为黑色
                    // mPaint.setAlpha((int) (255 * p.alpha));
                    // 正确的设置方式，这样透明颜色就不是黑色了
                    mPaint.setAlpha((int) (Color.alpha(p.getColor()) * p.getAlpha()));
                    canvas.drawCircle(p.getCx(), p.getCy(), p.getRadius(), mPaint);
                }
            }
        }
    }

    public void start() {
        proxyAnimator.start();
    }

    public void setInterpolator(Interpolator inp) {
        proxyAnimator.setInterpolator(inp);
    }

    public void addListener(Animator.AnimatorListener animatorListener) {
        proxyAnimator.addListener(animatorListener);
    }

    public void cancel(){
        if(proxyAnimator != null){
            proxyAnimator.cancel();
        }

    }
}
