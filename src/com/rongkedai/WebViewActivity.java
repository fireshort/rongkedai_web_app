package com.rongkedai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class WebViewActivity extends Activity
{
    private WebView mWebView;

    private ImageButton btnBack,btnReload;

    private static final String LOG_TAG="WebViewActivity";

    private final WebViewClient mWebViewClient=new WebViewClient()
    {
        // 处理页面导航
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url)
        {
            Log.d(LOG_TAG,"Loading url="+url);
            mWebView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view,String url)
        {
            WebViewActivity.this.setProgressBarIndeterminateVisibility(false);
            super.onPageFinished(view,url);
            String javascript="javascript:document.getElementById('box').style.display = 'none';javascript:document.getElementsByClassName('numberdiv')[0].style.display = 'none';javascript:document.getElementsByClassName('footertopmy')[0].style.display = 'none';javascript:document.getElementsByClassName('topmain')[0].style.display = 'none';document.getElementsByClassName('abmenutt')[0].style.display = 'none';";
            view.loadUrl(javascript);
        }

        @Override
        public void onPageStarted(WebView view,String url,Bitmap favicon)
        {
            WebViewActivity.this.setProgressBarIndeterminateVisibility(true);
            super.onPageStarted(view,url,favicon);
        }
    };

    // 浏览网页历史记录
    // goBack()和goForward()
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if((keyCode==KeyEvent.KEYCODE_BACK)&&mWebView.canGoBack())
        {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode,event);
    }

    private WebChromeClient mChromeClient=new WebChromeClient()
    {
        private View myView=null;

        private CustomViewCallback myCallback=null;

        // 配置权限 （在WebChromeClinet中实现）
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,GeolocationPermissions.Callback callback)
        {
            callback.invoke(origin,true,false);
            super.onGeolocationPermissionsShowPrompt(origin,callback);
        }

        // 扩充数据库的容量（在WebChromeClinet中实现）
        @Override
        public void onExceededDatabaseQuota(String url,String databaseIdentifier,long currentQuota,long estimatedSize,
                long totalUsedQuota,WebStorage.QuotaUpdater quotaUpdater)
        {

            quotaUpdater.updateQuota(estimatedSize*2);
        }

        // 扩充缓存的容量
        @Override
        public void onReachedMaxAppCacheSize(long spaceNeeded,long totalUsedQuota,WebStorage.QuotaUpdater quotaUpdater)
        {

            quotaUpdater.updateQuota(spaceNeeded*2);
        }

        // Android 使WebView支持HTML5 Video（全屏）播放的方法
        @Override
        public void onShowCustomView(View view,CustomViewCallback callback)
        {
            if(myCallback!=null)
            {
                myCallback.onCustomViewHidden();
                myCallback=null;
                return;
            }

            ViewGroup parent=(ViewGroup)mWebView.getParent();
            parent.removeView(mWebView);
            parent.addView(view);
            myView=view;
            myCallback=callback;
            mChromeClient=this;
        }

        @Override
        public void onHideCustomView()
        {
            if(myView!=null)
            {
                if(myCallback!=null)
                {
                    myCallback.onCustomViewHidden();
                    myCallback=null;
                }

                ViewGroup parent=(ViewGroup)myView.getParent();
                parent.removeView(myView);
                parent.addView(mWebView);
                myView=null;
            }
        }

        // 当WebView进度改变时更新窗口进度
        @Override
        public void onProgressChanged(WebView view,int newProgress)
        {
            // Activity的进度范围在0到10000之间,所以这里要乘以100
            // WebViewActivity.this.setProgress(newProgress*100);
        }

        /*
         * public boolean onJsAlert(WebView wv,String url,String
         * message,JsResult result) {
         * Toast.makeText(wv.getContext(),message,Toast.LENGTH_SHORT).show();
         * result.confirm(); return true; }
         */
        @Override
        public boolean onJsAlert(WebView view,String url,String message,JsResult result)
        {
            Log.d(LOG_TAG,String.format("WebView JsAlert message = %s",url,message));
            return false;
        }

        // public boolean onConsoleMessage(ConsoleMessage consoleMsg)
        // {
        // StringBuilder msg=new
        // StringBuilder(consoleMsg.messageLevel().name()).append('\t')
        // .append(consoleMsg.message()).append('\t').append(consoleMsg.sourceId()).append(" (")
        // .append(consoleMsg.lineNumber()).append(")\n");
        // if(consoleMsg.messageLevel()==ConsoleMessage.MessageLevel.ERROR)
        // {
        // Log.e(LOG_TAG,msg.toString());
        // }else
        // {
        // Log.d(LOG_TAG,msg.toString());
        // }
        // return true;
        // }
    };

    private void initSettings()
    {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // // 全屏
        setContentView(R.layout.webview);
        mWebView=(WebView)findViewById(R.id.webview);

        WebSettings webSettings=mWebView.getSettings();
        // 开启Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        // 启用localStorage 和 SessionStorage
        webSettings.setDomStorageEnabled(true);
        // 开启应用程序缓存
        webSettings.setAppCacheEnabled(true);
        String appCacheDir=this.getApplicationContext().getDir("cache",Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheMaxSize(1024*1024*10);// 设置缓冲大小，我设的是10M
        webSettings.setAllowFileAccess(false);
        // 启用Webdatabase数据库
        webSettings.setDatabaseEnabled(true);
        String databaseDir=this.getApplicationContext().getDir("database",Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(databaseDir);// 设置数据库路径
        webSettings.setGeolocationEnabled(true); // 启用地理定位
        // 设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(databaseDir);
        // 开启插件（对flash的支持）
        // webSettings.setPluginsEnabled(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 缩放支持
        webSettings.setBuiltInZoomControls(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // webSettings.setDefaultZoom(ZoomDensity.FAR);
        // webSettings.setSupportZoom(false);

        mWebView.setWebChromeClient(mChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        // mWebView.addJavascriptInterface(new UAJscriptHandler(this),"bms");

        // btnBack=(ImageButton)findViewById(R.id.btnBack);
        // btnBack.setOnClickListener(new View.OnClickListener()
        // {
        // public void onClick(View v)
        // {
        // finish();
        // }
        // });
        // btnReload=(ImageButton)findViewById(R.id.btnReload);
        // btnReload.setOnClickListener(new View.OnClickListener()
        // {
        // public void onClick(View v)
        // {
        // mWebView.reload();
        // }
        // });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.initSettings();
        Intent intent=getIntent();
        String url="https://www.rongkedai.com/wapborrow/nav.jhtml";// intent.getStringExtra("url");

        Log.d("bms",url);

        String htmlText="<html>"+"<head>"+"<style type=\"text/css\">"+".abmenutt{display:none;}"+"</style>"+"</head>";
        mWebView.loadData(htmlText,"text/html","utf-8");
        // mWebView.loadUrl("javascript:document.getElementsByClassName('abmenutt')[0].style.display = 'none';");
        mWebView.loadUrl(url);
    }
}