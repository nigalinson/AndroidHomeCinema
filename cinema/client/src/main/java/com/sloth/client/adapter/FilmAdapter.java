package com.sloth.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.sloth.client.R;
import com.sloth.functions.adapter.BaseAdapter;
import com.sloth.ifilm.Film;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/28 15:30
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/28         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class FilmAdapter extends BaseAdapter<FilmViewHolder, Film> {

    public FilmAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> {

        });
    }
}
