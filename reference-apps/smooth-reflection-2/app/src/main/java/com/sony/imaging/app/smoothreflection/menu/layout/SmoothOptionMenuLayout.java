package com.sony.imaging.app.smoothreflection.menu.layout;

import android.hardware.Camera;
import android.util.Pair;
import android.view.KeyEvent;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.smoothreflection.R;
import com.sony.imaging.app.smoothreflection.common.AppLog;
import com.sony.imaging.app.smoothreflection.common.SaUtil;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionBackUpKey;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionUtil;
import com.sony.imaging.app.smoothreflection.shooting.camera.ThemeController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SmoothOptionMenuLayout extends SpecialScreenMenuLayout {
    public static final String MENU_ID = "ID_SMOOTHOPTIONMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private final String DEFAULT_THEME = ThemeController.MID;
    private String mLastSelectedLevel = ThemeController.MID;
    private String mSelectedLevel = ThemeController.MID;
    private ThemeController mThemeController = null;
    private String mCurrentMonotoneValue = ThemeController.BW;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mThemeController = (ThemeController) ThemeController.getInstance();
        String currentTheme = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_THEME, ThemeController.TWILIGHTREFLECTION);
        if (currentTheme.equals(ThemeController.CUSTOM)) {
            this.mLastSelectedLevel = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SHOTS, "64");
            this.mSelectedLevel = this.mLastSelectedLevel;
            clearMonotoneEffect();
        } else {
            this.mLastSelectedLevel = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_SMOOTH_LEVEL, ThemeController.MID);
            this.mSelectedLevel = this.mLastSelectedLevel;
            clearMonotoneEffect();
            if (currentTheme.equals(ThemeController.MONOTONE)) {
                this.mCurrentMonotoneValue = BackUpUtil.getInstance().getPreferenceString(SmoothReflectionBackUpKey.KEY_SELECTED_MONOTONE_COLOR, this.mCurrentMonotoneValue);
                SmoothReflectionUtil.getInstance().setMonotoneSetting(this.mCurrentMonotoneValue);
                SmoothReflectionUtil.getInstance().applyGammaTable();
            }
        }
        this.mThemeController.setValue(ThemeController.SMOOTHING, this.mSelectedLevel);
        super.onResume();
        if (this.mScreenTitleView != null) {
            this.mScreenTitleView.setText(R.string.STRID_FUNC_SMOOTH_PHOTO_SMOOTHING);
        }
        this.mThemeController.setValue(ThemeController.SMOOTHING, this.mSelectedLevel);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return MENU_ID;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        openPreviousMenu();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int returnState = 1;
        if (event.getScanCode() != 595 && event.getScanCode() != 513) {
            returnState = super.onKeyDown(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        SmoothReflectionUtil smoothReflectionUtil = SmoothReflectionUtil.getInstance();
        if (smoothReflectionUtil.isComeFromPageMenu()) {
            smoothReflectionUtil.setComeFromPageMenu(false);
            closeMenuLayout(null);
            return 1;
        }
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mSelectedLevel = this.mLastSelectedLevel;
        this.mThemeController.setValue(ThemeController.SMOOTHING, this.mSelectedLevel);
        if (true == ThemeMenuLayout.sbISCenterKeyPushed) {
            ThemeMenuLayout.sbISCenterKeyPushed = false;
            clearMonotoneEffect();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        SmoothReflectionUtil.getInstance().setComeFromPageMenu(false);
        super.closeLayout();
    }

    private void clearMonotoneEffect() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> supParams = CameraSetting.getInstance().getSupportedParameters(1);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParams = CameraSetting.getInstance().getEmptyParameters();
        if (!SaUtil.isAVIP() && ((CameraEx.ParametersModifier) supParams.second).isRGBMatrixSupported()) {
            ((CameraEx.ParametersModifier) emptyParams.second).setRGBMatrix((int[]) null);
        }
        CameraSetting.getInstance().setParameters(emptyParams);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return -1;
    }
}
