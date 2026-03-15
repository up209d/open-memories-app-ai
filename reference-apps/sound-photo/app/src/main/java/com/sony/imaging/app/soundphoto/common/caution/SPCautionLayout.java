package com.sony.imaging.app.soundphoto.common.caution;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.soundphoto.util.AppLog;

/* loaded from: classes.dex */
public class SPCautionLayout extends CautionLayout {
    private static final String TAG = "SPCautionLayout";

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int ret = super.onKeyUp(keyCode, event);
        int scanCode = event.getScanCode();
        if (scanCode == 772 || scanCode == 516) {
            ret = 0;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int returnState;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (131202 == this.cautionfunc.getCurrentCautionId() || 131206 == this.cautionfunc.getCurrentCautionId()) {
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.MENU /* 514 */:
                case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                    CautionUtilityClass.getInstance().executeTerminate();
                case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                    returnState = -1;
                    break;
                default:
                    returnState = super.onKeyDown(keyCode, event);
                    break;
            }
        } else if (131204 == this.cautionfunc.getCurrentCautionId()) {
            switch (event.getScanCode()) {
                case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    returnState = 1;
                    break;
                case 517:
                default:
                    returnState = super.onKeyDown(keyCode, event);
                    break;
                case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                    returnState = 1;
                    break;
            }
        } else {
            returnState = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }
}
