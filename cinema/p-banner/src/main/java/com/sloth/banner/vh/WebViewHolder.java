package com.sloth.banner.vh;

import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.sloth.functions.AutoDispose;
import com.sloth.functions.adapter.BaseViewHolder;
import com.sloth.banner.data.Playable;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.StringUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/9 17:57
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/9         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class WebViewHolder<T extends Playable> extends BaseViewHolder<T> implements AutoDispose.AutoDisposable {

    private static final String TAG = WebViewHolder.class.getSimpleName();

    private WebView webView;

    public WebViewHolder(View itemView, int webViewId) {
        super(itemView);
        Context context = itemView.getContext();
        AutoDispose.fromPool(context.getClass().getSimpleName()).bind(context).autoDispose(this);
        webView = (WebView) itemView.findViewById(webViewId);
        initWebView();
    }

    @Override
    public void bindViewData(T data) {
        if(webView != null){
            if(StringUtils.notEmpty(data.localPath())){
                webView.loadUrl(data.localPath());
            }else if(StringUtils.notEmpty(data.webUrl())){
                webView.loadUrl(data.webUrl());
            }else{
                LogUtils.e(TAG, "无有效网页地址");
            }
        }
    }

    @Override
    public void autoDispose() {
        if(webView != null){
            webView.destroy();
            webView = null;
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
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
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
}
