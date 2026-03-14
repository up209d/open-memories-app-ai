package com.sony.imaging.app.digitalfilter.menu.layout;

import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.SetMenuLayout;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFPositionLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShadingLinkController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFShootingOrderController;
import com.sony.imaging.app.digitalfilter.shooting.camera.TouchLessShutterController;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFSetMenuLayout extends SetMenuLayout implements NotificationListener {
    private static String mItemID = null;
    private String[] TAGS = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};

    @Override // com.sony.imaging.app.base.menu.layout.SetMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        mItemID = this.mService.getMenuItemId();
        if (mItemID.equalsIgnoreCase(TouchLessShutterController.TAG_TOUCHLESS_SHUTTER)) {
            if (TouchLessShutterController.getInstance().getValue(null).equals("Off")) {
                CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_TOUCHLESS_SHOOTING);
            }
            DisplayModeObserver.getInstance().setNotificationListener(this);
        }
        GFKikiLogUtil.getInstance().countMenuSettings(mItemID);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SetMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (mItemID.equalsIgnoreCase(TouchLessShutterController.TAG_TOUCHLESS_SHUTTER)) {
            DisplayModeObserver.getInstance().removeNotificationListener(this);
        }
        mItemID = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SetMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!GFCommonUtil.getInstance().isLayerSetting() && mItemID.equalsIgnoreCase(GFEEAreaController.EE)) {
            if (GFEEAreaController.getInstance().isLand()) {
                GFCommonUtil.getInstance().setCameraSettings(0, false);
                GFCommonUtil.getInstance().setBorderId(0);
            } else if (GFEEAreaController.getInstance().isSky()) {
                GFCommonUtil.getInstance().setCameraSettings(1, false);
                GFCommonUtil.getInstance().setBorderId(0);
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                GFCommonUtil.getInstance().setCameraSettings(2, false);
                GFCommonUtil.getInstance().setBorderId(1);
            }
        }
        super.closeLayout();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (itemid.equals(GFEEAreaController.LAYER3) && !GFFilterSetController.getInstance().isAvailable("Layer3Settings")) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_LAYER3_INVALID);
        }
        super.doItemClickProcessing(itemid);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        if (mItemID.equals(GFEEAreaController.EE) || mItemID.equals(GFShootingOrderController.ORDER) || mItemID.equals(GFPositionLinkController.RELATIVE_MODE) || mItemID.equals(GFShadingLinkController.RELATIVE_MODE)) {
            return -1;
        }
        return super.pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (mItemID.equals(GFEEAreaController.EE) || mItemID.equals(GFShootingOrderController.ORDER)) {
            String prevMenu = GFFilterShootingMenuLayout.getPrevMenu();
            if (prevMenu != null && prevMenu.equals("FilterSet")) {
                CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
                return 1;
            }
        } else if ((GFCommonUtil.getInstance().isLayerSetting() && mItemID.equals(GFPositionLinkController.RELATIVE_MODE)) || mItemID.equals(GFShadingLinkController.RELATIVE_MODE)) {
            openPreviousMenu();
            return 1;
        }
        return super.pushedFnKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            int code = event.getScanCode();
            if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
                int ret = super.onConvertedKeyDown(event, func);
                return ret;
            }
            if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
                return 0;
            }
            return -1;
        }
        int ret2 = super.onConvertedKeyDown(event, func);
        return ret2;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if ((GFCommonUtil.getInstance().isLayerSetting() || mItemID.equals(GFEEAreaController.EE) || mItemID.equals(GFShootingOrderController.ORDER)) && !CustomizableFunction.Unchanged.equals(func)) {
            return -1;
        }
        return super.onConvertedKeyUp(event, func);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (mItemID.equalsIgnoreCase(TouchLessShutterController.TAG_TOUCHLESS_SHUTTER)) {
            getMenuUpdater().restartMenuUpdater();
            if (!TouchLessShutterController.getInstance().isAvailable(null)) {
                super.pushedMenuKey();
            }
        }
    }
}
