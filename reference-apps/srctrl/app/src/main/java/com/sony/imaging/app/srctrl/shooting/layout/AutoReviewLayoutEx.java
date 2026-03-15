package com.sony.imaging.app.srctrl.shooting.layout;

import android.view.View;
import com.sony.imaging.app.base.shooting.layout.AutoReviewLayout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.srctrl.shooting.state.DevelopmentStateExBase;

/* loaded from: classes.dex */
public class AutoReviewLayoutEx extends AutoReviewLayout {
    private static final String tag = AutoReviewLayoutEx.class.getName();

    @Override // com.sony.imaging.app.base.shooting.layout.AutoReviewLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        View footerGuide = getActivity().findViewById(R.id.footer_guide);
        if (footerGuide != null) {
            footerGuide.setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.layout.AutoReviewLayout
    public void setReviewInfoListener() {
        if (DevelopmentStateExBase.s_ReviewInfo != null) {
            displayInfo(DevelopmentStateExBase.s_ReviewInfo);
        } else {
            super.setReviewInfoListener();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.layout.AutoReviewLayout
    public void unsetReviewInfoListener() {
        if (DevelopmentStateExBase.s_ReviewInfo == null) {
            super.unsetReviewInfoListener();
        }
    }
}
