package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.util.RelativeLayoutGroup;

/* loaded from: classes.dex */
public class AppNameConstantView extends RelativeLayoutGroup {
    protected static String mText = null;
    protected TextView mTextView;

    public static void setText(String text) {
        mText = text;
    }

    public AppNameConstantView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTextView = null;
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(-2, -2);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.AppNameView);
        int styleId = attr.getResourceId(0, -1);
        attr.recycle();
        if (-1 == styleId) {
            int device = DisplayModeObserver.getInstance().getActiveDevice();
            if (device == 0 || 2 == device) {
                styleId = R.attr.RESID_FONTSIZE_STD_L_EDGE_ON;
            } else if (1 == device) {
                styleId = R.attr.RESID_FONTSIZE_STD_S_EDGE_ON;
            }
        }
        this.mTextView = new TextView(context, attrs, styleId);
        this.mTextView.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL));
        addView(this.mTextView, param);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mTextView.setText(mText);
    }
}
