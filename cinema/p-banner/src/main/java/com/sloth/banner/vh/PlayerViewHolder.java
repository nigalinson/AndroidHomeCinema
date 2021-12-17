package com.sloth.banner.vh;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatImageView;
import com.sloth.banner.data.Playable;
import com.sloth.banner.player.PlayerConfig;
import com.sloth.banner.player.PlayerProxy;
import com.sloth.banner.player.VideoPlayerProxyPools;
import com.sloth.functions.snapshot.SnapshotManager;
import com.sloth.player.PlayerConst;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.StringUtils;
import com.sloth.tools.util.ViewUtils;
import io.reactivex.observers.DisposableObserver;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/10 13:55
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/10         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class PlayerViewHolder<T extends Playable> extends PagerViewHolder<T> implements PlayerProxy.PlayerListener {

    private static final String TAG = PlayerViewHolder.class.getSimpleName();

    private VideoPlayerProxyPools videoViewPools;

    private PlayerConfig playerConfig;

    protected PlayerProxy mPlayer;

    private VideoStatusListener videoStatusListener;

    private final FrameLayout videoContainer;

    private final AppCompatImageView preView;

    private String showingPreviewUrl;

    public PlayerViewHolder(View itemView) {
        super(itemView);
        videoContainer = initVideoContainer(itemView);
        preView = initVideoPreview(itemView);
    }

    protected abstract FrameLayout initVideoContainer(View itemView);

    protected abstract AppCompatImageView initVideoPreview(View itemView);

    public void setPlayerPool(VideoPlayerProxyPools pool, PlayerConfig playerConfig){
        this.videoViewPools = pool;
        boolean firstInit = (this.playerConfig == null);
        this.playerConfig = playerConfig;

        if(firstInit && preView != null){
            if(playerConfig.getScaleType() == PlayerConst.ScaleType.FIT_XY.code){
                preView.setScaleType(ImageView.ScaleType.FIT_XY);
            }else{
                preView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
    }

    public void setVideoStatusListener(VideoStatusListener videoStatusListener) {
        this.videoStatusListener = videoStatusListener;
    }

    @Override
    public void onPreLoad(T data) {
        LogUtils.d(TAG, "onPreLoad-" + hashCode());

        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.stop();
        }
        recycleController();

        ifNeedPreview(data.localPath());
    }

    private void ifNeedPreview(String localPath) {
        if(!playerConfig.isSnapshot()){
            LogUtils.d(TAG, "关闭了预览功能，不加载预览图");
            return;
        }

        if(preView == null || StringUtils.isEmpty(localPath)){
            return;
        }
        //加载预览图
        LogUtils.d(TAG, "加载预览图：" + localPath);

        if(preView.getVisibility() != View.VISIBLE){
            preView.setVisibility(View.VISIBLE);
        }

        if(localPath.equals(showingPreviewUrl)){
            //预览图内容未变，不需要重新加载
            return;
        }

        SnapshotManager.instance().snapshot(localPath, playerConfig.getSnapshotMixedQuality())
                .subscribe(new DisposableObserver<Bitmap>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Bitmap bitmap) {
                        preView.setImageBitmap(bitmap);
                        showingPreviewUrl = localPath;
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        LogUtils.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() { }
                });
    }

    @Override
    public void onLoaded(T data) {
        LogUtils.d(TAG, "onLoaded-" + hashCode());
        initPlayer();
        if(mPlayer == null){
            throw new RuntimeException("player is null");
        }
        if(!mPlayer.isAttached()){
            mPlayer.attach(videoContainer);
        }
        applySize(mPlayer, preView);
        mPlayer.play(data.localPath(), data.webUrl());
    }

    private void applySize(PlayerProxy mPlayer, AppCompatImageView preView) {
        Drawable drawable = preView.getDrawable();
        if(drawable == null){ return; }
        Bitmap bp = ((BitmapDrawable)drawable).getBitmap();
        if(bp == null){ return; }
        mPlayer.setVideoSize(bp.getWidth(), bp.getHeight());
    }

    @Override
    public void onClose(T data) {
        LogUtils.d(TAG, "onClose-" + hashCode());
        //清空预览图
        LogUtils.d(TAG, "释放预览图内存");
        ViewUtils.releaseImageViewResource(preView);
        preView.setImageBitmap(null);
        showingPreviewUrl = null;
    }

    private void initPlayer() {
        if(mPlayer == null){
            mPlayer = videoViewPools.get(itemView.getContext());
            if(mPlayer != null){
                mPlayer.setPlayerListener(this);
                LogUtils.d(TAG, "初始化播放组件");
            }else{
                LogUtils.d(TAG, "播放组件实例超过1，显示黑屏");
            }
        }
    }

    private void recycleController(){
        if(videoViewPools != null && mPlayer != null){
            LogUtils.d(TAG, "回收播放组件" + hashCode());
            videoViewPools.recycle(mPlayer);
            mPlayer = null;
        }
    }

    @Override
    public void onPlayerPrepared() {
        LogUtils.d(TAG, "video prepared");
        if(videoStatusListener != null){
            videoStatusListener.onPrepared(getAdapterPosition());
        }

        if(preView != null && preView.getVisibility() == View.VISIBLE){
            preView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlayerEnd() {
        if(videoStatusListener != null){
            videoStatusListener.onEnd(getAdapterPosition());
        }
    }

    @Override
    public void onPlayerError(String msg) {
        LogUtils.e(TAG, msg);
        if(videoStatusListener != null){
            videoStatusListener.onError(getAdapterPosition());
        }
    }
}
