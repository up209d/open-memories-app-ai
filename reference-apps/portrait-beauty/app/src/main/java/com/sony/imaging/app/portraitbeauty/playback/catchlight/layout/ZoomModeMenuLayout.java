package com.sony.imaging.app.portraitbeauty.playback.catchlight.layout;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppContext;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.menu.controller.ZoomModePatternController;
import com.sony.imaging.app.portraitbeauty.playback.browser.PortraitBeautyCatchLightState;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.state.ZoomModeState;
import com.sony.imaging.app.portraitbeauty.shooting.widget.PortraitBeautyVerticalSeekBar;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ZoomModeMenuLayout extends SpecialScreenMenuLayout implements SpecialScreenView.OnItemSelectedListener, IModableLayout {
    private RelativeLayout mBrigtness;
    protected NotificationListener mDisplayChangedListener;
    private RelativeLayout mParentLayout;
    private RelativeLayout mPattern;
    private ImageView mPatternImegeView;
    private RelativeLayout mPositionLeft;
    private RelativeLayout mPositionRight;
    private RelativeLayout mSave;
    private PortraitBeautyVerticalSeekBar mSeekBarBrightness;
    private RelativeLayout mSeekBarBrightnessArea;
    private PortraitBeautyVerticalSeekBar mSeekBarSize;
    private RelativeLayout mSeekBarSizeArea;
    private RelativeLayout mSize;
    private static int DEFAULT_GAUGE_VALUE = 8;
    public static int mSizeGaugeValue = DEFAULT_GAUGE_VALUE;
    private static final String[] TAGS_OBSERVE_DISPLAY = {DisplayModeObserver.TAG_DEVICE_CHANGE};
    private final String TAG = AppLog.getClassName();
    private ZoomModePatternController mPatternController = null;
    private String mPatternValue = ZoomModePatternController.PATTERN_1;
    private String mSelectedLevel = ZoomModePatternController.PATTERN_1;
    private ArrayList<String> mList = null;
    private final int ITEM_COUNT_7 = 7;
    private int mSelectedItemPosition = -1;
    private boolean mCountDown_started = false;
    private final int isVISIBLE = 0;
    private String ZOOM_MODE_PATTERN = "ZoomModePattern";
    private int mBrightnessGaugeValue = DEFAULT_GAUGE_VALUE;
    private int mSelectedZoomItem = 1;
    private FooterGuide mFooterGuide = null;
    private TextView mMainGuideBackgroundView_pattern = null;
    private TextView mMainGuideBackgroundView_size = null;
    private TextView mMainGuideBackgroundView_brightness = null;
    private TextView mMainGuideBackgroundView_right = null;
    private TextView mMainGuideBackgroundView_left = null;
    private TextView mMainGuideBackgroundView_save = null;
    private ImageView mGeneralGuideBackgroundImage = null;
    private int mSelectedParamIocnPosition = 0;
    private int mBrightnessMax = 13;
    private RelativeLayout.LayoutParams mLayoutParams_param_guide_background = null;
    private View mCurrentView = null;
    private View mDonotUseEVF = null;
    private boolean isToggled = true;
    private DisplayManager mDisplayManager = null;
    private int mEVfSizeGaugeValue = DEFAULT_GAUGE_VALUE;
    private int mEVFBrightnessGaugeValue = DEFAULT_GAUGE_VALUE;
    private int mEVFSelectedZoomItem = 1;
    private String mEVFPatternValue = ZoomModePatternController.PATTERN_1;
    CountDownTimer Cdt_4sec_timer = new CountDownTimer(4000, 1000) { // from class: com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.ZoomModeMenuLayout.1
        @Override // android.os.CountDownTimer
        public void onTick(long arg0) {
            if (ZoomModeMenuLayout.this.mCountDown_started) {
            }
        }

        @Override // android.os.CountDownTimer
        public void onFinish() {
            if (ZoomModeMenuLayout.this.mGeneralGuideBackgroundImage != null) {
                if (ZoomModeMenuLayout.this.mGeneralGuideBackgroundImage.getVisibility() == 0) {
                    ZoomModeMenuLayout.this.mBackgroundMainInvisibler();
                }
                ZoomModeMenuLayout.this.mCountDown_started = false;
            }
        }
    }.start();
    private OnLayoutModeChangeListener mLayoutChangeListener = new OnLayoutModeChangeListener(this, 1);

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.zoom_mode_layout);
        if (ZoomModeState.mChangeListener == null) {
            return this.mCurrentView;
        }
        initializeView();
        if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
            this.mParentLayout.setVisibility(0);
        }
        this.mService = new BaseMenuService(AppContext.getAppContext());
        this.mService.setMenuItemId(this.ZOOM_MODE_PATTERN);
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(AppContext.getAppContext(), R.layout.menu_sub_adapter, this.mService);
        this.mAdapter.setMenuItemList(this.mService.getMenuItemList());
        this.mSpecialScreenView.setAdapter(this.mAdapter);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mGeneralGuideBackgroundImage = (ImageView) this.mCurrentView.findViewById(R.id.general_guide_arrow_background);
        this.mMainGuideBackgroundView_pattern = (TextView) this.mCurrentView.findViewById(R.id.balloon_guide_MAIN_background_position_pattern);
        this.mMainGuideBackgroundView_size = (TextView) this.mCurrentView.findViewById(R.id.balloon_guide_MAIN_background_position_size);
        this.mMainGuideBackgroundView_brightness = (TextView) this.mCurrentView.findViewById(R.id.balloon_guide_MAIN_background_position_brightness);
        this.mMainGuideBackgroundView_left = (TextView) this.mCurrentView.findViewById(R.id.balloon_guide_MAIN_background_position_Left);
        this.mMainGuideBackgroundView_right = (TextView) this.mCurrentView.findViewById(R.id.balloon_guide_MAIN_background_position_right);
        this.mMainGuideBackgroundView_save = (TextView) this.mCurrentView.findViewById(R.id.balloon_guide_MAIN_background_position_save);
        this.mLayoutParams_param_guide_background = new RelativeLayout.LayoutParams(-2, -2);
        getDefaultBackUpUtillValues();
        this.mSeekBarSize.setMax(CatchLightPlayBackLayout.mMaxGauzebarLevel);
        this.mSeekBarSize.setProgress(mSizeGaugeValue);
        this.mSeekBarBrightness.setMax(this.mBrightnessMax);
        this.mSeekBarBrightness.setProgress(this.mBrightnessGaugeValue);
        ZoomModeState.mChangeListener.onChangeBrightness(this.mBrightnessGaugeValue);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        this.mEVFSelectedZoomItem = 1;
        this.mEVFBrightnessGaugeValue = this.mBrightnessGaugeValue;
        this.mEVFPatternValue = this.mPatternValue;
        this.mEVfSizeGaugeValue = mSizeGaugeValue;
        return this.mCurrentView;
    }

    private void getDefaultBackUpUtillValues() {
        this.mSelectedZoomItem = 1;
        this.mPatternValue = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_CURRENT_PATTERN, ZoomModePatternController.PATTERN_1);
        mSizeGaugeValue = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_CURRENT_SIZE, -1);
        if (-1 == mSizeGaugeValue) {
            mSizeGaugeValue = CatchLightPlayBackLayout.mGauzeCurlevel;
            BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CURRENT_SIZE, Integer.valueOf(mSizeGaugeValue));
        }
        this.mBrightnessGaugeValue = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_CURRENT_BRIGHTNESS, DEFAULT_GAUGE_VALUE);
    }

    private void initializeView() {
        this.mPattern = (RelativeLayout) this.mCurrentView.findViewById(R.id.pattern);
        this.mSize = (RelativeLayout) this.mCurrentView.findViewById(R.id.size);
        this.mBrigtness = (RelativeLayout) this.mCurrentView.findViewById(R.id.brightness);
        this.mPositionLeft = (RelativeLayout) this.mCurrentView.findViewById(R.id.position_left);
        this.mPositionRight = (RelativeLayout) this.mCurrentView.findViewById(R.id.position_right);
        this.mSeekBarSizeArea = (RelativeLayout) this.mCurrentView.findViewById(R.id.seekbar_size_area);
        this.mSeekBarBrightnessArea = (RelativeLayout) this.mCurrentView.findViewById(R.id.seekbar_brightness_area);
        this.mSeekBarSize = (PortraitBeautyVerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_size_seekbar);
        this.mSeekBarBrightness = (PortraitBeautyVerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_brightness_seekbar);
        this.mSave = (RelativeLayout) this.mCurrentView.findViewById(R.id.save);
        this.mSpecialScreenView = (SpecialScreenView) this.mCurrentView.findViewById(R.id.listview);
        ZoomModePatternController zoomModePatternController = this.mPatternController;
        this.mPatternController = ZoomModePatternController.getInstance();
        this.mSpecialScreenView.setOnSpecialScreenItemSelectedListener(this);
        this.mViewArea = (SpecialScreenArea) this.mCurrentView.findViewById(R.id.listviewarea);
        this.mParentLayout = (RelativeLayout) this.mCurrentView.findViewById(R.id.zoom_mode_parent_layout);
        this.mPatternImegeView = (ImageView) this.mCurrentView.findViewById(R.id.pattern_image_view);
        this.mPatternImegeView.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_pattern2_menu);
        if (CatchLightPlayBackLayout.mCatchlight_focus_arrow != null) {
            CatchLightPlayBackLayout.mCatchlight_focus_arrow.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (ZoomModeState.mChangeListener != null) {
            registerDisplayChangedListener();
            DisplayModeObserver.getInstance().setNotificationListener(this.mLayoutChangeListener);
            if (this.data != null && this.data.getBoolean("menu_pressed", false)) {
                PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY = false;
                ZoomModeState.mChangeListener.onChangeSingleFace();
            }
            if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                this.mParentLayout.setVisibility(4);
            } else {
                this.mParentLayout.setVisibility(0);
            }
            if (CatchLightPlayBackLayout.isEVFShown) {
                this.mParentLayout.setVisibility(4);
            }
            this.mSpecialScreenView.setOnSpecialScreenItemSelectedListener(this);
            this.mViewArea.intialize(AppContext.getAppContext(), this.mSpecialScreenView);
            this.mViewArea.setVisibility(4);
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            this.mSelectedZoomItem = 1;
            resumeVisisbility();
            updatePattern();
            if (this.mFooterGuide != null) {
                setFooterGuide();
            }
            mSizeGaugeValue = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_CURRENT_SIZE, CatchLightPlayBackLayout.mGauzeCurlevel);
            this.mSeekBarSize.setProgress(mSizeGaugeValue);
            AppLog.exit(this.TAG, AppLog.getMethodName());
        }
    }

    private void resumeVisisbility() {
        if (this.mSelectedZoomItem == 1) {
            AppLog.enter(this.TAG, "Pattern is focused_1");
            this.mPattern.setSelected(true);
            makeAllSettingItemsInvisible();
            this.mSpecialScreenView.setVisibility(0);
            this.mViewArea.setVisibility(4);
            this.mSelectedParamIocnPosition = 1;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (ZoomModeState.mChangeListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mLayoutChangeListener);
            unregisterDisplayChangedListener();
            saveCurrentEffectValues();
            AppLog.exit(this.TAG, AppLog.getMethodName());
            super.onPause();
        }
    }

    private void saveCurrentEffectValues() {
        if (this.mBrigtness.isSelected()) {
            BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_ITEM, 8);
        } else {
            BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_SELECTED_ITEM, 1);
        }
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CURRENT_PATTERN, this.mPatternValue);
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CURRENT_BRIGHTNESS, Integer.valueOf(this.mBrightnessGaugeValue));
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_DEFAULT_BRIGHTNESS, Integer.valueOf(CatchLightPlayBackLayout.mPatternBriteness));
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CURRENT_SIZE, Integer.valueOf(mSizeGaugeValue));
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        closeLayout();
        deinitializeView();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    private void deinitializeView() {
        this.mPattern = null;
        this.mSize = null;
        this.mBrigtness = null;
        this.mPositionLeft = null;
        this.mPositionRight = null;
        this.mSave = null;
        this.mParentLayout = null;
        this.mSeekBarSizeArea = null;
        this.mSeekBarSize = null;
        this.mSeekBarBrightnessArea = null;
        this.mSeekBarBrightness = null;
        this.mPatternController = null;
        this.mPatternValue = ZoomModePatternController.PATTERN_1;
        this.mSelectedLevel = ZoomModePatternController.PATTERN_1;
        this.mList = null;
        this.mSelectedItemPosition = -1;
        this.mCountDown_started = false;
        mSizeGaugeValue = DEFAULT_GAUGE_VALUE;
        this.mSelectedZoomItem = 0;
        this.mMainGuideBackgroundView_pattern = null;
        this.mMainGuideBackgroundView_size = null;
        this.mMainGuideBackgroundView_brightness = null;
        this.mMainGuideBackgroundView_right = null;
        this.mMainGuideBackgroundView_left = null;
        this.mMainGuideBackgroundView_save = null;
        this.mGeneralGuideBackgroundImage = null;
        this.mSelectedParamIocnPosition = 0;
        this.mLayoutParams_param_guide_background = null;
        this.mFooterGuide = null;
        PortraitBeautyConstants.CENTER_KEY_LR_UD = false;
        this.mCurrentView = null;
    }

    public int pushLeftKey() {
        if (CatchLightPlayBackLayout.mCatchlight_focus_arrow != null) {
            CatchLightPlayBackLayout.mCatchlight_focus_arrow.setVisibility(4);
        }
        if (this.mSize.isSelected()) {
            this.mPattern.setSelected(true);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            this.mSelectedParamIocnPosition = 1;
        } else if (this.mBrigtness.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(true);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            this.mSelectedParamIocnPosition = 2;
        } else if (this.mPositionLeft.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(true);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black = true;
            ZoomModeState.mChangeListener.onDrawBlackEyeFrame();
            this.mSelectedParamIocnPosition = 8;
        } else if (this.mPositionRight.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(true);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black = false;
            CatchLightPlayBackLayout.mIsLeftEyeSelected = true;
            ZoomModeState.mChangeListener.onChangeLeftPosition(0);
            this.mSelectedParamIocnPosition = 4;
        } else if (this.mSave.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(true);
            this.mSave.setSelected(false);
            CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black = false;
            CatchLightPlayBackLayout.mIsLeftEyeSelected = false;
            ZoomModeState.mChangeListener.onChangeRightPosition(0);
            this.mSelectedParamIocnPosition = 5;
        } else if (this.mPattern.isSelected()) {
            this.mSave.setSelected(true);
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSelectedParamIocnPosition = 6;
        }
        if (!CatchLightPlayBackLayout.isEVFShown) {
            funct_guide_displayer(this.mSelectedParamIocnPosition);
        }
        changeSettingItems(this.mSelectedParamIocnPosition);
        setFooterGuide();
        return 1;
    }

    public int pushRightKey() {
        if (CatchLightPlayBackLayout.mCatchlight_focus_arrow != null) {
            CatchLightPlayBackLayout.mCatchlight_focus_arrow.setVisibility(4);
        }
        if (this.mPattern.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(true);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            this.mSelectedParamIocnPosition = 2;
        } else if (this.mSize.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(true);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            this.mSelectedParamIocnPosition = 8;
        } else if (this.mBrigtness.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(true);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            this.mSelectedParamIocnPosition = 4;
            CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black = false;
            CatchLightPlayBackLayout.mIsLeftEyeSelected = true;
            ZoomModeState.mChangeListener.onChangeLeftPosition(0);
            ZoomModeState.mChangeListener.onChangeCenter(PortraitBeautyConstants.FOCUS_ARROW_INVISIBLE);
        } else if (this.mPositionLeft.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(true);
            this.mSave.setSelected(false);
            CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black = false;
            CatchLightPlayBackLayout.mIsLeftEyeSelected = false;
            ZoomModeState.mChangeListener.onChangeRightPosition(0);
            this.mSelectedParamIocnPosition = 5;
        } else if (this.mPositionRight.isSelected()) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(true);
            this.mSelectedParamIocnPosition = 6;
        } else if (this.mSave.isSelected()) {
            this.mPattern.setSelected(true);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black = true;
            ZoomModeState.mChangeListener.onDrawBlackEyeFrame();
            this.mSelectedParamIocnPosition = 1;
        }
        if (!CatchLightPlayBackLayout.isEVFShown) {
            funct_guide_displayer(this.mSelectedParamIocnPosition);
        }
        changeSettingItems(this.mSelectedParamIocnPosition);
        setFooterGuide();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        if (this.mPositionLeft.isSelected()) {
            this.mPattern.setFocusable(false);
            this.mSize.setFocusable(false);
            this.mPositionRight.setFocusable(false);
            this.mBrigtness.setFocusable(false);
            this.mSave.setFocusable(false);
        }
        if (this.mPositionRight.isSelected()) {
            this.mPattern.setFocusable(false);
            this.mSize.setFocusable(false);
            this.mPositionLeft.setFocusable(false);
            this.mBrigtness.setFocusable(false);
            this.mSave.setFocusable(false);
        }
        if (true == CatchLightPlayBackLayout.isreturningfromPreview) {
            CatchLightPlayBackLayout.isreturningfromPreview = false;
            BackUpUtil.getInstance().setPreference("FACE_SELECTION", 0);
        }
        boolean handle = false;
        switch (scanCode) {
            case 103:
                if (!PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                    if (this.mSize.isSelected() && mSizeGaugeValue < CatchLightPlayBackLayout.mMaxGauzebarLevel) {
                        this.mSeekBarSize.moveUp();
                        mSizeGaugeValue++;
                        PortraitBeautyConstants.Resize_Key_Up = false;
                        ZoomModeState.mChangeListener.onChangeSize(mSizeGaugeValue);
                        handle = true;
                    } else if (this.mBrigtness.isSelected() && this.mBrightnessGaugeValue < this.mBrightnessMax) {
                        this.mSeekBarBrightness.moveUp();
                        this.mBrightnessGaugeValue++;
                        PortraitBeautyConstants.Brightness_Key_Up = false;
                        ZoomModeState.mChangeListener.onChangeBrightness(this.mBrightnessGaugeValue);
                        handle = true;
                    } else if (PortraitBeautyConstants.CENTER_KEY_LR_UD && (this.mPositionLeft.isSelected() || this.mPositionRight.isSelected())) {
                        ZoomModeState.mChangeListener.onChangeCenter(PortraitBeautyConstants.UP_ON_CENTER);
                    }
                    if (this.mSize.isSelected() || this.mBrigtness.isSelected() || this.mPositionLeft.isSelected() || this.mPositionRight.isSelected() || this.mSave.isSelected()) {
                        handle = true;
                    }
                    mBackgroundMainInvisibler();
                    break;
                } else {
                    return -1;
                }
                break;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                if (PortraitBeautyConstants.CENTER_KEY_LR_UD) {
                    ZoomModeState.mChangeListener.onChangeCenter(PortraitBeautyConstants.LEFT_ON_CENTER);
                    break;
                } else if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                    ZoomModeState.mChangeListener.onChangeFaceFocusFrame(PortraitBeautyConstants.LEFT_ON_CENTER);
                    break;
                } else {
                    pushLeftKey();
                    break;
                }
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                if (PortraitBeautyConstants.CENTER_KEY_LR_UD) {
                    ZoomModeState.mChangeListener.onChangeCenter(PortraitBeautyConstants.RIGHT_ON_CENTER);
                    break;
                } else if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                    ZoomModeState.mChangeListener.onChangeFaceFocusFrame(PortraitBeautyConstants.RIGHT_ON_CENTER);
                    break;
                } else {
                    pushRightKey();
                    break;
                }
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (!PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                    if (this.mSize.isSelected() && mSizeGaugeValue > 0) {
                        this.mSeekBarSize.moveDown();
                        mSizeGaugeValue--;
                        PortraitBeautyConstants.Resize_Key_Up = true;
                        ZoomModeState.mChangeListener.onChangeSize(mSizeGaugeValue);
                        handle = true;
                    } else if (this.mBrigtness.isSelected() && this.mBrightnessGaugeValue > 0) {
                        this.mSeekBarBrightness.moveDown();
                        this.mBrightnessGaugeValue--;
                        PortraitBeautyConstants.Brightness_Key_Up = true;
                        ZoomModeState.mChangeListener.onChangeBrightness(this.mBrightnessGaugeValue);
                        handle = true;
                    } else if (PortraitBeautyConstants.CENTER_KEY_LR_UD && (this.mPositionLeft.isSelected() || this.mPositionRight.isSelected())) {
                        ZoomModeState.mChangeListener.onChangeCenter(PortraitBeautyConstants.DOWN_ON_CENTER);
                    }
                    if (this.mSize.isSelected() || this.mBrigtness.isSelected() || this.mPositionLeft.isSelected() || this.mPositionRight.isSelected() || this.mSave.isSelected()) {
                        handle = true;
                    }
                    mBackgroundMainInvisibler();
                    break;
                } else {
                    return -1;
                }
                break;
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                handle = true;
                break;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                if (true == PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                    getHandler().sendEmptyMessage(PortraitBeautyConstants.MSG_ID_TO_OPEN_BROWSER_STATE);
                } else {
                    openMenuLayout(PortraitBeautyCatchLightState.ID_CONFIRMATIONLAYOUT, null);
                }
                handle = true;
                break;
            case 232:
                if (CatchLightPlayBackLayout.bTransitToShooting) {
                    handle = false;
                    break;
                } else if (this.mSave.isSelected()) {
                    handle = false;
                    break;
                } else if (CatchLightPlayBackLayout.isEVFShown) {
                    this.mParentLayout.setVisibility(4);
                    handle = true;
                    break;
                } else if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                    PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY = false;
                    ZoomModeState.mChangeListener.onChangeSingleFace();
                    mSizeGaugeValue = CatchLightPlayBackLayout.mGauzeCurlevel;
                    this.mSeekBarSize.setMax(CatchLightPlayBackLayout.mMaxGauzebarLevel);
                    this.mSeekBarSize.setProgress(mSizeGaugeValue);
                    this.mSelectedItemPosition = this.mPatternController.getSupportedValue(ZoomModePatternController.PATTERN_CHANGE).indexOf(this.mPatternValue) - 1;
                    applyEffectPattern(this.mSelectedItemPosition);
                    this.mParentLayout.setVisibility(0);
                    handle = true;
                    break;
                } else {
                    if (!PortraitBeautyConstants.CENTER_KEY_LR_UD && ((this.mPositionLeft.isSelected() || this.mPositionRight.isSelected()) && !this.mSave.isSelected())) {
                        PortraitBeautyConstants.CENTER_KEY_LR_UD = true;
                        ZoomModeState.mChangeListener.onChangeCenter(PortraitBeautyConstants.FOCUS_ARROW_VISIBLE);
                    } else if (!this.mSave.isSelected()) {
                        PortraitBeautyConstants.CENTER_KEY_LR_UD = false;
                        ZoomModeState.mChangeListener.onChangeCenter(PortraitBeautyConstants.FOCUS_ARROW_INVISIBLE);
                    }
                    setFooterGuide();
                    handle = true;
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.DISP /* 608 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                return -1;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (this.mSize.isSelected() && mSizeGaugeValue > 0) {
                    this.mSeekBarSize.moveDown();
                    mSizeGaugeValue--;
                    PortraitBeautyConstants.Resize_Key_Up = true;
                    ZoomModeState.mChangeListener.onChangeSize(mSizeGaugeValue);
                    handle = true;
                } else if (this.mBrigtness.isSelected() && this.mBrightnessGaugeValue > 0) {
                    this.mSeekBarBrightness.moveDown();
                    this.mBrightnessGaugeValue--;
                    PortraitBeautyConstants.Brightness_Key_Up = true;
                    ZoomModeState.mChangeListener.onChangeBrightness(this.mBrightnessGaugeValue);
                    handle = true;
                } else if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                    ZoomModeState.mChangeListener.onChangeFaceFocusFrame(PortraitBeautyConstants.RIGHT_ON_CENTER);
                    handle = true;
                } else if (this.mPositionLeft.isSelected() || this.mPositionRight.isSelected()) {
                    mBackgroundMainInvisibler();
                    return -1;
                }
                if (this.mSize.isSelected() || this.mBrigtness.isSelected() || this.mPositionLeft.isSelected() || this.mPositionRight.isSelected() || this.mSave.isSelected()) {
                    handle = true;
                }
                mBackgroundMainInvisibler();
                break;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (this.mSize.isSelected() && mSizeGaugeValue < CatchLightPlayBackLayout.mMaxGauzebarLevel) {
                    this.mSeekBarSize.moveUp();
                    mSizeGaugeValue++;
                    PortraitBeautyConstants.Resize_Key_Up = false;
                    ZoomModeState.mChangeListener.onChangeSize(mSizeGaugeValue);
                    handle = true;
                } else if (this.mBrigtness.isSelected() && this.mBrightnessGaugeValue < this.mBrightnessMax) {
                    this.mSeekBarBrightness.moveUp();
                    this.mBrightnessGaugeValue++;
                    PortraitBeautyConstants.Brightness_Key_Up = false;
                    ZoomModeState.mChangeListener.onChangeBrightness(this.mBrightnessGaugeValue);
                    handle = true;
                } else if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                    ZoomModeState.mChangeListener.onChangeFaceFocusFrame(PortraitBeautyConstants.LEFT_ON_CENTER);
                    handle = true;
                } else if (this.mPositionLeft.isSelected() || this.mPositionRight.isSelected()) {
                    mBackgroundMainInvisibler();
                    return -1;
                }
                if (this.mSize.isSelected() || this.mBrigtness.isSelected() || this.mPositionLeft.isSelected() || this.mPositionRight.isSelected() || this.mSave.isSelected()) {
                    handle = true;
                }
                mBackgroundMainInvisibler();
                break;
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                if (!PortraitBeautyConstants.CENTER_KEY_LR_UD) {
                    if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                        ZoomModeState.mChangeListener.onChangeFaceFocusFrame(PortraitBeautyConstants.RIGHT_ON_CENTER);
                        handle = true;
                        break;
                    } else {
                        pushRightKey();
                        handle = true;
                        break;
                    }
                } else {
                    return -1;
                }
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                if (!PortraitBeautyConstants.CENTER_KEY_LR_UD) {
                    if (PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                        ZoomModeState.mChangeListener.onChangeFaceFocusFrame(PortraitBeautyConstants.LEFT_ON_CENTER);
                        handle = true;
                        break;
                    } else {
                        pushLeftKey();
                        handle = true;
                        break;
                    }
                } else {
                    return -1;
                }
        }
        BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CURRENT_SIZE, Integer.valueOf(mSizeGaugeValue));
        if (handle) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int keyCode2 = event.getScanCode();
        int customhandled = 0;
        if (keyCode2 == 530 || keyCode2 == 786) {
            customhandled = -1;
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return customhandled;
    }

    private void changeSettingItems(int position) {
        switch (position) {
            case 1:
                makeAllSettingItemsInvisible();
                this.mSpecialScreenView.setVisibility(0);
                this.mViewArea.setVisibility(4);
                return;
            case 2:
                makeAllSettingItemsInvisible();
                this.mSeekBarSizeArea.setVisibility(0);
                return;
            case 3:
            case 7:
            default:
                return;
            case 4:
                makeAllSettingItemsInvisible();
                return;
            case 5:
                makeAllSettingItemsInvisible();
                break;
            case 6:
                break;
            case 8:
                makeAllSettingItemsInvisible();
                this.mSeekBarBrightnessArea.setVisibility(0);
                return;
        }
        makeAllSettingItemsInvisible();
    }

    private void makeAllSettingItemsInvisible() {
        this.mSeekBarSizeArea.setVisibility(8);
        this.mSeekBarBrightnessArea.setVisibility(8);
        this.mSpecialScreenView.setVisibility(8);
        this.mViewArea.setVisibility(8);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected ArrayList<String> getOptionMenuItemList(String itemId) {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        if (!PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
            AppLog.enter(this.TAG, AppLog.getMethodName());
            if (this.mSpecialScreenView != null) {
                if (this.mPattern.isSelected()) {
                    String itemId = this.mList.get(position);
                    this.mPatternValue = this.mService.getMenuItemValue(itemId);
                    this.mService.getMenuItemList().indexOf(this.mPatternValue);
                    this.mSelectedItemPosition = this.mPatternController.getSupportedValue(ZoomModePatternController.PATTERN_CHANGE).indexOf(this.mPatternValue) - 1;
                    update(position);
                    applyEffectPattern(this.mSelectedItemPosition);
                    super.onItemSelected(parent, view, position, id);
                }
                AppLog.exit(this.TAG, AppLog.getMethodName());
            }
        }
    }

    private void applyEffectPattern(int mSelectedItemPosition) {
        switch (mSelectedItemPosition) {
            case 0:
                this.mPatternImegeView.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_pattern2_menu);
                ZoomModeState.mChangeListener.onChangePattern(0);
                return;
            case 1:
                this.mPatternImegeView.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_pattern1_menu);
                ZoomModeState.mChangeListener.onChangePattern(1);
                return;
            case 2:
                this.mPatternImegeView.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_pattern4_menu);
                ZoomModeState.mChangeListener.onChangePattern(2);
                return;
            case 3:
                this.mPatternImegeView.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_pattern3_menu);
                ZoomModeState.mChangeListener.onChangePattern(3);
                return;
            case 4:
                this.mPatternImegeView.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_pattern6_menu);
                ZoomModeState.mChangeListener.onChangePattern(4);
                return;
            case 5:
                this.mPatternImegeView.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_pattern7_menu);
                ZoomModeState.mChangeListener.onChangePattern(5);
                return;
            case 6:
                this.mPatternImegeView.setImageResource(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_pattern5_menu);
                ZoomModeState.mChangeListener.onChangePattern(6);
                return;
            default:
                return;
        }
    }

    private void update(int position) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        String itemId = this.mList.get(position);
        this.mPatternValue = this.mService.getMenuItemValue(itemId);
        this.mPatternController.setValue(ZoomModePatternController.PATTERN_CHANGE, this.mPatternValue);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void updatePattern() {
        this.mList = this.mService.getMenuItemList();
        this.mSelectedLevel = this.mPatternValue;
        int position = this.mService.getMenuItemList().indexOf(this.mPatternValue);
        int mBlock = getMenuItemList().size();
        if (mBlock > 7) {
            mBlock = 7;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        funct_guide_displayer(this.mSelectedParamIocnPosition);
        this.mSelectedItemPosition = this.mPatternController.getSupportedValue(ZoomModePatternController.PATTERN_CHANGE).indexOf(this.mPatternValue) - 1;
        update(position);
        applyEffectPattern(this.mSelectedItemPosition);
        this.mPatternImegeView.invalidate();
        if (this.mSpecialScreenView.getFirstPosition() == -1) {
            this.mSpecialScreenView.setSelection(firstPosition, position);
        } else {
            this.mSpecialScreenView.setSelection(position);
        }
        this.mAdapter.notifyDataSetChanged();
        this.mViewArea.update();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        openMenuLayout(PortraitBeautyCatchLightState.ID_CONFIRMATIONLAYOUT, null);
        return 1;
    }

    private void setFooterGuide() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (1 == Environment.getVersionOfHW()) {
            if (this.mPattern.isSelected() || this.mSize.isSelected() || this.mBrigtness.isSelected()) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_CL_ADJUST_FGUIDE_EYCS));
            } else if (this.mPositionLeft.isSelected() || this.mPositionRight.isSelected()) {
                if (true == PortraitBeautyConstants.CENTER_KEY_LR_UD) {
                    this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_CL_ADJUST_BRIGHTNESS_FGUIDE_EYCS));
                } else {
                    this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_CL_ADJUSTPOS_FGUIDE_EYCS));
                }
            } else if (this.mSave.isSelected()) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_SELECT_PERSON_FGUIDE_EYCS));
            }
        } else if (this.mPattern.isSelected() || this.mSize.isSelected() || this.mBrigtness.isSelected()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_CL_ADJUST_FGUIDE_PJONE));
        } else if (this.mPositionLeft.isSelected() || this.mPositionRight.isSelected()) {
            if (true == PortraitBeautyConstants.CENTER_KEY_LR_UD) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_CL_ADJUST_BRIGHTNESS_FGUIDE_PJONE));
            } else {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_CL_ADJUSTPOS_FGUIDE_PJONE));
            }
        } else if (this.mSave.isSelected()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_SELECT_PERSON_FGUIDE_PJONE));
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return 0;
    }

    private void funct_guide_displayer(int position) {
        if (this.mGeneralGuideBackgroundImage.getVisibility() == 0) {
            mBackgroundMainInvisibler();
            this.mGeneralGuideBackgroundImage.setVisibility(4);
        }
        switch (position) {
            case 1:
                this.mLayoutParams_param_guide_background.setMargins(66, 346, 0, 0);
                this.mGeneralGuideBackgroundImage.setLayoutParams(this.mLayoutParams_param_guide_background);
                this.mGeneralGuideBackgroundImage.setVisibility(0);
                this.mMainGuideBackgroundView_pattern.setVisibility(0);
                this.mCountDown_started = true;
                this.Cdt_4sec_timer.start();
                return;
            case 2:
                this.mLayoutParams_param_guide_background.setMargins(162, 346, 0, 0);
                this.mGeneralGuideBackgroundImage.setLayoutParams(this.mLayoutParams_param_guide_background);
                this.mGeneralGuideBackgroundImage.setVisibility(0);
                this.mMainGuideBackgroundView_size.setVisibility(0);
                this.mCountDown_started = true;
                this.Cdt_4sec_timer.start();
                return;
            case 3:
            case 7:
            default:
                this.mCountDown_started = false;
                return;
            case 4:
                this.mLayoutParams_param_guide_background.setMargins(354, 346, 0, 0);
                this.mGeneralGuideBackgroundImage.setLayoutParams(this.mLayoutParams_param_guide_background);
                this.mGeneralGuideBackgroundImage.setVisibility(0);
                this.mMainGuideBackgroundView_left.setVisibility(0);
                this.mCountDown_started = true;
                this.Cdt_4sec_timer.start();
                return;
            case 5:
                this.mLayoutParams_param_guide_background.setMargins(450, 346, 0, 0);
                this.mGeneralGuideBackgroundImage.setLayoutParams(this.mLayoutParams_param_guide_background);
                this.mGeneralGuideBackgroundImage.setVisibility(0);
                this.mMainGuideBackgroundView_right.setVisibility(0);
                this.mCountDown_started = true;
                this.Cdt_4sec_timer.start();
                return;
            case 6:
                this.mLayoutParams_param_guide_background.setMargins(558, 346, 0, 0);
                this.mGeneralGuideBackgroundImage.setLayoutParams(this.mLayoutParams_param_guide_background);
                this.mGeneralGuideBackgroundImage.setVisibility(0);
                this.mMainGuideBackgroundView_save.setVisibility(0);
                this.mCountDown_started = true;
                this.Cdt_4sec_timer.start();
                return;
            case 8:
                this.mLayoutParams_param_guide_background.setMargins(258, 346, 0, 0);
                this.mGeneralGuideBackgroundImage.setLayoutParams(this.mLayoutParams_param_guide_background);
                this.mGeneralGuideBackgroundImage.setVisibility(0);
                this.mMainGuideBackgroundView_brightness.setVisibility(0);
                this.mCountDown_started = true;
                this.Cdt_4sec_timer.start();
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mBackgroundMainInvisibler() {
        if (this.mMainGuideBackgroundView_pattern != null) {
            this.mMainGuideBackgroundView_pattern.setVisibility(4);
        }
        if (this.mMainGuideBackgroundView_size != null) {
            this.mMainGuideBackgroundView_size.setVisibility(4);
        }
        if (this.mMainGuideBackgroundView_brightness != null) {
            this.mMainGuideBackgroundView_brightness.setVisibility(4);
        }
        if (this.mMainGuideBackgroundView_right != null) {
            this.mMainGuideBackgroundView_right.setVisibility(4);
        }
        if (this.mMainGuideBackgroundView_left != null) {
            this.mMainGuideBackgroundView_left.setVisibility(4);
        }
        if (this.mMainGuideBackgroundView_save != null) {
            this.mMainGuideBackgroundView_save.setVisibility(4);
        }
        if (this.mGeneralGuideBackgroundImage != null) {
            this.mGeneralGuideBackgroundImage.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        mBackgroundMainInvisibler();
        saveEVFValues();
        if (device == 2) {
            PortraitBeautyConstants.CENTER_KEY_LR_UD = false;
            updateView();
        }
        if (ZoomModeState.mChangeListener != null) {
            if (PortraitBeautyConstants.isNoFaceCautionVisible) {
                setVisibilityOfView(4);
                return;
            }
            if (device == 1) {
                setVisibilityOfView(4);
            } else if (!PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
                getEVFValues();
                setVisibilityOfView(0);
                CatchLightPlayBackLayout.isEVFShown = false;
            }
        }
    }

    private void setVisibilityOfView(int visibilityStatus) {
        if (getView() != null) {
            getView().setVisibility(visibilityStatus);
        }
    }

    private void saveEVFValues() {
        this.mEVFSelectedZoomItem = this.mSelectedParamIocnPosition;
        this.mEVFBrightnessGaugeValue = this.mBrightnessGaugeValue;
        this.mEVfSizeGaugeValue = mSizeGaugeValue;
    }

    private void getEVFValues() {
        this.mSelectedZoomItem = this.mEVFSelectedZoomItem;
        this.mSelectedParamIocnPosition = this.mEVFSelectedZoomItem;
        this.mBrightnessGaugeValue = this.mEVFBrightnessGaugeValue;
        mSizeGaugeValue = this.mEVfSizeGaugeValue;
        if (this.mSelectedZoomItem == 1) {
            AppLog.enter(this.TAG, "Pattern is focused_1");
            this.mPattern.setSelected(true);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            makeAllSettingItemsInvisible();
            this.mSpecialScreenView.setVisibility(0);
            this.mViewArea.setVisibility(4);
        } else if (this.mSelectedZoomItem == 8) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(true);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            makeAllSettingItemsInvisible();
            this.mSeekBarBrightnessArea.setVisibility(0);
        } else if (this.mSelectedZoomItem == 2) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(true);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            makeAllSettingItemsInvisible();
            this.mSeekBarSizeArea.setVisibility(0);
        } else if (this.mSelectedZoomItem == 4) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(true);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(false);
            makeAllSettingItemsInvisible();
            CatchLightPlayBackLayout.mIsLeftEyeSelected = true;
            ZoomModeState.mChangeListener.onChangeLeftPosition(0);
            this.mSelectedParamIocnPosition = 4;
        } else if (this.mSelectedZoomItem == 5) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(true);
            this.mSave.setSelected(false);
            makeAllSettingItemsInvisible();
            CatchLightPlayBackLayout.mIsLeftEyeSelected = false;
            ZoomModeState.mChangeListener.onChangeRightPosition(0);
            this.mSelectedParamIocnPosition = 5;
        } else if (this.mSelectedZoomItem == 6) {
            this.mPattern.setSelected(false);
            this.mSize.setSelected(false);
            this.mBrigtness.setSelected(false);
            this.mPositionLeft.setSelected(false);
            this.mPositionRight.setSelected(false);
            this.mSave.setSelected(true);
            this.mSelectedParamIocnPosition = 6;
        }
        changeSettingItems(this.mSelectedParamIocnPosition);
        setFooterGuide();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class DisplayChangedListener implements NotificationListener {
        protected DisplayChangedListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return ZoomModeMenuLayout.TAGS_OBSERVE_DISPLAY;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CatchLightPlayBackLayout.isHDMIShown && !CatchLightPlayBackLayout.isEVFShown) {
                PortraitBeautyConstants.CENTER_KEY_LR_UD = false;
                ZoomModeMenuLayout.this.updateView();
                CatchLightPlayBackLayout.isHDMIShown = false;
            }
        }
    }

    protected void registerDisplayChangedListener() {
        this.mDisplayChangedListener = new DisplayChangedListener();
        DisplayModeObserver.getInstance().setNotificationListener(this.mDisplayChangedListener);
    }

    protected void unregisterDisplayChangedListener() {
        if (this.mDisplayChangedListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mDisplayChangedListener);
            this.mDisplayChangedListener = null;
        }
    }
}
