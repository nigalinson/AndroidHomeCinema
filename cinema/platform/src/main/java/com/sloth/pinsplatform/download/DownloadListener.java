package com.sloth.pinsplatform.download;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 17:48
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface DownloadListener {

    void onDownloadStart();

    void onDownloadProgress(long current, long total);

    void onDownloadComplete(String filePath);

    void onDownloadFailed(String errCode);

}
