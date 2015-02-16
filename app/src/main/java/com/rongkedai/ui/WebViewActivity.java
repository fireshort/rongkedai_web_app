package com.rongkedai.ui;

import android.app.ActionBar;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.*;
import android.webkit.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.rongkedai.R;
import com.yuexiaohome.framework.util.L;
import com.yuexiaohome.framework.util.Toaster;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class WebViewActivity extends ActionBarActivity {
    @InjectView(R.id.webview)
    WebView mWebView;

    @InjectView(R.id.ptr_frame)
    PtrClassicFrameLayout ptrFrame;

    private boolean enlargePic = true;

    private String data = null;

    private final WebViewClient mWebViewClient = new WebViewClient() {
        // 处理页面导航
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            L.d("url:" + url);
            Intent intent;
            if (url.startsWith("tel:")) {
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
            }
            else if (url.startsWith("mailto:")) {
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(url));
                intent.putExtra(Intent.EXTRA_SUBJECT, "意见反馈");
                //intent.putExtra(Intent.EXTRA_TEXT, "这是内容");
                startActivity(intent);
            }
            else mWebView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            WebViewActivity.this.setProgressBarIndeterminateVisibility(false);
            super.onPageFinished(view, url);
            String javascript = "javascript:document.getElementsByTagName('footer')[0].style.display = 'none';javascript:document.getElementsByClassName('header')[0].style.display = 'none';javascript:document.getElementsByClassName('banner')[0].style.display = 'none';void(0)";
            if (enlargePic)
                javascript = "javascript:var elements=document.getElementsByTagName('img');for(var i=0;i<elements.length;i++)elements[i].onclick=function(){rkd.showImg(this.src);};"
                        + javascript;
            javascript = "javascript:rkd.settingPageTitle(document.getElementsByClassName('topic')[0].innerText);" + javascript;
            view.loadUrl(javascript);
            ptrFrame.refreshComplete();
            //getActionBar().setTitle(Setting.pageTitle);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            WebViewActivity.this.setProgressBarIndeterminateVisibility(true);
            super.onPageStarted(view, url, favicon);
        }
    };

    // 浏览网页历史记录
    // goBack()和goForward()
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            if (data == null)
                mWebView.goBack();
            else onBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private WebChromeClient mChromeClient = new WebChromeClient() {
        private View myView = null;

        private CustomViewCallback myCallback = null;

        // 配置权限 （在WebChromeClinet中实现）
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        // Android 使WebView支持HTML5 Video（全屏）播放的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }

            ViewGroup parent = (ViewGroup) mWebView.getParent();
            parent.removeView(mWebView);
            parent.addView(view);
            myView = view;
            myCallback = callback;
            mChromeClient = this;
        }

        @Override
        public void onHideCustomView() {
            if (myView != null) {
                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                }

                ViewGroup parent = (ViewGroup) myView.getParent();
                parent.removeView(myView);
                parent.addView(mWebView);
                myView = null;
            }
        }

        // 当WebView进度改变时更新窗口进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
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
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            L.d(String.format("WebView JsAlert message = %s", url, message));
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

    private void initSettings() {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // // 全屏
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.webview);
        ButterKnife.inject(this);

        ptrFrame.setLastUpdateTimeRelateObject(this);
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return mWebView.getScrollY() == 0;
                //return true;
                //return PtrDefaultHandler.checkContentCanBePulledDown(frame,content,header);
            }
        });

        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        // 启用localStorage 和 SessionStorage
        settings.setDomStorageEnabled(true);
        // 开启应用程序缓存
        settings.setAppCacheEnabled(true);
        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        settings.setAppCachePath(appCacheDir);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheMaxSize(1024 * 1024 * 10);// 设置缓冲大小，我设的是10M
        settings.setAllowFileAccess(false);
        // 启用Webdatabase数据库
        settings.setDatabaseEnabled(true);
        String databaseDir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(databaseDir);// 设置数据库路径
        settings.setGeolocationEnabled(true); // 启用地理定位
        // 设置定位的数据库路径
        settings.setGeolocationDatabasePath(databaseDir);
        // 开启插件（对flash的支持）
        // settings.setPluginsEnabled(true);
        //settings.setRenderPriority(RenderPriority.HIGH);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 缩放支持
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(false);
        Boolean useWideViewPort = getIntent().getBooleanExtra("useWideViewPort", true);
        settings.setUseWideViewPort(useWideViewPort);
        settings.setLoadWithOverviewMode(true);

        mWebView.setBackgroundColor(0x000000);
        mWebView.setWebChromeClient(mChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.addJavascriptInterface(new UAJscriptHandler(this), "rkd");

    }

    //@OnClick(R.id.btnHome)
    public void goHome(View view) {
        //mWebView.loadUrl("https://www.rongkedai.com/wapborrow/nav.jhtml");
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initSettings();
        enlargePic = getIntent().getBooleanExtra("enlargePic", true);
        String url = getIntent().getStringExtra("url");
        //String url="https://www.rongkedai.com/wapborrow/nav.jhtml";// intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url))
            mWebView.loadUrl(url);
        else {
            data = getIntent().getStringExtra("data");
            L.d("data:" + data);
            mWebView.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "utf-8", null);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu);

//        MenuItem locationItem = menu.add(0, R.id.action_copyurl, 0, "复制链接进剪贴板");
        //locationItem.setIcon(R.drawable.ic_action_location);

        // Need to use MenuItemCompat methods to call any action item related methods
//        MenuItemCompat.setShowAsAction(locationItem, MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //add top-left icon click event deal
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_copyurl:
                //获取剪贴板管理服务
                ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
//将文本数据复制到剪贴板
                cm.setText(mWebView.getUrl());
                Toaster.showLong(this, "成功复制。");
                break;
            case R.id.action_open_in_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebView.getUrl()));
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        if (data != null)
            mWebView.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "utf-8", null);
        else mWebView.reload();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PageTitleEvent event) {
        getSupportActionBar().setTitle(event.getPageTitle());
    }

}