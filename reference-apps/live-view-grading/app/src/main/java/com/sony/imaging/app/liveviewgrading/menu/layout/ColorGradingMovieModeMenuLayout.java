package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.menu.layout.MovieModeMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.liveviewgrading.ColorGradingInfo;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;

/* loaded from: classes.dex */
public class ColorGradingMovieModeMenuLayout extends MovieModeMenuLayout {
    public static boolean sPushedCenterKey = false;
    private String mSeletedId = null;

    @Override // com.sony.imaging.app.base.menu.layout.MovieModeMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mSeletedId != null) {
            if (this.mSeletedId.equals("ExposureMode_movie_ProgramAuto") || this.mSeletedId.equals("ExposureMode_movie_Aperture") || this.mSeletedId.equals("ExposureMode_movie_Shutter") || this.mSeletedId.equals("ExposureMode_movie_Manual") || this.mSeletedId.equals("movie_ProgramAuto") || this.mSeletedId.equals("movie_Aperture") || this.mSeletedId.equals("movie_Shutter") || this.mSeletedId.equals("movie_Manual")) {
                sPushedCenterKey = true;
                return super.pushedCenterKey();
            }
            displayCaution();
            return 1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        CautionUtilityClass.getInstance().executeTerminate();
        super.closeLayout();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.MovieModeMenuLayout
    public void update(int position) {
        super.update(position);
        this.mSeletedId = this.mService.getMenuItemList().get(position);
    }

    private void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingMovieModeMenuLayout.1
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
                        ColorGradingMovieModeMenuLayout.this.updateView();
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
}
