package com.sloth.functions.http.executor;

import androidx.lifecycle.LifecycleOwner;

import com.sloth.functions.AutoDispose;
import com.sloth.functions.http.options.APIInfos;
import com.sloth.functions.http.options.RetryWithDelay;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.NetworkUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/29 9:52
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/29         Carl            1.0                    1.0
 * Why & What is modified:
 * 传入API observable 和 Observer，生成 disposable
 * 组装请求行为逻辑 如：重试、断线逻辑等
 * attach 支持直接追随宿主生命周期 {@link Builder#attach(LifecycleOwner)}
 * 也可返回disposable让开发人员自行管理生命周期
 */
public class RequestExecutor {
    private static final String TAG = RequestExecutor.class.getSimpleName();

    public static Builder request(Observable observable){
        return new Builder(observable);
    }

    public static class Builder {

        private Observable request;

        //priority 1
        private Observer observer;

        //priority 2
        private Consumer onNext = o->{};
        private Consumer<? extends Throwable> onError = throwable -> {};
        private Action onComplete = ()->{};

        //可以设置生命周期父控件，跟随父控件中断请求
        private LifecycleOwner lifecycleOwner;

        private Builder(Observable request) {
            this.request = request;
        }

        /**
         * 线程切换 设置
         */
        private ObservableTransformer threadTransform = upstream ->
                upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        /**
         * 数据结构适配器 设置
         */
        private ObservableTransformer dataTransform;

        /**
         * 重试操作 设置
         */
        private Function retryFunction;

        /**
         * 是否需要网络
         */
        private boolean needOnline = false;

        public Builder io(){
            this.threadTransform = upstream ->
                    upstream.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
            return this;
        }

        public Builder main(){
            this.threadTransform = upstream ->
                    upstream.subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread());
            return this;
        }

        public Builder retry(Function function){
            this.retryFunction = function;
            return this;
        }

        public Builder retry(int times, int interval){
            this.retryFunction = rxRetry(times, interval);
            return this;
        }

        public Builder retry3Time(){
            this.retryFunction = new RetryWithDelay(3/*重试3次*/, 3000/*重试间隔*/);
            return this;
        }

        public Builder online(){
            this.needOnline = true;
            return this;
        }

        public Builder dataTransform(ObservableTransformer dataTransform){
            this.dataTransform = dataTransform;
            return this;
        }

        /**
         * 跟随生命周期
         * @param lifecycleOwner
         * @return
         */
        public Builder attach(LifecycleOwner lifecycleOwner){
            this.lifecycleOwner = lifecycleOwner;
            return this;
        }

        public Observable buildRequest(){
            if(request == null){
                return Observable.never();
            }

            if(threadTransform != null){
                request = request.compose(threadTransform);
            }

            if(retryFunction != null){
                request = request.retryWhen(retryFunction);
            }

            if(dataTransform != null){
                request = request.compose(dataTransform);
            }

            return request;
        }

        /**
         * 真实执行
         */
        private Disposable realExecute(){
            buildRequest();

            if(onNext == null && onError == null && onComplete == null){
                return disposeShit();
            }

            Disposable disposable = null;

            if(observer != null){
                disposable = request.subscribe(
                        o -> observer.onNext(o),
                        throwable -> observer.onError((Throwable) throwable),
                        () -> observer.onComplete()
                );
            }else{
                disposable = request.subscribe(onNext, onError, onComplete);
            }

            if(lifecycleOwner != null){
                AutoDispose autoDispose = AutoDispose.newIns();
                autoDispose.bind(lifecycleOwner);
                autoDispose.rx(disposable);
            }
            return disposable;
        }

        public Disposable execute(Observer observer){
            this.observer = observer;

            if(needOnline && !checkNetwork(observer)){
                return disposeShit();
            }

            return realExecute();
        }

        public Disposable execute(Consumer consumer){
            this.onNext = consumer;

            if(needOnline && !checkNetwork(null, null)){
                return disposeShit();
            }

            return realExecute();
        }

        public Disposable execute(Consumer next, Consumer error){
            this.onNext = next;
            this.onError = error;

            if(needOnline && !checkNetwork(error, null)){
                return disposeShit();
            }

            return realExecute();
        }

        public Disposable execute(Consumer next, Consumer error, Action complete){
            this.onNext = next;
            this.onError = error;
            this.onComplete = complete;

            if(needOnline && !checkNetwork(error, complete)){
                return disposeShit();
            }

            return realExecute();
        }
    }

    private static boolean checkNetwork(Observer subscriber) {
        if (!NetworkUtils.isAvailable()) {
            if(subscriber != null){
                subscriber.onError(new RuntimeException(APIInfos.NETWORK_ERROR_MSG));
                subscriber.onComplete();
            }
            return false;
        }
        return true;
    }

    private static boolean checkNetwork(Consumer consumer, Action action) {
        if (!NetworkUtils.isAvailable()) {
            if(consumer != null){
                try {
                    consumer.accept(new RuntimeException(APIInfos.NETWORK_ERROR_MSG));
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

            if(action != null){
                try {
                    action.run();
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 生成retry
     * @param times
     * @param interval
     * @return
     */
    private static Function rxRetry(int times, int interval) {
        return new RetryWithDelay(times, interval);
    }

    public static Disposable disposeShit(){
        return new Disposable() {
            @Override
            public void dispose() { }

            @Override
            public boolean isDisposed() { return true; }
        };
    }
}
