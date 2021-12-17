package com.sloth.functions.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      16/5/9 下午1:49
 * Description: mvp 架构 Fragment 基础类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 16/5/9      ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public abstract class RYBaseMvpFragment<P extends RYBasePresenter> extends Fragment {
    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if(savedInstanceState != null){
            if(mPresenter != null){
                mPresenter.onSaveInstanceRestored(savedInstanceState);
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(mPresenter != null){
            mPresenter.onRestoreInstance(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        super.onDestroy();
    }

    protected abstract P createPresenter();
}
