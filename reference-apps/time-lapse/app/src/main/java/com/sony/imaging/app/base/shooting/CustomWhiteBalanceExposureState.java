package com.sony.imaging.app.base.shooting;

import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class CustomWhiteBalanceExposureState extends StateBase {
    private static final String TAG = "CustomWhiteBalanceExposureState";

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (Environment.isCustomWBOnePush()) {
            WhiteBalanceController wbc = WhiteBalanceController.getInstance();
            wbc.saveCustomWhiteBalance(wbc.getValue());
            this.data.putInt("colorCompensation", 0);
            this.data.putInt("colorTemperature", WhiteBalanceController.DEF_TEMP);
            this.data.putBoolean("inRange", true);
            this.data.putInt("lightBalance", 0);
        }
        openLayout(StateBase.DEFAULT_LAYOUT, this.data);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraEx.CustomWhiteBalanceInfo info;
        WhiteBalanceController wbc = WhiteBalanceController.getInstance();
        wbc.setValue(WhiteBalanceController.WHITEBALANCE, wbc.getValue());
        if (!Environment.isCustomWBOnePush() && (info = (CameraEx.CustomWhiteBalanceInfo) removeData("CUSTOM_WB_INFO")) != null) {
            WhiteBalanceController.WhiteBalanceParam wbInfo = new WhiteBalanceController.WhiteBalanceParam(info.lightBalance, info.colorCompensation, info.colorTemperature);
            wbc.setDetailValue(wbInfo);
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase
    protected int getMyBeepPattern() {
        return 2;
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State
    public ApoWrapper.APO_TYPE getApoType() {
        return ApoWrapper.APO_TYPE.SPECIAL;
    }
}
