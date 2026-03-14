package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.ColorGradingCaution;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class LastBastionMenuLayout extends BaseMenuLayout {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        AppLog.exit(TAG, AppLog.getMethodName());
        return new View(getActivity());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        displayCaution();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        CautionUtilityClass.getInstance().disapperTrigger(ColorGradingCaution.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE);
        CautionUtilityClass.getInstance().setDispatchKeyEvent(ColorGradingCaution.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE, null);
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected void displayCaution() {
        AppLog.enter(TAG, AppLog.getMethodName());
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.LastBastionMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        return 1;
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                        LastBastionMenuLayout.this.getActivity().finish();
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        if (ModeDialDetector.getModeDialPosition() != -1) {
                            CautionUtilityClass.getInstance().executeTerminate();
                            turnedModeDial();
                        }
                        return 0;
                    case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        if (!EVDialDetector.hasEVDial()) {
                            return 1;
                        }
                        ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                        return 1;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().requestTrigger(ColorGradingCaution.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE);
        CautionUtilityClass.getInstance().setDispatchKeyEvent(ColorGradingCaution.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE, mKey);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
