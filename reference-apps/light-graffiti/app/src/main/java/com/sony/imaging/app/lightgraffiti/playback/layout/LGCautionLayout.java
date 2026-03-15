package com.sony.imaging.app.lightgraffiti.playback.layout;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionLayout;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.lightgraffiti.caution.LGInfo;

/* loaded from: classes.dex */
public class LGCautionLayout extends CautionLayout {
    private String TAG = "LGCautionLayout";

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        int scanCode = event.getScanCode();
        Log.i(this.TAG, "onKeyDown scanCode = " + scanCode);
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                closeLayout();
                return 0;
            case AppRoot.USER_KEYCODE.IR_SHUTTER /* 552 */:
            case AppRoot.USER_KEYCODE.IR_SHUTTER_2SEC /* 553 */:
                disappearCurrentCaution();
                handled = true;
                break;
        }
        if (handled) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.caution.CautionLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        Log.i(this.TAG, "onKeyDown scanCode = " + scanCode);
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                closeLayout();
                return 0;
            default:
                if (0 != 0) {
                    return 1;
                }
                return super.onKeyDown(keyCode, event);
        }
    }

    private void disappearCurrentCaution() {
        Log.d(this.TAG, "disappearCurrentCaution");
        try {
            Log.d(this.TAG, "onNotify:getcaution_id");
            int caution_ID = CautionUtilityClass.getInstance().getCurrentCautionData().maxPriorityId;
            switch (caution_ID) {
                case LGInfo.CAUTION_ID_DLAPP_FUNC_INV_APP /* 132004 */:
                case LGInfo.CAUTION_ID_DLAPP_FUNC_INV_APP_APO_NONE /* 132007 */:
                    CautionUtilityClass.getInstance().disapperTrigger(caution_ID);
                    return;
                case LGInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING /* 132005 */:
                case LGInfo.CAUTION_ID_DLAPP_MEMORY_CARD_FULL /* 132006 */:
                default:
                    return;
            }
        } catch (IndexOutOfBoundsException ex) {
            Log.i(this.TAG, ex.getMessage());
        } catch (NullPointerException e) {
            Log.i(this.TAG, "NullPointerException");
        }
    }
}
