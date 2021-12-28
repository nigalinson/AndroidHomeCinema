package com.sloth.client.data;

import android.content.Context;
import com.sloth.client.http.ApiStore;
import com.sloth.client.http.HttpResult;
import com.sloth.functions.http.API;
import com.sloth.functions.http.ApiCallback;
import com.sloth.functions.http.ApiHost;
import com.sloth.functions.http.executor.RequestExecutor;
import com.sloth.functions.mvp.RYBaseCase;
import com.sloth.ifilm.Film;
import java.util.List;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/28 13:51
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/28         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class Repository extends RYBaseCase<DataListener> {

    private ApiStore apiStore;

    public Repository(DataListener dataListener) {
        super(dataListener);
    }

    public Repository(Context context, DataListener dataListener) {
        super(context, dataListener);
    }

    public void setHost(Context context, String url){
        apiStore = API.getInstance().reCreate(new ApiHost(url), ApiStore.class);
        setContext(context);
    }

    public void getFilmList(int pageIndex, int pageSize, String name){
        RequestExecutor.request(apiStore.queryFilmList(String.valueOf(pageIndex), String.valueOf(pageSize), name))
                .online().execute(new ApiCallback<HttpResult<List<Film>>>() {
                    @Override
                    public void onComplete() { }

                    @Override
                    protected void onSuccess(HttpResult<List<Film>> httpResult) {
                        if(httpResult.isSuccess()){
                            mView.getFilmListSuccess(httpResult.getData());
                        }else{
                            mView.getFilmListFailed("failed");
                        }
                    }

                    @Override
                    protected void onFailed(String message) {
                        mView.getFilmListFailed(message);
                    }
                });
    }

    public void loadMoreFilmList(int pageIndex, int pageSize, String name){
        RequestExecutor.request(apiStore.queryFilmList(String.valueOf(pageIndex), String.valueOf(pageSize), name))
                .online().execute(new ApiCallback<HttpResult<List<Film>>>() {
            @Override
            public void onComplete() { }

            @Override
            protected void onSuccess(HttpResult<List<Film>> httpResult) {
                if(httpResult.isSuccess()){
                    mView.loadMoreFilmListSuccess(httpResult.getData());
                }else{
                    mView.loadMoreFilmListFailed("failed");
                }
            }

            @Override
            protected void onFailed(String message) {
                mView.loadMoreFilmListFailed(message);
            }
        });
    }

}
