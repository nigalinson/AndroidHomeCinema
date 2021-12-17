package com.sloth.banner.adapter;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sloth.functions.adapter.BaseViewHolder;
import com.sloth.banner.data.Playable;
import com.sloth.banner.vh.PagerViewHolder;
import com.sloth.banner.vh.PlayerViewHolder;
import com.sloth.functions.viewpager2.widget.ViewPager2;
import com.sloth.tools.util.LogUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/4 17:29
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/4         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class PagerAdapter<VH extends BaseViewHolder<T>, T extends Playable> extends VideoAdapter<VH,T> {

    private static final String TAG = PagerAdapter.class.getSimpleName();

    //触发翻页后，需要下一帧再实际操作
    static final long FIX_REFRESH_PAGE_WHEN_NEXT_FRAME_DELAY = 300L;

    //系统触发的滑动延时会比较早触发， 但滚动还未结束，因此需要等待滚动结束后，再触发翻页
    static final long FIX_SYSTEM_AUTO_SCROLL_REFRESH_PAGE_WHEN_NEXT_FRAME_DELAY = 900L;

    public static class PagerChangedListener extends InfinitePagerChangedListener{
        private static final String TAG = PagerChangedListener.class.getSimpleName();

        private final ViewPager2 viewPager2;
        private final PagerAdapter adapter;

        //快速滑动优化
        private Disposable notifyTool;
        private Disposable delay;
        private final AtomicBoolean fastNotify = new AtomicBoolean(true);

        public PagerChangedListener(ViewPager2 viewPager2, PagerAdapter adp) {
            super(adp);
            this.viewPager2 = viewPager2;
            this.adapter = adp;
        }

        @Override
        protected void onJumpToPage(int pos) {
            LogUtils.d(TAG, "jump to layout page:" + pos);
            if(delay != null && !delay.isDisposed()){
                delay.dispose();
            }
            delay = null;

            delay = Observable.timer(FIX_REFRESH_PAGE_WHEN_NEXT_FRAME_DELAY, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> viewPager2.setCurrentItem(pos, false));
        }

        @Override
        protected void onPageChanged(int pos) {
            LogUtils.d(TAG, "layout page:" + pos);
            //触发翻页后下一帧再刷新数据状态，避开操作高峰
            fastNotify.set(true);
            notifyPageChanged(pos, FIX_REFRESH_PAGE_WHEN_NEXT_FRAME_DELAY);
        }

        private void notifyPageChanged(int pos, long delay) {
            if(notifyTool != null && !notifyTool.isDisposed()){
                notifyTool.dispose();
            }
            notifyTool = null;

            notifyTool = Observable.timer(delay, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        if(fastNotify.get()){
                            //尝试快速触发
                            //是否真实手动滑动 - 系统触发的pageChange会比较快速触发，需要增加一个延时
                            boolean handScroll = (scrollState == ViewPager2.SCROLL_STATE_IDLE);
                            if(handScroll){
                                //快速滑动
                                adapter.onPageChanged(pos);
                            }else{
                                //非快速滑动，延长计时
                                fastNotify.set(false);
                                notifyPageChanged(pos, FIX_SYSTEM_AUTO_SCROLL_REFRESH_PAGE_WHEN_NEXT_FRAME_DELAY);
                            }
                        }else{
                            //低速滑动
                            adapter.onPageChanged(pos);
                        }
                    });
        }

        public void destroy(){
            if(delay != null && !delay.isDisposed()){
                delay.dispose();
            }
            delay = null;

            if(notifyTool != null && !notifyTool.isDisposed()){
                notifyTool.dispose();
            }
            notifyTool = null;
        }
    }

    private final UsingViewHolderQueue<VH, T> usingViewHolderQueue = new UsingViewHolderQueue<VH,T>();
    private int preLoadOffset = 1;

    public PagerAdapter(Context context) {
        super(context);
    }

    public void setPreLoadOffset(int offset){
        this.preLoadOffset = offset;
    }

    @Override
    protected int getInfiniteItemViewType(int layoutIndex, int dataIndex) { return 0; }

    @Override
    protected void onBindInfiniteViewHolder(VH holder, int layoutIndex, int dataIndex) {
        T item = getItemData(dataIndex);
        usingViewHolderQueue.put(layoutIndex, holder, item);
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        if(holder instanceof PlayerViewHolder){
            ((PlayerViewHolder)holder).onClose(null);
        }
        usingViewHolderQueue.remove(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        usingViewHolderQueue.clear();
    }

    private int highLightingIndex = -1;

    public void onPageChanged(int pos){

        //close
        usingViewHolderQueue.close(pos - (preLoadOffset + 1));
        usingViewHolderQueue.close(pos + ((preLoadOffset + 1)));

        //preload
        for(int i = 1; i <= preLoadOffset; i++){
            usingViewHolderQueue.preLoaded(pos - i);
            usingViewHolderQueue.preLoaded(pos + i);
        }

        //loaded
        usingViewHolderQueue.loaded(pos);

        highLightingIndex = pos;
    }

    @Override
    public void startPlayer() {
        super.startPlayer();
        if(highLightingIndex != -1){
            usingViewHolderQueue.loaded(highLightingIndex);
        }
    }

    @Override
    public void stopPlayer() {
        super.stopPlayer();
        if(highLightingIndex != -1){
            usingViewHolderQueue.preLoaded(highLightingIndex);
        }
    }

    public void destroy() {
        usingViewHolderQueue.clear();
    }

    private static class UsingViewHolderQueue<VH extends BaseViewHolder<T>, T extends Playable> {
        private final SparseArray<VH> queue = new SparseArray<>();
        private final SparseArray<T> data = new SparseArray<>();

        public void put(int index, VH item, T d){
            queue.put(index, item);
            data.put(index, d);
        }

        public void loaded(int index){
            if(index < 0){ return; }
            VH item = queue.get(index);
            T d = data.get(index);
            if(item != null && d != null && item instanceof PagerViewHolder){
                ((PagerViewHolder)item).loaded(d);
            }
        }

        public void preLoaded(int index){
            if(index < 0){ return; }
            VH item = queue.get(index);
            T d = data.get(index);
            if(item != null && d != null && item instanceof PagerViewHolder){
                ((PagerViewHolder)item).preLoad(d);
            }
        }

        public void close(int index){
            if(index < 0){ return; }
            VH item = queue.get(index);
            T d = data.get(index);
            if(item != null && item instanceof PagerViewHolder){
                ((PagerViewHolder)item).close(d);
            }
        }

        public void remove(int index){
            if(index == -1){ return; }
            VH item = queue.get(index);
            if(item != null && item instanceof PagerViewHolder){
                ((PagerViewHolder)item).close(data.get(index));
            }
            queue.remove(index);
            data.remove(index);
        }

        public void remove(VH item){
            LogUtils.d(TAG, "queue removed viewholder:" + item.hashCode());
            int index = queue.indexOfValue(item);
            int key = queue.keyAt(index);
            remove(key);
        }

        public void clear(){
            queue.clear();
            data.clear();
        }
    }

}
