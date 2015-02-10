package com.yuexiaohome.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.rongkedai.R;


public class AspectImageView extends ImageView {

    private float mRatio;

    public AspectImageView(Context context) {
        this(context, null);
    }

    public AspectImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.AspectImageView, defStyle, 0);
        String ratio = a.getString(R.styleable.AspectImageView_img_ratio);
        if (ratio != null) {
            try {
                mRatio = Float.parseFloat(ratio);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (mRatio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int lockedWidth = widthSize - hPadding;
        int lockedHeight = heightSize - vPadding;

        if (heightSize > 0 && (lockedWidth > heightSize * mRatio)) {
            lockedWidth = (int) (lockedHeight * mRatio + .5);
        } else {
            lockedHeight = (int) (lockedWidth / mRatio + .5);
        }

        lockedWidth += hPadding;
        lockedHeight += vPadding;

        super.onMeasure(MeasureSpec.makeMeasureSpec(lockedWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(lockedHeight, MeasureSpec.EXACTLY));
    }


    public void setRatio(float ratio, boolean requestLayout) {
        mRatio = ratio;
        if (requestLayout) {
            requestLayout();
        }
    }
}
