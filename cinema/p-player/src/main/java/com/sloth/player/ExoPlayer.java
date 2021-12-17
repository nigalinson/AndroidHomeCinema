package com.sloth.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.video.VideoSize;
import com.sloth.functions.snapshot.SnapshotManager;
import com.sloth.pinsplatform.R;
import com.sloth.pinsplatform.player.Player;
import com.sloth.pinsplatform.player.PlayerListener;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.GsonUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.MediaUtils;
import com.sloth.tools.util.ViewUtils;
import com.sloth.tools.util.StringUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/14 10:58
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/14         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ExoPlayer extends FrameLayout implements Player,
        com.google.android.exoplayer2.Player.Listener, TextureView.SurfaceTextureListener, SurfaceHolder.Callback {

    private static final String TAG = ExoPlayer.class.getSimpleName();

    private AppCompatImageView preView;

    private boolean loop = false;

    //新建RyPlayer时，是否自动初始化内置播放器
    // (有些属性一旦确认会比较难以修改，因此允许创建后配置完全后再初始化播放器)
    private boolean autoInitPlayer;

    //默认使用surfaceView
    private int surfaceType;

    //默认按照原比例
    private int scaleType;

    //是否根据容器大小自适应内部播放器视图大小
    private boolean autoScaleViewPort;

    private PlayerListener mControllerListener;

    protected final AtomicBoolean playerPrepared = new AtomicBoolean(false);

    protected final AtomicBoolean surfacePrepared = new AtomicBoolean(false);

    protected final AtomicBoolean playWhenReady = new AtomicBoolean(false);

    /**
     * 显示封面中，播放器的onPrepared回调后画面还会黑100ms左右
     * 如果直接隐藏封面，会有黑一下的视觉效果，因此onPrepared调用后，延迟一定时间再关闭封面
     */
    private long closePreviewDelay = 0;

    private SimpleExoPlayer mPlayer;
    private View surfaceView;

    //目前在播放的预览图链接，如果一致，不需要重复加载
    private String showingPreviewUrl;

    //用来持有surface的引用，防止surface对应的bufferQueue销毁
    private Object surfaceReference;

    //播放中的地址
    private String playingUri;

    public ExoPlayer(@NonNull Context context) {
        this(context, null);
    }

    public ExoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ExoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        //默认自动初始化播放器
        autoInitPlayer = true;
        autoScaleViewPort = false;
        surfaceType = PlayerConst.SurfaceType.Surface.code;
        scaleType = PlayerConst.ScaleType.FIT_XY.code;
        if(attrs != null){
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RyPlayer, 0, 0);
            try {
                autoInitPlayer = a.getBoolean(R.styleable.RyPlayer_player_auto_init, autoInitPlayer);
                autoScaleViewPort = a.getBoolean(R.styleable.RyPlayer_player_auto_scale_view_port, autoScaleViewPort);
                surfaceType = a.getInt(R.styleable.RyPlayer_player_surface_type, surfaceType);
                scaleType = a.getInt(R.styleable.RyPlayer_player_scale_type, scaleType);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                a.recycle();
            }
        }

        if(autoInitPlayer){
            initStructures();
        }
    }

    public void initStructures() {
        setBackgroundColor(Color.BLACK);

        if(mPlayer == null){
            //配置解码器
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
            renderersFactory.setEnableDecoderFallback(true);
            renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
            mPlayer = new SimpleExoPlayer.Builder(getContext(), renderersFactory).build();
            mPlayer.addListener(this);
            mPlayer.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_OFF);

            if(surfaceType == PlayerConst.SurfaceType.Surface.code){
                surfaceView = new SurfaceView(getContext());
                LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                param.gravity = Gravity.CENTER;
                addView(surfaceView, param);
                ((SurfaceView)surfaceView).getHolder().addCallback(this);
                mPlayer.setVideoSurfaceView(((SurfaceView)surfaceView));
            }else{
                surfaceView = new TextureView(getContext());
                LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                param.gravity = Gravity.CENTER;
                addView(surfaceView, param);
                ((TextureView)surfaceView).setSurfaceTextureListener(this);
            }

            preView = new AppCompatImageView(getContext());
            preView.setScaleType(toImgScaleType(scaleType));
            preView.setAlpha(0.99f);
            addView(preView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            preView.setVisibility(View.GONE);
        }

    }

    private ImageView.ScaleType toImgScaleType(int scaleType) {
        LogUtils.d(TAG, "scale Type:" + scaleType);
        if(scaleType == PlayerConst.ScaleType.FIT_XY.code){
            return ImageView.ScaleType.FIT_XY;
        }else if(scaleType == PlayerConst.ScaleType.FIT_CENTER.code){
            return ImageView.ScaleType.FIT_CENTER;
        }
        return ImageView.ScaleType.FIT_XY;
    }


    @Override
    public void setListener(PlayerListener controllerListener){
        this.mControllerListener = controllerListener;
    }

    public int getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(int surfaceType) {
        this.surfaceType = surfaceType;
    }

    public int getScaleType() {
        return scaleType;
    }

    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    public void setClosePreviewDelay(long closePreviewDelay) {
        this.closePreviewDelay = closePreviewDelay;
    }

    public boolean isAutoScaleViewPort() {
        return autoScaleViewPort;
    }

    public void setAutoScaleViewPort(boolean b){
        this.autoScaleViewPort = b;
    }

    @Override
    public void loop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop(){
        return loop;
    }

    @Override
    public void preView(String localPath) {

        if(StringUtils.notEmpty(localPath)){
            LogUtils.d(TAG, "加载预览图：" + localPath);
            //加载预览图
            if(preView.getVisibility() != View.VISIBLE){
                preView.setVisibility(View.VISIBLE);
            }
            if(!localPath.equals(showingPreviewUrl)){
                SnapshotManager.instance().snapshot(localPath).subscribe(new DisposableObserver<Bitmap>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Bitmap bitmap) {
                        applyVidRatio(bitmap.getWidth(), bitmap.getHeight());
                        showingPreviewUrl = localPath;
                        preView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        LogUtils.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() { }
                });
            }

        }else{
            //清空预览图
            LogUtils.d(TAG, "释放预览图内存");
            ViewUtils.releaseImageViewResource(preView);
            showingPreviewUrl = null;
            preView.setImageBitmap(null);
        }
    }

    @Override
    public void setVideoSize(int w, int h) {
        applyVidRatio(w, h);
    }

    @Override
    public void play(String local) {
        play(local, null);
    }

    @Override
    public void play(String local, String online) {
        LogUtils.d(TAG, "播放：local:" + local + ", online:" + online);
        if(mPlayer != null){
            if(StringUtils.notEmpty(local) && FileUtils.isFileExists(local)){
                LogUtils.d(TAG, "置入播放链接！" +local);
                this.playingUri = local;
                mPlayer.stop(true);
                mPlayer.setMediaItem(MediaItem.fromUri(Uri.fromFile(new File(local))));
                mPlayer.prepare();
                playWhenReady.set(true);
                detectPlay();
            }else if(StringUtils.notEmpty(online)){
                LogUtils.d(TAG, "置入播放链接！" + online);
                this.playingUri = online;
                mPlayer.stop(true);
                mPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(online)));
                mPlayer.prepare();
                playWhenReady.set(true);
                detectPlay();
            }else{
                LogUtils.e(TAG, "无有效播放地址！");
            }
        }
    }

    private void detectPlay(){
        if(!playerPrepared.get()){
            LogUtils.e(TAG, "播放器暂未prepared");
            return;
        }

        if(!surfacePrepared.get()){
            LogUtils.e(TAG, "surface暂未就绪");
            return;
        }

        if(!playWhenReady.get()){
            LogUtils.e(TAG, "一切就绪，但用户未触发播放");
            if(isPlaying()){
                //因为状态为不允许播放，如果发现在播放中，顺便暂停播放
                stop();
            }
            return;
        }

        if(isPlaying()){
            LogUtils.e(TAG, "已经在播放中");
            return;
        }

        if(mPlayer != null){
            mPlayer.play();
        }
    }

    @Override
    public void stop(){
        LogUtils.d(TAG, "停止播放");
        playWhenReady.set(false);
        playerPrepared.set(false);
        if(mPlayer != null) {
            mPlayer.pause();
            mPlayer.stop();
        }
    }

    @Override
    public void resume() {
        if(mPlayer != null){
            mPlayer.play();
        }
    }

    @Override
    public void pause() {
        if(mPlayer != null){
            mPlayer.pause();
        }
    }

    @Override
    public boolean isPlaying() {
        if(mPlayer != null){
            mPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(int dur) {
        if(mPlayer != null){
            mPlayer.seekTo((int) dur);
        }
    }

    @Override
    public int getCurrentPosition() {
        if(mPlayer != null){
            return (int) mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void setVolume(float l, float r) {
        if(mPlayer != null){
            mPlayer.setVolume(l);
        }
    }

    @Override
    public void forward(boolean forward, int duration) {
        if(null == playingUri || "".equals(playingUri)){ return; }
        long total = MediaUtils.getDefaultEngine().duration(playingUri);
        long now = getCurrentPosition();
        now = (forward ? now + duration : now - duration);
        now = Math.max(0, now);
        now = Math.min(now, total - 1000);
        seekTo((int)now);
    }

    @Override
    public void release() {
        if(surfaceType == PlayerConst.SurfaceType.Surface.code && surfaceView != null){
            ((SurfaceView)surfaceView).getHolder().removeCallback(this);
        }else if(surfaceType == PlayerConst.SurfaceType.Texture.code && surfaceView != null){
            ((TextureView)surfaceView).setSurfaceTextureListener(null);
        }
        surfaceView = null;
        surfaceReference = null;

        if(mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void triggerPlayerPrepared(){
        if(preView.getVisibility() == View.VISIBLE){
            preView.setVisibility(View.GONE);
        }
        if(mControllerListener != null){
            mControllerListener.onPlayerPrepared();
        }
    }

    private void triggerPlayerSizeChanged(int w, int h){
        if(mControllerListener != null){
            mControllerListener.onPlayerSizeChanged(w, h);
        }
    }

    private void triggerPlayerEnd(){
        if(mControllerListener != null){
            mControllerListener.onPlayerEnd();
        }
    }

    private void triggerPlayerError(int errCode, String errMsg){
        if(mControllerListener != null){
            mControllerListener.onPlayerError(errCode, errMsg);
        }
    }

    //=========================================player callback============================================
    //region player callback

    @Override
    public void onPlaybackStateChanged(int state) {
        if(state == com.google.android.exoplayer2.Player.STATE_READY){
            LogUtils.i(TAG, "视频就绪");
            playerPrepared.set(true);
            detectPlay();

            //此方法回调后，实测有些机器要过几百毫秒左右 才会真实渲染，因此延迟通知外部关闭封面
            postDelayed(this::triggerPlayerPrepared, closePreviewDelay);

            if(mPlayer != null){
                mPlayer.setRepeatMode(loop ? com.google.android.exoplayer2.Player.REPEAT_MODE_ONE : com.google.android.exoplayer2.Player.REPEAT_MODE_OFF);
            }

        }else if(state == com.google.android.exoplayer2.Player.STATE_ENDED){
            LogUtils.i(TAG, "播放结束");
            triggerPlayerEnd();
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        triggerPlayerError(error!= null ? error.type : -1, error != null ? GsonUtils.toJson(error.getMessage()) : "暂无报错信息");
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        applyVidRatio(videoSize.width, videoSize.height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(autoScaleViewPort){
            applyVidRatio(vidWidth, vidHeight);
        }
    }

    private int vidWidth = -1, vidHeight = -1;
    private Disposable applyRatioDispose;
    private void applyVidRatio(int videoWidth, int videoHeight) {
        if(videoWidth == -1 || videoHeight == -1){ return; }
        if(videoWidth == getWidth() && videoHeight == getHeight()){ return; }
        this.vidWidth = videoWidth;
        this.vidHeight = videoHeight;

        scheduleApplyRatioTask(videoWidth, videoHeight);
    }

    private void scheduleApplyRatioTask(int videoWidth, int videoHeight) {
        //尚未初始化，返回
        if(mPlayer == null){ return; }

        if(autoScaleViewPort){
            //根据容器大小自适应内部大小，需要一个延迟，否则连续快速计算容器大小
            cancelApplyRatioTask();
            applyRatioDispose = Observable.timer(200, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> realApplyVideoSize(videoWidth, videoHeight));
        }else{
            realApplyVideoSize(videoWidth, videoHeight);
        }
    }

    private void realApplyVideoSize(int videoWidth, int videoHeight) {
        LogUtils.d(TAG, "onVideoSizeChanged:" + videoWidth + "," + videoHeight);
        if(scaleType == PlayerConst.ScaleType.FIT_CENTER.code){
            //等比缩放
            LogUtils.d(TAG, "surface:" + surfaceView.getWidth() + "," + surfaceView.getHeight());
            float surfaceRatio = containerRatio();
            float vidRatio = 1.0f * videoWidth / videoHeight;
            if(vidRatio > surfaceRatio){
                //视频比容器更扁，以容器width为锚点缩放
                int width = getWidth();
                int height = (int) (width / vidRatio);
                LayoutParams params = (LayoutParams) surfaceView.getLayoutParams();
                params.width = width;
                params.height = height;
                surfaceView.setLayoutParams(params);
            }else if(vidRatio < surfaceRatio){
                //视频比容器更窄，以容器height为锚点缩放
                int height = getHeight();
                int width = (int) (height * vidRatio);
                LayoutParams params = (LayoutParams) surfaceView.getLayoutParams();
                params.width = width;
                params.height = height;
                surfaceView.setLayoutParams(params);
            }else{
                //比例已经一致，不需要处理
            }
        }else if(scaleType == PlayerConst.ScaleType.FIT_XY.code){
            //播放器默认全屏，不用处理
        }
    }

    private void cancelApplyRatioTask() {
        if(applyRatioDispose != null && !applyRatioDispose.isDisposed()){
            applyRatioDispose.dispose();
        }
    }

    private float containerRatio() {
        return 1.0f * getWidth() / getHeight();
    }

    //endregion player callback

    //=========================================texture============================================
    //region texture

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtils.i(TAG, "Texture就绪");
        Surface surfaceHolder = new Surface(surface);
        surfaceReference = surfaceHolder;
        if(mPlayer != null){
            mPlayer.setVideoSurface(surfaceHolder);
        }

        surfacePrepared.set(true);
        detectPlay();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        LogUtils.i(TAG, "Texture销毁");
        surfacePrepared.set(false);
        //容器销毁
        if(mPlayer != null){
            mPlayer.pause();
            mPlayer.stop();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) { }

    //endregion texture

    //=========================================surface============================================
    //region surface

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.i(TAG, "surface就绪");
        surfaceReference = holder.getSurface();
        surfacePrepared.set(true);
        detectPlay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.i(TAG, "surface销毁");
        surfacePrepared.set(false);
        //容器销毁
        if(mPlayer != null){
            mPlayer.pause();
            mPlayer.stop();
        }
    }

    //endregion surface

}
