package com.sony.imaging.app.base.shooting;

import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.util.ApoWrapper;

/* loaded from: classes.dex */
public class CustomWhiteBalanceEEState extends StateBase {
    String mItemIdForCWBreset;

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mItemIdForCWBreset = this.data.getString(WhiteBalanceController.BUNDLE_RESET_ITEMID);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        WhiteBalanceController wbc = new WhiteBalanceController();
        if (this.mItemIdForCWBreset != null) {
            wbc.setValue(WhiteBalanceController.WHITEBALANCE, this.mItemIdForCWBreset);
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
