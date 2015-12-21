package com.hitouba.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.hitouba.R;
import com.hitouba.bean.AccountBean;
import com.hitouba.dao.MyAccountDao;
import com.hitouba.misc.Urls;
import com.yuexiaohome.framework.exception.AppException;
import com.yuexiaohome.framework.lib.AsyncTaskEx;
import com.yuexiaohome.framework.util.L;
import com.yuexiaohome.framework.util.Toaster;
import com.yuexiaohome.framework.widget.AspectImageView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {
    static String[] adPics = {Urls.AD_PIC_1, Urls.AD_PIC_2, Urls.AD_PIC_3, Urls.AD_PIC_4};
    static String[] adWebs = {Urls.AD_WEB_1, Urls.AD_WEB_2, Urls.AD_WEB_3, Urls.AD_WEB_4};

    @InjectView(R.id.ad_is)
    ImageSwitcher adImageSwitcher;

    private static int position = 0;
    private static int lastPos = 0;

    private Timer timer;

    @Override
    protected void onPause() {
        timer.cancel();
        timer = null;
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        adImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                AspectImageView imageView = new AspectImageView(MainActivity.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setRatio(3.2f, true);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return imageView;
            }
        });

        adImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        adImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        final ImageView imageView = (ImageView) adImageSwitcher.getNextView();
        imageView.setImageResource(R.drawable.adpic);
        adImageSwitcher.showNext();

    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                final DisplayImageOptions options =
                        new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).build();

                if (msg.what == 1) {
                    if (position == lastPos) {
                        final ImageView iv = (ImageView) adImageSwitcher.getNextView();
                        final int currPos = position % adPics.length;
                        L.d("position:" + currPos);

                        final String url = adPics[currPos];
                        ImageLoader.getInstance().loadImage(url, options, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                DiskCache diskCache = ImageLoader.getInstance().getDiskCache();
                                File file = diskCache.get(url);
                                L.d("pic url:" + url);
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                iv.setImageBitmap(bitmap);
                                iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                                        L.d("position inside:" + currPos);
                                        intent.putExtra("url", adWebs[currPos]);
                                        L.d("page url:" + adWebs[currPos]);
                                        //startActivity(intent);
                                    }
                                });
                                position++;
                                lastPos++;
                                adImageSwitcher.showNext();
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                            }
                        });
                    }
                }
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 0, 4000);
    }

    @OnClick({R.id.borrow_tv, R.id.capital_record_tv, R.id.hicunbao_tv, R.id.website_notice_view, R.id.signin_tv, R.id.account_tv})
    public void launchActivity(TextView tv) {
        Intent intent;
        switch (tv.getId()) {
            case R.id.borrow_tv:
                intent = new Intent(this, ProjectListActivity.class);
                //intent.putExtra("url",Urls.PROJECT_LIST_WEB);
                startActivity(intent);
                break;
            case R.id.website_notice_view:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Urls.WEBSITE_NOTICE_WEB);
                startActivity(intent);
                break;
            case R.id.capital_record_tv:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Urls.CAPITAL_RECORD_WEB);
                startActivity(intent);
                break;
            case R.id.signin_tv:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("enlargePic", false);
                intent.putExtra("url", Urls.SIGNIN_WEB);
                startActivity(intent);
                break;
            case R.id.account_tv:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Urls.ACCOUNT_WEB);
                intent.putExtra("hideFooter",true);
                startActivity(intent);
                break;
            case R.id.hicunbao_tv:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Urls.HICUNBAO_WEB);
                intent.putExtra("hideFooter",false);
                startActivity(intent);
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // It is also possible add items here. Use a generated id from
        // resources (ids.xml) to ensure that all menu ids are distinct.
        MenuItem locationItem = menu.add(0, R.id.action_about, 0, "说明");
        //locationItem.setIcon(R.drawable.ic_action_location);

        // Need to use MenuItemCompat methods to call any action item related methods
        MenuItemCompat.setShowAsAction(locationItem, MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, WebViewActivity.class);
        switch (item.getItemId()) {
            case R.id.action_about:
                intent.putExtra("data", getString(R.string.remark));
                break;
            case R.id.action_recharge:
                intent.putExtra("url", Urls.RECHARGE_WEB);
                break;
            case R.id.action_take_cash:
                intent.putExtra("url", Urls.TAKE_CASH_WEB);
                break;
            case R.id.action_auto_bid:
                intent.putExtra("url", Urls.AUTO_BID_WEB);
                break;
//            case R.id.action_redpocket:
//                intent.putExtra("url", Urls.REDPOCKET_WEB);
//                break;
//            case R.id.action_getpocket:
//                intent.putExtra("url", Urls.GET_REDPOCKET_WEB);
//                break;
            case R.id.action_aboutrkd:
                intent.putExtra("url", Urls.ABOUT_US_WEB);
                break;


        }
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    class MyAccountTask extends AsyncTaskEx<Void, Void, AccountBean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //MainActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected AccountBean doInBackground(Void... params) {
            MyAccountDao dao = new MyAccountDao();
            try {
                return dao.doAction();
            } catch (AppException e) {
                setFailure(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(AccountBean accountBean) {
            Toaster.showLong(MainActivity.this, accountBean.getUsername());

        }
    }
}
