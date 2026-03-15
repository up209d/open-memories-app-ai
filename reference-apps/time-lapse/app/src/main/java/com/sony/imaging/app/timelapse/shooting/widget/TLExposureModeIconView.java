package com.sony.imaging.app.timelapse.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;

/* loaded from: classes.dex */
public class TLExposureModeIconView extends ExposureModeIconView {
    public TLExposureModeIconView(Context context) {
        super(context);
    }

    public TLExposureModeIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVisibility(4);
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage, android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        if (TLCommonUtil.getInstance().getCurrentState() != 7) {
            visibility = 8;
        }
        super.setVisibility(visibility);
    }
}
