package com.sloth.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Author:    Oscar
 * Version    V1.0
 * Date:      2020/9/28
 * Description:自适应VideoView
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/28      Oscar            1.0                    1.0
 * Why & What is modified:
 */
public class FixedVideoView extends VideoView {

    public FixedVideoView(Context context) {
        super(context);
    }

    public FixedVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

}
