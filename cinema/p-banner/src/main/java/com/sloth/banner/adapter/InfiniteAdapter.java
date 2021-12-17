package com.sloth.banner.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import com.sloth.functions.adapter.BaseAdapter;
import com.sloth.functions.adapter.BaseViewHolder;
import com.sloth.tools.util.LogUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/18 21:12
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class InfiniteAdapter<VH extends BaseViewHolder<T>, T> extends BaseAdapter<VH,T> {

    private static final String TAG = InfiniteAdapter.class.getSimpleName();

    private static final int MAX_THRESHOLD = Integer.MAX_VALUE - 100;

    public InfiniteAdapter(Context context) {
        super(context);
    }

    public int dataIndex(int pos){
        if(getDataCount() == 0){
            return 0;
        }
        return pos % getDataCount();
    }

    @Override
    public int getItemViewType(int position) {
        return getInfiniteItemViewType(position, dataIndex(position));
    }

    protected abstract int getInfiniteItemViewType(int layoutIndex, int dataIndex);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        int real = dataIndex(position);
        super.onBindViewHolder(holder, real);
        onBindInfiniteViewHolder(holder, position, real);
    }

    protected abstract void onBindInfiniteViewHolder(VH holder, int layoutIndex, int dataIndex);

    @Override
    public int getItemCount() {
        if(getDataCount() == 0){
            return 0;
        }
        return Integer.MAX_VALUE;
    }

    static abstract class InfinitePagerChangedListener extends PageChangeCallbackFix {

        private static final String TAG = InfinitePagerChangedListener.class.getSimpleName();

        private final InfiniteAdapter adapter;

        protected int scrollState;

        public InfinitePagerChangedListener(InfiniteAdapter adp) {
            this.adapter = adp;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            scrollState = state;
        }

        @Override
        protected void onPageSelectedFix(int position) {
            LogUtils.d(TAG, "layout pos:" + position);
            if(position == MAX_THRESHOLD){
                onJumpToPage(adapter.getDataCount() * 10 + (MAX_THRESHOLD % adapter.getDataCount()));
            }else if(position == adapter.getDataCount()){
                onJumpToPage(adapter.getDataCount() * 10);
            }else{
                onPageChanged(position);
            }
        }

        protected abstract void onJumpToPage(int pos);

        protected abstract void onPageChanged(int pos);

    }

}
