package com.sloth.banner.vh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/18 20:26
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class AnimateContainer extends FrameLayout {
    public AnimateContainer(@NonNull Context context) {
        super(context);
    }

    public AnimateContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
