package com.sloth.functions.image;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import com.sloth.tools.util.LogUtils;
import java.lang.ref.WeakReference;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/27 10:22
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/27         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class Loader {

    private static final String TAG = Loader.class.getSimpleName();

    protected WeakReference<Context> wkContext;
    protected String url;
    protected String localPath;
    protected int localRes;
    protected Uri uri;
    protected int placeHolder;
    protected int error;
    protected Object transform;
    protected Object transition;
    protected LoadListener loadListener;

    protected boolean skipMemoryCache = false;
    protected Object diskStrategy;

    public Loader(Context context) {
        this.wkContext = new WeakReference<>(context);
    }

    public Loader with(Context context){
        this.wkContext = new WeakReference<>(context);
        return this;
    }

    public Loader load(String url){
        this.url = url;
        return this;
    }

    public Loader load(int res){
        this.localRes = res;
        return this;
    }

    public Loader loadLocal(String localPath){
        this.localPath = localPath;
        return this;
    }


    public Loader load(Uri uri){
        this.uri = uri;
        return this;
    }

    public Loader placeHolder(int resId){
        this.placeHolder = resId;
        return this;
    }

    public Loader error(int resId){
        this.error = resId;
        return this;
    }

    public Loader transition(Object transition){
        validateTransitionType(transition);
        this.transition = transition;
        return this;
    }

    public Loader transform(Object transform){
        validateTransformType(transform);
        this.transform = transform;
        return this;
    }

    public Loader skipMemoryCache(boolean skipMemoryCache){
        this.skipMemoryCache = skipMemoryCache;
        return this;
    }

    public Loader diskStrategy(Object diskStrategy){
        validateDiskStrategyType(diskStrategy);
        this.diskStrategy = diskStrategy;
        return this;
    }

    public Loader listener(LoadListener loadListener){
        this.loadListener = loadListener;
        return this;
    }

    /**
     * 校验形变参数类型准确性（非必要）
     * @param transform
     */
    protected abstract void validateTransformType(Object transform);

    /**
     * 校验动画参数类型准确性（非必要）
     * @param transition
     */
    protected abstract void validateTransitionType(Object transition);

    /**
     * 校验缓存参数类型准确性（非必要）
     * @param diskStrategy
     */
    protected abstract void validateDiskStrategyType(Object diskStrategy);
    /**
     * 校验结果
     * @param res
     */
    protected void validate(boolean res, String failed){
        if(!res){
            LogUtils.e(TAG, failed);
            throw new RuntimeException(failed);
        }else{
            //校验成功
        }
    }

    public abstract void into(ImageView imageView);

    public abstract void into(LoadTarget loadTarget);

}
