package com.sony.imaging.app.smoothreflection.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.smoothreflection.SmoothReflectionApp;
import com.sony.imaging.app.smoothreflection.caution.SmoothReflectionInfo;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;

/* loaded from: classes.dex */
public class SmoothReflectionModeMenuLayout extends ExposureModeMenuLayout {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public boolean isParentItemAvailable() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            AppLog.info(TAG, e.toString());
        }
        if (ThemeController.CUSTOM.equalsIgnoreCase(selectedTheme)) {
            boolean retVal = super.isParentItemAvailable();
            return retVal;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (232 == event.getScanCode()) {
            if (SmoothReflectionApp.sBootFactor == 0) {
                SmoothReflectionUtil.getInstance().setCurrentMenuSelectionScreen(SmoothReflectionConstants.THEME_SELECTION);
            }
        } else {
            SmoothReflectionUtil.getInstance().setCurrentMenuSelectionScreen(null);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (232 == event.getScanCode()) {
            if (SmoothReflectionApp.sBootFactor == 0) {
                SmoothReflectionUtil.getInstance().setCurrentMenuSelectionScreen(SmoothReflectionConstants.THEME_SELECTION);
            }
        } else {
            SmoothReflectionUtil.getInstance().setCurrentMenuSelectionScreen(null);
        }
        return super.onKeyUp(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void checkCaution() {
        String itemid;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mService != null && (itemid = this.mService.getMenuItemId()) != null) {
            String selectedTheme = null;
            try {
                selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
            } catch (IController.NotSupportedException e) {
                AppLog.info(TAG, e.toString());
            }
            if (!ThemeController.CUSTOM.equalsIgnoreCase(selectedTheme)) {
                requestCautionTrigger(itemid);
                closeMenuLayout(null);
            }
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = null;
        try {
            selectedTheme = ThemeController.getInstance().getValue(ThemeController.THEMESELECTION);
        } catch (IController.NotSupportedException e) {
            e.printStackTrace();
        }
        if (!ThemeController.CUSTOM.equalsIgnoreCase(selectedTheme)) {
            displayCaution(SmoothReflectionInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_WHEN_INVALID_THEME);
        } else {
            super.requestCautionTrigger(itemId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void displayCaution(final int cautionId) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.smoothreflection.menu.layout.SmoothReflectionModeMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                int scanCode = event.getScanCode();
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                        CautionUtilityClass.getInstance().disapperTrigger(cautionId);
                        return 0;
                    default:
                        return -1;
                }
            }

            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyUp(int keyCode, KeyEvent event) {
                return -1;
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionId, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return ExposureModeMenuLayout.MENU_ID;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID("ApplicationTop");
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, "ApplicationTop");
        bundle.putString(MenuState.LAYOUT_ID, layoutID);
        bundle.putBoolean(MenuState.BOOLEAN_FINISH_MENU_STATE, true);
        closeMenuLayout(bundle);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
