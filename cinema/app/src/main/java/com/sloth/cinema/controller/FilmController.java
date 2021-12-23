package com.sloth.cinema.controller;

import com.sankuai.waimai.router.Router;
import com.sloth.cinema.rest.Result;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmManager;
import com.sloth.ifilm.FilmQueryParam;
import com.sloth.ifilm.Strategy;
import com.yanzhenjie.andserver.annotation.DeleteMapping;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.PutMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import java.util.List;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/23 17:58
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/23         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RestController
public class FilmController {

    @GetMapping("/film/list")
    String filmList(@RequestParam("pageIndex") String pageIndex,
                    @RequestParam("pageSize") String pageSize,
                    @RequestParam("name") String name) {
        List<Film> films = Router.getService(FilmManager.class, Strategy._DEFAULT).getFilms(
                new FilmQueryParam.Builder()
                        .setPageIndex(pageIndex)
                        .setPageSize(pageSize)
                        .setName(name)
                        .build());
        for(Film film: films){
            film.getLinks();
        }
        return Result.ok(films).json();
    }

    @PostMapping("/film/add")
    String addFilm(@RequestParam("name") String name) {
        Router.getService(FilmManager.class, Strategy._DEFAULT).addFilm(name);
        return Result.ok().json();
    }

    @PutMapping("/film/crawler")
    String searchFilm(@RequestParam("id") String id) {
        Router.getService(FilmManager.class, Strategy._DEFAULT).searchFilmResources(Long.parseLong(id));
        return Result.ok().json();
    }

    @DeleteMapping("/film/delete")
    String deleteFilm(@RequestParam("id") String id) {
        Router.getService(FilmManager.class, Strategy._DEFAULT).removeFilm(Long.parseLong(id));
        return Result.ok().json();
    }

    @PutMapping("/film/download")
    String downloadFilm(@RequestParam("id") String id) {
        Router.getService(FilmManager.class, Strategy._DEFAULT).downloadFilm(Long.parseLong(id));
        return Result.ok().json();
    }

    @PutMapping("/film/downloadLink")
    String downloadFilmByLink(@RequestParam("id") String id, @RequestParam("linkId") String linkId) {
        Router.getService(FilmManager.class, Strategy._DEFAULT).downloadFilmByLink(Long.parseLong(id), Long.parseLong(linkId));
        return Result.ok().json();
    }

    @DeleteMapping("/film/deleteCache")
    String deleteFilmCache(@RequestParam("id") String id) {
        Router.getService(FilmManager.class, Strategy._DEFAULT).removeFilmCache(Long.parseLong(id));
        return Result.ok().json();
    }

    @DeleteMapping("/film/disableLink")
    String disableLink(@RequestParam("id") String id) {
        Router.getService(FilmManager.class, Strategy._DEFAULT).disableLink(Long.parseLong(id));
        return Result.ok().json();
    }

}
