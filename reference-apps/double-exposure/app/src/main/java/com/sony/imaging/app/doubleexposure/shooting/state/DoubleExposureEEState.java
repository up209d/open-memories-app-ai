package com.sony.imaging.app.doubleexposure.shooting.state;

import android.os.Bundle;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.EEState;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.doubleexposure.DoubleExposureApp;
import com.sony.imaging.app.doubleexposure.R;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DESA;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.ModeSelectionController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.fw.ICustomKey;

/* loaded from: classes.dex */
public class DoubleExposureEEState extends EEState {
    private final String TAG = AppLog.getClassName();
    private final String NEXT_STATE_MENU = ICustomKey.CATEGORY_MENU;
    private final String DATA_TAG_ITEMID = MenuState.ITEM_ID;
    private String mItemId = null;
    private Bundle mBundle = null;

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.data != null) {
            this.mItemId = this.data.getString(MenuState.ITEM_ID);
            this.data = null;
        }
        super.onResume();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.shooting.EEState, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mItemId = null;
        this.mBundle = null;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.EEState
    public String getNextChildState() {
        String state;
        AppLog.enter(this.TAG, AppLog.getMethodName());
        DoubleExposureUtil doubleExposureUtil = DoubleExposureUtil.getInstance();
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        String currentMenuSelectionScreen = doubleExposureUtil.getCurrentMenuSelectionScreen();
        String currentShootingScreen = doubleExposureUtil.getCurrentShootingScreen();
        String title = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        updateApplicationTiltle(title);
        if (currentShootingScreen != null && currentShootingScreen.equalsIgnoreCase(DoubleExposureConstant.FIRST_SHOOTING)) {
            DESA.getInstance().terminate();
        } else {
            String aspectRatio = DoubleExposureUtil.getInstance().getFirstImageAspectRatio();
            if (aspectRatio != null) {
                PictureSizeController.getInstance().setValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO, aspectRatio);
                doubleExposureUtil.setFirstImageAspectRatio(null);
            } else {
                AppLog.info(this.TAG, "Aspect Ratio is null");
            }
        }
        if (ExposureModeController.EXPOSURE_MODE.equals(this.mItemId)) {
            this.mBundle = new Bundle();
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            if (DoubleExposureApp.sBootFactor != 0) {
                doubleExposureUtil.setCurrentMenuSelectionScreen(null);
            }
            this.mItemId = null;
            state = ICustomKey.CATEGORY_MENU;
        } else if (currentMenuSelectionScreen != null && currentMenuSelectionScreen.equalsIgnoreCase(DoubleExposureConstant.THEME_SELECTION)) {
            this.mItemId = ThemeSelectionController.THEMESELECTION;
            this.mBundle = new Bundle();
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            doubleExposureUtil.setCurrentMenuSelectionScreen(null);
            this.mItemId = null;
            AppLog.exit(this.TAG, AppLog.getMethodName());
            state = ICustomKey.CATEGORY_MENU;
        } else if (selectedTheme.equalsIgnoreCase("Manual") && currentMenuSelectionScreen != null && currentMenuSelectionScreen.equalsIgnoreCase(DoubleExposureConstant.MODE_SELECTION)) {
            this.mItemId = ModeSelectionController.MODESELECTION;
            this.mBundle = new Bundle();
            this.mBundle.putString(MenuState.ITEM_ID, this.mItemId);
            doubleExposureUtil.setCurrentMenuSelectionScreen(null);
            this.mItemId = null;
            AppLog.exit(this.TAG, AppLog.getMethodName());
            state = ICustomKey.CATEGORY_MENU;
        } else {
            AppLog.exit(this.TAG, AppLog.getMethodName());
            state = super.getNextChildState();
        }
        return state;
    }

    @Override // com.sony.imaging.app.base.shooting.EEState
    protected Bundle setStateBundle() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mBundle;
    }

    private void updateApplicationTiltle(String value) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (value.equalsIgnoreCase("Manual")) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_MANUAL));
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SIL)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_SIL));
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SKY)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_SKY));
        } else if (value.equalsIgnoreCase(ThemeSelectionController.TEXTURE)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_TEXTURE));
        } else if (value.equalsIgnoreCase("Rotation")) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_ROTATE));
        } else if (value.equalsIgnoreCase(ThemeSelectionController.MIRROR)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_MIRROR));
        } else if (value.equalsIgnoreCase(ThemeSelectionController.SOFTFILTER)) {
            AppNameView.setText(getResources().getString(R.string.STRID_FUNC_MLE_SOFTFILTER));
        }
        AppNameView.show(true);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }
}
