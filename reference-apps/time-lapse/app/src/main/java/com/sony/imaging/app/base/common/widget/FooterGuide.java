package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.util.RelativeLayoutGroup;

/* loaded from: classes.dex */
public class FooterGuide extends RelativeLayoutGroup {
    IFooterGuideData mData;
    TextView mTextView;

    public FooterGuide(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FooterGuide);
        int style = a.getResourceId(0, R.attr.footer_guide);
        int l = a.getDimensionPixelOffset(1, 0);
        int t = a.getDimensionPixelOffset(2, 0);
        int w = a.getDimensionPixelSize(3, -2);
        int h = a.getDimensionPixelSize(4, -2);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(w, h);
        param.leftMargin = l;
        param.topMargin = t;
        this.mTextView = new TextView(context, null, style);
        addView(this.mTextView, param);
        IFooterGuideData data = null;
        int common = a.getResourceId(5, 0);
        if (common != 0) {
            data = new FooterGuideDataResId(context, common);
        } else {
            int p1 = a.getResourceId(6, 0);
            int emt = a.getResourceId(7, 0);
            if (p1 != 0 || emt != 0) {
                data = new FooterGuideDataResId(context, p1, emt);
            }
        }
        setData(data);
        a.recycle();
    }

    public void setData(IFooterGuideData data) {
        this.mData = data;
        CharSequence text = null;
        if (data != null) {
            text = data.getText();
        }
        this.mTextView.setText(text);
        setVisibility(text != null ? 0 : 4);
    }
}
