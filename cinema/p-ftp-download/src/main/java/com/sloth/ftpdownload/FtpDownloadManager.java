package com.sloth.ftpdownload;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.download.AbsDownloadManager;
import com.sloth.functions.download.AbsDownloadTask;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import java.io.File;
import java.util.Map;
import java.util.Objects;

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
@RouterService(interfaces = DownloadManager.class, key = Strategies.DownloadEngine.FTP, singleton = true)
public class FtpDownloadManager extends AbsDownloadManager<FTPClient> {

    @Override
    protected AbsDownloadTask<FTPClient> makeDownloadTask(String url, String filePath, DownloadListener downloadListener) {
        return new DownloadTask(downloadTasks, url, filePath, downloadListener);
    }

    private static final class DownloadTask extends AbsDownloadTask<FTPClient>{

        public DownloadTask(Map<String, AbsDownloadTask<FTPClient>> downloadTasks, String urlLink, String filePath, DownloadListener downloadListener) {
            super(downloadTasks, urlLink, filePath, downloadListener);
        }

        @Override
        protected void terminateClient(FTPClient client) {
            if(client != null){
                FTPUtils.closeFTP(client);
            }
        }

        @Override
        protected FTPClient makeClient() {
            return buildClient(urlLink);
        }

        @Override
        protected void onDownloading(FTPClient client) {
            notifyStart();
            client.setCopyStreamListener(new CopyStreamListener() {
                @Override
                public void bytesTransferred(CopyStreamEvent copyStreamEvent) {
                    long now = copyStreamEvent.getTotalBytesTransferred();
                    long total = copyStreamEvent.getStreamSize();
                    notifyProgress(now, total);
                }

                @Override
                public void bytesTransferred(long l, int i, long l1) {

                }
            });
            FTPUtils.downLoadFTP(
                    client,
                    urlLink,
                    FileUtils.getFileName(filePath),
                    Objects.requireNonNull(new File(filePath).getParentFile()).getAbsolutePath()
            );
            notifyComplete(filePath);
        }

        private FTPClient buildClient(String url) {
            //ftp://guest:guest@61.141.157.87:21
            //ftp://账号:密码@IP地址:端口号
            int headIndex = url.indexOf("ftp://");
            String body = url.substring(headIndex + 6);
            String[] part = body.split("@");
            String[] auths = part[0].split(":");
            String[] uris = part[1].split(":");
            int port = Integer.parseInt(uris[1].substring(0, uris[1].indexOf("/")));
            return FTPUtils.getFTPClient(uris[0], port, auths[0], auths[1]);
        }
    }
}
