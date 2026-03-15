package com.sony.imaging.app.soundphoto.state;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.ForceExitScreenState;
import com.sony.imaging.app.soundphoto.common.caution.SPInfo;
import com.sony.imaging.app.soundphoto.util.AppLog;

/* loaded from: classes.dex */
public class SPForceExitScreenState extends ForceExitScreenState {
    @Override // com.sony.imaging.app.base.common.ForceExitScreenState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.common.ForceExitScreenState, com.sony.imaging.app.base.common.layout.ForceExitScreenLayout.Callback
    public void onFinish() {
        AppLog.info(AppLog.getClassName(), AppLog.getMethodName());
        CautionUtilityClass.getInstance().requestTrigger(SPInfo.CAUTION_ID_DLAPP_DB_ERROR);
        removeState();
        ((BaseApp) getActivity()).finish(false);
    }
}
