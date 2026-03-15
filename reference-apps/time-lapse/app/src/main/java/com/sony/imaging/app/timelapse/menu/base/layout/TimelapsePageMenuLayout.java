package com.sony.imaging.app.timelapse.menu.base.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.SilentShutterController;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class TimelapsePageMenuLayout extends PageMenuLayout {
    private static final String TAG = TimelapsePageMenuLayout.class.getSimpleName();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return PageMenuLayout.MENU_ID;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppNameView.show(false);
        super.onResume();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        ArrayList<String> supportedItemIds = new ArrayList<>();
        menuItemIds.remove("AngleShiftAddOn");
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
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        ArrayList<String> menuItemIds = this.mService.getMenuItemList();
        ArrayList<String> supportedItemIds = new ArrayList<>();
        menuItemIds.remove("AngleShiftAddOn");
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
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        updatePreferences();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    private void updatePreferences() {
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 1) {
            TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
            TimeLapseConstants.TIME_LAPSE_PICTURE_SIZE = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_IMAGE_SIZE);
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE_KEY, TimeLapseConstants.TIME_LAPSE_PICTURE_ASPECT_SIZE);
            BackUpUtil.getInstance().setPreference(TimeLapseConstants.TIME_LAPSE_PICTURE_SIZE_KEY, TimeLapseConstants.TIME_LAPSE_PICTURE_SIZE);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout
    public void updateListViewItems(ArrayList<String> itemIds) {
        if (TLCommonUtil.getInstance().isTimelapseLiteApplication() && itemIds.contains(SelfTimerIntervalPriorityController.INTERVAL_PRIORITY)) {
            itemIds.remove(SelfTimerIntervalPriorityController.INTERVAL_PRIORITY);
        }
        super.updateListViewItems(itemIds);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        int[] cautionId = this.mService.getMenuItemCautionId(itemId);
        if (cautionId == null || cautionId[0] == 0) {
            displayCaution(itemId);
        } else if (TimeLapseConstants.SELECTED_MENU_ITEM_THEME.contains(itemId)) {
            displayCaution(itemId);
        } else if (itemId.equalsIgnoreCase(DigitalZoomController.DIGITAL_ZOOM) && TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
            displayCaution(itemId);
        } else {
            super.requestCautionTrigger(itemId);
        }
        AppLog.info(TAG, "requestCautionTrigger  itemId  " + itemId);
    }

    private void displayCaution(String itemId) {
        int cautionID = TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME;
        if (itemId.equals("PictureEffect") && (TLCommonUtil.getInstance().getCurrentState() == 7 || TLCommonUtil.getInstance().getCurrentState() == 5)) {
            cautionID = 1 == Environment.getVersionOfHW() ? 131074 : 33570;
        } else if (itemId.equals(CreativeStyleController.CREATIVESTYLE) && TLCommonUtil.getInstance().getCurrentState() == 7) {
            cautionID = 1 == Environment.getVersionOfHW() ? 131074 : 33546;
        } else if (itemId.equals(SilentShutterController.TAG_SILENT_SHUTTER)) {
            if (TLCommonUtil.getInstance().getCurrentState() == 5) {
                cautionID = TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME;
            } else {
                cautionID = 34071;
            }
        } else if (TimeLapseConstants.SELECTED_MENU_ITEM_THEME.contains(itemId)) {
            cautionID = TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME;
        } else if (TimeLapseConstants.SELECTED_MENU_ITEM_FILE_FORMAT.contains(itemId)) {
            cautionID = TimelapseInfo.CAUTION_ID_DLAPP_INVALID_MOVIE_MODE;
        }
        CautionUtilityClass.getInstance().requestTrigger(cautionID);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        System.gc();
        if (itemid.equals(TLShootModeSettingController.APPLICATION_SETTINGS)) {
            if (this.mService != null) {
                doApplicationSettingsProcessing(itemid);
            }
        } else {
            if (itemid.equals("ListViewForAngleShiftAddOn")) {
                if (TLCommonUtil.getInstance().hasAngleShiftAddon()) {
                    if (!MediaNotificationManager.getInstance().isMounted()) {
                        CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_NO_MEMEORY_CARD_SHOOTING);
                        return;
                    } else {
                        CameraNotificationManager.getInstance().requestNotify("ListViewForAngleShiftAddOn");
                        return;
                    }
                }
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_AS_ADDON_MSG);
                return;
            }
            super.doItemClickProcessing(itemid);
        }
    }

    private void doApplicationSettingsProcessing(String itemid) {
        String nextFragmentID;
        if (TimeLapseConstants.EXTENSTION_CUSTOM_THEME_SETTING && TLCommonUtil.getInstance().getCurrentState() == 7) {
            nextFragmentID = "ID_TLCUSTOMTHEMEOPTIONLAYOUT";
        } else {
            nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
        }
        if (nextFragmentID != null) {
            openNextMenu(itemid, nextFragmentID);
        } else {
            AppLog.error(TAG, "Can't open the next MenuLayout: " + nextFragmentID);
        }
    }
}
