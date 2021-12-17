package com.sloth.functions;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TintContextWrapper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import com.sloth.tools.util.LogUtils;
import org.greenrobot.eventbus.EventBus;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/8/21 14:46
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/8/21         Carl            1.0                    1.0
 * Why & What is modified:
 * 用来统一管理需要回收的组件的生命周期，绑定宿主生命周期对象，在destroy时自动解绑组件
 */
public class AutoDispose implements LifecycleObserver{

    private static final String TAG = AutoDispose.class.getSimpleName();

    /**
     * 宿主Context
     */
    private Context mContext;

    /**
     * 主生命周期宿主
     */
    private LifecycleOwner lifecycleOwner;

    /**
     * EventBus
     */
    private List<Object> evBus;

    /**
     * 用来管理需要自动解绑的RX组件
     */
    private CompositeDisposable mCompositeDisposable;

    /**
     * butterKnife
     */
    private List<Unbinder> unbinders;

    /**
     * 广播
     */
    private List<BroadcastReceiver> receivers;

    /**
     * 服务
     */
    private List<ServiceConnection> serviceConnections;

    /**
     * 实现自动注销的类
     */
    private List<AutoDisposable> autoDisposables;

    private AutoDispose() { }

    /**
     * 最好使用from pool(String) 减少对象生成次数
     * @return
     */
    public static AutoDispose newIns(){
        return Pool.fromPool();
    }

    public static AutoDispose fromPool(String key){
        return Pool.fromPool(key);
    }

    /**
     * 实例池
     */
    private static final class Pool{
        //缓存池最大值，未达到最大值时，回收的autoDispose全部缓存下来
        private static int POOL_SIZE = 20;

        private static final Map<String, WeakReference<AutoDispose>> usingPool = new ConcurrentHashMap<>();
        private static final Queue<AutoDispose> recycledPool = new LinkedBlockingQueue<>(POOL_SIZE);

        public static void setPoolSize(int size){
            POOL_SIZE = size;
        }

        public static void recycle(AutoDispose autoDispose){
            if(recycledPool.size() < POOL_SIZE){
                recycledPool.add(autoDispose);
            }else{
                LogUtils.i(TAG, "缓存池已满，直接销毁不继续保存");
            }
        }

        public static AutoDispose fromPool(){

            //从缓存池取一个
            if(!recycledPool.isEmpty()){
                return recycledPool.poll();
            }

            //缓存池也空了 - 新建一个
            return new AutoDispose();
        }

        public static AutoDispose fromPool(String key){
            //已在使用中，直接获取
            if(usingPool.get(key) != null && usingPool.get(key).get() != null){
                return usingPool.get(key).get();
            }

            //没有使用中的，从缓存池取一个
            if(!recycledPool.isEmpty()){
                AutoDispose ins = recycledPool.poll();
                usingPool.put(key, new WeakReference<>(ins));
                return ins;
            }

            //缓存池也空了 - 新建一个
            AutoDispose ins = new AutoDispose();
            usingPool.put(key, new WeakReference<>(ins));
            return ins;
        }
    }

    /**
     * @warning bind请在onCreate生命周期中执行，
     * 否则activity异常onDestroy时，清空了成员变量不重新初始化将造成异常
     * @param context
     * @param lifecycleOwner
     * @return
     */
    public AutoDispose bind(Context context, LifecycleOwner lifecycleOwner) {
        if(this.lifecycleOwner != null){
            //已经绑定过，是复用，不用另外绑定
            return this;
        }

        this.mContext = context;
        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleOwner.getLifecycle().addObserver(this);
        return this;
    }

    public AutoDispose bind(LifecycleOwner lifecycleOwner) {
        if(this.lifecycleOwner != null){
            //已经绑定过，是复用，不用另外绑定
            return this;
        }

        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleOwner.getLifecycle().addObserver(this);
        return this;
    }

    public AutoDispose bind(Context context) {
        if(this.lifecycleOwner != null){
            //已经绑定过，是复用，不用另外绑定
            return this;
        }

        if(context instanceof TintContextWrapper){
            context = ((TintContextWrapper)context).getBaseContext();
        }

        if(context instanceof AppCompatActivity){
            this.mContext = context;
            this.lifecycleOwner = ((AppCompatActivity)context);
            this.lifecycleOwner.getLifecycle().addObserver(this);
        }else{
            throw new RuntimeException("autoDispose绑定失败，无效的宿主" + context.getClass().getSimpleName());
        }
        return this;
    }

    private void assertAttachedToLifecycle(){
        if(this.lifecycleOwner == null){
            throw new RuntimeException("请先绑定生命周期，否则不会触发onDestroy()，造成内存泄漏！！");
        }
    }

    public AutoDispose autoDispose(AutoDisposable autoDisposable){
        assertAttachedToLifecycle();
        if(autoDisposables == null){
            autoDisposables = new ArrayList<>();
        }
        autoDisposables.add(autoDisposable);
        return this;
    }

    public AutoDispose rx(Disposable disposable){
        assertAttachedToLifecycle();
        if(mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }

        mCompositeDisposable.add(disposable);
        return this;
    }

    public AutoDispose rx(Disposable... disposables){
        assertAttachedToLifecycle();
        if(mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }
        for(Disposable item: disposables){
            if(item != null){
                mCompositeDisposable.add(item);
            }
        }
        return this;
    }

    public AutoDispose butterKnife(Activity activity){
        assertAttachedToLifecycle();
        if(unbinders == null){
            unbinders = new ArrayList<>();
        }
        unbinders.add(ButterKnife.bind(activity));
        return this;
    }

    public AutoDispose butterKnife(Object target, View view){
        assertAttachedToLifecycle();
        if(unbinders == null){
            unbinders = new ArrayList<>();
        }
        unbinders.add(ButterKnife.bind(target, view));
        return this;
    }

    public AutoDispose butterKnife(Unbinder unbinder){
        assertAttachedToLifecycle();
        if(unbinders == null){
            unbinders = new ArrayList<>();
        }
        unbinders.add(unbinder);
        return this;
    }

    public AutoDispose eventBus(Object evCtx){
        assertAttachedToLifecycle();
        if(!EventBus.getDefault().isRegistered(evCtx)){
            EventBus.getDefault().register(evCtx);
            if(evBus == null){
                evBus = new ArrayList<>();
            }
            evBus.add(evCtx);
        }
        return this;
    }

    public AutoDispose receiver(BroadcastReceiver rcv, IntentFilter intentFilter){
        assertAttachedToLifecycle();
        if(mContext == null){
            throw new RuntimeException("绑定广播请用带Context的构造函数传入");
        }

        if(receivers == null){
            receivers = new ArrayList<>();
        }
        mContext.registerReceiver(rcv, intentFilter);
        receivers.add(rcv);
        return this;
    }

    public AutoDispose service(Intent intent, ServiceConnection connection, int flag){
        assertAttachedToLifecycle();
        if(mContext == null){
            throw new RuntimeException("绑定服务请用带Context的构造函数传入");
        }
        if(serviceConnections == null){
            serviceConnections = new ArrayList<>();
        }
        mContext.bindService(intent, connection, flag);
        serviceConnections.add(connection);
        return this;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){

        //region AutoDisposable
        if(autoDisposables!= null && !autoDisposables.isEmpty()){
            for(AutoDisposable autoDisposable: autoDisposables){
                if(autoDisposable != null){
                    autoDisposable.autoDispose();
                }
            }
            autoDisposables.clear();
        }
        //endregion AutoDisposable

        //region rx
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
        //endregion rx

        //region butterKnife
        if(unbinders != null && !unbinders.isEmpty()){
            for(Unbinder ub: unbinders){
                ub.unbind();
            }
            unbinders.clear();
            unbinders = null;
        }
        //endregion butterKnife

        //region eventBus
        if(evBus != null && evBus.size() > 0){
            Iterator<Object> iterator = evBus.iterator();
            while (iterator.hasNext()){
                Object item = iterator.next();
                if(EventBus.getDefault().isRegistered(item)){
                    EventBus.getDefault().unregister(item);
                }
                iterator.remove();
            }
            evBus = null;
        }
        //endregion eventBus

        //region receivers
        if(receivers != null && !receivers.isEmpty()){
            if(mContext != null){
                for(BroadcastReceiver rv: receivers){
                    mContext.unregisterReceiver(rv);
                }
                receivers.clear();
            }
            receivers = null;
        }
        //endregion receivers

        //region service
        if(serviceConnections != null && !serviceConnections.isEmpty()){
            if(mContext != null){
                for(ServiceConnection sc: serviceConnections){
                    mContext.unbindService(sc);
                }
                serviceConnections.clear();
            }
            serviceConnections = null;
        }

        //endregion service

        //region recycler Myself
        if(lifecycleOwner != null){
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
        lifecycleOwner = null;
        mCompositeDisposable = null;
        mContext = null;

        //endregion recycler Myself

        //回收
        Pool.recycle(this);
    }

    public interface AutoDisposable{
        void autoDispose();
    }

}
