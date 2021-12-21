package com.sloth.functions.download;

import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.tools.util.ExecutorUtils;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/21 11:10
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/21         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class AbsDownloadTask<T> extends ExecutorUtils.WorkRunnable {
    protected Map<String, AbsDownloadTask<T>> downloadTasks;
    protected final String urlLink;
    protected final String filePath;
    protected DownloadListener downloadListener;

    protected T client;

    protected final AtomicBoolean running = new AtomicBoolean(true);

    public AbsDownloadTask(Map<String, AbsDownloadTask<T>> downloadTasks, String urlLink, String filePath, DownloadListener downloadListener) {
        this.downloadTasks = downloadTasks;
        this.urlLink = urlLink;
        this.filePath = filePath;
        this.downloadListener = downloadListener;
    }

    public void terminate(){
        terminateClient(client);
        detach();
        running.set(false);
    }

    public void terminateButRemainIn(){
        terminateClient(client);
        running.set(false);
    }

    protected abstract void terminateClient(T client);

    private void detach(){
        if(downloadTasks != null){
            downloadTasks.remove(urlLink);
            downloadTasks = null;
        }
        downloadListener = null;
    }

    @Override
    public void run() {
        notifyStart();
        onDownloading(client = makeClient());
    }

    protected abstract T makeClient();

    protected abstract void onDownloading(T client);

    protected void notifyStart(){
        runOnUiThread(()->{
            if(downloadListener != null){
                downloadListener.onDownloadStart();
            }
        });
    }

    protected void notifyProgress(long cur, long total){
        if(downloadListener != null){
            downloadListener.onDownloadProgress(cur, total);
        }
    }

    protected void notifyComplete(String filePath){
        runOnUiThread(()->{
            if(downloadListener != null){
                downloadListener.onDownloadComplete(filePath);
            }
        });
        detach();
    }

    protected void notifyFailed(String err){
        runOnUiThread(()->{
            if(downloadListener != null){
                downloadListener.onDownloadFailed(err);
            }
        });
        detach();
    }
}
