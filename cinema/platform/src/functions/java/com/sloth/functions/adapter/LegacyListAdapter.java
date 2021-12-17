package com.sloth.functions.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      14-11-22 14:49
 * Description: ListView/GridView Adapter 基类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 14-11-22      ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public abstract class LegacyListAdapter<T> extends BaseAdapter {
    protected final Context mContext;
    protected final LayoutInflater mLayoutInflater;
    protected List<T> mListData = new ArrayList<>();

    public LegacyListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setListData(List<T> mListData) {
        this.mListData = mListData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public T getItem(int position) {
        return mListData == null ? null : mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup viewGroup);

    /**
     * 批量添加记录
     *
     * @param data     需要加入的数据结构
     * @param position 插入位置
     */
    public void addItems(List<T> data, int position) {
        if (position <= mListData.size() && data != null && data.size() > 0) {
            mListData.addAll(position, data);
            notifyDataSetChanged();
        }
    }

    /**
     * 批量添加记录
     *
     * @param data 需要加入的数据结构
     */
    public void addItems(List<T> data) {
        addItems(data, mListData.size());
    }

    /**
     * 移除所有记录
     */
    public void clearItems() {
        int size = mListData.size();
        if (size > 0) {
            mListData.clear();
            notifyDataSetChanged();
        }
    }
}
