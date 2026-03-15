package com.sony.imaging.app.timelapse.menu.layout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.FrameRateController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.timelapse.shooting.widget.TimelapseImageView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TLCustomThemeOptionLayout extends DisplayMenuItemsMenuLayout implements View.OnClickListener {
    private static final int AEPF1LOCK_BG_LEFTMARGIN = 378;
    private static final int AEPF1LOCK_LEFTMARGIN = 351;
    private static final int AEPF1TRACK_LEFTMARGIN = 475;
    private static final int AEPF1_BG_HIGHT = 7;
    private static final int AEPF1_BG_TOPMARGIN = 301;
    private static final int AEPF1_HIGHT = 48;
    private static final int AEPF1_TOPMARGIN = 258;
    private static final int AEPF1_WIDTH = 114;
    private static final int ROW_AESTATUS = 4;
    private static final int ROW_INTERVAL = 2;
    private static final int ROW_RECORDING = 1;
    private static final int ROW_RESET = 0;
    private static final int ROW_SHOT = 3;
    private static int mAEStatus;
    private static int mInterval;
    private static int mOldAEStatus;
    private static int mOldInterval;
    private static int mOldShootingNumber;
    private static ArrayList<Integer> mShootingList;
    private static int mShootingNumber;
    private View mCurrentView;
    public TLCommonUtil mTLCommonUtil;
    private TLShootModeSettingController mTlShootModeController;
    private static TextView mRecordingModeName = null;
    private static TextView mAEStatusName = null;
    private static TextView mIntervalName = null;
    private static TextView mShootingNumberName = null;
    private static TextView mIntervalValue = null;
    private static TextView mShootingNumberValue = null;
    private static TextView mShootingTimeTitle = null;
    private static TextView mShootingTimeValue = null;
    private static TextView mThemeName = null;
    private static TextView mPlayBackTextTime = null;
    private static ImageButton mResetImageBtn = null;
    private static ImageView mPlayBackImageBtn = null;
    private static ImageView mFrameRateImage = null;
    private static ImageButton mMovieBtn = null;
    private static ImageButton mStillBtn = null;
    private static ImageButton mStillMovieBtn = null;
    private static ImageButton mAeLock = null;
    private static ImageButton mAelTracking = null;
    private static ImageButton mAelTrackLow = null;
    private static ImageButton mAelTrackMid = null;
    private static ImageButton mAelTrackHigh = null;
    private static ImageButton mMovieBtnBg = null;
    private static ImageButton mStillBtnBg = null;
    private static ImageButton mStillMovieBtnBg = null;
    private static ImageButton mAeLockBg = null;
    private static ImageButton mAelTrackingBg = null;
    private static ImageButton mAelTrackLowBg = null;
    private static ImageButton mAelTrackMidBg = null;
    private static ImageButton mAelTrackHighBg = null;
    private static TextView mInterval10min = null;
    private static TextView mInterval1min = null;
    private static TextView mInterval10sec = null;
    private static TextView mInterval1sec = null;
    private static TextView mInterval10minVal = null;
    private static TextView mInterval1minVal = null;
    private static TextView mInterval10secVal = null;
    private static TextView mInterval1secVal = null;
    private static ImageView mIntervalView = null;
    private static TimelapseImageView mInterval10minUp = null;
    private static TimelapseImageView mInterval1minUp = null;
    private static TimelapseImageView mInterval10secUp = null;
    private static TimelapseImageView mInterval1secUp = null;
    private static TimelapseImageView mInterval10minDown = null;
    private static TimelapseImageView mInterval1minDown = null;
    private static TimelapseImageView mInterval10secDown = null;
    private static TimelapseImageView mInterval1secDown = null;
    private static TextView mShots1000 = null;
    private static TextView mShots100 = null;
    private static TextView mShots10 = null;
    private static TextView mShots1 = null;
    private static TextView mShots1000Val = null;
    private static TextView mShots100Val = null;
    private static TextView mShots10Val = null;
    private static TextView mShots1Val = null;
    private static ImageView mShotsView = null;
    private static TimelapseImageView mShots1000Up = null;
    private static TimelapseImageView mShots100Up = null;
    private static TimelapseImageView mShots10Up = null;
    private static TimelapseImageView mShots1Up = null;
    private static TimelapseImageView mShots1000Down = null;
    private static TimelapseImageView mShots100Down = null;
    private static TimelapseImageView mShots10Down = null;
    private static TimelapseImageView mShots1Down = null;
    private static boolean mHasFocusOnInterval = false;
    private static boolean mHasFocusOnShots = false;
    private static View[] mViewArray = null;
    private static View mLastFocusedView = null;
    private static FooterGuide mFooterGuide = null;
    private static String mFileFormat = null;
    private static int inRecRowSelection = 0;
    private static int inAEStatusRowSelection = 0;
    private static String mOldFileFormat = null;
    private static String mSecondString = null;
    private static RelativeLayout.LayoutParams mLayoutParamsAELock = null;
    private static RelativeLayout.LayoutParams mLayoutParamsAETracking = null;
    private static RelativeLayout.LayoutParams mLayoutParamsAELockBg = null;
    private static final int AEPF1_BG_WIDTH = 60;
    private static final int[] mShots = {1, 30, AEPF1_BG_WIDTH, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360, 390, 420, 450, 480, 510, AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL, 570, AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S, AppRoot.USER_KEYCODE.MODE_S, 660, 690, 720, 750, 780, 810, 840, 870, 900, 930, 960, 990};
    private static int MIN_INTERVAL = 1;
    private static int MAX_INTERVAL = AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S;
    private static boolean isMaxIntervalLimit = false;
    private static int MIN_SHOTS = 1;
    private static int mCurrentRow = 1;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.tl_layout_menu_custom_theme_option);
        }
        this.mService = new BaseMenuService(getActivity().getApplicationContext());
        return this.mCurrentView;
    }

    private void initializeView() {
        mRecordingModeName = (TextView) this.mCurrentView.findViewById(R.id.rec_mode_name);
        mAEStatusName = (TextView) this.mCurrentView.findViewById(R.id.ae_status_name);
        mIntervalName = (TextView) this.mCurrentView.findViewById(R.id.interval_name);
        mShootingNumberName = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_name);
        mIntervalValue = (TextView) this.mCurrentView.findViewById(R.id.interval_value);
        mShootingNumberValue = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_value);
        mShootingTimeTitle = (TextView) this.mCurrentView.findViewById(R.id.shooting_time_title);
        mShootingTimeValue = (TextView) this.mCurrentView.findViewById(R.id.shooting_time_value);
        mMovieBtn = (ImageButton) this.mCurrentView.findViewById(R.id.movie_btn);
        mResetImageBtn = (ImageButton) this.mCurrentView.findViewById(R.id.tl_reset_button);
        mStillBtn = (ImageButton) this.mCurrentView.findViewById(R.id.still_btn);
        mStillMovieBtn = (ImageButton) this.mCurrentView.findViewById(R.id.still_movie_btn);
        mAeLock = (ImageButton) this.mCurrentView.findViewById(R.id.ael_ael);
        mAelTracking = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_image);
        mAelTrackLow = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_low);
        mAelTrackMid = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_mid);
        mAelTrackHigh = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_high);
        mMovieBtnBg = (ImageButton) this.mCurrentView.findViewById(R.id.movie_btn_bg);
        mStillBtnBg = (ImageButton) this.mCurrentView.findViewById(R.id.still_btn_bg);
        mStillMovieBtnBg = (ImageButton) this.mCurrentView.findViewById(R.id.still_movie_btn_bg);
        mAeLockBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_ael_bg);
        mAelTrackingBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_image_bg);
        mAelTrackLowBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_low_bg);
        mAelTrackMidBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_mid_bg);
        mAelTrackHighBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_high_bg);
        mInterval10min = (TextView) this.mCurrentView.findViewById(R.id.interval_10min);
        mInterval1min = (TextView) this.mCurrentView.findViewById(R.id.interval_1min);
        mInterval10sec = (TextView) this.mCurrentView.findViewById(R.id.interval_10sec);
        mInterval1sec = (TextView) this.mCurrentView.findViewById(R.id.interval_1sec);
        mInterval10minVal = (TextView) this.mCurrentView.findViewById(R.id.interval_10min_val);
        mInterval1minVal = (TextView) this.mCurrentView.findViewById(R.id.interval_1min_val);
        mInterval10secVal = (TextView) this.mCurrentView.findViewById(R.id.interval_10sec_val);
        mInterval1secVal = (TextView) this.mCurrentView.findViewById(R.id.interval_1sec_val);
        mIntervalView = (ImageView) this.mCurrentView.findViewById(R.id.interval_raw);
        mInterval10minUp = (TimelapseImageView) this.mCurrentView.findViewById(R.id.interval_10min_up);
        mInterval1minUp = (TimelapseImageView) this.mCurrentView.findViewById(R.id.interval_1min_up);
        mInterval10secUp = (TimelapseImageView) this.mCurrentView.findViewById(R.id.interval_10sec_up);
        mInterval1secUp = (TimelapseImageView) this.mCurrentView.findViewById(R.id.interval_1sec_up);
        mInterval10minDown = (TimelapseImageView) this.mCurrentView.findViewById(R.id.interval_10min_down);
        mInterval1minDown = (TimelapseImageView) this.mCurrentView.findViewById(R.id.interval_1min_down);
        mInterval10secDown = (TimelapseImageView) this.mCurrentView.findViewById(R.id.interval_10sec_down);
        mInterval1secDown = (TimelapseImageView) this.mCurrentView.findViewById(R.id.interval_1sec_down);
        mShots1000 = (TextView) this.mCurrentView.findViewById(R.id.shooting_1000);
        mShots100 = (TextView) this.mCurrentView.findViewById(R.id.shooting_100);
        mShots10 = (TextView) this.mCurrentView.findViewById(R.id.shooting_10);
        mShots1 = (TextView) this.mCurrentView.findViewById(R.id.shooting_1);
        mShots1000Val = (TextView) this.mCurrentView.findViewById(R.id.shooting_1000_val);
        mShots100Val = (TextView) this.mCurrentView.findViewById(R.id.shooting_100_val);
        mShots10Val = (TextView) this.mCurrentView.findViewById(R.id.shooting_10_val);
        mShots1Val = (TextView) this.mCurrentView.findViewById(R.id.shooting_1_val);
        mShotsView = (ImageView) this.mCurrentView.findViewById(R.id.shooting_raw);
        mShots1000Up = (TimelapseImageView) this.mCurrentView.findViewById(R.id.shooting_1000_up);
        mShots100Up = (TimelapseImageView) this.mCurrentView.findViewById(R.id.shooting_100_up);
        mShots10Up = (TimelapseImageView) this.mCurrentView.findViewById(R.id.shooting_10_up);
        mShots1Up = (TimelapseImageView) this.mCurrentView.findViewById(R.id.shooting_1_up);
        mShots1000Down = (TimelapseImageView) this.mCurrentView.findViewById(R.id.shooting_1000_down);
        mShots100Down = (TimelapseImageView) this.mCurrentView.findViewById(R.id.shooting_100_down);
        mShots10Down = (TimelapseImageView) this.mCurrentView.findViewById(R.id.shooting_10_down);
        mShots1Down = (TimelapseImageView) this.mCurrentView.findViewById(R.id.shooting_1_down);
        mPlayBackImageBtn = (ImageView) this.mCurrentView.findViewById(R.id.playback_image_view);
        mFrameRateImage = (ImageView) this.mCurrentView.findViewById(R.id.framerate);
        mPlayBackTextTime = (TextView) this.mCurrentView.findViewById(R.id.playback_value);
        mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_theme_option);
        setFooterGuide();
        mThemeName = (TextView) this.mCurrentView.findViewById(R.id.theme_name);
        mViewArray = new View[]{mMovieBtn, mStillBtn, mStillMovieBtn, mAeLock, mAelTrackLow, mAelTrackMid, mAelTrackHigh, mAelTracking, mResetImageBtn, mInterval10min, mInterval1min, mInterval10sec, mInterval1sec, mIntervalView, mShots1000, mShots100, mShots10, mShots1, mShotsView};
    }

    private void initializeListenersForView() {
        for (int i = 0; i < mViewArray.length - 1; i++) {
            mViewArray[i].setOnClickListener(this);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        initializeView();
        initialOperationOnResume();
        setAEViewVisibilityForPF();
        updatePFSpecificAEUI();
    }

    private void updatePFSpecificAEUI() {
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            if (mLayoutParamsAELock == null) {
                mLayoutParamsAELock = getRelativeLayout(AEPF1LOCK_LEFTMARGIN, AEPF1_TOPMARGIN, AEPF1_WIDTH, AEPF1_HIGHT);
            }
            mAeLock.setLayoutParams(mLayoutParamsAELock);
            if (mLayoutParamsAETracking == null) {
                mLayoutParamsAETracking = getRelativeLayout(AEPF1TRACK_LEFTMARGIN, AEPF1_TOPMARGIN, AEPF1_WIDTH, AEPF1_HIGHT);
            }
            mAelTracking.setLayoutParams(mLayoutParamsAETracking);
            if (mLayoutParamsAELockBg == null) {
                mLayoutParamsAELockBg = getRelativeLayout(AEPF1LOCK_BG_LEFTMARGIN, AEPF1_BG_TOPMARGIN, AEPF1_BG_WIDTH, 7);
            }
            mAeLockBg.setLayoutParams(mLayoutParamsAELockBg);
        }
    }

    private RelativeLayout.LayoutParams getRelativeLayout(int leftMargin, int topMargin, int width, int hight) {
        RelativeLayout.LayoutParams mRelativeLayout = new RelativeLayout.LayoutParams(-2, -2);
        mRelativeLayout.leftMargin = leftMargin;
        mRelativeLayout.topMargin = topMargin;
        mRelativeLayout.width = width;
        mRelativeLayout.height = hight;
        return mRelativeLayout;
    }

    private void setAEViewVisibilityForPF() {
        if (TLCommonUtil.getInstance().isTimelapseLiteApplication()) {
            mAelTrackHigh.setVisibility(4);
            mAelTrackMid.setVisibility(4);
            mAelTrackLow.setVisibility(4);
            mAelTrackHighBg.setVisibility(4);
            mAelTrackMidBg.setVisibility(4);
            mAelTrackLowBg.setVisibility(4);
            mAeLock.setVisibility(4);
            mAeLockBg.setVisibility(4);
            mAelTracking.setVisibility(4);
            mAelTrackingBg.setVisibility(4);
            mAEStatusName.setVisibility(4);
            return;
        }
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            mAelTrackHigh.setVisibility(4);
            mAelTrackMid.setVisibility(4);
            mAelTrackLow.setVisibility(4);
            mAelTrackHighBg.setVisibility(4);
            mAelTrackMidBg.setVisibility(4);
            mAelTrackLowBg.setVisibility(4);
            mAelTrackingBg.setVisibility(0);
            return;
        }
        mAelTrackHigh.setVisibility(0);
        mAelTrackMid.setVisibility(0);
        mAelTrackLow.setVisibility(0);
        mAelTrackHighBg.setVisibility(0);
        mAelTrackMidBg.setVisibility(0);
        mAelTrackLowBg.setVisibility(0);
        mAelTrackingBg.setVisibility(4);
    }

    private void initialOperationOnResume() {
        initializeListenersForView();
        initializeObjects();
        getLastStoredValues();
        releaseSelectionFromAll();
        makeViewsUnfocusable();
        restoreDefaultSelection();
        setDefaultSelectValue();
        setNormalInterval();
        setNormalShots();
        handlePlayTimeVisibility(inRecRowSelection == 1 ? 4 : 0);
        if (!isFunctionGuideShown()) {
            setFocusOnResume();
        }
    }

    private void initializeObjects() {
        this.mTlShootModeController = TLShootModeSettingController.getInstance();
        mShootingList = new ArrayList<>();
        mSecondString = getResources().getString(R.string.STRID_FUNC_TIMELAPSE_INTERVAL_VALUE);
        this.mTLCommonUtil = TLCommonUtil.getInstance();
        mThemeName.setText(this.mTLCommonUtil.getSelectedThemeName());
        for (int i = 0; i < mShots.length; i++) {
            mShootingList.add(Integer.valueOf(mShots[i]));
        }
        mPlayBackImageBtn.setImageResource(R.drawable.p_16_dd_parts_tm_playbacktime_icon_small_nomal);
        if ("framerate-24p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
            mFrameRateImage.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        } else if ("framerate-30p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
            mFrameRateImage.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        }
    }

    private void handlePlayTimeVisibility(int visibilityState) {
        mPlayBackImageBtn.setVisibility(visibilityState);
        mPlayBackTextTime.setText("" + getPlackBackDuration());
        mPlayBackTextTime.setVisibility(visibilityState);
        mFrameRateImage.setVisibility(visibilityState);
    }

    private void setFooterGuide() {
        if ((mCurrentRow == 2 && !hasFocusOnInterval()) || (mCurrentRow == 3 && !hasFocusOnInterval())) {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.autofill_postal_code, android.R.string.hardware));
        } else {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE_SK));
        }
    }

    private void restoreDefaultSelection() {
        removeFocusFromAll();
        setNormalIcon();
        setNormalInterval();
        setNormalShots();
        setOptionNameNormal();
        switch (mCurrentRow) {
            case 0:
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_focused);
                break;
            case 1:
                switch (inRecRowSelection) {
                    case 1:
                        setSelectedItemOnResume(mStillBtn, R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
                        mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 2:
                        setSelectedItemOnResume(mStillMovieBtn, R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
                        mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    default:
                        setSelectedItemOnResume(mMovieBtn, R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
                        mMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                }
            case 2:
                mIntervalView.setVisibility(0);
                setFocusableView(mIntervalView);
                break;
            case 3:
                mShotsView.setVisibility(0);
                setFocusableView(mShotsView);
                break;
            case 4:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    setValidFocuseOnResume();
                    break;
                } else {
                    setDisabledFocusOnResume();
                    break;
                }
        }
        setOptionNameSelected();
        setShotsText();
    }

    private void setDisabledFocusOnResume() {
        switch (inAEStatusRowSelection) {
            case 1:
                setSelectedItemOnResume(mAelTrackHigh, R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
                mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 2:
                setSelectedItemOnResume(mAelTrackMid, R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
                mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 3:
                setSelectedItemOnResume(mAelTrackLow, R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
                mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 4:
                setSelectedItemOnResume(mAelTracking, R.drawable.p_16_dd_parts_tm_tracking_disable);
                mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            default:
                setSelectedItemOnResume(mAeLock, R.drawable.p_16_dd_parts_tm_ael_disable);
                mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
        }
    }

    private void setValidFocuseOnResume() {
        switch (inAEStatusRowSelection) {
            case 1:
                setSelectedItemOnResume(mAelTrackHigh, R.drawable.p_16_dd_parts_tm_tracking_hi_focused);
                mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 2:
                setSelectedItemOnResume(mAelTrackMid, R.drawable.p_16_dd_parts_tm_tracking_mid_focused);
                mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 3:
                setSelectedItemOnResume(mAelTrackLow, R.drawable.p_16_dd_parts_tm_tracking_lo_focused);
                mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 4:
                setSelectedItemOnResume(mAelTracking, R.drawable.p_16_dd_parts_tm_tracking_focused);
                mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            default:
                setSelectedItemOnResume(mAeLock, R.drawable.p_16_dd_parts_tm_ael_focused);
                mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
        }
    }

    private void setSelectedItemOnResume(ImageButton view, int drawableID) {
        view.setImageResource(drawableID);
        setFocusableView(view);
    }

    private void releaseSelectionFromAll() {
        for (int i = 0; i < mViewArray.length - 1; i++) {
            mViewArray[i].setSelected(false);
        }
        setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_normal);
    }

    private void removeFocusFromAll() {
        for (int i = 0; i < mViewArray.length - 1; i++) {
            clearFocusedView(mViewArray[i]);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tl_reset_button /* 2131361837 */:
                mCurrentRow = 0;
                resetThemeProperty();
                break;
            case R.id.movie_btn /* 2131362389 */:
                AppLog.trace("INSIDE", "movie");
                mFileFormat = "MOVIE_FORMAT";
                removeFocusFromAll();
                mCurrentRow = 1;
                inRecRowSelection = 0;
                setNormalIcon();
                mMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
                mMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                handlePlayTimeVisibility(0);
                this.mTlShootModeController.setCaptureState(0);
                setSelectedOptionAEStatusRow();
                break;
            case R.id.still_btn /* 2131362391 */:
                AppLog.trace("INSIDE", AntiHandBlurController.STILL);
                mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL;
                removeFocusFromAll();
                mCurrentRow = 1;
                inRecRowSelection = 1;
                setNormalIcon();
                handlePlayTimeVisibility(4);
                mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
                mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                this.mTlShootModeController.setCaptureState(1);
                setSelectedOptionAEStatusRow();
                break;
            case R.id.still_movie_btn /* 2131362393 */:
                AppLog.trace("INSIDE", "still & movie");
                mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE;
                removeFocusFromAll();
                mCurrentRow = 1;
                inRecRowSelection = 2;
                handlePlayTimeVisibility(0);
                setNormalIcon();
                mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
                mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                this.mTlShootModeController.setCaptureState(2);
                setSelectedOptionAEStatusRow();
                break;
            case R.id.ael_ael /* 2131362437 */:
                AppLog.trace("INSIDE", "aestatus aelock");
                removeFocusFromAll();
                mCurrentRow = 4;
                inAEStatusRowSelection = 0;
                setNormalIcon();
                mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
            case R.id.ael_tracking_image /* 2131362440 */:
                AppLog.trace("INSIDE", "aestatus low");
                removeFocusFromAll();
                mCurrentRow = 4;
                inAEStatusRowSelection = 4;
                setNormalIcon();
                mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
            case R.id.ael_tracking_low /* 2131362441 */:
                AppLog.trace("INSIDE", "aestatus low");
                removeFocusFromAll();
                mCurrentRow = 4;
                inAEStatusRowSelection = 3;
                setNormalIcon();
                mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
            case R.id.ael_tracking_mid /* 2131362443 */:
                AppLog.trace("INSIDE", "aestatus mid");
                removeFocusFromAll();
                mCurrentRow = 4;
                inAEStatusRowSelection = 2;
                setNormalIcon();
                mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
            case R.id.ael_tracking_high /* 2131362445 */:
                AppLog.trace("INSIDE", "aestatus high");
                removeFocusFromAll();
                mCurrentRow = 4;
                inAEStatusRowSelection = 1;
                setNormalIcon();
                mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
        }
        if (!hasFocusOnInterval() && !hasFocusOnShots()) {
            makeViewsUnfocusable();
        }
        setOptionNameNormal();
        setOptionNameSelected();
        setFooterGuide();
        pushedCenterKey();
    }

    private void setNormalIcon() {
        setFirstRowNormal();
        mIntervalView.setVisibility(4);
        mShotsView.setVisibility(4);
        setFifthRowNormal();
    }

    private void setValidImage() {
        mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_normal);
        mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_normal);
        mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_normal);
        mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_normal);
        mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_normal);
        mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        }
    }

    private void setDisabledImage() {
        mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
        mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
        mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
        mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
        mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_disable);
        mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        }
    }

    private void setFirstRowNormal() {
        mMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_normal);
        mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_normal);
        mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_normal);
        mMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
    }

    private void setFifthRowNormal() {
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            setValidImage();
        } else {
            setDisabledImage();
        }
    }

    private void getLastStoredValues() {
        mFileFormat = this.mTLCommonUtil.getFileFormat();
        mAEStatus = this.mTLCommonUtil.getAETrakingStatus();
        mInterval = this.mTLCommonUtil.getInterval();
        mShootingNumber = this.mTLCommonUtil.getShootingNum();
        mOldFileFormat = mFileFormat;
        mOldAEStatus = mAEStatus;
        mOldInterval = mInterval;
        mOldShootingNumber = mShootingNumber;
        inRecRowSelection = this.mTlShootModeController.getCurrentCaptureState();
        inAEStatusRowSelection = mAEStatus;
        if ((mLastFocusedView != null && mLastFocusedView.getId() == R.id.tl_reset_button) || mCurrentRow == 0 || isLastFocusedItemFromAERow()) {
            mLastFocusedView = mMovieBtn;
            mCurrentRow = 1;
        }
    }

    private boolean isLastFocusedItemFromAERow() {
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption() || mLastFocusedView == null) {
            return false;
        }
        switch (mLastFocusedView.getId()) {
            case R.id.ael_ael /* 2131362437 */:
            case R.id.ael_tracking_image /* 2131362440 */:
            case R.id.ael_tracking_low /* 2131362441 */:
            case R.id.ael_tracking_mid /* 2131362443 */:
            case R.id.ael_tracking_high /* 2131362445 */:
                return true;
            case R.id.ael_ael_bg /* 2131362438 */:
            case R.id.ael_tracking_image_bg /* 2131362439 */:
            case R.id.ael_tracking_low_bg /* 2131362442 */:
            case R.id.ael_tracking_mid_bg /* 2131362444 */:
            default:
                return false;
        }
    }

    private void resetDefaultValues() {
        mFileFormat = "MOVIE_FORMAT";
        mAEStatus = 0;
        mInterval = 1;
        mShootingNumber = 240;
        inAEStatusRowSelection = mAEStatus;
        inRecRowSelection = 0;
        restoreDefaultSelection();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        restoreDefaultSelection();
        setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_normal);
        cancelSetValue();
        restoreOldValues();
        openPreviousMenu();
        deinitializeView();
        return 1;
    }

    private void restoreOldValues() {
        mFileFormat = mOldFileFormat;
        mInterval = mOldInterval;
        inAEStatusRowSelection = mOldAEStatus;
        mShootingNumber = mOldShootingNumber;
    }

    private void resetThemeProperty() {
        resetDefaultValues();
        handlePlayTimeVisibility(0);
        setFocusableView(mResetImageBtn);
        setDefaultSelectValue();
    }

    private void setDefaultSelectValue() {
        int selectorBgResourceID = R.drawable.p_16_dd_parts_tm_option_button_select;
        switch (inRecRowSelection) {
            case 1:
                mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                break;
            case 2:
                mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                break;
            default:
                mMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                break;
        }
        if (!this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            selectorBgResourceID = R.drawable.p_16_dd_parts_tm_option_button_disable;
        }
        switch (inAEStatusRowSelection) {
            case 1:
                mAelTrackHighBg.setBackgroundResource(selectorBgResourceID);
                return;
            case 2:
                mAelTrackMidBg.setBackgroundResource(selectorBgResourceID);
                return;
            case 3:
                mAelTrackLowBg.setBackgroundResource(selectorBgResourceID);
                return;
            case 4:
                mAelTrackingBg.setBackgroundResource(selectorBgResourceID);
                return;
            default:
                mAeLockBg.setBackgroundResource(selectorBgResourceID);
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (mCurrentRow == 0) {
            return -1;
        }
        if (mCurrentRow == 2) {
            if (hasFocusOnInterval()) {
                mHasFocusOnInterval = false;
                setNormalInterval();
                mIntervalView.setVisibility(0);
                setFocusableView(mIntervalView);
            } else {
                mHasFocusOnInterval = true;
                mIntervalView.setVisibility(4);
                mInterval10min.setBackgroundResource(R.drawable.state_btn_custom);
                mInterval1min.setBackgroundResource(R.drawable.state_btn_custom);
                mInterval10sec.setBackgroundResource(R.drawable.state_btn_custom);
                mInterval1sec.setBackgroundResource(R.drawable.state_btn_custom);
                setFocusableInterval1sec();
            }
        } else if (mCurrentRow == 3) {
            if (hasFocusOnShots()) {
                mHasFocusOnShots = false;
                setNormalShots();
                mShotsView.setVisibility(0);
                setFocusableView(mShotsView);
            } else {
                mHasFocusOnShots = true;
                mShotsView.setVisibility(4);
                mShots1000.setBackgroundResource(R.drawable.state_btn_custom);
                mShots100.setBackgroundResource(R.drawable.state_btn_custom);
                mShots10.setBackgroundResource(R.drawable.state_btn_custom);
                mShots1.setBackgroundResource(R.drawable.state_btn_custom);
                setFocusableShots1();
            }
        } else {
            if (mCurrentRow == 4 && !this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME);
                return -1;
            }
            if (this.data != null && parcelable.getPreviousMenuLayoutId().equals("ID_TLTHEMESELECTIONLAYOUT")) {
                openPreviousMenu();
                deinitializeView();
            } else {
                closeMenuLayout(null);
            }
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        handleRightAction();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        handleLeftAction();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (mCurrentRow == 3) {
            if (TLCommonUtil.getInstance().isTimelapseLiteApplication()) {
                return -1;
            }
            if (!this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_INVALID_THEME);
            }
        }
        if (!hasFocusOnInterval() && !hasFocusOnShots()) {
            removeFocusFromAll();
            if (mCurrentRow < 4) {
                mCurrentRow++;
            }
        }
        switch (mCurrentRow) {
            case 1:
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_normal);
                setFirstRowNormal();
                switch (inRecRowSelection) {
                    case 0:
                        setFocusableView(mMovieBtn);
                        mMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
                        mMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 1:
                        setFocusableView(mStillBtn);
                        mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
                        mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 2:
                        setFocusableView(mStillMovieBtn);
                        mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
                        mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        setSelectedOptionAEStatusRow();
                        break;
                }
            case 2:
                if (hasFocusOnInterval()) {
                    decrementIntervalValue();
                    break;
                } else {
                    setSelectedImageOnRecording();
                    setSelectedOptionAEStatusRow();
                    mIntervalView.setVisibility(0);
                    setFocusableView(mIntervalView);
                    break;
                }
            case 3:
                if (hasFocusOnShots()) {
                    decrementShotsValue();
                    break;
                } else {
                    setSelectedImageOnRecording();
                    mIntervalView.setVisibility(4);
                    setSelectedOptionAEStatusRow();
                    mShotsView.setVisibility(0);
                    setFocusableView(mShotsView);
                    break;
                }
            case 4:
                mShotsView.setVisibility(4);
                setSelectedImageOnRecording();
                setSelectedBtnOnAEStatusRow();
                break;
        }
        setOptionNameNormal();
        setOptionNameSelected();
        setFooterGuide();
        return 1;
    }

    private void setResetButtonBackground(int resetButtonID) {
        mResetImageBtn.setImageResource(resetButtonID);
    }

    private void setFocusableView(View view) {
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.requestFocus();
        view.setSelected(true);
        mLastFocusedView = view;
    }

    private void clearFocusedView(View view) {
        view.clearFocus();
        view.setFocusable(false);
        view.setSelected(false);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (!hasFocusOnInterval() && !hasFocusOnShots()) {
            removeFocusFromAll();
            if (mCurrentRow > 0) {
                mCurrentRow--;
            }
        }
        switch (mCurrentRow) {
            case 0:
                setSelectedImageOnRecording();
                setSelectedOptionAEStatusRow();
                setFocusableView(mResetImageBtn);
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_focused);
                break;
            case 1:
                setFirstRowNormal();
                mIntervalView.setVisibility(4);
                switch (inRecRowSelection) {
                    case 0:
                        setFocusableView(mMovieBtn);
                        mMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
                        mMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 1:
                        setFocusableView(mStillBtn);
                        mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
                        mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 2:
                        setFirstRowNormal();
                        setFocusableView(mStillMovieBtn);
                        mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
                        mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                }
            case 2:
                if (hasFocusOnInterval()) {
                    incrementIntervalValue();
                    break;
                } else {
                    mShotsView.setVisibility(4);
                    mIntervalView.setVisibility(0);
                    setFocusableView(mIntervalView);
                    break;
                }
            case 3:
                if (hasFocusOnShots()) {
                    incrementShotsValue();
                    break;
                } else {
                    setSelectedImageOnRecording();
                    setSelectedOptionAEStatusRow();
                    mShotsView.setVisibility(0);
                    setFocusableView(mShotsView);
                    break;
                }
        }
        setOptionNameNormal();
        setOptionNameSelected();
        setFooterGuide();
        return 1;
    }

    private void setSelectedOptionAEStatusRow() {
        setFifthRowNormal();
        int bgResourceID = R.drawable.p_16_dd_parts_tm_option_button_disable;
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            bgResourceID = R.drawable.p_16_dd_parts_tm_option_button_select;
        }
        switch (inAEStatusRowSelection) {
            case 1:
                mAelTrackHighBg.setBackgroundResource(bgResourceID);
                return;
            case 2:
                mAelTrackMidBg.setBackgroundResource(bgResourceID);
                return;
            case 3:
                mAelTrackLowBg.setBackgroundResource(bgResourceID);
                return;
            case 4:
                mAelTrackingBg.setBackgroundResource(bgResourceID);
                return;
            default:
                mAeLockBg.setBackgroundResource(bgResourceID);
                return;
        }
    }

    private void setSelectedBtnOnAEStatusRow() {
        switch (inAEStatusRowSelection) {
            case 0:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_focused);
                } else {
                    mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
                }
                setFocusableView(mAeLock);
                mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 1:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_focused);
                } else {
                    mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
                }
                mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setFocusableView(mAelTrackHigh);
                return;
            case 2:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_focused);
                } else {
                    mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
                }
                mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setFocusableView(mAelTrackMid);
                return;
            case 3:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_focused);
                } else {
                    mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
                }
                mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setFocusableView(mAelTrackLow);
                return;
            case 4:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_focused);
                } else {
                    mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_disable);
                }
                mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setFocusableView(mAelTracking);
                return;
            default:
                return;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToLeft(KeyEvent event) {
        handleLeftAction();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToRight(KeyEvent event) {
        handleRightAction();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToLeft(KeyEvent event) {
        handleLeftAction();
        return super.onDial1TurnedToLeft(event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToRight(KeyEvent event) {
        handleRightAction();
        return super.onDial1TurnedToRight(event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToLeft(KeyEvent event) {
        handleLeftAction();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToRight(KeyEvent event) {
        handleRightAction();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToLeft(KeyEvent event) {
        handleLeftAction();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToRight(KeyEvent event) {
        handleRightAction();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        return "ID_TLCUSTOMTHEMEOPTIONLAYOUT";
    }

    private void handleRightAction() {
        if (mStillBtn.hasFocus()) {
            inRecRowSelection = 2;
            setFirstRowNormal();
            clearFocusedView(mStillBtn);
            setFocusableView(mStillMovieBtn);
            mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
            mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            this.mTlShootModeController.setCaptureState(2);
            mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE;
            handlePlayTimeVisibility(0);
            TLCommonUtil.getInstance().setJpegDummyQuality();
            return;
        }
        if (mMovieBtn.hasFocus()) {
            inRecRowSelection = 1;
            setFirstRowNormal();
            clearFocusedView(mMovieBtn);
            setFocusableView(mStillBtn);
            mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
            mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL;
            handlePlayTimeVisibility(4);
            this.mTlShootModeController.setCaptureState(1);
            TLCommonUtil.getInstance().setJpegDummyQuality();
            return;
        }
        if (mAeLock.hasFocus()) {
            AppLog.info("INSIDE------->", "Right Ael---------->");
            setFifthRowNormal();
            clearFocusedView(mAeLock);
            setNextFocusOnRightAEPF();
            return;
        }
        if (mAelTrackLow.hasFocus()) {
            AppLog.info("INSIDE------->", "Right Low---------->");
            inAEStatusRowSelection = 2;
            setFifthRowNormal();
            clearFocusedView(mAelTrackLow);
            setFocusableView(mAelTrackMid);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_focused);
                mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_normal);
            } else {
                mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
                mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
            }
            mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (mAelTrackMid.hasFocus()) {
            AppLog.info("INSIDE------->", "Right Mid---------->");
            inAEStatusRowSelection = 1;
            setFifthRowNormal();
            clearFocusedView(mAelTrackMid);
            setFocusableView(mAelTrackHigh);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_focused);
                mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_normal);
            } else {
                mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
                mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
            }
            mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (!mIntervalView.hasFocus()) {
            if (mInterval10min.hasFocus()) {
                setFocusableInterval1min();
                return;
            }
            if (mInterval1min.hasFocus()) {
                setFocusableInterval10sec();
                return;
            }
            if (mInterval10sec.hasFocus()) {
                setFocusableInterval1sec();
                return;
            }
            if (mInterval1sec.hasFocus()) {
                setFocusableInterval10min();
                return;
            }
            if (!mShotsView.hasFocus()) {
                if (mShots1000.hasFocus()) {
                    setFocusableShots100();
                    return;
                }
                if (mShots100.hasFocus()) {
                    setFocusableShots10();
                } else if (mShots10.hasFocus()) {
                    setFocusableShots1();
                } else if (mShots1.hasFocus()) {
                    setFocusableShots1000();
                }
            }
        }
    }

    private void setNextFocusOnRightAEPF() {
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            setFocusableView(mAelTracking);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_focused);
                mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_normal);
            } else {
                mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_disable);
                mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
            }
            mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            inAEStatusRowSelection = 4;
            return;
        }
        setFocusableView(mAelTrackLow);
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_focused);
            mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_normal);
        } else {
            mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
            mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
        }
        mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        inAEStatusRowSelection = 3;
    }

    private void handleLeftAction() {
        if (mStillMovieBtn.hasFocus()) {
            inRecRowSelection = 1;
            setFirstRowNormal();
            clearFocusedView(mStillMovieBtn);
            setFocusableView(mStillBtn);
            mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
            mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL;
            this.mTlShootModeController.setCaptureState(1);
            handlePlayTimeVisibility(4);
            TLCommonUtil.getInstance().setJpegDummyQuality();
            return;
        }
        if (mStillBtn.hasFocus()) {
            inRecRowSelection = 0;
            setFirstRowNormal();
            clearFocusedView(mStillBtn);
            setFocusableView(mMovieBtn);
            mMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
            mMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            mFileFormat = "MOVIE_FORMAT";
            this.mTlShootModeController.setCaptureState(0);
            handlePlayTimeVisibility(0);
            TLCommonUtil.getInstance().setJpegDummyQuality();
            return;
        }
        if (mAelTracking.hasFocus()) {
            AppLog.info("INSIDE------->", "Left Low---------->");
            inAEStatusRowSelection = 0;
            setFifthRowNormal();
            clearFocusedView(mAelTracking);
            setFocusableView(mAeLock);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_focused);
                mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_normal);
            } else {
                mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
                mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_disable);
            }
            mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (mAelTrackLow.hasFocus()) {
            AppLog.info("INSIDE------->", "Left Lcok---------->");
            inAEStatusRowSelection = 0;
            setFifthRowNormal();
            clearFocusedView(mAelTrackLow);
            setFocusableView(mAeLock);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_focused);
                mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_normal);
            } else {
                mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
                mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
            }
            mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (mAelTrackMid.hasFocus()) {
            AppLog.info("INSIDE------->", "left Mid---------->");
            inAEStatusRowSelection = 3;
            setFifthRowNormal();
            clearFocusedView(mAelTrackMid);
            setFocusableView(mAelTrackLow);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_focused);
                mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_normal);
            } else {
                mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
                mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
            }
            mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (mAelTrackHigh.hasFocus()) {
            AppLog.info("INSIDE------->", "left High---------->");
            inAEStatusRowSelection = 2;
            setFifthRowNormal();
            clearFocusedView(mAelTrackHigh);
            setFocusableView(mAelTrackMid);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_focused);
                mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_normal);
            } else {
                mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
                mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
            }
            mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (!mIntervalView.hasFocus()) {
            if (mInterval10min.hasFocus()) {
                setFocusableInterval1sec();
                return;
            }
            if (mInterval1min.hasFocus()) {
                setFocusableInterval10min();
                return;
            }
            if (mInterval10sec.hasFocus()) {
                setFocusableInterval1min();
                return;
            }
            if (mInterval1sec.hasFocus()) {
                setFocusableInterval10sec();
                return;
            }
            if (!mShotsView.hasFocus()) {
                if (mShots1000.hasFocus()) {
                    setFocusableShots1();
                    return;
                }
                if (mShots100.hasFocus()) {
                    setFocusableShots1000();
                } else if (mShots10.hasFocus()) {
                    setFocusableShots100();
                } else if (mShots1.hasFocus()) {
                    setFocusableShots10();
                }
            }
        }
    }

    private void makeViewsUnfocusable() {
        if (getView() != null) {
            getView().setFocusable(false);
            getView().setFocusableInTouchMode(false);
        }
        for (int i = 0; i < mViewArray.length - 1; i++) {
            mViewArray[i].setFocusable(false);
            mViewArray[i].setFocusableInTouchMode(false);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        if (!isFunctionGuideShown()) {
            setFocusOnResume();
        }
        return super.pushedGuideFuncKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (!isFunctionGuideShown()) {
            setFocusOnResume();
            int ret = super.onKeyDown(keyCode, event);
            return ret;
        }
        int ret2 = super.onKeyDown(keyCode, event);
        setFocusOnResume();
        return ret2;
    }

    private void setFocusOnResume() {
        if (mLastFocusedView != null) {
            setFocusableView(mLastFocusedView);
        }
    }

    private void setSelectedImageOnRecording() {
        setFirstRowNormal();
        switch (inRecRowSelection) {
            case 0:
                mMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 1:
                mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 2:
                mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            default:
                return;
        }
    }

    private void setOptionNameSelected() {
        switch (mCurrentRow) {
            case 1:
                mRecordingModeName.setTextColor(Color.parseColor("#FF8000"));
                return;
            case 2:
                mIntervalName.setTextColor(Color.parseColor("#FF8000"));
                return;
            case 3:
                mShootingNumberName.setTextColor(Color.parseColor("#FF8000"));
                return;
            case 4:
                mAEStatusName.setTextColor(Color.parseColor("#FF8000"));
                return;
            default:
                return;
        }
    }

    private void setOptionNameNormal() {
        mRecordingModeName.setTextColor(Color.parseColor("#FFFFFF"));
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            mAEStatusName.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            mAEStatusName.setTextColor(Color.parseColor("#808080"));
        }
        mIntervalName.setTextColor(Color.parseColor("#FFFFFF"));
        mShootingNumberName.setTextColor(Color.parseColor("#FFFFFF"));
        setBottomValuesNormal();
    }

    private void setBottomValuesNormal() {
        mIntervalValue.setTextColor(Color.parseColor("#FFFFFF"));
        mShootingNumberValue.setTextColor(Color.parseColor("#FFFFFF"));
        mShootingTimeTitle.setTextColor(Color.parseColor("#FFFFFF"));
        mShootingTimeValue.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private String getPlackBackDuration() {
        int playTime = 0;
        if (mFileFormat.equalsIgnoreCase("MOVIE_FORMAT") || mFileFormat.equalsIgnoreCase(TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE)) {
            if ("framerate-24p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
                playTime = mShootingNumber / 24;
            } else if ("framerate-30p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
                playTime = mShootingNumber / 30;
            }
        }
        return String.format(mSecondString, Integer.valueOf(playTime));
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            mAEStatus = 0;
        } else {
            mAEStatus = inAEStatusRowSelection;
        }
        if (MIN_INTERVAL > mInterval) {
            mInterval = MIN_INTERVAL;
        }
        if (isMaxIntervalLimit && MAX_INTERVAL < mInterval) {
            mInterval = MAX_INTERVAL;
        }
        this.mTLCommonUtil.saveChangedValues(mFileFormat, mAEStatus, mInterval, mShootingNumber);
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        deinitializeView();
        TLCommonUtil.getInstance().setJpegDummyQuality();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deinitializeView();
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        deinitializeView();
        super.onDestroy();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    private void deinitializeView() {
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mService = null;
        mIntervalName = null;
        mRecordingModeName = null;
        mShootingNumberName = null;
        mAEStatusName = null;
        this.mCurrentView = null;
        mIntervalValue = null;
        mShootingNumberValue = null;
        mShootingTimeTitle = null;
        mShootingTimeValue = null;
        mThemeName = null;
        mPlayBackTextTime = null;
        mHasFocusOnInterval = false;
        mHasFocusOnShots = false;
        releaseImageViewDrawable(mPlayBackImageBtn);
        releaseImageViewDrawable(mFrameRateImage);
        releaseImageViewDrawable(mMovieBtn);
        releaseImageViewDrawable(mStillBtn);
        releaseImageViewDrawable(mStillMovieBtn);
        releaseImageViewDrawable(mMovieBtnBg);
        releaseImageViewDrawable(mStillBtnBg);
        releaseImageViewDrawable(mStillMovieBtnBg);
        releaseImageViewDrawable(mAeLock);
        releaseImageViewDrawable(mAelTrackHigh);
        releaseImageViewDrawable(mAelTrackMid);
        releaseImageViewDrawable(mAelTrackLow);
        releaseImageViewDrawable(mAelTracking);
        releaseImageViewDrawable(mAeLockBg);
        releaseImageViewDrawable(mAelTrackHighBg);
        releaseImageViewDrawable(mAelTrackMidBg);
        releaseImageViewDrawable(mAelTrackLowBg);
        releaseImageViewDrawable(mAelTrackingBg);
        mInterval10min = null;
        mInterval1min = null;
        mInterval10sec = null;
        mInterval1sec = null;
        mInterval10minVal = null;
        mInterval1minVal = null;
        mInterval10secVal = null;
        mInterval1secVal = null;
        mIntervalView = null;
        mInterval10minUp = null;
        mInterval1minUp = null;
        mInterval10secUp = null;
        mInterval1secUp = null;
        mInterval10minDown = null;
        mInterval1minDown = null;
        mInterval10secDown = null;
        mInterval1secDown = null;
        mShots1000 = null;
        mShots100 = null;
        mShots10 = null;
        mShots1 = null;
        mShots1000Val = null;
        mShots100Val = null;
        mShots10Val = null;
        mShots1Val = null;
        mShotsView = null;
        mShots1000Up = null;
        mShots100Up = null;
        mShots10Up = null;
        mShots1Up = null;
        mShots1000Down = null;
        mShots100Down = null;
        mShots10Down = null;
        mShots1Down = null;
        mFileFormat = null;
        mAEStatus = 0;
        mInterval = 0;
        mShootingNumber = 0;
        inRecRowSelection = 0;
        inAEStatusRowSelection = 0;
        mLayoutParamsAELock = null;
        mLayoutParamsAETracking = null;
        mLayoutParamsAELock = null;
        mLayoutParamsAETracking = null;
        mLayoutParamsAELockBg = null;
        mFooterGuide = null;
        mOldFileFormat = null;
        mOldAEStatus = 0;
        mOldInterval = 0;
        mOldShootingNumber = 0;
        mResetImageBtn = null;
        this.mTLCommonUtil = null;
        mShootingList = null;
        mSecondString = null;
        if (mViewArray != null) {
            for (int i = 0; i < mViewArray.length - 1; i++) {
                if (mViewArray[i] != null) {
                    mViewArray[i].setOnClickListener(null);
                    mViewArray[i] = null;
                }
            }
            mViewArray = null;
        }
        this.mCurrentView = null;
        System.gc();
    }

    public int getShootingNumInPlus(int progress) {
        AppLog.info("INSIDE", "getShootingNumInRange");
        int rangeProgress = 1;
        for (int i = 0; i < mShootingList.size() - 1; i++) {
            if (progress <= 1) {
                rangeProgress = 1;
            } else if (progress > mShootingList.get(i).intValue() && progress <= mShootingList.get(i + 1).intValue()) {
                rangeProgress = mShootingList.get(i + 1).intValue();
            }
        }
        return rangeProgress;
    }

    public int getShootingNumInMinus(int progress) {
        AppLog.info("INSIDE", "getShootingNumInRange");
        int rangeProgress = 1;
        for (int i = 0; i < mShootingList.size() - 1; i++) {
            if (progress <= 1) {
                rangeProgress = 1;
            } else if (progress > mShootingList.get(i).intValue() && progress <= mShootingList.get(i + 1).intValue()) {
                rangeProgress = mShootingList.get(i).intValue();
            }
        }
        return rangeProgress;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        switch (mCurrentRow) {
            case 0:
                setGuideResource(guideResources, R.string.STRID_AMC_STR_01117, R.string.STRID_FUNC_TIMELAPSE_SETTING_RESET_GUIDE, true);
                return;
            case 1:
                switch (inRecRowSelection) {
                    case 1:
                        setGuideResource(guideResources, R.string.STRID_AMC_STR_00947, R.string.STRID_FUNC_TIMELAPSE_RECMODE_JPEG_GUIDE, true);
                        return;
                    case 2:
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_STILL_MOVIE, R.string.STRID_FUNC_TIMELAPSE_RECMODE_STILL_MOVIE_GUIDE, true);
                        return;
                    default:
                        setGuideResource(guideResources, android.R.string.sim_added_message, R.string.STRID_FUNC_TIMELAPSE_RECMODE_MOVIE_GUIDE, true);
                        return;
                }
            case 2:
                setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_INTERVAL, R.string.STRID_FUNC_TIMELAPSE_INTERVAL_GUIDE, true);
                return;
            case 3:
                setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_IMAGENUM, R.string.STRID_FUNC_TIMELAPSE_IMAGENUM_GUIDE, true);
                return;
            case 4:
                switch (inAEStatusRowSelection) {
                    case 1:
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_HI, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_HI_GUIDE, true);
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_HI, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_HI_GUIDE_S, true);
                        return;
                    case 2:
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_MID, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_MID_GUIDE, true);
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_MID, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_MID_GUIDE_S, true);
                        return;
                    case 3:
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_LOW, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_LOW_GUIDE, true);
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_LOW, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_LOW_GUIDE_S, true);
                        return;
                    case 4:
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_GUIDE, true);
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING, R.string.STRID_FUNC_TIMELAPSE_AE_TRACKING_GUIDE_S, true);
                        return;
                    default:
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AEL, R.string.STRID_FUNC_TIMELAPSE_AEL_ON_GUIDE, true);
                        setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_AEL, R.string.STRID_FUNC_TIMELAPSE_AEL_ON_GUIDE2, true);
                        return;
                }
            default:
                return;
        }
    }

    private void setGuideResource(ArrayList<Object> guideResources, int guideTitleID, int guideDefi, boolean isAvailble) {
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(Boolean.valueOf(isAvailble));
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            return 1;
        }
        int returnState = super.onKeyUp(keyCode, event);
        return returnState;
    }

    private boolean hasFocusOnInterval() {
        return mHasFocusOnInterval;
    }

    private void setNormalInterval() {
        if (MIN_INTERVAL > mInterval) {
            mInterval = MIN_INTERVAL;
        }
        if (isMaxIntervalLimit && MAX_INTERVAL < mInterval) {
            mInterval = MAX_INTERVAL;
        }
        mInterval10min.setSelected(false);
        mInterval1min.setSelected(false);
        mInterval10sec.setSelected(false);
        mInterval1sec.setSelected(false);
        mInterval10min.setBackgroundResource(0);
        mInterval1min.setBackgroundResource(0);
        mInterval10sec.setBackgroundResource(0);
        mInterval1sec.setBackgroundResource(0);
        mInterval10minUp.setVisibility(4);
        mInterval1minUp.setVisibility(4);
        mInterval10secUp.setVisibility(4);
        mInterval1secUp.setVisibility(4);
        mInterval10minDown.setVisibility(4);
        mInterval1minDown.setVisibility(4);
        mInterval10secDown.setVisibility(4);
        mInterval1secDown.setVisibility(4);
        setIntervalText();
    }

    private void incrementIntervalValue() {
        if (mInterval10min.hasFocus()) {
            if (isMaxIntervalLimit) {
                if (MAX_INTERVAL != mInterval) {
                    mInterval = MAX_INTERVAL;
                } else {
                    mInterval = 0;
                }
            } else {
                mInterval += AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S;
                if ((mInterval / AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S) % 10 == 0) {
                    mInterval -= 6000;
                }
            }
        } else if (mInterval1min.hasFocus()) {
            mInterval += AEPF1_BG_WIDTH;
            if ((mInterval / AEPF1_BG_WIDTH) % 10 == 0) {
                mInterval -= 600;
            }
        } else if (mInterval10sec.hasFocus()) {
            mInterval += 10;
            if ((mInterval % AEPF1_BG_WIDTH) / 10 == 0) {
                mInterval -= 60;
            }
        } else if (mInterval1sec.hasFocus()) {
            mInterval++;
            if ((mInterval / 1) % 10 == 0) {
                mInterval -= 10;
            }
        }
        if (isMaxIntervalLimit && MAX_INTERVAL < mInterval) {
            mInterval -= MAX_INTERVAL;
        }
        setIntervalText();
        mShootingTimeValue.setText(this.mTLCommonUtil.getRecordingTime(mInterval * (mShootingNumber - 1)));
    }

    private void decrementIntervalValue() {
        int i = MIN_INTERVAL;
        if (mInterval10min.hasFocus()) {
            if (isMaxIntervalLimit) {
                if (MAX_INTERVAL != mInterval) {
                    mInterval = MAX_INTERVAL;
                } else {
                    mInterval = 0;
                }
            } else {
                mInterval -= AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S;
                if (mInterval < 0 || (mInterval / AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S) % 10 == 9) {
                    mInterval += 6000;
                }
            }
        } else if (mInterval1min.hasFocus()) {
            mInterval -= AEPF1_BG_WIDTH;
            if (mInterval < 0 || (mInterval / AEPF1_BG_WIDTH) % 10 == 9) {
                mInterval += AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S;
            }
        } else if (mInterval10sec.hasFocus()) {
            mInterval -= 10;
            if (mInterval < 0 || (mInterval % AEPF1_BG_WIDTH) / 10 == 5) {
                mInterval += AEPF1_BG_WIDTH;
            }
        } else if (mInterval1sec.hasFocus()) {
            mInterval--;
            if (mInterval < 0 || (mInterval / 1) % 10 == 9) {
                mInterval += 10;
            }
        }
        if (isMaxIntervalLimit && MAX_INTERVAL < mInterval) {
            mInterval -= MAX_INTERVAL;
        }
        setIntervalText();
        setShotsText();
    }

    private void setIntervalText() {
        int min = mInterval / AEPF1_BG_WIDTH;
        int sec = mInterval % AEPF1_BG_WIDTH;
        int min10 = min / 10;
        int min1 = min % 10;
        int sec10 = sec / 10;
        int sec1 = sec % 10;
        String sMin10 = "" + min10;
        String sMin1 = "" + min1;
        String sSec10 = "" + sec10;
        String sSec1 = "" + sec1;
        mInterval10minVal.setText(sMin10);
        mInterval1minVal.setText(sMin1);
        mInterval10secVal.setText(sSec10);
        mInterval1secVal.setText(sSec1);
        if (mInterval != 0) {
            mIntervalValue.setText(this.mTLCommonUtil.getTimeString(mInterval, this));
        } else {
            mIntervalValue.setText(this.mTLCommonUtil.getTimeString(1, this));
        }
    }

    private void setFocusableInterval10min() {
        mInterval10min.setSelected(true);
        mInterval1min.setSelected(false);
        mInterval10sec.setSelected(false);
        mInterval1sec.setSelected(false);
        mInterval10minUp.setVisibility(0);
        mInterval1minUp.setVisibility(4);
        mInterval10secUp.setVisibility(4);
        mInterval1secUp.setVisibility(4);
        mInterval10minDown.setVisibility(0);
        mInterval1minDown.setVisibility(4);
        mInterval10secDown.setVisibility(4);
        mInterval1secDown.setVisibility(4);
        setFocusableView(mInterval10min);
    }

    private void setFocusableInterval1min() {
        mInterval10min.setSelected(false);
        mInterval1min.setSelected(true);
        mInterval10sec.setSelected(false);
        mInterval1sec.setSelected(false);
        mInterval10minUp.setVisibility(4);
        mInterval1minUp.setVisibility(0);
        mInterval10secUp.setVisibility(4);
        mInterval1secUp.setVisibility(4);
        mInterval10minDown.setVisibility(4);
        mInterval1minDown.setVisibility(0);
        mInterval10secDown.setVisibility(4);
        mInterval1secDown.setVisibility(4);
        setFocusableView(mInterval1min);
    }

    private void setFocusableInterval10sec() {
        mInterval10min.setSelected(false);
        mInterval1min.setSelected(false);
        mInterval10sec.setSelected(true);
        mInterval1sec.setSelected(false);
        mInterval10minUp.setVisibility(4);
        mInterval1minUp.setVisibility(4);
        mInterval10secUp.setVisibility(0);
        mInterval1secUp.setVisibility(4);
        mInterval10minDown.setVisibility(4);
        mInterval1minDown.setVisibility(4);
        mInterval10secDown.setVisibility(0);
        mInterval1secDown.setVisibility(4);
        setFocusableView(mInterval10sec);
    }

    private void setFocusableInterval1sec() {
        mInterval10min.setSelected(false);
        mInterval1min.setSelected(false);
        mInterval10sec.setSelected(false);
        mInterval1sec.setSelected(true);
        mInterval10minUp.setVisibility(4);
        mInterval1minUp.setVisibility(4);
        mInterval10secUp.setVisibility(4);
        mInterval1secUp.setVisibility(0);
        mInterval10minDown.setVisibility(4);
        mInterval1minDown.setVisibility(4);
        mInterval10secDown.setVisibility(4);
        mInterval1secDown.setVisibility(0);
        setFocusableView(mInterval1sec);
    }

    private boolean hasFocusOnShots() {
        return mHasFocusOnShots;
    }

    private void setNormalShots() {
        if (MIN_SHOTS > mShootingNumber) {
            mShootingNumber = MIN_SHOTS;
        }
        mShots1000.setSelected(false);
        mShots100.setSelected(false);
        mShots10.setSelected(false);
        mShots1.setSelected(false);
        mShots1000.setBackgroundResource(0);
        mShots100.setBackgroundResource(0);
        mShots10.setBackgroundResource(0);
        mShots1.setBackgroundResource(0);
        mShots1000Up.setVisibility(4);
        mShots100Up.setVisibility(4);
        mShots10Up.setVisibility(4);
        mShots1Up.setVisibility(4);
        mShots1000Down.setVisibility(4);
        mShots100Down.setVisibility(4);
        mShots10Down.setVisibility(4);
        mShots1Down.setVisibility(4);
        setShotsText();
    }

    private void incrementShotsValue() {
        int incrementVal = 1;
        if (mShots1000.hasFocus()) {
            incrementVal = 1000;
        } else if (mShots100.hasFocus()) {
            incrementVal = 100;
        } else if (mShots10.hasFocus()) {
            incrementVal = 10;
        } else if (mShots1.hasFocus()) {
            incrementVal = 1;
        }
        mShootingNumber += incrementVal;
        if ((mShootingNumber / incrementVal) % 10 == 0) {
            mShootingNumber -= incrementVal * 10;
        }
        setShotsText();
    }

    private void decrementShotsValue() {
        int decrementVal = MIN_SHOTS;
        if (mShots1000.hasFocus()) {
            decrementVal = 1000;
        } else if (mShots100.hasFocus()) {
            decrementVal = 100;
        } else if (mShots10.hasFocus()) {
            decrementVal = 10;
        } else if (mShots1.hasFocus()) {
            decrementVal = 1;
        }
        mShootingNumber -= decrementVal;
        if (mShootingNumber < 0 || (mShootingNumber / decrementVal) % 10 == 9) {
            mShootingNumber += decrementVal * 10;
        }
        setShotsText();
    }

    private void setShotsText() {
        int shootingNumber = mShootingNumber;
        int shots1 = shootingNumber % 10;
        int shootingNumber2 = shootingNumber / 10;
        int shots10 = shootingNumber2 % 10;
        int shootingNumber3 = shootingNumber2 / 10;
        int shots100 = shootingNumber3 % 10;
        int shots1000 = (shootingNumber3 / 10) % 10;
        String sShot1000 = "" + shots1000;
        String sShot100 = "" + shots100;
        String sShot10 = "" + shots10;
        String sShot1 = "" + shots1;
        mShots1000Val.setText(sShot1000);
        mShots100Val.setText(sShot100);
        mShots10Val.setText(sShot10);
        mShots1Val.setText(sShot1);
        int shootingNumber4 = mShootingNumber;
        if (mShootingNumber == 0) {
            mShootingNumber = 1;
        }
        int intervalVal = mInterval;
        if (mInterval == 0) {
            mInterval = 1;
        }
        mShootingNumberValue.setText(mShootingNumber + "");
        mShootingTimeValue.setText(this.mTLCommonUtil.getRecordingTime(mInterval * (mShootingNumber - 1)));
        mPlayBackTextTime.setText("" + getPlackBackDuration());
        mShootingNumber = shootingNumber4;
        mInterval = intervalVal;
    }

    private void setFocusableShots1000() {
        mShots1000.setSelected(true);
        mShots100.setSelected(false);
        mShots10.setSelected(false);
        mShots1.setSelected(false);
        mShots1000Up.setVisibility(0);
        mShots100Up.setVisibility(4);
        mShots10Up.setVisibility(4);
        mShots1Up.setVisibility(4);
        mShots1000Down.setVisibility(0);
        mShots100Down.setVisibility(4);
        mShots10Down.setVisibility(4);
        mShots1Down.setVisibility(4);
        setFocusableView(mShots1000);
    }

    private void setFocusableShots100() {
        mShots1000.setSelected(false);
        mShots100.setSelected(true);
        mShots10.setSelected(false);
        mShots1.setSelected(false);
        mShots1000Up.setVisibility(4);
        mShots100Up.setVisibility(0);
        mShots10Up.setVisibility(4);
        mShots1Up.setVisibility(4);
        mShots1000Down.setVisibility(4);
        mShots100Down.setVisibility(0);
        mShots10Down.setVisibility(4);
        mShots1Down.setVisibility(4);
        setFocusableView(mShots100);
    }

    private void setFocusableShots10() {
        mShots1000.setSelected(false);
        mShots100.setSelected(false);
        mShots10.setSelected(true);
        mShots1.setSelected(false);
        mShots1000Up.setVisibility(4);
        mShots100Up.setVisibility(4);
        mShots10Up.setVisibility(0);
        mShots1Up.setVisibility(4);
        mShots1000Down.setVisibility(4);
        mShots100Down.setVisibility(4);
        mShots10Down.setVisibility(0);
        mShots1Down.setVisibility(4);
        setFocusableView(mShots10);
    }

    private void setFocusableShots1() {
        mShots1000.setSelected(false);
        mShots100.setSelected(false);
        mShots10.setSelected(false);
        mShots1.setSelected(true);
        mShots1000Up.setVisibility(4);
        mShots100Up.setVisibility(4);
        mShots10Up.setVisibility(4);
        mShots1Up.setVisibility(0);
        mShots1000Down.setVisibility(4);
        mShots100Down.setVisibility(4);
        mShots10Down.setVisibility(4);
        mShots1Down.setVisibility(0);
        setFocusableView(mShots1);
    }
}
