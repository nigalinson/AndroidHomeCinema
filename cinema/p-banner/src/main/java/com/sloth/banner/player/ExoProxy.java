package com.sloth.banner.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sloth.banner.R;
import com.sloth.pinsplatform.player.PlayerListener;
import com.sloth.player.ExoPlayer;
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
public class ExoProxy extends PlayerProxy implements PlayerListener {

    private static final String TAG = ExoProxy.class.getSimpleName();

    private final ExoPlayer exoPlayer;

    public ExoProxy(Context context, PlayerConfig playerConfig) {
        super(playerConfig);

        boolean surface = (playerConfig == null || playerConfig.getSurfaceType() == PlayerConst.SurfaceType.Surface.code);
        boolean fitXy = (playerConfig == null || playerConfig.getScaleType() == PlayerConst.ScaleType.FIT_XY.code);
        boolean autoScaleViewPort = (playerConfig != null && playerConfig.isAutoScaleViewPort());

        exoPlayer = (ExoPlayer) LayoutInflater.from(context).inflate(R.layout.layout_ry_player, null);
        exoPlayer.setScaleType(fitXy ? PlayerConst.ScaleType.FIT_XY.code : PlayerConst.ScaleType.FIT_CENTER.code);
        exoPlayer.setSurfaceType(surface ? PlayerConst.SurfaceType.Surface.code : PlayerConst.SurfaceType.Texture.code);
        exoPlayer.setAutoScaleViewPort(autoScaleViewPort);
        exoPlayer.loop(playerConfig.isLoop());
        //手动初始化播放器
        exoPlayer.initStructures();

        exoPlayer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        exoPlayer.setListener(this);
    }

    @Override
    public View attachingView() {
        return exoPlayer;
    }

    @Override
    public void preView(String localPath) {
        if(exoPlayer != null){
            exoPlayer.preView(localPath);
        }
    }

    @Override
    public void setVideoSize(int w, int h) {
        if(exoPlayer != null){
            exoPlayer.setVideoSize(w, h);
        }
    }

    @Override
    public void loop(boolean loop) {
        if(exoPlayer != null){
            exoPlayer.loop(loop);
        }
    }

    @Override
    public void seekTo(int dur) {
        if(exoPlayer != null){
            exoPlayer.seekTo(dur);
        }
    }

    @Override
    public void play(String localPath, String webUrl) {
        if(exoPlayer != null){
            exoPlayer.play(localPath, webUrl);
        }
    }

    @Override
    public void stop() {
        if(exoPlayer != null){
            exoPlayer.stop();
        }
    }

    @Override
    public void resume() {
        if(exoPlayer != null){
            exoPlayer.resume();
        }
    }

    @Override
    public void pause() {
        if(exoPlayer != null){
            exoPlayer.pause();
        }
    }

    @Override
    public void volume(float l, float r) {
        if(exoPlayer != null){
            exoPlayer.setVolume(l, r);
        }
    }

    @Override
    public void release() {
        if(exoPlayer != null){
            exoPlayer.release();
        }
    }

    @Override
    public long getCurrentPosition() {
        if(exoPlayer != null){
            return exoPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void forward(boolean forward, int dur) {
        if(exoPlayer != null){
            exoPlayer.forward(forward, dur);
        }
    }

    @Override
    public boolean isPlaying() {
        return exoPlayer != null && exoPlayer.isPlaying();
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
