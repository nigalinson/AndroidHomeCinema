package com.sloth.functions.image.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.sloth.functions.image.LoadTarget;
import com.sloth.functions.image.Loader;
import com.sloth.functions.image.RYImageLoader;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.LogUtils;

import java.io.File;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/27 10:47
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/27         Carl            1.0                    1.0
 * Why & What is modified:
 * Glide图片加载器
 */
public class GlideLoader extends Loader {

    private static final String TAG = GlideLoader.class.getSimpleName();

    public GlideLoader(Context context) {
        super(context);
    }

    @Override
    protected void validateTransformType(Object transform) {
        validate(transform instanceof Transformation, "图片加载器的形变类型校验失败");
    }

    @Override
    protected void validateTransitionType(Object transition) {
        validate(transition instanceof TransitionOptions, "图片加载器的动画类型校验失败");
    }

    @Override
    protected void validateDiskStrategyType(Object diskStrategy) {
        validate(diskStrategy instanceof DiskCacheStrategy, "图片缓存类型校验失败");
    }

    @Override
    public void into(ImageView imageView) {

        if(imageView == null){
            LogUtils.i(TAG, "图片控件已销毁，不继续加载图片");
            return;
        }

        RequestBuilder requestBuilder = buildRequest();

        if(requestBuilder == null){ return;}

        requestBuilder.into(imageView).clearOnDetach();
    }

    @Override
    public void into(LoadTarget loadTarget) {
        if(loadTarget == null){
            LogUtils.i(TAG, "容器已销毁，不继续加载图片");
            return;
        }

        RequestBuilder requestBuilder = buildRequest();

        if(requestBuilder == null){ return;}

        requestBuilder.into(new Target<Drawable>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                loadTarget.prepared(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                loadTarget.loadFailed(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                loadTarget.loadSuccess(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) { }

            @Override
            public void getSize(@NonNull SizeReadyCallback cb) { }

            @Override
            public void removeCallback(@NonNull SizeReadyCallback cb) { }

            @Override
            public void setRequest(@Nullable Request request) { }

            @Nullable
            @Override
            public Request getRequest() { return null; }

            @Override
            public void onStart() { }

            @Override
            public void onStop() { }

            @Override
            public void onDestroy() { }
        });
    }

    private RequestBuilder buildRequest(){
        if(wkContext.get() == null){
            LogUtils.i(TAG, "挂载父容器已销毁，不继续加载图片");
            return null;
        }

        RequestManager requestManager = Glide.with(wkContext.get());

        RequestBuilder<Drawable> requestBuilder;
        if(exist(localPath)){
            //本地图片优先级最高
            requestBuilder = requestManager.load(new File(localPath));
        }else if(!nil(localRes)){
            //resId
            requestBuilder = requestManager.load(localRes);
        }else if(uri != null){
            //uri
            requestBuilder = requestManager.load(uri);
        }else if(!nil(url)){
            //网络图片
            requestBuilder = requestManager.load(url);
        }else if(!nil(error)){
            //加载错误图片
            requestBuilder = requestManager.load(error);
        }else if(!nil(placeHolder)){
            //加载中图片
            requestBuilder = requestManager.load(placeHolder);
        }else{
            LogUtils.e(TAG, "未设置任何资源，可能导致图片空白！！！");
            requestBuilder = requestManager.load("");
        }

        if(!nil(placeHolder)){
            requestBuilder = requestBuilder.placeholder(placeHolder);
        }

        if(!nil(placeHolder)){
            requestBuilder = requestBuilder.error(error);
        }

        if(transform != null){
            //前面代码已经校验过参数合法性，可放心强转
            requestBuilder = requestBuilder.transform((Transformation)transform);
        }

        if(transition != null){
            //前面代码已经校验过参数合法性，可放心强转
            requestBuilder = requestBuilder.transition((TransitionOptions)transition);
        }

        if( diskStrategy != null){
            //前面代码已经校验过参数合法性，可放心强转
            requestBuilder = requestBuilder.diskCacheStrategy((DiskCacheStrategy) diskStrategy);
        }else{
            //未设置自定义策略，判断默认策略
            if(RYImageLoader.DefaultOptions.diskStrategy == RYImageLoader.DefaultOptions.DISK_STRATEGY_NONE){
                requestBuilder = requestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE);
            }
        }

        if(skipMemoryCache){
            requestBuilder.skipMemoryCache(true);
        }

        if(loadListener != null){
            requestBuilder = requestBuilder.listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loadListener.loadFailed(e != null ? e.getMessage() : "加载失败");
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadListener.loadSuccess(resource);
                    return false;
                }
            });
        }

        return requestBuilder;
    }

    private boolean nil(String txt){
        return (null == txt || "".equals(txt));
    }

    private boolean nil(int res){
        return (-1 == res || 0 == res);
    }

    private boolean exist(String path){
        return FileUtils.isFileExists(path);
    }
}
