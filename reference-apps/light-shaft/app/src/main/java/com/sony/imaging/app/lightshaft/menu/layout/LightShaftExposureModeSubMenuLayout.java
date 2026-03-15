package com.sony.imaging.app.lightshaft.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.BootFactor;
import com.sony.imaging.app.lightshaft.AppLog;
import com.sony.imaging.app.lightshaft.caution.LightShaftInfo;
import com.sony.imaging.app.lightshaft.shooting.camera.EffectSelectController;
import com.sony.imaging.app.lightshaft.shooting.camera.LightShaftExposureModeController;

/* loaded from: classes.dex */
public class LightShaftExposureModeSubMenuLayout extends ExposureModeMenuLayout {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        displayCaution();
        super.requestCautionTrigger(itemId);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return "ID_EXPOSUREMODESUBMENULAYOUT";
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (this.mService != null) {
            super.closeLayout();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout
    protected int getCautionId() {
        return LightShaftInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_PASM_OK;
    }

    private void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.lightshaft.menu.layout.LightShaftExposureModeSubMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        LightShaftExposureModeSubMenuLayout.this.updateView();
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
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
        CautionUtilityClass.getInstance().setDispatchKeyEvent(getCautionId(), mKey);
        CautionUtilityClass.getInstance().requestTrigger(getCautionId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        if (isAppTopBooted()) {
            openAppTopScreen();
        } else {
            super.postSetValue();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        if (isAppTopBooted()) {
            openAppTopScreen();
        } else {
            super.closeMenuLayout(bundle);
        }
    }

    private boolean isAppTopBooted() {
        if (BootFactor.get().bootFactor != 0 || !ModeDialDetector.hasModeDial() || this.data.getString(ExposureModeController.EXPOSURE_MODE) == null || !LightShaftExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void openRootMenu() {
        if (isAppTopBooted()) {
            openAppTopScreen();
        } else {
            super.openRootMenu();
        }
    }

    private void openAppTopScreen() {
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(EffectSelectController.EFFECTSELECT);
        openNextMenu(EffectSelectController.EFFECTSELECT, layoutID, true, this.data);
        AppLog.exit("ID_EXPOSUREMODESUBMENULAYOUT", "postSetValue  ExposureMode check fail");
    }
}
