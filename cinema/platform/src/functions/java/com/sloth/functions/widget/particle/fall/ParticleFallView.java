package com.sloth.functions.widget.particle.fall;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sloth.functions.widget.AgentView;
import com.sloth.tools.util.LogUtils;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/9/23
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/23      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class ParticleFallView extends View implements AgentView {
  private static final String TAG = ParticleFallView.class.getSimpleName();
  private ParticleFallAnimator animator;

  public ParticleFallView(Context context) {
    super(context);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    animator.draw(canvas);
  }

  public void explode(Rect rect, Bitmap snapshot, int radius, long dur, final AnimatorListenerAdapter listener) {
    explode(rect, snapshot, radius, -1, -1, dur, listener);
  }

  public void explode(Rect rect, Bitmap snapshot, int radius, int xCount, int yCount, long dur, final Animator.AnimatorListener listener) {

    if(snapshot == null){
      LogUtils.e(TAG, "图像为空，不进行动画");
      return;
    }

    if(xCount == -1 || yCount == -1){
      animator = new ParticleFallAnimator(this, snapshot, rect, radius);
    } else {
      animator = new ParticleFallAnimator(this, snapshot, rect, radius, xCount , yCount);
    }

    animator.setDuration(dur);
    animator.setInterpolator(new AccelerateDecelerateInterpolator());
    // 接口回调
    animator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        if (listener != null) {
          listener.onAnimationStart(animation);
        }
        // 延时添加到界面上
        // 让被爆炸的View消失（爆炸的View是新创建的View，原View本身不会发生任何变化）
//                view.animate().alpha(0f).setDuration(20).start();
//                view.setAlpha(0);
//                view.setVisibility(GONE);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (listener != null) {
          listener.onAnimationEnd(animation);
        }
        // 从界面中移除
        // 让被爆炸的View显示（爆炸的View是新创建的View，原View本身不会发生任何变化）
//                view.animate().alpha(0f).setDuration(20).start();
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        if (listener != null) {
          listener.onAnimationCancel(animation);
        }
      }

      @Override
      public void onAnimationRepeat(Animator animation) {
        if (listener != null) {
          listener.onAnimationRepeat(animation);
        }
      }
    });
    animator.start();
  }

  @Override
  public void play(Rect rect, Animator.AnimatorListener animatorListener, Object... args) {
    explode(rect, (Bitmap)args[0], (int)args[1], (int)args[2], (int)args[3], (long)args[4], animatorListener);
  }

  @Override
  public View view() {
    return this;
  }
}
