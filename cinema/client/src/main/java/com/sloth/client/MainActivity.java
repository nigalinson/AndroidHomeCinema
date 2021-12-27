package com.sloth.client;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sloth.tools.util.ByteDanceDpiUtils;
import com.sloth.tools.util.LogUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_PEM_CODE = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ByteDanceDpiUtils.setCustomDensity(getResources());
        setContentView(R.layout.activity_main);
        ifPermissionOk();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PEM_CODE){
            for(int i: grantResults){
                if(i != PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
            onPermissionOk();
        }
    }

    private void onPermissionOk() {
        if(ContextCompat.checkSelfPermission(this,  Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }

        LogUtils.d(TAG, "permission ok !");

        play();
    }

    private void play() {


    }

}