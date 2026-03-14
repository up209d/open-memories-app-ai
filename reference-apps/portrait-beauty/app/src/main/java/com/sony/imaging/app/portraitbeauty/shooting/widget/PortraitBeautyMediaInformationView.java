package com.sony.imaging.app.portraitbeauty.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.MediaInformationView;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;

/* loaded from: classes.dex */
public class PortraitBeautyMediaInformationView extends MediaInformationView {
    public PortraitBeautyMediaInformationView(Context context) {
        super(context);
    }

    public PortraitBeautyMediaInformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            setVisibility(4);
        } else {
            setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.MediaInformationView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            setVisibility(4);
        } else {
            setVisibility(0);
        }
    }
}
