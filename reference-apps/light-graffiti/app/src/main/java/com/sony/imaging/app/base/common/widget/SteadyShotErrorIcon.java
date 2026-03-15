package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.InhWatch;
import com.sony.imaging.app.util.AvailableInfo;

/* loaded from: classes.dex */
public class SteadyShotErrorIcon extends ImageView implements InhWatch.InhWatchInterface {
    private static final String INH_ID_STEADY_SHOT_ERROR = "INH_FACTOR_STEADYSHOT_ERR";
    private static final String TAG = "SteadyShotErrorIcon";
    private boolean isRepeat;
    private int mCallbackId;
    private InhWatch mInhWatch;

    public SteadyShotErrorIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isRepeat = true;
        this.mCallbackId = -1;
        setVisibility(4);
        this.mInhWatch = InhWatch.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.InhWatch.InhWatchInterface
    public void onCallback() {
        if (this.isRepeat) {
            refreshIcon();
        }
    }

    private void refreshIcon() {
        Drawable d;
        boolean steadyShotErrorInh = AvailableInfo.isFactor(INH_ID_STEADY_SHOT_ERROR);
        if (steadyShotErrorInh) {
            d = getDrawableFromPool();
        } else {
            setVisibility(4);
            d = null;
        }
        if (d != null) {
            setVisibility(0);
            setImageDrawable(d);
        }
    }

    private Drawable getDrawableFromPool() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (device == 0 || device == 2) {
            Drawable d = getResources().getDrawable(17304287);
            return d;
        }
        if (device != 1) {
            return null;
        }
        Drawable d2 = getResources().getDrawable(17304537);
        return d2;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        this.isRepeat = true;
        this.mCallbackId = this.mInhWatch.addCallback(this, 1, TAG);
        this.mInhWatch.startCallback(this.mCallbackId);
        super.onAttachedToWindow();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        this.isRepeat = false;
        setVisibility(4);
        this.mInhWatch.stopCallback(this.mCallbackId);
        this.mInhWatch.removeCallback(this.mCallbackId);
        super.onDetachedFromWindow();
    }
}
