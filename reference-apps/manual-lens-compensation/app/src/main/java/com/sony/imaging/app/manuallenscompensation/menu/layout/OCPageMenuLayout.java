package com.sony.imaging.app.manuallenscompensation.menu.layout;

import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.menu.controller.ExternalMediaStateController;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.DeleteFocusMagnifierController;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCFocalLengthController;
import com.sony.imaging.app.manuallenscompensation.widget.OCAppNameFocalValue;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class OCPageMenuLayout extends PageMenuLayout {
    private static final String TAG = OCPageMenuLayout.class.getSimpleName();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        AppLog.info(TAG, AppLog.getMethodName());
        return "ID_PAGEMENULAYOUT";
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppNameView.show(false);
        OCAppNameFocalValue.show(false);
        ExternalMediaStateController.getInstance().setCautionID();
        super.onResume();
        AppNameConstantView.setText((String) getResources().getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION));
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        ArrayList<String> supportedItemIds = new ArrayList<>();
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String pageId = i$.next();
            ArrayList<String> list = this.mService.getSupportedItemList(pageId);
            if (list != null && list.size() != 0) {
                supportedItemIds.add(pageId);
            }
        }
        updatePageViewItems(supportedItemIds);
        this.mPageListView.invalidateViews();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout
    public ArrayList<String> getDisplayedItemList() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int pagePos = BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_MENU_POSITION_GLOBAL_MENU_PAGE, 0);
        ArrayList<String> displayList = super.getDisplayedItemList();
        if (pagePos == 0 && Environment.getVersionOfHW() == 1 && displayList.contains(DeleteFocusMagnifierController.FOCUS_MAGNIFIER_SWITCH_IC)) {
            displayList.remove(DeleteFocusMagnifierController.FOCUS_MAGNIFIER_SWITCH_IC);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return displayList;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        AppLog.enter(TAG, AppLog.getMethodName());
        ExternalMediaStateController.getInstance().setCautionID();
        AppNameConstantView.setText((String) getResources().getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION));
        AppNameView.show(false);
        OCAppNameFocalValue.show(false);
        super.onReopened();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        ArrayList<String> supportedItemIds = new ArrayList<>();
        Iterator i$ = menuItemIds.iterator();
        while (i$.hasNext()) {
            String pageId = i$.next();
            ArrayList<String> list = this.mService.getSupportedItemList(pageId);
            if (list != null && list.size() != 0) {
                supportedItemIds.add(pageId);
            }
        }
        updatePageViewItems(supportedItemIds);
        this.mPageListView.invalidateViews();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, "ItemId = " + itemId);
        if (OCFocalLengthController.FOCAL_LENGTH.equals(itemId)) {
            CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_COMPENSATION_STEADY_FOCAL_LENGUIDE);
        } else if (ExternalMediaStateController.DELETE_EXTERNAL_PROFILE.equals(itemId)) {
            int cautionID = ExternalMediaStateController.getInstance().getCautionID();
            CautionUtilityClass.getInstance().requestTrigger(cautionID);
        } else {
            super.requestCautionTrigger(itemId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
