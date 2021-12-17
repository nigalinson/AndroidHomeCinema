package com.sloth.tools.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import java.util.Locale;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2019/06/18
 *     desc  : utils about view
 * </pre>
 */
public class ViewUtils {

    private ViewUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static <V extends View> V setGone(V view, boolean isGone) {
        if (view != null) {
            if (isGone) {
                if (View.GONE != view.getVisibility()) {
                    view.setVisibility(View.GONE);
                }
            } else {
                if (View.VISIBLE != view.getVisibility()) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
        return view;
    }

    public static <V extends View> V setInvisible(V view, boolean isGone) {
        if (view != null) {
            if (isGone) {
                if (View.INVISIBLE != view.getVisibility()) {
                    view.setVisibility(View.INVISIBLE);
                }
            } else {
                if (View.VISIBLE != view.getVisibility()) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
        return view;
    }

    public static void setViewsInvisible(boolean gone, View... views) {
        for (View view : views) {
            setInvisible(view, gone);
        }
    }

    public static void setViewsGone(boolean gone, View... views) {
        for (View view : views) {
            setGone(view, gone);
        }
    }

    /**
     * Set the enabled state of this view.
     *
     * @param view    The view.
     * @param enabled True to enabled, false otherwise.
     */
    public static void setViewEnabled(View view, boolean enabled) {
        setViewEnabled(view, enabled, (View) null);
    }

    /**
     * Set the enabled state of this view.
     *
     * @param view     The view.
     * @param enabled  True to enabled, false otherwise.
     * @param excludes The excludes.
     */
    public static void setViewEnabled(View view, boolean enabled, View... excludes) {
        if (view == null) return;
        if (excludes != null) {
            for (View exclude : excludes) {
                if (view == exclude) return;
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setViewEnabled(viewGroup.getChildAt(i), enabled, excludes);
            }
        }
        view.setEnabled(enabled);
    }

    /**
     * @param runnable The runnable
     */
    public static void runOnUiThread(final Runnable runnable) {
        UtilsCenter.runOnUiThread(runnable);
    }

    /**
     * @param runnable    The runnable.
     * @param delayMillis The delay (in milliseconds) until the Runnable will be executed.
     */
    public static void runOnUiThreadDelayed(final Runnable runnable, long delayMillis) {
        UtilsCenter.runOnUiThreadDelayed(runnable, delayMillis);
    }

    /**
     * Return whether horizontal layout direction of views are from Right to Left.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Locale primaryLocale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                primaryLocale = Utils.getApp().getResources().getConfiguration().getLocales().get(0);
            } else {
                primaryLocale = Utils.getApp().getResources().getConfiguration().locale;
            }
            return TextUtils.getLayoutDirectionFromLocale(primaryLocale) == View.LAYOUT_DIRECTION_RTL;
        }
        return false;
    }

    /**
     * Fix the problem of topping the ScrollView nested ListView/GridView/WebView/RecyclerView.
     *
     * @param view The root view inner of ScrollView.
     */
    public static void fixScrollViewTopping(View view) {
        view.setFocusable(false);
        ViewGroup viewGroup = null;
        if (view instanceof ViewGroup) {
            viewGroup = (ViewGroup) view;
        }
        if (viewGroup == null) {
            return;
        }
        for (int i = 0, n = viewGroup.getChildCount(); i < n; i++) {
            View childAt = viewGroup.getChildAt(i);
            childAt.setFocusable(false);
            if (childAt instanceof ViewGroup) {
                fixScrollViewTopping(childAt);
            }
        }
    }

    public static View layoutId2View(@LayoutRes final int layoutId) {
        LayoutInflater inflate =
                (LayoutInflater) Utils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate.inflate(layoutId, null);
    }

    /**
     * 截图
     * @param v
     * @return
     */
    public static Bitmap snapshot(View v){
        return snapshot(v, v.getWidth(), v.getHeight());
    }

    public static Bitmap snapshot(View v, int width, int height){
        if(width <= 0 || height <= 0){ return null; }

        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        if(b == null){ return null; }
        Canvas c = new Canvas(b);
        synchronized (c){
            c.setBitmap(b);
            v.draw(c);
            c.setBitmap(null);
        }
        return b;
    }


    /**
     * 调整侧边图标大小
     * @param index
     * @param tv
     * @param size
     */
    public static void adjustDrawableSize(int index, TextView tv, int size){
        Drawable drawable = tv.getCompoundDrawables()[index];
        int dp = SizeUtils.dp2px(size);
        drawable.setBounds(0, 0, dp, dp);//第一个 0 是距左边距离，第二个 0 是距上边距离，40 分别是长宽
        tv.setCompoundDrawables(
                index == 0 ? drawable : null,
                index == 1 ? drawable : null,
                index == 2 ? drawable : null,
                index == 3 ? drawable : null
        );
    }

    public static void expandViewTouchDelegate(final View view, final int top, final int bottom, final int left, final int right) {
        ((View) view.getParent()).post(() -> {
            Rect bounds = new Rect();
            view.getHitRect(bounds);

            bounds.top -= top;
            bounds.bottom += bottom;
            bounds.left -= left;
            bounds.right += right;

            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

            if (view.getParent() instanceof View) {
                ((View) view.getParent()).setTouchDelegate(touchDelegate);
            }
        });
    }

    public static void clearViewTouchDelegate(final View view) {
        ((View) view.getParent()).post(() -> {
            Rect bounds = new Rect();
            view.getHitRect(bounds);
            bounds.setEmpty();
            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

            if (view.getParent() instanceof View) {
                ((View) view.getParent()).setTouchDelegate(touchDelegate);
            }
        });
    }

    public static void releaseImageViewResource(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

}