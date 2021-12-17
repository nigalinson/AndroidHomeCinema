package com.sloth.banner.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import com.sloth.banner.R;
import com.sloth.banner.data.Playable;
import com.sloth.banner.vh.PagerViewHolder;
import com.sloth.banner.vh.PlayerViewHolder;
import com.sloth.functions.AutoDispose;
import com.sloth.functions.adapter.BaseViewHolder;
import com.sloth.functions.image.RYImageLoader;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.StringUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/20 15:13
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class SimpleBannerAdapter<T extends Playable> extends BannerAdapter<BaseViewHolder<T>, T> {

    public SimpleBannerAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemData(position).mediaType();
    }

    @Override
    protected int layoutResId(int viewType) {
        int resId = -1;
        if(viewType == Playable.MediaType.Video.type){
            resId = R.layout.item_vh_default_video;
        }else if(viewType == Playable.MediaType.Image.type){
            resId = R.layout.item_vh_default_image;
        }else if(viewType == Playable.MediaType.Web.type){
            resId = R.layout.item_vh_default_webview;
        }else{
            resId = createOtherLayoutId(viewType);
        }
        return resId;
    }

    protected int createOtherLayoutId(int viewType) {
        return R.layout.item_vh_default_image;
    }

    @Override
    public BaseViewHolder<T> onCreateViewHolder(int viewType, @NonNull View itemView) {
        BaseViewHolder<T> vh = null;
        if(viewType == Playable.MediaType.Video.type){
            vh = new SimpleVideoHolder<T>(itemView);
        }else if(viewType == Playable.MediaType.Image.type){
            vh = new SimpleGlideVH<T>(itemView);
        }else if(viewType == Playable.MediaType.Web.type){
            vh = new SimpleWebViewViewHolder<>(itemView);
        }else{
            vh = createOtherViewHolder(viewType, itemView);
        }

        onViewHolderCreated(vh);
        System.out.println("onCreateViewHolder:" + vh.getClass().getSimpleName() + "," + vh.hashCode());
        return vh;
    }

    @Override
    public void onViewRecycled(@NonNull BaseViewHolder<T> holder) {
        System.out.println("onViewRecycled:" + holder.getClass().getSimpleName() + "," + holder.hashCode());
        super.onViewRecycled(holder);
    }

    protected BaseViewHolder<T> createOtherViewHolder(int viewType, View itemView) {
        return new SimpleGlideVH<T>(itemView);
    }

    protected void onViewHolderCreated(BaseViewHolder<T> vh) { }

    public static class SimpleGlideVH<T extends Playable> extends PagerViewHolder<T> {

        public ImageView iv;

        public SimpleGlideVH(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
        }

        @Override
        public void onPreLoad(T data) { }

        @Override
        public void onLoaded(T data) { }

        @Override
        public void onClose(T data) { }

        @Override
        public void bindViewData(T data) {
            if(data == null) return;
            //GlideRequest 随Context生命周期销毁，长时间停留在一个页面会造成leak
            RYImageLoader.with(iv.getContext()).loadLocal(data.localPath()).load(data.webUrl()).load(data.resId()).into(iv);
        }
    }

    public static class SimpleVideoHolder<T extends Playable> extends PlayerViewHolder<T> {

        public SimpleVideoHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected FrameLayout initVideoContainer(View itemView) {
            return itemView.findViewById(R.id.container);
        }

        @Override
        protected AppCompatImageView initVideoPreview(View itemView) {
            return itemView.findViewById(R.id.preview);
        }


        @Override
        public void bindViewData(T data) { }
    }

    public static class SimpleWebViewViewHolder<T extends Playable> extends PagerViewHolder<T> implements AutoDispose.AutoDisposable {

        private static final String TAG = SimpleWebViewViewHolder.class.getSimpleName();

        private WebView webView;

        public SimpleWebViewViewHolder(View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            AutoDispose.fromPool(context.getClass().getSimpleName()).bind(context).autoDispose(this);
            webView = itemView.findViewById(R.id.webview);
            initWebView();
        }

        @Override
        public void bindViewData(T data) {
            if(webView != null){
                if(!StringUtils.isEmpty(data.localPath())){
                    webView.loadUrl(data.localPath());
                }else if(!StringUtils.isEmpty(data.webUrl())){
                    webView.loadUrl(data.webUrl());
                }else{
                    LogUtils.e(TAG, "无有效网页地址");
                }
            }
        }

        @Override
        public void onPreLoad(T data) { }

        @Override
        public void onLoaded(T data) {
            if(webView != null){
                webView.onResume();
            }
        }

        @Override
        public void onClose(T data) {
            if(webView != null){
                webView.onPause();
            }
        }

        private void initWebView() {
            WebSettings mWebSettings = webView.getSettings();
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            mWebSettings.setSupportZoom(true);
            mWebSettings.setLoadWithOverviewMode(true);
            mWebSettings.setUseWideViewPort(true);
            mWebSettings.setJavaScriptEnabled(true);
            mWebSettings.setDefaultTextEncodingName("GBK");
            mWebSettings.setSupportMultipleWindows(false);
            mWebSettings.setLoadsImagesAutomatically(true);
            mWebSettings.setDomStorageEnabled(true);
            mWebSettings.setDatabaseEnabled(true);
            mWebSettings.setAppCacheEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWebSettings.setAllowUniversalAccessFromFileURLs(true);
                mWebSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mWebSettings.setMediaPlaybackRequiresUserGesture(false);
            }
            mWebSettings.setAllowFileAccess(true);
            mWebSettings.setDisplayZoomControls(false);
            setWebViewListener();
        }

        private void setWebViewListener() {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public void onPageFinished(WebView view, String url) { }
            });

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(webView);
                    resultMsg.sendToTarget();
                    return true;
                }
            });
        }

        @Override
        public void autoDispose() {
            if(webView != null){
                webView.destroy();
                webView = null;
            }
        }
    }

}
