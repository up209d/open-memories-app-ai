package com.sony.imaging.app.portraitbeauty.playback.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.AppContext;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyPlayDisplayModeObserver;

/* loaded from: classes.dex */
public class PortraitBeautyPseudoRecSingleLayout extends PseudoRecSingleLayout {
    private View mCurrentView = null;
    private FooterGuide mFooterGuide = null;
    int mcautionId = 0;

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mCurrentView != null) {
            this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(getFooterGuideResource());
            if (this.mFooterGuide != null) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_PLAY_FGUIDE_PJONE));
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mFooterGuide = null;
        this.mCurrentView = null;
        if (this.mcautionId != 0) {
            CautionUtilityClass.getInstance().disapperTrigger(this.mcautionId);
            this.mcautionId = 0;
        }
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecSingleLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        this.mcautionId = PortraitBeautyInfo.CAUTION_ID_DLAPP_NO_CARD_ON_PLAYBACK_PSEUDOREC;
        CautionUtilityClass.getInstance().requestTrigger(this.mcautionId);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDispFuncKey() {
        PortraitBeautyPlayDisplayModeObserver.getInstance().toggleDisplayMode(1);
        return 1;
    }
}
