package com.sloth.functions.mvp;

import android.content.Context;
import com.sloth.functions.http.executor.RequestExecutor;
import com.sloth.tools.util.NetworkUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/30 13:57
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/30         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class RYBaseModel {
    protected final String TAG = getClass().getSimpleName();

    private Context context;

    /**
     * 用来管理需要自动解绑的RX组件
     */
    private CompositeDisposable mCompositeDisposable;

    public RYBaseModel() { }

    public RYBaseModel(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected void execute(Disposable disposable){
        if(mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }
        if(disposable != null){
            mCompositeDisposable.add(disposable);
        }
    }

    protected <O, T> void executeHTTP(Observable<O> request, Observer<T> apiCallback){
        execute(RequestExecutor.request(request).io().retry3Time().online().execute(apiCallback));
    }

    protected <O, T> void executeHTTP(Observable<O> request, ObservableTransformer<O,T> dataTransform, Observer<T> apiCallback){
        execute(RequestExecutor.request(request).io().retry3Time().online().dataTransform(dataTransform).execute(apiCallback));
    }

    protected <O, T> void execute(Observable<O> request, Observer<T> apiCallback){
        execute(RequestExecutor.request(request).io().execute(apiCallback));
    }

    protected <O, T> void execute(Observable<O> request, ObservableTransformer<O,T> dataTransform, Observer<T> apiCallback){
        execute(RequestExecutor.request(request).io().dataTransform(dataTransform).execute(apiCallback));
    }

    //是否有网络
    protected boolean isOnline() {
        return NetworkUtils.isAvailable();
    }

    public void destroy(){
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }
}
