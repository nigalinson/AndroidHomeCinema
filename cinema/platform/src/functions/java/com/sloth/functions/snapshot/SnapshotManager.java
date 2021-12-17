package com.sloth.functions.snapshot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.sloth.pinsplatform.Strategies;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.ImageUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.MediaUtils;
import com.sloth.tools.util.Utils;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/1/11 11:55
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/1/11         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class SnapshotManager {

    private static final String TAG = "SnapshotManager";

    private static final class SnapShotHolder{
        private static final SnapshotManager INSTANCE = new SnapshotManager();
    }

    public static SnapshotManager instance(){
        return SnapShotHolder.INSTANCE;
    }

    private SnapshotManager() { }

    private final List<String> writing = new ArrayList<>();

    public Observable<Bitmap> snapshot(String localVideoPath) {
        return snapshot(localVideoPath, 0.3f);
    }

    /**
     * 保存视频截图到本地
     * @param localVideoPath 视频地址
     */
    public Observable<Bitmap> snapshot(String localVideoPath, float quality) {

        return Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {

            String cacheFile = cacheFileName(localVideoPath);
            LogUtils.d(TAG, "preview file name:" + cacheFile);
            if(FileUtils.isFileExists(cacheFile)){
                //文件已存在
                boolean isWriting = false;
                synchronized (writing){
                    if(writing.contains(localVideoPath)){
                        isWriting = true;
                    }
                }
                if(isWriting){
                    //文件存在，但不完整，正在写入
                    emitter.onError(new RuntimeException("封面生成中"));
                }else{
                    //文件存在，且不是写入中状态 - 可以加载
                    LogUtils.d(TAG, "封面已准备好，加载中");
                    emitter.onNext(BitmapFactory.decodeFile(cacheFile));
                    emitter.onComplete();
                }
            }else{
                //文件不存在
                boolean isWaiting = false;
                synchronized (writing){
                    if(writing.contains(localVideoPath)){
                        isWaiting = true;
                    }
                }

                if(!isWaiting){
                    writing.add(localVideoPath);
                    //任务不存在，开始生成并写到本地
                    Bitmap cover = MediaUtils.getEngine(Strategies.MediaInfoRetrieverEngine.FFMPEG).cover(localVideoPath);
                    Bitmap b = ImageUtils.bytes2Bitmap(ImageUtils.compressByQuality(cover, (int)(quality * 100)));
                    if(b == null){
                        writing.remove(localVideoPath);
                        emitter.onError(new RuntimeException("获取封面失败"));
                    }else{
                        emitter.onNext(b);
                        FileOutputStream opStream = null;
                        try{
                            opStream = new FileOutputStream(cacheFile);
                            b.compress(Bitmap.CompressFormat.JPEG, 100, opStream);
                            opStream.flush();
                            opStream.close();
                        }catch(Exception e){
                            e.printStackTrace();
                            LogUtils.e(TAG, e.getMessage());
                        }finally {
                            if(opStream != null){
                                opStream.close();
                            }
                        }
                        writing.remove(localVideoPath);
                        emitter.onComplete();
                    }
                }else{
                    //任务已存在，重复任务，跳过本次
                    emitter.onError(new RuntimeException("封面正在等待生成"));
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private static String cacheFileName(String localVideoPath) {
        String fileName = FileUtils.getFileName(localVideoPath) + ".jpg";
        return Utils.getApp().getCacheDir() + fileName;
    }

}
