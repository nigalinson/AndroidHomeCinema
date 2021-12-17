package com.sloth.pinsplatform.log;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/18 18:32
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Log {

    int V = android.util.Log.VERBOSE;
    int D = android.util.Log.DEBUG;
    int I = android.util.Log.INFO;
    int W = android.util.Log.WARN;
    int E = android.util.Log.ERROR;
    int A = android.util.Log.ASSERT;

    @IntDef({V, D, I, W, E, A})
    @Retention(RetentionPolicy.SOURCE)
    @interface Level { }

    void setLevel(@Level int level);

    void d(String tag, String msg);

    void v(String tag, String msg);

    void i(String tag, String msg);

    void w(String tag, String msg);

    void e(String tag, String msg);

    void json(String tag, Object obj);

    default void flush(boolean sync){ }

    default void exit(){ }

}
