package com.sloth.pinsplatform.download;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 17:47
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface DownloadManager {

    void download(String url, String filePath, DownloadListener downloadListener);

    boolean isDownloading(String url);

    int runningTasks();

    void terminate(String url);

    void terminateAll();
}
