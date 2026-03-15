package com.sony.imaging.app.timelapse.menu.layout;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.controller.FrameRateController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TLThemeOptionLayout extends DisplayMenuItemsMenuLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final int AEPF1LOCK_BG_LEFTMARGIN = 378;
    private static final int AEPF1LOCK_LEFTMARGIN = 351;
    private static final int AEPF1TRACK_LEFTMARGIN = 475;
    private static final int AEPF1_BG_HIGHT = 7;
    private static final int AEPF1_BG_TOPMARGIN = 301;
    private static final int AEPF1_BG_WIDTH = 60;
    private static final int AEPF1_HIGHT = 48;
    private static final int AEPF1_TOPMARGIN = 258;
    private static final int AEPF1_WIDTH = 114;
    private static final int INTERVAL_LOW_LIMIT_STILL_MOVIE = 3;
    private static final int MAX_DOT_SIZE = 60;
    private static final int ROW_AESTATUS = 4;
    private static final int ROW_INTERVAL = 2;
    private static final int ROW_RECORDING = 1;
    private static final int ROW_RESET = 0;
    private static final int ROW_SHOT = 3;
    private static final int THEME_CLOUDY_SKY = 0;
    private static final int THEME_CUSTOM = 7;
    private static final int THEME_MINIATURE = 5;
    private static final int THEME_NIGHT_SCENE = 2;
    private static final int THEME_NIGHT_SKY = 1;
    private static final int THEME_STANDARD = 6;
    private static final int THEME_SUNRISE = 4;
    private static final int THEME_SUNSET = 3;
    private static TLShootModeSettingController mTlShootModeController;
    private int mAEStatus;
    private View mCurrentView;
    private int mInterval;
    private int mOldAEStatus;
    private int mOldInterval;
    private int mOldShootingNumber;
    private ArrayList<Integer> mShootingList;
    private int mShootingNumber;
    public TLCommonUtil mTLCommonUtil;
    private static int[] mShots = {1, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360, 390, 420, 450, 480, 510, AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL, 570, AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_AF_S, AppRoot.USER_KEYCODE.MODE_S, 660, 690, 720, 750, 780, 810, 840, 870, 900, 930, 960, 990};
    private static int mCurrentRow = 1;
    private static int mIntervalTmp = 0;
    private TextView mRecordingModeName = null;
    private TextView mAEStatusName = null;
    private TextView mIntervalName = null;
    private TextView mShootingNumberName = null;
    private TextView mIntervalValue = null;
    private TextView mShootingNumberValue = null;
    private TextView mShootingTimeTitle = null;
    private TextView mShootingTimeValue = null;
    private TextView mThemeName = null;
    private TextView mPlayBackTextTime = null;
    private ImageButton mResetImageBtn = null;
    private ImageView mPlayBackImageBtn = null;
    private ImageView mFrameRateImage = null;
    private ImageButton mMovieBtin = null;
    private ImageButton mStillBtn = null;
    private ImageButton mStillMovieBtn = null;
    private ImageButton mAeLock = null;
    private ImageButton mAelTracking = null;
    private ImageButton mAelTrackLow = null;
    private ImageButton mAelTrackMid = null;
    private ImageButton mAelTrackHigh = null;
    private ImageButton mMovieBtinBg = null;
    private ImageButton mStillBtnBg = null;
    private ImageButton mStillMovieBtnBg = null;
    private ImageButton mAeLockBg = null;
    private ImageButton mAelTrackingBg = null;
    private ImageButton mAelTrackLowBg = null;
    private ImageButton mAelTrackMidBg = null;
    private ImageButton mAelTrackHighBg = null;
    private SeekBar mIntervalBar = null;
    private SeekBar mShootingNumberBar = null;
    private View[] mViewArray = null;
    private View mLastFocusedView = null;
    private FooterGuide mFooterGuide = null;
    private String mFileFormat = null;
    private int inRecRowSelection = 0;
    private int inAEStatusRowSelection = 0;
    private String mOldFileFormat = null;
    private String mSecondString = null;
    private boolean isSetSeekBarNormal = false;
    private RelativeLayout.LayoutParams mLayoutParamsAELock = null;
    private RelativeLayout.LayoutParams mLayoutParamsAETracking = null;
    private RelativeLayout.LayoutParams mLayoutParamsAELockBg = null;
    private ImageView[] mDot = null;
    private int previousShootingNum = 1;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.tl_layout_menu_theme_option);
        }
        this.mService = new BaseMenuService(getActivity().getApplicationContext());
        return this.mCurrentView;
    }

    private void initializeView() {
        this.mRecordingModeName = (TextView) this.mCurrentView.findViewById(R.id.rec_mode_name);
        this.mAEStatusName = (TextView) this.mCurrentView.findViewById(R.id.ae_status_name);
        this.mIntervalName = (TextView) this.mCurrentView.findViewById(R.id.interval_name);
        this.mShootingNumberName = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_name);
        this.mIntervalValue = (TextView) this.mCurrentView.findViewById(R.id.interval_value);
        this.mShootingNumberValue = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_value);
        this.mShootingTimeTitle = (TextView) this.mCurrentView.findViewById(R.id.shooting_time_title);
        this.mShootingTimeValue = (TextView) this.mCurrentView.findViewById(R.id.shooting_time_value);
        this.mMovieBtin = (ImageButton) this.mCurrentView.findViewById(R.id.movie_btn);
        this.mResetImageBtn = (ImageButton) this.mCurrentView.findViewById(R.id.tl_reset_button);
        this.mStillBtn = (ImageButton) this.mCurrentView.findViewById(R.id.still_btn);
        this.mStillMovieBtn = (ImageButton) this.mCurrentView.findViewById(R.id.still_movie_btn);
        this.mAeLock = (ImageButton) this.mCurrentView.findViewById(R.id.ael_ael);
        this.mAelTracking = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_image);
        this.mAelTrackLow = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_low);
        this.mAelTrackMid = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_mid);
        this.mAelTrackHigh = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_high);
        this.mMovieBtinBg = (ImageButton) this.mCurrentView.findViewById(R.id.movie_btn_bg);
        this.mStillBtnBg = (ImageButton) this.mCurrentView.findViewById(R.id.still_btn_bg);
        this.mStillMovieBtnBg = (ImageButton) this.mCurrentView.findViewById(R.id.still_movie_btn_bg);
        this.mAeLockBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_ael_bg);
        this.mAelTrackingBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_image_bg);
        this.mAelTrackLowBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_low_bg);
        this.mAelTrackMidBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_mid_bg);
        this.mAelTrackHighBg = (ImageButton) this.mCurrentView.findViewById(R.id.ael_tracking_high_bg);
        this.mIntervalBar = (SeekBar) this.mCurrentView.findViewById(R.id.interval_bar);
        this.mShootingNumberBar = (SeekBar) this.mCurrentView.findViewById(R.id.shooting_num_bar);
        this.mPlayBackImageBtn = (ImageView) this.mCurrentView.findViewById(R.id.playback_image_view);
        this.mFrameRateImage = (ImageView) this.mCurrentView.findViewById(R.id.framerate);
        this.mPlayBackTextTime = (TextView) this.mCurrentView.findViewById(R.id.playback_value);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_theme_option);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE_SK));
        this.mThemeName = (TextView) this.mCurrentView.findViewById(R.id.theme_name);
        this.mViewArray = new View[]{this.mMovieBtin, this.mStillBtn, this.mStillMovieBtn, this.mAeLock, this.mAelTrackLow, this.mAelTrackMid, this.mAelTrackHigh, this.mAelTracking, this.mResetImageBtn, this.mIntervalBar, this.mShootingNumberBar};
    }

    private void initializeListenersForView() {
        for (int i = 0; i < this.mViewArray.length - 2; i++) {
            this.mViewArray[i].setOnClickListener(this);
        }
        this.mIntervalBar.setOnSeekBarChangeListener(this);
        this.mShootingNumberBar.setOnSeekBarChangeListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        initializeView();
        initializeDotImages();
        initialOperationOnResume();
        setAEViewVisibilityForPF();
        updatePFSpecificAEUI();
        if (!isFunctionGuideShown()) {
            setFocusOnResume();
        }
        mIntervalTmp = 0;
    }

    private void updatePFSpecificAEUI() {
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            if (this.mLayoutParamsAELock == null) {
                this.mLayoutParamsAELock = getRelativeLayout(AEPF1LOCK_LEFTMARGIN, AEPF1_TOPMARGIN, AEPF1_WIDTH, AEPF1_HIGHT);
            }
            this.mAeLock.setLayoutParams(this.mLayoutParamsAELock);
            if (this.mLayoutParamsAETracking == null) {
                this.mLayoutParamsAETracking = getRelativeLayout(AEPF1TRACK_LEFTMARGIN, AEPF1_TOPMARGIN, AEPF1_WIDTH, AEPF1_HIGHT);
            }
            this.mAelTracking.setLayoutParams(this.mLayoutParamsAETracking);
            if (this.mLayoutParamsAELockBg == null) {
                this.mLayoutParamsAELockBg = getRelativeLayout(AEPF1LOCK_BG_LEFTMARGIN, AEPF1_BG_TOPMARGIN, 60, 7);
            }
            this.mAeLockBg.setLayoutParams(this.mLayoutParamsAELockBg);
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
            this.mAelTrackHigh.setVisibility(4);
            this.mAelTrackMid.setVisibility(4);
            this.mAelTrackLow.setVisibility(4);
            this.mAelTrackHighBg.setVisibility(4);
            this.mAelTrackMidBg.setVisibility(4);
            this.mAelTrackLowBg.setVisibility(4);
            this.mAeLock.setVisibility(4);
            this.mAeLockBg.setVisibility(4);
            this.mAelTracking.setVisibility(4);
            this.mAelTrackingBg.setVisibility(4);
            this.mAEStatusName.setVisibility(4);
            return;
        }
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            this.mAelTrackHigh.setVisibility(4);
            this.mAelTrackMid.setVisibility(4);
            this.mAelTrackLow.setVisibility(4);
            this.mAelTrackHighBg.setVisibility(4);
            this.mAelTrackMidBg.setVisibility(4);
            this.mAelTrackLowBg.setVisibility(4);
            this.mAelTrackingBg.setVisibility(0);
            return;
        }
        this.mAelTrackHigh.setVisibility(0);
        this.mAelTrackMid.setVisibility(0);
        this.mAelTrackLow.setVisibility(0);
        this.mAelTrackHighBg.setVisibility(0);
        this.mAelTrackMidBg.setVisibility(0);
        this.mAelTrackLowBg.setVisibility(0);
        this.mAelTrackingBg.setVisibility(4);
    }

    private void initialOperationOnResume() {
        initializeListenersForView();
        initializeObjects();
        getLastStoredValues();
        releaseSelectionFromAll();
        makeViewsUnfocusable();
        restoreDefaultSelection();
        setDefaultSelectValue();
        handlePlayTimeVisibility(this.inRecRowSelection == 1 ? 4 : 0);
        this.mIntervalValue.setText(String.format(this.mSecondString, Integer.valueOf(this.mInterval)));
        setDots();
    }

    private void initializeObjects() {
        mTlShootModeController = TLShootModeSettingController.getInstance();
        this.mShootingList = new ArrayList<>();
        this.mSecondString = getResources().getString(R.string.STRID_FUNC_TIMELAPSE_INTERVAL_VALUE);
        this.mTLCommonUtil = TLCommonUtil.getInstance();
        this.mThemeName.setText(this.mTLCommonUtil.getSelectedThemeName());
        for (int i = 0; i < mShots.length; i++) {
            this.mShootingList.add(Integer.valueOf(mShots[i]));
        }
        this.mPlayBackImageBtn.setImageResource(R.drawable.p_16_dd_parts_tm_playbacktime_icon_small_nomal);
        if ("framerate-24p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
            this.mFrameRateImage.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        } else if ("framerate-30p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
            this.mFrameRateImage.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        }
    }

    private void handlePlayTimeVisibility(int visibilityState) {
        this.mPlayBackImageBtn.setVisibility(visibilityState);
        this.mPlayBackTextTime.setText("" + getPlackBackDuration());
        this.mPlayBackTextTime.setVisibility(visibilityState);
        this.mFrameRateImage.setVisibility(visibilityState);
    }

    private void restoreDefaultSelection() {
        removeFocusFromAll();
        setNormalIcon();
        setOptionNameNormal();
        setSeekbarNormal();
        switch (mCurrentRow) {
            case 0:
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_focused);
                break;
            case 1:
                switch (this.inRecRowSelection) {
                    case 1:
                        setSelectedItemOnResume(this.mStillBtn, R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
                        this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 2:
                        setSelectedItemOnResume(this.mStillMovieBtn, R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
                        this.mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    default:
                        setSelectedItemOnResume(this.mMovieBtin, R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
                        this.mMovieBtinBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                }
            case 2:
                setSeekbarSelected(0);
                setFocusableView(this.mIntervalBar);
                break;
            case 3:
                setSeekbarSelected(1);
                setFocusableView(this.mShootingNumberBar);
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
    }

    private void setDisabledFocusOnResume() {
        switch (this.inAEStatusRowSelection) {
            case 1:
                setSelectedItemOnResume(this.mAelTrackHigh, R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
                this.mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 2:
                setSelectedItemOnResume(this.mAelTrackMid, R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
                this.mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 3:
                setSelectedItemOnResume(this.mAelTrackLow, R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
                this.mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 4:
                setSelectedItemOnResume(this.mAelTracking, R.drawable.p_16_dd_parts_tm_tracking_disable);
                this.mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            default:
                setSelectedItemOnResume(this.mAeLock, R.drawable.p_16_dd_parts_tm_ael_disable);
                this.mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
        }
    }

    private void setValidFocuseOnResume() {
        switch (this.inAEStatusRowSelection) {
            case 1:
                setSelectedItemOnResume(this.mAelTrackHigh, R.drawable.p_16_dd_parts_tm_tracking_hi_focused);
                this.mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 2:
                setSelectedItemOnResume(this.mAelTrackMid, R.drawable.p_16_dd_parts_tm_tracking_mid_focused);
                this.mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 3:
                setSelectedItemOnResume(this.mAelTrackLow, R.drawable.p_16_dd_parts_tm_tracking_lo_focused);
                this.mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 4:
                setSelectedItemOnResume(this.mAelTracking, R.drawable.p_16_dd_parts_tm_tracking_focused);
                this.mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            default:
                setSelectedItemOnResume(this.mAeLock, R.drawable.p_16_dd_parts_tm_ael_focused);
                this.mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
        }
    }

    private void setSelectedItemOnResume(ImageButton view, int drawableID) {
        view.setImageResource(drawableID);
        setFocusableView(view);
    }

    private void releaseSelectionFromAll() {
        for (int i = 0; i < this.mViewArray.length - 2; i++) {
            this.mViewArray[i].setSelected(false);
        }
        setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_normal);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar bar, int progress, boolean arg2) {
        if (mCurrentRow == 3 || mCurrentRow == 2) {
            switch (bar.getId()) {
                case R.id.interval_bar /* 2131362447 */:
                    AppLog.trace("INSIDE", "interval bar");
                    if (progress < 1) {
                        progress = 1;
                        this.mInterval = 1;
                    }
                    this.mInterval = progress;
                    if (this.inRecRowSelection == 2 && this.mInterval < 3) {
                        this.mInterval = 3;
                    }
                    this.mIntervalBar.setProgress(this.mInterval);
                    this.mIntervalValue.setText(String.format(this.mSecondString, Integer.valueOf(this.mInterval)));
                    setDots();
                    break;
                case R.id.shooting_num_bar /* 2131362448 */:
                    if (progress < 1) {
                        this.mShootingNumber = 1;
                    } else if (!this.isSetSeekBarNormal) {
                        if (progress > this.previousShootingNum) {
                            this.mShootingNumber = getShootingNumInPlus(progress);
                        } else if (progress < this.previousShootingNum) {
                            this.mShootingNumber = getShootingNumInMinus(progress);
                        }
                    } else {
                        this.mShootingNumber = progress;
                        this.isSetSeekBarNormal = false;
                    }
                    this.previousShootingNum = this.mShootingNumber;
                    this.mShootingNumberBar.setProgress(this.mShootingNumber);
                    this.mShootingNumberValue.setText(this.mShootingNumber + "");
                    this.mPlayBackTextTime.setText("" + getPlackBackDuration());
                    break;
            }
            this.mShootingTimeValue.setText(getTotalRecTime());
            this.mTLCommonUtil.setShootingNum(this.mShootingNumber);
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekbar) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekbar) {
    }

    private void removeFocusFromAll() {
        for (int i = 0; i < this.mViewArray.length - 2; i++) {
            clearFocusedView(this.mViewArray[i]);
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
                this.mFileFormat = "MOVIE_FORMAT";
                removeFocusFromAll();
                mCurrentRow = 1;
                this.inRecRowSelection = 0;
                setNormalIcon();
                this.mMovieBtin.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
                this.mMovieBtinBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                handlePlayTimeVisibility(0);
                mTlShootModeController.setCaptureState(0);
                setSelectedOptionAEStatusRow();
                break;
            case R.id.still_btn /* 2131362391 */:
                AppLog.trace("INSIDE", AntiHandBlurController.STILL);
                this.mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL;
                removeFocusFromAll();
                mCurrentRow = 1;
                this.inRecRowSelection = 1;
                setNormalIcon();
                handlePlayTimeVisibility(4);
                this.mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
                this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                mTlShootModeController.setCaptureState(1);
                setSelectedOptionAEStatusRow();
                break;
            case R.id.still_movie_btn /* 2131362393 */:
                AppLog.trace("INSIDE", "still & movie");
                this.mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE;
                removeFocusFromAll();
                mCurrentRow = 1;
                this.inRecRowSelection = 2;
                handlePlayTimeVisibility(0);
                setNormalIcon();
                this.mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
                this.mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                mTlShootModeController.setCaptureState(2);
                setSelectedOptionAEStatusRow();
                break;
            case R.id.ael_ael /* 2131362437 */:
                AppLog.trace("INSIDE", "aestatus aelock");
                removeFocusFromAll();
                mCurrentRow = 4;
                this.inAEStatusRowSelection = 0;
                setNormalIcon();
                this.mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
            case R.id.ael_tracking_image /* 2131362440 */:
                AppLog.trace("INSIDE", "aestatus low");
                removeFocusFromAll();
                mCurrentRow = 4;
                this.inAEStatusRowSelection = 4;
                setNormalIcon();
                this.mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
            case R.id.ael_tracking_low /* 2131362441 */:
                AppLog.trace("INSIDE", "aestatus low");
                removeFocusFromAll();
                mCurrentRow = 4;
                this.inAEStatusRowSelection = 3;
                setNormalIcon();
                this.mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
            case R.id.ael_tracking_mid /* 2131362443 */:
                AppLog.trace("INSIDE", "aestatus mid");
                removeFocusFromAll();
                mCurrentRow = 4;
                this.inAEStatusRowSelection = 2;
                setNormalIcon();
                this.mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
            case R.id.ael_tracking_high /* 2131362445 */:
                AppLog.trace("INSIDE", "aestatus high");
                removeFocusFromAll();
                mCurrentRow = 4;
                this.inAEStatusRowSelection = 1;
                setNormalIcon();
                this.mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setSelectedImageOnRecording();
                break;
        }
        makeViewsUnfocusable();
        setOptionNameNormal();
        setOptionNameSelected();
        pushedCenterKey();
    }

    private void setNormalIcon() {
        setFirstRowNormal();
        setFifthRowNormal();
    }

    private void setValidImage() {
        this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_normal);
        this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_normal);
        this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_normal);
        this.mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_normal);
        this.mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_normal);
        this.mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            this.mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        }
    }

    private void setDisabledImage() {
        this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
        this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
        this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
        this.mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
        this.mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_disable);
        this.mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        this.mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        this.mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        this.mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            this.mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_disable);
        }
    }

    private void setFirstRowNormal() {
        this.mMovieBtin.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_normal);
        this.mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_normal);
        this.mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_normal);
        this.mMovieBtinBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
    }

    private void setFifthRowNormal() {
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            setValidImage();
        } else {
            setDisabledImage();
        }
    }

    private void getLastStoredValues() {
        this.mFileFormat = this.mTLCommonUtil.getFileFormat();
        this.mAEStatus = this.mTLCommonUtil.getAETrakingStatus();
        this.mInterval = this.mTLCommonUtil.getInterval();
        this.mShootingNumber = this.mTLCommonUtil.getShootingNum();
        this.mOldFileFormat = this.mFileFormat;
        this.mOldAEStatus = this.mAEStatus;
        this.mOldInterval = this.mInterval;
        this.mOldShootingNumber = this.mShootingNumber;
        this.inRecRowSelection = mTlShootModeController.getCurrentCaptureState();
        this.inAEStatusRowSelection = this.mAEStatus;
        this.previousShootingNum = this.mShootingNumber;
        setValuesOnScreen();
        if ((this.mLastFocusedView != null && this.mLastFocusedView.getId() == R.id.tl_reset_button) || mCurrentRow == 0 || isLastFocusedItemFromAERow()) {
            this.mLastFocusedView = this.mMovieBtin;
            mCurrentRow = 1;
        }
    }

    private boolean isLastFocusedItemFromAERow() {
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption() || this.mLastFocusedView == null) {
            return false;
        }
        switch (this.mLastFocusedView.getId()) {
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
        switch (TLCommonUtil.getThemeUtil().getCurrentState()) {
            case 0:
                this.mFileFormat = "MOVIE_FORMAT";
                this.mAEStatus = 0;
                this.mInterval = 5;
                this.mShootingNumber = 240;
                break;
            case 1:
                this.mFileFormat = "MOVIE_FORMAT";
                this.mAEStatus = 0;
                this.mInterval = 30;
                this.mShootingNumber = 240;
                break;
            case 2:
                this.mFileFormat = "MOVIE_FORMAT";
                this.mAEStatus = 0;
                this.mInterval = 3;
                this.mShootingNumber = 240;
                break;
            case 3:
                this.mFileFormat = "MOVIE_FORMAT";
                this.mAEStatus = 3;
                this.mInterval = 10;
                this.mShootingNumber = 240;
                if (this.mTLCommonUtil.isOlderPFVersion()) {
                    this.mAEStatus = 4;
                    break;
                }
                break;
            case 4:
                this.mFileFormat = "MOVIE_FORMAT";
                this.mAEStatus = 3;
                this.mInterval = 10;
                this.mShootingNumber = 240;
                if (this.mTLCommonUtil.isOlderPFVersion()) {
                    this.mAEStatus = 4;
                    break;
                }
                break;
            case 5:
                this.mFileFormat = "MOVIE_FORMAT";
                this.mAEStatus = 0;
                this.mInterval = 2;
                this.mShootingNumber = 240;
                break;
            case 6:
                this.mFileFormat = "MOVIE_FORMAT";
                this.mAEStatus = 0;
                this.mInterval = 1;
                this.mShootingNumber = 240;
                break;
            case 7:
                this.mFileFormat = "MOVIE_FORMAT";
                this.mAEStatus = 0;
                this.mInterval = 1;
                this.mShootingNumber = 240;
                break;
        }
        this.inAEStatusRowSelection = this.mAEStatus;
        this.inRecRowSelection = 0;
        this.previousShootingNum = this.mShootingNumber;
        setValuesOnScreen();
        restoreDefaultSelection();
    }

    private void setValuesOnScreen() {
        this.mIntervalBar.setProgress(this.mInterval);
        this.mShootingNumberBar.setProgress(this.mShootingNumber);
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
        this.mFileFormat = this.mOldFileFormat;
        this.mInterval = this.mOldInterval;
        this.inAEStatusRowSelection = this.mOldAEStatus;
        this.mShootingNumber = this.mOldShootingNumber;
    }

    private void resetThemeProperty() {
        resetDefaultValues();
        handlePlayTimeVisibility(0);
        setFocusableView(this.mResetImageBtn);
        setDefaultSelectValue();
        setSeekbarNormal();
        this.mIntervalValue.setText(String.format(this.mSecondString, Integer.valueOf(this.mInterval)));
        setDots();
    }

    private void setDefaultSelectValue() {
        int selectorBgResourceID = R.drawable.p_16_dd_parts_tm_option_button_select;
        switch (this.inRecRowSelection) {
            case 1:
                this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                break;
            case 2:
                this.mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                break;
            default:
                this.mMovieBtinBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                break;
        }
        if (!this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            selectorBgResourceID = R.drawable.p_16_dd_parts_tm_option_button_disable;
        }
        switch (this.inAEStatusRowSelection) {
            case 1:
                this.mAelTrackHighBg.setBackgroundResource(selectorBgResourceID);
                return;
            case 2:
                this.mAelTrackMidBg.setBackgroundResource(selectorBgResourceID);
                return;
            case 3:
                this.mAelTrackLowBg.setBackgroundResource(selectorBgResourceID);
                return;
            case 4:
                this.mAelTrackingBg.setBackgroundResource(selectorBgResourceID);
                return;
            default:
                this.mAeLockBg.setBackgroundResource(selectorBgResourceID);
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        if (mCurrentRow == 0) {
            return -1;
        }
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
        removeFocusFromAll();
        if (mCurrentRow < 4) {
            mCurrentRow++;
        }
        switch (mCurrentRow) {
            case 1:
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_normal);
                setFirstRowNormal();
                switch (this.inRecRowSelection) {
                    case 0:
                        setFocusableView(this.mMovieBtin);
                        this.mMovieBtin.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
                        this.mMovieBtinBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 1:
                        setFocusableView(this.mStillBtn);
                        this.mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
                        this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 2:
                        setFocusableView(this.mStillMovieBtn);
                        this.mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
                        this.mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        setSelectedOptionAEStatusRow();
                        break;
                }
            case 2:
                if (mIntervalTmp > 0) {
                    this.mInterval = mIntervalTmp;
                    mIntervalTmp = 0;
                    this.mIntervalBar.setProgress(this.mInterval);
                    this.mIntervalValue.setText(String.format(this.mSecondString, Integer.valueOf(this.mInterval)));
                }
                setSelectedImageOnRecording();
                setSelectedOptionAEStatusRow();
                setSeekbarSelected(0);
                setFocusableView(this.mIntervalBar);
                break;
            case 3:
                setSelectedImageOnRecording();
                setSelectedOptionAEStatusRow();
                setSeekbarSelected(1);
                setFocusableView(this.mShootingNumberBar);
                this.previousShootingNum = this.mShootingNumber;
                break;
            case 4:
                setSelectedImageOnRecording();
                setSelectedBtnOnAEStatusRow();
                break;
        }
        setOptionNameNormal();
        setOptionNameSelected();
        return 1;
    }

    private void setResetButtonBackground(int resetButtonID) {
        this.mResetImageBtn.setImageResource(resetButtonID);
    }

    private void setFocusableView(View view) {
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.requestFocus();
        view.setSelected(true);
        this.mLastFocusedView = view;
    }

    private void clearFocusedView(View view) {
        view.clearFocus();
        view.setFocusable(false);
        view.setSelected(false);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        removeFocusFromAll();
        if (mCurrentRow > 0) {
            mCurrentRow--;
        }
        switch (mCurrentRow) {
            case 0:
                setSelectedImageOnRecording();
                setSelectedOptionAEStatusRow();
                setFocusableView(this.mResetImageBtn);
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_focused);
                break;
            case 1:
                setFirstRowNormal();
                switch (this.inRecRowSelection) {
                    case 0:
                        setFocusableView(this.mMovieBtin);
                        this.mMovieBtin.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
                        this.mMovieBtinBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 1:
                        setFocusableView(this.mStillBtn);
                        this.mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
                        this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                    case 2:
                        setFirstRowNormal();
                        setFocusableView(this.mStillMovieBtn);
                        this.mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
                        this.mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                        break;
                }
            case 2:
                setSeekbarSelected(1);
                setFocusableView(this.mIntervalBar);
                break;
            case 3:
                setSelectedImageOnRecording();
                setSelectedOptionAEStatusRow();
                setSeekbarSelected(1);
                setFocusableView(this.mShootingNumberBar);
                break;
        }
        setOptionNameNormal();
        setOptionNameSelected();
        return 1;
    }

    private void setSelectedOptionAEStatusRow() {
        setFifthRowNormal();
        int bgResourceID = R.drawable.p_16_dd_parts_tm_option_button_disable;
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            bgResourceID = R.drawable.p_16_dd_parts_tm_option_button_select;
        }
        switch (this.inAEStatusRowSelection) {
            case 1:
                this.mAelTrackHighBg.setBackgroundResource(bgResourceID);
                return;
            case 2:
                this.mAelTrackMidBg.setBackgroundResource(bgResourceID);
                return;
            case 3:
                this.mAelTrackLowBg.setBackgroundResource(bgResourceID);
                return;
            case 4:
                this.mAelTrackingBg.setBackgroundResource(bgResourceID);
                return;
            default:
                this.mAeLockBg.setBackgroundResource(bgResourceID);
                return;
        }
    }

    private void setSelectedBtnOnAEStatusRow() {
        switch (this.inAEStatusRowSelection) {
            case 0:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_focused);
                } else {
                    this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
                }
                setFocusableView(this.mAeLock);
                this.mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 1:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    this.mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_focused);
                } else {
                    this.mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
                }
                this.mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setFocusableView(this.mAelTrackHigh);
                return;
            case 2:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_focused);
                } else {
                    this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
                }
                this.mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setFocusableView(this.mAelTrackMid);
                return;
            case 3:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_focused);
                } else {
                    this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
                }
                this.mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setFocusableView(this.mAelTrackLow);
                return;
            case 4:
                if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                    this.mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_focused);
                } else {
                    this.mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_disable);
                }
                this.mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                setFocusableView(this.mAelTracking);
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

    private void increaseIntervalBar() {
        int curValue = this.mIntervalBar.getProgress();
        this.mIntervalBar.setProgress(curValue + 1);
    }

    private void decreaseIntervalBar() {
        int curValue = this.mIntervalBar.getProgress();
        if (this.inRecRowSelection == 2 && curValue - 1 < 3) {
            curValue = 4;
        }
        this.mIntervalBar.setProgress(curValue - 1);
    }

    private void incrementShootingNum() {
        int curValue = this.mShootingNumberBar.getProgress();
        this.mShootingNumberBar.setProgress(curValue + 1);
    }

    private void decrementShootingNum() {
        int curValue = this.mShootingNumberBar.getProgress();
        this.mShootingNumberBar.setProgress(curValue - 1);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        return "ID_TLTHEMEOPTIONLAYOUT";
    }

    private void handleRightAction() {
        if (this.mStillBtn.hasFocus()) {
            this.inRecRowSelection = 2;
            setFirstRowNormal();
            clearFocusedView(this.mStillBtn);
            setFocusableView(this.mStillMovieBtn);
            this.mStillMovieBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_focused);
            this.mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            mTlShootModeController.setCaptureState(2);
            this.mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE;
            handlePlayTimeVisibility(0);
            TLCommonUtil.getInstance().setJpegDummyQuality();
            if (this.mInterval < 3) {
                mIntervalTmp = this.mInterval;
                this.mInterval = 3;
                this.mIntervalBar.setProgress(this.mInterval);
                setDots();
                this.mIntervalValue.setText(String.format(this.mSecondString, Integer.valueOf(this.mInterval)));
                this.mShootingTimeValue.setText(getTotalRecTime());
                return;
            }
            return;
        }
        if (this.mMovieBtin.hasFocus()) {
            this.inRecRowSelection = 1;
            setFirstRowNormal();
            clearFocusedView(this.mMovieBtin);
            setFocusableView(this.mStillBtn);
            this.mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
            this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            this.mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL;
            handlePlayTimeVisibility(4);
            mTlShootModeController.setCaptureState(1);
            TLCommonUtil.getInstance().setJpegDummyQuality();
            if (mIntervalTmp > 0) {
                this.mInterval = mIntervalTmp;
                this.mIntervalBar.setProgress(this.mInterval);
                this.mIntervalValue.setText(String.format(this.mSecondString, Integer.valueOf(this.mInterval)));
                return;
            }
            return;
        }
        if (this.mAeLock.hasFocus()) {
            AppLog.info("INSIDE------->", "Right Ael---------->");
            setFifthRowNormal();
            clearFocusedView(this.mAeLock);
            setNextFocusOnRightAEPF();
            return;
        }
        if (this.mAelTrackLow.hasFocus()) {
            AppLog.info("INSIDE------->", "Right Low---------->");
            this.inAEStatusRowSelection = 2;
            setFifthRowNormal();
            clearFocusedView(this.mAelTrackLow);
            setFocusableView(this.mAelTrackMid);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_focused);
                this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_normal);
            } else {
                this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
                this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
            }
            this.mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (this.mAelTrackMid.hasFocus()) {
            AppLog.info("INSIDE------->", "Right Mid---------->");
            this.inAEStatusRowSelection = 1;
            setFifthRowNormal();
            clearFocusedView(this.mAelTrackMid);
            setFocusableView(this.mAelTrackHigh);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                this.mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_focused);
                this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_normal);
            } else {
                this.mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
                this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
            }
            this.mAelTrackHighBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (this.mIntervalBar.hasFocus()) {
            increaseIntervalBar();
        } else if (this.mShootingNumberBar.hasFocus()) {
            incrementShootingNum();
            this.mPlayBackTextTime.setText("" + getPlackBackDuration());
        }
    }

    private void setNextFocusOnRightAEPF() {
        if (this.mTLCommonUtil.isOlderPFVersion()) {
            setFocusableView(this.mAelTracking);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                this.mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_focused);
                this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_normal);
            } else {
                this.mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_disable);
                this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
            }
            this.mAelTrackingBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            this.inAEStatusRowSelection = 4;
            return;
        }
        setFocusableView(this.mAelTrackLow);
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_focused);
            this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_normal);
        } else {
            this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
            this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
        }
        this.mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        this.inAEStatusRowSelection = 3;
    }

    private void handleLeftAction() {
        if (this.mStillMovieBtn.hasFocus()) {
            this.inRecRowSelection = 1;
            setFirstRowNormal();
            clearFocusedView(this.mStillMovieBtn);
            setFocusableView(this.mStillBtn);
            this.mStillBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_focused);
            this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            this.mFileFormat = TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL;
            mTlShootModeController.setCaptureState(1);
            handlePlayTimeVisibility(4);
            TLCommonUtil.getInstance().setJpegDummyQuality();
            if (mIntervalTmp > 0) {
                this.mInterval = mIntervalTmp;
                this.mIntervalBar.setProgress(this.mInterval);
                setDots();
                this.mIntervalValue.setText(String.format(this.mSecondString, Integer.valueOf(this.mInterval)));
                this.mShootingTimeValue.setText(getTotalRecTime());
                return;
            }
            return;
        }
        if (this.mStillBtn.hasFocus()) {
            this.inRecRowSelection = 0;
            setFirstRowNormal();
            clearFocusedView(this.mStillBtn);
            setFocusableView(this.mMovieBtin);
            this.mMovieBtin.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie_focused);
            this.mMovieBtinBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            this.mFileFormat = "MOVIE_FORMAT";
            mTlShootModeController.setCaptureState(0);
            handlePlayTimeVisibility(0);
            TLCommonUtil.getInstance().setJpegDummyQuality();
            return;
        }
        if (this.mAelTracking.hasFocus()) {
            AppLog.info("INSIDE------->", "Left Low---------->");
            this.inAEStatusRowSelection = 0;
            setFifthRowNormal();
            clearFocusedView(this.mAelTracking);
            setFocusableView(this.mAeLock);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_focused);
                this.mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_normal);
            } else {
                this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
                this.mAelTracking.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_disable);
            }
            this.mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (this.mAelTrackLow.hasFocus()) {
            AppLog.info("INSIDE------->", "Left Lcok---------->");
            this.inAEStatusRowSelection = 0;
            setFifthRowNormal();
            clearFocusedView(this.mAelTrackLow);
            setFocusableView(this.mAeLock);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_focused);
                this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_normal);
            } else {
                this.mAeLock.setImageResource(R.drawable.p_16_dd_parts_tm_ael_disable);
                this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
            }
            this.mAeLockBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (this.mAelTrackMid.hasFocus()) {
            AppLog.info("INSIDE------->", "left Mid---------->");
            this.inAEStatusRowSelection = 3;
            setFifthRowNormal();
            clearFocusedView(this.mAelTrackMid);
            setFocusableView(this.mAelTrackLow);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_focused);
                this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_normal);
            } else {
                this.mAelTrackLow.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_lo_disable);
                this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
            }
            this.mAelTrackLowBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (this.mAelTrackHigh.hasFocus()) {
            AppLog.info("INSIDE------->", "left High---------->");
            this.inAEStatusRowSelection = 2;
            setFifthRowNormal();
            clearFocusedView(this.mAelTrackHigh);
            setFocusableView(this.mAelTrackMid);
            if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
                this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_focused);
                this.mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_normal);
            } else {
                this.mAelTrackMid.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_mid_disable);
                this.mAelTrackHigh.setImageResource(R.drawable.p_16_dd_parts_tm_tracking_hi_disable);
            }
            this.mAelTrackMidBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            return;
        }
        if (this.mIntervalBar.hasFocus()) {
            decreaseIntervalBar();
        } else if (this.mShootingNumberBar.hasFocus()) {
            decrementShootingNum();
            this.mPlayBackTextTime.setText("" + getPlackBackDuration());
        }
    }

    public String getTotalRecTime() {
        int sec = this.mIntervalBar.getProgress() * (this.mShootingNumberBar.getProgress() - 1);
        int hours = sec / 3600;
        int remainder = sec % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;
        String totalRecTime = (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        return totalRecTime;
    }

    private void makeViewsUnfocusable() {
        if (getView() != null) {
            getView().setFocusable(false);
            getView().setFocusableInTouchMode(false);
        }
        for (int i = 0; i < this.mViewArray.length - 2; i++) {
            this.mViewArray[i].setFocusable(false);
            this.mViewArray[i].setFocusableInTouchMode(false);
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
        if (this.mLastFocusedView != null) {
            setFocusableView(this.mLastFocusedView);
        }
    }

    private void setSelectedImageOnRecording() {
        setFirstRowNormal();
        switch (this.inRecRowSelection) {
            case 0:
                this.mMovieBtinBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 1:
                this.mStillBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            case 2:
                this.mStillMovieBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                return;
            default:
                return;
        }
    }

    private void setOptionNameSelected() {
        switch (mCurrentRow) {
            case 0:
                setSeekbarNormal();
                return;
            case 1:
                this.mRecordingModeName.setTextColor(Color.parseColor("#FF8000"));
                setSeekbarNormal();
                return;
            case 2:
                this.mIntervalName.setTextColor(Color.parseColor("#FF8000"));
                setSeekbarNormal();
                setSeekbarSelected(0);
                return;
            case 3:
                this.mShootingNumberName.setTextColor(Color.parseColor("#FF8000"));
                setSeekbarNormal();
                setSeekbarSelected(1);
                return;
            case 4:
                this.mAEStatusName.setTextColor(Color.parseColor("#FF8000"));
                setSeekbarNormal();
                return;
            default:
                return;
        }
    }

    private void setOptionNameNormal() {
        this.mRecordingModeName.setTextColor(Color.parseColor("#FFFFFF"));
        if (this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            this.mAEStatusName.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            this.mAEStatusName.setTextColor(Color.parseColor("#808080"));
        }
        this.mIntervalName.setTextColor(Color.parseColor("#FFFFFF"));
        this.mShootingNumberName.setTextColor(Color.parseColor("#FFFFFF"));
        setBottomValuesNormal();
    }

    private void setBottomValuesNormal() {
        this.mIntervalValue.setTextColor(Color.parseColor("#FFFFFF"));
        this.mShootingNumberValue.setTextColor(Color.parseColor("#FFFFFF"));
        this.mShootingTimeTitle.setTextColor(Color.parseColor("#FFFFFF"));
        this.mShootingTimeValue.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void setSeekbarNormal() {
        Rect r1 = this.mIntervalBar.getProgressDrawable().getBounds();
        this.mIntervalBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_normal));
        this.mIntervalBar.getProgressDrawable().setBounds(r1);
        if (this.mIntervalBar.getProgress() == this.mIntervalBar.getMax()) {
            this.isSetSeekBarNormal = true;
            this.mIntervalBar.setProgress(this.mIntervalBar.getProgress() - 1);
            this.isSetSeekBarNormal = true;
            this.mIntervalBar.setProgress(this.mIntervalBar.getProgress() + 1);
        } else {
            this.isSetSeekBarNormal = true;
            this.mIntervalBar.setProgress(this.mIntervalBar.getProgress() + 1);
            this.isSetSeekBarNormal = true;
            this.mIntervalBar.setProgress(this.mIntervalBar.getProgress() - 1);
        }
        Rect r2 = this.mShootingNumberBar.getProgressDrawable().getBounds();
        this.mShootingNumberBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_normal));
        this.mShootingNumberBar.getProgressDrawable().setBounds(r2);
        if (this.mShootingNumberBar.getProgress() == this.mShootingNumberBar.getMax()) {
            this.isSetSeekBarNormal = true;
            this.mShootingNumberBar.setProgress(this.mShootingNumberBar.getProgress() - 1);
            this.isSetSeekBarNormal = true;
            this.mShootingNumberBar.setProgress(this.mShootingNumberBar.getProgress() + 1);
        } else {
            this.isSetSeekBarNormal = true;
            this.mShootingNumberBar.setProgress(this.mShootingNumberBar.getProgress() + 1);
            this.isSetSeekBarNormal = true;
            this.mShootingNumberBar.setProgress(this.mShootingNumberBar.getProgress() - 1);
        }
        this.mShootingTimeValue.setText(getTotalRecTime());
        this.mTLCommonUtil.setShootingNum(this.mShootingNumber);
        this.mShootingNumberValue.setText(this.mShootingNumber + "");
    }

    private void setSeekbarSelected(int whichBar) {
        switch (whichBar) {
            case 0:
                Rect r1 = this.mIntervalBar.getProgressDrawable().getBounds();
                this.mIntervalBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_focused));
                this.mIntervalBar.getProgressDrawable().setBounds(r1);
                if (this.mIntervalBar.getProgress() == this.mIntervalBar.getMax()) {
                    this.isSetSeekBarNormal = true;
                    this.mIntervalBar.setProgress(this.mIntervalBar.getProgress() - 1);
                    this.isSetSeekBarNormal = true;
                    this.mIntervalBar.setProgress(this.mIntervalBar.getProgress() + 1);
                    return;
                }
                this.isSetSeekBarNormal = true;
                this.mIntervalBar.setProgress(this.mIntervalBar.getProgress() + 1);
                this.isSetSeekBarNormal = true;
                this.mIntervalBar.setProgress(this.mIntervalBar.getProgress() - 1);
                return;
            case 1:
                Rect r2 = this.mShootingNumberBar.getProgressDrawable().getBounds();
                this.mShootingNumberBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_focused));
                this.mShootingNumberBar.getProgressDrawable().setBounds(r2);
                if (this.mShootingNumberBar.getProgress() == this.mShootingNumberBar.getMax()) {
                    this.isSetSeekBarNormal = true;
                    this.mShootingNumberBar.setProgress(this.mShootingNumberBar.getProgress() - 1);
                    this.isSetSeekBarNormal = true;
                    this.mShootingNumberBar.setProgress(this.mShootingNumberBar.getProgress() + 1);
                    return;
                }
                this.isSetSeekBarNormal = true;
                this.mShootingNumberBar.setProgress(this.mShootingNumberBar.getProgress() + 1);
                this.isSetSeekBarNormal = true;
                this.mShootingNumberBar.setProgress(this.mShootingNumberBar.getProgress() - 1);
                return;
            default:
                return;
        }
    }

    private void setDots() {
        disappearAllDots();
        setDotPattern(this.mInterval);
    }

    private void disappearAllDots() {
        for (int i = 0; i < 60; i++) {
            this.mDot[i].setVisibility(4);
        }
    }

    private void setDotPattern(int interval) {
        int i = interval - 1;
        while (i < 60) {
            this.mDot[i].setVisibility(0);
            i += interval;
        }
    }

    private void initializeDotImages() {
        this.mDot = new ImageView[60];
        int[] resId = {R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4, R.id.dot5, R.id.dot6, R.id.dot7, R.id.dot8, R.id.dot9, R.id.dot10, R.id.dot11, R.id.dot12, R.id.dot13, R.id.dot14, R.id.dot15, R.id.dot16, R.id.dot17, R.id.dot18, R.id.dot19, R.id.dot20, R.id.dot21, R.id.dot22, R.id.dot23, R.id.dot24, R.id.dot25, R.id.dot26, R.id.dot27, R.id.dot28, R.id.dot29, R.id.dot30, R.id.dot31, R.id.dot32, R.id.dot33, R.id.dot34, R.id.dot35, R.id.dot36, R.id.dot37, R.id.dot38, R.id.dot39, R.id.dot40, R.id.dot41, R.id.dot42, R.id.dot43, R.id.dot44, R.id.dot45, R.id.dot46, R.id.dot47, R.id.dot48, R.id.dot49, R.id.dot50, R.id.dot51, R.id.dot52, R.id.dot53, R.id.dot54, R.id.dot55, R.id.dot56, R.id.dot57, R.id.dot58, R.id.dot59, R.id.dot60};
        for (int i = 0; i < 60; i++) {
            this.mDot[i] = (ImageView) this.mCurrentView.findViewById(resId[i]);
            this.mDot[i].setImageResource(R.drawable.p_16_dd_parts_tm_interval_indicator_dot);
        }
    }

    private String getPlackBackDuration() {
        int playTime = 0;
        if (this.mFileFormat.equalsIgnoreCase("MOVIE_FORMAT") || this.mFileFormat.equalsIgnoreCase(TimeLapseConstants.TIME_LAPSE_FILE_FORMAT_STILL_MOVIE)) {
            if ("framerate-24p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
                playTime = this.mShootingNumber / 24;
            } else if ("framerate-30p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
                playTime = this.mShootingNumber / 30;
            }
        }
        return String.format(this.mSecondString, Integer.valueOf(playTime));
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!this.mTLCommonUtil.isValidThemeForUpdatedOption()) {
            this.mAEStatus = 0;
        } else {
            this.mAEStatus = this.inAEStatusRowSelection;
        }
        this.mTLCommonUtil.saveChangedValues(this.mFileFormat, this.mAEStatus, this.mInterval, this.mShootingNumber);
        if (1 == TLCommonUtil.getThemeUtil().getCurrentState()) {
            this.mTLCommonUtil.setNightSkyExposureCompensation();
        }
        this.mTLCommonUtil.setThemeChanged(false);
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        deinitializeView();
        TLCommonUtil.getInstance().setJpegDummyQuality();
        if (CameraSetting.getInstance().getCamera() != null) {
            TLShootModeSettingController.getInstance().setMiniatureOptionValue(TLShootModeSettingController.getInstance().getCurrentCaptureState());
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deinitializeView();
        clear();
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
        this.mIntervalName = null;
        this.mRecordingModeName = null;
        this.mShootingNumberName = null;
        this.mAEStatusName = null;
        this.mCurrentView = null;
        this.mIntervalValue = null;
        this.mShootingNumberValue = null;
        this.mShootingTimeTitle = null;
        this.mShootingTimeValue = null;
        this.mThemeName = null;
        this.mPlayBackTextTime = null;
        this.mSecondString = null;
        releaseImageViewDrawable(this.mPlayBackImageBtn);
        this.mPlayBackImageBtn = null;
        releaseImageViewDrawable(this.mFrameRateImage);
        this.mFrameRateImage = null;
        releaseImageViewDrawable(this.mMovieBtin);
        this.mMovieBtin = null;
        releaseImageViewDrawable(this.mStillBtn);
        this.mStillBtn = null;
        releaseImageViewDrawable(this.mStillMovieBtn);
        this.mStillMovieBtn = null;
        releaseImageViewDrawable(this.mMovieBtinBg);
        this.mMovieBtinBg = null;
        releaseImageViewDrawable(this.mStillBtnBg);
        this.mStillBtnBg = null;
        releaseImageViewDrawable(this.mStillMovieBtnBg);
        this.mStillMovieBtnBg = null;
        releaseImageViewDrawable(this.mAeLock);
        this.mAeLock = null;
        releaseImageViewDrawable(this.mAelTrackHigh);
        this.mAelTrackHigh = null;
        releaseImageViewDrawable(this.mAelTrackMid);
        this.mAelTrackMid = null;
        releaseImageViewDrawable(this.mAelTrackLow);
        this.mAelTrackLow = null;
        releaseImageViewDrawable(this.mAelTracking);
        this.mAelTracking = null;
        releaseImageViewDrawable(this.mAeLockBg);
        this.mAeLockBg = null;
        releaseImageViewDrawable(this.mAelTrackHighBg);
        this.mAelTrackHighBg = null;
        releaseImageViewDrawable(this.mAelTrackMidBg);
        this.mAelTrackMidBg = null;
        releaseImageViewDrawable(this.mAelTrackLowBg);
        this.mAelTrackLowBg = null;
        releaseImageViewDrawable(this.mAelTrackingBg);
        this.mAelTrackingBg = null;
        this.mIntervalBar = null;
        this.mShootingNumberBar = null;
        this.mFileFormat = null;
        this.mAEStatus = 0;
        this.mInterval = 0;
        this.mShootingNumber = 0;
        this.inRecRowSelection = 0;
        this.inAEStatusRowSelection = 0;
        this.mLayoutParamsAELock = null;
        this.mLayoutParamsAETracking = null;
        this.mLayoutParamsAELockBg = null;
        this.mShootingList = null;
        this.mFooterGuide = null;
        mTlShootModeController = null;
        this.mOldFileFormat = null;
        this.mOldAEStatus = 0;
        this.mOldInterval = 0;
        this.mOldShootingNumber = 0;
        this.mResetImageBtn = null;
        this.mTLCommonUtil = null;
        this.previousShootingNum = 0;
        this.mShootingList = null;
        if (this.mViewArray != null) {
            for (int i = 0; i < this.mViewArray.length - 2; i++) {
                if (this.mViewArray[i] != null) {
                    this.mViewArray[i].setOnClickListener(null);
                    this.mViewArray[i] = null;
                }
            }
            this.mViewArray = null;
        }
        if (this.mDot != null) {
            for (int i2 = 0; i2 < 60; i2++) {
                if (this.mDot[i2] != null) {
                    releaseImageViewDrawable(this.mDot[i2]);
                    this.mDot[i2] = null;
                }
            }
            this.mDot = null;
        }
        System.gc();
    }

    public int getIntervalValuePlus(int progress) {
        if (progress >= 60) {
            return progress;
        }
        int interval = progress + 1;
        return interval;
    }

    public int getIntervalValueMinus(int progress) {
        if (progress <= 1) {
            return progress;
        }
        int interval = progress - 1;
        return interval;
    }

    public int getShootingNumInPlus(int progress) {
        AppLog.info("INSIDE", "getShootingNumInRange");
        int rangeProgress = 1;
        for (int i = 0; i < this.mShootingList.size() - 1; i++) {
            if (progress <= 1) {
                rangeProgress = 1;
            } else if (progress > this.mShootingList.get(i).intValue() && progress <= this.mShootingList.get(i + 1).intValue()) {
                rangeProgress = this.mShootingList.get(i + 1).intValue();
            }
        }
        return rangeProgress;
    }

    public int getShootingNumInMinus(int progress) {
        AppLog.info("INSIDE", "getShootingNumInRange");
        int rangeProgress = 1;
        for (int i = 0; i < this.mShootingList.size() - 1; i++) {
            if (progress <= 1) {
                rangeProgress = 1;
            } else if (progress > this.mShootingList.get(i).intValue() && progress <= this.mShootingList.get(i + 1).intValue()) {
                rangeProgress = this.mShootingList.get(i).intValue();
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
                switch (this.inRecRowSelection) {
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
                switch (this.inAEStatusRowSelection) {
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
}
