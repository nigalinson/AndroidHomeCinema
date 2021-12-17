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
import com.sloth.cinema.IWebServiceCallbackInterface;
import com.sloth.cinema.IWebServiceInterface;
import com.sloth.cinema.R;
import com.sloth.cinema.service.WebService;
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

}