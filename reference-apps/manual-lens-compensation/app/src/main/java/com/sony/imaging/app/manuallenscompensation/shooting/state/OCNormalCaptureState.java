package com.sony.imaging.app.manuallenscompensation.shooting.state;

import com.sony.imaging.app.base.shooting.NormalCaptureState;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.scalar.sysutil.didep.Kikilog;

/* loaded from: classes.dex */
public class OCNormalCaptureState extends NormalCaptureState {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.CaptureStateBase
    public void writeKikiLog() {
        OCUtil.getInstance().setLastCaptureMode(1);
        super.writeKikiLog();
        Kikilog.Options options = new Kikilog.Options();
        options.getClass();
        options.recType = 32;
        Integer kikilogId = 4166;
        Kikilog.setUserLog(kikilogId.intValue(), options);
    }
}
