package com.sony.imaging.app.timelapse.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.AntiHandBlurIcon;
import com.sony.imaging.app.timelapse.shooting.base.TimeLapseDisplayModeObserver;

/* loaded from: classes.dex */
public class TimelapseAntiHandBlurIcon extends AntiHandBlurIcon {
    public TimelapseAntiHandBlurIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public boolean isVisible() {
        if (TimeLapseDisplayModeObserver.getInstance().getActiveDispMode(0) == 11) {
            return false;
        }
        return super.isVisible();
    }
}
