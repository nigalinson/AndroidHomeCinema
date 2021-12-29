package com.sloth.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.sloth.client.R;
import com.sloth.functions.adapter.BaseAdapter;
import com.sloth.functions.adapter.OnItemClickListener;
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

    private int checkIndex = -1;

    private OnItemClickListener onItemClickListener;

    public FilmAdapter(Context context) {
        super(context);
    }

    public FilmAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public void check(int index){
        if(checkIndex == -1){
            checkIndex = index;
            notifyItemChanged(checkIndex);
        }else if(checkIndex != index){
            int oldIndex = checkIndex;
            checkIndex = index;
            notifyItemChanged(oldIndex);
            notifyItemChanged(checkIndex);
        }
    }

    public int getCheckedIndex(){
        return checkIndex;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(position == checkIndex){
            holder.itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_choosen));
        }else{
            holder.itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_nothing));
        }
        holder.itemView.setOnClickListener(v -> {
            check(position);
            if(onItemClickListener != null){
                onItemClickListener.OnItemClick(position);
            }
        });
    }
}
