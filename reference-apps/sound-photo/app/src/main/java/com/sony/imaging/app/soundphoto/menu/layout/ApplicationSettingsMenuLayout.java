package com.sony.imaging.app.soundphoto.menu.layout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.menu.layout.controller.ApplicationSettingsMenuController;
import com.sony.imaging.app.soundphoto.util.AppLog;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ApplicationSettingsMenuLayout extends DisplayMenuItemsMenuLayout implements View.OnClickListener {
    public static final String TAG = "ID_APPLICATIONSETTINGSMENULAYOUT";
    private View mSelectedView;
    private int duration = 8;
    private int post_duration = 4;
    private View mCurrentView = null;
    private TextView mDuration = null;
    private TextView mDurationTitle = null;
    private TextView mTimingTitle = null;
    private ImageView mLeftArrow = null;
    private ImageView mRightArrow = null;
    private ImageView mTimingProgress = null;
    private ImageView mProgressDivider = null;
    private ImageView mPreCaptureTimeBar = null;
    private ImageView mPostCaptureTimeBar = null;
    private ImageView mTimingPreCaptureDisable = null;
    private ImageView mTimingPostCaptureDisable = null;
    private TextView mPreCaptureTimeValue = null;
    private TextView mPostCaptureTimeValue = null;
    private View[] mViewArray = null;
    private FooterGuide mFooterGuide = null;
    private final int ROW_Duration = 0;
    private final int ROW_Timing = 1;
    private final int INT_VALUE_ZERO = 0;
    private final int REFERENCE_POSITION_PROGRESS = 390;
    private final int REFERENCE_POSITION_PRE_POST_BAR = 388;
    private final int REFERENCE_POSITION_PRE_POST_VALUE = 373;
    private final int REFERENCE_PRE_DISABLED_POSITION = 230;
    private final int DOT_LENGTH = 20;
    private int mOldDurationValue = 0;
    private int mOldTimingValue = 0;
    private int mCurrentRow = 0;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.layout_menu_application_settings);
        }
        initializeView();
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    private void initializeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mDuration = (TextView) this.mCurrentView.findViewById(R.id.duration_text);
        this.mLeftArrow = (ImageView) this.mCurrentView.findViewById(R.id.left_arrow);
        this.mRightArrow = (ImageView) this.mCurrentView.findViewById(R.id.right_arrow);
        this.mTimingTitle = (TextView) this.mCurrentView.findViewById(R.id.timing_title);
        this.mDurationTitle = (TextView) this.mCurrentView.findViewById(R.id.duration_title);
        this.mTimingProgress = (ImageView) this.mCurrentView.findViewById(R.id.timing_progress_bar);
        this.mTimingPreCaptureDisable = (ImageView) this.mCurrentView.findViewById(R.id.timing_precapture_disable);
        this.mTimingPostCaptureDisable = (ImageView) this.mCurrentView.findViewById(R.id.timing_postcapture_disable);
        this.mProgressDivider = (ImageView) this.mCurrentView.findViewById(R.id.progress_divider);
        this.mPreCaptureTimeBar = (ImageView) this.mCurrentView.findViewById(R.id.precapture_time_bar);
        this.mPostCaptureTimeBar = (ImageView) this.mCurrentView.findViewById(R.id.postcapture_time_bar);
        this.mPreCaptureTimeValue = (TextView) this.mCurrentView.findViewById(R.id.precapture_time_value);
        this.mPostCaptureTimeValue = (TextView) this.mCurrentView.findViewById(R.id.postcapture_time_value);
        this.mViewArray = new View[]{this.mLeftArrow, this.mRightArrow, this.mDurationTitle, this.mTimingTitle, this.mTimingProgress, this.mProgressDivider};
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_theme_option);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_CMN_FOOTER_GUIDE_7, R.string.STRID_CMN_FOOTER_GUIDE_7));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        disableTouch();
        AppLog.enter(TAG, AppLog.getMethodName());
        initializeListener();
        initialOperationOnResume();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void disableTouch() {
        if (this.mCurrentView != null) {
            this.mCurrentView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.soundphoto.menu.layout.ApplicationSettingsMenuLayout.1
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        if (this.mDurationTitle != null) {
            this.mDurationTitle.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.soundphoto.menu.layout.ApplicationSettingsMenuLayout.2
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        if (this.mTimingTitle != null) {
            this.mTimingTitle.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.soundphoto.menu.layout.ApplicationSettingsMenuLayout.3
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
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
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void initialOperationOnResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
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
        setTitleNameNormal();
        setFocusOnView();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setFocusOnView() {
        switch (this.mCurrentRow) {
            case 0:
                this.mDuration.setFocusable(true);
                this.mDuration.requestFocus();
                this.mDuration.setSelected(true);
                break;
            case 1:
                this.mTimingProgress.setFocusable(true);
                this.mTimingProgress.requestFocus();
                this.mTimingProgress.setSelected(true);
                break;
        }
        setOptionNameSelected();
    }

    private void setDefaultSelectValue() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setProgressImage(this.duration);
        setProgressPosition(this.post_duration);
        updatePrePostTimingValue();
        updateDisabledPortion();
        resetPrePostBarImageDrawable();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void getLastStoredValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int totalDuration = ApplicationSettingsMenuController.getInstance().getTotalDuration();
        this.mOldDurationValue = totalDuration;
        this.duration = totalDuration;
        int postDuration = ApplicationSettingsMenuController.getInstance().getPostDuration();
        this.mOldTimingValue = postDuration;
        this.post_duration = postDuration;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        closeMenuLayout(null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (ApplicationSettingsMenuController.getInstance() != null) {
            ApplicationSettingsMenuController.getInstance().updateThemeParameter(this.post_duration, this.duration);
            ApplicationSettingsMenuController.getInstance().updateBackupValue();
        }
        super.closeLayout();
        deinitializeView();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        restoreDefaultSelection();
        restoreOldValues();
        openPreviousMenu();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    private void restoreOldValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.duration = this.mOldDurationValue;
        this.post_duration = this.mOldTimingValue;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        AppLog.enter(TAG, AppLog.getMethodName());
        view.getId();
        AppLog.info(TAG, "onClick() shooting_num_bar   clicked");
        pushedCenterKey();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToLeft(KeyEvent event) {
        pushedUpKey();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onShuttleTurnedToRight(KeyEvent event) {
        pushedDownKey();
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
        pushedDownKey();
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        pushedUpKey();
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToRight(KeyEvent event) {
        pushedDownKey();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToLeft(KeyEvent event) {
        pushedUpKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        removeFocusFromAll();
        if (this.mCurrentRow == 0) {
            this.mCurrentRow++;
            setFocusableView(this.mTimingProgress);
        } else {
            setFocusableView(this.mTimingProgress);
        }
        setTitleNameNormal();
        setOptionNameSelected();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    private void setFocusableView(View view) {
        AppLog.enter(TAG, "" + view.getId());
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
        if (view.getId() == R.id.timing_progress_bar) {
            this.mProgressDivider.clearFocus();
            this.mProgressDivider.setFocusable(false);
            this.mProgressDivider.setSelected(false);
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
        if (this.mCurrentRow == 0) {
            Increase_duration();
        } else {
            increase_post_duration();
            setProgressPosition(this.post_duration);
            updateDisabledPortion();
        }
        updatePrePostTimingValue();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void increase_post_duration() {
        if (this.post_duration < this.duration) {
            this.post_duration++;
        }
        setProgressPosition(this.post_duration);
    }

    private void updateDisabledPortion() {
        if (this.mTimingPreCaptureDisable != null) {
            this.mTimingPreCaptureDisable.getLayoutParams().width = (10 - this.duration) * 20;
        }
        if (this.mTimingPostCaptureDisable != null) {
            this.mTimingPostCaptureDisable.getLayoutParams().width = (10 - this.duration) * 20;
        }
        RelativeLayout.LayoutParams params_pre_disable = (RelativeLayout.LayoutParams) this.mTimingPreCaptureDisable.getLayoutParams();
        params_pre_disable.leftMargin = 230;
        this.mTimingPreCaptureDisable.setLayoutParams(params_pre_disable);
        RelativeLayout.LayoutParams params_post_disable = (RelativeLayout.LayoutParams) this.mTimingPostCaptureDisable.getLayoutParams();
        params_post_disable.leftMargin = (this.duration * 20) + 390;
        this.mTimingPostCaptureDisable.setLayoutParams(params_post_disable);
    }

    private void Decrease_duration() {
        if (this.duration > 2) {
            if (this.post_duration > this.duration - this.post_duration && this.post_duration > 2) {
                this.post_duration--;
            }
            this.duration--;
            setProgressImage(this.duration);
            setProgressPosition(this.post_duration);
        }
        updateDisabledPortion();
    }

    private void Increase_duration() {
        if (this.duration < 10) {
            if (this.post_duration <= this.duration - this.post_duration) {
                this.post_duration++;
            }
            this.duration++;
            setProgressImage(this.duration);
            setProgressPosition(this.post_duration);
            updateDisabledPortion();
        }
    }

    private void setProgressImage(int duration) {
        if (this.mTimingProgress != null) {
            this.mTimingProgress.getLayoutParams().width = duration * 20;
        }
        if (this.mDuration != null) {
            this.mDuration.setText(getduration_values(duration));
        }
        if (duration == 2) {
            this.mLeftArrow.setVisibility(8);
            this.mRightArrow.setVisibility(0);
        } else if (duration == 10) {
            this.mLeftArrow.setVisibility(0);
            this.mRightArrow.setVisibility(8);
        } else {
            this.mLeftArrow.setVisibility(0);
            this.mRightArrow.setVisibility(0);
        }
        if (duration == 2) {
            this.mTimingProgress.setBackgroundResource(R.drawable.p_16_dd_parts_soundphoto_bar_enble);
        } else {
            this.mTimingProgress.setBackgroundResource(R.drawable.state_streak_progress_selector);
        }
    }

    private String getduration_values(int currentDuration) {
        switch (currentDuration) {
            case 2:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_2SEC);
            case 3:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_3SEC);
            case 4:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_4SEC);
            case 5:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_5SEC);
            case 6:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_6SEC);
            case 7:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_7SEC);
            case 8:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_8SEC);
            case 9:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_9SEC);
            case 10:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_10SEC);
            default:
                return getResources().getString(R.string.STRID_FUNC_SOUNDPHTO_TIME_10SEC);
        }
    }

    private void updatePrePostTimingValue() {
        if (this.duration - this.post_duration > 0) {
            this.mPreCaptureTimeValue.setText("-" + (this.duration - this.post_duration));
        } else {
            this.mPreCaptureTimeValue.setText(String.valueOf(this.duration - this.post_duration));
        }
        this.mPostCaptureTimeValue.setText(String.valueOf(this.post_duration));
    }

    private void setProgressPosition(int post_duration) {
        if (post_duration > 2 || this.duration > 2) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mTimingProgress.getLayoutParams();
            RelativeLayout.LayoutParams paramsprebar = (RelativeLayout.LayoutParams) this.mPreCaptureTimeBar.getLayoutParams();
            RelativeLayout.LayoutParams paramspostbar = (RelativeLayout.LayoutParams) this.mPostCaptureTimeBar.getLayoutParams();
            RelativeLayout.LayoutParams paramsprevalue = (RelativeLayout.LayoutParams) this.mPreCaptureTimeValue.getLayoutParams();
            RelativeLayout.LayoutParams paramspostvalue = (RelativeLayout.LayoutParams) this.mPostCaptureTimeValue.getLayoutParams();
            params.leftMargin = 390 - ((this.duration - post_duration) * 20);
            this.mTimingProgress.setLayoutParams(params);
            paramsprebar.leftMargin = 388 - ((this.duration - post_duration) * 20);
            this.mPreCaptureTimeBar.setLayoutParams(paramsprebar);
            paramspostbar.leftMargin = (post_duration * 20) + 388;
            this.mPostCaptureTimeBar.setLayoutParams(paramspostbar);
            paramsprevalue.leftMargin = 373 - ((this.duration - post_duration) * 20);
            this.mPreCaptureTimeValue.setLayoutParams(paramsprevalue);
            paramspostvalue.leftMargin = (post_duration * 20) + 373;
            this.mPostCaptureTimeValue.setLayoutParams(paramspostvalue);
            return;
        }
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) this.mTimingProgress.getLayoutParams();
        RelativeLayout.LayoutParams paramsprebar2 = (RelativeLayout.LayoutParams) this.mPreCaptureTimeBar.getLayoutParams();
        RelativeLayout.LayoutParams paramspostbar2 = (RelativeLayout.LayoutParams) this.mPostCaptureTimeBar.getLayoutParams();
        RelativeLayout.LayoutParams paramsprevalue2 = (RelativeLayout.LayoutParams) this.mPreCaptureTimeValue.getLayoutParams();
        RelativeLayout.LayoutParams paramspostvalue2 = (RelativeLayout.LayoutParams) this.mPostCaptureTimeValue.getLayoutParams();
        params2.leftMargin = 390;
        this.mTimingProgress.setLayoutParams(params2);
        paramsprebar2.leftMargin = 388;
        this.mPreCaptureTimeBar.setLayoutParams(paramsprebar2);
        paramspostbar2.leftMargin = (post_duration * 20) + 388;
        this.mPostCaptureTimeBar.setLayoutParams(paramspostbar2);
        paramsprevalue2.leftMargin = 373;
        this.mPreCaptureTimeValue.setLayoutParams(paramsprevalue2);
        paramspostvalue2.leftMargin = (post_duration * 20) + 373;
        this.mPostCaptureTimeValue.setLayoutParams(paramspostvalue2);
    }

    private void handleLeftAction() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCurrentRow == 0) {
            Decrease_duration();
        } else {
            dicrease_post_duration();
            setProgressPosition(this.post_duration);
            updateDisabledPortion();
        }
        updatePrePostTimingValue();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void dicrease_post_duration() {
        if (this.post_duration > 2) {
            this.post_duration--;
        }
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
        if (this.mCurrentRow == 1) {
            this.mCurrentRow--;
            setFocusableView(this.mDuration);
        } else {
            setFocusableView(this.mDuration);
        }
        setTitleNameNormal();
        setOptionNameSelected();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    private void setOptionNameSelected() {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (this.mCurrentRow) {
            case 0:
                this.mDurationTitle.setTextColor(Color.parseColor("#FF8000"));
                this.mDuration.setTextColor(Color.parseColor("#FF8000"));
                this.mLeftArrow.setImageResource(R.drawable.p_16_dd_parts_soundphoto_arrow_left_enable);
                this.mRightArrow.setImageResource(R.drawable.p_16_dd_parts_soundphoto_arrow_right_enable);
                setProgressImage(this.duration);
                break;
            case 1:
                this.mTimingTitle.setTextColor(Color.parseColor("#FF8000"));
                this.mLeftArrow.setImageResource(R.drawable.p_16_dd_parts_soundphoto_arrow_left_disable);
                this.mRightArrow.setImageResource(R.drawable.p_16_dd_parts_soundphoto_arrow_right_disable);
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setTitleNameNormal() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mTimingTitle.setTextColor(Color.parseColor("#FFFFFF"));
        this.mDurationTitle.setTextColor(Color.parseColor("#FFFFFF"));
        this.mDuration.setTextColor(Color.parseColor("#FFFFFF"));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void removeFocusFromAll() {
        AppLog.enter(TAG, AppLog.getMethodName());
        for (int i = 0; i < this.mViewArray.length; i++) {
            clearFocusedView(this.mViewArray[i]);
        }
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
        releaseImageViewDrawable(this.mLeftArrow);
        releaseImageViewDrawable(this.mRightArrow);
        releaseImageViewDrawable(this.mLeftArrow);
        releaseImageViewDrawable(this.mTimingProgress);
        releaseImageViewDrawable(this.mProgressDivider);
        releaseImageViewDrawable(this.mPreCaptureTimeBar);
        releaseImageViewDrawable(this.mPostCaptureTimeBar);
        releaseImageViewDrawable(this.mTimingPreCaptureDisable);
        releaseImageViewDrawable(this.mTimingPostCaptureDisable);
        this.mTimingProgress = null;
        this.mProgressDivider = null;
        this.mPreCaptureTimeBar = null;
        this.mPostCaptureTimeBar = null;
        this.mTimingPreCaptureDisable = null;
        this.mTimingPostCaptureDisable = null;
        this.mDuration = null;
        this.mDurationTitle = null;
        this.mTimingTitle = null;
        this.duration = 0;
        this.post_duration = 0;
        this.mOldDurationValue = 0;
        this.mOldTimingValue = 0;
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
                setGuideResource(guideResources, R.string.STRID_FUNC_SOUNDPHOTO_SOUND_DURATION, R.string.STRID_FUNC_SOUNDPHOTO_GUIDE_SOUND_DURATION, true);
                break;
            case 1:
                setGuideResource(guideResources, R.string.STRID_FUNC_SOUNDPHOTO_SHUTTER_TIMING, R.string.STRID_FUNC_SOUNDPHOTO_GUIDE_SHUTTER_TIMING, true);
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
        if (!isFunctionGuideShown()) {
            setFocusOnResume();
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

    private void resetPrePostBarImageDrawable() {
        this.mPreCaptureTimeBar.setImageResource(R.drawable.p_16_dd_parts_soundphoto_leader_for_num);
        this.mPostCaptureTimeBar.setImageResource(R.drawable.p_16_dd_parts_soundphoto_leader_for_num);
    }
}
