package com.sloth.cinema.rest;

import com.sloth.tools.util.GsonUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/23 18:15
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/23         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class HttpResult<T> {

    private boolean success;

    private T data;

    public HttpResult(boolean success) {
        this.success = success;
    }

    public HttpResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String json(){
        return GsonUtils.toJson(this);
    }

    public static HttpResult<String> ok(){
        return new HttpResult<>(true, "ok");
    }

    public static <M> HttpResult<M> ok(M data){
        return new HttpResult<M>(true, data);
    }

    public static HttpResult<String> fail(){
        return new HttpResult<>(false, "err");
    }

    public static HttpResult<String> fail(String err){
        return new HttpResult<>(false, err);
    }

}
