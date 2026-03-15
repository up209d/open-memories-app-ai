package com.sony.imaging.app.timelapse.shooting.layout;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.AELController;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.base.shooting.widget.DigitalLevelView;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.base.shooting.widget.MediaInformationView;
import com.sony.imaging.app.base.shooting.widget.StillImageAspectratioIcon;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.base.TimeLapseDisplayModeObserver;
import com.sony.imaging.app.timelapse.shooting.controller.FrameRateController;
import com.sony.imaging.app.timelapse.shooting.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.timelapse.shooting.widget.TLStillImageQualityIcon;
import com.sony.imaging.app.timelapse.shooting.widget.TLStillImageSizeIcon;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class TimeLapseStableLayout extends StableLayout implements NotificationListener {
    private static final String TAG = "TimeLapseStableLayout";
    private static String[] TAGS;
    protected static final SparseArray<Integer> TIMELAPSE_LAYOUT_LIST_FOR_FINDER;
    protected static final SparseArray<Integer> TIMELAPSE_LAYOUT_LIST_FOR_HDMI;
    ExposureModeIconView icon1;
    private RelativeLayout.LayoutParams mLayoutParamsBottomCenter;
    private RelativeLayout.LayoutParams mLayoutParamsBottomLeft;
    private RelativeLayout.LayoutParams mLayoutParamsBottomRight;
    private RelativeLayout.LayoutParams mLayoutParamsTopCenter;
    private RelativeLayout.LayoutParams mLayoutParamsTopLeft;
    private RelativeLayout.LayoutParams mLayoutParamsTopRight;
    public static boolean is2SecondTimerFinish = false;
    public static boolean isCapturing = false;
    protected static final SparseArray<Integer> TIMELAPSE_LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private TextView intervalTime = null;
    private TextView recTime = null;
    private TextView numberImage = null;
    private int displayMode = 0;
    private int device = 0;
    private TLCommonUtil tlTheme = null;
    private ImageView movieFrameTopLeft = null;
    private ImageView movieFrameTopCenter = null;
    private ImageView movieFrameTopRight = null;
    private ImageView movieFrameBottomLeft = null;
    private ImageView movieFrameBottomCenter = null;
    private ImageView movieFrameBottomRight = null;
    private int MOVIE_FRAME_HALF_WIDTH = 40;
    private int MOVIE_FRAME_WIDTH = 80;
    private int MOVIE_FRAME_HEIGHT = 4;

    static {
        TIMELAPSE_LAYOUT_LIST_FOR_PANEL.append(11, Integer.valueOf(R.layout.timelapse_shooting_main_sid_info_on_panel));
        TIMELAPSE_LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.tl_shooting_main_sid_info_on_panel));
        TIMELAPSE_LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.tl_shooting_main_sid_info_off_panel));
        TIMELAPSE_LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.tl_shooting_main_sid_histogram_panel));
        TIMELAPSE_LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.tl_shooting_main_sid_digitallevel_panel));
        TIMELAPSE_LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        TIMELAPSE_LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.tl_shooting_main_sid_basic_info_evf));
        TIMELAPSE_LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.tl_shooting_main_sid_info_off_evf));
        TIMELAPSE_LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.tl_shooting_main_sid_histogram_evf));
        TIMELAPSE_LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_evf));
        TIMELAPSE_LAYOUT_LIST_FOR_HDMI = TIMELAPSE_LAYOUT_LIST_FOR_PANEL;
        TAGS = new String[]{DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, TimeLapseConstants.CAPTURE_PROCESSING, TimeLapseConstants.CAPTURE_PROCESSING_FINISH, "Aperture", TimeLapseConstants.REMOVE_MOVIE_SPECIFIC_ICON, TimeLapseConstants.REMOVE_CAPTURE_CALLBACK_STABLE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE};
    }

    public TimeLapseStableLayout() {
        this.idleHandler = new UpdateMovieIcons();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        if (TLCommonUtil.getInstance().checkExposureMode()) {
            vg.setVisibility(4);
        } else {
            vg.setVisibility(0);
        }
        return vg;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.e(TAG, "onResume");
        TimeLapseDisplayModeObserver.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        DisplayModeObserver.getInstance().setNotificationListener(this);
        TLShootModeSettingController.getInstance().setMiniatureOptionValue(TLShootModeSettingController.getInstance().getCurrentCaptureState());
        setAntiBlurOff();
        setIrisRingSettingByCamera();
        if (!isCapturing && !TLCommonUtil.getInstance().isS1On()) {
            AELController.getInstance().cancelAELock();
        }
        updateExposureComp();
        super.onResume();
        displayGridLineForMovie(this.device);
    }

    private void setIrisRingSettingByCamera() {
        int type = ScalarProperties.getInt("device.iris.ring.type");
        if (type != 0) {
            TLCommonUtil.getInstance().setThemeApertureValue();
        }
    }

    private void setAntiBlurOff() {
        if (!"off".equals(AntiHandBlurController.getInstance().getValue(AntiHandBlurController.STILL))) {
            AntiHandBlurController.getInstance().setValue(AntiHandBlurController.STILL, "off");
        }
    }

    private void updateExposureComp() {
        if (TimeLapseConstants.isEVDial) {
            TimeLapseConstants.isEVDial = false;
            TimeLapseConstants.slastStateExposureCompansasion = EVDialDetector.getEVDialPosition();
            ExposureCompensationController.getInstance().setEvDialValue(TimeLapseConstants.slastStateExposureCompansasion);
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        Log.e(TAG, "onReopened");
        super.onReopened();
        if (isCapturing && this.numberImage != null) {
            this.numberImage.setText("");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.e(TAG, "onPause");
        TimeLapseDisplayModeObserver.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        this.tlTheme = null;
        this.intervalTime = null;
        this.recTime = null;
        this.numberImage = null;
        this.movieFrameTopLeft = null;
        this.movieFrameTopCenter = null;
        this.movieFrameTopRight = null;
        this.movieFrameBottomLeft = null;
        this.movieFrameBottomCenter = null;
        this.movieFrameBottomRight = null;
        this.mLayoutParamsTopLeft = null;
        this.mLayoutParamsTopCenter = null;
        this.mLayoutParamsTopRight = null;
        this.mLayoutParamsBottomLeft = null;
        this.mLayoutParamsBottomCenter = null;
        this.mLayoutParamsBottomRight = null;
        this.icon1 = null;
        super.onPause();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        this.device = DisplayModeObserver.getInstance().getActiveDevice();
        this.displayMode = TimeLapseDisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (this.device == 0) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(this.device, this.displayMode);
            touchAreaId = R.id.touchArea;
        } else if (this.device == 1) {
            baseViewId = R.layout.shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(this.device, this.displayMode);
        } else if (this.device == 2) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(this.device, this.displayMode);
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
        this.icon1 = (ExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        AppIconView icon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon);
        if (this.device == 1) {
            this.icon1.setAutoDisappear(false);
            if (1 == this.displayMode || 11 == this.displayMode) {
                icon2.setAutoDisappear(false);
            } else {
                icon2.setAutoDisappear(true);
            }
        } else if (1 == this.displayMode || 11 == this.displayMode) {
            this.icon1.setAutoDisappear(false);
            icon2.setAutoDisappear(false);
        } else {
            this.icon1.setAutoDisappear(true);
            icon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                TouchArea.OnTouchAreaListener touchListener = getOnTouchListener();
                this.mTouchArea.setTouchAreaListener(touchListener);
                if (touchListener instanceof TouchArea.OnTouchAreaDoubleTapListener) {
                    this.mTouchArea.setTouchAreaDoubleTapListener((TouchArea.OnTouchAreaDoubleTapListener) touchListener);
                }
            }
        }
        handleTimeLapseIcon();
        displayGridLineForMovie(this.device);
    }

    private void displayGridLineForMovie(int output) {
        this.movieFrameTopLeft = (ImageView) this.mCurrentMainView.findViewById(R.id.movie_frame_top_left);
        this.movieFrameTopCenter = (ImageView) this.mCurrentMainView.findViewById(R.id.movie_frame_top_center);
        this.movieFrameTopRight = (ImageView) this.mCurrentMainView.findViewById(R.id.movie_frame_top_right);
        this.movieFrameBottomLeft = (ImageView) this.mCurrentMainView.findViewById(R.id.movie_frame_bottom_left);
        this.movieFrameBottomCenter = (ImageView) this.mCurrentMainView.findViewById(R.id.movie_frame_bottom_center);
        this.movieFrameBottomRight = (ImageView) this.mCurrentMainView.findViewById(R.id.movie_frame_bottom_right);
        this.movieFrameTopLeft.setVisibility(4);
        this.movieFrameTopCenter.setVisibility(4);
        this.movieFrameTopRight.setVisibility(4);
        this.movieFrameBottomLeft.setVisibility(4);
        this.movieFrameBottomCenter.setVisibility(4);
        this.movieFrameBottomRight.setVisibility(4);
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 2) {
            DisplayManager.VideoRect rect = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
            int width = rect.pxRight - rect.pxLeft;
            int height = rect.pxBottom - rect.pxTop;
            int deviceOsdAspect = ScalarProperties.getInt("device.panel.aspect");
            int frameHeight = (width * 9) / 16;
            this.mLayoutParamsTopLeft = new RelativeLayout.LayoutParams(-2, -2);
            this.mLayoutParamsTopCenter = new RelativeLayout.LayoutParams(-2, -2);
            this.mLayoutParamsTopRight = new RelativeLayout.LayoutParams(-2, -2);
            this.mLayoutParamsBottomLeft = new RelativeLayout.LayoutParams(-2, -2);
            this.mLayoutParamsBottomCenter = new RelativeLayout.LayoutParams(-2, -2);
            this.mLayoutParamsBottomRight = new RelativeLayout.LayoutParams(-2, -2);
            RelativeLayout.LayoutParams layoutParams = this.mLayoutParamsTopLeft;
            RelativeLayout.LayoutParams layoutParams2 = this.mLayoutParamsBottomLeft;
            int i = this.MOVIE_FRAME_HALF_WIDTH;
            layoutParams2.width = i;
            layoutParams.width = i;
            RelativeLayout.LayoutParams layoutParams3 = this.mLayoutParamsTopRight;
            RelativeLayout.LayoutParams layoutParams4 = this.mLayoutParamsBottomRight;
            int i2 = this.MOVIE_FRAME_HALF_WIDTH;
            layoutParams4.width = i2;
            layoutParams3.width = i2;
            RelativeLayout.LayoutParams layoutParams5 = this.mLayoutParamsTopCenter;
            RelativeLayout.LayoutParams layoutParams6 = this.mLayoutParamsBottomCenter;
            int i3 = this.MOVIE_FRAME_WIDTH;
            layoutParams6.width = i3;
            layoutParams5.width = i3;
            RelativeLayout.LayoutParams layoutParams7 = this.mLayoutParamsTopLeft;
            RelativeLayout.LayoutParams layoutParams8 = this.mLayoutParamsBottomLeft;
            int i4 = this.MOVIE_FRAME_HEIGHT;
            layoutParams8.height = i4;
            layoutParams7.height = i4;
            RelativeLayout.LayoutParams layoutParams9 = this.mLayoutParamsTopCenter;
            RelativeLayout.LayoutParams layoutParams10 = this.mLayoutParamsBottomCenter;
            int i5 = this.MOVIE_FRAME_HEIGHT;
            layoutParams10.height = i5;
            layoutParams9.height = i5;
            RelativeLayout.LayoutParams layoutParams11 = this.mLayoutParamsTopRight;
            RelativeLayout.LayoutParams layoutParams12 = this.mLayoutParamsBottomRight;
            int i6 = this.MOVIE_FRAME_HEIGHT;
            layoutParams12.height = i6;
            layoutParams11.height = i6;
            RelativeLayout.LayoutParams layoutParams13 = this.mLayoutParamsTopLeft;
            RelativeLayout.LayoutParams layoutParams14 = this.mLayoutParamsBottomLeft;
            int i7 = rect.pxLeft;
            layoutParams14.leftMargin = i7;
            layoutParams13.leftMargin = i7;
            RelativeLayout.LayoutParams layoutParams15 = this.mLayoutParamsTopCenter;
            RelativeLayout.LayoutParams layoutParams16 = this.mLayoutParamsBottomCenter;
            int i8 = (rect.pxLeft + (width / 2)) - this.MOVIE_FRAME_HALF_WIDTH;
            layoutParams16.leftMargin = i8;
            layoutParams15.leftMargin = i8;
            RelativeLayout.LayoutParams layoutParams17 = this.mLayoutParamsTopRight;
            RelativeLayout.LayoutParams layoutParams18 = this.mLayoutParamsBottomRight;
            int i9 = rect.pxRight - this.MOVIE_FRAME_HALF_WIDTH;
            layoutParams18.leftMargin = i9;
            layoutParams17.leftMargin = i9;
            if (1 != output && 169 == deviceOsdAspect) {
                frameHeight = ((width * 4) * 9) / 48;
            }
            this.mLayoutParamsTopLeft.topMargin = (rect.pxTop + ((height - frameHeight) / 2)) - (this.MOVIE_FRAME_HEIGHT / 2);
            this.mLayoutParamsTopCenter.topMargin = this.mLayoutParamsTopLeft.topMargin;
            this.mLayoutParamsTopRight.topMargin = this.mLayoutParamsTopLeft.topMargin;
            this.mLayoutParamsBottomLeft.topMargin = this.mLayoutParamsTopLeft.topMargin + frameHeight;
            this.mLayoutParamsBottomCenter.topMargin = this.mLayoutParamsBottomLeft.topMargin;
            this.mLayoutParamsBottomRight.topMargin = this.mLayoutParamsBottomLeft.topMargin;
            this.movieFrameTopLeft.setLayoutParams(this.mLayoutParamsTopLeft);
            this.movieFrameTopCenter.setLayoutParams(this.mLayoutParamsTopCenter);
            this.movieFrameTopRight.setLayoutParams(this.mLayoutParamsTopRight);
            this.movieFrameBottomLeft.setLayoutParams(this.mLayoutParamsBottomLeft);
            this.movieFrameBottomCenter.setLayoutParams(this.mLayoutParamsBottomCenter);
            this.movieFrameBottomRight.setLayoutParams(this.mLayoutParamsBottomRight);
            this.movieFrameTopLeft.setVisibility(0);
            this.movieFrameTopCenter.setVisibility(0);
            this.movieFrameTopRight.setVisibility(0);
            this.movieFrameBottomLeft.setVisibility(0);
            this.movieFrameBottomCenter.setVisibility(0);
            this.movieFrameBottomRight.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        if (device == 0) {
            int layout = TIMELAPSE_LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
            return layout;
        }
        if (device == 1) {
            int layout2 = TIMELAPSE_LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
            return layout2;
        }
        if (device != 2) {
            return -1;
        }
        int layout3 = TIMELAPSE_LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        return layout3;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        this.device = device;
        this.displayMode = displayMode;
        updateView();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(DisplayModeObserver.TAG_DISPMODE_CHANGE) || tag.equals(DisplayModeObserver.TAG_DEVICE_CHANGE)) {
            TimeLapseDisplayModeObserver mDisplayModeObserver = TimeLapseDisplayModeObserver.getInstance();
            this.device = mDisplayModeObserver.getActiveDevice();
            this.displayMode = mDisplayModeObserver.getActiveDispMode(0);
            onLayoutModeChanged(this.device, this.displayMode);
            return;
        }
        if (tag.equals(TimeLapseConstants.CAPTURE_PROCESSING) && isCapturing) {
            if (this.numberImage != null) {
                this.numberImage.setText("");
            }
            setVisiblityOFTimeLapseMode(8);
            invisibleDigitalLevelView(8);
            if (!SelfTimerIntervalPriorityController.getInstance().isSelfTimer() && TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
                invisibleMovieSpecificIcon(8);
                return;
            }
            return;
        }
        if (tag.equals(TimeLapseConstants.CAPTURE_PROCESSING_FINISH)) {
            if (this.numberImage != null) {
                this.numberImage.setText("" + this.tlTheme.getShootingNum());
            }
            setVisiblityOFTimeLapseMode(0);
            invisibleMovieSpecificIcon(0);
            return;
        }
        if (tag.equals("Aperture")) {
            if (TLCommonUtil.getInstance().getCurrentState() == 7) {
                BackUpUtil.getInstance().setPreference(TimeLapseConstants.CUSTOM_APERTURE_KEY, TLCommonUtil.getInstance().getApertureValues(CameraSetting.getInstance().getApertureInfo()));
                return;
            }
            int type = ScalarProperties.getInt("device.iris.ring.type");
            if (type != 0 && TLCommonUtil.getInstance().getCurrentState() != 6) {
                Log.d(TAG, "getTargetApertureValue:" + TLCommonUtil.getInstance().getTargetApertureValue());
                Log.d(TAG, "getApertureInfo:" + TLCommonUtil.getInstance().getApertureValues(CameraSetting.getInstance().getApertureInfo()));
                if (!TLCommonUtil.getInstance().getTargetApertureValue().equalsIgnoreCase(TLCommonUtil.getInstance().getApertureValues(CameraSetting.getInstance().getApertureInfo()))) {
                    TLCommonUtil.getInstance().setThemeApertureValue();
                    return;
                }
                return;
            }
            return;
        }
        if (tag.equals(TimeLapseConstants.REMOVE_MOVIE_SPECIFIC_ICON) && isCapturing) {
            if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1) {
                invisibleMovieSpecificIcon(8);
            }
            invisibleDigitalLevelView(0);
        } else {
            if (tag.equals(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK_STABLE) && !isCapturing) {
                is2SecondTimerFinish = false;
                setDefaultText();
                invisibleMovieSpecificIcon(0);
                invisibleDigitalLevelView(0);
                setVisiblityOFTimeLapseMode(0);
                return;
            }
            if (tag.equals(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE)) {
                displayGridLineForMovie(this.device);
            }
        }
    }

    void handleTimeLapseIcon() {
        this.mCurrentMainView = obtainViewFromPool(this.mCurrentMainViewId);
        this.displayMode = TimeLapseDisplayModeObserver.getInstance().getActiveDispMode(0);
        this.tlTheme = TLCommonUtil.getThemeUtil();
        if (this.mCurrentMainView != null && this.displayMode == 11) {
            TextView timeLapseTheme = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_theme_name);
            timeLapseTheme.setText(this.tlTheme.getSelectedThemeName());
            ImageView shootMode = (ImageView) this.mCurrentMainView.findViewById(R.id.shooting_information_recording_icon);
            setShootModeIcon(shootMode);
            ImageView aeStatusIcon = (ImageView) this.mCurrentMainView.findViewById(R.id.ae_status);
            setAEStatusIcon(this.tlTheme.getAETrakingStatus(), aeStatusIcon);
            this.intervalTime = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_interval);
            this.intervalTime.setText(this.tlTheme.getTimeString(this.tlTheme.getInterval(), this));
            this.numberImage = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_shooting_num);
            this.recTime = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_recording_time);
            this.recTime.setText(this.tlTheme.getRecordingTime(this.tlTheme.getInterval() * (this.tlTheme.getShootingNum() - 1)));
            if (isCapturing) {
                if (this.numberImage != null) {
                    this.numberImage.setText("");
                }
                setVisiblityOFTimeLapseMode(8);
                if (!is2SecondTimerFinish && SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
                    invisibleDigitalLevelView(8);
                } else {
                    invisibleDigitalLevelView(0);
                }
            } else {
                this.numberImage.setText("" + this.tlTheme.getShootingNum());
                this.recTime.setText(this.tlTheme.getRecordingTime(this.tlTheme.getInterval() * (this.tlTheme.getShootingNum() - 1)));
                setVisiblityOFTimeLapseMode(0);
                invisibleDigitalLevelView(0);
            }
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1 && isCapturing && is2SecondTimerFinish) {
            invisibleMovieSpecificIcon(8);
        } else {
            invisibleMovieSpecificIcon(0);
        }
    }

    private void setAEStatusIcon(int aeStatus, ImageView aeStatusIcon) {
        switch (aeStatus) {
            case 1:
                aeStatusIcon.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_tracking_hi);
                return;
            case 2:
                aeStatusIcon.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_tracking_mid);
                return;
            case 3:
                aeStatusIcon.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_tracking_lo);
                return;
            case 4:
                aeStatusIcon.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_tracking);
                return;
            default:
                aeStatusIcon.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_ael);
                return;
        }
    }

    private void setShootModeIcon(ImageView shootMode) {
        switch (TLShootModeSettingController.getInstance().getCurrentCaptureState()) {
            case 1:
                shootMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_menu_normal);
                return;
            case 2:
                shootMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_still_movie_normal);
                return;
            default:
                if ("framerate-24p".equalsIgnoreCase(FrameRateController.getInstance().getValue())) {
                    shootMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
                    return;
                } else {
                    shootMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
                    return;
                }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVisiblityOFTimeLapseMode(int status) {
        if (this.tlTheme == null) {
            this.tlTheme = TLCommonUtil.getThemeUtil();
        }
        if (this.intervalTime != null) {
            this.intervalTime.setVisibility(status);
            this.recTime.setVisibility(status);
            this.numberImage.setVisibility(status);
        }
    }

    private void setDefaultText() {
        if (this.tlTheme == null) {
            this.tlTheme = TLCommonUtil.getThemeUtil();
        }
        if (this.intervalTime != null) {
            this.intervalTime.setText(this.tlTheme.getTimeString(this.tlTheme.getInterval(), this));
            this.recTime.setText("" + this.tlTheme.getRecordingTime(this.tlTheme.getInterval() * (this.tlTheme.getShootingNum() - 1)));
            this.numberImage.setText("" + this.tlTheme.getShootingNum());
        }
    }

    /* loaded from: classes.dex */
    private class UpdateMovieIcons extends StableLayout.DelayAttachView {
        private UpdateMovieIcons() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.DelayAttachView, android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            super.queueIdle();
            if (TLShootModeSettingController.getInstance().getCurrentCaptureState() == 1 || !TimeLapseStableLayout.isCapturing) {
                TimeLapseStableLayout.this.invisibleMovieSpecificIcon(0);
            } else {
                TimeLapseStableLayout.this.invisibleMovieSpecificIcon(8);
            }
            if (TimeLapseStableLayout.isCapturing) {
                if (TimeLapseStableLayout.this.numberImage != null) {
                    TimeLapseStableLayout.this.numberImage.setText("");
                }
                TimeLapseStableLayout.this.setVisiblityOFTimeLapseMode(8);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invisibleMovieSpecificIcon(int status) {
        Log.d(TAG, "TimeLapseStableLayout: invisibleMovieSpecificIcon()" + status);
        StillImageAspectratioIcon iAspect = (StillImageAspectratioIcon) this.mCurrentMainView.findViewById(R.id.i1_3);
        TLStillImageSizeIcon iSize = (TLStillImageSizeIcon) this.mCurrentMainView.findViewById(R.id.i1_4);
        TLStillImageQualityIcon iQuality = (TLStillImageQualityIcon) this.mCurrentMainView.findViewById(R.id.i1_5);
        MediaInformationView imageInfo = (MediaInformationView) this.mCurrentMainView.findViewById(R.id.media_information_view);
        if (iAspect != null) {
            iAspect.setVisibility(status);
        }
        if (iSize != null) {
            iSize.setVisibility(status);
        }
        if (iQuality != null) {
            iQuality.setVisibility(status);
        }
        if (imageInfo != null) {
            handleImageInfoview(imageInfo, status);
        }
    }

    private void invisibleDigitalLevelView(int status) {
        DigitalLevelView digitalLevelView;
        if (this.displayMode == 4 && (digitalLevelView = (DigitalLevelView) this.mCurrentMainView.findViewById(R.id.digitalLevelIcon)) != null) {
            digitalLevelView.setVisibility(status);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.base.shooting.layout.IStableLayout
    public void setUserChanging(int whatUserChanging) {
        super.setUserChanging(whatUserChanging);
        if (2 == whatUserChanging) {
            TimeLapseConstants.slastStateExposureCompansasion = ExposureCompensationController.getInstance().getExposureCompensationIndex();
        }
    }

    private void handleImageInfoview(MediaInformationView imageInfo, int status) {
        if (this.device != 1) {
            if (this.displayMode == 11 || this.displayMode == 1) {
                imageInfo.setVisibility(status);
                return;
            } else {
                imageInfo.setVisibility(4);
                return;
            }
        }
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1 && isCapturing && is2SecondTimerFinish) {
            imageInfo.setVisibility(status);
        } else {
            imageInfo.setVisibility(0);
        }
    }
}
