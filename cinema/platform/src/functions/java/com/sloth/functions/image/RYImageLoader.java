package com.sloth.functions.image;

import android.content.Context;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/27 10:43
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/27         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class RYImageLoader {

    //默认Glide加载器
    private static LoaderType defaultLoaderType = LoaderType.Glide;

    public static void setDefault(LoaderType loaderType){
        RYImageLoader.defaultLoaderType = loaderType;
    }

    public static Loader with(Context context){
        return LoaderFactory.get(context,defaultLoaderType);
    }

    public static Loader with(Context context, LoaderType loaderType){
        RYImageLoader.defaultLoaderType = loaderType;
        return with(context);
    }

    public static class DefaultOptions {
        public static final int DISK_STRATEGY_AUTO = 1;
        public static final int DISK_STRATEGY_NONE = 0;

        public static int diskStrategy = DISK_STRATEGY_AUTO;
    }

}
