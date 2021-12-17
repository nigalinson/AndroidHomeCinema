package com.sloth.functions.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import com.sloth.functions.AutoDispose;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/30 10:59
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/30         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private AutoDispose autoDispose;

    private Unbinder unbinder;

    public BaseViewHolder(View itemView) {
        super(itemView);
        autoDispose = AutoDispose.fromPool(itemView.getContext().getClass().getSimpleName()).bind(itemView.getContext());
        unbinder = ButterKnife.bind(this, itemView);
        autoDispose.butterKnife(unbinder);
    }

    public abstract void bindViewData(T data);

}
