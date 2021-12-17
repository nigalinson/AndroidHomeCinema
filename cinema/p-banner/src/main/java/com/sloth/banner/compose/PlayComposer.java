package com.sloth.banner.compose;

import androidx.recyclerview.widget.RecyclerView;

import com.sloth.banner.XBanner;
import com.sloth.banner.adapter.PageChangeCallbackFix;
import com.sloth.banner.adapter.PagerAdapter;
import com.sloth.banner.data.Playable;
import com.sloth.banner.vh.VideoStatusListener;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.MediaUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/15 14:30
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/15         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class PlayComposer implements VideoStatusListener {

    private static final String TAG = PlayComposer.class.getSimpleName();

    private XBanner banner;

    private long defaultLoopDuration = 10000L;

    private boolean valid;

    private Disposable autoPlayDispose;

    private int rvState;

    private final PageChangeCallbackFix onPageChangeCallback = new PageChangeCallbackFix() {

        @Override
        protected void onPageSelectedFix(int position) {
            //实际翻页产生后，重置时间
            reStart();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            rvState = state;
        }
    };

    public PlayComposer(XBanner banner) {
        this.banner = banner;
        banner.addOnPageChangeListener(onPageChangeCallback);
    }

    public PlayComposer setDefaultLoopDuration(long defaultLoopDuration) {
        this.defaultLoopDuration = defaultLoopDuration;
        return this;
    }

    public void reStart() {
        long nowInterval = getCurrentItemInterval();
        cancelCounter();
        if(banner == null){ return; }
        scheduleCounter(nowInterval);
        resume();
    }

    public void resume() {
        valid = true;
    }

    public void pause() {
        valid = false;
    }

    public void stop() {
        cancelCounter();
    }

    public void destroy() {
        if(banner != null){
            banner.setVideoStatusListener(null);
            banner.removePageChangeListener(onPageChangeCallback);
            banner = null;
        }
        stop();
    }

    private long getCurrentItemInterval() {

        if (banner == null || banner.getAdapter() == null || banner.getAdapter().getItemCount() <= 0) {
            LogUtils.e(TAG, "准备开始切换倒计时，但检测到没有数据");
            return defaultLoopDuration;
        }

        int layoutPos = banner.getCurrentItem();
        if (layoutPos < 0) {
            LogUtils.e(TAG, "无效的当前页码");
            return defaultLoopDuration;
        }

        int dataIndex = ((PagerAdapter) banner.getAdapter()).dataIndex(layoutPos);
        Playable item = (Playable) ((PagerAdapter) banner.getAdapter()).getItemData(dataIndex);

        if (item == null) {
            LogUtils.e(TAG, "当前页面数据为空");
            return defaultLoopDuration;
        }

        if (isVideoType(item)) {
            //视频按照自身时长切换
            long mediaDuration = 0;
            if (FileUtils.isFileExists(item.localPath())) {
                mediaDuration = MediaUtils.getDefaultEngine().duration(item.localPath());
            } else {
            }
            return mediaDuration > 0 ? mediaDuration : defaultLoopDuration;
        } else {
            //其他按照配置时间播放
            long packLoopingDuration = item.playDuration();
            return packLoopingDuration > 0 ? packLoopingDuration : defaultLoopDuration;
        }
    }

    @Override
    public void onPrepared(int pos) { }

    @Override
    public void onEnd(int pos) { }

    //播放异常时自动下一页
    @Override
    public void onError(int pos) {
        next();
    }

    private boolean isVideoType(Playable item) {
        return item.mediaType() == Playable.MediaType.Video.type;
    }

    private void next() {
        if (banner != null) {
            banner.toNextPage(600);
        }
    }

    private void cancelCounter() {
        if (autoPlayDispose != null && !autoPlayDispose.isDisposed()) {
            autoPlayDispose.dispose();
        }
        autoPlayDispose = null;
    }

    private void scheduleCounter(long delay) {
        cancelCounter();
        autoPlayDispose = Observable.interval(delay, delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (validateCouldPaging()) {
                        next();
                    } else {
                        //重新计时
                        if(banner != null){
                            scheduleCounter(delay);
                        }
                    }
                });
    }

    private boolean validateCouldPaging() {
        if (rvState != RecyclerView.SCROLL_STATE_IDLE) {
            LogUtils.e(TAG, "惯性滚动中，不触发自动滚动, cancel.>>>");
            return false;
        }

        if (!valid) {
            LogUtils.e(TAG, "pause中，不触发自动滚动，顺延到下次, cancel.>>>");
            return false;
        }
        return true;
    }


}
