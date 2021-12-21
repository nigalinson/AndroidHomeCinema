package com.sloth.functions.download;

import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.ExecutorUtils;
import com.sloth.tools.util.LogUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2017/8/25 下午4:23
 * Description: 文件同步下载工具类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/8/25      ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public abstract class AbsDownloadManager<T> implements DownloadManager {

  private static final String TAG = "AbsDownloadManager";

  protected final Map<String, AbsDownloadTask<T>> downloadTasks = new ConcurrentHashMap<>();

  @Override
  public void download(String url, String filePath, DownloadListener downloadListener) {
    if(!downloadTasks.containsKey(url)){
      AbsDownloadTask<T> task = makeDownloadTask(url, filePath, downloadListener);
      downloadTasks.put(url, task);
      ExecutorUtils.getNormal().submit(task);
    }else{
      LogUtils.e(TAG, "已有下载中的任务");
    }
  }

  protected abstract AbsDownloadTask<T> makeDownloadTask(String url, String filePath, DownloadListener downloadListener);

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
    AbsDownloadTask<T> task = downloadTasks.get(url);
    if(task != null){
      task.terminate();
    }
  }

  @Override
  public void terminateAll() {
    Iterator<AbsDownloadTask<T>> iterator = downloadTasks.values().iterator();
    while(iterator.hasNext()){
      AbsDownloadTask<T> task = iterator.next();
      task.terminateButRemainIn();
      iterator.remove();
    }
  }
}
