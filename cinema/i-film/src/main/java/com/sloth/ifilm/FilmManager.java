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

    Observable<Boolean> addFilm(String name);

    Observable<Boolean> searchFilmResources(long filmId);

    Observable<Boolean> removeFilm(long filmId);

    Observable<Boolean> downloadFilm(long filmId);

    Observable<Boolean> downloadFilmByLink(long filmId, long linkId);

    Observable<Boolean> removeFilmCache(long filmId);

    Observable<Boolean> disableLink(long linkId);

}
