package com.sloth.client.adapter;

import android.view.View;
import android.widget.ImageView;
import com.sloth.client.R;
import com.sloth.functions.adapter.BaseViewHolder;
import com.sloth.functions.image.RYImageLoader;
import com.sloth.ifilm.Film;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/28 15:31
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/28         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class FilmViewHolder extends BaseViewHolder<Film> {

    private ImageView cover;

    public FilmViewHolder(View itemView) {
        super(itemView);
        cover = itemView.findViewById(R.id.cover);
    }

    @Override
    public void bindViewData(Film data) {
        RYImageLoader.with(cover.getContext()).load(data.getImg()).into(cover);
    }
}
