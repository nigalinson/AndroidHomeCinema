package com.sloth.ifilm;

import java.util.List;
import io.reactivex.Observable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/16 18:36
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface FilmManager {

    void openEngine(boolean open);

    Observable<List<Film>> getFilms(FilmQueryParam param);

    Observable<Boolean> addFilm(Film film);

    Observable<Boolean> editFilm(Film film);

    Observable<Boolean> removeFilm(long id);

    Observable<Boolean> removeFilm(Film film);

    Observable<Boolean> cacheFilm(long id);

    Observable<Boolean> cacheFilm(Film film);

    Observable<Boolean> removeFilmCache(long id);

    Observable<Boolean> removeFilmCache(Film film);

    Observable<Boolean> setCachePolicy(@FilmCachePolicy int policy, int concurrency);

}
