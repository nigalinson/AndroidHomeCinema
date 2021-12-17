package com.sloth.cinema.push;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.sloth.cinema.BuildConfig;
import com.sloth.functions.dynamicbean.DynamicBean;
import com.sloth.push.PushData;
import com.sloth.tools.util.GsonUtils;
import com.sloth.tools.util.StringUtils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;

/**
 * PushSDK集成帮助类
 */
public class PushHelper {

    private static final String TAG = "PushHelper";

    /**
     * 预初始化
     */
    public static void preInit(Context context) {
        //解决厂商通知点击时乱码等问题
        PushAgent.setup(context, PushConstants.APP_KEY, PushConstants.MESSAGE_SECRET);
        UMConfigure.preInit(context, PushConstants.APP_KEY, PushConstants.CHANNEL);
    }

    /**
     * 初始化
     */
    public static void init(final Context context) {
        // 基础组件包提供的初始化函数，应用配置信息：http://message.umeng.com/list/apps
        // 参数一：上下文context；
        // 参数二：应用申请的Appkey；
        // 参数三：发布渠道名称；
        // 参数四：设备类型，UMConfigure.DEVICE_TYPE_PHONE：手机；UMConfigure.DEVICE_TYPE_BOX：盒子；默认为手机
        // 参数五：Push推送业务的secret，填写Umeng Message Secret对应信息
        UMConfigure.init(context, PushConstants.APP_KEY, PushConstants.CHANNEL,
                UMConfigure.DEVICE_TYPE_PHONE, PushConstants.MESSAGE_SECRET);

        //获取推送实例
        PushAgent pushAgent = PushAgent.getInstance(context);

        //TODO:需修改为您app/src/main/AndroidManifest.xml中package值
        pushAgent.setResourcePackageName(BuildConfig.APPLICATION_ID);

        //推送设置
        pushSetting(context);

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new UPushRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG, "deviceToken --> " + deviceToken);
            }

            @Override
            public void onFailure(String errCode, String errDesc) {
                Log.e(TAG, "register failure：--> " + "code:" + errCode + ",desc:" + errDesc);
            }
        });
    }


    //推送设置
    private static void pushSetting(Context context) {
        PushAgent pushAgent = PushAgent.getInstance(context);

        //设置通知栏显示通知的最大个数（0～10），0：不限制个数
        pushAgent.setDisplayNotificationNumber(0);

        //推送消息处理
        UmengMessageHandler msgHandler = new UmengMessageHandler() {
            //处理通知栏消息
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                super.dealWithNotificationMessage(context, msg);
                Log.i(TAG, "notification receiver:" + msg.getRaw().toString());
            }

            //自定义通知样式，此方法可以修改通知样式等
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                return super.getNotification(context, msg);
            }

            //处理透传消息
            @Override
            public void dealWithCustomMessage(Context context, UMessage msg) {
                super.dealWithCustomMessage(context, msg);
                //{"display_type":"custom","body":{"custom":"{notify}"},"msg_id":"usj5bhj163973625149411"}
                Log.i(TAG, "um receiver:" + msg.getRaw().toString());
                String diy = DynamicBean.parse(msg.getRaw().toString()).get("body").get("custom").toStr();
                if(StringUtils.notEmpty(diy)){
                    PushData pushData = GsonUtils.fromJson(diy, PushData.class);
                    if(pushData != null && pushData.getType() != PushData.Type.ERR){
                        Intent it = new Intent(PushConstants.BROADCAST_ACTION);
                        it.putExtra("data", pushData);
                        context.sendBroadcast(it);
                    }
                }
            }
        };
        pushAgent.setMessageHandler(msgHandler);

        //推送消息点击处理
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
                Log.i(TAG, "click openActivity: " + msg.getRaw().toString());
            }

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                Log.i(TAG, "click launchApp: " + msg.getRaw().toString());
            }

            @Override
            public void dismissNotification(Context context, UMessage msg) {
                super.dismissNotification(context, msg);
                Log.i(TAG, "click dismissNotification: " + msg.getRaw().toString());
            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);
    }

}
