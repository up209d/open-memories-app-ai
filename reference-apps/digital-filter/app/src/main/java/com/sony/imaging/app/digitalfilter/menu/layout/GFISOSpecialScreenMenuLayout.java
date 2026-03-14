package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.base.menu.layout.ISOSpecialScreenMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.common.SaUtil;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.sa.SFRSA;
import com.sony.imaging.app.digitalfilter.shooting.base.GFDisplayModeObserver;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFISOSpecialScreenMenuLayout extends ISOSpecialScreenMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFISOSPECIALSCREENMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static FooterGuide mFooterGuide = null;
    private static BorderView mBorderView = null;
    private static boolean isHistgramView = false;
    private static TextView mTitle = null;
    private static boolean isLand = false;
    private static boolean isSky = false;
    private static boolean isLayer3 = false;
    private static int mSetting = 0;
    private static boolean isCanceled = false;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange", GFConstants.UPDATED_BORDER, GFConstants.TAG_GYROSCOPE};

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup currentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        isLand = GFCommonUtil.getInstance().isLand();
        isSky = GFCommonUtil.getInstance().isSky();
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        if (!GFCommonUtil.getInstance().isLayerSetting()) {
            if (GFEEAreaController.getInstance().isLand()) {
                isLand = true;
                isSky = false;
                isLayer3 = false;
                mSetting = 0;
            } else if (GFEEAreaController.getInstance().isSky()) {
                isLand = false;
                isSky = true;
                isLayer3 = false;
                mSetting = 1;
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                isLand = false;
                isSky = false;
                isLayer3 = true;
                mSetting = 2;
            }
        }
        mTitle = (TextView) currentView.findViewById(R.id.menu_screen_title);
        mFooterGuide = (FooterGuide) currentView.findViewById(R.id.footer_guide);
        mGFGraphViewFilter = (GFGraphView) currentView.findViewById(R.id.filter_histgram);
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.base_histgram);
        } else {
            mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.earth_histgram);
        }
        int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
        isHistgramView = displayMode == 5;
        if (!GFCommonUtil.getInstance().isLayerSetting()) {
            startLiveveiwEffect();
            CameraNotificationManager.getInstance().requestNotify(GFConstants.START_BASESETTING);
        }
        GFHistgramTask.getInstance().startHistgramUpdating();
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        if (!isHistgramView) {
            mGFGraphViewFilter.setVisibility(8);
            mGFGraphViewBase.setVisibility(8);
        } else if (!GFCommonUtil.getInstance().isLayerSetting()) {
            mGFGraphViewFilter.setVisibility(8);
            mGFGraphViewBase.setVisibility(0);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        IFooterGuideData data = new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.autofill_phone_suffix_separator_re, android.R.string.hardware);
        mFooterGuide.setData(data);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        super.onResume();
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            GFCommonUtil.getInstance().setMenuTitleText(mTitle, this.mService);
        } else {
            int id = R.string.STRID_FUNC_DF_ISO_LAND;
            if (GFEEAreaController.getInstance().isSky()) {
                id = R.string.STRID_FUNC_DF_ISO_SKY;
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                id = R.string.STRID_FUNC_DF_ISO_3RD_AREA;
            }
            mTitle.setText(id);
        }
        DisplayModeObserver.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        mBorderView = null;
        mGFGraphViewFilter.setVisibility(8);
        mGFGraphViewBase.setVisibility(8);
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mFooterGuide = null;
        mTitle = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    private void startLiveveiwEffect() {
        if (!SFRSA.getInstance().isAvailableHistgram()) {
            SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
            SFRSA.getInstance().initialize();
            SFRSA.getInstance().setCommand(SFRSA.CMD_LAND_HIST);
            SFRSA.getInstance().startLiveViewEffect(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        if (SaUtil.isAVIP() && itemid.equalsIgnoreCase("Iso_0") && ExposureModeController.MANUAL_MODE.equalsIgnoreCase(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE))) {
            CautionUtilityClass.getInstance().requestTrigger(1470);
        } else if (GFCommonUtil.getInstance().isLayerSetting()) {
            AppLog.info(TAG, "ISO: Filter Setting");
            openPreviousMenu();
        } else {
            AppLog.info(TAG, "ISO: Base Setting");
            super.doItemClickProcessing(itemid);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!isCanceled) {
            GFLinkUtil.getInstance().saveLinkedISO(mSetting, isLand, isSky, isLayer3);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (!GFCommonUtil.getInstance().isLayerSetting()) {
            return super.pushedFnKey();
        }
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            if (CustomizableFunction.Unchanged.equals(func) || CustomizableFunction.Guide.equals(func) || CustomizableFunction.MainPrev.equals(func) || CustomizableFunction.MainNext.equals(func) || CustomizableFunction.SubPrev.equals(func) || CustomizableFunction.SubNext.equals(func) || CustomizableFunction.ThirdPrev.equals(func) || CustomizableFunction.ThirdNext.equals(func) || CustomizableFunction.MfAssist.equals(func)) {
                int ret = super.onConvertedKeyDown(event, func);
                return ret;
            }
            return -1;
        }
        int ret2 = super.onConvertedKeyDown(event, func);
        return ret2;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (!GFCommonUtil.getInstance().isLayerSetting() || CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                return -1;
            default:
                int result = super.onKeyDown(keyCode, event);
                return result;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 232 || code == 595) {
            return 1;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (GFConstants.UPDATED_BORDER.equals(tag)) {
            SFRSA.getInstance().updateLiveViewEffect();
        }
        mBorderView.invalidate();
    }
}
