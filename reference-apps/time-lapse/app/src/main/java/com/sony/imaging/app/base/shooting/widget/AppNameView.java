package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class AppNameView extends AppNameConstantView {
    private static final int DISPLAYING_TIME = 4000;
    private static boolean mShow = false;
    private Runnable disappear;
    private Handler handler;

    public static void show(boolean show) {
        mShow = show;
    }

    public AppNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.handler = new Handler();
        this.disappear = new Runnable() { // from class: com.sony.imaging.app.base.shooting.widget.AppNameView.1
            @Override // java.lang.Runnable
            public void run() {
                AppNameView.this.setVisibility(4);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AppNameConstantView, com.sony.imaging.app.util.AbstractRelativeLayoutGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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
