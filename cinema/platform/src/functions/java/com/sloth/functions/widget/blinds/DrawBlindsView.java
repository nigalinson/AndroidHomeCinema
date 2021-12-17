package com.sloth.functions.widget.blinds;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.sloth.functions.widget.AgentView;
import com.sloth.tools.util.LogUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/26 10:05
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/26         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class DrawBlindsView extends View implements AgentView, ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = DrawBlindsView.class.getSimpleName();

    //每列item播放间隔比例
    private static final float PLAY_OFFSET_PERCENT = 0.05f;

    private Bitmap[][] frontImages, backImages;

    private Paint paint;

    private ValueAnimator blindAnimator;

    private int rowCount, columnCount, itemSpace;

    private float progress = 0f;

    private int itemWidth, itemHeight;

    //每列item生命周期占用的progress
    private float perItemProgress;

    public DrawBlindsView(Context context) {
        this(context, null, -1);
    }

    public DrawBlindsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DrawBlindsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setWillNotDraw(false);
    }

    /**
     * @param rect
     * @param animatorListener
     * @param args
     * front = (Bitmap) args[0]
     * back = (Bitmap) args[1]
     * isVertical = (boolean) args[2];
     * itemSpace = (int) args[3];
     * rowCount = (int) args[4];
     * columnCount = (int) args[5];
     * duration = (long) args[6];
     *
     */
    @Override
    public void play(Rect rect, Animator.AnimatorListener animatorListener, Object... args) {
        itemSpace = (int) args[3];
        rowCount = (int) args[4];
        columnCount = (int) args[5];
        long duration = (long) args[6];

        LogUtils.d(TAG,  (args[0] != null ? "出场图片 " : "" ) + (args[1] != null ? "入场图片 " : ""));
        createSubImages((Bitmap) args[0], (Bitmap) args[1]);

        if(blindAnimator != null && blindAnimator.isRunning()){
            blindAnimator.removeAllListeners();
            blindAnimator.cancel();
            blindAnimator = null;
        }

        blindAnimator = ValueAnimator.ofFloat(0f, 1f);
        blindAnimator.setDuration(duration);
        blindAnimator.addListener(animatorListener);
        blindAnimator.addUpdateListener(this);
        blindAnimator.start();
    }

    /**
     * 创建子View
     * @param arg
     * @param arg1
     */
    private void createSubImages(Bitmap arg, Bitmap arg1) {
        perItemProgress =  1f / (1 + PLAY_OFFSET_PERCENT * (columnCount - 1));
        itemWidth = arg.getWidth() / columnCount;
        itemHeight = arg.getHeight() / rowCount;

        frontImages = new Bitmap[rowCount][columnCount];
        backImages = new Bitmap[rowCount][columnCount];

        for(int i = 0; i < rowCount; i++){
            for(int j = 0; j < columnCount; j++){
                frontImages[i][j] = Bitmap.createBitmap(arg, j * itemWidth, i * itemHeight, itemWidth, itemHeight);
                backImages[i][j] = Bitmap.createBitmap(arg1, j * itemWidth, i * itemHeight, itemWidth, itemHeight);
            }
        }
    }

    public void stop(){
        if(blindAnimator != null && blindAnimator.isRunning()){
            blindAnimator.removeAllListeners();
            blindAnimator.cancel();
            blindAnimator = null;
        }
    }

    @Override
    public View view() {
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(paint == null){
            paint = new Paint();
        }
        canvas.drawColor(Color.BLACK);

        if(frontImages != null && backImages != null){

            if(progress == 0.5f){
                //一半时无需绘制
                return;
            }

            for(int i = 0; i < rowCount; i++){
                for(int j = 0; j < columnCount; j++){
                    float itemStartP = perItemProgress * PLAY_OFFSET_PERCENT * (columnCount - j);
                    float itemProgress = (progress - itemStartP) / perItemProgress;
                    itemProgress = Math.min(itemProgress, 1.0f);
                    itemProgress = Math.max(itemProgress, 0f);
                    drawItem(canvas, itemProgress <= 0.5f ? frontImages[i][j] : backImages[i][j], itemProgress, j * itemWidth, i * itemHeight);
                }
            }
        }
    }

    private void drawItem(Canvas canvas, Bitmap bitmap, float progress, float x, float y) {
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float[] src = {0, 0,  0, bh,  bw, bh,  bw, 0};

        int DX = (int) ((progress <= 0.5f )
                ? (bw / 2 * (progress / 0.5f))
                : (bw / 2 * ((1 - progress) / 0.5f)));


        int h = (int) (bh * 0.1f);
        int DY = (int) ((progress <= 0.5f )
                ? (h * (progress / 0.5f))
                : (h * ((1 - progress) / 0.5f)));
        float[] dst = {
                DX, DY,
                DX, bh-DY,
                bw-DX, bh+DY,
                bw-DX, -DY
        };
        matrix.setPolyToPoly(src, 0, dst, 0, 4);
        float scale = (progress <= 0.5f )
                ? (1 - 0.4f * (progress / 0.5f))
                : (1 - 0.4f * ((1 - progress) / 0.5f));
        matrix.postScale(scale, scale);
        float xOffset = bw * (1f - scale) / 2;
        float yOffset = bh * (1f - scale) / 2;
        matrix.postTranslate(x + xOffset, y + yOffset);
        canvas.drawBitmap(bitmap, matrix, paint);
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        progress = animation.getAnimatedFraction();
        invalidate();
    }

}
