package com.sony.imaging.app.timelapse.angleshift.layout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFaderController;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFrameRateController;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftMovieController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class AngleShiftThemeOptionLayout extends DisplayMenuItemsMenuLayout implements View.OnClickListener {
    private static View[] mViewArray = null;
    private View mCurrentView;
    private TextView mFrameRate = null;
    private TextView mMovieSize = null;
    private TextView mFader = null;
    private ImageButton mResetImageBtn = null;
    private ImageButton mMovie24p = null;
    private ImageButton mMovie30p = null;
    private ImageButton mMovie1920x1080 = null;
    private ImageButton mMovie1280x720 = null;
    private ImageButton mFaderOn = null;
    private ImageButton mFaderOff = null;
    private ImageButton mMovie24pBg = null;
    private ImageButton mMovie30pBg = null;
    private ImageButton mMovie1920x1080Bg = null;
    private ImageButton mMovie1280x720Bg = null;
    private ImageButton mFaderOnBg = null;
    private ImageButton mFaderOffBg = null;
    private View mLastFocusedView = null;
    private FooterGuide mFooterGuide = null;
    private TextView mNumberOfImages = null;
    private TextView mPlayBackTime = null;
    private int mNumberOfImagesValue = 0;
    private int mPlayBackTimeValue = 0;
    private ImageView mRecMode = null;
    private String mFrameRateValue = null;
    private String mMovieSizeValue = null;
    private String mFaderValue = null;
    private final int ROW_RESET = 0;
    private final int ROW_FRAMERATE = 1;
    private final int ROW_SIZE = 2;
    private final int ROW_FADER = 3;
    private int mCurrentRow = 1;
    private int numberOfItems = 3;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.as_layout_menu_theme_option);
        if (AngleShiftFaderController.getInstance().isAvailable(null)) {
            this.numberOfItems = 3;
        } else {
            this.numberOfItems = 2;
        }
        this.mService = new BaseMenuService(getActivity().getApplicationContext());
        return this.mCurrentView;
    }

    private void initializeView() {
        this.mFrameRate = (TextView) this.mCurrentView.findViewById(R.id.framerate_name);
        this.mMovieSize = (TextView) this.mCurrentView.findViewById(R.id.moviesize_name);
        this.mFader = (TextView) this.mCurrentView.findViewById(R.id.fader_name);
        this.mResetImageBtn = (ImageButton) this.mCurrentView.findViewById(R.id.tl_reset_button);
        this.mMovie24p = (ImageButton) this.mCurrentView.findViewById(R.id.movie24p_btn);
        this.mMovie30p = (ImageButton) this.mCurrentView.findViewById(R.id.movie30p_btn);
        this.mMovie1920x1080 = (ImageButton) this.mCurrentView.findViewById(R.id.size1920x1080_btn);
        this.mMovie1280x720 = (ImageButton) this.mCurrentView.findViewById(R.id.size1280x720_btn);
        this.mFaderOn = (ImageButton) this.mCurrentView.findViewById(R.id.fader_on_btn);
        this.mFaderOff = (ImageButton) this.mCurrentView.findViewById(R.id.fader_off_btn);
        this.mMovie24pBg = (ImageButton) this.mCurrentView.findViewById(R.id.movie24p_btn_bg);
        this.mMovie30pBg = (ImageButton) this.mCurrentView.findViewById(R.id.movie30p_btn_bg);
        this.mMovie1920x1080Bg = (ImageButton) this.mCurrentView.findViewById(R.id.size1920x1080_btn_bg);
        this.mMovie1280x720Bg = (ImageButton) this.mCurrentView.findViewById(R.id.size1280x720_btn_bg);
        this.mFaderOnBg = (ImageButton) this.mCurrentView.findViewById(R.id.fader_on_btn_bg);
        this.mFaderOffBg = (ImageButton) this.mCurrentView.findViewById(R.id.fader_off_btn_bg);
        this.mNumberOfImages = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_value);
        this.mPlayBackTime = (TextView) this.mCurrentView.findViewById(R.id.playback_value);
        this.mRecMode = (ImageView) this.mCurrentView.findViewById(R.id.recording_mode);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_theme_option);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_OPT_FOOTER_GUIDE_SK));
        mViewArray = new View[]{this.mResetImageBtn, this.mMovie24p, this.mMovie30p, this.mMovie1920x1080, this.mMovie1280x720, this.mFaderOn, this.mFaderOff};
        for (int i = 0; i < mViewArray.length; i++) {
            mViewArray[i].setSelected(false);
            mViewArray[i].setOnClickListener(this);
        }
        if (!AngleShiftFaderController.getInstance().isAvailable(null)) {
            this.mFader.setVisibility(4);
            this.mFaderOn.setVisibility(4);
            this.mFaderOff.setVisibility(4);
            this.mFaderOnBg.setVisibility(4);
            this.mFaderOffBg.setVisibility(4);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        initializeView();
        makeViewsUnfocusable();
        getLastStoredValues();
        update();
        if (!isFunctionGuideShown()) {
            setFocusOnResume();
        }
    }

    private void setNormalIcon() {
        this.mResetImageBtn.setImageResource(R.drawable.p_16_dd_parts_tm_reset_normal);
        this.mMovie24p.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        this.mMovie30p.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        this.mMovie1920x1080.setImageResource(R.drawable.p_16_dd_parts_tm_1920x1080_normal);
        this.mMovie1280x720.setImageResource(R.drawable.p_16_dd_parts_tm_1280x720_normal);
        this.mFaderOn.setImageResource(R.drawable.p_16_dd_parts_tm_fade_on_normal);
        this.mFaderOff.setImageResource(R.drawable.p_16_dd_parts_tm_fade_off_normal);
        this.mMovie24pBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mMovie30pBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mMovie1920x1080Bg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mMovie1280x720Bg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mFaderOnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
        this.mFaderOffBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_normal);
    }

    private void makeViewsUnfocusable() {
        if (getView() != null) {
            getView().setFocusable(false);
            getView().setFocusableInTouchMode(false);
        }
        for (int i = 0; i < mViewArray.length - 2; i++) {
            mViewArray[i].setFocusable(false);
            mViewArray[i].setFocusableInTouchMode(false);
        }
    }

    private void getLastStoredValues() {
        this.mFrameRateValue = AngleShiftFrameRateController.getInstance().getValue();
        this.mMovieSizeValue = AngleShiftMovieController.getInstance().getValue();
        this.mFaderValue = AngleShiftFaderController.getInstance().getValue();
        if ((this.mLastFocusedView != null && this.mLastFocusedView.getId() == R.id.tl_reset_button) || this.mCurrentRow == 0) {
            this.mLastFocusedView = this.mMovie24p;
            this.mCurrentRow = 1;
        }
    }

    private int getPlayBackTime(int numOfImages, String frameRate) {
        if ("framerate-24p".equalsIgnoreCase(frameRate)) {
            int playbackTime = numOfImages / 24;
            return playbackTime;
        }
        int playbackTime2 = numOfImages / 30;
        return playbackTime2;
    }

    private void update() {
        removeFocusFromAll();
        setNormalIcon();
        switch (this.mCurrentRow) {
            case 0:
                this.mResetImageBtn.setImageResource(R.drawable.p_16_dd_parts_tm_reset_focused);
                setFocusableView(this.mResetImageBtn);
                break;
            case 1:
                if ("framerate-24p".equalsIgnoreCase(this.mFrameRateValue)) {
                    this.mMovie24p.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_focused);
                    setFocusableView(this.mMovie24p);
                    break;
                } else {
                    this.mMovie30p.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_focused);
                    setFocusableView(this.mMovie30p);
                    break;
                }
            case 2:
                if (AngleShiftMovieController.MOVIE_1920x1080.equalsIgnoreCase(this.mMovieSizeValue)) {
                    this.mMovie1920x1080.setImageResource(R.drawable.p_16_dd_parts_tm_1920x1080_focused);
                    setFocusableView(this.mMovie1920x1080);
                    break;
                } else {
                    this.mMovie1280x720.setImageResource(R.drawable.p_16_dd_parts_tm_1280x720_focused);
                    setFocusableView(this.mMovie1280x720);
                    break;
                }
            case 3:
                if (AngleShiftFaderController.FADE_ON.equalsIgnoreCase(this.mFaderValue)) {
                    this.mFaderOn.setImageResource(R.drawable.p_16_dd_parts_tm_fade_on_focused);
                    setFocusableView(this.mFaderOn);
                    break;
                } else {
                    this.mFaderOff.setImageResource(R.drawable.p_16_dd_parts_tm_fade_off_focused);
                    setFocusableView(this.mFaderOff);
                    break;
                }
        }
        updateBgResources();
        updateItemNameColor();
        updateInfo();
    }

    private void updateBgResources() {
        if ("framerate-24p".equalsIgnoreCase(this.mFrameRateValue)) {
            this.mMovie24pBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        } else {
            this.mMovie30pBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        }
        if (AngleShiftMovieController.MOVIE_1920x1080.equalsIgnoreCase(this.mMovieSizeValue)) {
            this.mMovie1920x1080Bg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        } else {
            this.mMovie1280x720Bg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        }
        if (AngleShiftFaderController.FADE_ON.equalsIgnoreCase(this.mFaderValue)) {
            this.mFaderOnBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        } else {
            this.mFaderOffBg.setBackgroundResource(R.drawable.p_16_dd_parts_tm_option_button_select);
        }
    }

    private void updateItemNameColor() {
        this.mFrameRate.setTextColor(Color.parseColor("#FFFFFF"));
        this.mMovieSize.setTextColor(Color.parseColor("#FFFFFF"));
        this.mFader.setTextColor(Color.parseColor("#FFFFFF"));
        switch (this.mCurrentRow) {
            case 1:
                this.mFrameRate.setTextColor(Color.parseColor("#FF8000"));
                return;
            case 2:
                this.mMovieSize.setTextColor(Color.parseColor("#FF8000"));
                return;
            case 3:
                this.mFader.setTextColor(Color.parseColor("#FF8000"));
                return;
            default:
                return;
        }
    }

    private void updateInfo() {
        this.mNumberOfImagesValue = AngleShiftSetting.getInstance().getTargetNumber();
        this.mNumberOfImages.setText("" + this.mNumberOfImagesValue);
        this.mPlayBackTimeValue = getPlayBackTime(this.mNumberOfImagesValue, this.mFrameRateValue);
        this.mPlayBackTime.setText(TLCommonUtil.getInstance().getTimeString(this.mPlayBackTimeValue, this));
        if ("framerate-24p".equalsIgnoreCase(this.mFrameRateValue)) {
            this.mRecMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        } else {
            this.mRecMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        }
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

    private void removeFocusFromAll() {
        for (int i = 0; i < mViewArray.length - 2; i++) {
            clearFocusedView(mViewArray[i]);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tl_reset_button /* 2131361837 */:
                this.mCurrentRow = 0;
                reset();
                break;
            case R.id.movie24p_btn /* 2131361839 */:
                this.mFrameRateValue = "framerate-24p";
                this.mCurrentRow = 1;
                break;
            case R.id.movie30p_btn /* 2131361841 */:
                this.mFrameRateValue = "framerate-30p";
                this.mCurrentRow = 1;
                break;
            case R.id.size1920x1080_btn /* 2131361844 */:
                this.mMovieSizeValue = AngleShiftMovieController.MOVIE_1920x1080;
                this.mCurrentRow = 2;
                break;
            case R.id.size1280x720_btn /* 2131361846 */:
                this.mMovieSizeValue = AngleShiftMovieController.MOVIE_1280x720;
                this.mCurrentRow = 2;
                break;
            case R.id.fader_on_btn /* 2131361849 */:
                this.mFaderValue = AngleShiftFaderController.FADE_ON;
                this.mCurrentRow = 3;
                break;
            case R.id.fader_off_btn /* 2131361851 */:
                this.mFaderValue = AngleShiftFaderController.FADE_OFF;
                this.mCurrentRow = 3;
                break;
        }
        makeViewsUnfocusable();
        update();
        pushedCenterKey();
    }

    private void reset() {
        this.mFrameRateValue = "framerate-24p";
        this.mMovieSizeValue = AngleShiftMovieController.MOVIE_1920x1080;
        this.mFaderValue = AngleShiftFaderController.FADE_ON;
        this.mNumberOfImagesValue = AngleShiftSetting.getInstance().getTargetNumber();
        this.mPlayBackTimeValue = getPlayBackTime(this.mNumberOfImagesValue, this.mFrameRateValue);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        cancelSettingValues();
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_THEME_LAYOUT);
        openPreviousMenu();
        deinitializeView();
        return 1;
    }

    private void cancelSettingValues() {
        this.mFrameRateValue = AngleShiftFrameRateController.getInstance().getValue();
        this.mMovieSizeValue = AngleShiftMovieController.getInstance().getValue();
        this.mFaderValue = AngleShiftFaderController.getInstance().getValue();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mCurrentRow == 0) {
            return -1;
        }
        saveSettings();
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_THEME_LAYOUT);
        openPreviousMenu();
        deinitializeView();
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
        if (this.mCurrentRow < this.numberOfItems) {
            this.mCurrentRow++;
        }
        update();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (this.mCurrentRow > 0) {
            this.mCurrentRow--;
        }
        update();
        return 1;
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToLeft(KeyEvent event) {
        handleLeftAction();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToRight(KeyEvent event) {
        handleRightAction();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        return "ID_ANGLESHIFTTHEMEOPTIONLAYOUT";
    }

    private void handleRightAction() {
        if (this.mMovie24p.hasFocus()) {
            this.mFrameRateValue = "framerate-30p";
        } else if (this.mMovie1920x1080.hasFocus()) {
            this.mMovieSizeValue = AngleShiftMovieController.MOVIE_1280x720;
        } else if (this.mFaderOn.hasFocus()) {
            this.mFaderValue = AngleShiftFaderController.FADE_OFF;
        }
        update();
    }

    private void handleLeftAction() {
        if (this.mMovie30p.hasFocus()) {
            this.mFrameRateValue = "framerate-24p";
        } else if (this.mMovie1280x720.hasFocus()) {
            this.mMovieSizeValue = AngleShiftMovieController.MOVIE_1920x1080;
        } else if (this.mFaderOff.hasFocus()) {
            this.mFaderValue = AngleShiftFaderController.FADE_ON;
        }
        update();
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

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        saveSettings();
        deinitializeView();
        this.mFooterGuide = null;
        this.mFrameRateValue = null;
        this.mMovieSizeValue = null;
        this.mFaderValue = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        clear();
        System.gc();
    }

    private void saveSettings() {
        String prevMovieSizeValue = AngleShiftMovieController.getInstance().getValue();
        AngleShiftFrameRateController.getInstance().setValue(this.mFrameRateValue);
        AngleShiftMovieController.getInstance().setValue(this.mMovieSizeValue);
        AngleShiftFaderController.getInstance().setValue(this.mFaderValue);
        if (!this.mMovieSizeValue.equalsIgnoreCase(prevMovieSizeValue)) {
            AngleShiftSetting.getInstance().resizeCustomRect();
        }
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
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
        this.mCurrentView = null;
        this.mService = null;
        if (mViewArray != null) {
            for (int i = 0; i < mViewArray.length; i++) {
                if (mViewArray[i] != null) {
                    mViewArray[i].setOnClickListener(null);
                    mViewArray[i] = null;
                }
            }
            mViewArray = null;
        }
        this.mFrameRate = null;
        this.mMovieSize = null;
        this.mFader = null;
        this.mNumberOfImages = null;
        this.mPlayBackTime = null;
        if (this.mMovie24pBg != null) {
            this.mMovie24pBg.setBackgroundResource(0);
        }
        if (this.mMovie30pBg != null) {
            this.mMovie30pBg.setBackgroundResource(0);
        }
        if (this.mMovie1920x1080Bg != null) {
            this.mMovie1920x1080Bg.setBackgroundResource(0);
        }
        if (this.mMovie1280x720Bg != null) {
            this.mMovie1280x720Bg.setBackgroundResource(0);
        }
        if (this.mFaderOnBg != null) {
            this.mFaderOnBg.setBackgroundResource(0);
        }
        if (this.mFaderOffBg != null) {
            this.mFaderOffBg.setBackgroundResource(0);
        }
        releaseImageViewDrawable(this.mResetImageBtn);
        this.mResetImageBtn = null;
        releaseImageViewDrawable(this.mMovie24p);
        this.mMovie24p = null;
        releaseImageViewDrawable(this.mMovie30p);
        this.mMovie30p = null;
        releaseImageViewDrawable(this.mMovie1920x1080);
        this.mMovie1920x1080 = null;
        releaseImageViewDrawable(this.mMovie1280x720);
        this.mMovie1280x720 = null;
        releaseImageViewDrawable(this.mFaderOn);
        this.mFaderOn = null;
        releaseImageViewDrawable(this.mFaderOff);
        this.mFaderOff = null;
        releaseImageViewDrawable(this.mMovie24pBg);
        this.mMovie24pBg = null;
        releaseImageViewDrawable(this.mMovie30pBg);
        this.mMovie30pBg = null;
        releaseImageViewDrawable(this.mMovie1920x1080Bg);
        this.mMovie1920x1080Bg = null;
        releaseImageViewDrawable(this.mMovie1280x720Bg);
        this.mMovie1280x720Bg = null;
        releaseImageViewDrawable(this.mFaderOnBg);
        this.mFaderOnBg = null;
        releaseImageViewDrawable(this.mFaderOffBg);
        this.mFaderOffBg = null;
        releaseImageViewDrawable(this.mRecMode);
        this.mRecMode = null;
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        switch (this.mCurrentRow) {
            case 0:
                setGuideResource(guideResources, R.string.STRID_AMC_STR_01117, R.string.STRID_FUNC_TIMELAPSE_SETTING_RESET_GUIDE);
                return;
            case 1:
                if ("framerate-24p".equalsIgnoreCase(this.mFrameRateValue)) {
                    setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_RECMODE_AVI24, R.string.STRID_FUNC_TIMELAPSE_EFFECT_FRAME_RATE_24P_GUIDE);
                    return;
                } else {
                    setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_RECMODE_AVI30, R.string.STRID_FUNC_TIMELAPSE_EFFECT_FRAME_RATE_30P_GUIDE);
                    return;
                }
            case 2:
                if (AngleShiftMovieController.MOVIE_1920x1080.equalsIgnoreCase(this.mMovieSizeValue)) {
                    setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_MOVSIZE_1920_1080, R.string.STRID_FUNC_TIMELAPSE_EFFECT_SIZE_1920_GUIDE);
                    return;
                } else {
                    setGuideResource(guideResources, R.string.STRID_FUNC_TIMELAPSE_MOVSIZE_1280_720, R.string.STRID_FUNC_TIMELAPSE_EFFECT_SIZE_1280_GUIDE);
                    return;
                }
            case 3:
                if (AngleShiftFaderController.FADE_ON.equalsIgnoreCase(this.mFaderValue)) {
                    setGuideResource(guideResources, R.string.STRID_AMC_STR_01062, R.string.STRID_FUNC_TIMELAPSE_EFFECT_FADER_ON_GUIDE);
                    return;
                } else {
                    setGuideResource(guideResources, R.string.STRID_AMC_STR_00058, R.string.STRID_FUNC_FADER_GUIDE_OFF2);
                    return;
                }
            default:
                return;
        }
    }

    private void setGuideResource(ArrayList<Object> guideResources, int guideTitleID, int guideDefi) {
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(true);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIRRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUmRemoteRecKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result;
        if (CustomizableFunction.Unchanged.equals(func)) {
            result = super.onConvertedKeyDown(event, func);
        } else {
            if (func.equals(CustomizableFunction.Guide)) {
                return super.onConvertedKeyDown(event, func);
            }
            switch (event.getScanCode()) {
                case 103:
                case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    result = super.onConvertedKeyDown(event, func);
                    break;
                default:
                    result = -1;
                    break;
            }
        }
        return result;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                return 1;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            default:
                return 0;
        }
    }
}
