package com.sloth.banner.transform;

import android.content.Context;
import androidx.core.view.ViewCompat;
import com.sloth.banner.XBanner;
import com.sloth.banner.transform.drawer.BaseDrawerTransform;
import com.sloth.banner.transform.drawer.Drawer;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/14 11:26
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/14         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ClipPathTransform extends BaseDrawerTransform {

    private static final String TAG = ClipPathTransform.class.getSimpleName();

    private final XBanner banner;

    private int mode = ClipPathDrawer.Mode.WINDOW_OPEN_VERTICAL.code;

    public ClipPathTransform(XBanner banner) {
        this.banner = banner;
    }

    public ClipPathTransform(XBanner banner, int mode) {
        this.banner = banner;
        this.mode = mode;
    }

    @Override
    protected Drawer createDrawer(Context context) {
        ClipPathDrawer clipPathDrawer = new ClipPathDrawer(mode, 1200);
        clipPathDrawer.setWidth(banner.getWidth());
        clipPathDrawer.setHeight(banner.getHeight());
        return clipPathDrawer;
    }

    @Override
    protected void transforming(DrawerDecor view, float position) {
        view.setTranslationX(-view.getWidth() * position);

        if(position > 1 || position <= -1){
            ViewCompat.setTranslationZ(view, -1f);
            view.dispatchProgress(0);
        }else if(position <= 0){
            ViewCompat.setTranslationZ(view, 1f);
            view.dispatchProgress(Math.abs(position));
        }else if(position <= 1){
            ViewCompat.setTranslationZ(view, 0f);
            view.dispatchProgress(0);
        }
    }
}
