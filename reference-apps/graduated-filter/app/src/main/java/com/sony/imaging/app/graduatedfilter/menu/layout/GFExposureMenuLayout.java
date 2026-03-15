package com.sony.imaging.app.graduatedfilter.menu.layout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.fw.CustomKeyMgr;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.SaUtil;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeAperture;
import com.sony.imaging.app.graduatedfilter.shooting.ChangeSs;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.camera.GFExposureModeController;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.graduatedfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFExposureMenuLayout extends DisplayMenuItemsMenuLayout implements NotificationListener {
    public static final String MENU_ID = "ID_GFEXPOSUREMENULAYOUT";
    private static final String TAG = AppLog.getClassName();
    private static View mCurrentView = null;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static DigitView mApertureView = null;
    private static DigitView mShutterSpeedView = null;
    private static DigitView mExposureCompView = null;
    private static boolean userSettingAperture = false;
    private static boolean userSettingShutterSpeed = false;
    private static boolean userSettingExposureComp = false;
    private static boolean isHistgramView = false;
    private static boolean isCancelSetting = false;
    private static ImageView mDialTvAv = null;
    private static ImageView mFuncTvAv = null;
    private static ImageView mLeftButton = null;
    private static ImageView mLeftButtonEv = null;
    private static ImageView mRightButton = null;
    private static ImageView mRightButtonIso = null;
    private static ImageView mDeleteButton = null;
    private static TextView mDeleteButtonDisp = null;
    private static RelativeLayout mFilterGuide = null;
    private static BorderView mBorderView = null;
    private static Handler myHandler = null;
    private static Runnable myRunnable = null;
    private static Runnable myRunnableGuide = null;
    private static boolean isTvSetting = true;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange"};
    GFEffectParameters.Parameters mParams = null;
    NotificationListener mCameraListener = new ChangeCameraNotifier();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        ExposureModeController cntl = ExposureModeController.getInstance();
        String expMode = cntl.getValue(null);
        if (ExposureModeController.PROGRAM_AUTO_MODE.equals(expMode) || ExposureModeController.MOVIE_PROGRAM_AUTO_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_P;
        }
        if ("Aperture".equals(expMode) || ExposureModeController.MOVIE_APERATURE_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_A;
        }
        if ("Shutter".equals(expMode) || ExposureModeController.MOVIE_SHUTTER_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_S;
        }
        if (ExposureModeController.MANUAL_MODE.equals(expMode) || ExposureModeController.MOVIE_MANUAL_MODE.equals(expMode)) {
            return ICustomKey.CATEGORY_SHOOTING_M;
        }
        return ICustomKey.CATEGORY_SHOOTING_OTHER;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_exposure);
        mGFGraphViewFilter = (GFGraphView) mCurrentView.findViewById(R.id.filter_histgram);
        mGFGraphViewBase = (GFGraphView) mCurrentView.findViewById(R.id.base_histgram);
        mApertureView = (DigitView) mCurrentView.findViewById(R.id.aperture_view);
        mShutterSpeedView = (DigitView) mCurrentView.findViewById(R.id.shutterspeed_view);
        mExposureCompView = (DigitView) mCurrentView.findViewById(R.id.exposure_and_meteredmanual_view);
        mDialTvAv = (ImageView) mCurrentView.findViewById(R.id.dial_tvav);
        mFuncTvAv = (ImageView) mCurrentView.findViewById(R.id.func_tvav);
        mFuncTvAv.setImageResource(getTvAvResId());
        mLeftButton = (ImageView) mCurrentView.findViewById(R.id.left_button);
        mLeftButtonEv = (ImageView) mCurrentView.findViewById(R.id.left_button_ev);
        if (ExposureModeController.MANUAL_MODE.equalsIgnoreCase(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE)) && !ISOSensitivityController.ISO_AUTO.equalsIgnoreCase(ISOSensitivityController.getInstance().getValue())) {
            mLeftButtonEv.setImageResource(R.drawable.p_16_dd_parts_hgf_parts_assigned_func_ev_disable);
        } else {
            mLeftButtonEv.setImageResource(R.drawable.p_16_dd_parts_hgf_parts_assigned_func_ev);
        }
        mRightButton = (ImageView) mCurrentView.findViewById(R.id.right_button);
        mRightButtonIso = (ImageView) mCurrentView.findViewById(R.id.right_button_iso);
        mDeleteButton = (ImageView) mCurrentView.findViewById(R.id.delete_button);
        mDeleteButtonDisp = (TextView) mCurrentView.findViewById(R.id.delete_button_disp);
        mFilterGuide = (RelativeLayout) mCurrentView.findViewById(R.id.filter_exp_guide);
        mBorderView = (BorderView) mCurrentView.findViewById(R.id.border_view);
        setButtonVisibility(0);
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (!isHistgramView) {
            disappearButtonView();
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        userSettingAperture = false;
        userSettingShutterSpeed = false;
        userSettingExposureComp = false;
        isCancelSetting = false;
        isTvSetting = true;
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this.mCameraListener);
        DisplayModeObserver.getInstance().setNotificationListener(this);
        this.mParams = GFEffectParameters.getInstance().getParameters();
        GFHistgramTask.getInstance().startHistgramUpdating();
        if (GFCommonUtil.getInstance().needShowFilterExpSettingGuide()) {
            mFilterGuide.setVisibility(0);
            GFCommonUtil.getInstance().setShowFilterEVSettingFlag(false);
        } else {
            mFilterGuide.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this.mCameraListener);
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        mBorderView = null;
        if (myHandler != null) {
            myHandler.removeCallbacks(myRunnable);
            myHandler.removeCallbacks(myRunnableGuide);
            myHandler = null;
        }
        GFHistgramTask.getInstance().setEnableUpdating(false);
        myRunnable = null;
        myRunnableGuide = null;
        GFHistgramTask.getInstance().stopHistgramUpdating();
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        mDialTvAv = null;
        mFuncTvAv = null;
        mLeftButton = null;
        mLeftButtonEv = null;
        mRightButton = null;
        mRightButtonIso = null;
        mDeleteButton = null;
        mDeleteButtonDisp = null;
        mApertureView = null;
        mShutterSpeedView = null;
        mExposureCompView = null;
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        mCurrentView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setButtonVisibility(int visibility) {
        if (!GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase(ExposureModeController.PROGRAM_AUTO_MODE)) {
            mDialTvAv.setVisibility(visibility);
            mFuncTvAv.setVisibility(visibility);
        } else {
            mDialTvAv.setVisibility(4);
            mFuncTvAv.setVisibility(4);
        }
        mLeftButton.setVisibility(visibility);
        mLeftButtonEv.setVisibility(visibility);
        mRightButton.setVisibility(visibility);
        mRightButtonIso.setVisibility(visibility);
        mDeleteButton.setVisibility(visibility);
        mDeleteButtonDisp.setVisibility(visibility);
    }

    private void disappearButtonView() {
        if (myHandler == null) {
            myHandler = new Handler();
        }
        if (myRunnable == null) {
            myRunnable = new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.menu.layout.GFExposureMenuLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!GFExposureMenuLayout.isHistgramView) {
                        GFExposureMenuLayout.this.setButtonVisibility(4);
                    }
                }
            };
        }
        myHandler.postDelayed(myRunnable, 3000L);
    }

    private int getTvAvResId() {
        if (GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase("Aperture")) {
            return R.drawable.p_16_dd_parts_hgf_parts_assigned_func_av;
        }
        if (!GFExposureModeController.getInstance().getValue(null).equalsIgnoreCase("Shutter")) {
            return R.drawable.p_16_dd_parts_hgf_parts_assigned_func_tvav;
        }
        return R.drawable.p_16_dd_parts_hgf_parts_assigned_func_tv;
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
        if (userSettingAperture) {
            GFCommonUtil.getInstance().setAperture(false);
        }
        if (userSettingShutterSpeed) {
            GFCommonUtil.getInstance().setShutterSpeed(false);
        }
        if (userSettingExposureComp) {
            GFCommonUtil.getInstance().setEvComp(false);
        }
    }

    private void saveValues() {
        if (userSettingAperture) {
            int aperture = ChangeAperture.getApertureFromPF();
            if (aperture > 0) {
                this.mParams.setAperture(false, aperture);
            }
            AppLog.info(TAG, "Aperture: " + aperture);
        }
        if (userSettingShutterSpeed) {
            Pair<Integer, Integer> ss = ChangeSs.getCurrentSsFromPF();
            this.mParams.setSSNumerator(false, ((Integer) ss.first).intValue());
            this.mParams.setSSDenominator(false, ((Integer) ss.second).intValue());
            AppLog.info(TAG, "SSNumerator: " + ss.first);
            AppLog.info(TAG, "SSDenominator: " + ss.second);
        }
        if (userSettingExposureComp) {
            GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
            Integer index = Integer.valueOf(ExposureCompensationController.getInstance().getExposureCompensationIndex());
            params.setExposureComp(false, index.toString());
        }
    }

    private int handleExposreCompensationFunction() {
        if (ExposureModeController.MANUAL_MODE.equalsIgnoreCase(ExposureModeController.getInstance().getValue(ExposureModeController.EXPOSURE_MODE)) && !ISOSensitivityController.ISO_AUTO.equalsIgnoreCase(ISOSensitivityController.getInstance().getValue())) {
            if (SaUtil.isAVIP()) {
                CautionUtilityClass.getInstance().requestTrigger(1470);
            } else {
                CautionUtilityClass.getInstance().requestTrigger(3083);
            }
            return -1;
        }
        closeLayout();
        openNextMenu("ExposureCompensation", GFExposureCompensationMenuLayout.MENU_ID);
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
    public int turnedEVDial() {
        CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_EXPOSURE_DIAL);
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedExposureCompensationCustomKey() {
        userSettingExposureComp = true;
        ExposureCompensationController.getInstance().incrementExposureCompensation();
        mExposureCompView.setUserChanging(true);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedExposureCompensationCustomKey() {
        userSettingExposureComp = true;
        ExposureCompensationController.getInstance().decrementExposureCompensation();
        mExposureCompView.setUserChanging(true);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        AELController cntl = AELController.getInstance();
        cntl.repressAELock();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        AELController cntl = AELController.getInstance();
        cntl.holdAELock(true);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        AELController cntl = AELController.getInstance();
        cntl.holdAELock(false);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func) || CustomizableFunction.MfAssist.equals(func)) {
            int ret = super.onConvertedKeyDown(event, func);
            return ret;
        }
        if (CustomizableFunction.TvAvChange.equals(func)) {
            toggleTvOrAvSetting();
            return 1;
        }
        if (CustomizableFunction.TvOrAvDec.equals(func)) {
            decrementedTvOrAv();
            return 1;
        }
        if (CustomizableFunction.TvOrAvInc.equals(func)) {
            incrementedTvOrAv();
            return 1;
        }
        if (CustomizableFunction.ExposureCompensationIncrement.equals(func)) {
            incrementedExposureCompensationCustomKey();
            return 1;
        }
        if (CustomizableFunction.ExposureCompensationDecrement.equals(func)) {
            decrementedExposureCompensationCustomKey();
            return 1;
        }
        if (CustomizableFunction.DispChange.equals(func)) {
            toggleDispMode();
            return 1;
        }
        if (CustomizableFunction.ExposureCompensation.equals(func)) {
            int ret2 = handleExposreCompensationFunction();
            return ret2;
        }
        if (CustomizableFunction.AelHold.equals(func) || CustomizableFunction.AelToggle.equals(func)) {
            return 0;
        }
        if (CustomizableFunction.IsoSensitivity.equals(func)) {
            closeLayout();
            openNextMenu(ISOSensitivityController.MENU_ITEM_ID_ISO, GFISOSpecialScreenMenuLayout.MENU_ID);
            return 1;
        }
        if (CustomizableFunction.ApertureDecrement.equals(func) || CustomizableFunction.ApertureIncrement.equals(func) || CustomizableFunction.ShutterSpeedDecrement.equals(func) || CustomizableFunction.ShutterSpeedIncrement.equals(func)) {
            int ret3 = super.onConvertedKeyDown(event, func);
            return ret3;
        }
        if (595 == event.getScanCode()) {
            toggleDispMode();
            return 1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (GFCommonUtil.getInstance().isFilterSetting() && CustomizableFunction.AfMfHold.equals(func)) {
            return -1;
        }
        return super.onConvertedKeyUp(event, func);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int result = 1;
        int code = event.getScanCode();
        mApertureView.setUserChanging(false);
        mShutterSpeedView.setUserChanging(false);
        mExposureCompView.setUserChanging(false);
        if (code == 232) {
            if (mFilterGuide.getVisibility() == 0) {
                mFilterGuide.setVisibility(4);
            } else {
                openPreviousMenu();
            }
        } else if (code == 105 || (code == 108 && SaUtil.isAVIP())) {
            result = handleExposreCompensationFunction();
        } else if (code == 106) {
            closeLayout();
            openNextMenu(ISOSensitivityController.MENU_ITEM_ID_ISO, GFISOSpecialScreenMenuLayout.MENU_ID);
        } else if ((code == 526 || code == 525) && GFCommonUtil.getInstance().hasIrisRing() && GFCommonUtil.getInstance().isAperture()) {
            if (code == 526) {
                decrementedApertureCustomKey();
            } else {
                incrementedApertureCustomKey();
            }
        } else if ((code == 529 || code == 528) && GFCommonUtil.getInstance().hasIrisRing() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
            if (code == 529) {
                decrementedApertureCustomKey();
            } else {
                incrementedApertureCustomKey();
            }
        } else if (code == 513) {
            toggleDispMode();
        } else {
            result = (code == 610 || code == 611 || code == 645) ? -1 : GFCommonUtil.getInstance().isTriggerMfAssist(code) ? 0 : super.onKeyDown(keyCode, event);
        }
        mFilterGuide.setVisibility(4);
        return result;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 530 || code == 655) {
            if (!GFCommonUtil.getInstance().hasIrisRing() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
                if (GFCommonUtil.getInstance().isFixedAperture()) {
                    userSettingAperture = false;
                } else if (!userSettingAperture) {
                    GFCommonUtil.getInstance().setAperture(false);
                }
            }
        } else {
            if (code == 232) {
                return 1;
            }
            if (code == 595) {
                ICustomKey key = CustomKeyMgr.getInstance().get(code);
                if (!key.getAssigned("Menu").equals(CustomizableFunction.AelHold)) {
                    return 1;
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedApertureCustomKey() {
        if (!GFCommonUtil.getInstance().hasIrisRing() && GFCommonUtil.getInstance().isFixedAperture() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
            userSettingAperture = false;
            return -1;
        }
        userSettingAperture = true;
        CameraSetting.getInstance().incrementAperture();
        mApertureView.setUserChanging(true);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedApertureCustomKey() {
        if (!GFCommonUtil.getInstance().hasIrisRing() && GFCommonUtil.getInstance().isFixedAperture() && (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual())) {
            userSettingAperture = false;
            return -1;
        }
        userSettingAperture = true;
        CameraSetting.getInstance().decrementAperture();
        mApertureView.setUserChanging(true);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int incrementedShutterSpeedCustomKey() {
        userSettingShutterSpeed = true;
        CameraSetting.getInstance().incrementShutterSpeed();
        mShutterSpeedView.setUserChanging(true);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int decrementedShutterSpeedCustomKey() {
        userSettingShutterSpeed = true;
        CameraSetting.getInstance().decrementShutterSpeed();
        mShutterSpeedView.setUserChanging(true);
        return 1;
    }

    private void toggleDispMode() {
        if (!isHistgramView) {
            isHistgramView = true;
            setButtonVisibility(0);
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        } else {
            isHistgramView = false;
            disappearButtonView();
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        }
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
    }

    private void incrementedTvOrAv() {
        if (isTvSetting) {
            incrementedShutterSpeedCustomKey();
        } else {
            incrementedApertureCustomKey();
        }
    }

    private void decrementedTvOrAv() {
        if (isTvSetting) {
            decrementedShutterSpeedCustomKey();
        } else {
            decrementedApertureCustomKey();
        }
    }

    private void toggleTvOrAvSetting() {
        if (isTvSetting) {
            isTvSetting = false;
            mApertureView.setUserChanging(true);
        } else {
            isTvSetting = true;
            mShutterSpeedView.setUserChanging(true);
        }
    }

    /* loaded from: classes.dex */
    static class ChangeCameraNotifier implements NotificationListener {
        private static final String[] tags = {CameraNotificationManager.DEVICE_LENS_CHANGED};

        ChangeCameraNotifier() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (!GFCommonUtil.getInstance().hasIrisRing()) {
                if (GFCommonUtil.getInstance().isAperture() || GFCommonUtil.getInstance().isManual()) {
                    if (GFCommonUtil.getInstance().isFixedAperture()) {
                        boolean unused = GFExposureMenuLayout.userSettingAperture = false;
                        return;
                    }
                    if (!GFExposureMenuLayout.userSettingAperture) {
                        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
                        int step = ChangeAperture.getApertureAdjustmentStep(ChangeAperture.getApertureFromPF(), params.getAperture(false));
                        if (step != 0) {
                            GFCommonUtil.getInstance().setAperture(false);
                        }
                    }
                }
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
