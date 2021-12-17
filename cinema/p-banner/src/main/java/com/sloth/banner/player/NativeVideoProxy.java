package com.sloth.banner.player;

import android.content.Context;
import android.view.View;
import com.sloth.pinsplatform.player.Player;
import com.sloth.pinsplatform.player.PlayerListener;
import com.sloth.player.NativeSurfacePlayer;
import com.sloth.player.NativeTexturePlayer;
import com.sloth.player.PlayerConst;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/10 10:41
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/10         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class NativeVideoProxy extends PlayerProxy implements PlayerListener {

    private static final String TAG = NativeVideoProxy.class.getSimpleName();

    private final Player videoView;

    public NativeVideoProxy(Context context, PlayerConfig playerConfig) {
        super(playerConfig);

        boolean surface = (playerConfig == null || playerConfig.getSurfaceType() == PlayerConst.SurfaceType.Surface.code);
        boolean fitXy = (playerConfig == null || playerConfig.getScaleType() == PlayerConst.ScaleType.FIT_XY.code);
        boolean autoScaleViewPort = (playerConfig != null && playerConfig.isAutoScaleViewPort());

        if(surface){
            this.videoView = new NativeSurfacePlayer(context);
            ((NativeSurfacePlayer)videoView).setScaleType(fitXy ? PlayerConst.ScaleType.FIT_XY.code : PlayerConst.ScaleType.FIT_CENTER.code);
            ((NativeSurfacePlayer)videoView).setAutoScaleViewPort(autoScaleViewPort);
        }else{
            this.videoView = new NativeTexturePlayer(context);
            ((NativeTexturePlayer)videoView).setScaleType(fitXy ? PlayerConst.ScaleType.FIT_XY.code : PlayerConst.ScaleType.FIT_CENTER.code);
            ((NativeTexturePlayer)videoView).setAutoScaleViewPort(autoScaleViewPort);
        }
        loop(playerConfig.isLoop());

        this.videoView.setListener(this);
    }


    @Override
    public View attachingView() {
        return (View) videoView;
    }


    @Override
    public void preView(String localPath) {
        if(videoView != null){
            videoView.preView(localPath);
        }
    }

    @Override
    public void setVideoSize(int w, int h) {
        if(videoView != null){
            videoView.setVideoSize(w, h);
        }
    }

    @Override
    public void loop(boolean loop) {
        if(videoView != null){
            videoView.loop(loop);
        }
    }

    @Override
    public void seekTo(int dur) {
        if(videoView != null){
            videoView.seekTo(dur);
        }
    }

    @Override
    public void play(String localPath, String webUrl) {
        if(videoView != null){
            videoView.play(localPath, webUrl);
        }
    }

    @Override
    public void stop() {
        if(videoView != null){
            videoView.stop();
        }
    }

    @Override
    public void resume() {
        if(videoView != null){
            videoView.resume();
        }
    }

    @Override
    public void pause() {
        if(videoView != null){
            videoView.pause();
        }
    }

    @Override
    public void volume(float l, float r) {
        if(videoView != null){
            videoView.setVolume(l, r);
        }
    }

    @Override
    public void forward(boolean forward, int dur) {
        if(videoView != null){
            videoView.forward(forward, dur);
        }
    }

    @Override
    public void release() {
        if(videoView != null){
            videoView.release();
        }
    }

    @Override
    public long getCurrentPosition() {
        if(videoView != null){
            return videoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return videoView != null && videoView.isPlaying();
    }

    @Override
    public void onPlayerPrepared() {
        triggerPlayerPrepared();
    }

    @Override
    public void onPlayerSizeChanged(int width, int height) { }

    @Override
    public void onPlayerEnd() {
        triggerPlayerEnd();
    }

    @Override
    public void onPlayerError(int code, String msg) {
        triggerPlayerError(msg);
    }
}
