package com.sloth.functions.download;

import android.text.TextUtils;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.GsonUtils;
import com.sloth.tools.util.LogUtils;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

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
@RouterService(interfaces = DownloadManager.class, key = Strategies.DownloadEngine.URL_CONNECTION, singleton = true, defaultImpl = true)
public class DefaultDownloadManager extends AbsDownloadManager<Object> {

  private static final String TAG = "RYDownloadFileHelper";

  @Override
  protected AbsDownloadTask<Object> makeDownloadTask(String url, String filePath, DownloadListener downloadListener) {
    return new DownloadRunnable(downloadTasks, url, filePath, downloadListener);
  }

  private static class DownloadRunnable extends AbsDownloadTask<Object>{

    public DownloadRunnable(Map<String, AbsDownloadTask<Object>> downloadTasks, String urlLink, String filePath, DownloadListener downloadListener) {
      super(downloadTasks, urlLink, filePath, downloadListener);
    }

    @Override
    protected void terminateClient(Object client) { }

    @Override
    protected Object makeClient() {
      return null;
    }

    @Override
    protected void onDownloading(Object client) {
      try {
        LogUtils.d(TAG, "--> 准备下载文件  --> url:" + urlLink + " --> 文件路径:" + filePath);
        notifyStart();
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (!parent.exists()) {
          boolean isSuccess = file.mkdirs();
          LogUtils.d(TAG, "--> 创建下载目录 :" + isSuccess);
        }
        long downSize = 0;
        if (file.exists()) {
          downSize = file.length();
          LogUtils.d(TAG, "--> 文件已存在，已下载大小：" + downSize);
        }else{
          LogUtils.d(TAG, "--> 文件不存在，完全下载");
        }
        URL url = new URL(urlLink);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Connection", "close");
        //Range 字节是从0开始，所以这里需要用文件已下载大小-1
        conn.setRequestProperty("Range", "bytes=" + downSize + "-");
        conn.connect();
        if (conn.getResponseCode() == 404) {//文件不存在
          LogUtils.w(TAG, "--> 远端文件不存在，无法下载或确认本地文件完整性，判定错误！！！  --> 文件地址:" + urlLink);
          conn.disconnect();
          notifyFailed("404 not found !");
          return;
        }
        if (0 == conn.getContentLength()) {
          //http协议返回空，默认已下载完成
          conn.disconnect();
          LogUtils.i(TAG, "--> HTTP返回空数据，认为已无内容需要下载，判定成功  --> 文件路径:" + filePath);
          //发送文件下载成功的广播
          notifyComplete(filePath);
          return;
        }
        long fileSize = 0;
        String contentRange = conn.getHeaderField("Content-Range");
        if (TextUtils.isEmpty(contentRange)) {
          fileSize = conn.getContentLength();
          LogUtils.w(TAG, "--> 不支持断点续传，需要重新下载的文件大小：" + fileSize);
          if (downSize == fileSize) {
            conn.disconnect();
            LogUtils.i(TAG, "--> 本地已存在完整文件，直接判定成功");
            notifyComplete(filePath);
            return;
          }else{
            LogUtils.i(TAG, "--> 本地文件下载不完整，且不支持断点续传，删除历史文件，重新下载");
            downSize = 0;
            FileUtils.delete(filePath);
          }
        } else {
          String[] ranges = contentRange.split("/");
          LogUtils.w(TAG, "--> 断点续传参数：" + GsonUtils.toJson(ranges));
        }

        LogUtils.d(TAG, "--> 正在下载文件！！！");
        InputStream is = conn.getInputStream();

        RandomAccessFile fos = new RandomAccessFile(file, "rwd");
        if (downSize > 0) {
          fos.seek(downSize);
        }
        byte[] buf = new byte[1024];
        int numRead;
        while ((numRead = is.read(buf)) != -1) {
          if (numRead <= 0) {
            break;
          }

          if(!running.get()){
            notifyFailed("cancel download");
            break;
          }

          fos.write(buf, 0, numRead);
          downSize+=numRead;
          notifyProgress(downSize, fileSize);
        }
        fos.close();
        is.close();
        conn.disconnect();
        notifyComplete(filePath);
      } catch (Exception e) {
        e.printStackTrace();
        LogUtils.e(TAG, "--> 文件下载出错！！！  --> e = " + e.getMessage());
        notifyFailed(e.getMessage());
      }
    }

  }

}
