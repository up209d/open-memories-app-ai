package com.sony.imaging.app.graduatedfilter.menu.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.shooting.layout.GFS1OffLayout;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFPageMenuLayout extends PageMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFPAGEMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private String[] TAGS = {GFConstants.RESET_COLOR_SETTING};

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        GFS1OffLayout.mPrevS1OffDispMode = -1;
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
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
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (itemid.equals("ApplicationGuide")) {
            openLayout("ID_GFINTRODUCTIONLAYOUT");
        } else if (itemid.equals("ResetParameters")) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_RESET_PARAM);
        } else {
            super.doItemClickProcessing(itemid);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        if (itemId.equals("ResetColorSetting")) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_RESET_COLOR_PARAM);
        } else {
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
        GFCommonUtil.getInstance().needCTempSetting();
        GFCommonUtil.getInstance().setCommonCameraSettings();
        GFCommonUtil.getInstance().setBaseCameraSettings();
    }
}
