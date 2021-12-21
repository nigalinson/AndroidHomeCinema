package com.sloth.torrent;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.download.AbsDownloadManager;
import com.sloth.functions.download.AbsDownloadTask;
import com.sloth.functions.download.DownloadUtils;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.FileUtils;
import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/20 14:44
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = DownloadManager.class, key = Strategies.DownloadEngine.TORRENT, singleton = true)
public class TorrentDownloadManager extends AbsDownloadManager<TorrentStream> {
    private static final String TAG = "TorrentDownloadManager";

    @Override
    protected AbsDownloadTask<TorrentStream> makeDownloadTask(String url, String filePath, DownloadListener downloadListener) {
        String folder = Objects.requireNonNull(new File(filePath).getParentFile()).getAbsolutePath();
        String fileName = FileUtils.getFileName(filePath);
        String fileNameNoExtension = FileUtils.getFileNameNoExtension(filePath);
        return new DownloadTask(downloadTasks, url, folder, fileNameNoExtension, fileName, downloadListener);
    }

    private static class DownloadTask extends AbsDownloadTask<TorrentStream> {

        private final String realFolder;

        private final String subFolder;

        private final String fileName;

        public DownloadTask(Map<String, AbsDownloadTask<TorrentStream>> downloadTasks, String urlLink, String realFolder, String subFolder, String fileName, DownloadListener downloadListener) {
            super(downloadTasks, urlLink, realFolder + "/" + subFolder + "/" + fileName, downloadListener);
            this.realFolder = realFolder;
            this.subFolder = subFolder;
            this.fileName = fileName;
        }

        @Override
        protected TorrentStream makeClient() {
            TorrentOptions torrentOptions = new TorrentOptions.Builder()
                    .saveLocation(Objects.requireNonNull(new File(filePath).getParentFile()).getAbsolutePath())
                    .removeFilesAfterStop(true)
                    .build();
            return TorrentStream.init(torrentOptions);
        }

        @Override
        protected void onDownloading(TorrentStream client) {
            client.addListener(torrentListener);
            client.startStream(urlLink);
        }

        @Override
        protected void terminateClient(TorrentStream client) {
            if(client != null){
                client.removeListener(torrentListener);
                client.stopStream();
            }
        }

        private final TorrentListener torrentListener = new TorrentListener() {
            @Override
            public void onStreamPrepared(Torrent torrent) { }

            @Override
            public void onStreamStarted(Torrent torrent) {
                notifyStart();
            }

            @Override
            public void onStreamError(Torrent torrent, Exception e) {
                notifyFailed(e.getMessage());
            }

            @Override
            public void onStreamReady(Torrent torrent) { }

            @Override
            public void onStreamProgress(Torrent torrent, StreamStatus status) {
                notifyProgress((long) (status.progress * 100), 100);
            }

            @Override
            public void onStreamStopped() {
                // torrent file has no absolute file name, so we need to find aim file and rename it
                File subFile = new File(realFolder + "/" + subFolder);
                File downFile = DownloadUtils.findMovie(subFile);
                if(downFile != null){
                    FileUtils.move(downFile, new File(realFolder + "/" + fileName));
                    notifyComplete(realFolder + "/" + fileName);
                }else{
                    notifyFailed("result file not found !");
                }
            }
        };
    }

}
