package com.sony.imaging.app.each.shooting;

import android.os.Message;
import android.util.Log;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.each.EachApp;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class TLS_EEState extends EEState {
    String TAG = "TLS_EEState";
    private final String KEY_BKUP_OPTIONS_PARAM__FIRST_TIME_GUIDE_SHOWN = "KEY_BKUP_OPTIONS_PARAM__FIRST_TIME_GUIDE_SHOWN";
    final int CAUTION_ID_DLAPP_STEADY_SHOT = 132004;

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d("TLS_EEState", "EachApp.RequestCautionAtEE:" + EachApp.RequestCautionAtEE);
        super.onResume();
        EachApp.ExposingByTouchLessShutter = false;
        if ("FALSE".equals(BackUpUtil.getInstance().getPreferenceString("KEY_BKUP_OPTIONS_PARAM__FIRST_TIME_GUIDE_SHOWN", "FALSE"))) {
            Log.d("TLS_EEState", "request Caution");
            CautionUtilityClass.getInstance().requestTrigger(132001);
            BackUpUtil.getInstance().setPreference("KEY_BKUP_OPTIONS_PARAM__FIRST_TIME_GUIDE_SHOWN", "TRUE");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        Log.d(this.TAG, "handleMessage<<<");
        int sCautionId = 0;
        if (518 == msg.what) {
            sCautionId = getCautionId();
            Log.d(this.TAG, "sCautionId:" + sCautionId);
            if (sCautionId != 0) {
                CautionUtilityClass.getInstance().requestTrigger(sCautionId);
            }
        }
        if (sCautionId != 0) {
            return false;
        }
        boolean ret = super.handleMessage(msg);
        return ret;
    }

    public int getCautionId() {
        AvailableInfo.update();
        if (!AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_C") && !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_OSS_SW_ON_TYPE_P")) {
            return 0;
        }
        return 132004;
    }
}
