package com.rongkedai.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rongkedai.R;
import com.yuexiaohome.framework.util.L;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;


public class LargeImageActivity extends Activity {

    private PhotoViewAttacher mAttacher;

    @InjectView(R.id.image_view)
    ImageView mImageView;

    @InjectView(R.id.image_loading_pb)
    ProgressBar imageLoadingPB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);
        ButterKnife.inject(this);

        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnDoubleTapListener(mTapListener);
        //mAttacher.setOnMatrixChangeListener(LargeImageActivity.this);

        final String url = getIntent().getStringExtra("imgUrl");
        L.d("url:" + url);

        if (url == null)
            return;


        DisplayImageOptions options =
                new DisplayImageOptions.Builder()
                        //.showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片
                        //.showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
                        //.showImageOnFail(R.drawable.ic_launcher)
                        .cacheOnDisk(true).cacheInMemory(true).build();
        ImageLoader.getInstance().loadImage(url, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                L.d("onLoadingComplete...1624");
                DiskCache diskCache = ImageLoader.getInstance().getDiskCache();
                File file = diskCache.get(url);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mImageView.setLayoutParams(lp);
                mImageView.setImageBitmap(bitmap);
                imageLoadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
        //ImageLoader.getInstance().displayImage(url, mImageView, options);

        mAttacher.update();
    }

    private Bitmap mergeBitmaps(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmp =
                Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bmp1, 0, 0, null);
        Paint alphaP = new Paint(Paint.ANTI_ALIAS_FLAG);
        alphaP.setAlpha(0x88);
        canvas.drawBitmap(bmp2, 0, 0, alphaP);
        return bmp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAttacher.cleanup();
    }

    GestureDetector.OnDoubleTapListener mTapListener = new GestureDetector.OnDoubleTapListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            finish();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float minimumScale = mAttacher.getMinimumScale();

            // animate runnable sometimes does not resume exactly to minScale,so we add some
            // adjustment
            if (mAttacher.getScale() > minimumScale + .05) {
                mAttacher.setScale(minimumScale, true);
            } else {
                try {
                    mAttacher.setScale(minimumScale * 3, e.getX(), e.getY(), true);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    L.e("Invalid motion event", ex);
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    };


    SimpleImageLoadingListener mLoadingListener = new SimpleImageLoadingListener() {

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

        }
    };

    /*
    @Override
    public void onMatrixChanged(RectF rectF) {
        if (rectF.isEmpty())
            return;
        // already resolved image matrix,don't need to listen anymore
        mAttacher.setOnMatrixChangeListener(null);

        final float scaleX = mImageView.getWidth() / rectF.width();
        final float scaleY = mImageView.getHeight() / rectF.height();

        float minScale = 1;

        if (scaleX > scaleY)
            minScale = scaleX;

        mAttacher.setMaximumScale(minScale * 9);
        mAttacher.setMediumScale(minScale * 6);
        mAttacher.setMinimumScale(minScale);

        if (scaleX > scaleY)
            mAttacher.setScale(scaleX, mImageView.getWidth() / 2, 0, false);

    }*/
}
