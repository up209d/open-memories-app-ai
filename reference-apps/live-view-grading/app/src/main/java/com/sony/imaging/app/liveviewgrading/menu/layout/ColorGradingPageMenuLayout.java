package com.sony.imaging.app.liveviewgrading.menu.layout;

import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;

/* loaded from: classes.dex */
public class ColorGradingPageMenuLayout extends PageMenuLayout {
    private static final String TAG = AppLog.getClassName();
    private ColorGradingController mController = ColorGradingController.getInstance();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return PageMenuLayout.MENU_ID;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (itemid.equalsIgnoreCase("ApplicationSettings")) {
            this.mController.setComingFromApplicationSettings(true);
            String curGroup = this.mController.getLastSelectedGroup();
            if (curGroup.equals(ColorGradingController.STANDARD)) {
                itemid = ColorGradingController.STANDARD;
            } else if (curGroup.equals(ColorGradingController.CINEMA)) {
                itemid = ColorGradingController.CINEMA;
            } else if (curGroup.equals(ColorGradingController.EXTREME)) {
                itemid = ColorGradingController.EXTREME;
            }
        } else if (itemid.equalsIgnoreCase("PresetAdjustmentSettings")) {
            this.mController.setComeFromMenu(true);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        super.doItemClickProcessing(itemid);
    }
}
