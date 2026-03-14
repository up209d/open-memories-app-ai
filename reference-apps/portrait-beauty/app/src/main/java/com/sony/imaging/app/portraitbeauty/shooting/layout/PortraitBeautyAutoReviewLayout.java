package com.sony.imaging.app.portraitbeauty.shooting.layout;

import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.shooting.layout.AutoReviewLayout;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppContext;

/* loaded from: classes.dex */
public class PortraitBeautyAutoReviewLayout extends AutoReviewLayout {
    private FooterGuide mFooterGuide = null;

    @Override // com.sony.imaging.app.base.shooting.layout.AutoReviewLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mCurrentLayout != null) {
            this.mFooterGuide = (FooterGuide) this.mCurrentLayout.findViewById(getFooterGuideResource());
            if (this.mFooterGuide != null) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_PLAY_FGUIDE_PJONE));
            }
        }
    }
}
