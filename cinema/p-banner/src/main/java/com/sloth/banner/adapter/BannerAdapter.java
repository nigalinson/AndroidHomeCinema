package com.sloth.banner.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.sloth.functions.adapter.BaseAdapter;
import com.sloth.functions.adapter.BaseViewHolder;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/19 15:41
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class BannerAdapter<VH extends BaseViewHolder<T>, T> extends BaseAdapter<VH, T> {

    public BannerAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int resId = layoutResId(viewType);
        return onCreateViewHolder(viewType, (resId != 0 && resId != -1) ? mLayoutInflater.inflate(resId, parent, false) : parent);
    }

    protected abstract int layoutResId(int viewType);

    public abstract VH onCreateViewHolder(int viewType, @NonNull View itemView);

}
