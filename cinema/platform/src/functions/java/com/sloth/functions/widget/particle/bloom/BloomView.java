package com.sloth.functions.widget.particle.bloom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import com.sloth.functions.widget.AgentView;
import com.sloth.tools.util.LogUtils;
import java.util.List;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/8/27
 * Description:粒子爆炸view
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/8/27      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class BloomView extends View implements AgentView {
    private final static String TAG = "BloomView";

    private float mBloomParticleRadius = 10;
    private Animator.AnimatorListener mBloomListener;
    private List<BloomParticle> mBloomParticles;
    private final Paint mDrawPaint;
    private final Matrix mDrawMatrix;
    private final Path mDrawPath;
    private final RectF mTargetRect;
    private final RectF mBoundRect; //the bound rect for particles.
    private BloomEffector mEffector;
    private ParticleShapeDistributor mShapeDistributor;
    private ValueAnimator mBloomAnimator;
    //todo 目前根据radius和bitmap总体大小计算，后期补全
    private int row = 10,column = 10;

    public BloomView(Context context) {
        super(context);

        mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDrawMatrix = new Matrix();
        mTargetRect = new RectF();
        mBoundRect = new RectF();
        mDrawPath = new Path();
    }

    public void setParticleRadius(float radius) {
        mBloomParticleRadius = radius;
    }

    public void setBloomListener(Animator.AnimatorListener bloomListener) {
        this.mBloomListener = bloomListener;
    }

    public void setEffector(BloomEffector bloomEffector) {
        this.mEffector = bloomEffector;
    }

    public void setBloomShapeDistributor(ParticleShapeDistributor shapeDistributor) {
        mShapeDistributor = shapeDistributor;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void boom(Rect rect, Bitmap snap){

        if (snap == null){
            LogUtils.w(TAG, "Aim bitmap is null");
            return;
        }

        mTargetRect.set(rect);
        mBoundRect.set(rect);

//        if(row == -1){
            row = (int) (snap.getWidth() / (mBloomParticleRadius * 2));
//        }

//        if(column == -1){
            column = (int) (snap.getHeight() / (mBloomParticleRadius * 2));
//        }

        if (mShapeDistributor == null){
            mShapeDistributor = new CircleShapeDistributor();
        }

        mBloomParticles = BloomParticleUtil.generateParticles(snap, row, column, mTargetRect,
                mBoundRect, mBloomParticleRadius, mShapeDistributor);

        if (mBloomParticles == null
                || mBloomParticles.isEmpty()){
            LogUtils.w(TAG, "Generating particles failed.");
            return;
        }

        configureBloomEffector();
        boomNow();
    }

    private void configureBloomEffector(){
        if (mEffector == null){
            mEffector = new BloomEffector.Builder()
                    .setAnchor(mTargetRect.width()  / 2, mTargetRect.height() / 2)
                    .setSpeedRange(0.1f, 0.5f)
                    .build();
        }

        mBloomAnimator = ValueAnimator.ofInt(0, (int) mEffector.getDuration());
        mBloomAnimator.setDuration(mEffector.getDuration());
        mBloomAnimator.setInterpolator(mEffector.getInterpolator());
        mBloomAnimator.addUpdateListener(mAnimatorUpdateListener);
        mBloomAnimator.addListener(mAnimatorListener);
    }

    private void boomNow(){
        mBloomAnimator.start();
    }

    private void cancelAnimator(){
        if (mBloomAnimator != null){
            if (mBloomAnimator.isRunning()) {
                mBloomAnimator.cancel();
            }
            mBloomAnimator.removeListener(mAnimatorListener);
            mBloomAnimator.removeUpdateListener(mAnimatorUpdateListener);
            mBloomAnimator = null;
        }
    }

    /**
     * */
    private void onBoomEnd(Animator animator){

        if (mEffector != null) {
            mEffector.destroy();
            mEffector = null;
        }

        cancelAnimator();
    }

    /**
     * Animation updates to refresh the state of particles.
     * */
    private void onAnimatorUpdate(int animatedValue){
        for (BloomParticle particle : mBloomParticles) {
            if (particle.isActivated()) {
                mEffector.apply(animatedValue, particle);
                particle.setActivated(BloomParticleUtil.isParticleActivated(mBoundRect, particle));
            }
        }

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBloomParticles == null
                || mBloomParticles.isEmpty()){
            return;
        }

        for (BloomParticle particle : mBloomParticles){
            if (!particle.isActivated()){
                continue;
            }

            Path particlePath = particle.getShape().getShapePath();
            if (particlePath != null
                    && !particlePath.isEmpty()) {

                mDrawMatrix.reset();
                mDrawPath.reset();

                float deltaX = particle.getDrawX() - particle.getInitialX();
                float deltaY = particle.getDrawY() - particle.getInitialY();

                mDrawMatrix.postSkew(particle.getSkew(), particle.getSkew(), particle.getInitialX(),
                        particle.getInitialY());

                mDrawMatrix.postRotate(particle.getRotation(), particle.getInitialX(),
                        particle.getInitialY());

                mDrawMatrix.postScale(particle.getScale(), particle.getScale(), particle.getInitialX(),
                        particle.getInitialY());
                mDrawMatrix.postTranslate(mTargetRect.left + deltaX, mTargetRect.top+deltaY);

                particlePath.transform(mDrawMatrix, mDrawPath);

                mDrawPaint.setColor(particle.getColor());
                mDrawPaint.setAlpha(particle.getAlpha());
                canvas.drawPath(mDrawPath, mDrawPaint);
            }
        }
    }

    private final ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = animation ->
            onAnimatorUpdate((Integer) animation.getAnimatedValue());

    private final AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            if(mBloomListener != null){
                mBloomListener.onAnimationStart(animation);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
            if(mBloomListener != null){
                mBloomListener.onAnimationCancel(animation);
            }
            onBoomEnd(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(mBloomListener != null){
                mBloomListener.onAnimationEnd(animation);
            }
            onBoomEnd(animation);
        }
    };

    public void cancel() {
        cancelAnimator();
    }

    @Override
    public void play(Rect rect, Animator.AnimatorListener animatorListener, Object... args) {

        Bitmap snap = (Bitmap) args[0];

        int bpX = snap.getWidth();
        int bpY = snap.getHeight();

        if(bpX < rect.width()){
            int gap = (rect.width() - bpX) / 2;
            rect.left = gap;
            rect.right = bpX + gap;
        }

        if(bpY < rect.height()){
            int gap = (rect.height() - bpY) / 2;
            rect.top = gap;
            rect.bottom = bpY + gap;
        }

        BloomSceneMaker bloomSceneMaker = (BloomSceneMaker) args[1];

        if(bloomSceneMaker != null){
            bloomSceneMaker.setAnchor((float)rect.width() / 2, rect.height());
            bloomSceneMaker.apply(this);
        }

        setBloomListener(animatorListener);
        boom(rect, snap);
    }

    @Override
    public View view() {
        return this;
    }
}
