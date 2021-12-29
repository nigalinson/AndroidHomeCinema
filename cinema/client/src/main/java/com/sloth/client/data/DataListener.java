package com.sloth.client.data;

import com.sloth.functions.mvp.RYBaseView;
import com.sloth.ifilm.Film;

import java.util.List;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/28 13:52
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/28         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface DataListener extends RYBaseView {

    void getFilmListSuccess(List<Film> data);

    void getFilmListFailed(String message);

    void loadMoreFilmListSuccess(List<Film> data);

    void loadMoreFilmListFailed(String message);

    void toast(String message);

}
