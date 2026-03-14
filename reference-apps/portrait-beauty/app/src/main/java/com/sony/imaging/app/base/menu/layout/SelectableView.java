package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class SelectableView extends RelativeLayout {
    private static final String VIEW_TAG_SELECTED = "selected";
    private static final String VIEW_TAG_UNSELECTED = "unselected";
    protected ImageView mImageView;
    private RelativeLayout.LayoutParams mLayoutParams;
    private Drawable mSelectedDrawable;
    private int mSelectedTextColor;
    private String mText;
    private TextView mTextView;
    private TypedArray mTypedArray;
    private Drawable mUnSelectedDrawable;
    private int mUnSelectedTextColor;

    public SelectableView(Context context) {
        super(context);
        this.mSelectedTextColor = -1;
        this.mUnSelectedTextColor = -1;
        this.mText = "samp";
        this.mLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
        this.mLayoutParams.addRule(13);
        this.mImageView = new ImageView(context);
        this.mTextView = new TextView(context);
        this.mTextView.setText(this.mText);
        addView(this.mImageView, this.mLayoutParams);
        addView(this.mTextView, this.mLayoutParams);
    }

    public SelectableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSelectedTextColor = -1;
        this.mUnSelectedTextColor = -1;
        this.mText = "samp";
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectableItemView);
        this.mLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
        this.mLayoutParams.addRule(13);
        this.mImageView = new ImageView(context, attrs);
        this.mTextView = new TextView(context, attrs);
        addView(this.mImageView, this.mLayoutParams);
        addView(this.mTextView, this.mLayoutParams);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSelected(false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        this.mSelectedDrawable = null;
        this.mUnSelectedDrawable = null;
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mUnSelectedDrawable = this.mTypedArray.getDrawable(1);
        this.mSelectedDrawable = this.mTypedArray.getDrawable(0);
        this.mUnSelectedTextColor = this.mTypedArray.getColor(3, -1);
        this.mSelectedTextColor = this.mTypedArray.getColor(2, -1);
    }

    public void setSelectedDrawable(Drawable d) {
        this.mSelectedDrawable = d;
    }

    public void setUnSelectedDrawable(Drawable d) {
        this.mUnSelectedDrawable = d;
    }

    public void setSelectedTextColor(int c) {
        this.mSelectedTextColor = c;
    }

    public void setUnSelectedTextColor(int c) {
        this.mUnSelectedTextColor = c;
    }

    @Override // android.view.View
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            if (this.mSelectedDrawable != null) {
                this.mImageView.setImageDrawable(this.mSelectedDrawable);
            }
            if (this.mSelectedTextColor != -1) {
                this.mTextView.setTextColor(this.mSelectedTextColor);
                return;
            } else {
                removeView(this.mTextView);
                return;
            }
        }
        if (this.mUnSelectedDrawable != null) {
            this.mImageView.setImageDrawable(this.mUnSelectedDrawable);
        }
        if (this.mUnSelectedTextColor != -1) {
            this.mTextView.setTextColor(this.mUnSelectedTextColor);
        } else {
            removeView(this.mTextView);
        }
    }
}
