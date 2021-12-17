package com.sloth.functions.http.options;

import com.sloth.tools.util.LogUtils;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      16/8/18 上午10:30
 * Description: 重试机制转换
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 16/8/18      ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {
    private final int mMaxRetries;//重试次数
    private final int mRetryDelayMillis;//每次重试延时时间
    private int mRetryCount;//当前重试次数

    public RetryWithDelay(int maxRetries, int retryDelayMillis) {
        mMaxRetries = maxRetries;
        mRetryDelayMillis = retryDelayMillis;
    }

    @Override
    public Observable<?> apply(@NonNull Observable<? extends Throwable> observable) throws Exception {
        return observable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            Throwable e = throwable;
            while (e.getCause() != null) {
                throwable = e;
                e = e.getCause();
            }
            if (++mRetryCount <= mMaxRetries) {
                LogUtils.d("RetryWithDelay", "服务器超时错误,将在 " + mRetryDelayMillis + " 秒之后重试, 当前重试次数: " + mRetryCount);
                return Observable.timer(mRetryDelayMillis, TimeUnit.MILLISECONDS);
            }
            return Observable.error(throwable);
        });
    }
}
