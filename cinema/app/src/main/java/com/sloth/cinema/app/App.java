package com.sloth.cinema.app;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sankuai.waimai.router.common.DefaultRootUriHandler;
import com.sankuai.waimai.router.components.DefaultLogger;
import com.sankuai.waimai.router.components.DefaultOnCompleteListener;
import com.sankuai.waimai.router.core.Debugger;
import com.sloth.cinema.push.PushHelper;
import com.sloth.pinsplatform.log.Log;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.Utils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/11 14:20
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/11         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = Context.class, key = "/application", singleton = true)
public class App extends MultiDexApplication {

    @RouterProvider
    public static App provideApplication() {
        return sApplication;
    }

    @SuppressLint("StaticFieldLeak")
    private static App sApplication;


    @Override
    public void onCreate() {
        sApplication = this;
        super.onCreate();
        Utils.init(this);
        initRouter(this);

        LogUtils.init(Router.getService(Log.class, Configs.LOG_ENGINE));

        initPush();

    }

    private void initRouter(Context context) {
        // 自定义Logger
        DefaultLogger logger = new DefaultLogger() {
            @Override
            protected void handleError(Throwable t) {
                super.handleError(t);
                // 此处上报Fatal级别的异常
            }
        };

        // 设置Logger
        Debugger.setLogger(logger);

        // Log开关，建议测试环境下开启，方便排查问题。
        Debugger.setEnableLog(true);

        // 调试开关，建议测试环境下开启。调试模式下，严重问题直接抛异常，及时暴漏出来。
        Debugger.setEnableDebug(true);

        // 创建RootHandler
        DefaultRootUriHandler rootHandler = new DefaultRootUriHandler(context);

        // 设置全局跳转完成监听器，可用于跳转失败时统一弹Toast提示，做埋点统计等。
        rootHandler.setGlobalOnCompleteListener(DefaultOnCompleteListener.INSTANCE);
        // 初始化
        Router.init(rootHandler);
    }

    private void initPush() {
        //日志开关
        UMConfigure.setLogEnabled(true);
        //预初始化
        PushHelper.preInit(Utils.getApp());
        //是否同意隐私政策
        boolean isMainProcess = UMUtils.isMainProgress(Utils.getApp());
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            new Thread(() -> PushHelper.init(Utils.getApp())).start();
        } else {
            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
            PushHelper.init(Utils.getApp());
        }
    }

}
