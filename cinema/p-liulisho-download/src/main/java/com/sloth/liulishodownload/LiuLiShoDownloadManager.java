package com.sloth.liulishodownload;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.download.AbsDownloadManager;
import com.sloth.functions.download.AbsDownloadTask;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.Map;

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
public class LiuLiShoDownloadManager extends AbsDownloadManager<BaseDownloadTask> {

    private static final String TAG = "LiuLiShoDownloadManager";

    @Override
    protected AbsDownloadTask<BaseDownloadTask> makeDownloadTask(String url, String filePath, DownloadListener downloadListener) {
        return new LiuLiShoTask(downloadTasks, url, filePath, downloadListener);
    }

    private static final class LiuLiShoTask extends AbsDownloadTask<BaseDownloadTask> {

        public LiuLiShoTask(Map<String, AbsDownloadTask<BaseDownloadTask>> downloadTasks, String urlLink, String filePath, DownloadListener downloadListener) {
            super(downloadTasks, urlLink, filePath, downloadListener);
        }

        @Override
        protected void terminateClient(BaseDownloadTask client) {
            if(client != null){
                client.pause();
            }
        }

        @Override
        protected BaseDownloadTask makeClient() {
            return FileDownloader.getImpl().create(urlLink)
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
                                    client.pause();
                                    notifyFailed("create file failed !");
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
        }

        @Override
        protected void onDownloading(BaseDownloadTask client) {
            client.start();
        }

    }

}
