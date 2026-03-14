package com.sony.imaging.app.portraitbeauty.shooting.state;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.ShootingMenuState;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.DROAutoHDRController;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.caution.PortraitBeautyInfo;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class PortraitBeautyShootingMenuState extends ShootingMenuState {
    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppNameView.setText(getResources().getString(R.string.STRID_FUNC_SELFIE_LAUNCHER_NAME_MY));
        AppNameView.show(false);
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
        AELController.getInstance().cancelAELock();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        String droValue = DROAutoHDRController.getInstance().getValue();
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_MODE_DRO, droValue);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        displayCaution(PortraitBeautyInfo.CAUTION_ID_DLAPP_MODE_DIAL_INVALID);
        return 1;
    }

    private void displayCaution(final int cautionId) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.portraitbeauty.shooting.state.PortraitBeautyShootingMenuState.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case 103:
                    case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                    case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    case 232:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 1;
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                    case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                    case AppRoot.USER_KEYCODE.FN /* 520 */:
                    case AppRoot.USER_KEYCODE.AEL /* 532 */:
                    case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
                    case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
                    case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                    case AppRoot.USER_KEYCODE.SLIDE_AF_MF /* 596 */:
                    case AppRoot.USER_KEYCODE.DISP /* 608 */:
                    case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
                    case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
                    case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
                    case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                        return -1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
                        return 1;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    default:
                        return -1;
                }
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                    case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    default:
                        return -1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.base.menu.MenuState
    protected boolean canRemoveState() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ShootingMenuState, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        PortraitBeautyConstants.sISEVDIALTURN = true;
        PortraitBeautyConstants.slastStateExposureCompansasion = 0;
        onClosed(null);
        return 1;
    }
}
