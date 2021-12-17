package com.sloth.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.sloth.banner.adapter.BannerAdapter;
import com.sloth.banner.adapter.PagerAdapter;
import com.sloth.banner.adapter.ProxyAdapter;
import com.sloth.banner.compose.PlayComposer;
import com.sloth.banner.data.Playable;
import com.sloth.banner.transform.Orientable;
import com.sloth.banner.vh.VideoStatusListener;
import com.sloth.functions.AutoDispose;
import com.sloth.functions.adapter.BaseViewHolder;
import com.sloth.functions.viewpager2.widget.ViewPager2;
import com.sloth.player.PlayerConst;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/18 17:17
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class XBanner<T extends Playable> extends FrameLayout {

    public static final String TAG = XBanner.class.getSimpleName();

    public static AutoDispose autoDispose(){
        return AutoDispose.fromPool(TAG);
    }

    private ViewPager2 viewPager2;

    private ViewPager2.PageTransformer pageTransformer;

    private ProxyAdapter<BaseViewHolder<T>, T> proxyAdapter;

    private PagerAdapter.PagerChangedListener proxyPagerChangedListener;

    private final List<ViewPager2.OnPageChangeCallback> pageChangeCallbacks = new ArrayList<>();

    private final List<T> data = new ArrayList<>();

    private boolean disAllowTouch = false;

    private VideoStatusListener videoStatusListener;

    private PlayComposer mPlayComposer;

    private boolean videoLoop = false;

    private int playerType = PlayerConst.PlayerType.EXO.type;

    private int scaleType = PlayerConst.ScaleType.FIT_XY.code;

    private int surfaceType = PlayerConst.SurfaceType.Surface.code;

    private boolean autoScalePlayerViewPort = false;

    private boolean snapshot = true;

    private float snapshotQuality = 0.3f;

    private int preloadOffset = 1;

    private Disposable autoClearCacheLoop;

    public XBanner(@NonNull Context context) {
        this(context, null, -1);
    }

    public XBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public XBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        autoDispose().bind(context);
        viewPager2 = new ViewPager2(context);
        viewPager2.setOffscreenPageLimit(1);
        addView(viewPager2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setClipChildren(false);
        setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setClipToPadding(false);
    }

    public void addOnPageChangeListener(ViewPager2.OnPageChangeCallback lis){
        if(!pageChangeCallbacks.contains(lis)){
            pageChangeCallbacks.add(lis);
        }
    }

    public void removePageChangeListener(ViewPager2.OnPageChangeCallback lis){
        pageChangeCallbacks.remove(lis);
    }

    public void clearPageChangeListener(){
        pageChangeCallbacks.clear();
    }

    public void setOffscreenPageLimit(int limit){
        viewPager2.setOffscreenPageLimit(limit);
    }

    public void setAdapter(BannerAdapter<BaseViewHolder<T>, T> adapter){
        if(proxyAdapter == null){
            proxyAdapter = new ProxyAdapter<BaseViewHolder<T>, T>(adapter);
            proxyAdapter.setVideoPlayerType(playerType);
            proxyAdapter.setVideoLoop(videoLoop);
            proxyAdapter.setVideoScaleType(scaleType);
            proxyAdapter.setSurfaceType(surfaceType);
            proxyAdapter.setPreLoadOffset(preloadOffset);
            proxyAdapter.autoScalePlayerViewPort(autoScalePlayerViewPort);
            proxyAdapter.setSnapshot(snapshot, snapshotQuality);
            if(pageTransformer != null){
                proxyAdapter.setTransformer(pageTransformer);
            }
        }else{
            proxyAdapter.setUserAdapter(adapter);
            proxyAdapter.resetItems(adapter.getDataList());
        }

        if(proxyPagerChangedListener == null){
            proxyPagerChangedListener = new PagerAdapter.PagerChangedListener(viewPager2, proxyAdapter){
                @Override
                protected void onPageChanged(int pos) {
                    super.onPageChanged(pos);
                    for(ViewPager2.OnPageChangeCallback lis: pageChangeCallbacks){
                        lis.onPageSelected(pos);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                    for(ViewPager2.OnPageChangeCallback lis: pageChangeCallbacks){
                        lis.onPageScrollStateChanged(state);
                    }
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    for(ViewPager2.OnPageChangeCallback lis: pageChangeCallbacks){
                        lis.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                }
            };
            viewPager2.registerOnPageChangeCallback(proxyPagerChangedListener);
        }

        if(proxyAdapter != null){
            proxyAdapter.setVideoStatusListener(videoStatusListener);
        }
    }

    @Nullable
    public BannerAdapter<BaseViewHolder<T>, T> getInnerAdapter() {
        return proxyAdapter != null ? proxyAdapter.getUserAdapter() : null;
    }

    @Nullable
    public RecyclerView.Adapter getAdapter() {
        return viewPager2.getAdapter();
    }

    public void bindData(List<T> list){
        data.clear();
        data.addAll(list);
        if(proxyAdapter == null){
            throw new RuntimeException("请在setAdapter()后设置数据");
        }
        proxyAdapter.resetItems(data);
        viewPager2.setAdapter(proxyAdapter);
        if(list.size() > 0){
            viewPager2.setCurrentItem(list.size() * 10, false);
        }
    }

    public int getCurrentItem() {
        return viewPager2.getCurrentItem();
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager2.setCurrentItem(item, smoothScroll);
    }

    public int getCurrentDataItem() {
        return proxyAdapter.dataIndex(viewPager2.getCurrentItem());
    }

    public void setCurrentDataItem(int item, boolean smoothScroll) {
        if(proxyAdapter != null){
            int layoutIndex = proxyAdapter.getDataCount() * 10 + item;
            viewPager2.setCurrentItem(layoutIndex, smoothScroll);
        }
    }

    public void setPageTransformer(ViewPager2.PageTransformer transformer) {
        this.pageTransformer = transformer;

        if(transformer != null){
            if(transformer instanceof Orientable){
                viewPager2.setOrientation(((Orientable)transformer).orientation());
            }
            if(transformer.valueAnimator()){
                //值动画，需要video组件自适应
                autoScalePlayerViewPort(true);
            }
        }


        if(proxyAdapter != null){
            proxyAdapter.setTransformer(transformer);
        }
        viewPager2.setPageTransformer(transformer);
    }

    public void startPlayer(){
        if(proxyAdapter != null){
            proxyAdapter.startPlayer();
        }
    }

    public void stopPlayer(){
        if(proxyAdapter != null){
            proxyAdapter.stopPlayer();
        }
    }

    public void resumePlayer(){
        if(proxyAdapter != null){
            proxyAdapter.resumePlayer();
        }
    }

    public void pausePlayer(){
        if(proxyAdapter != null){
            proxyAdapter.pausePlayer();
        }
    }

    public void volumePlayer(float l, float r){
        if(proxyAdapter != null){
            proxyAdapter.volumePlayer(l, r);
        }
    }

    public void forwardPlayer(int dur){
        if(proxyAdapter != null){
            proxyAdapter.forwardPlayer(dur);
        }
    }

    public void backwardPlayer(int dur){
        if(proxyAdapter != null){
            proxyAdapter.backwardPlayer(dur);
        }
    }

    public void destroy(){
        if(proxyPagerChangedListener != null){
            proxyPagerChangedListener.destroy();
            viewPager2.unregisterOnPageChangeCallback(proxyPagerChangedListener);
            proxyPagerChangedListener = null;
        }
        if(mPlayComposer != null){
            mPlayComposer.destroy();
        }

        if(proxyAdapter != null){
            proxyAdapter.destroy();
            proxyAdapter = null;
        }
        clearCache();
        stopAutoClear();
        autoDispose().onDestroy();
    }

    public void disAllowTouch(boolean bol){
        this.disAllowTouch = bol;
    }

    public void setVideoStatusListener(VideoStatusListener videoStatusListener){
        this.videoStatusListener = videoStatusListener;
        if(proxyAdapter != null){
            proxyAdapter.setVideoStatusListener(videoStatusListener);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(disAllowTouch){
            return true;
        }else{
            return super.onInterceptTouchEvent(ev);
        }
    }

    public void autoPlay(boolean auto){
        autoPlay(auto, -1);
    }

    public void autoPlay(boolean auto, long duration){
        if(auto){
            if(mPlayComposer == null){
                mPlayComposer = new PlayComposer(this);
                if(duration != -1){
                    mPlayComposer.setDefaultLoopDuration(duration);
                }
            }
            mPlayComposer.reStart();
        }else{
            if(mPlayComposer != null){
                mPlayComposer.pause();
            }
        }
    }

    public void setVideoLoop(boolean loop){
        this.videoLoop = loop;
        if(proxyAdapter != null){
            proxyAdapter.setVideoLoop(videoLoop);
        }
    }

    public void setPlayerType(int playerType){
        this.playerType = playerType;
        if(proxyAdapter != null){
            proxyAdapter.setVideoPlayerType(playerType);
        }
    }

    public void setVideoScaleType(int scaleType){
        this.scaleType = scaleType;
        if(proxyAdapter != null){
            proxyAdapter.setVideoScaleType(scaleType);
        }
    }

    public void setVideoSurfaceType(int surfaceType){
        this.surfaceType = surfaceType;
        if(proxyAdapter != null){
            proxyAdapter.setSurfaceType(surfaceType);
        }
    }

    public void autoScalePlayerViewPort(boolean auto){
        this.autoScalePlayerViewPort = auto;
        if(proxyAdapter != null){
            proxyAdapter.autoScalePlayerViewPort(auto);
        }
    }

    public void setSnapshot(boolean snapshot){
        this.snapshot = snapshot;
        if(proxyAdapter != null){
            proxyAdapter.setSnapshot(snapshot);
        }
    }

    public void setSnapshot(boolean snapshot, float quality){
        this.snapshot = snapshot;
        this.snapshotQuality = quality;
        if(proxyAdapter != null){
            proxyAdapter.setSnapshot(snapshot, snapshotQuality);
        }
    }

    public void setPreloadOffset(int offset){
        this.preloadOffset = offset;
        if(proxyAdapter != null){
            proxyAdapter.setPreLoadOffset(offset);
        }
    }

    public void clearCache(){
        if(viewPager2 != null){
            viewPager2.clearCache();
        }
    }

    /**
     * 每过多久时间清除一次缓存
     * @param duration
     */
    public void autoClearCache(int duration){
        stopAutoClear();
        autoClearCacheLoop = Observable.interval(duration, duration, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    clearCache();
                });

    }

    public void stopAutoClear() {
        if(autoClearCacheLoop != null && !autoClearCacheLoop.isDisposed()){
            autoClearCacheLoop.dispose();
        }
        autoClearCacheLoop = null;
    }

    public void toPreviousPage(){
        if(viewPager2 != null){
            viewPager2.toPreviousPage();
        }
    }

    public void toPreviousPage(int dur){
        if(viewPager2 != null){
            viewPager2.toPreviousPage(dur);
        }
    }

    public void toNextPage(){
        if(viewPager2 != null){
            viewPager2.toNextPage();
        }
    }

    public void toNextPage(int dur){
        if(viewPager2 != null){
            viewPager2.toNextPage(dur);
        }
    }

}
