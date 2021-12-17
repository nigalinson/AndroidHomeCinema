package com.sloth.banner.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sloth.functions.adapter.BaseViewHolder;
import com.sloth.banner.data.Playable;
import com.sloth.banner.player.PlayerConfig;
import com.sloth.banner.player.VideoPlayerProxyPools;
import com.sloth.banner.vh.PlayerViewHolder;
import com.sloth.banner.vh.VideoStatusListener;


/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/19 17:10
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class VideoAdapter<VH extends BaseViewHolder<T>, T extends Playable> extends InfiniteAdapter<VH,T> {

    private final PlayerConfig playerConfig = new PlayerConfig();
    private final VideoPlayerProxyPools videoViewPools = VideoPlayerProxyPools.newInstance();

    private VideoStatusListener outListener;

    private final VideoStatusListener videoStatusListener = new VideoStatusListener() {
        @Override
        public void onPrepared(int pos) {
            if(outListener != null){
                outListener.onPrepared(pos);
            }
        }

        @Override
        public void onEnd(int pos) {
            if(outListener != null){
                outListener.onEnd(pos);
            }
        }

        @Override
        public void onError(int pos) {
            if(outListener != null){
                outListener.onError(pos);
            }
        }
    };

    public VideoAdapter(Context context) {
        super(context);
        videoViewPools.setGenerateConfig(playerConfig);
    }

    public void setVideoStatusListener(VideoStatusListener outListener) {
        this.outListener = outListener;
    }

    public void setVideoPlayerType(int videoPlayerType){
        playerConfig.setPlayerType(videoPlayerType);
    }

    public void setVideoLoop(boolean loop){
        playerConfig.setLoop(loop);
    }

    public void autoScalePlayerViewPort(boolean auto){
        playerConfig.setAutoScaleViewPort(auto);
    }

    public void setVideoScaleType(int scaleType){
        playerConfig.setScaleType(scaleType);
    }

    public void setSurfaceType(int surfaceType){
        playerConfig.setSurfaceType(surfaceType);
    }

    public void setSnapshot(boolean snapshot){
        playerConfig.setSnapshot(snapshot);
    }

    public void setSnapshot(boolean snapshot, float quality){
        playerConfig.setSnapshot(snapshot);
        playerConfig.setSnapshotMixedQuality(quality);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if(holder instanceof PlayerViewHolder){
            ((PlayerViewHolder)holder).setPlayerPool(videoViewPools, playerConfig);
            ((PlayerViewHolder)holder).setVideoStatusListener(videoStatusListener);
        }
        super.onBindViewHolder(holder, position);
    }

    public void startPlayer(){
        //开启播放需要数据源，应该由adp控制viewholder生命周期来完成
    }

    public void stopPlayer(){
        //开启播放需要数据源，应该由adp控制viewholder生命周期来完成
        //因此停止播放也需要成对触发viewhodler生命周期
    }

    public void resumePlayer(){
        videoViewPools.resumeUsing();
    }

    public void pausePlayer(){
        videoViewPools.pauseUsing();
    }

    public void volumePlayer(float l, float r){
        videoViewPools.volumeUsing(l, r);
    }

    public void forwardPlayer(int dur){
        videoViewPools.forwardUsing(dur);
    }

    public void backwardPlayer(int dur){
        videoViewPools.backwardUsing(dur);
    }

}
