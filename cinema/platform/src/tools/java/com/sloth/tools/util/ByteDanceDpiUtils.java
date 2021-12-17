package com.sloth.tools.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import androidx.appcompat.app.AlertDialog;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/1/29 13:40
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/1/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ByteDanceDpiUtils {

    private static final String TAG = "ByteDanceDpiUtils";

    private ByteDanceDpiUtils() {
        throw new UnsupportedOperationException("do not initialize me !!");
    }

    public static void setCustomDensity(Resources resource) {

        final DisplayMetrics appDisplayMetrics = resource.getDisplayMetrics();

        //360表示这个项目设计图的宽度为360dp
        final float targetDensity = appDisplayMetrics.widthPixels / dpi(resource);//每dp等于targetDensity px

        final int targetDensityDpi = (int) (160 * targetDensity);//重新计算设备的dpi

        appDisplayMetrics.density = appDisplayMetrics.scaledDensity = targetDensity;

        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDisplayMetrics = resource.getDisplayMetrics();

        activityDisplayMetrics.density = activityDisplayMetrics.scaledDensity = targetDensity;

        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

    private static int dpi(Resources resource) {
        Configuration mConfiguration = resource.getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            return 640;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            return 360;
        }
        return 360;
    }

    public static void adjustDialogSize(Activity activity, AlertDialog mAlertDialog){
        if (mAlertDialog.getWindow() != null) {
            LogUtils.d(TAG, "调整AlertDialog大小");
            WindowManager.LayoutParams lp = mAlertDialog.getWindow().getAttributes();
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            //宽度，可根据屏幕宽度进行计算
            lp.width = size.x / 10 * 8;
            lp.gravity = Gravity.CENTER;
            mAlertDialog.getWindow().setAttributes(lp);
        }
    }

}
