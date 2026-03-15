package com.sony.imaging.app.startrails.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.startrails.common.STCaptureDisplayModeObserver;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeRecomandedMenuSetting;

/* loaded from: classes.dex */
public class STExposureModeIconView extends ExposureModeIconView {
    private static final String TAG = "STExposureModeIconView";
    int displayMode;

    public STExposureModeIconView(Context context) {
        super(context);
        this.displayMode = 11;
    }

    public STExposureModeIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.displayMode = 11;
        setVisibility(4);
        this.displayMode = STCaptureDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (STUtility.getInstance().getCurrentTrail() == 2 && this.displayMode != 12) {
            setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        AppLog.enter(TAG, "displayMode = " + this.displayMode);
        this.displayMode = STCaptureDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (STUtility.getInstance().getCurrentTrail() != 2 || this.displayMode == 12) {
            AppLog.info(TAG, "displayMode = " + this.displayMode);
            visibility = 8;
        } else {
            AppLog.info(TAG, "displayMode = " + this.displayMode);
            ThemeRecomandedMenuSetting.getInstance().updateExposureModeForCustomTheme();
        }
        super.setVisibility(visibility);
        AppLog.exit(TAG, "displayMode = " + this.displayMode);
    }
}
