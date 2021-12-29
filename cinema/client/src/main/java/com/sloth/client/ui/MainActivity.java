package com.sloth.client.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.sloth.arp.ARPUtils;
import com.sloth.client.R;
import com.sloth.client.app.AppConstants;
import com.sloth.client.data.DataListener;
import com.sloth.client.data.Repository;
import com.sloth.ifilm.Film;
import com.sloth.tools.util.ByteDanceDpiUtils;
import com.sloth.tools.util.ExecutorUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.SPUtils;
import com.sloth.tools.util.StringUtils;
import com.sloth.tools.util.ToastUtils;
import com.sloth.udp.UDPManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DataListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_PEM_CODE = 999;

    private Repository repository;

    private UDPManager udpManager;

    private final List<String> ipList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ByteDanceDpiUtils.setCustomDensity(getResources());
        setContentView(R.layout.activity_main);
        repository = new Repository(this, this);
        initView();
        ifPermissionOk();
    }

    private void initView() {
        findViewById(R.id.view_list).setOnClickListener(this);
        findViewById(R.id.view_add).setOnClickListener(this);
        findViewById(R.id.view_setting).setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.view_list){
            toList();
        }else if(v.getId() == R.id.view_add){
            AppCompatEditText et = new AppCompatEditText(this);
            et.setHint("请输入电影全名..");
            AlertDialog dg = new AlertDialog.Builder(this).setView(et)
                    .setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                    }).setPositiveButton("OK", (dialog, which) -> {
                        if(StringUtils.isEmpty(et.getText())){
                            ToastUtils.showShort("please input something !");
                        }else{
                            dialog.dismiss();
                            repository.addFilm(et.getText().toString());
                        }
                    }).create();
            dg.show();
            ByteDanceDpiUtils.adjustDialogSize(MainActivity.this, dg);
        }else if(v.getId() == R.id.view_setting){
            ToastUtils.showLong("Searching for servers ..");

            ipList.clear();

            if(udpManager == null){
                udpManager = new UDPManager(this, Looper.getMainLooper());
                udpManager.listen(ipList::add);
            }

            ExecutorUtils.getNormal().execute(new ExecutorUtils.WorkRunnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(()->{
                        udpManager.destroy();
                        udpManager = null;
                        AlertDialog dg = new AlertDialog.Builder(MainActivity.this).setItems(ipList.toArray(new String[]{}), (dialog, which) -> {
                            dialog.dismiss();
                            String ip = ARPUtils.parseARP(ipList.get(which));
                            if(StringUtils.notEmpty(ip)){
                                String host = "http://" + ip + ":8888/";
                                SPUtils.getInstance().put(AppConstants.SP_IP_HOST, host);
                                if(repository != null){
                                    repository.changeHost(host);
                                }
                            }
                        }).create();
                        dg.show();
                        ByteDanceDpiUtils.adjustDialogSize(MainActivity.this, dg);
                    });
                }
            });

        }
    }

    private void toList() {
        startActivity(new Intent(this, ListActivity.class));
    }


    @Override
    public void getFilmListSuccess(List<Film> data) { }

    @Override
    public void getFilmListFailed(String message) { }

    @Override
    public void loadMoreFilmListSuccess(List<Film> data) { }

    @Override
    public void loadMoreFilmListFailed(String message) { }

    @Override
    public void toast(String message) {
        ToastUtils.showShort(message);
    }
}