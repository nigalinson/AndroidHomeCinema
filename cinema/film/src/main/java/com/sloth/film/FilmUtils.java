package com.sloth.film;

import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmLink;
import com.sloth.ifilm.LinkState;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/23 10:40
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/23         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class FilmUtils {

    public static FilmLink findFirstUsableLink(Film film){
        if(film.getLinks() != null && film.getLinks().size() > 0){
            for(FilmLink link: film.getLinks()){
                if(link.getState() == LinkState.WAIT){
                    return link;
                }
            }
        }
        return null;
    }

    public static FilmLink findLink(Film film, Long linkId){
        if(film.getLinks() != null && film.getLinks().size() > 0){
            for(FilmLink link: film.getLinks()){
                if(link.getId().equals(linkId)){
                    return link;
                }
            }
        }
        return null;
    }
}
