package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;

/* loaded from: classes.dex */
public class PortraitBeautyAppIcon extends AppIconView {
    private final String TAG;

    public PortraitBeautyAppIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (PortraitBeautyUtil.bIsAdjustModeGuide) {
            visibility = 8;
        }
        super.setVisibility(visibility);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
