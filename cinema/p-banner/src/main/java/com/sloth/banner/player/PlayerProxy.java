package com.sloth.banner.player;


/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/10 11:18
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/10         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class PlayerProxy extends AttachableProxy {

    private static final String TAG = PlayerProxy.class.getSimpleName();

    private PlayerListener mControllerListener;
    private PlayerConfig playerConfig;

    public PlayerProxy(PlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
    }

    public void setPlayerListener(PlayerListener controllerListener){
        this.mControllerListener = controllerListener;
    }

    public abstract void preView(String localPath);

    public abstract void setVideoSize(int w, int h);

    public abstract void loop(boolean loop);

    public abstract void seekTo(int dur);

    public abstract void play(String localPath, String webUrl);

    public abstract void stop();

    public abstract void resume();

    public abstract void pause();

    public abstract void volume(float l, float r);

    public abstract void forward(boolean forward, int dur);

    public abstract void release();

    public abstract long getCurrentPosition();

    public abstract boolean isPlaying();

    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public void setPlayerConfig(PlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
    }

    protected void triggerPlayerPrepared(){
        if(mControllerListener != null){
            mControllerListener.onPlayerPrepared();
        }
    }

    protected void triggerPlayerEnd(){
        if(mControllerListener != null){
            mControllerListener.onPlayerEnd();
        }
    }

    protected void triggerPlayerError(String errMsg){
        if(mControllerListener != null){
            mControllerListener.onPlayerError(errMsg);
        }
    }

    public interface PlayerListener {

        void onPlayerPrepared();

        void onPlayerEnd();

        void onPlayerError(String msg);
    }
}
