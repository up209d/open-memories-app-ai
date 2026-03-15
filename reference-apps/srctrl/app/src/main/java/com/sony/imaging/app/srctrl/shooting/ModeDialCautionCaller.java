package com.sony.imaging.app.srctrl.shooting;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.srctrl.caution.InfoEx;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.util.BeepUtility;

/* loaded from: classes.dex */
public class ModeDialCautionCaller {
    private static final String TAG = ModeDialCautionCaller.class.getSimpleName();
    private boolean requireCaution = false;

    /* loaded from: classes.dex */
    public enum CautionType {
        UNKNOWN,
        ON_EE,
        ON_DIAL
    }

    public void checkModeDialCautionRequirement() {
        if (StateController.getInstance().hasModeDial()) {
            switch (ModeDialDetector.getModeDialPosition()) {
                case AppRoot.USER_KEYCODE.MODE_DIAL_AUTO /* 535 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_PROGRAM /* 537 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_AES /* 538 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_AEA /* 539 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL /* 540 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_SCN /* 545 */:
                    Log.v(TAG, "ModeDial: SUPPORTED");
                    this.requireCaution = false;
                    return;
                case AppRoot.USER_KEYCODE.MODE_DIAL_HQAUTO /* 536 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_CUSTOM /* 541 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_PANORAMA /* 542 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_3D /* 543 */:
                case AppRoot.USER_KEYCODE.MODE_DIAL_MOVIE /* 544 */:
                default:
                    Log.v(TAG, "ModeDial: NOT SUPPORTED");
                    this.requireCaution = true;
                    return;
            }
        }
        this.requireCaution = false;
    }

    public boolean requireModeDialCaution() {
        return this.requireCaution;
    }

    public void resetModeDialRequirement() {
        this.requireCaution = false;
    }

    public void showChangeModeDialCaution(Activity activity, CautionType type) {
        if (CautionType.ON_EE.equals(type)) {
            showChangeModeDialCautionOnEe(activity);
        } else if (CautionType.ON_DIAL.equals(type)) {
            showChangeModeDialCautionOnDial();
        } else {
            Log.e(TAG, "UNKNOWN CAUTION TYPE");
        }
    }

    private void showChangeModeDialCautionOnEe(final Activity activity) {
        Log.v(TAG, "showChangeModeDialCautionOnEE");
        CautionUtilityClass.getInstance().setDispatchKeyEvent(InfoEx.CAUTION_ID_SMART_REMOTE_CHANGE_DIAL_TO_ANOTHER_EE, new IkeyDispatchEach(null, 0 == true ? 1 : 0) { // from class: com.sony.imaging.app.srctrl.shooting.ModeDialCautionCaller.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
            public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
                if (!CustomizableFunction.Unchanged.equals(func)) {
                    return 1;
                }
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                        BeepUtility.getInstance().playBeep("BEEP_ID_SELECT");
                        activity.finish();
                        return 1;
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        return -1;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        ModeDialCautionCaller.this.requireCaution = false;
                        return -1;
                    case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                        return -1;
                    default:
                        return 1;
                }
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IConvertibleKeyHandler
            public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
                int code = event.getScanCode();
                if (!CustomizableFunction.Unchanged.equals(func)) {
                    return 1;
                }
                switch (code) {
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        return -1;
                    default:
                        return 1;
                }
            }
        });
        CautionUtilityClass.getInstance().requestTrigger(InfoEx.CAUTION_ID_SMART_REMOTE_CHANGE_DIAL_TO_ANOTHER_EE);
        StateController.getInstance().setAppCondition(StateController.AppCondition.DIAL_INHIBIT);
    }

    private void showChangeModeDialCautionOnDial() {
        Log.v(TAG, "showChangeModeDialCautionOnDial");
        CautionUtilityClass.getInstance().requestTrigger(InfoEx.CAUTION_ID_SMART_REMOTE_CHANGE_DIAL_TO_ANOTHER_MENU);
    }

    public void showModeChangedCaution() {
        CautionUtilityClass.getInstance().requestTrigger(InfoEx.CAUTION_ID_SMART_REMOTE_WILL_CHANGE_TO_AUTO);
    }
}
