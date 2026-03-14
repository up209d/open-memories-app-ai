package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.portraitbeauty.common.AppLog;

/* loaded from: classes.dex */
public class PortraitBeautyModeIconView extends ExposureModeIconView {
    private final String TAG;

    public PortraitBeautyModeIconView(Context context) {
        super(context);
        this.TAG = AppLog.getClassName();
    }

    public PortraitBeautyModeIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = AppLog.getClassName();
        AppLog.enter(this.TAG, AppLog.getMethodName());
        setVisibility(4);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(8);
    }
}
