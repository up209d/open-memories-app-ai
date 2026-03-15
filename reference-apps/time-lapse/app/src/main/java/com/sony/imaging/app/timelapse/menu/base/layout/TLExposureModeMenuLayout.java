package com.sony.imaging.app.timelapse.menu.base.layout;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TLExposureModeMenuLayout extends ExposureModeMenuLayout {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public boolean isParentItemAvailable() {
        if (TLCommonUtil.getThemeUtil().getCurrentState() == 7) {
            return super.isParentItemAvailable();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public ArrayList<String> getMenuItemList() {
        ArrayList<String> mSortedDisplayMenuItems = super.getMenuItemList();
        if (!ModeDialDetector.hasModeDial()) {
            if (mSortedDisplayMenuItems.contains(ExposureModeController.INTELLIGENT_AUTO_MODE)) {
                mSortedDisplayMenuItems.remove(ExposureModeController.INTELLIGENT_AUTO_MODE);
            }
            if (mSortedDisplayMenuItems.contains(ExposureModeController.SUPERIOR_AUTO_MODE)) {
                mSortedDisplayMenuItems.remove(ExposureModeController.SUPERIOR_AUTO_MODE);
            }
            if (mSortedDisplayMenuItems.contains(ExposureModeController.SWEEPSHOOTING_MODE)) {
                mSortedDisplayMenuItems.remove(ExposureModeController.SWEEPSHOOTING_MODE);
            }
            if (mSortedDisplayMenuItems.contains("SceneSelectionMode")) {
                mSortedDisplayMenuItems.remove("SceneSelectionMode");
            }
        }
        return mSortedDisplayMenuItems;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void checkCaution() {
        String itemid;
        if (this.mService != null && (itemid = this.mService.getMenuItemId()) != null && TLCommonUtil.getThemeUtil().getCurrentState() != 7) {
            requestCautionTrigger(itemid);
            closeMenuLayout(null);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        int theme;
        if (TLCommonUtil.getThemeUtil().getCurrentState() == 7) {
            if (!ModeDialDetector.hasModeDial()) {
                String eString = ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE);
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_EXPOSURE_MODE_KEY, eString);
            }
        } else {
            this.mIsCanceling = true;
        }
        CautionUtilityClass.getInstance().executeTerminate();
        super.closeLayout();
        if (ModeDialDetector.hasModeDial() && 7 != (theme = TLCommonUtil.getThemeUtil().getCurrentState())) {
            if (6 == theme) {
                ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "ProgramAuto");
            } else {
                ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        if (TLCommonUtil.getInstance().getCurrentState() == 7) {
            displayCaution();
        } else {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return "ID_EXPOSUREMODESUBMENULAYOUT";
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout
    protected int getCautionId() {
        return TimelapseInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_PASM_OK;
    }

    private void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.timelapse.menu.base.layout.TLExposureModeMenuLayout.1
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
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        TLExposureModeMenuLayout.this.updateView();
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(TimelapseInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_PASM_OK, mKey);
        CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_PASM_OK);
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }
}
