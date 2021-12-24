package com.sloth.ifilm;

import java.util.List;

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

    List<Film> getFilms(FilmQueryParam param);

    void addFilm(String name, boolean autoDownload);

    void searchFilmResources(long filmId);

    void searchAllFilmResources();

    void removeFilm(long filmId);

    void downloadFilm(long filmId);

    void downloadFilmByLink(long filmId, long linkId);

    void removeFilmCache(long filmId);

    void disableLink(long linkId);

    void stopAll();

}
