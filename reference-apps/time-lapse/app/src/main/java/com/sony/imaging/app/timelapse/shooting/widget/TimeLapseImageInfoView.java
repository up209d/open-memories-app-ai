package com.sony.imaging.app.timelapse.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.MediaInformationView;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.layout.TimeLapseStableLayout;

/* loaded from: classes.dex */
public class TimeLapseImageInfoView extends MediaInformationView {
    public TimeLapseImageInfoView(Context context) {
        super(context);
    }

    public TimeLapseImageInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView
    public void showRemaining(int remaining) {
        if (!TLCommonUtil.getInstance().isTestShot() && !TimeLapseStableLayout.isCapturing) {
            TLCommonUtil.getInstance().calculateRemainingMemory();
            super.showRemaining(TLCommonUtil.getInstance().getAvailableRemainingShot());
        }
    }
}
