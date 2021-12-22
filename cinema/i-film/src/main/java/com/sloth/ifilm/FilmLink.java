package com.sloth.ifilm;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/22 16:26
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/22         Carl            1.0                    1.0
 * Why & What is modified:
 */
@Entity
public class FilmLink {

    @Unique
    @Id
    private Long id;

    private Long filmId;

    @LinkState
    private int state;

    private String url;

    @Generated(hash = 171660248)
    public FilmLink(Long id, Long filmId, int state, String url) {
        this.id = id;
        this.filmId = filmId;
        this.state = state;
        this.url = url;
    }

    @Generated(hash = 677295579)
    public FilmLink() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public int getState() {
        return state;
    }

    public void setState(@LinkState int state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
