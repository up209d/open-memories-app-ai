package com.sony.imaging.app.bracketpro.shooting.state.layout;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.bracketpro.BMAperture;
import com.sony.imaging.app.bracketpro.BMShutterSpeed;
import com.sony.imaging.app.bracketpro.R;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.menu.controller.BracketMasterController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class BracketMasterStableLayout extends StableLayout implements IModableLayout, NotificationListener {
    private static final String INVALID_APERTURE_STRING = "F--";
    static String bracketType;
    static int mRange;
    static int mRangeShutterSpeed;
    ImageView Value1_number;
    ImageView Value1_numbers;
    ImageView Value2_number;
    ImageView Value2_numbers;
    ImageView Value3_number;
    ImageView Value3_numbers;
    BMAperture aa;
    private boolean isAperturemain;
    private boolean isShuttermain;
    BracketMasterController mController;
    private ImageView mFocus1;
    RelativeLayout mFocus2;
    RelativeLayout mFocus22;
    RelativeLayout mFocus3;
    protected OnLayoutModeChangeListener mListener;
    BMNotification mNotify;
    String picture1;
    String picture2;
    String picture3;
    BMShutterSpeed ss;
    String text1;
    String text2;
    String text3;
    TextView textValue1;
    TextView textValue2;
    TextView textValue3;
    TextView textValues1;
    TextView textValues2;
    TextView textValues3;
    public static boolean sShootingExpoComp = false;
    public static int mshiftssT = 0;
    public static int mshiftssS = 0;
    public static int mshiftapS = 0;
    public static int mshiftapT = 0;
    String TAG = "BracketMasterStableLayout";
    private int mActiveDevice = 0;
    private RelativeLayout mMainFocusImageLayout = null;
    private final String[] tags = {"Aperture", CameraNotificationManager.SHUTTER_SPEED};

    public BracketMasterStableLayout() {
        if (this.mListener == null) {
            this.mListener = new OnLayoutModeChangeListener(this, 0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        initializeVariables();
        handleDisplayModeEVF();
        if (this.mNotify == null) {
            this.mNotify = new BMNotification();
            CameraNotificationManager.getInstance().setNotificationListener(this.mNotify);
        }
        updateExposureComp();
        super.onResume();
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NORMAL);
        if (this.mController == null) {
            this.mController = BracketMasterController.getInstance();
        }
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        this.mActiveDevice = DisplayModeObserver.getInstance().getActiveDevice();
        bracketType = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        showInfo();
        updateBracketValues();
        if (BracketMasterUtil.getCurrentBracketType().equalsIgnoreCase(BMMenuController.FocusBracket) && !FocusModeDialDetector.hasFocusModeDial()) {
            FocusModeController.getInstance().setValue("af-s");
        }
    }

    private void updateExposureComp() {
        if (CameraSetting.getPfApiVersion() >= 2 && EVDialDetector.hasEVDial()) {
            int ev = EVDialDetector.getEVDialPosition();
            if (ev != 0) {
                ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (device == 0) {
            baseViewId = R.layout.bm_shooting_main_sid_info_on_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 1) {
            baseViewId = R.layout.bm_shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(device, displayMode);
        } else if (device == 2) {
            baseViewId = R.layout.bm_shooting_main_sid_info_on_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        }
        if (-1 != baseViewId && -1 != baseFooterViewId && -1 != mainViewId && (baseViewId != this.mCurrentBaseViewId || baseFooterViewId != this.mCurrentBaseFooterViewId || mainViewId != this.mCurrentMainViewId)) {
            detachView();
            this.mCurrentBaseViewId = baseViewId;
            this.mCurrentMainViewId = mainViewId;
            this.mCurrentBaseFooterViewId = baseFooterViewId;
            this.mCurrentView = obtainViewFromPool(baseViewId);
            this.mCurrentBaseView = (ViewGroup) this.mCurrentView;
            View baseFooterView = obtainViewFromPool(baseFooterViewId);
            this.mCurrentBaseFooterView = baseFooterView;
            this.mCurrentBaseView.addView(baseFooterView);
            if (baseFooterView != null) {
                this.mUserChangableViews[0] = (DigitView) baseFooterView.findViewById(R.id.aperture_view);
                this.mUserChangableViews[1] = (DigitView) baseFooterView.findViewById(R.id.shutterspeed_view);
                this.mUserChangableViews[2] = (DigitView) baseFooterView.findViewById(R.id.exposure_and_meteredmanual_view);
                this.mUserChangableViews[3] = (DigitView) baseFooterView.findViewById(R.id.iso_sensitivity_view);
            }
            Looper.myQueue().addIdleHandler(this.idleHandler);
        }
        ExposureModeIconView icon1 = (ExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        AppIconView icon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon);
        if (device == 1) {
            icon1.setAutoDisappear(false);
            if (1 == displayMode) {
                icon2.setAutoDisappear(false);
            } else {
                icon2.setAutoDisappear(true);
            }
        } else if (1 == displayMode) {
            icon1.setAutoDisappear(false);
            icon2.setAutoDisappear(false);
        } else {
            icon1.setAutoDisappear(true);
            icon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                this.mTouchArea.setTouchAreaListener(getOnTouchListener());
            }
        }
    }

    public void showMainScree() {
        String bType = BMMenuController.getInstance().getSelectedBracket();
        bracketType = bType;
        if (bType.equalsIgnoreCase(BMMenuController.FocusBracket)) {
            focusMainScreen();
            return;
        }
        if (bType.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket)) {
            shutterMainScreen();
            int mRange2 = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_SHUTTER_RANGE_MAIN, 2);
            BracketMasterController.getInstance().ShutterSpeedValues(mRange2);
        } else if (bType.equalsIgnoreCase(BMMenuController.ApertureBracket)) {
            apertureMainScreen();
            int mRange1 = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_MAIN, 2);
            BracketMasterController.getInstance().apertureValues(mRange1);
        } else if (bType.equalsIgnoreCase(BMMenuController.FlashBracket)) {
            invisibleVariable();
            setFlashTypeNotForcely();
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateBracketValues();
    }

    public void updateBracketValues() {
        String bType = BracketMasterUtil.getCurrentBracketType();
        if (BMMenuController.ShutterSpeedBracket.equals(bType)) {
            int rangeLevel = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_SHUTTER_RANGE_MAIN, 2);
            BracketMasterController.getInstance().ShutterSpeedValues(rangeLevel);
        } else if (BMMenuController.ApertureBracket.equals(bType)) {
            int rangeLevel2 = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_MAIN, 2);
            BracketMasterController.getInstance().apertureValues(rangeLevel2);
        }
    }

    public void focusMainScreen() {
        invisibleVariable();
        CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
        if (info == null) {
            displayCaution();
        }
        if (info != null && info.LensType.equalsIgnoreCase("A-mount")) {
            displayAMountLensCaution(CautionInfo.CAUTION_ID_INH_FACTOR_INVALID_WITH_MOUNT_ADAPTER);
        }
        int bRange = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_FOCUS_RANGE, 1);
        this.mFocus1.setVisibility(0);
        this.mFocus2.setVisibility(8);
        if (bRange == 2) {
            this.mFocus1.setImageResource(R.drawable.p_16_dd_parts_bm_range_wide);
        } else if (bRange == 1) {
            this.mFocus1.setImageResource(R.drawable.p_16_dd_parts_bm_range_narrow);
        } else if (bRange == 0) {
            this.mFocus1.setImageResource(R.drawable.p_16_dd_parts_bm_range_mid);
        }
    }

    private void handleDisplayModeEVF() {
        RelativeLayout.LayoutParams adaptLayout;
        if (this.mFocus22 != null) {
            this.mFocus22.setVisibility(0);
        }
        this.mActiveDevice = DisplayModeObserver.getInstance().getActiveDevice();
        if (this.mActiveDevice == 1) {
            bracketType = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
            if (BMMenuController.FocusBracket.equalsIgnoreCase(bracketType)) {
                int bRange = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_FOCUS_RANGE, 1);
                adaptLayout = new RelativeLayout.LayoutParams(120, 64);
                adaptLayout.setMargins(63, 354, 0, 0);
                if (bRange == 2) {
                    this.mFocus1.setImageResource(R.drawable.p_16_aa_parts_bm_range_wide);
                } else if (bRange == 1) {
                    this.mFocus1.setImageResource(R.drawable.p_16_aa_parts_bm_range_mid);
                } else if (bRange == 0) {
                    this.mFocus1.setImageResource(R.drawable.p_16_aa_parts_bm_range_narrow);
                }
            } else {
                adaptLayout = new RelativeLayout.LayoutParams(160, 118);
                adaptLayout.setMargins(63, 300, 0, 0);
                RelativeLayout.LayoutParams txts1 = new RelativeLayout.LayoutParams(120, 30);
                RelativeLayout.LayoutParams txts2 = new RelativeLayout.LayoutParams(120, 30);
                RelativeLayout.LayoutParams txts3 = new RelativeLayout.LayoutParams(120, 30);
                RelativeLayout.LayoutParams Imgnum1 = new RelativeLayout.LayoutParams(30, 30);
                RelativeLayout.LayoutParams Imgnum2 = new RelativeLayout.LayoutParams(30, 30);
                RelativeLayout.LayoutParams Imgnum3 = new RelativeLayout.LayoutParams(30, 30);
                txts1.setMargins(35, 0, 0, 0);
                txts2.setMargins(35, 30, 0, 0);
                txts3.setMargins(35, 60, 0, 0);
                Imgnum1.setMargins(5, 0, 0, 0);
                Imgnum2.setMargins(5, 30, 0, 0);
                Imgnum3.setMargins(5, 60, 0, 0);
                this.textValues1.setLayoutParams(txts1);
                this.textValues2.setLayoutParams(txts2);
                this.textValues3.setLayoutParams(txts3);
                this.textValues1.setTextAppearance(getActivity().getApplicationContext(), R.style.RESID_FONTSIZE_REC_EXP_EVF_EDGE);
                this.textValues1.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN_EVF));
                this.textValues2.setTextAppearance(getActivity().getApplicationContext(), R.style.RESID_FONTSIZE_REC_EXP_EVF_EDGE);
                this.textValues2.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN_EVF));
                this.textValues3.setTextAppearance(getActivity().getApplicationContext(), R.style.RESID_FONTSIZE_REC_EXP_EVF_EDGE);
                this.textValues3.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN_EVF));
                this.Value1_numbers.setLayoutParams(Imgnum1);
                this.Value2_numbers.setLayoutParams(Imgnum2);
                this.Value3_numbers.setLayoutParams(Imgnum3);
                this.Value1_numbers.setImageResource(R.drawable.p_16_dd_parts_bm_number_1);
                this.Value2_numbers.setImageResource(R.drawable.p_16_dd_parts_bm_number_2);
                this.Value3_numbers.setImageResource(R.drawable.p_16_dd_parts_bm_number_3);
                this.textValue1.setLayoutParams(txts1);
                this.textValue2.setLayoutParams(txts2);
                this.textValue3.setLayoutParams(txts3);
                this.textValue1.setTextAppearance(getActivity().getApplicationContext(), R.style.RESID_FONTSIZE_REC_EXP_EVF_EDGE);
                this.textValue1.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN_EVF));
                this.textValue2.setTextAppearance(getActivity().getApplicationContext(), R.style.RESID_FONTSIZE_REC_EXP_EVF_EDGE);
                this.textValue2.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN_EVF));
                this.textValue3.setTextAppearance(getActivity().getApplicationContext(), R.style.RESID_FONTSIZE_REC_EXP_EVF_EDGE);
                this.textValue3.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN_EVF));
                this.Value1_number.setLayoutParams(Imgnum1);
                this.Value2_number.setLayoutParams(Imgnum2);
                this.Value3_number.setLayoutParams(Imgnum3);
                this.Value1_number.setImageResource(R.drawable.p_16_dd_parts_bm_number_1);
                this.Value2_number.setImageResource(R.drawable.p_16_dd_parts_bm_number_2);
                this.Value3_number.setImageResource(R.drawable.p_16_dd_parts_bm_number_3);
            }
            this.mMainFocusImageLayout.setLayoutParams(adaptLayout);
        }
    }

    public void shutterMainScreen() {
        onNotify(this.tags[1]);
        this.isShuttermain = true;
        invisibleVariable();
        mRangeShutterSpeed = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_SHUTTER_RANGE_MAIN, 2);
        this.ss = new BMShutterSpeed();
        this.mController.ShutterSpeedValues(mRangeShutterSpeed);
        getValueShutter();
        this.picture1 = "" + this.ss.getCenterShutterSpeedValue();
        BracketMasterController bracketMasterController = this.mController;
        int pic = BracketMasterController.getInstance().mShutterSpeedValueList.indexOf(this.picture1);
        BracketMasterController bracketMasterController2 = this.mController;
        BracketMasterController.getInstance().setCenterPos(pic);
        this.picture2 = "" + this.mController.getFirstShutterSpeedValue(this.ss.getCenterShutterSpeedValue(), mshiftssT);
        BracketMasterController bracketMasterController3 = this.mController;
        int pic2 = BracketMasterController.getInstance().mShutterSpeedValueList.indexOf(this.picture2);
        BracketMasterController bracketMasterController4 = this.mController;
        BracketMasterController.getInstance().setFirstPos(pic2);
        this.picture3 = "" + this.mController.getSecondShutterSpeedValue(this.ss.getCenterShutterSpeedValue(), mshiftssS);
        BracketMasterController bracketMasterController5 = this.mController;
        int pic3 = BracketMasterController.getInstance().mShutterSpeedValueList.indexOf(this.picture3);
        BracketMasterController bracketMasterController6 = this.mController;
        BracketMasterController.getInstance().setSecondPos(pic3);
        this.mFocus3.setBackgroundResource(R.drawable.s_16_dd_parts_bm_background);
        visibleVariableForshutter();
        setValueforShutter(this.picture1, this.picture2, this.picture3);
    }

    public void apertureMainScreen() {
        this.isAperturemain = true;
        onNotify(this.tags[0]);
        invisibleVariable();
        this.aa = new BMAperture();
        mRange = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_MAIN, 2);
        this.mController.apertureValues(mRange);
        mshiftapS = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_MAIN_shiftS, 2);
        mshiftapT = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_MAIN_shiftT, 2);
        getValueAperture(mshiftapS, mshiftapT);
        String text1 = "" + this.aa.getCenterApertureValue();
        String text2 = "" + this.mController.getFirstApertureValue(this.aa.getCenterApertureValue(), mshiftapS);
        String text3 = "" + this.mController.getSecondApertureValue(this.aa.getCenterApertureValue(), mshiftapT);
        CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
        if (info == null || !text1.equalsIgnoreCase(INVALID_APERTURE_STRING)) {
            visibleVariable();
            this.mFocus2.setVisibility(0);
            setValue(text1, text2, text3);
        }
    }

    public void initializeVariables() {
        this.Value1_number = (ImageView) this.mCurrentView.findViewById(R.id.Value1number);
        this.Value2_number = (ImageView) this.mCurrentView.findViewById(R.id.Value2number);
        this.Value3_number = (ImageView) this.mCurrentView.findViewById(R.id.Value3number);
        this.textValue1 = (TextView) this.mCurrentView.findViewById(R.id.Value1value);
        this.textValue2 = (TextView) this.mCurrentView.findViewById(R.id.Value2value);
        this.textValue3 = (TextView) this.mCurrentView.findViewById(R.id.Value3value);
        this.mFocus1 = (ImageView) this.mCurrentView.findViewById(R.id.Main_Focus20);
        this.mFocus2 = (RelativeLayout) this.mCurrentView.findViewById(R.id.Main_Focus21);
        this.mFocus3 = (RelativeLayout) this.mCurrentView.findViewById(R.id.Main_Shutter);
        this.mFocus22 = (RelativeLayout) this.mCurrentView.findViewById(R.id.Main_Focus22);
        this.Value1_numbers = (ImageView) this.mCurrentView.findViewById(R.id.Value1numbers);
        this.Value2_numbers = (ImageView) this.mCurrentView.findViewById(R.id.Value2numbers);
        this.Value3_numbers = (ImageView) this.mCurrentView.findViewById(R.id.Value3numbers);
        this.textValues1 = (TextView) this.mCurrentView.findViewById(R.id.Value1values);
        this.textValues2 = (TextView) this.mCurrentView.findViewById(R.id.Value2values);
        this.textValues3 = (TextView) this.mCurrentView.findViewById(R.id.Value3values);
        this.mMainFocusImageLayout = (RelativeLayout) this.mCurrentView.findViewById(R.id.Main_Focus22);
    }

    public void visibleVariable() {
        this.mFocus2.setVisibility(0);
        this.mFocus3.setVisibility(8);
        this.Value1_number.setVisibility(0);
        this.Value2_number.setVisibility(0);
        this.Value3_number.setVisibility(0);
        this.textValue1.setVisibility(0);
        this.textValue2.setVisibility(0);
        this.textValue3.setVisibility(0);
    }

    public void visibleVariableForshutter() {
        this.mFocus2.setVisibility(8);
        this.mFocus3.setVisibility(0);
        this.Value1_numbers.setVisibility(0);
        this.Value2_numbers.setVisibility(0);
        this.Value3_numbers.setVisibility(0);
        this.textValues1.setVisibility(0);
        this.textValues2.setVisibility(0);
        this.textValues3.setVisibility(0);
    }

    public void invisibleVariable() {
        this.Value1_number.setVisibility(8);
        this.Value2_number.setVisibility(8);
        this.Value3_number.setVisibility(8);
        this.textValue1.setVisibility(8);
        this.textValue2.setVisibility(8);
        this.textValue3.setVisibility(8);
        this.mFocus1.setVisibility(8);
        this.mFocus2.setVisibility(8);
        this.mFocus3.setVisibility(8);
    }

    void setValue(String value1, String value2, String value3) {
        if (value1 == null || value1.equalsIgnoreCase(INVALID_APERTURE_STRING) || value1.equalsIgnoreCase(null) || value1.equalsIgnoreCase("null")) {
            value1 = INVALID_APERTURE_STRING;
            value2 = INVALID_APERTURE_STRING;
            value3 = INVALID_APERTURE_STRING;
        }
        if (this.textValue1 != null && this.textValue2 != null && this.textValue3 != null) {
            this.textValue1.setText(value1);
            this.textValue2.setText(value2);
            this.textValue3.setText(value3);
        }
    }

    void setValueforShutter(String value1, String value2, String value3) {
        if (value1 == null || value1.equalsIgnoreCase(null) || value1.equalsIgnoreCase("null")) {
            value1 = "";
            value2 = "";
            value3 = "";
        }
        this.textValues1.setText(value1);
        this.textValues2.setText(value2);
        this.textValues3.setText(value3);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                if (this.isShuttermain) {
                    shutterMainScreen();
                } else if (this.isAperturemain) {
                    apertureMainScreen();
                }
                return 0;
            default:
                return 0;
        }
    }

    private void freeResources() {
        removeBitmap(this.mFocus1);
        removeBitmap(this.Value1_number);
        removeBitmap(this.Value2_number);
        removeBitmap(this.Value3_number);
        removeBitmap(this.Value1_numbers);
        removeBitmap(this.Value2_numbers);
        removeBitmap(this.Value3_numbers);
        this.mMainFocusImageLayout = null;
        if (this.mCurrentView != null) {
            this.mCurrentView = null;
        }
        if (this.mNotify != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mNotify);
            this.mNotify = null;
        }
        this.Value1_number = null;
        this.Value2_number = null;
        this.Value3_number = null;
        this.textValue1 = null;
        this.textValue2 = null;
        this.textValue3 = null;
        this.mFocus1 = null;
        this.mFocus2 = null;
        this.mFocus3 = null;
        this.mFocus22 = null;
        this.Value1_numbers = null;
        this.Value2_numbers = null;
        this.Value3_numbers = null;
        this.textValues1 = null;
        this.textValues2 = null;
        this.textValues3 = null;
        this.mMainFocusImageLayout = null;
    }

    private void removeBitmap(ImageView v) {
        Bitmap b = null;
        if (v != null) {
            b = v.getDrawingCache();
        }
        if (b != null) {
            b.recycle();
            v.setImageResource(0);
        }
    }

    void showInfo() {
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
        bracketType = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        if (this.mActiveDevice == 1) {
            if (1 == displayMode) {
                showMainScree();
                return;
            } else {
                invisibleVariable();
                return;
            }
        }
        if (1 == displayMode) {
            showMainScree();
        } else {
            invisibleVariable();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class BMNotification implements NotificationListener {
        private final String[] tags = {"Aperture", CameraNotificationManager.SHUTTER_SPEED};

        BMNotification() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equalsIgnoreCase(CameraNotificationManager.SHUTTER_SPEED) && BracketMasterStableLayout.bracketType.equalsIgnoreCase(BMMenuController.ShutterSpeedBracket) && BracketMasterStableLayout.this.isShuttermain) {
                BracketMasterStableLayout.this.shutterMainScreen();
            } else if (tag.equalsIgnoreCase("Aperture") && BracketMasterStableLayout.bracketType.equalsIgnoreCase(BMMenuController.ApertureBracket) && BracketMasterStableLayout.this.isAperturemain) {
                BracketMasterStableLayout.this.apertureMainScreen();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        this.mActiveDevice = device;
        updateView();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.isShuttermain = false;
        this.isAperturemain = false;
        freeResources();
        super.onPause();
    }

    void getValueShutter() {
        double Center = this.mController.getShutterCenterValuesForTrapezium(this.ss.getCenterShutterSpeedValue());
        double first = this.mController.getShutterFirstValuesForTrapezium(this.ss.getCenterShutterSpeedValue(), mRangeShutterSpeed);
        double second = this.mController.getShutterSecondValuesForTrapezium(this.ss.getCenterShutterSpeedValue(), mRangeShutterSpeed);
        this.ss.setmCenterXVal(Center);
        this.ss.setmSecondXvalue(second);
        this.ss.setmFirstXvalue(first);
        BracketMasterController bracketMasterController = this.mController;
        int firstPos = BracketMasterController.getInstance().getFirstPos();
        BracketMasterController bracketMasterController2 = this.mController;
        mshiftssT = (firstPos - BracketMasterController.getInstance().getCenterPos()) - 1;
        BracketMasterController bracketMasterController3 = this.mController;
        int centerPos = BracketMasterController.getInstance().getCenterPos();
        BracketMasterController bracketMasterController4 = this.mController;
        mshiftssS = (centerPos - BracketMasterController.getInstance().getSecondPos()) - 1;
        Log.i(this.TAG, "getValueShutter()  mshiftssT:" + mshiftssT + "  mshiftssS:" + mshiftssS);
    }

    void getValueAperture(int shS, int shT) {
        int Center = this.mController.getCenterApertureTrapeziumValue(this.aa.getCenterApertureValue());
        int first = this.mController.getFirstApertureTrapeziumValue(this.aa.getCenterApertureValue(), mRange);
        int second = this.mController.getSecondApertureTrapeziumValue(this.aa.getCenterApertureValue(), mRange);
        this.aa.setmCenterYval(Center);
        this.aa.setmSecondYvalue(second);
        this.aa.setmFirstYvalue(first);
        mshiftapT = this.aa.getThirdShootingPosition();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        CameraEx.LensInfo info;
        if (tag == "Aperture" && (info = CameraSetting.getInstance().getLensInfo()) != null && info.LensType.equalsIgnoreCase("A-mount") && BMMenuController.getInstance().getSelectedBracket().equalsIgnoreCase(BMMenuController.FocusBracket)) {
            CautionUtilityClass.getInstance().disapperTrigger(CautionInfo.CAUTION_ID_INH_FACTOR_DETACH_EMOUNT_LENS);
        }
        BracketMasterController.getInstance().refreshApertureValues();
        BracketMasterController.getInstance().refreshShutterSpeedValues();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.tags;
    }

    private void setFlashTypeNotForcely() {
        String flashType = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.FLASH_TYPE_DIADEM, null);
        if (flashType != null) {
            Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getEmptyParameters();
            ((CameraEx.ParametersModifier) params.second).setFlashType(flashType);
            CameraSetting.getInstance().setParameters(params);
        }
    }

    private void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.shooting.state.layout.BracketMasterStableLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                        return 0;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        try {
                            Thread.sleep(500L);
                        } catch (Exception e) {
                        }
                        CameraEx.LensInfo info = CameraSetting.getInstance().getLensInfo();
                        if (info == null || info.LensType.equalsIgnoreCase("A-mount")) {
                        }
                        return -1;
                    default:
                        return -1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CautionInfo.CAUTION_ID_INH_FACTOR_DETACH_EMOUNT_LENS, mKey);
        CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_ID_INH_FACTOR_DETACH_EMOUNT_LENS);
    }

    private void displayAMountLensCaution(int cautionid) {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.shooting.state.layout.BracketMasterStableLayout.2
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                        return 0;
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    default:
                        return -1;
                }
            }
        };
        CautionUtilityClass.getInstance().setDispatchKeyEvent(cautionid, mKey);
        CautionUtilityClass.getInstance().requestTrigger(cautionid);
    }
}
