package com.sloth.cinema;

import android.content.Context;
import android.os.Environment;

import com.sloth.tools.util.LogUtils;
import com.yanzhenjie.andserver.annotation.Config;
import com.yanzhenjie.andserver.framework.config.WebConfig;
import com.yanzhenjie.andserver.framework.website.AssetsWebsite;
import com.yanzhenjie.andserver.framework.website.FileBrowser;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/16 10:40
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
@Config
public class WebServerConfig implements WebConfig {

    private static final String TAG = "WebServerConfig";

    @Override
    public void onConfig(Context context, Delegate delegate) {
        // 增加一个位于assets的web目录的网站
        delegate.addWebsite(new AssetsWebsite(context, "/web/"));

        // 增加一个资源目录
        String staticFileDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
        LogUtils.d(TAG, "add static resource dir: " + staticFileDir);
        delegate.addWebsite(new FileBrowser(staticFileDir));
    }
}
