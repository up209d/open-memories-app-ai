package com.sony.imaging.app.lightshaft.caution;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;

/* loaded from: classes.dex */
public class LightShaftCautionLayout extends CautionLayout {
    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int ret = super.onKeyUp(keyCode, event);
        if (131201 == this.cautionfunc.getCurrentCautionId()) {
            int scanCode = event.getScanCode();
            if (scanCode == 772 || scanCode == 516) {
                return 0;
            }
            return ret;
        }
        if (131074 == this.cautionfunc.getCurrentCautionId()) {
            int scanCode2 = event.getScanCode();
            if (scanCode2 == 772 || scanCode2 == 516) {
                return 0;
            }
            if (scanCode2 == 530 || scanCode2 == 786) {
                CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
                return 0;
            }
            return ret;
        }
        return ret;
    }
}
