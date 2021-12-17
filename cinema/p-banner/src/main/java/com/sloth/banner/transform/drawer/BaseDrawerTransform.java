package com.sloth.banner.transform.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sloth.banner.transform.DecorTransform;
import com.sloth.functions.viewpager2.widget.ViewPager2;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/19 16:03
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class BaseDrawerTransform implements ViewPager2.PageTransformer, DecorTransform {

    private Drawer drawer;

    private DrawerInitializeListener drawerInitializeListener;

    public BaseDrawerTransform() { }

    public void setDrawerInitializeListener(DrawerInitializeListener drawerInitializeListener) {
        this.drawerInitializeListener = drawerInitializeListener;
    }

    @Override
    public FrameLayout createDecor(Context context) {
        DrawerDecor decor = new DrawerDecor(context);
        if(drawer == null){
            drawer = createDrawer(context);
            if(drawerInitializeListener != null){
                drawerInitializeListener.onDrawerInited(drawer);
            }
        }
        decor.setDrawer(drawer);
        return decor;
    }

    protected abstract Drawer createDrawer(Context context);

    @Override
    public void transformPage(@NonNull View view, float position) {
        transforming(((DrawerDecor)view), position);
    }

    protected abstract void transforming(DrawerDecor view, float position);

    protected static class DrawerDecor extends FrameLayout {

        private static final String TAG = DrawerDecor.class.getSimpleName();

        private Drawer drawer;

        public DrawerDecor(@NonNull Context context) {
            super(context);
            init(context, null, -1);
        }

        public DrawerDecor(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs, -1);
        }

        public DrawerDecor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context, attrs, defStyleAttr);
        }

        private void init(Context context, AttributeSet attrs, int defStyleAttr) {
            setWillNotDraw(false);
        }

        private float pro = 0;

        public void setDrawer(Drawer dr){
            this.drawer = dr;
        }

        public void dispatchProgress(float progress){
            pro = progress;
            if(drawer != null){
                invalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(drawer != null){
                drawer.advance(canvas, pro);
            }
            super.onDraw(canvas);
        }
    }

    public interface DrawerInitializeListener {
        void onDrawerInited(Drawer drawer);
    }
}
