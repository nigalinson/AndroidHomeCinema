package com.sloth.client.http;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/28 13:29
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/28         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface ApiStore {

    @GET("/film/list")
    Observable<HttpResult<String>> queryFilmList(
            @Query("pageIndex") String pageIndex,
            @Query("pageSize") String pageSize
    );

    @GET("/film/list")
    Observable<HttpResult<String>> queryFilmList(
            @Query("pageIndex") String pageIndex,
            @Query("pageSize") String pageSize,
            @Query("name") String name
    );

    @POST("/film/add")
    Observable<HttpResult<String>> addFilm(
            @Query("name") String name,
            @Query("autoDownload") String autoDownload
    );

    @PUT("/film/crawler")
    Observable<HttpResult<String>> crawlerFilm(@Query("id") String id);

    @PUT("/film/crawlerAll")
    Observable<HttpResult<String>> crawlerAllFilm();

    @DELETE("/film/delete")
    Observable<HttpResult<String>> deleteFilm(@Query("id") String id);

    @PUT("/film/download")
    Observable<HttpResult<String>> downloadFilm(@Query("id") String id);

    @PUT("/film/downloadLink")
    Observable<HttpResult<String>> downloadFilmLink(@Query("id") String id, @Query("linkId") String linkId);

    @DELETE("/film/deleteCache")
    Observable<HttpResult<String>> downloadFilmCache(@Query("id") String id);

    @DELETE("/film/disableLink")
    Observable<HttpResult<String>> disableLink(@Query("id") String id);

    @PUT("/film/stopAll")
    Observable<HttpResult<String>> stopAll();

}
