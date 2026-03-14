package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.liveviewgrading.ColorGradingInfo;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingExposureModeController;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ColorGradingExposureModeSubMenuLayout extends ExposureModeMenuLayout {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        displayCaution();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return ExposureModeMenuLayout.MENU_ID;
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

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout, com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        CautionUtilityClass.getInstance().executeTerminate();
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout
    protected int getCautionId() {
        return ColorGradingInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE_OK;
    }

    private void displayCaution() {
        ColorGradingController.getInstance().setPlayKeyInvalid(true);
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingExposureModeSubMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        ColorGradingExposureModeSubMenuLayout.this.updateView();
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(ColorGradingInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE_OK, mKey);
        CautionUtilityClass.getInstance().requestTrigger(ColorGradingInfo.CAUTION_ID_DLAPP_CHANGE_DIAL_TO_MOVIE_OK);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void postSetValue() {
        String itemId = ColorGradingController.COLORGRADING;
        BaseMenuService service = new BaseMenuService(getActivity());
        String layoutID = service.getMenuItemCustomStartLayoutID(itemId);
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, itemId);
        bundle.putString(MenuState.LAYOUT_ID, layoutID);
        bundle.putBoolean(MenuState.BOOLEAN_FINISH_MENU_STATE, true);
        closeMenuLayout(bundle);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        if (!ColorGradingExposureModeController.getInstance().isValidDialPosition()) {
            ColorGradingController.getInstance().setPlayKeyInvalid(true);
        }
        return super.pushedPlayBackKey();
    }
}
