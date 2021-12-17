package com.sloth.functions.image.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.sloth.functions.image.LoadTarget;
import com.sloth.functions.image.Loader;
import com.sloth.functions.image.RYImageLoader;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.LogUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.Arrays;

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
 * Picasso图片加载器
 */
public class PicassoLoader extends Loader {

    private static final String TAG = PicassoLoader.class.getSimpleName();

    public PicassoLoader(Context context) {
        super(context);
    }

    @Override
    protected void validateTransformType(Object transform) {
        validate(transform instanceof Transformation, "图片加载器的形变类型校验失败");
    }

    @Override
    protected void validateTransitionType(Object transition) {
        //todo 校验动画类合法性
    }

    @Override
    protected void validateDiskStrategyType(Object diskStrategy) {
        validate((diskStrategy instanceof MemoryPolicy) || (diskStrategy instanceof MemoryPolicy[]), "缓存策略类型校验失败");
    }

    @Override
    public void into(ImageView imageView) {
        if(imageView == null){
            LogUtils.i(TAG, "图片控件已销毁，不继续加载图片");
            return ;
        }

        RequestCreator requestCreator = buildCreator();
        if(requestCreator == null){ return; }

        if(loadListener != null){
            requestCreator.into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setImageBitmap(bitmap);
                    loadListener.loadSuccess(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    imageView.setImageDrawable(errorDrawable);
                    loadListener.loadFailed("加载失败");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    imageView.setImageDrawable(placeHolderDrawable);
                }
            });
        }else{
            requestCreator.into(imageView);
        }
    }

    @Override
    public void into(LoadTarget loadTarget) {
        if(loadTarget == null){
            LogUtils.i(TAG, "容器已销毁，不继续加载图片");
            return ;
        }

        RequestCreator requestCreator = buildCreator();
        if(requestCreator == null){ return; }

        requestCreator.into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                loadTarget.loadSuccess(bitmap);
                if(loadListener != null){
                    loadListener.loadSuccess(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                loadTarget.loadFailed(errorDrawable);
                if(loadListener != null){
                    loadListener.loadFailed("加载失败");
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                loadTarget.prepared(placeHolderDrawable);
            }
        });
    }

    private RequestCreator buildCreator(){
        if(wkContext.get() == null){
            LogUtils.i(TAG, "挂载父容器已销毁，不继续加载图片");
            return null;
        }

        Picasso picasso = Picasso.with(wkContext.get());
        RequestCreator requestCreator;
        if(exist(localPath)){
            //本地图片优先级最高
            requestCreator = picasso.load(new File(localPath));
        }else if(!nil(localRes)){
            //resId
            requestCreator = picasso.load(localRes);
        }else if(uri != null){
            //uri
            requestCreator = picasso.load(uri);
        }else if(!nil(url)){
            //网络图片
            requestCreator = picasso.load(url);
        }else if(!nil(error)){
            //加载错误图片
            requestCreator = picasso.load(error);
        }else if(!nil(placeHolder)){
            //加载中图片
            requestCreator = picasso.load(placeHolder);
        }else{
            LogUtils.e(TAG, "未设置任何资源，可能导致图片空白！！！");
            requestCreator = picasso.load("");
        }

        if(!nil(placeHolder)){
            requestCreator = requestCreator.placeholder(placeHolder);
        }

        if(!nil(placeHolder)){
            requestCreator = requestCreator.error(placeHolder);
        }

        if(transform != null){
            //前面代码已经校验过参数合法性，可放心强转
            requestCreator = requestCreator.transform((Transformation) transform);
        }

        if( diskStrategy != null){
            //前面代码已经校验过参数合法性，可放心强转
            if(diskStrategy instanceof MemoryPolicy){
                requestCreator = requestCreator.memoryPolicy((MemoryPolicy) diskStrategy);
            }else if(diskStrategy instanceof MemoryPolicy[]){
                MemoryPolicy[] tmp = (MemoryPolicy[]) diskStrategy;
                requestCreator = requestCreator.memoryPolicy(tmp[0], Arrays.copyOfRange(tmp, 1, tmp.length));
            }
        }else{
            //未设置自定义策略，判断默认策略
            if(RYImageLoader.DefaultOptions.diskStrategy == RYImageLoader.DefaultOptions.DISK_STRATEGY_NONE){
                requestCreator = requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
            }
        }

        if(transition != null){
            //前面代码已经校验过参数合法性，可放心强转
            //todo picasso实现动画效果
        }

        return requestCreator;
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
