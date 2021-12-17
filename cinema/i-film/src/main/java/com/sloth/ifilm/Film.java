package com.sloth.ifilm;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/16 18:42
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
@Entity
public class Film {

    @Id
    @Unique
    private Long id;

    private String name;

    private String cover;

    private String description;

    private String onlineUrl;

    @FilmState
    private Integer state;

    private Long createTime;

    @Generated(hash = 1958070794)
    public Film(Long id, String name, String cover, String description, String onlineUrl,
            Integer state, Long createTime) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.description = description;
        this.onlineUrl = onlineUrl;
        this.state = state;
        this.createTime = createTime;
    }

    @Generated(hash = 1658281933)
    public Film() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOnlineUrl() {
        return onlineUrl;
    }

    public void setOnlineUrl(String onlineUrl) {
        this.onlineUrl = onlineUrl;
    }
    
    public Integer getState() {
        return state;
    }

    public void setState(@FilmState Integer state) {
        this.state = state;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
