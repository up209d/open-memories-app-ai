package com.sony.imaging.app.startrails.menu.base.layout;

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
import com.sony.imaging.app.startrails.base.menu.controller.STExposureModeController;
import com.sony.imaging.app.startrails.common.caution.STInfo;
import com.sony.imaging.app.startrails.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STBackUpKey;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class STExposureModeMenuLayout extends ExposureModeMenuLayout {
    private static String TAG = "STExposureModeMenuLayout";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public boolean isParentItemAvailable() {
        boolean isParentItemAvailable;
        AppLog.enter(TAG, AppLog.getMethodName());
        super.isParentItemAvailable();
        if (STUtility.getInstance().getCurrentTrail() == 2) {
            isParentItemAvailable = true;
        } else {
            isParentItemAvailable = false;
        }
        AppLog.exit(TAG, AppLog.getMethodName() + "  " + isParentItemAvailable);
        return isParentItemAvailable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void checkCaution() {
        String itemid;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mService != null && (itemid = this.mService.getMenuItemId()) != null) {
            if (STUtility.getInstance().getCurrentTrail() != 2) {
                requestCautionTrigger(itemid);
                closeMenuLayout(null);
            }
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        CautionUtilityClass.getInstance().executeTerminate();
        if (this.mService == null) {
            updateCustomPreferences();
            return;
        }
        if (ModeDialDetector.hasModeDial() && STUtility.getInstance().getCurrentTrail() != 2) {
            this.mIsCanceling = true;
        }
        super.closeLayout();
        updateCustomPreferences();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateCustomPreferences() {
        if (!ModeDialDetector.hasModeDial() && STUtility.getInstance().getCurrentTrail() == 2) {
            String eString = ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
            BackUpUtil.getInstance().setPreference(STBackUpKey.CUSTOM_EXPOSURE_MODE_KEY, eString);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (STUtility.getInstance().getCurrentTrail() == 2) {
            displayCaution();
        } else {
            CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_INVALID_THEME);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return "ID_EXPOSUREMODESUBMENULAYOUT";
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout
    protected int getCautionId() {
        return STInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_PASM_OK;
    }

    private void displayCaution() {
        AppLog.enter(TAG, AppLog.getMethodName());
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.startrails.menu.base.layout.STExposureModeMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        break;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                    case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        STExposureModeMenuLayout.this.updateView();
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        STUtility.getInstance().setEVDialRotated(true);
                        ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
                        break;
                    default:
                        return 1;
                }
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(STInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_PASM_OK, mKey);
        CautionUtilityClass.getInstance().requestTrigger(STInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_PASM_OK);
        AppLog.exit(TAG, AppLog.getMethodName());
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
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isAppTopBooted = false;
        if (STUtility.getInstance().isAppTopBooted() && ModeDialDetector.hasModeDial() && this.data.getString(ExposureModeController.EXPOSURE_MODE) != null && STExposureModeController.getInstance().isValidDialPosition(ModeDialDetector.getModeDialPosition())) {
            isAppTopBooted = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return isAppTopBooted;
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
        AppLog.enter(TAG, AppLog.getMethodName());
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(ThemeSelectionController.APP_TOP);
        openNextMenu(ThemeSelectionController.APP_TOP, layoutID, true, this.data);
        AppLog.exit("ID_EXPOSUREMODESUBMENULAYOUT", "postSetValue  ExposureMode check fail");
    }
}
