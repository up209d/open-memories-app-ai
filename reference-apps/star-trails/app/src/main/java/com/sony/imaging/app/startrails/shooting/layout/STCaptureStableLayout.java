package com.sony.imaging.app.startrails.shooting.layout;

import android.graphics.Bitmap;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.base.menu.controller.STExposureCompensationController;
import com.sony.imaging.app.startrails.common.STCaptureDisplayModeObserver;
import com.sony.imaging.app.startrails.shooting.widget.CapturingShootTimeWidget;
import com.sony.imaging.app.startrails.shooting.widget.STProgressBar;
import com.sony.imaging.app.startrails.shooting.widget.ShootTimeWidget;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.startrails.util.ThemeParameterSettingUtility;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class STCaptureStableLayout extends StableLayout implements NotificationListener {
    protected static final SparseArray<Integer> STARTRAILS_LAYOUT_LIST_FOR_FINDER;
    protected static final SparseArray<Integer> STARTRAILS_LAYOUT_LIST_FOR_HDMI;
    private static final String TAG = "STCaptureStableLayout";
    private static String[] TAGS;
    ExposureModeIconView icon1;
    public static boolean is2SecondTimerFinish = false;
    public static boolean isCapturing = false;
    protected static final SparseArray<Integer> STARTRAILS_LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private int displayMode = 0;
    private int device = 0;
    private TextView mNumberofShot = null;
    private ShootTimeWidget mShootTimeWidget = null;
    private CapturingShootTimeWidget mShootTimeWidgetInfoOn = null;
    private STProgressBar mMovieProgressUpdate = null;
    private ImageView[] mImgVwMoveFrame = null;
    private ImageView mShootMode = null;
    ThemeParameterSettingUtility mTUtility = null;
    int mMaxShootNumber = 1;

    static {
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(11, Integer.valueOf(R.layout.star_trails_capturing_shooting_main_sid_info_on_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(12, Integer.valueOf(R.layout.star_trails_mute_shooting_main_sid_info_on_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.st_capturing_shooting_main_sid_info_on_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_panel));
        STARTRAILS_LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_panel));
        STARTRAILS_LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        STARTRAILS_LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.st_capturing_shooting_main_sid_basic_info_evf));
        STARTRAILS_LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_evf));
        STARTRAILS_LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_evf));
        STARTRAILS_LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_evf));
        STARTRAILS_LAYOUT_LIST_FOR_HDMI = STARTRAILS_LAYOUT_LIST_FOR_PANEL;
        TAGS = new String[]{DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, STConstants.TIMER_UPDATE, STConstants.THUMBNAIL_UPDATE, STConstants.CAPTURING_TWO_SEC_FINISH};
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, "onResume()");
        STUtility.getInstance().setIsEEStateBoot(false);
        updateExposureCompensationValue();
        super.onResume();
        STCaptureDisplayModeObserver.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        handleAppIconView();
        CameraNotificationManager.getInstance().requestNotify(STConstants.CAPTURING_STARTED);
        AppLog.exit(TAG, "onResume()");
    }

    private void handleAppIconView() {
        AppIconView icon2;
        if (this.mCurrentView != null && this.displayMode == 12 && (icon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon)) != null) {
            icon2.setVisibility(4);
        }
    }

    private void updateExposureCompensationValue() {
        if (Boolean.TRUE.booleanValue() == STUtility.getInstance().isEVDialRotated() && EVDialDetector.hasEVDial() && EVDialDetector.getEVDialPosition() != 100000) {
            STUtility.getInstance().setEVDialRotated(false);
            STExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, "onPause()");
        STCaptureDisplayModeObserver.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
        AppLog.exit(TAG, "onPause()");
    }

    private void releaseResources() {
        if (this.mImgVwMoveFrame != null) {
            for (int i = 0; i < 5; i++) {
                if (this.mImgVwMoveFrame[i] != null) {
                    this.mImgVwMoveFrame[i].setVisibility(8);
                }
            }
            if (this.mMovieProgressUpdate != null) {
                this.mMovieProgressUpdate.setProgress(0);
            }
            this.mMovieProgressUpdate = null;
            this.mImgVwMoveFrame = null;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        this.mTUtility = ThemeParameterSettingUtility.getInstance();
        this.device = DisplayModeObserver.getInstance().getActiveDevice();
        this.displayMode = STCaptureDisplayModeObserver.getInstance().getActiveDispMode(0);
        this.mMaxShootNumber = this.mTUtility.getNumberOfShot();
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
            this.displayMode = STCaptureDisplayModeObserver.getInstance().getActiveDispMode(0);
            if (this.mCurrentBaseView != null && this.displayMode != 12) {
                this.mCurrentBaseView.addView(baseFooterView);
                if (baseFooterView != null) {
                    this.mUserChangableViews[0] = (DigitView) baseFooterView.findViewById(R.id.aperture_view);
                    this.mUserChangableViews[1] = (DigitView) baseFooterView.findViewById(R.id.shutterspeed_view);
                    this.mUserChangableViews[2] = (DigitView) baseFooterView.findViewById(R.id.exposure_and_meteredmanual_view);
                    this.mUserChangableViews[3] = (DigitView) baseFooterView.findViewById(R.id.iso_sensitivity_view);
                }
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
                this.mTouchArea.setTouchAreaListener(getOnTouchListener());
            }
        }
        handleStarTrailMode();
    }

    private void handleStarTrailMode() {
        this.mNumberofShot = null;
        this.mShootTimeWidget = null;
        this.mShootTimeWidgetInfoOn = null;
        this.mCurrentMainView = obtainViewFromPool(this.mCurrentMainViewId);
        this.displayMode = STCaptureDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (this.mCurrentMainView != null && this.displayMode == 11) {
            TextView themeName = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_theme_name);
            themeName.setText(getResources().getString(STUtility.getInstance().getThemeNameResID(STUtility.getInstance().getCurrentTrail())));
            this.mShootMode = (ImageView) this.mCurrentMainView.findViewById(R.id.shooting_information_recording_icon);
            updateRecModeIcon();
            createMovieFrameObjects();
            TextView streakLevel = (TextView) this.mCurrentMainView.findViewById(R.id.streakLevel);
            int streakValue = this.mTUtility.getStreakLevel();
            CharSequence streakLevelValue = STUtility.getInstance().getStreakValue(streakValue);
            streakLevel.setText(streakLevelValue);
            this.mShootTimeWidget = (ShootTimeWidget) this.mCurrentMainView.findViewById(R.id.shooting_information_recording_time_capturing);
            this.mNumberofShot = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_shooting_num);
            this.mMovieProgressUpdate = (STProgressBar) this.mCurrentMainView.findViewById(R.id.movie_capturing_progressBar);
            if (this.mMovieProgressUpdate != null) {
                this.mMovieProgressUpdate.setMax(this.mMaxShootNumber);
                this.mMovieProgressUpdate.setProgress(STConstants.sCaptureImageCounter);
                if (STUtility.getInstance().isPreTakePictureTestShot() && !STUtility.getInstance().isCapturingStarted()) {
                    this.mMovieProgressUpdate.setVisibility(4);
                }
            }
            this.mNumberofShot.setText("" + this.mTUtility.getNumberOfShot());
            if (STUtility.getInstance().isCapturingStarted() && STConstants.sCaptureImageCounter > 0) {
                updateStarTrailValue();
                updateImageView();
            }
        }
        updateInfoStart();
    }

    private void updateInfoStart() {
        if (this.displayMode == 1 && this.mCurrentMainView != null) {
            updateInfoModeStarTrailsIcon();
            ImageView shootIcon = (ImageView) this.mCurrentMainView.findViewById(R.id.shooting_information_shooting_num_icon);
            if (STConstants.sCaptureImageCounter == 0 || STUtility.getInstance().isPreTakePictureTestShot()) {
                if (this.mNumberofShot != null) {
                    this.mNumberofShot.setVisibility(4);
                    this.mNumberofShot.setText("" + STConstants.sCaptureImageCounter + "/" + this.mTUtility.getNumberOfShot());
                }
                if (this.mShootTimeWidgetInfoOn != null) {
                    this.mShootTimeWidgetInfoOn.setVisibility(4);
                    this.mShootTimeWidgetInfoOn.setText(STUtility.getInstance().getTotleRecordingTime());
                }
                if (this.mShootTimeWidget != null) {
                    this.mShootTimeWidget.setVisibility(4);
                    this.mShootTimeWidget.setText(STUtility.getInstance().getTotleRecordingTime());
                }
                if (shootIcon != null) {
                    shootIcon.setVisibility(4);
                    return;
                }
                return;
            }
            if (this.mNumberofShot != null) {
                this.mNumberofShot.setVisibility(0);
                this.mNumberofShot.setText("" + STConstants.sCaptureImageCounter + "/" + this.mTUtility.getNumberOfShot());
            }
            if (this.mShootTimeWidgetInfoOn != null) {
                this.mShootTimeWidgetInfoOn.setVisibility(0);
                this.mShootTimeWidgetInfoOn.setText(STUtility.getInstance().getTotleRecordingTime());
            }
            if (this.mShootTimeWidget != null) {
                this.mShootTimeWidget.setVisibility(0);
                this.mShootTimeWidget.setText(STUtility.getInstance().getTotleRecordingTime());
            }
            if (shootIcon != null) {
                shootIcon.setVisibility(0);
            }
        }
    }

    private void updateInfoModeStarTrailsIcon() {
        if (STUtility.getInstance().isPreTakePictureTestShot() && this.mMovieProgressUpdate != null) {
            this.mMovieProgressUpdate.setVisibility(8);
        }
        this.mNumberofShot = (TextView) this.mCurrentMainView.findViewById(R.id.shooting_information_shooting_num_info_on);
        if (this.device == 1) {
            this.mShootTimeWidgetInfoOn = (CapturingShootTimeWidget) this.mCurrentMainView.findViewById(R.id.shooting_information_recording_time_info_on);
        } else {
            this.mShootTimeWidget = (ShootTimeWidget) this.mCurrentMainView.findViewById(R.id.shooting_information_normal_time_info_on);
        }
    }

    private void createMovieFrameObjects() {
        this.mImgVwMoveFrame = new ImageView[5];
        int[] resid = {R.id.movie_frame1, R.id.movie_frame2, R.id.movie_frame3, R.id.movie_frame4, R.id.movie_frame5};
        for (int i = 0; i < 5; i++) {
            this.mImgVwMoveFrame[i] = (ImageView) this.mCurrentMainView.findViewById(resid[i]);
            this.mImgVwMoveFrame[i].setVisibility(4);
        }
    }

    private void releaseImageViews() {
        if (this.mImgVwMoveFrame != null) {
            for (int i = 0; i < 5; i++) {
                if (this.mImgVwMoveFrame[i] != null) {
                    releaseImageViewDrawable(this.mImgVwMoveFrame[i]);
                    this.mImgVwMoveFrame[i].setVisibility(8);
                    this.mImgVwMoveFrame[i] = null;
                }
            }
            this.mImgVwMoveFrame = null;
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

    private void releaseFrameViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    private void updateImageView() {
        if (this.mImgVwMoveFrame != null) {
            AppLog.trace(TAG, "onNotify: updateImageView");
            for (int i = 0; i < STConstants.sFrameBitmapList.size(); i++) {
                if (this.displayMode == 11) {
                    releaseFrameViewDrawable(this.mImgVwMoveFrame[i]);
                    this.mImgVwMoveFrame[i].setVisibility(0);
                    Bitmap bmp = STConstants.sFrameBitmapList.get(i);
                    if (bmp != null && !bmp.isRecycled()) {
                        this.mImgVwMoveFrame[i].setImageBitmap(bmp);
                    }
                } else {
                    this.mImgVwMoveFrame[i].setVisibility(4);
                }
            }
        }
    }

    private void updateRecModeIcon() {
        if (this.mTUtility.getRecordingMode() == 0) {
            this.mShootMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie24p_menu_normal);
        } else {
            this.mShootMode.setImageResource(R.drawable.p_16_dd_parts_tm_recordingmode_movie30p_menu_normal);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        if (device == 0) {
            int layout = STARTRAILS_LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
            return layout;
        }
        if (device == 1) {
            int layout2 = STARTRAILS_LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
            return layout2;
        }
        if (device != 2) {
            return -1;
        }
        int layout3 = STARTRAILS_LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        return layout3;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        AppLog.info(TAG, "onLayoutModeChanged()");
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
            this.device = DisplayModeObserver.getInstance().getActiveDevice();
            this.displayMode = STCaptureDisplayModeObserver.getInstance().getActiveDispMode(0);
            onLayoutModeChanged(this.device, this.displayMode);
            if (this.mMovieProgressUpdate != null) {
                this.mMovieProgressUpdate.setVisibility(0);
                return;
            }
            return;
        }
        if (!STUtility.getInstance().isPreTakePictureTestShot()) {
            if (tag.equals(STConstants.TIMER_UPDATE) && STConstants.sCaptureImageCounter >= 1) {
                AppLog.info(TAG, "IMDLAPP7-1380 tag=  " + tag);
                updateInfoStart();
                updateStarTrailValue();
            } else if (tag.equals(STConstants.THUMBNAIL_UPDATE) && STConstants.sCaptureImageCounter >= 1) {
                updateImageView();
            } else if (tag.equals(STConstants.CAPTURING_TWO_SEC_FINISH)) {
                if (this.mMovieProgressUpdate != null) {
                    this.mMovieProgressUpdate.setVisibility(0);
                }
                updateInfoStart();
                updateStarTrailValue();
            }
        }
    }

    private void updateStarTrailValue() {
        if (!STUtility.getInstance().isPreTakePictureTestShot() && this.displayMode != 1) {
            AppLog.info(TAG, "IMDLAPP7-1380 tag= mNumberofShot " + this.mNumberofShot);
            if (this.mNumberofShot != null) {
                AppLog.info(TAG, "IMDLAPP7-1380 tag=  " + this.mNumberofShot);
                this.mNumberofShot.setVisibility(0);
                this.mNumberofShot.setText("" + STConstants.sCaptureImageCounter + "/" + this.mTUtility.getNumberOfShot());
            }
            AppLog.info(TAG, "IMDLAPP7-1380 tag= mShootTimeWidget " + this.mShootTimeWidget);
            if (this.mShootTimeWidget != null) {
                if (this.mShootTimeWidget.getVisibility() != 0) {
                    this.mShootTimeWidget.setVisibility(0);
                }
                this.mShootTimeWidget.setText(STUtility.getInstance().getTotleRecordingTime());
            }
            if (this.mMovieProgressUpdate != null && this.mMovieProgressUpdate.getProgress() != this.mMaxShootNumber) {
                this.mMovieProgressUpdate.setVisibility(0);
                AppLog.info(TAG, "MovieProgressUpdate in progress " + this.mMovieProgressUpdate);
                if (this.mMaxShootNumber == STConstants.sCaptureImageCounter) {
                    AppLog.info(TAG, "MovieProgressUpdate in progress " + this.mMovieProgressUpdate);
                    this.mMovieProgressUpdate.setProgress(this.mMaxShootNumber);
                } else {
                    this.mMovieProgressUpdate.setProgress(STConstants.sCaptureImageCounter % this.mMaxShootNumber);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        deinitializeView();
        super.closeLayout();
    }

    private void deinitializeView() {
        AppLog.enter(TAG, "deinitializeView()");
        releaseImageViews();
        releaseResources();
        STConstants.sCaptureImageCounter = 0;
        releaseTextView();
        STUtility.getInstance().setCaptureStatus(false);
        releaseImageViewDrawable(this.mShootMode);
        STUtility.getInstance().releaseBitmapList();
        AppLog.exit(TAG, "deinitializeView()");
    }

    private void releaseTextView() {
        if (this.mNumberofShot != null) {
            this.mNumberofShot.setText("");
            this.mNumberofShot = null;
        }
        if (this.mShootTimeWidget != null) {
            this.mShootTimeWidget.setText("");
            this.mShootTimeWidget = null;
        }
        if (this.mShootTimeWidgetInfoOn != null) {
            this.mShootTimeWidgetInfoOn.setText("");
            this.mShootTimeWidgetInfoOn = null;
        }
    }
}
