package com.sloth.banner.player;

import android.content.Context;
import com.sloth.functions.AutoDispose;
import com.sloth.banner.XBanner;
import com.sloth.tools.util.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/10 11:25
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/10         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class VideoPlayerProxyPools implements AutoDispose.AutoDisposable {

    private static final String TAG = VideoPlayerProxyPools.class.getSimpleName();

    /**
     * 最大播放器实例个数
     */
    private final static int SUGGESTING_MAX_POOL_SIZE = 1;

    public static VideoPlayerProxyPools newInstance(){
        return new VideoPlayerProxyPools();
    }

    private VideoPlayerProxyPools() {
        XBanner.autoDispose().autoDispose(this);
    }

    public void setGenerateConfig(PlayerConfig playerConfig){
        this.playerConfig = playerConfig;
    }

    private PlayerConfig playerConfig;

    private final Queue<PlayerProxy> usingControllers = new LinkedBlockingDeque<PlayerProxy>();
    private final Queue<PlayerProxy> recycledControllers = new LinkedBlockingDeque<PlayerProxy>();

    /**
     * 回收后2S以内没有复用的话直接release
     */
    private final long releaseDelay = 2000;
    private Disposable releaseLoop;

    public PlayerProxy get(Context context){
        PlayerProxy res = recycledControllers.poll();
        if(res != null){
            //复用
            usingControllers.add(res);
        }else if(usingControllers.size() < SUGGESTING_MAX_POOL_SIZE){
            //新建
            res = VideoPlayerProxyFactory.generate(context, playerConfig);
            usingControllers.add(res);
        }else {
            LogUtils.d("播放器池超过建议最大实例数！强制回收！");
            //其他页面可能没回收，直接强行征用给第二个用
            res = usingControllers.element();
            res.stop();
            if(res.isAttached()){
                // 如果是添加状态
                //先detach
                res.detach();
            }
        }
        LogUtils.d(TAG, "目前播放器池：recycled:" + recycledControllers.size() + ",using:" + usingControllers.size());
        return res;
    }

    public void recycle(PlayerProxy recycled){
        if(recycled.isAttached()){
            // 如果是添加状态
            //先detach
            recycled.detach();
        }

        //使用池
        Iterator<PlayerProxy> iterator = usingControllers.iterator();
        while(iterator.hasNext()){
            PlayerProxy item = iterator.next();
            if(item != null){
                if(item == recycled || !item.isAttached()){
                    //如果已失效，放到回收池中，并从使用池去除
                    recycledControllers.add(item);
                    iterator.remove();
                }
            }else{
                //已经空了，无效对象，删除
                iterator.remove();
            }
        }
        startReleaseRequest();
        LogUtils.d(TAG, "目前播放器池：recycled:" + recycledControllers.size() + ",using:" + usingControllers.size());
    }

    public void startReleaseRequest(){
        stopReleaseRequest();
        releaseLoop = Observable.timer(releaseDelay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    LogUtils.d(TAG, "正在判断，是否需要清空回收池");
                    clear(recycledControllers);
                    LogUtils.d(TAG, "目前播放器池：recycled:" + recycledControllers.size() + ",using:" + usingControllers.size());
                });
    }

    public void stopReleaseRequest(){
        if(releaseLoop != null && !releaseLoop.isDisposed()){
            releaseLoop.dispose();
        }
    }

    public void resumeUsing(){
        if(usingControllers.isEmpty()){ return; }
        for(PlayerProxy playerProxy: usingControllers){
            playerProxy.resume();
        }
    }

    public void pauseUsing(){
        if(usingControllers.isEmpty()){ return; }
        for(PlayerProxy playerProxy: usingControllers){
            playerProxy.pause();
        }
    }

    public void volumeUsing(float l, float r){
        if(usingControllers.isEmpty()){ return; }
        for(PlayerProxy playerProxy: usingControllers){
            playerProxy.volume(l, r);
        }
    }

    public void forwardUsing(int dur){
        if(usingControllers.isEmpty()){ return; }
        for(PlayerProxy playerProxy: usingControllers){
            playerProxy.forward(true, dur);
        }
    }

    public void backwardUsing(int dur){
        if(usingControllers.isEmpty()){ return; }
        for(PlayerProxy playerProxy: usingControllers){
            playerProxy.forward(false, dur);
        }
    }

    @Override
    public void autoDispose() {
        destroy();
    }

    public void destroy(){
        stopReleaseRequest();
        clear(usingControllers);
        clear(recycledControllers);
        LogUtils.d(TAG, "目前播放器池：recycled:" + recycledControllers.size() + ",using:" + usingControllers.size());
    }

    private void clear(Collection<PlayerProxy> container) {
        if(container.isEmpty()){ return; }
        LogUtils.d(TAG, "正在清空池");
        Iterator<PlayerProxy> iterator = container.iterator();
        while(iterator.hasNext()){
            PlayerProxy item = iterator.next();
            if(item != null){
                item.setPlayerListener(null);
                item.stop();
                item.release();
                iterator.remove();
            }
        }
    }
}
