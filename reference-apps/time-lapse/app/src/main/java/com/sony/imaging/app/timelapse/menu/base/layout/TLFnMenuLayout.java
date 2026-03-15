package com.sony.imaging.app.timelapse.menu.base.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.layout.FnMenuLayout;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.CustomKeySupportController;
import com.sony.imaging.app.timelapse.TimeLapseConstants;

/* loaded from: classes.dex */
public class TLFnMenuLayout extends FnMenuLayout {
    private static final String TAG = TLFnMenuLayout.class.getSimpleName();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return FnMenuLayout.MENU_ID;
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppNameView.show(false);
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        int[] cautionId = this.mService.getMenuItemCautionId(itemId);
        if (cautionId == null || TimeLapseConstants.SELECTED_MENU_ITEM_THEME.contains(itemId)) {
            displayCaution(itemId);
        } else {
            super.requestCautionTrigger(itemId);
        }
        AppLog.info(TAG, "requestCautionTrigger  itemId  " + itemId);
    }

    private void displayCaution(String itemId) {
        CautionUtilityClass.getInstance().requestTrigger(CustomKeySupportController.getInstance().getCautionID(itemId));
    }
}
