package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppContext;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.sa.SFRSA;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.base.GFDisplayModeObserver;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFExposureCompensationMenuLayout extends ExposureCompensationMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFEXPOSURECOMPENSATIONMENULAYOUT";
    private static final String STR_ZERO = "0";
    private static int mLevelOfZero;
    private static final String TAG = AppLog.getClassName();
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean isHistgramView = false;
    private static TextView mTitle = null;
    private static BorderView mBorderView = null;
    private static Handler myHandler = null;
    private static Runnable myRunnable = null;
    private static boolean isLand = false;
    private static boolean isSky = false;
    private static boolean isLayer3 = false;
    private static int mSetting = 0;
    private static boolean isCanceled = false;
    private static boolean isLayerSetting = false;
    private static String mOrigBase = null;
    private static String mOrigFilter = null;
    private static String mOrigLayer3 = null;
    private static String mReloadImage = null;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange", GFConstants.UPDATED_BORDER, GFConstants.TAG_GYROSCOPE};

    @Override // com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout, com.sony.imaging.app.base.menu.layout.CompensationMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<String> support = ExposureCompensationController.getInstance().getSupportedValueInMode(1);
        if (support != null && support.size() != 0) {
            mLevelOfZero = support.indexOf("0");
        }
        ViewGroup currentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        mTitle = (TextView) currentView.findViewById(R.id.menu_screen_title);
        mGFGraphViewFilter = (GFGraphView) currentView.findViewById(R.id.filter_histgram);
        isLand = GFCommonUtil.getInstance().isLand();
        isSky = GFCommonUtil.getInstance().isSky();
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        isLayerSetting = GFCommonUtil.getInstance().isLayerSetting();
        if (!isLayerSetting) {
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
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.base_histgram);
            isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
            setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), R.string.STRID_FUNC_DF_GUIDE_DISP, android.R.string.httpErrorAuth));
        } else {
            mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.earth_histgram);
            int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
            isHistgramView = displayMode == 5;
            setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.autofill_postal_code, android.R.string.httpErrorAuth));
        }
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
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout, com.sony.imaging.app.base.menu.layout.CompensationMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        mReloadImage = null;
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        mOrigBase = params.getExposureComp(0);
        mOrigFilter = params.getExposureComp(1);
        mOrigLayer3 = params.getExposureComp(2);
        super.onResume();
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            GFCommonUtil.getInstance().setMenuTitleText(mTitle, this.mService);
            if (!BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_EXPCOMP_LINK_MSG, false)) {
                showLinkMsg();
            }
        } else {
            int id = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAND;
            if (GFEEAreaController.getInstance().isSky()) {
                id = R.string.STRID_FUNC_DF_EXPOSURECOMP_SKY;
            } else if (GFEEAreaController.getInstance().isLayer3()) {
                id = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAYER3;
            }
            mTitle.setText(id);
        }
        DisplayModeObserver.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler = null;
        }
        myRunnable = null;
        mBorderView = null;
        mGFGraphViewFilter.setVisibility(8);
        mGFGraphViewBase.setVisibility(8);
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mTitle = null;
        mOrigBase = null;
        mOrigFilter = null;
        mOrigLayer3 = null;
        mReloadImage = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.CompensationMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    private void showLinkMsg() {
        boolean isValidLink = false;
        if (GFLinkAreaController.getInstance().isExpCompLink(mSetting)) {
            if (isLand) {
                if (GFLinkAreaController.getInstance().isExpCompLink(1) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isExpCompLink(2))) {
                    isValidLink = true;
                }
            } else if (isSky) {
                if (GFLinkAreaController.getInstance().isExpCompLink(0) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isExpCompLink(2))) {
                    isValidLink = true;
                }
            } else if (GFLinkAreaController.getInstance().isExpCompLink(0) || GFLinkAreaController.getInstance().isExpCompLink(1)) {
                isValidLink = true;
            }
        }
        if (isValidLink) {
            Bundle bundle = new Bundle();
            bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_EXPCOMP");
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.CompensationMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int titleId = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAND;
        if (isSky) {
            titleId = R.string.STRID_FUNC_DF_EXPOSURECOMP_SKY;
        } else if (isLayer3) {
            titleId = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAYER3;
        }
        guideResources.add(getText(titleId));
        guideResources.add(getText(android.R.string.ext_media_unmounting_notification_message));
        guideResources.add(true);
    }

    private void startLiveveiwEffect() {
        if (!SFRSA.getInstance().isAvailableHistgram()) {
            SFRSA.getInstance().setPackageName(AppContext.getAppContext().getPackageName());
            SFRSA.getInstance().initialize();
            SFRSA.getInstance().setCommand(SFRSA.CMD_LAND_HIST);
            SFRSA.getInstance().startLiveViewEffect(false);
        }
    }

    private void toggleDispMode() {
        if (!isHistgramView) {
            isHistgramView = true;
            mGFGraphViewBase.setVisibility(0);
            if (GFCommonUtil.getInstance().isLayerSetting()) {
                mGFGraphViewFilter.setVisibility(0);
            }
        } else {
            isHistgramView = false;
            mGFGraphViewFilter.setVisibility(8);
            mGFGraphViewBase.setVisibility(8);
        }
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.ExposureCompensationMenuLayout, com.sony.imaging.app.base.menu.layout.CompensationMenuLayout
    public void setCompensationLevel(int level) {
        String expCompLevel = Integer.toString(level - mLevelOfZero);
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        params.setExposureComp(mSetting, expCompLevel);
        super.setCompensationLevel(level);
        if (!isCanceled) {
            mReloadImage = GFLinkUtil.getInstance().saveLinkedExpComp(expCompLevel, mSetting, isLand, isSky, isLayer3);
            return;
        }
        params.setExposureComp(0, mOrigBase);
        params.setExposureComp(1, mOrigFilter);
        params.setExposureComp(2, mOrigLayer3);
    }

    @Override // com.sony.imaging.app.base.menu.layout.CompensationMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            AppLog.info(TAG, "ExpComp: Filter Setting");
            if (mReloadImage != null) {
                CameraNotificationManager.getInstance().requestNotify(mReloadImage);
            }
            openPreviousMenu();
            return 1;
        }
        AppLog.info(TAG, "ExpComp: Base Setting");
        super.pushedCenterKey();
        return 1;
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

    @Override // com.sony.imaging.app.base.menu.layout.CompensationMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        mReloadImage = null;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int ret;
        if (CustomizableFunction.DispChange.equals(func)) {
            toggleDispMode();
            return 1;
        }
        if (GFCommonUtil.getInstance().isLayerSetting()) {
            if (CustomizableFunction.Unchanged.equals(func) || CustomizableFunction.Guide.equals(func) || CustomizableFunction.MainPrev.equals(func) || CustomizableFunction.MainNext.equals(func) || CustomizableFunction.SubPrev.equals(func) || CustomizableFunction.SubNext.equals(func) || CustomizableFunction.ThirdPrev.equals(func) || CustomizableFunction.ThirdNext.equals(func) || CustomizableFunction.MfAssist.equals(func)) {
                ret = super.onConvertedKeyDown(event, func);
            } else {
                ret = -1;
            }
        } else {
            ret = super.onConvertedKeyDown(event, func);
        }
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (!isLayerSetting || CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
                if (!isFunctionGuideShown() && isLayerSetting) {
                    toggleDispMode();
                }
                return 1;
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
