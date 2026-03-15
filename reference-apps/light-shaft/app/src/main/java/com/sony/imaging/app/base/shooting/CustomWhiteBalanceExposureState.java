package com.sony.imaging.app.base.shooting;

import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class CustomWhiteBalanceExposureState extends StateBase {
    private static final String TAG = "CustomWhiteBalanceExposureState";

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        openLayout(StateBase.DEFAULT_LAYOUT, this.data);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        WhiteBalanceController wbc = WhiteBalanceController.getInstance();
        wbc.setValue(WhiteBalanceController.WHITEBALANCE, wbc.getValue());
        CameraEx.CustomWhiteBalanceInfo info = (CameraEx.CustomWhiteBalanceInfo) removeData("CUSTOM_WB_INFO");
        if (info != null) {
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
