package com.sony.imaging.app.manuallenscompensation.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.util.RelativeLayoutGroup;

/* loaded from: classes.dex */
public class OCAppNameFocalValue extends RelativeLayoutGroup {
    private static final int DISPLAYING_TIME = 4000;
    private static boolean mShow = false;
    private static String mText = null;
    private Runnable disappear;
    private Handler handler;
    private TextView mTextView;

    public static void show(boolean show) {
        mShow = show;
    }

    public static void setText(String text) {
        mText = text;
    }

    public OCAppNameFocalValue(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.handler = new Handler();
        this.mTextView = null;
        this.disappear = new Runnable() { // from class: com.sony.imaging.app.manuallenscompensation.widget.OCAppNameFocalValue.1
            @Override // java.lang.Runnable
            public void run() {
                OCAppNameFocalValue.this.setVisibility(4);
            }
        };
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(-2, -2);
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (device == 0 || 2 == device) {
            this.mTextView = new TextView(context, attrs, R.attr.RESID_FONTSIZE_M_EDGE);
            this.mTextView.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
        } else if (1 == device) {
            this.mTextView = new TextView(context, attrs, R.attr.RESID_FONTSIZE_S_EDGE);
            this.mTextView.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN_EVF));
        }
        addView(this.mTextView, param);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mTextView.setText(mText);
        if (mShow) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.handler.removeCallbacks(this.disappear);
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mShow) {
            mShow = false;
            this.handler.postDelayed(this.disappear, 4000L);
        }
    }
}
