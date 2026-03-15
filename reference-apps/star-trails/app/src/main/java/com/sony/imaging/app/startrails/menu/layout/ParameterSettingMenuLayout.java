package com.sony.imaging.app.startrails.menu.layout;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.startrails.AppContext;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.shooting.widget.STImageButton;
import com.sony.imaging.app.startrails.shooting.widget.STSeekBar;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ParameterSettingMenuLayout extends DisplayMenuItemsMenuLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "ParameterSettingMenuLayout";
    private View mSelectedView;
    private View mCurrentView = null;
    private TextView mRecordingModeTV = null;
    private TextView mStreakLevelTV = null;
    private TextView mShootingNumberTV = null;
    private TextView mStreakValue = null;
    private TextView mShootingNumberValue = null;
    private TextView mShootingTimeValue = null;
    private TextView mThemeName = null;
    private TextView mPlayBackTextTime = null;
    private STImageButton mResetImageBtn = null;
    private STImageButton mMpeg24RecModeBtn = null;
    private STImageButton mMpeg30RecModeBtn = null;
    private STImageButton mMpeg24RecModeBtnBg = null;
    private STImageButton mMpeg30RecModeBtnBg = null;
    private ImageView mStreakLevelBar = null;
    private ImageView mStreakLevelBar_star = null;
    private STSeekBar mShootingNumberBar_Focused = null;
    private STSeekBar mShootingNumberBar_Normal = null;
    private View[] mViewArray = null;
    private FooterGuide mFooterGuide = null;
    private final int STEP_SIZE_24 = 24;
    private final int ROW_RESET = 0;
    private final int ROW_RECORDING = 1;
    private final int ROW_SHOT = 2;
    private final int ROW_STREAKLEVEL = 3;
    private final int STEP_SIZE_30 = 30;
    private final int INT_VALUE_ONE = 1;
    private final int INT_VALUE_ZERO = 0;
    private final int MAX_SHOT = 990;
    private int mStreakLevel = 3;
    private int mShootingNumber = 0;
    private int inRecRowSelection = 0;
    private int mOldRecordingMode = 0;
    private int mOldStreakLevel = 0;
    private int mOldShootingNumber = 0;
    private int mCurrentRow = 1;
    private ThemeParameterSettingUtility mThemeParameterutility = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.st_layout_menu_theme_parameter_settings);
        }
        initializeView();
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    private void initializeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mRecordingModeTV = (TextView) this.mCurrentView.findViewById(R.id.rec_mode_name);
        this.mStreakLevelTV = (TextView) this.mCurrentView.findViewById(R.id.streakname);
        this.mShootingNumberTV = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_name);
        this.mStreakValue = (TextView) this.mCurrentView.findViewById(R.id.streakValue);
        this.mShootingNumberValue = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_value);
        this.mShootingTimeValue = (TextView) this.mCurrentView.findViewById(R.id.shooting_time_value);
        this.mThemeName = (TextView) this.mCurrentView.findViewById(R.id.theme_name);
        this.mPlayBackTextTime = (TextView) this.mCurrentView.findViewById(R.id.playback_value);
        this.mResetImageBtn = (STImageButton) this.mCurrentView.findViewById(R.id.st_reset_button);
        this.mMpeg24RecModeBtn = (STImageButton) this.mCurrentView.findViewById(R.id.mpeg_24_btn);
        this.mMpeg30RecModeBtn = (STImageButton) this.mCurrentView.findViewById(R.id.mpeg_30_btn);
        this.mMpeg24RecModeBtnBg = (STImageButton) this.mCurrentView.findViewById(R.id.mpeg_24_btn_bg);
        this.mMpeg30RecModeBtnBg = (STImageButton) this.mCurrentView.findViewById(R.id.mpeg_30_btn_bg);
        this.mShootingNumberBar_Focused = (STSeekBar) this.mCurrentView.findViewById(R.id.shooting_num_bar_focused);
        this.mShootingNumberBar_Focused.setProgress(1);
        this.mShootingNumberBar_Focused.incrementProgressBy(1);
        this.mShootingNumberBar_Focused.setMax(990);
        this.mShootingNumberBar_Normal = (STSeekBar) this.mCurrentView.findViewById(R.id.shooting_num_bar_normal);
        this.mShootingNumberBar_Normal.setProgress(1);
        this.mShootingNumberBar_Normal.incrementProgressBy(1);
        this.mShootingNumberBar_Normal.setMax(990);
        this.mStreakLevelBar = (ImageView) this.mCurrentView.findViewById(R.id.streak_bar);
        this.mStreakLevelBar_star = (ImageView) this.mCurrentView.findViewById(R.id.streak_bar_star);
        this.mViewArray = new View[]{this.mMpeg24RecModeBtn, this.mMpeg30RecModeBtn, this.mResetImageBtn, this.mShootingNumberBar_Focused, this.mShootingNumberBar_Normal, this.mStreakLevelBar, this.mStreakLevelBar_star};
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_theme_option);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE_SK));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        AppLog.enter(TAG, AppLog.getMethodName());
        initializeListener();
        initialOperationOnResume();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setStreakProgress(int i) {
        releaseImageViewDrawable(this.mStreakLevelBar);
        this.mStreakLevelBar = (ImageView) this.mCurrentView.findViewById(R.id.streak_bar);
        this.mStreakLevelBar.setImageDrawable(getProgressDrawable(i));
        this.mStreakValue.setText(STUtility.getInstance().getStreakValue(i));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mStreakLevelBar_star.getLayoutParams();
        params.leftMargin = (i * 64) + 146;
        this.mStreakLevelBar_star.setLayoutParams(params);
    }

    private Drawable getProgressDrawable(int i) {
        switch (i) {
            case 1:
                Drawable d = getResources().getDrawable(R.drawable.state_streak_progress_1_selector);
                return d;
            case 2:
                Drawable d2 = getResources().getDrawable(R.drawable.state_streak_progress_2_selector);
                return d2;
            case 3:
                Drawable d3 = getResources().getDrawable(R.drawable.state_streak_progress_3_selector);
                return d3;
            case 4:
                Drawable d4 = getResources().getDrawable(R.drawable.state_streak_progress_4_selector);
                return d4;
            case 5:
                Drawable d5 = getResources().getDrawable(R.drawable.state_streak_progress_5_selector);
                return d5;
            case 6:
                Drawable d6 = getResources().getDrawable(R.drawable.state_streak_progress_6_selector);
                return d6;
            case 7:
                Drawable d7 = getResources().getDrawable(R.drawable.state_streak_progress_full_selector);
                return d7;
            default:
                return null;
        }
    }

    private void initializeListener() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mViewArray != null) {
            for (int i = 0; i < this.mViewArray.length - 2; i++) {
                if (this.mViewArray[i] != null) {
                    this.mViewArray[i].setOnClickListener(this);
                }
            }
        }
        this.mShootingNumberBar_Focused.setOnSeekBarChangeListener(this);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void initialOperationOnResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mThemeParameterutility = ThemeParameterSettingUtility.getInstance();
        this.mThemeParameterutility.initializeThemeParameters();
        this.mThemeName.setText(getResources().getString(STUtility.getInstance().getThemeNameResID(STUtility.getInstance().getCurrentTrail())));
        getLastStoredValues();
        releaseSelectionFromAll();
        makeViewsUnfocusable();
        restoreDefaultSelection();
        setDefaultSelectValue();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void releaseSelectionFromAll() {
        AppLog.enter(TAG, AppLog.getMethodName());
        for (int i = 0; i < this.mViewArray.length - 2; i++) {
            this.mViewArray[i].setSelected(false);
        }
        setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_normal);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void makeViewsUnfocusable() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (getView() != null) {
            getView().setFocusable(false);
            getView().setFocusableInTouchMode(false);
        }
        for (int i = 0; i < this.mViewArray.length - 2; i++) {
            this.mViewArray[i].setFocusable(false);
            this.mViewArray[i].setFocusableInTouchMode(false);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void restoreDefaultSelection() {
        AppLog.enter(TAG, AppLog.getMethodName());
        removeFocusFromAll();
        setFirstRowNormal();
        setTitleNameNormal();
        setFocusableView(this.mShootingNumberBar_Focused);
        clearFocusedView(this.mShootingNumberBar_Focused);
        setNormalVisible();
        this.mStreakLevelBar_star.setImageResource(R.drawable.state_streak_num_thumb);
        setFocusOnView();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setFocusOnView() {
        switch (this.mCurrentRow) {
            case 0:
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_focused);
                break;
            case 1:
                if (this.inRecRowSelection == 1) {
                    setSelectedItemOnResume(this.mMpeg30RecModeBtn, R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_focused);
                    this.mMpeg30RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                    break;
                } else {
                    setSelectedItemOnResume(this.mMpeg24RecModeBtn, R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_focused);
                    this.mMpeg24RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                    break;
                }
            case 2:
                setSelectedVisible();
                setFocusableView(this.mShootingNumberBar_Focused);
                break;
            case 3:
                setSelectedImageOnRecording();
                setFocusableView(this.mStreakLevelBar);
                break;
        }
        setOptionNameSelected();
    }

    private void setSelectedItemOnResume(ImageButton view, int drawableID) {
        view.setImageResource(drawableID);
        setFocusableView(view);
    }

    private void setDefaultSelectValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.inRecRowSelection == 0) {
            this.mMpeg24RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        } else {
            this.mMpeg30RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        }
        setStreakProgress(this.mStreakLevel);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void getLastStoredValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int recordingMode = this.mThemeParameterutility.getRecordingMode();
        this.mOldRecordingMode = recordingMode;
        this.inRecRowSelection = recordingMode;
        int streakLevel = this.mThemeParameterutility.getStreakLevel();
        this.mOldStreakLevel = streakLevel;
        this.mStreakLevel = streakLevel;
        int numberOfShot = this.mThemeParameterutility.getNumberOfShot();
        this.mOldShootingNumber = numberOfShot;
        this.mShootingNumber = numberOfShot;
        if (this.mCurrentRow == 0) {
            this.mCurrentRow = 1;
        }
        setValuesOnScreen();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setValuesOnScreen() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setStreakProgress(this.mStreakLevel);
        this.mShootingNumberBar_Focused.setProgress(this.mShootingNumber);
        this.mShootingNumberBar_Normal.setProgress(this.mShootingNumber);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentRow == 0) {
            return -1;
        }
        if (checkLastMenu()) {
            openPreviousMenu();
        } else {
            closeMenuLayout(null);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    private boolean checkLastMenu() {
        return getMenuData().getPreviousMenuLayoutId().equals(ThemeSelectMenuLayout.APPTOP_MENU_ID);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        ThemeParameterSettingUtility.getInstance().updateThemeParameter(this.inRecRowSelection, this.mShootingNumber, this.mStreakLevel);
        ThemeParameterSettingUtility.getInstance().updateBackupValue();
        super.closeLayout();
        deinitializeView();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        restoreDefaultSelection();
        setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_normal);
        restoreOldValues();
        openPreviousMenu();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    private void restoreOldValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.inRecRowSelection = this.mOldRecordingMode;
        this.mStreakLevel = this.mOldStreakLevel;
        this.mShootingNumber = this.mOldShootingNumber;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (fromUser && progress >= 0 && progress <= 990) {
            if (progress < this.mShootingNumber) {
                progress = ((this.mShootingNumber - 30) / 30) * 30;
            } else {
                progress = ((this.mShootingNumber + 30) / 30) * 30;
            }
        }
        if (progress <= 0) {
            progress = 1;
        }
        AppLog.trace(TAG, "shooting_num_bar bar onProgressChanged  " + progress);
        this.mShootingNumber = progress;
        this.mShootingNumberBar_Focused.setProgress(this.mShootingNumber);
        this.mShootingNumberBar_Normal.setProgress(this.mShootingNumber);
        this.mShootingNumberValue.setText("" + this.mShootingNumber);
        this.mPlayBackTextTime.setText("" + getPlackBackDuration());
        this.mShootingTimeValue.setText(STUtility.getInstance().calculateShootingTime(this.mShootingNumber));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getPlackBackDuration() {
        int playTime;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.inRecRowSelection == 0) {
            playTime = this.mShootingNumber / 24;
        } else {
            playTime = this.mShootingNumber / 30;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return String.format(AppContext.getAppContext().getResources().getString(R.string.STRID_FUNC_TIMELAPSE_INTERVAL_VALUE), Integer.valueOf(playTime));
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        AppLog.info(TAG, AppLog.getMethodName());
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        AppLog.info(TAG, AppLog.getMethodName());
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (view.getId()) {
            case R.id.st_reset_button /* 2131362329 */:
                AppLog.info(TAG, "onClick() reset button clicked");
                resetThemeProperty();
                break;
            default:
                AppLog.info(TAG, "onClick() shooting_num_bar   clicked");
                pushedCenterKey();
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void resetThemeProperty() {
        AppLog.enter(TAG, AppLog.getMethodName());
        resetDefaultValues();
        setFocusableView(this.mResetImageBtn);
        setDefaultSelectValue();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void resetDefaultValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (STUtility.getInstance().getCurrentTrail()) {
            case 1:
                this.inRecRowSelection = 0;
                this.mShootingNumber = 480;
                this.mStreakLevel = 4;
                break;
            case 2:
                this.inRecRowSelection = 0;
                this.mShootingNumber = 480;
                this.mStreakLevel = 4;
                break;
            default:
                this.inRecRowSelection = 0;
                this.mShootingNumber = 480;
                this.mStreakLevel = 4;
                break;
        }
        setValuesOnScreen();
        restoreDefaultSelection();
        AppLog.exit(TAG, AppLog.getMethodName());
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        handleRightAction();
        return super.turnedSubDialNext();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        handleLeftAction();
        return super.turnedSubDialPrev();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        handleRightAction();
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        handleLeftAction();
        return super.turnedMainDialPrev();
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentRow == 3) {
            return -1;
        }
        removeFocusFromAll();
        if (this.mCurrentRow < 3) {
            this.mCurrentRow++;
        }
        switch (this.mCurrentRow) {
            case 1:
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_normal);
                setFirstRowNormal();
                if (this.inRecRowSelection == 0) {
                    setFocusableView(this.mMpeg24RecModeBtn);
                    this.mMpeg24RecModeBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_focused);
                    this.mMpeg24RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                    break;
                } else {
                    setFocusableView(this.mMpeg30RecModeBtn);
                    this.mMpeg30RecModeBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_focused);
                    this.mMpeg30RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                    break;
                }
            case 2:
                setSelectedImageOnRecording();
                setSelectedVisible();
                setFocusableView(this.mShootingNumberBar_Focused);
                break;
            case 3:
                setNormalVisible();
                setSelectedImageOnRecording();
                setFocusableView(this.mStreakLevelBar);
                break;
        }
        setTitleNameNormal();
        setOptionNameSelected();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    private void setResetButtonBackground(int resetButtonID) {
        this.mResetImageBtn.setImageResource(resetButtonID);
    }

    private void setFocusableView(View view) {
        if (view.getId() == R.id.streak_bar) {
            this.mStreakLevelBar_star.setFocusableInTouchMode(true);
            this.mStreakLevelBar_star.setFocusable(true);
            this.mStreakLevelBar_star.requestFocus();
            this.mStreakLevelBar_star.setSelected(true);
        }
        AppLog.enter(TAG, "" + view.getId());
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.requestFocus();
        view.setSelected(true);
        this.mSelectedView = view;
        AppLog.exit(TAG, "" + view.getId());
    }

    private void clearFocusedView(View view) {
        AppLog.enter(TAG, "" + view.getId());
        view.clearFocus();
        view.setFocusable(false);
        view.setSelected(false);
        if (view.getId() == R.id.streak_bar) {
            this.mStreakLevelBar_star.clearFocus();
            this.mStreakLevelBar_star.setFocusable(false);
            this.mStreakLevelBar_star.setSelected(false);
        }
        AppLog.exit(TAG, "" + view.getId());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        handleRightAction();
        return 1;
    }

    private void handleRightAction() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mMpeg24RecModeBtn.hasFocus()) {
            this.inRecRowSelection = 1;
            setFirstRowNormal();
            clearFocusedView(this.mMpeg24RecModeBtn);
            setFocusableView(this.mMpeg30RecModeBtn);
            this.mMpeg30RecModeBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_focused);
            this.mMpeg30RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            this.mPlayBackTextTime.setText("" + getPlackBackDuration());
        } else if (this.mStreakLevelBar.hasFocus()) {
            increaseStreakLevel();
        } else if (this.mShootingNumberBar_Focused.hasFocus()) {
            incrementShootingNum();
            this.mPlayBackTextTime.setText("" + getPlackBackDuration());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void handleLeftAction() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mMpeg30RecModeBtn.hasFocus()) {
            this.inRecRowSelection = 0;
            setFirstRowNormal();
            clearFocusedView(this.mMpeg30RecModeBtn);
            setFocusableView(this.mMpeg24RecModeBtn);
            this.mMpeg24RecModeBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_focused);
            this.mMpeg24RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
            this.mPlayBackTextTime.setText("" + getPlackBackDuration());
        } else if (this.mStreakLevelBar.hasFocus()) {
            decreaseStreakLevel();
        } else if (this.mShootingNumberBar_Focused.hasFocus()) {
            decrementShootingNum();
            this.mPlayBackTextTime.setText("" + getPlackBackDuration());
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void increaseStreakLevel() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStreakLevel != 7) {
            this.mStreakLevel++;
        }
        setStreakProgress(this.mStreakLevel);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void decreaseStreakLevel() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mStreakLevel != 1) {
            this.mStreakLevel--;
        }
        setStreakProgress(this.mStreakLevel);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void incrementShootingNum() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int curValue = this.mShootingNumberBar_Focused.getProgress();
        if (curValue < this.mShootingNumberBar_Focused.getMax()) {
            int progress = ((curValue + 30) / 30) * 30;
            this.mShootingNumberBar_Focused.setProgress(progress);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void decrementShootingNum() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int curValue = this.mShootingNumberBar_Focused.getProgress();
        if (curValue > 1) {
            int progress = ((curValue - 30) / 30) * 30;
            this.mShootingNumberBar_Focused.setProgress(progress);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        handleLeftAction();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        removeFocusFromAll();
        if (this.mCurrentRow == 0) {
            return -1;
        }
        if (this.mCurrentRow > 0) {
            this.mCurrentRow--;
        }
        switch (this.mCurrentRow) {
            case 0:
                setSelectedImageOnRecording();
                setFocusableView(this.mResetImageBtn);
                setResetButtonBackground(R.drawable.p_16_dd_parts_tm_reset_focused);
                break;
            case 1:
                setFirstRowNormal();
                setFocusableView(this.mShootingNumberBar_Normal);
                if (this.inRecRowSelection == 0) {
                    setFocusableView(this.mMpeg24RecModeBtn);
                    this.mMpeg24RecModeBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_focused);
                    this.mMpeg24RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                } else {
                    setFocusableView(this.mMpeg30RecModeBtn);
                    this.mMpeg30RecModeBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_focused);
                    this.mMpeg30RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
                }
                setNormalVisible();
                break;
            case 2:
                setSelectedImageOnRecording();
                clearFocusedView(this.mStreakLevelBar);
                setSelectedVisible();
                setFocusableView(this.mShootingNumberBar_Focused);
                break;
            case 3:
                setSelectedImageOnRecording();
                setFocusableView(this.mStreakLevelBar);
                break;
        }
        setTitleNameNormal();
        setOptionNameSelected();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    private void setFirstRowNormal() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mMpeg24RecModeBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        this.mMpeg30RecModeBtn.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        this.mMpeg24RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mMpeg30RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setSelectedImageOnRecording() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setFirstRowNormal();
        if (this.inRecRowSelection == 0) {
            this.mMpeg24RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        } else {
            this.mMpeg30RecModeBtnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setOptionNameSelected() {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (this.mCurrentRow) {
            case 1:
                this.mRecordingModeTV.setTextColor(Color.parseColor("#FF8000"));
                break;
            case 2:
                this.mShootingNumberTV.setTextColor(Color.parseColor("#FF8000"));
                break;
            case 3:
                this.mStreakLevelTV.setTextColor(Color.parseColor("#FF8000"));
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setTitleNameNormal() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mRecordingModeTV.setTextColor(Color.parseColor("#FFFFFF"));
        this.mStreakLevelTV.setTextColor(Color.parseColor("#FFFFFF"));
        this.mShootingNumberTV.setTextColor(Color.parseColor("#FFFFFF"));
        setBottomValuesNormal();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setBottomValuesNormal() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mStreakLevelTV.setTextColor(Color.parseColor("#FFFFFF"));
        this.mShootingNumberValue.setTextColor(Color.parseColor("#FFFFFF"));
        this.mShootingTimeValue.setTextColor(Color.parseColor("#FFFFFF"));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void removeFocusFromAll() {
        AppLog.enter(TAG, AppLog.getMethodName());
        for (int i = 0; i < this.mViewArray.length - 2; i++) {
            clearFocusedView(this.mViewArray[i]);
        }
        clearFocusedView(this.mShootingNumberBar_Focused);
        clearFocusedView(this.mStreakLevelBar);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        deinitializeView();
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deinitializeView();
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(TAG, AppLog.getMethodName());
        deinitializeView();
        AppLog.exit(TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (imageView != null) {
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void deinitializeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mFooterGuide = null;
        this.mRecordingModeTV = null;
        this.mStreakLevelTV = null;
        this.mShootingNumberTV = null;
        this.mStreakValue = null;
        this.mShootingNumberValue = null;
        this.mShootingTimeValue = null;
        this.mThemeName = null;
        this.mPlayBackTextTime = null;
        releaseImageViewDrawable(this.mResetImageBtn);
        releaseImageViewDrawable(this.mMpeg24RecModeBtn);
        releaseImageViewDrawable(this.mMpeg30RecModeBtn);
        releaseImageViewDrawable(this.mMpeg24RecModeBtnBg);
        releaseImageViewDrawable(this.mMpeg30RecModeBtnBg);
        releaseImageViewDrawable(this.mMpeg24RecModeBtnBg);
        releaseImageViewDrawable(this.mStreakLevelBar);
        releaseImageViewDrawable(this.mStreakLevelBar_star);
        this.mStreakLevelBar = null;
        this.mStreakLevelBar_star = null;
        if (this.mShootingNumberBar_Focused != null) {
            this.mShootingNumberBar_Focused.setOnSeekBarChangeListener(null);
            this.mShootingNumberBar_Focused = null;
        }
        if (this.mShootingNumberBar_Normal != null) {
            this.mShootingNumberBar_Normal.setOnSeekBarChangeListener(null);
            this.mShootingNumberBar_Normal = null;
        }
        this.mStreakLevel = 0;
        this.mShootingNumber = 0;
        this.inRecRowSelection = 0;
        this.mOldRecordingMode = 0;
        this.mOldStreakLevel = 0;
        this.mOldShootingNumber = 0;
        this.mThemeParameterutility = null;
        if (this.mViewArray != null) {
            for (int i = 0; i < this.mViewArray.length - 2; i++) {
                if (this.mViewArray[i] != null) {
                    this.mViewArray[i].setOnClickListener(null);
                    this.mViewArray[i] = null;
                }
            }
            this.mViewArray = null;
        }
        this.mSelectedView = null;
        this.mCurrentView = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (this.mCurrentRow) {
            case 0:
                setGuideResource(guideResources, R.string.STRID_AMC_STR_01117, R.string.STRID_FUNC_TIMELAPSE_SETTING_RESET_GUIDE, true);
                break;
            case 1:
                if (this.inRecRowSelection == 0) {
                    setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_RECMODE_AVI24, R.string.STRID_FUNC_TIMELAPSE_RECMODE_AVI24_GUIDE, true);
                    break;
                } else {
                    setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_RECMODE_AVI30, R.string.STRID_FUNC_TIMELAPSE_RECMODE_AVI30_GUIDE, true);
                    break;
                }
            case 2:
                setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_IMAGENUM, R.string.STRID_FUNC_TIMELAPSE_IMAGENUM_GUIDE, true);
                break;
            case 3:
                setGuideResource(guideResources, R.string.STRID_FUNC_STRS_OPTION_STREAKS_LEVEL, R.string.STRID_FUNC_STRS_OPTION_STREAKS_LEVELS_GUIDE, true);
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setGuideResource(ArrayList<Object> guideResources, int guideTitleID, int guideDefi, boolean isAvailble) {
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(Boolean.valueOf(isAvailble));
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int returnState = 1;
        if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            AppLog.enter(TAG, "Delete key releases");
        } else {
            returnState = super.onKeyUp(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
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
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                setFocusOnResume();
                break;
        }
        if (0 != 0) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setFocusOnResume() {
        if (this.mSelectedView != null) {
            initializeListener();
            setFocusableView(this.mSelectedView);
        }
    }

    private void setNormalVisible() {
        this.mShootingNumberBar_Normal.setVisibility(0);
        this.mShootingNumberBar_Normal.setProgress(this.mShootingNumberBar_Normal.getProgress());
        this.mShootingNumberBar_Focused.setVisibility(8);
    }

    private void setSelectedVisible() {
        this.mShootingNumberBar_Focused.setVisibility(0);
        this.mShootingNumberBar_Focused.setProgress(this.mShootingNumberBar_Normal.getProgress());
        this.mShootingNumberBar_Normal.setVisibility(8);
    }
}
