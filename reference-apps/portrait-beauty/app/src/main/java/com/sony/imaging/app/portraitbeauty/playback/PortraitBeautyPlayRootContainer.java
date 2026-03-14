package com.sony.imaging.app.portraitbeauty.playback;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.PseudoViewMode;
import com.sony.imaging.app.base.playback.contents.StillFolderViewMode;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.playback.browser.PortraitBeautyCatchLightState;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.imaging.app.portraitbeauty.shooting.state.PortraitBeautyDevelopmentState;

/* loaded from: classes.dex */
public class PortraitBeautyPlayRootContainer extends PlayRootContainer {
    public static final String ID_DELETE_MULTIPLE = "ID_DELETE_MULTIPLE";
    private String mPlayBackStateId = null;

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        PortraitBeautyUtil.bIsAdjustModeGuide = false;
        Bundle bundle = this.data;
        if (bundle != null) {
            this.mPlayBackStateId = bundle.getString(PortraitBeautyDevelopmentState.sTransitShootingToPlayBack);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    public String getStartFunction() {
        String retVal;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mPlayBackStateId != null && this.mPlayBackStateId.equalsIgnoreCase(PortraitBeautyAdjustEffectState.ID_ADJUST_EFFECT_STATE)) {
            retVal = PortraitBeautyAdjustEffectState.ID_ADJUST_EFFECT_STATE;
        } else if (this.mPlayBackStateId != null && this.mPlayBackStateId.equalsIgnoreCase(PortraitBeautyCatchLightState.ID_CATCH_LIGHT_PB)) {
            retVal = PortraitBeautyCatchLightState.ID_CATCH_LIGHT_PB;
        } else if (this.mPlayBackStateId != null && this.mPlayBackStateId.equalsIgnoreCase(PortraitBeautyReview.ID_REVIEW)) {
            retVal = PortraitBeautyReview.ID_REVIEW;
            this.mPlayBackStateId = null;
        } else {
            retVal = super.getStartFunction();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return retVal;
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        if (CatchLightPlayBackLayout.sSelectedOptimizedImage != null) {
            CatchLightPlayBackLayout.sSelectedOptimizedImage.release();
            CatchLightPlayBackLayout.sSelectedOptimizedImage = null;
        }
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer
    protected void initializeViewMode() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (!isMemoryCardAvailable() || (this.data != null && this.data.getBoolean("ADJUSTMODE_SHOOTING"))) {
            mgr.setViewMode(PseudoViewMode.class);
        } else {
            mgr.setViewMode(StillFolderViewMode.class);
        }
    }

    @Override // com.sony.imaging.app.base.playback.PlayRootContainer, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (event.getScanCode() == 620) {
            PortraitBeautyConstants.slastStateExposureCompansasion = 0;
            PortraitBeautyConstants.sISEVDIALTURN = true;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.onConvertedKeyDown(event, func);
    }
}
