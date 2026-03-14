package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.caution.GFInfo;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFTvMenuLayout extends DisplayMenuItemsMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFTVMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private static View mCurrentView = null;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean userSetting = false;
    private static boolean isHistgramView = false;
    private static boolean isCancelSetting = false;
    private static TextView mTitle = null;
    private static BorderView mBorderView = null;
    private static boolean isLand = false;
    private static boolean isSky = false;
    private static boolean isLayer3 = false;
    private static int mSetting = 0;
    private static ImageView mRightArrow = null;
    private static ImageView mLeftArrow = null;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};
    GFEffectParameters.Parameters mParams = null;
    NotificationListener mCameraListener = new ChangeCameraNotifier();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isLand = GFCommonUtil.getInstance().isLand();
        isSky = GFCommonUtil.getInstance().isSky();
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_tv);
        mGFGraphViewFilter = (GFGraphView) mCurrentView.findViewById(R.id.filter_histgram);
        mGFGraphViewBase = (GFGraphView) mCurrentView.findViewById(R.id.base_histgram);
        mTitle = (TextView) mCurrentView.findViewById(R.id.menu_screen_title);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        mRightArrow = (ImageView) mCurrentView.findViewById(R.id.right_arrow);
        mLeftArrow = (ImageView) mCurrentView.findViewById(R.id.left_arrow);
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (!isHistgramView) {
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        userSetting = false;
        isCancelSetting = false;
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        GFCommonUtil.getInstance().setMenuTitleText(mTitle, this.mService);
        CameraNotificationManager.getInstance().setNotificationListener(this.mCameraListener);
        DisplayModeObserver.getInstance().setNotificationListener(this);
        this.mParams = GFEffectParameters.getInstance().getParameters();
        GFHistgramTask.getInstance().startHistgramUpdating();
        updateArrows();
        if (!BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_SS_LINK_MSG, false)) {
            showLinkMsg();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this.mCameraListener);
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        GFHistgramTask.getInstance().setEnableUpdating(false);
        GFHistgramTask.getInstance().stopHistgramUpdating();
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mTitle = null;
        mBorderView = null;
        mRightArrow = null;
        mLeftArrow = null;
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mCurrentView = null;
        super.onDestroyView();
        System.gc();
    }

    private void showLinkMsg() {
        boolean isValidLink = false;
        if (GFLinkAreaController.getInstance().isSSLink(mSetting)) {
            if (isLand) {
                if (GFLinkAreaController.getInstance().isSSLink(1) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isSSLink(2))) {
                    isValidLink = true;
                }
            } else if (isSky) {
                if (GFLinkAreaController.getInstance().isSSLink(0) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isSSLink(2))) {
                    isValidLink = true;
                }
            } else if (GFLinkAreaController.getInstance().isSSLink(0) || GFLinkAreaController.getInstance().isSSLink(1)) {
                isValidLink = true;
            }
        }
        if (isValidLink) {
            Bundle bundle = new Bundle();
            bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_SS");
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateArrows() {
        CameraEx.ShutterSpeedInfo info = CameraSetting.getInstance().getShutterSpeedInfo();
        int max_d = info.currentAvailableMax_d;
        int max_n = info.currentAvailableMax_n;
        int min_d = info.currentAvailableMin_d;
        int min_n = info.currentAvailableMin_n;
        int d = info.currentShutterSpeed_d;
        int n = info.currentShutterSpeed_n;
        if (d == max_d && n == max_n) {
            mRightArrow.setVisibility(4);
            mLeftArrow.setVisibility(0);
        } else if (d == min_d && n == min_n) {
            mRightArrow.setVisibility(0);
            mLeftArrow.setVisibility(4);
        } else {
            mRightArrow.setVisibility(0);
            mLeftArrow.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (isCancelSetting) {
            cancelSettings();
        } else {
            saveValues();
        }
        super.closeLayout();
    }

    private void cancelSettings() {
        if (userSetting) {
            GFCommonUtil.getInstance().setShutterSpeed(mSetting);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int guideTitleID = R.string.STRID_FUNC_DF_SS_LAND;
        if (isLayer3) {
            guideTitleID = R.string.STRID_FUNC_SKYND_SS_LAYER3;
        } else if (isSky) {
            guideTitleID = R.string.STRID_FUNC_DF_SS_SKY;
        }
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(R.string.STRID_FUNC_SKYND_SS_GUIDE));
        guideResources.add(true);
    }

    private void saveValues() {
        if (userSetting) {
            String reloadImage = GFLinkUtil.getInstance().saveLinkedSS(mSetting, isLand, isSky, isLayer3);
            if (reloadImage != null) {
                if (isLand) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_SKY_IMAGE);
                } else if (isSky) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_LAND_IMAGE);
                } else if (isLayer3) {
                    CameraNotificationManager.getInstance().requestNotify(GFConstants.RELOAD_ALL_IMAGE);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCancelSetting = true;
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (GFConstants.SettingLayerCustomizableFunction.contains(func)) {
            int ret = super.onConvertedKeyDown(event, func);
            return ret;
        }
        if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
            return 0;
        }
        if (CustomizableFunction.DispChange.equals(func)) {
            toggleDispMode();
            return 1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
                if (!isFunctionGuideShown()) {
                    toggleDispMode();
                }
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                decrementedSS();
                return 1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                incrementedSS();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (!isFunctionGuideShown()) {
                    openPreviousMenu();
                    return 1;
                }
                int ret = super.onKeyDown(keyCode, event);
                return ret;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case 645:
                return -1;
            default:
                int ret2 = super.onKeyDown(keyCode, event);
                return ret2;
        }
    }

    private int incrementedSS() {
        userSetting = true;
        CameraSetting.getInstance().incrementShutterSpeed();
        return 1;
    }

    private int decrementedSS() {
        userSetting = true;
        CameraSetting.getInstance().decrementShutterSpeed();
        return 1;
    }

    private void toggleDispMode() {
        if (!isHistgramView) {
            isHistgramView = true;
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        } else {
            isHistgramView = false;
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        }
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
    }

    /* loaded from: classes.dex */
    static class ChangeCameraNotifier implements NotificationListener {
        private static final String[] tags = {CameraNotificationManager.SHUTTER_SPEED, GFConstants.TAG_GYROSCOPE};

        ChangeCameraNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals(CameraNotificationManager.SHUTTER_SPEED)) {
                GFTvMenuLayout.updateArrows();
            } else if (tag.equals(GFConstants.TAG_GYROSCOPE)) {
                GFTvMenuLayout.mBorderView.invalidate();
            }
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        mBorderView.invalidate();
    }
}
