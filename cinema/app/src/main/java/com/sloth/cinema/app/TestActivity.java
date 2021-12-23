package com.sloth.cinema.app;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/23 19:04
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/23         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

//        testCrawler();

//        testDownload();

//        testInfoFinder();

    }

//    private void testCrawler() {
//        new Thread(() -> new YinFansCrawler(1, "黑客帝国", 5).listener((id, name, url) -> {
//            System.out.println("find url: " + url);
//        }).run()).start();
//    }
//
//    private void testDownload() {
//        String url1 = "https://rarbgmirror.com/download.php?id=w95k8h4&h=e66&f=The.Matrix.4.Resurrections.2021.1080p.WEBRip.x265-RARBG-[rarbg.to].torrent";
//        String url2 = "magnet:?xt=urn:btih:3594336353964980fa87a6c9ae8a8e9985d9d418&dn=%e9%98%b3%e5%85%89%e7%94%b5%e5%bd%b1www.ygdy8.com.%e6%af%8d%e4%ba%b2%e6%9c%ba%e5%99%a8%e4%ba%ba.2021.BD.1080P.%e4%b8%ad%e8%8b%b1%e5%8f%8c%e5%ad%97.mkv&tr=udp%3a%2f%2ftracker.opentrackr.org%3a1337%2fannounce&tr=udp%3a%2f%2fexodus.desync.com%3a6969%2fannounce";
//        String url3 = "magnet:?xt=urn:btih:8b5f63932ae489b6719417a1b0d8aaab522a7eb2";
//
//        Router.getService(DownloadManager.class, Strategies.DownloadEngine.TORRENT)
//                .download(url3, DownloadConstants.downloadMovieFilePath(1234), new DownloadListener() {
//                    @Override
//                    public void onDownloadStart() {
//                        System.out.println("start download");
//                    }
//
//                    @Override
//                    public void onDownloadProgress(long current, long total) {
//                        System.out.println("progress: " + current + "/" + total);
//                    }
//
//                    @Override
//                    public void onDownloadComplete(String filePath) {
//                        System.out.println("download complete: " + filePath);
//                    }
//
//                    @Override
//                    public void onDownloadFailed(String errCode) {
//                        System.out.println("download failed: " + errCode);
//                    }
//                });
//    }
//
//    private void testInfoFinder() {
//        new Thread(() -> new InfoCrawler(1, "黑客帝国3").listener((id, infoMap) -> {
//            System.out.println("find info: " + GsonUtils.toJson(infoMap));
//        }).run()).start();
//    }

}
