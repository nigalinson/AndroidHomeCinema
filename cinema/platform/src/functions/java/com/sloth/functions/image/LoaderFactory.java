package com.sloth.functions.image;

import android.content.Context;

import com.sloth.functions.image.impl.GlideLoader;
import com.sloth.functions.image.impl.PicassoLoader;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/27 13:27
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/27         Carl            1.0                    1.0
 * Why & What is modified:
 * 因为需要保留实现类的泛型，因此返回具体的实现类对象，由ImageLoad自动完成实例化
 */
public class LoaderFactory {

    public static Loader get(Context context, LoaderType type){
        if(LoaderType.Glide.val.equals(type.val)){
            //glide
            return new GlideLoader(context);
        }else if(LoaderType.Picasso.val.equals(type.val)){
            //picasso
            return new PicassoLoader(context);
        }else if(LoaderType.Volley.val.equals(type.val)){
            //volley
            throw new RuntimeException("暂未实现Volley图片加载器");
        }else{
            //默认使用Glide
            return new GlideLoader(context);
        }

    }
}
