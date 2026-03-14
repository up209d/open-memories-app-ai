package com.sony.imaging.app.doubleexposure.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.doubleexposure.caution.DoubleExposureInfo;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class DoubleExposureModeMenuLayout extends ExposureModeMenuLayout {
    private final String TAG = AppLog.getClassName();
    private boolean mIsTurnedEVDial = false;

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mIsTurnedEVDial) {
            DoubleExposureUtil.getInstance().updateExposureCompensation();
            this.mIsTurnedEVDial = false;
        }
        super.onPause();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public boolean isParentItemAvailable() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if ("Manual".equalsIgnoreCase(selectedTheme)) {
            boolean retVal = super.isParentItemAvailable();
            return retVal;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void checkCaution() {
        String itemid;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mService != null && (itemid = this.mService.getMenuItemId()) != null) {
            String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
            if (!"Manual".equalsIgnoreCase(selectedTheme)) {
                requestCautionTrigger(itemid);
                closeMenuLayout(null);
            }
            AppLog.exit(this.TAG, AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (!"Manual".equalsIgnoreCase(selectedTheme)) {
            displayCaution(DoubleExposureInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_WHEN_INVALID_THEME);
        } else {
            super.requestCautionTrigger(itemId);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void displayCaution(final int cautionId) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.doubleexposure.menu.layout.DoubleExposureModeMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int retValue;
                AppLog.enter(DoubleExposureModeMenuLayout.this.TAG, AppLog.getMethodName());
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        retValue = 1;
                        break;
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        retValue = 0;
                        break;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        retValue = 0;
                        break;
                    default:
                        retValue = -1;
                        break;
                }
                AppLog.exit(DoubleExposureModeMenuLayout.this.TAG, AppLog.getMethodName());
                return retValue;
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                AppLog.enter(DoubleExposureModeMenuLayout.this.TAG, AppLog.getMethodName());
                AppLog.exit(DoubleExposureModeMenuLayout.this.TAG, AppLog.getMethodName());
                return -1;
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mIsTurnedEVDial = true;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return super.turnedEVDial();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return ExposureModeMenuLayout.MENU_ID;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(ThemeSelectionController.THEMESELECTION);
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, ThemeSelectionController.THEMESELECTION);
        bundle.putString(MenuState.LAYOUT_ID, layoutID);
        bundle.putBoolean(MenuState.BOOLEAN_FINISH_MENU_STATE, true);
        closeMenuLayout(bundle);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
