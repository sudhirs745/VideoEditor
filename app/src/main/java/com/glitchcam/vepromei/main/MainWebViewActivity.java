package com.glitchcam.vepromei.main;

import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.utils.Util;

/**
 * 首页广告详情页面
 * Home ad details page
 */
public class MainWebViewActivity extends BaseActivity {

    private WebView mWebView;
    private Bundle mBundle;
    private Toolbar mToolbar;
    private TextView mContentTitle;

    @Override
    protected int initRootView() {
        return R.layout.activity_main_web_view;
    }

    @Override
    protected void initViews() {
        // toolbar
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar( ).setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.main_webview_back);
        // webView
        mWebView = findViewById(R.id.main_activity_web_view);
        WebSettings settings = mWebView.getSettings( );
        settings.setDomStorageEnabled(true);
        /*
         * 设置允许加载混合内容
         * Setting to allow mixed content to load
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /*
         * 解决一些图片加载问题
         * Solve some image loading problems
         * */
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        /*
         * 断网情况下加载本地缓存
         * Load local cache in case of network disconnection
         * */
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        /*
         * 让WebView支持DOM storage API
         * Make WebView support DOM storage API
         * */
        settings.setDomStorageEnabled(true);
        settings.setSaveFormData(false);
        //WebViewClient
        mWebView.setWebViewClient(new WebViewClient( ) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("mWebView", "url: " + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext( ));
                builder.setMessage(R.string.ssl_error_prompt);
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed( );
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener( ) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel( );
                    }
                });
                AlertDialog dialog = builder.create( );
                dialog.show( );
            }
        });
        // title
        mContentTitle = findViewById(R.id.webview_content_title);
        // WebChromeClient
        mWebView.setWebChromeClient(new WebChromeClient( ) {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mContentTitle != null) {
                    mContentTitle.setText(title);
                }
            }
        });
    }

    @Override
    protected void initTitle() {
    }

    @Override
    protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause( );
        }
        super.onPause( );
    }

    @Override
    protected void onResume() {
        if (mWebView != null) {
            mWebView.onResume( );
        }
        super.onResume( );
    }

    @Override
    protected void initData() {
        String url = "";
        mBundle = getIntent( ).getExtras( );
        if (mBundle != null) {
            url = mBundle.getString("URL");
        }
        mWebView.loadUrl(url);
    }

    @Override
    protected void initListener() {
        // toolbar
        mToolbar.setNavigationOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                finish( );
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater( ).inflate(R.menu.webview_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.webView_refresh) {
            if (Util.isFastClick()) {
                return true;
            }
            if ((mWebView != null)) {
                mWebView.reload();
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.setTag(null);
            mWebView.clearHistory( );
            ((ViewGroup) mWebView.getParent( )).removeView(mWebView);
            mWebView.destroy( );
            mWebView = null;
        }
        super.onDestroy( );
    }
}
