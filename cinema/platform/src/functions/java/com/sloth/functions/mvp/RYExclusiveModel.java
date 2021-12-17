package com.sloth.functions.mvp;

import android.content.Context;

import androidx.annotation.IntRange;
import com.sloth.functions.http.executor.RequestExecutor;
import com.sloth.tools.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/12 14:28
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/12         Carl            1.0                    1.0
 * Why & What is modified:
 * 支持进行排他性请求
 * （同一时间一个Model实例中，只允许存在某KEY的一次请求）
 */
public class RYExclusiveModel extends RYBaseModel {

    private int hit(int key){
        if(key > 30 || key < 0){ return 0; }

        return bloom>>key&1;
    }

    private void put(int key, boolean running){
        if(key > 30 || key < 0){ return; }
        if(running){
            bloom |= (1<<key);
        }else{
            bloom |= (1<<key);
            bloom ^= (1<<key);
        }
    }

    /**
     * 将key记录到32位的 Bloom 参数中，节约内存
     */
    private int bloom = 0;

    public RYExclusiveModel() { }

    public RYExclusiveModel(Context context) {
        super(context);
    }

    protected <O, T> void executeHTTP(@IntRange(from = 0, to = 30) int exclusiveKey, Observable<O> request, Observer<T> apiCallback){
        if(hit(exclusiveKey) == 1){
            LogUtils.d(TAG, "请求：" + exclusiveKey + "已在进行中,不重复请求");
            return;
        }

        execute(RequestExecutor.request(request).io().retry3Time().online().execute(new ExclusiveObserver<T>(apiCallback){
            @Override
            public void onExclusiveStart() {
                put(exclusiveKey, true);
            }

            @Override
            public void onExclusiveEnd() {
                put(exclusiveKey, false);
            }
        }));
    }

    protected <O, T> void executeHTTP(@IntRange(from = 0, to = 30) int exclusiveKey, Observable<O> request, ObservableTransformer<O,T> dataTransform, Observer<T> apiCallback){
        if(hit(exclusiveKey) == 1){
            LogUtils.d(TAG, "请求：" + exclusiveKey + "已在进行中,不重复请求");
            return;
        }

        execute(RequestExecutor.request(request).io().retry3Time().online().dataTransform(dataTransform).execute(new ExclusiveObserver<T>(apiCallback){
            @Override
            public void onExclusiveStart() {
                put(exclusiveKey, true);
            }

            @Override
            public void onExclusiveEnd() {
                put(exclusiveKey, false);
            }
        }));
    }

    protected <O, T> void execute(@IntRange(from = 0, to = 30) int exclusiveKey, Observable<O> request, Observer<T> apiCallback){
        if(hit(exclusiveKey) == 1){
            LogUtils.d(TAG, "请求：" + exclusiveKey + "已在进行中,不重复请求");
            return;
        }

        execute(RequestExecutor.request(request).io().execute(new ExclusiveObserver<T>(apiCallback){
            @Override
            public void onExclusiveStart() {
                put(exclusiveKey, true);
            }

            @Override
            public void onExclusiveEnd() {
                put(exclusiveKey, false);
            }
        }));
    }

    protected <O, T> void execute(@IntRange(from = 0, to = 30) int exclusiveKey, Observable<O> request, ObservableTransformer<O,T> dataTransform, Observer<T> apiCallback){
        if(hit(exclusiveKey) == 1){
            LogUtils.d(TAG, "请求：" + exclusiveKey + "已在进行中,不重复请求");
            return;
        }

        execute(RequestExecutor.request(request).io().dataTransform(dataTransform).execute(new ExclusiveObserver<T>(apiCallback){
            @Override
            public void onExclusiveStart() {
                put(exclusiveKey, true);
            }

            @Override
            public void onExclusiveEnd() {
                put(exclusiveKey, false);
            }
        }));
    }

    @Override
    public void destroy() {
        super.destroy();
        bloom = 0;
    }
}
