package com.sony.imaging.app.manuallenscompensation.shooting.layout;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.widget.ShootingGraphView;

/* loaded from: classes.dex */
public class OCShootingSoftKeyArea extends ShootingGraphView {
    public OCShootingSoftKeyArea(Context context) {
        super(context, null);
    }

    public OCShootingSoftKeyArea(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void refresh() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (device == 1) {
            setVisibility(4);
        } else {
            setVisibility(0);
        }
    }
}
