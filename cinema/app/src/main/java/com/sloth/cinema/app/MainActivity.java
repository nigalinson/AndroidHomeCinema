package com.sloth.cinema.app;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sankuai.waimai.router.Router;
import com.sloth.cinema.IWebServiceCallbackInterface;
import com.sloth.cinema.IWebServiceInterface;
import com.sloth.cinema.R;
import com.sloth.cinema.service.WebService;
import com.sloth.crawler.InfoCrawler;
import com.sloth.crawler.YinFansCrawler;
import com.sloth.functions.download.DownloadConstants;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.GsonUtils;
import com.sloth.tools.util.LogUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_PEM_CODE = 999;

    private IWebServiceInterface serviceProxy;

    private AppCompatTextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = findViewById(R.id.msg);
        findViewById(R.id.btn_open).setOnClickListener(v -> ifPermissionOk());
        findViewById(R.id.btn_close).setOnClickListener(v -> stopAll());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PEM_CODE){
            for(int res: grantResults){
                if(res != PackageManager.PERMISSION_GRANTED){
                    //未授权
                    finish();
                    break;
                }
            }
            onPermissionOk();
        }
    }

    private void ifPermissionOk() {
        if(ContextCompat.checkSelfPermission(this,  Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            LogUtils.d(TAG, "request permission");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            }, REQUEST_PEM_CODE);
        }else{
            onPermissionOk();
        }
    }

    private void stopAll() {
        if(serviceProxy != null){
            unbindService(serviceConnection);
            stopService(new Intent(this, WebService.class));
        }
    }

    private void onPermissionOk() {
        LogUtils.d(TAG, "permission ok !");
        try{
            appendMessage("prepare to start...");
            startService(new Intent(this, WebService.class));
            bindService(new Intent(this, WebService.class), serviceConnection, BIND_AUTO_CREATE);
        }catch (IllegalStateException e){
            LogUtils.e(TAG, "please open when resumed !");
            finish();
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceProxy = IWebServiceInterface.Stub.asInterface(service);
            try {
                serviceProxy.addCallback(serviceCallback);
                serviceProxy.start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                serviceProxy.removeCallback(serviceCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            serviceProxy = null;
        }
    };

    private final IWebServiceCallbackInterface serviceCallback = new IWebServiceCallbackInterface.Stub(){
        @Override
        public void onServiceMessage(String msg) throws RemoteException {
            appendMessage(msg);
        }
    };

    private void appendMessage(String msg) {
        if(!TextUtils.isEmpty(tvMsg.getText()) && tvMsg.getText().length() > 1000){
            tvMsg.setText("");
        }
        tvMsg.append(msg + "\n");
    }

    @Override
    protected void onResume() {
        super.onResume();

//        testCrawler();

//        testDownload();

        testInfoFinder();

    }

    private void testCrawler() {
        new Thread(() -> new YinFansCrawler(1, "黑客帝国3", 5).listener((id, name, url) -> {
            System.out.println("find url: " + url);
        }).run()).start();
    }

    private void testDownload() {
        String url1 = "https://rarbgmirror.com/download.php?id=w95k8h4&h=e66&f=The.Matrix.4.Resurrections.2021.1080p.WEBRip.x265-RARBG-[rarbg.to].torrent";
        String url2 = "magnet:?xt=urn:btih:3594336353964980fa87a6c9ae8a8e9985d9d418&dn=%e9%98%b3%e5%85%89%e7%94%b5%e5%bd%b1www.ygdy8.com.%e6%af%8d%e4%ba%b2%e6%9c%ba%e5%99%a8%e4%ba%ba.2021.BD.1080P.%e4%b8%ad%e8%8b%b1%e5%8f%8c%e5%ad%97.mkv&tr=udp%3a%2f%2ftracker.opentrackr.org%3a1337%2fannounce&tr=udp%3a%2f%2fexodus.desync.com%3a6969%2fannounce";
        String url3 = "magnet:?xt=urn:btih:8b5f63932ae489b6719417a1b0d8aaab522a7eb2";

        Router.getService(DownloadManager.class, Strategies.DownloadEngine.TORRENT)
                .download(url3, DownloadConstants.downloadMovieFilePath(1234), new DownloadListener() {
                    @Override
                    public void onDownloadStart() {
                        System.out.println("start download");
                    }

                    @Override
                    public void onDownloadProgress(long current, long total) {
                        System.out.println("progress: " + current + "/" + total);
                    }

                    @Override
                    public void onDownloadComplete(String filePath) {
                        System.out.println("download complete: " + filePath);
                    }

                    @Override
                    public void onDownloadFailed(String errCode) {
                        System.out.println("download failed: " + errCode);
                    }
                });
    }

    private void testInfoFinder() {
        new Thread(() -> new InfoCrawler(1, "黑客帝国3").listener((id, infoMap) -> {
            System.out.println("find info: " + GsonUtils.toJson(infoMap));
        }).run()).start();
    }


}