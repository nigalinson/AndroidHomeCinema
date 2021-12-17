package com.sloth.functions.http;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/28 19:22
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/28         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class BaseApiModule {

    //默认超时时间
    private static final long DEFAULT_TIMEOUT = 61;

    //默认缓存区大小
    public static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;//10MB

    /**
     * 通用拦截器
     */
    private final List<Interceptor> interceptors = new ArrayList<>();

    /**
     * 缓存配置
     */
    private final Cache cacheConfig;

    protected BaseApiModule() {
        initInterceptors(interceptors);
        cacheConfig = initCacheConfig(cacheSize());
    }

    protected abstract void initInterceptors(List<Interceptor> interceptors);

    protected abstract Cache initCacheConfig(long cacheSize);

    public abstract String apiHost();

    public List<Interceptor> getInterceptors(){
        return interceptors;
    }

    public Cache getCacheConfig() {
        return cacheConfig;
    }

    public long connectTimeout(){
        return DEFAULT_TIMEOUT;
    }

    public long readTimeout(){
        return DEFAULT_TIMEOUT;
    }

    protected long cacheSize(){
        return HTTP_RESPONSE_DISK_CACHE_MAX_SIZE;
    }
}
