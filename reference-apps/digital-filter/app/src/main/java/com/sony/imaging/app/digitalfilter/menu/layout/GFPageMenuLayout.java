package com.sony.imaging.app.digitalfilter.menu.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.layout.GFS1OffLayout;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class GFPageMenuLayout extends PageMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFPAGEMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private String[] TAGS = {GFConstants.COPY_THEME_SETTINGS, GFConstants.RESET_COLOR_SETTING, DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        GFCommonUtil.getInstance().endLayerSetting();
        GFS1OffLayout.mPrevS1OffDispMode = -1;
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
        CameraNotificationManager.getInstance().setNotificationListener(this);
        DisplayModeObserver.getInstance().setNotificationListener(this);
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
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        if (getLayout("ID_GFINTRODUCTIONLAYOUT").getView() != null) {
            getLayout("ID_GFINTRODUCTIONLAYOUT").closeLayout();
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout
    public ArrayList<String> getDisplayedItemList() {
        ArrayList<String> list = super.getDisplayedItemList();
        if (ISOSensitivityController.getInstance().getSupportedValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX) == null) {
            list.remove("IsoAutoMaxMin");
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (itemid.equals(GFSettingMenuLayout.LAND_ID) || itemid.equals("SkySettings") || itemid.equals("Layer3Settings")) {
            if (GFSettingMenuLayout.getCurrentArea().equals(GFSettingMenuLayout.LAND_ID)) {
                itemid = GFSettingMenuLayout.LAND_ID;
            } else if (GFSettingMenuLayout.getCurrentArea().equals("SkySettings")) {
                itemid = "SkySettings";
            } else if (GFSettingMenuLayout.getCurrentArea().equals("Layer3Settings")) {
                itemid = "Layer3Settings";
            }
            if (GFSettingMenuLayout.getCurrentArea().equals(GFSettingMenuLayout.LAND_ID)) {
                if (!GFEEAreaController.getInstance().isSky()) {
                    GFCommonUtil.getInstance().setFilterCameraSettings(true);
                }
            } else if (GFSettingMenuLayout.getCurrentArea().equals("SkySettings")) {
                if (!GFEEAreaController.getInstance().isLand()) {
                    GFCommonUtil.getInstance().setBaseCameraSettings(true);
                }
            } else if (GFSettingMenuLayout.getCurrentArea().equals("Layer3Settings") && !GFEEAreaController.getInstance().isLand()) {
                GFCommonUtil.getInstance().setBaseCameraSettings(true);
            }
        }
        if (itemid.equals("ApplicationGuide")) {
            openLayout("ID_GFINTRODUCTIONLAYOUT");
            return;
        }
        if (itemid.equals("ResetParameters")) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_RESET_PARAM);
        } else if (itemid.equals("ImportParameters")) {
            openNextMenu(itemid, "ID_GFIMPORTMENULAYOUT");
        } else {
            super.doItemClickProcessing(itemid);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void onDeviceStatusChanged() {
        getMenuUpdater().restartMenuUpdater();
        super.onDeviceStatusChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        if (itemId.equals("ResetColorSetting")) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_RESET_COLOR_PARAM);
        } else {
            if (itemId.equals("Layer3Settings")) {
                if (!GFFilterSetController.getInstance().isAvailable("Layer3Settings")) {
                    CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_LAYER3_INVALID);
                    return;
                }
                return;
            }
            super.requestCautionTrigger(itemId);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        getMenuUpdater().restartMenuUpdater();
        if (tag.equals(GFConstants.COPY_THEME_SETTINGS) || tag.equals(GFConstants.RESET_COLOR_SETTING)) {
            GFCommonUtil.getInstance().setCommonCameraSettings();
            GFCommonUtil.getInstance().setEECameraSettings();
        }
    }
}
