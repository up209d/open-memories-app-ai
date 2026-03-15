package com.sony.imaging.app.timelapse.shooting.state;

import com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class TLCustomWhiteBalanceControllerState extends CustomWhiteBalanceControllerState {
    private static String TAG = "TLCustomWhiteBalanceControllerState";

    @Override // com.sony.imaging.app.base.shooting.CustomWhiteBalanceControllerState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        AppLog.enter(TAG, "onPause");
        updateCustomWhiteBalanceValue();
        AppLog.exit(TAG, "onPause");
    }

    private void updateCustomWhiteBalanceValue() {
        AppLog.enter(TAG, "updateCustomWhiteBalanceValue");
        String wbValue = WhiteBalanceController.getInstance().getValue();
        BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_WB_KEY, wbValue);
        AppLog.trace(TAG, "CUSTOM_WB_VALUE  " + wbValue);
        WhiteBalanceController.WhiteBalanceParam param = (WhiteBalanceController.WhiteBalanceParam) WhiteBalanceController.getInstance().getDetailValue();
        String optValue = (("" + param.getLightBalance()) + "/" + param.getColorComp()) + "/" + param.getColorTemp();
        BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_WB_KEY_OPTION_VALUE, optValue);
        AppLog.trace(TAG, "CUSTOM_WB_OPTION_VALUE  " + optValue);
        AppLog.exit(TAG, "updateCustomWhiteBalanceValue");
    }
}
