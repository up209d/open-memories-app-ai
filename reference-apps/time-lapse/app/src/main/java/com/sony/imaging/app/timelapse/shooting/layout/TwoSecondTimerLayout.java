package com.sony.imaging.app.timelapse.shooting.layout;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.shooting.TimelapseExecutorCreator;
import com.sony.imaging.app.timelapse.shooting.base.TimeLapseDisplayModeObserver;
import com.sony.imaging.app.timelapse.shooting.controller.FrameRateController;
import com.sony.imaging.app.timelapse.shooting.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.timelapse.shooting.controller.TLShootModeSettingController;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseMonitorBrightnessController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class TwoSecondTimerLayout extends Layout implements NotificationListener, IModableLayout {
    private static final String TAG = "TwoSecondTimerLayout";
    public static boolean isShootingCautionIconDisp = false;
    private OnLayoutModeChangeListener mListener;
    private TLCommonUtil mUtil;
    private View mCurrentView = null;
    private Handler mHandler = null;
    private long mTimeOneSecond = 1000;
    private MediaNotificationManager mMediaNotifier = null;
    private CameraNotificationManager mCamNotifier = null;
    private TextView mTxtVwShootingNumber = null;
    private TextView mTxtVwRecordingTime = null;
    private TextView mTxtVwThemeName = null;
    private TextView mTxtVwIntervalTime = null;
    private ImageView mImgVwSelfTimer = null;
    private ImageView mImgVwIntervalIcon = null;
    private ImageView mImgVwIntervalCautionIcon = null;
    private ImageView mImgVwShootModeIcon = null;
    private ImageView mAEStatusIcon = null;
    private ImageView mImgVwShootNumberIcon = null;
    private ImageView mImgBackGroundBar = null;
    private ImageView[] mImgVwMoveFrame = null;
    private ImageView mImgVwBar = null;
    private int mRecordingTime = 0;
    private int mIntervalTime = 0;
    private ProgressUpdateTask mProgressUpdateTask = null;
    private ProgressBar mMovieProgressUpdate = null;
    private int mDisplayMode = 11;
    private Layout mLayout = this;
    private final String[] TAGS = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, TimeLapseConstants.TIMER_UPDATE, TimeLapseConstants.THUMBNAIL_UPDATE, TimeLapseConstants.REMOVE_CAPTURE_CALLBACK_TWO_SECOND};

    static /* synthetic */ int access$1210(TwoSecondTimerLayout x0) {
        int i = x0.mRecordingTime;
        x0.mRecordingTime = i - 1;
        return i;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.timelapse_timer_display_layout);
        this.mImgVwBar = (ImageView) this.mCurrentView.findViewById(R.id.bar);
        if (this.mCurrentView != null) {
            this.mImgVwSelfTimer = (ImageView) this.mCurrentView.findViewById(R.id.timelapse_timer);
            if (this.mImgVwSelfTimer != null) {
                this.mImgVwSelfTimer.setImageResource(R.drawable.p_16_dd_parts_tm_timer_2sec_during_countdown);
                this.mImgVwSelfTimer.setVisibility(4);
            }
        }
        this.mUtil = TLCommonUtil.getThemeUtil();
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        return this.mCurrentView;
    }

    private void startProgress() {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        Runnable runnable = new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.layout.TwoSecondTimerLayout.1
            @Override // java.lang.Runnable
            public void run() {
                TwoSecondTimerLayout.this.mImgVwSelfTimer.setVisibility(0);
                try {
                    Thread.sleep(TwoSecondTimerLayout.this.mTimeOneSecond);
                } catch (InterruptedException e) {
                    Log.d(TwoSecondTimerLayout.TAG, e.toString());
                }
                if (TwoSecondTimerLayout.this.mHandler != null) {
                    TwoSecondTimerLayout.this.mHandler.post(new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.layout.TwoSecondTimerLayout.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (TwoSecondTimerLayout.this.mImgVwSelfTimer != null) {
                                if (!TimeLapseStableLayout.isCapturing) {
                                    TwoSecondTimerLayout.this.mImgVwSelfTimer.setVisibility(8);
                                } else {
                                    TwoSecondTimerLayout.this.mImgVwSelfTimer.setImageResource(R.drawable.p_16_dd_parts_tm_timer_1sec_during_countdown);
                                }
                            }
                        }
                    });
                }
                try {
                    Thread.sleep(TwoSecondTimerLayout.this.mTimeOneSecond);
                } catch (InterruptedException e2) {
                    Log.d(TwoSecondTimerLayout.TAG, e2.toString());
                }
                if (TwoSecondTimerLayout.this.mHandler != null) {
                    TwoSecondTimerLayout.this.mHandler.post(new Runnable() { // from class: com.sony.imaging.app.timelapse.shooting.layout.TwoSecondTimerLayout.1.2
                        @Override // java.lang.Runnable
                        public void run() {
                            if (TwoSecondTimerLayout.this.mImgVwSelfTimer != null) {
                                TwoSecondTimerLayout.this.mImgVwSelfTimer.setVisibility(8);
                                if (TimeLapseStableLayout.isCapturing) {
                                    TimeLapseStableLayout.is2SecondTimerFinish = true;
                                    TwoSecondTimerLayout.this.mCamNotifier.requestNotify(TimeLapseConstants.REMOVE_MOVIE_SPECIFIC_ICON);
                                    TwoSecondTimerLayout.this.mTxtVwShootingNumber.setText("" + TimeLapseConstants.sCaptureImageCounter + "/" + TwoSecondTimerLayout.this.mUtil.getShootingNum());
                                    if (TwoSecondTimerLayout.this.mDisplayMode == 11 && !TLCommonUtil.getInstance().isTestShot()) {
                                        TwoSecondTimerLayout.this.mMovieProgressUpdate.setVisibility(0);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mRecordingTime = this.mUtil.getInterval() * (this.mUtil.getShootingNum() - TimeLapseConstants.sCaptureImageCounter);
        TimelapseMonitorBrightnessController.getInstance().applySetting();
        if (this.mUtil == null) {
            this.mUtil = TLCommonUtil.getThemeUtil();
        }
        this.mMediaNotifier.setNotificationListener(this);
        if (this.mCamNotifier == null) {
            this.mCamNotifier = CameraNotificationManager.getInstance();
        }
        this.mCamNotifier.setNotificationListener(this);
        if (this.mListener == null) {
            this.mListener = new OnLayoutModeChangeListener(this, 0);
        }
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
        handleTimeLapseIcon();
        handleSelfTimerOnOFF();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }

    private void handleSelfTimerOnOFF() {
        this.mMovieProgressUpdate.setMax(this.mUtil.getShootingNum());
        if (SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
            TimeLapseStableLayout.is2SecondTimerFinish = false;
            this.mImgVwSelfTimer.setVisibility(0);
            if (this.mDisplayMode == 11) {
                this.mMovieProgressUpdate.setVisibility(8);
                this.mMovieProgressUpdate.setProgress(0);
            }
            startProgress();
            return;
        }
        TimeLapseStableLayout.is2SecondTimerFinish = true;
        if (this.mDisplayMode == 11) {
            this.mMovieProgressUpdate.setVisibility(0);
        }
        this.mCamNotifier.requestNotify(TimeLapseConstants.REMOVE_MOVIE_SPECIFIC_ICON);
        this.mImgVwSelfTimer.setVisibility(8);
        this.mTxtVwShootingNumber.setText("" + TimeLapseConstants.sCaptureImageCounter + "/" + this.mUtil.getShootingNum());
        if (TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1 && TimeLapseStableLayout.isCapturing && this.mUtil.getShootingNum() == 1) {
            this.mMovieProgressUpdate.setProgress(this.mUtil.getShootingNum());
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        this.mCurrentView = null;
        this.mTxtVwThemeName = null;
        this.mTxtVwIntervalTime = null;
        this.mImgVwShootModeIcon = null;
        this.mAEStatusIcon = null;
        this.mImgVwShootNumberIcon = null;
        this.mImgBackGroundBar = null;
        this.mImgVwBar = null;
        if (this.mProgressUpdateTask != null && this.mProgressUpdateTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mProgressUpdateTask.cancel(true);
        }
        this.mProgressUpdateTask = null;
        if (this.mImgVwMoveFrame != null) {
            for (int i = 0; i < 5; i++) {
                if (this.mImgVwMoveFrame[i] != null) {
                    this.mImgVwMoveFrame[i].setVisibility(8);
                }
            }
            this.mImgVwMoveFrame = null;
        }
        this.mMovieProgressUpdate = null;
        if (this.mListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
        }
        if (this.mMediaNotifier != null) {
            this.mMediaNotifier.removeNotificationListener(this);
            this.mMediaNotifier = null;
        }
        if (this.mCamNotifier != null) {
            this.mCamNotifier.removeNotificationListener(this);
            this.mCamNotifier = null;
        }
    }

    private void handleTimeLapseIcon() {
        this.mDisplayMode = TimeLapseDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (this.mCurrentView != null) {
            if (this.mImgVwBar != null) {
                this.mImgVwBar.setVisibility(0);
            }
            this.mImgVwIntervalIcon = (ImageView) this.mCurrentView.findViewById(R.id.shooting_information_interval_icon);
            this.mImgVwIntervalCautionIcon = (ImageView) this.mCurrentView.findViewById(R.id.shooting_information_interval_caution_icon);
            this.mImgVwIntervalCautionIcon.setVisibility(4);
            this.mTxtVwThemeName = (TextView) this.mCurrentView.findViewById(R.id.shooting_information_theme_name);
            this.mTxtVwThemeName.setText(this.mUtil.getSelectedThemeName());
            this.mImgVwShootModeIcon = (ImageView) this.mCurrentView.findViewById(R.id.shooting_information_recording_icon);
            setShootModeIcon(this.mImgVwShootModeIcon);
            this.mAEStatusIcon = (ImageView) this.mCurrentView.findViewById(R.id.ae_status);
            setAEStatusIcon(this.mUtil.getAETrakingStatus(), this.mAEStatusIcon);
            this.mImgBackGroundBar = (ImageView) this.mCurrentView.findViewById(R.id.background_progress);
            this.mTxtVwIntervalTime = (TextView) this.mCurrentView.findViewById(R.id.shooting_information_interval);
            if (this.mTxtVwIntervalTime != null) {
                this.mTxtVwIntervalTime.setText(this.mUtil.getTimeString(this.mUtil.getInterval(), this.mLayout));
            }
            this.mImgVwShootNumberIcon = (ImageView) this.mCurrentView.findViewById(R.id.shooting_information_shooting_num_icon);
            this.mTxtVwShootingNumber = (TextView) this.mCurrentView.findViewById(R.id.shooting_information_shooting_num);
            this.mTxtVwRecordingTime = (TextView) this.mCurrentView.findViewById(R.id.shooting_information_recording_time);
            this.mImgVwMoveFrame = new ImageView[5];
            this.mMovieProgressUpdate = (ProgressBar) this.mCurrentView.findViewById(R.id.progressBar1);
            this.mMovieProgressUpdate.setProgress(TimeLapseConstants.sCaptureImageCounter % this.mUtil.getShootingNum());
            if (TimeLapseStableLayout.is2SecondTimerFinish) {
                this.mMovieProgressUpdate.setVisibility(0);
            } else {
                this.mMovieProgressUpdate.setVisibility(8);
            }
            createMovieFrameObjects();
            displayIntervalCautionIcon();
            if (this.mDisplayMode == 11) {
                this.mImgVwIntervalIcon.setVisibility(0);
                this.mTxtVwThemeName.setVisibility(0);
                this.mImgVwBar.setVisibility(0);
                this.mImgVwShootModeIcon.setVisibility(0);
                this.mAEStatusIcon.setVisibility(0);
                this.mTxtVwIntervalTime.setVisibility(0);
                this.mImgVwShootNumberIcon.setVisibility(0);
                this.mTxtVwShootingNumber.setVisibility(0);
                this.mTxtVwRecordingTime.setVisibility(0);
                if (!TLCommonUtil.getInstance().isTestShot()) {
                    this.mImgBackGroundBar.setVisibility(0);
                } else {
                    this.mImgBackGroundBar.setVisibility(4);
                }
                isCapturingGoingOn();
                return;
            }
            this.mImgVwIntervalIcon.setVisibility(4);
            this.mTxtVwThemeName.setVisibility(4);
            this.mImgVwBar.setVisibility(4);
            this.mImgVwShootModeIcon.setVisibility(4);
            this.mAEStatusIcon.setVisibility(4);
            this.mTxtVwIntervalTime.setVisibility(4);
            this.mImgVwShootNumberIcon.setVisibility(4);
            this.mTxtVwShootingNumber.setVisibility(4);
            this.mTxtVwRecordingTime.setVisibility(4);
            this.mMovieProgressUpdate.setVisibility(8);
            this.mImgBackGroundBar.setVisibility(4);
        }
    }

    private void isCapturingGoingOn() {
        if (TimeLapseConstants.sCaptureImageCounter == 0) {
            if (SelfTimerIntervalPriorityController.getInstance().isSelfTimer()) {
                this.mTxtVwShootingNumber.setText("" + this.mUtil.getShootingNum());
            } else {
                this.mTxtVwShootingNumber.setText("" + TimeLapseConstants.sCaptureImageCounter + "/" + this.mUtil.getShootingNum());
            }
            this.mTxtVwIntervalTime.setText(this.mUtil.getTimeString(this.mUtil.getInterval(), this.mLayout));
            this.mRecordingTime = this.mUtil.getInterval() * (this.mUtil.getShootingNum() - 1);
            this.mTxtVwRecordingTime.setText(this.mUtil.getRecordingTime(this.mRecordingTime));
            return;
        }
        this.mTxtVwShootingNumber.setText("" + TimeLapseConstants.sCaptureImageCounter + "/" + this.mUtil.getShootingNum());
        if (this.mIntervalTime > 0 && this.mIntervalTime <= this.mUtil.getInterval()) {
            this.mTxtVwIntervalTime.setText(this.mUtil.getTimeString(this.mIntervalTime, this.mLayout));
        }
        this.mTxtVwRecordingTime.setText(this.mUtil.getRecordingTime(this.mRecordingTime));
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
    public void displayIntervalCautionIcon() {
        int staus = 4;
        if (this.mDisplayMode == 11 && isShootingCautionIconDisp) {
            staus = 0;
        }
        if (this.mImgVwIntervalCautionIcon != null) {
            this.mImgVwIntervalCautionIcon.setVisibility(staus);
        }
    }

    private void createMovieFrameObjects() {
        int[] resid = {R.id.movie_frame1, R.id.movie_frame2, R.id.movie_frame3, R.id.movie_frame4, R.id.movie_frame5};
        for (int i = 0; i < 5; i++) {
            this.mImgVwMoveFrame[i] = (ImageView) this.mCurrentView.findViewById(resid[i]);
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

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        Log.d(TAG, "onNotify: " + tag);
        if (tag.equals(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK_TWO_SECOND) && !TimeLapseStableLayout.isCapturing) {
            removeCaptureCallback();
            return;
        }
        if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
            if (this.mMediaNotifier.isNoCard() && !TLCommonUtil.getInstance().isTestShot()) {
                TimeLapseStableLayout.isCapturing = false;
                CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK);
                CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK_TWO_SECOND);
                CameraNotificationManager.getInstance().requestNotify(TimeLapseConstants.REMOVE_CAPTURE_CALLBACK_STABLE);
                return;
            }
            return;
        }
        if (tag.equals(TimeLapseConstants.TIMER_UPDATE)) {
            pictureTakenUpdate();
        } else if (tag.equals(TimeLapseConstants.THUMBNAIL_UPDATE) && TLShootModeSettingController.getInstance().getCurrentCaptureState() != 1 && TimeLapseStableLayout.isCapturing) {
            updateImageView();
        }
    }

    private void pictureTakenUpdate() {
        if (this.mUtil.isCaptured() && TimeLapseStableLayout.isCapturing) {
            this.mUtil.setCaptureStatus(false);
            if (this.mProgressUpdateTask != null && this.mProgressUpdateTask.getStatus() != AsyncTask.Status.FINISHED) {
                this.mProgressUpdateTask.cancel(true);
            }
            this.mProgressUpdateTask = null;
            if (this.mUtil.getShootingNum() == TimeLapseConstants.sCaptureImageCounter) {
                if (this.mMovieProgressUpdate != null) {
                    this.mMovieProgressUpdate.setProgress(this.mUtil.getShootingNum());
                }
                if (this.mTxtVwShootingNumber != null) {
                    this.mTxtVwShootingNumber.setText("" + TimeLapseConstants.sCaptureImageCounter + "/" + this.mUtil.getShootingNum());
                }
                if (this.mTxtVwRecordingTime != null) {
                    this.mTxtVwRecordingTime.setText(this.mUtil.getRecordingTime(0));
                    return;
                }
                return;
            }
            if (!TLCommonUtil.getInstance().isTestShot()) {
                this.mProgressUpdateTask = new ProgressUpdateTask();
                this.mProgressUpdateTask.execute(true);
            }
        }
    }

    private void removeCaptureCallback() {
        if (this.mProgressUpdateTask != null && this.mProgressUpdateTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mProgressUpdateTask.cancel(true);
        }
        this.mProgressUpdateTask = null;
        if (this.mUtil.getShootingNum() == TimeLapseConstants.sCaptureImageCounter) {
            if (this.mTxtVwIntervalTime != null) {
                this.mTxtVwIntervalTime.setText(this.mUtil.getTimeString(this.mUtil.getInterval(), this.mLayout));
            }
            if (this.mTxtVwShootingNumber != null) {
                this.mTxtVwShootingNumber.setText("" + TimeLapseConstants.sCaptureImageCounter + "/" + this.mUtil.getShootingNum());
            }
            if (this.mTxtVwRecordingTime != null) {
                this.mTxtVwRecordingTime.setText(this.mUtil.getRecordingTime(0));
            }
            if (this.mMovieProgressUpdate != null) {
                this.mMovieProgressUpdate.setProgress(this.mUtil.getShootingNum());
            }
        } else {
            if (this.mTxtVwIntervalTime != null) {
                this.mTxtVwIntervalTime.setText(this.mUtil.getTimeString(this.mUtil.getInterval(), this.mLayout));
            }
            Log.i(TAG, "mTxtVwShootingNumber" + this.mUtil.getShootingNum());
            if (this.mTxtVwShootingNumber != null) {
                this.mTxtVwShootingNumber.setText("" + this.mUtil.getShootingNum());
            }
            if (this.mTxtVwRecordingTime != null) {
                this.mTxtVwRecordingTime.setText(this.mUtil.getRecordingTime(this.mUtil.getInterval() * (this.mUtil.getShootingNum() - 1)));
            }
        }
        closeLayout();
    }

    private void updateImageView() {
        Log.d(TAG, "onNotify: updateImageView");
        for (int i = 0; i < TimeLapseConstants.sFrameBitmapList.size(); i++) {
            if (this.mDisplayMode == 11) {
                this.mImgVwMoveFrame[i].setVisibility(0);
                Bitmap bmp = TimeLapseConstants.sFrameBitmapList.get(i);
                if (bmp != null && !bmp.isRecycled()) {
                    this.mImgVwMoveFrame[i].setImageBitmap(bmp);
                }
            } else {
                this.mImgVwMoveFrame[i].setVisibility(4);
            }
        }
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        isShootingCautionIconDisp = false;
        if (this.mListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
            this.mListener = null;
        }
        releaseImageViews();
        TimeLapseConstants.sCaptureImageCounter = 0;
        this.mUtil.setCaptureStatus(false);
        if (this.mProgressUpdateTask != null && this.mProgressUpdateTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mProgressUpdateTask.cancel(true);
        }
        this.mProgressUpdateTask = null;
        this.mHandler = null;
        this.mTxtVwShootingNumber = null;
        this.mTxtVwRecordingTime = null;
        releaseImageViewDrawable(this.mImgVwIntervalIcon);
        this.mImgVwIntervalIcon = null;
        releaseImageViewDrawable(this.mImgVwSelfTimer);
        this.mImgVwSelfTimer = null;
        this.mImgVwIntervalCautionIcon = null;
        this.mImgBackGroundBar = null;
        this.mListener = null;
        if (this.mCamNotifier != null) {
            this.mCamNotifier.removeNotificationListener(this);
            this.mCamNotifier = null;
        }
        super.closeLayout();
    }

    private void releaseBitmapList() {
        Log.d(TAG, "releaseBitmapList() sFrameBitmapList " + TimeLapseConstants.sFrameBitmapList);
        if (TimeLapseConstants.sFrameBitmapList != null) {
            int size = TimeLapseConstants.sFrameBitmapList.size();
            for (int i = 0; i < size; i++) {
                Bitmap bmp = TimeLapseConstants.sFrameBitmapList.get(i);
                if (bmp != null && !bmp.isRecycled()) {
                    bmp.recycle();
                }
            }
            TimeLapseConstants.sFrameBitmapList.clear();
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

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        this.mDisplayMode = displayMode;
        visibleTimeLapseMode();
        if (this.mProgressUpdateTask == null || (this.mProgressUpdateTask != null && this.mProgressUpdateTask.getStatus() == AsyncTask.Status.FINISHED)) {
            this.mUtil.setCaptureStatus(true);
            Log.d(TAG, "1Sec issue  mProgressUpdateTask onLayoutModeChanged block");
            pictureTakenUpdate();
            this.mUtil.setCaptureStatus(false);
        }
    }

    private void visibleTimeLapseMode() {
        handleTimeLapseIcon();
        updateImageView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ProgressUpdateTask extends AsyncTask<Boolean, Integer, Boolean> {
        private ProgressUpdateTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            Log.d("ProgressUpdateTask", "onPreExecute()");
            Log.d("ProgressUpdateTask", "onPreExecute():" + TimeLapseConstants.sCaptureImageCounter);
            if (TwoSecondTimerLayout.this.mTxtVwShootingNumber != null) {
                TwoSecondTimerLayout.this.mTxtVwShootingNumber.setText("" + TimeLapseConstants.sCaptureImageCounter + "/" + TwoSecondTimerLayout.this.mUtil.getShootingNum());
            }
            if (TwoSecondTimerLayout.this.mMovieProgressUpdate != null) {
                TwoSecondTimerLayout.this.mMovieProgressUpdate.setProgress(TimeLapseConstants.sCaptureImageCounter % TwoSecondTimerLayout.this.mUtil.getShootingNum());
            }
            TwoSecondTimerLayout.this.mIntervalTime = TwoSecondTimerLayout.this.mUtil.getInterval();
            if (TwoSecondTimerLayout.this.mTxtVwIntervalTime != null) {
                TwoSecondTimerLayout.this.mTxtVwIntervalTime.setText(TwoSecondTimerLayout.this.mUtil.getTimeString(TwoSecondTimerLayout.this.mIntervalTime, TwoSecondTimerLayout.this.mLayout));
            }
            TwoSecondTimerLayout.this.mRecordingTime = TwoSecondTimerLayout.this.mUtil.getInterval() * (TwoSecondTimerLayout.this.mUtil.getShootingNum() - TimeLapseConstants.sCaptureImageCounter);
            if (TwoSecondTimerLayout.this.mTxtVwRecordingTime != null) {
                TwoSecondTimerLayout.this.mTxtVwRecordingTime.setText(TwoSecondTimerLayout.this.mUtil.getRecordingTime(TwoSecondTimerLayout.this.mRecordingTime));
            }
            TwoSecondTimerLayout.this.displayIntervalCautionIcon();
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Boolean... isComplete) {
            Log.d("ProgressUpdateTask", "doInBackground()");
            long startTime = System.currentTimeMillis();
            int counter = 1;
            while (!isCancelled()) {
                try {
                    try {
                        Thread.sleep(100L);
                        long now = System.currentTimeMillis();
                        if (now - startTime > counter * 1000) {
                            if (TwoSecondTimerLayout.this.mUtil.getInterval() == 1) {
                                break;
                            }
                            publishProgress(Integer.valueOf(TwoSecondTimerLayout.this.mUtil.getInterval() - counter));
                            counter++;
                            if (counter > TwoSecondTimerLayout.this.mUtil.getInterval() - 1) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } catch (Exception e) {
                        Log.d("ProgressUpdateTask", "doInBackground()" + e.toString());
                    }
                } catch (Exception e2) {
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(Integer... values) {
            if (!isCancelled()) {
                if (TwoSecondTimerLayout.this.mTxtVwRecordingTime != null) {
                    TwoSecondTimerLayout.access$1210(TwoSecondTimerLayout.this);
                    TwoSecondTimerLayout.this.mTxtVwRecordingTime.setText(TwoSecondTimerLayout.this.mUtil.getRecordingTime(TwoSecondTimerLayout.this.mRecordingTime));
                }
                if (TwoSecondTimerLayout.this.mTxtVwIntervalTime != null && values != null) {
                    TwoSecondTimerLayout.this.mIntervalTime = values[0].intValue();
                    TwoSecondTimerLayout.this.mTxtVwIntervalTime.setText(TwoSecondTimerLayout.this.mUtil.getTimeString(TwoSecondTimerLayout.this.mIntervalTime, TwoSecondTimerLayout.this.mLayout));
                }
                super.onProgressUpdate((Object[]) values);
            }
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            super.onCancelled();
            Log.d("ProgressUpdateTask", "onCancelled()");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            super.onPostExecute((ProgressUpdateTask) result);
            Log.d("ProgressUpdateTask", "onPostExecute()");
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mProgressUpdateTask != null && this.mProgressUpdateTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mProgressUpdateTask.cancel(true);
        }
        this.mProgressUpdateTask = null;
        this.mCurrentView = null;
        releaseBitmapList();
        if (TLCommonUtil.getInstance().isPowerOffDuringCaptureing()) {
            TimelapseExecutorCreator creator = (TimelapseExecutorCreator) TimelapseExecutorCreator.getInstance();
            creator.saveTimeLapseBOData();
        }
    }
}
