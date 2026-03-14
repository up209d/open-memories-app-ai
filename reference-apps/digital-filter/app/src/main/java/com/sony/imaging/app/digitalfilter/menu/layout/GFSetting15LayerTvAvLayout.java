package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.widget.ApertureView;
import com.sony.imaging.app.base.shooting.widget.ShutterSpeedView;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFSetting15LayerTvAvLayout extends Fn15LayerAbsLayout {
    public static final String MENU_ID = "ID_GFSETTING15LAYERTVAVLAYOUT";
    NotificationListener mCameraListener = new ChangeCameraNotifier();
    private static final String TAG = AppLog.getClassName();
    private static boolean isCanceled = false;
    private static int mSetting = 0;
    private static boolean isAperture = false;
    private static boolean isAvSetting = false;
    private static ApertureView mApertureView = null;
    private static ShutterSpeedView mShutterSpeedView = null;
    private static ImageView mRightArrowFno = null;
    private static ImageView mLeftArrowFno = null;
    private static ImageView mRightArrowSS = null;
    private static ImageView mLeftArrowSS = null;
    private static boolean isLand = false;
    private static boolean isSky = false;
    private static boolean isLayer3 = false;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean isHistgramView = false;
    private static Handler mHandler = null;
    private static Runnable mRunnable = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.menu_setting15_tvav);
        mApertureView = (ApertureView) currentView.findViewById(R.id.aperture_view);
        mShutterSpeedView = (ShutterSpeedView) currentView.findViewById(R.id.shutterspeed_view);
        mRightArrowFno = (ImageView) currentView.findViewById(R.id.right_arrow_aperture);
        mLeftArrowFno = (ImageView) currentView.findViewById(R.id.left_arrow_aperture);
        mRightArrowSS = (ImageView) currentView.findViewById(R.id.right_arrow_ss);
        mLeftArrowSS = (ImageView) currentView.findViewById(R.id.left_arrow_ss);
        mGFGraphViewFilter = (GFGraphView) currentView.findViewById(R.id.filter_histgram);
        mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.base_histgram);
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (!isHistgramView) {
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        GFHistgramTask.getInstance().startHistgramUpdating();
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        isLand = GFCommonUtil.getInstance().isLand();
        isSky = GFCommonUtil.getInstance().isSky();
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        String key = this.data.getString(GFConstants.BUNDLE_SETTING15_KEY);
        isAperture = key.equals("aperture");
        isAvSetting = isAperture;
        if (isAperture) {
            mApertureView.setVisibility(0);
            mRightArrowFno.setVisibility(0);
            mLeftArrowFno.setVisibility(0);
            mShutterSpeedView.setVisibility(4);
            mRightArrowSS.setVisibility(4);
            mLeftArrowSS.setVisibility(4);
            updateArrowsAperture();
        } else {
            mApertureView.setVisibility(4);
            mRightArrowFno.setVisibility(4);
            mLeftArrowFno.setVisibility(4);
            mShutterSpeedView.setVisibility(0);
            mRightArrowSS.setVisibility(0);
            mLeftArrowSS.setVisibility(0);
            updateArrowsShutterSpeed();
        }
        disappearInfo();
        CameraNotificationManager.getInstance().setNotificationListener(this.mCameraListener);
        super.onResume();
        if (isAperture && !BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_FNO_LINK_MSG, false)) {
            showLinkMsg();
        }
        if (!isAperture && !BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_SS_LINK_MSG, false)) {
            showLinkMsg();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this.mCameraListener);
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
        mRunnable = null;
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        GFHistgramTask.getInstance().stopHistgramUpdating();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (isCanceled) {
            if (isAperture) {
                if (GFCommonUtil.getInstance().hasIrisRing() || !GFCommonUtil.getInstance().isFixedAperture()) {
                    if (GFCommonUtil.getInstance().isMaxMinAperture()) {
                        GFCommonUtil.getInstance().setApertureRetry(mSetting);
                    } else {
                        GFCommonUtil.getInstance().setAperture(mSetting, false);
                    }
                } else {
                    return;
                }
            } else {
                GFCommonUtil.getInstance().setShutterSpeed(mSetting);
            }
        } else if (isAperture) {
            String reloadImage = GFLinkUtil.getInstance().saveLinkedAperture(mSetting, isLand, isSky, isLayer3);
            if (reloadImage != null) {
                CameraNotificationManager.getInstance().requestNotify(reloadImage);
            }
        } else {
            String reloadImage2 = GFLinkUtil.getInstance().saveLinkedSS(mSetting, isLand, isSky, isLayer3);
            if (reloadImage2 != null) {
                CameraNotificationManager.getInstance().requestNotify(reloadImage2);
            }
        }
        isAvSetting = false;
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mApertureView = null;
        mShutterSpeedView = null;
        mRightArrowFno = null;
        mLeftArrowFno = null;
        mRightArrowSS = null;
        mLeftArrowSS = null;
        super.onDestroyView();
        System.gc();
    }

    public static boolean isAvSetting() {
        return isAvSetting;
    }

    private void showLinkMsg() {
        boolean isValidLink = false;
        if (isAperture) {
            if (GFLinkAreaController.getInstance().isApertureLink(mSetting)) {
                if (isLand) {
                    if (GFLinkAreaController.getInstance().isApertureLink(1) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isApertureLink(2))) {
                        isValidLink = true;
                    }
                } else if (isSky) {
                    if (GFLinkAreaController.getInstance().isApertureLink(0) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isApertureLink(2))) {
                        isValidLink = true;
                    }
                } else if (GFLinkAreaController.getInstance().isApertureLink(0) || GFLinkAreaController.getInstance().isApertureLink(1)) {
                    isValidLink = true;
                }
            }
        } else if (GFLinkAreaController.getInstance().isSSLink(mSetting)) {
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
            if (isAperture) {
                if (GFCommonUtil.getInstance().isRX10() || GFCommonUtil.getInstance().isRX100()) {
                    bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_F5_6");
                } else {
                    bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_F8");
                }
            } else {
                bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_SS");
            }
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int guideTitleID = R.string.STRID_FUNC_DF_APERTURE_LAND;
        if (isAperture) {
            if (isLayer3) {
                guideTitleID = R.string.STRID_FUNC_SKYND_APERTURE_LAYER3;
            } else if (isSky) {
                guideTitleID = R.string.STRID_FUNC_DF_APERTURE_SKY;
            }
        } else if (isLayer3) {
            guideTitleID = R.string.STRID_FUNC_SKYND_SS_LAYER3;
        } else if (isSky) {
            guideTitleID = R.string.STRID_FUNC_DF_SS_SKY;
        } else {
            guideTitleID = R.string.STRID_FUNC_DF_SS_LAND;
        }
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(R.string.STRID_FUNC_SKYND_APERTURE_GUIDE));
        guideResources.add(true);
    }

    private void incrementTvAv() {
        if (isAperture) {
            mApertureView.setVisibility(0);
            updateArrowsAperture();
            if (!GFCommonUtil.getInstance().isMaxAperture()) {
                CameraSetting.getInstance().incrementAperture();
            }
        } else {
            mShutterSpeedView.setVisibility(0);
            updateArrowsShutterSpeed();
            CameraSetting.getInstance().incrementShutterSpeed();
        }
        disappearInfo();
    }

    private void decrementTvAv() {
        if (isAperture) {
            mApertureView.setVisibility(0);
            updateArrowsAperture();
            if (!GFCommonUtil.getInstance().isMinAperture()) {
                CameraSetting.getInstance().decrementAperture();
            }
        } else {
            mShutterSpeedView.setVisibility(0);
            updateArrowsShutterSpeed();
            CameraSetting.getInstance().decrementShutterSpeed();
        }
        disappearInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateArrowsAperture() {
        if (mApertureView.getVisibility() != 4) {
            CameraEx.ApertureInfo info = CameraSetting.getInstance().getApertureInfo();
            int max = info.currentAvailableMax;
            int min = info.currentAvailableMin;
            int val = info.currentAperture;
            if (val == max) {
                mRightArrowFno.setVisibility(4);
                mLeftArrowFno.setVisibility(0);
            } else if (val == min) {
                mRightArrowFno.setVisibility(0);
                mLeftArrowFno.setVisibility(4);
            } else {
                mRightArrowFno.setVisibility(0);
                mLeftArrowFno.setVisibility(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateArrowsShutterSpeed() {
        CameraEx.ShutterSpeedInfo info = CameraSetting.getInstance().getShutterSpeedInfo();
        int max_d = info.currentAvailableMax_d;
        int max_n = info.currentAvailableMax_n;
        int min_d = info.currentAvailableMin_d;
        int min_n = info.currentAvailableMin_n;
        int d = info.currentShutterSpeed_d;
        int n = info.currentShutterSpeed_n;
        if (d == max_d && n == max_n) {
            mRightArrowSS.setVisibility(4);
            mLeftArrowSS.setVisibility(0);
        } else if (d == min_d && n == min_n) {
            mRightArrowSS.setVisibility(0);
            mLeftArrowSS.setVisibility(4);
        } else if (n != 65535) {
            mRightArrowSS.setVisibility(0);
            mLeftArrowSS.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void disappearInfo() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mRunnable);
        }
        if (mRunnable == null) {
            mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerTvAvLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    GFSetting15LayerTvAvLayout.setInfoVisibility(4);
                }
            };
        }
        mHandler.postDelayed(mRunnable, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setInfoVisibility(int visibility) {
        mApertureView.setVisibility(visibility);
        mShutterSpeedView.setVisibility(visibility);
        mRightArrowFno.setVisibility(visibility);
        mLeftArrowFno.setVisibility(visibility);
        mRightArrowSS.setVisibility(visibility);
        mLeftArrowSS.setVisibility(visibility);
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        incrementTvAv();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        decrementTvAv();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        incrementTvAv();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        decrementTvAv();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        incrementTvAv();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        decrementTvAv();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return 0;
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
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
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                closeMenuLayout(new Bundle());
                return 0;
            default:
                int ret = super.onKeyDown(keyCode, event);
                return ret;
        }
    }

    /* loaded from: classes.dex */
    static class ChangeCameraNotifier implements NotificationListener {
        ChangeCameraNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return GFSetting15LayerTvAvLayout.isAperture ? new String[]{"Aperture"} : new String[]{CameraNotificationManager.SHUTTER_SPEED};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if ("Aperture".equals(tag)) {
                GFSetting15LayerTvAvLayout.updateArrowsAperture();
            } else if (CameraNotificationManager.SHUTTER_SPEED.equals(tag)) {
                GFSetting15LayerTvAvLayout.updateArrowsShutterSpeed();
            }
            GFSetting15LayerTvAvLayout.disappearInfo();
        }
    }
}
