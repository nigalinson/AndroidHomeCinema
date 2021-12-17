package com.sloth.liulishodownload;

import android.os.Handler;
import android.os.Looper;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 18:35
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = DownloadManager.class, key = Strategies.DownloadEngine.LIU_LI_SHO, singleton = true)
public class LiuLiShoDownloadManager implements DownloadManager {

    private static final String TAG = "LiuLiShoDownloadManager";

    private final Map<String, MyDownloadTask> downloadTasks = new ConcurrentHashMap<>();

    @Override
    public void download(String url, String filePath, DownloadListener downloadListener) {
        if(!downloadTasks.containsKey(url)){
            MyDownloadTask downloadTask = new MyDownloadTask(downloadTasks, url, filePath, downloadListener);
            downloadTasks.put(url, downloadTask);
            downloadTask.start();
        }else{
            downloadListener.onDownloadFailed("already exist !");
            LogUtils.e(TAG, "已有下载中的任务，取消");
        }
    }

    @Override
    public boolean isDownloading(String url) {
        return downloadTasks.containsKey(url);
    }

    @Override
    public int runningTasks() {
        return downloadTasks.size();
    }

    @Override
    public void terminate(String url) {
        if(downloadTasks.containsKey(url)){
            downloadTasks.get(url).pause();
            downloadTasks.remove(url);
        }
    }

    @Override
    public void terminateAll() {
        Iterator<MyDownloadTask> iterator = downloadTasks.values().iterator();
        while(iterator.hasNext()){
            MyDownloadTask item = iterator.next();
            item.pause();
            iterator.remove();
        }
    }

    private static final class MyDownloadTask {
        private final static Handler mainHandler = new Handler(Looper.getMainLooper());
        private Map<String, MyDownloadTask> downloadTasks;
        private final String urlLink;
        private final String filePath;
        private DownloadListener downloadListener;
        private BaseDownloadTask realTask;

        public MyDownloadTask(Map<String, MyDownloadTask> downloadTasks, String urlLink, String filePath, DownloadListener downloadListener) {
            this.downloadTasks = downloadTasks;
            this.urlLink = urlLink;
            this.filePath = filePath;
            this.downloadListener = downloadListener;
        }

        private void start(){
            realTask = FileDownloader.getImpl().create(urlLink)
                    .setPath(filePath)
                    .setCallbackProgressTimes(300)
                    .setMinIntervalUpdateSpeed(400)
                    .setListener(new FileDownloadSampleListener(){
                        @Override
                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            LogUtils.d(TAG, "准备下载");
                            File des = new File(filePath);
                            if(!des.getParentFile().exists()){
                                des.getParentFile().mkdirs();
                            }
                            if(!des.exists()){
                                try {
                                    des.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    LogUtils.e(TAG, "创建文件失败");
                                    notifyFailed("create file failed !");
                                    pause();
                                    return;
                                }
                            }
                            notifyStart();
                        }

                        @Override
                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                            LogUtils.d(TAG, "正在下载..");
                            if (totalBytes == -1) {
                                // chunked transfer encoding data
                                LogUtils.d(TAG, "需要下载的totalByte为-1，错误");
                            } else {
                                int realPro = (int)(100f * soFarBytes / totalBytes);
                                LogUtils.d(TAG, "progress -->  = soFarBytes " + (soFarBytes / 1024 / 1024)
                                        + " totalBytes = " + (totalBytes / 1024 / 1024)
                                        + " percent = " + realPro);
                            }
                            notifyProgress(soFarBytes, totalBytes);
                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {
                            LogUtils.d(TAG, "下载完成！" + filePath);
                            notifyComplete(filePath);
                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            LogUtils.e(TAG, "下载异常！" + (e != null ? e.getMessage() : ""));
                            notifyFailed(e != null ? e.getMessage() : "download failed !");
                        }
                    });

            int taskId = realTask.start();
        }

        private void pause(){
            if(realTask != null && realTask.isRunning()){
                realTask.pause();
                realTask = null;
            }
            detach();
        }

        private void detach(){
            if(downloadTasks != null){
                downloadTasks.remove(urlLink);
                downloadTasks = null;
            }
            downloadListener = null;
        }
        
        private void notifyStart(){
            mainHandler.post(()->{
                if(downloadListener != null){
                    downloadListener.onDownloadStart();
                }
            });
        }

        private void notifyProgress(long cur, long total){
            mainHandler.post(()->{
                if(downloadListener != null){
                    downloadListener.onDownloadProgress(cur, total);
                }
            });
        }

        private void notifyComplete(String filePath){
            mainHandler.post(()->{
                if(downloadListener != null){
                    downloadListener.onDownloadComplete(filePath);
                }
            });
            detach();
        }

        private void notifyFailed(String err){
            mainHandler.post(()->{
                if(downloadListener != null){
                    downloadListener.onDownloadFailed(err);
                }
            });
            detach();
        }

    }

}
