package com.sloth.functions.http;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/29 17:15
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class ApiCallback<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }


    @Override
    public void onError(Throwable e) {
        onFailed(e.getMessage());
    }

    protected abstract void onSuccess(T t);

    protected abstract void onFailed(String message);

}
