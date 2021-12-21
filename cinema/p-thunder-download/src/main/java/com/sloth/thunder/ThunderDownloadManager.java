package com.sloth.thunder;

import androidx.annotation.NonNull;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.download.AbsDownloadManager;
import com.sloth.functions.download.AbsDownloadTask;
import com.sloth.functions.download.DownloadUtils;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.ExecutorUtils;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.Utils;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/20 19:23
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = DownloadManager.class, key = Strategies.DownloadEngine.THUNDER, singleton = true)
public class ThunderDownloadManager extends AbsDownloadManager<Long> {
    private static final String TAG = "ThunderDownloadManager";

    public ThunderDownloadManager() {
        XLTaskHelper.init(Utils.getApp());
    }

    @Override
    protected AbsDownloadTask<Long> makeDownloadTask(String url, String filePath, DownloadListener downloadListener) {
        String folder = Objects.requireNonNull(new File(filePath).getParentFile()).getAbsolutePath();
        String fileName = FileUtils.getFileName(filePath);
        String fileNameNoExtension = FileUtils.getFileNameNoExtension(filePath);
        return new DownloadTask(downloadTasks, url, folder, fileNameNoExtension, fileName, downloadListener);
    }

    private static class DownloadTask extends AbsDownloadTask<Long> {

        private final String realFolder;

        private final String subFolder;

        private final String fileName;

        private Disposable progressDispose;

        public DownloadTask(Map<String, AbsDownloadTask<Long>> downloadTasks, String urlLink, String realFolder, String subFolder, String fileName, DownloadListener downloadListener) {
            super(downloadTasks, urlLink, realFolder + "/" + subFolder + "/" + fileName, downloadListener);
            this.realFolder = realFolder;
            this.subFolder = subFolder;
            this.fileName = fileName;
        }

        @Override
        protected Long makeClient() {
            return XLTaskHelper.instance().addThunderTask(urlLink, realFolder + "/" + subFolder, "cache.mp4");
        }

        @Override
        protected void onDownloading(Long client) {
            if(client == null || client == -1){
                notifyFailed("create download task failed !");
            }else{
                runOnUiThread(this::startProgressListener);
            }
        }

        @Override
        protected void terminateClient(Long client) {
            runOnUiThread(this::stopProgressListener);
            XLTaskHelper.instance().stopTask(client);
        }

        private void stopProgressListener(){
            if(progressDispose != null && !progressDispose.isDisposed()){
                progressDispose.dispose();
                progressDispose = null;
            }
        }

        private void startProgressListener(){
            Observable.interval(5000, 5000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ProgressObserver());
        }

        private final class ProgressObserver implements Observer<Long>{

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                progressDispose = d;
            }

            @Override
            public void onNext(@NonNull Long aLong) {
                if(client != null && client != -1){
                    XLTaskInfo taskInfo = XLTaskHelper.instance().getTaskInfo(client);
                    if(taskInfo != null){
                        long downSize = taskInfo.mDownloadSize;
                        long fileSize = taskInfo.mFileSize;
                        if(downSize >= fileSize && fileSize > 0){
                            rename();
                        }else{
                            notifyProgress(downSize, fileSize);
                        }
                    }else{
                        stopProgressListener();
                        notifyFailed("task is invalid !");
                    }
                }else{
                    stopProgressListener();
                    notifyFailed("task is invalid !");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                String errMsg = (e.getMessage() != null ? e.getMessage() : "task is invalid !");
                LogUtils.e(TAG, errMsg);
                stopProgressListener();
                notifyFailed(errMsg);
            }

            @Override
            public void onComplete() { }
        }

        private void rename() {
            ExecutorUtils.getNormal().execute(new ExecutorUtils.WorkRunnable() {
                @Override
                public void run() {
                    File subFile = new File(realFolder + "/" + subFolder);
                    File downFile = DownloadUtils.findMovie(subFile);
                    if(downFile != null){
                        FileUtils.move(downFile, new File(realFolder + "/" + fileName));
                        notifyComplete(realFolder + "/" + fileName);
                    }else{
                        notifyFailed("result file not found !");
                    }
                }
            });
        }
    }

}
