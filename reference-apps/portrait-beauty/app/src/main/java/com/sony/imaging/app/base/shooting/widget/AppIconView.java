package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;

/* loaded from: classes.dex */
public class AppIconView extends ActiveImage {
    private Runnable disappear;
    private Handler handler;
    private boolean mWillDisappear;
    private static int mIcon = 0;
    private static int mEVFIcon = 0;
    private static int TIMEOUT = PortraitBeautyConstants.FOUR_SECOND_MILLIS;

    public static void setIcon(int resid) {
        setIcon(resid, resid);
    }

    public static void setIcon(int panelId, int evfId) {
        mIcon = panelId;
        mEVFIcon = evfId;
    }

    public void setAutoDisappear(boolean b) {
        this.mWillDisappear = b;
        setVisibility(0);
        this.handler.removeCallbacks(this.disappear);
        if (b) {
            this.handler.postDelayed(this.disappear, TIMEOUT);
        }
    }

    public AppIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.handler = new Handler();
        this.disappear = new Runnable() { // from class: com.sony.imaging.app.base.shooting.widget.AppIconView.1
            @Override // java.lang.Runnable
            public void run() {
                AppIconView.this.setVisibility(4);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (device == 1) {
            setBackgroundResource(mEVFIcon);
        } else {
            setBackgroundResource(mIcon);
        }
        if (this.mWillDisappear) {
            setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mWillDisappear) {
            this.handler.removeCallbacks(this.disappear);
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.mWillDisappear) {
            this.handler.removeCallbacks(this.disappear);
            this.handler.postDelayed(this.disappear, TIMEOUT);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        setVisibility(0);
        this.handler.removeCallbacks(this.disappear);
        if (this.mWillDisappear) {
            this.handler.postDelayed(this.disappear, TIMEOUT);
        }
    }
}
