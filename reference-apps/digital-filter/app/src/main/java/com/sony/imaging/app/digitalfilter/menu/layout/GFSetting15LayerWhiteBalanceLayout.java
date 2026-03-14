package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class GFSetting15LayerWhiteBalanceLayout extends GFFn15LayerWhiteBalanceLayout {
    public static final String MENU_ID = "ID_GFSETTING15LAYERWHITEBALANCELAYOUT";

    @Override // com.sony.imaging.app.digitalfilter.menu.layout.GFFn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (!BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_WB_LINK_MSG, false)) {
            showLinkMsg();
        }
    }

    @Override // com.sony.imaging.app.digitalfilter.menu.layout.GFFn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    private void showLinkMsg() {
        boolean isValidLink = false;
        if (GFLinkAreaController.getInstance().isWBLink(mSetting)) {
            if (isLand) {
                if (GFLinkAreaController.getInstance().isWBLink(1) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isWBLink(2))) {
                    isValidLink = true;
                }
            } else if (isSky) {
                if (GFLinkAreaController.getInstance().isWBLink(0) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isWBLink(2))) {
                    isValidLink = true;
                }
            } else if (GFLinkAreaController.getInstance().isWBLink(0) || GFLinkAreaController.getInstance().isWBLink(1)) {
                isValidLink = true;
            }
        }
        if (isValidLink) {
            Bundle bundle = new Bundle();
            bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_WB");
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.digitalfilter.menu.layout.GFFn15LayerWhiteBalanceLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                closeMenuLayout(new Bundle());
                return 0;
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                if (GFWhiteBalanceController.getInstance().isSupportedABGM()) {
                    GFSettingMenuLayout.requestWBAdjustment = true;
                }
                closeMenuLayout(new Bundle());
                return 0;
            default:
                int ret = super.onKeyDown(keyCode, event);
                return ret;
        }
    }
}
