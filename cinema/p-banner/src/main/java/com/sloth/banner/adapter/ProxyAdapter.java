package com.sloth.banner.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.sloth.functions.adapter.BaseViewHolder;
import com.sloth.banner.data.Playable;
import com.sloth.banner.transform.DecorTransform;
import com.sloth.functions.viewpager2.widget.ViewPager2;

import java.util.List;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/19 10:42
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ProxyAdapter<VH extends BaseViewHolder<T>, T extends Playable> extends PagerAdapter<VH,T> {

    /**
     * 用户传入的子adp，实际由代理adp来完成无限轮播、翻页等特性
     */
    private BannerAdapter<VH, T> userAdapter;

    /**
     * 动效
     */
    private ViewPager2.PageTransformer transformer;

    public ProxyAdapter(BannerAdapter<VH, T> userAdapter) {
        super(userAdapter.getContext());
        this.userAdapter = userAdapter;
    }

    public void setUserAdapter(BannerAdapter<VH, T> userAdapter) {
        this.userAdapter = userAdapter;
    }

    public BannerAdapter<VH, T> getUserAdapter() {
        return userAdapter;
    }

    public void setTransformer(ViewPager2.PageTransformer transformer){
        this.transformer = transformer;
    }

    @Override
    protected int getInfiniteItemViewType(int layoutIndex, int dataIndex) {
        return userAdapter.getItemViewType(dataIndex);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View originView = mLayoutInflater.inflate(userAdapter.layoutResId(viewType), parent, false);
        FrameLayout decorContainer = null;
        if(transformer != null
                && transformer instanceof DecorTransform
                && (decorContainer = ((DecorTransform)transformer).createDecor(parent.getContext())) != null){
            decorContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            FrameLayout.LayoutParams pm = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            pm.gravity = Gravity.CENTER;
            decorContainer.addView(originView, pm);
            return userAdapter.onCreateViewHolder(viewType, decorContainer);
        }else{
            return userAdapter.onCreateViewHolder(viewType, originView);
        }
    }


    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        userAdapter.onViewRecycled(holder);
    }

    @Override
    protected void onBindInfiniteViewHolder(VH holder, int layoutIndex, int dataIndex) {
        super.onBindInfiniteViewHolder(holder, layoutIndex, dataIndex);
        userAdapter.onBindViewHolder(holder, dataIndex);
    }

    @Override
    public void resetItems(List<T> data) {
        super.resetItems(data);
        if(userAdapter != null){
            userAdapter.resetItems(data);
        }
    }
}
