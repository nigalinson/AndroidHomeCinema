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
public class Result <T> {

    private boolean success;

    private T data;

    public Result(boolean success) {
        this.success = success;
    }

    public Result(boolean success, T data) {
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

    public static Result<String> ok(){
        return new Result<>(true, "ok");
    }

    public static <M> Result<M> ok(M data){
        return new Result<M>(true, data);
    }

    public static Result<String> fail(){
        return new Result<>(false, "err");
    }

    public static Result<String> fail(String err){
        return new Result<>(false, err);
    }

}
