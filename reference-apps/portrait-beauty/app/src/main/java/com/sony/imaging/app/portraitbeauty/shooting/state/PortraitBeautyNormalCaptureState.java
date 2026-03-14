package com.sony.imaging.app.portraitbeauty.shooting.state;

import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;

/* loaded from: classes.dex */
public class PortraitBeautyNormalCaptureState extends NormalCaptureState {
    public static final String FOCUS_LAYOUT = "FocusLayout";
    private final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.NormalCaptureState, com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (true == PortraitBeautyUtil.bIsAdjustModeGuide) {
            PortraitBeautyUtil.setVirtualMediaIds();
        }
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        openLayout("FocusLayout");
        openLayout(PortraitBeautyConstants.PROCESSING_PROGRESS_LAYOUT);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        closeLayout(PortraitBeautyConstants.PROCESSING_PROGRESS_LAYOUT);
        closeLayout("FocusLayout");
        super.onPause();
        CameraNotificationManager.OnFocusInfo onFocusInfo = new CameraNotificationManager.OnFocusInfo(0, null);
        CameraNotificationManager.getInstance().requestSyncNotify(CameraNotificationManager.DONE_AUTO_FOCUS, onFocusInfo);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
