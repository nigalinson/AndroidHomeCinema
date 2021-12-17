package com.sloth.functions.mvp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      16/5/11 下午3:04
 * Description: mvp 架构 ActionBarActivity 基础类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 16/5/11      ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public abstract class RYBaseMvpActivity<P extends RYBasePresenter> extends AppCompatActivity {

    protected P mPresenter;

    protected abstract P createPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if(savedInstanceState != null){
            if(mPresenter != null){
                mPresenter.onSaveInstanceRestored(savedInstanceState);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(mPresenter != null){
            mPresenter.onRestoreInstance(savedInstanceState);
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        super.onDestroy();
    }
}
