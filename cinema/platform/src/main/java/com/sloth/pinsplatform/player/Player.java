package com.sloth.pinsplatform.player;

import android.graphics.Bitmap;

import com.sloth.functions.snapshot.SnapshotManager;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/14 18:25
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/14         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Player {


    /**
     * 静态预加载视频的封面
     * @param localPath
     */
    default void preLoadCovers(String localPath){
        SnapshotManager.instance().snapshot(localPath).subscribe(new DisposableObserver<Bitmap>() {
            @Override
            public void onNext(@NonNull Bitmap bitmap) {
                bitmap.recycle();
            }

            @Override
            public void onError(@NonNull Throwable e) { }

            @Override
            public void onComplete() { }
        });
    }

    void preView(String localPath);

    void setVideoSize(int w, int h);

    void play(String local);

    void play(String local, String online);

    void resume();

    void pause();

    void stop();

    void forward(boolean forward, int duration);

    void release();

    void setListener(PlayerListener playerListener);

    void loop(boolean loop);

    boolean isPlaying();

    void seekTo(int position);

    int getCurrentPosition();

    void setVolume(float l, float r);

}
