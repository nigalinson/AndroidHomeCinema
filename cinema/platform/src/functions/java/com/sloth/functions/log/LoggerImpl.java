package com.sloth.functions.log;

import com.orhanobut.logger.Logger;
import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.log.Log;
import com.sloth.tools.util.GsonUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/22 16:31
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/22         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = Log.class, key = Strategies.LogEngine.LOGGER, singleton = true, defaultImpl = true)
public class LoggerImpl implements Log {

    @RouterProvider
    public static LoggerImpl provideLogger(){
        LoggerConfig loggerConfig = Router.getService(LoggerConfig.class);
        LoggerImpl logger = new LoggerImpl();
        if(loggerConfig != null){
            logger.setLevel(loggerConfig.level());
        }
        return logger;
    }


    public LoggerImpl() { }

    private int level = Log.E;

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void d(String tag, String msg) {
        if(level <= Log.D){
            Logger.t(tag).d(msg);
        }
    }

    @Override
    public void v(String tag, String msg) {
        if(level <= Log.V){
            Logger.t(tag).v(msg);
        }
    }

    @Override
    public void i(String tag, String msg) {
        if(level <= Log.I){
            Logger.t(tag).i(msg);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if(level <= Log.W){
            Logger.t(tag).w(msg);
        }
    }

    @Override
    public void e(String tag, String msg) {
        if(level <= Log.E){
            Logger.t(tag).e(msg);
        }
    }

    @Override
    public void json(String tag, Object obj) {
        if(level <= Log.I){
            Logger.t(tag).json(GsonUtils.toJson(obj));
        }
    }

    @Override
    public void flush(boolean sync) { }

    @Override
    public void exit() { }
}
