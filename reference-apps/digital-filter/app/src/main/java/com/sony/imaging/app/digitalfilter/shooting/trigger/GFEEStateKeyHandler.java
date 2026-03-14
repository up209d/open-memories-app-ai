package com.sony.imaging.app.digitalfilter.shooting.trigger;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;

/* loaded from: classes.dex */
public class GFEEStateKeyHandler extends EEStateKeyHandler {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.shooting.trigger.EEStateKeyHandler, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_EXPOSURE_DIAL);
        return -1;
    }
}
