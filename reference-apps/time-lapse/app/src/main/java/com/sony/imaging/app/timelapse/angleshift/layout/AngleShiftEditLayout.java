package com.sony.imaging.app.timelapse.angleshift.layout;

import android.content.ContentResolver;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftImageEditor;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFaderController;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.timelapse.playback.layout.ListViewLayout;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class AngleShiftEditLayout extends OptimizedImageLayoutBase implements View.OnClickListener, NotificationListener, IModableLayout {
    private static final int SCALE_CROPPED_IMAGE = 1;
    private static final int SHOW_DIRECT = 0;
    private ViewGroup mCurrentView;
    private Handler mHandler;
    private OnLayoutModeChangeListener mLayoutChangeListener;
    private OptimizedImageView mOptImageView;
    private static PlayBackController pbCntl = null;
    private static OptimizedImage mOptImage = null;
    private static View[] mViewArray = null;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF = new int[0];
    private static final int[] IMAGE_ERR_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_singlepb_image_err};
    private ImageView mTestShot = null;
    private boolean mSetOptimizedImage = false;
    public ContentResolver mResolver = null;
    private OptimizedImageView mImageView = null;
    private TextView mStartFrame = null;
    private TextView mEndFrame = null;
    private TextView mTheme = null;
    private ImageView mRecMode = null;
    private ImageView mFader = null;
    private ImageView mMovieSize = null;
    private ImageView mShots = null;
    private TextView mNumberOfImages = null;
    private ImageView mPlayBack = null;
    private TextView mPlayBackTime = null;
    private TextView mPreviewBtn = null;
    private TextView mSaveBtn = null;
    private ImageView mHeader = null;
    private ImageView mAppIcon = null;
    private BatteryIcon mBattery = null;
    private ImageView mCroppedStartFrame = null;
    private ImageView mCroppedEndFrame = null;
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    private boolean mIsLeftRotation = false;
    private String mPrevMessage = null;
    private final String[] TAGS = {AngleShiftConstants.TAG_AS_BOOT_DONE, AngleShiftConstants.TAG_AS_OPTION_LAYOUT, AngleShiftConstants.TAG_AS_THEME_LAYOUT, AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT, AngleShiftConstants.TAG_AS_FRAMESETTING_LAYOUT, AngleShiftConstants.TAG_AS_CROP_LAYOUT, AngleShiftConstants.TAG_AS_SHOW_DIRECT, AngleShiftConstants.TAG_AS_SCALE_CROPPED_IMAGE, AngleShiftConstants.TAG_AS_ROTATE, AngleShiftConstants.TAG_AS_SAVE_EXEC};
    private boolean mNeedReset = false;

    public void showImage(boolean b) {
        if (b) {
            getOptimizedImageView().setOptimizedImage(mOptImage);
        } else {
            getOptimizedImageView().setOptimizedImage((OptimizedImage) null);
        }
    }

    public void updateImage(OptimizedImage i) {
        updateOptimizedImage(i);
    }

    private void initializeView() {
        this.mRecMode = (ImageView) this.mCurrentView.findViewById(R.id.recording_mode);
        this.mFader = (ImageView) this.mCurrentView.findViewById(R.id.fader);
        this.mMovieSize = (ImageView) this.mCurrentView.findViewById(R.id.moviesize);
        this.mShots = (ImageView) this.mCurrentView.findViewById(R.id.shooting_num_icon);
        this.mNumberOfImages = (TextView) this.mCurrentView.findViewById(R.id.shooting_num_value);
        this.mNumberOfImages.setText("" + AngleShiftSetting.getInstance().getTargetNumber());
        this.mPlayBack = (ImageView) this.mCurrentView.findViewById(R.id.playback_image_view);
        this.mPlayBackTime = (TextView) this.mCurrentView.findViewById(R.id.playback_value);
        this.mPlayBackTime.setText(TLCommonUtil.getInstance().getTimeString(AngleShiftSetting.getInstance().getPlackBackDuration(), this));
        this.mTheme = (TextView) this.mCurrentView.findViewById(R.id.theme_name);
        this.mTheme.setText(getThemeStringId());
        this.mPreviewBtn = (TextView) this.mCurrentView.findViewById(R.id.previewBtn);
        this.mSaveBtn = (TextView) this.mCurrentView.findViewById(R.id.saveBtn);
        this.mHeader = (ImageView) this.mCurrentView.findViewById(R.id.header);
        this.mAppIcon = (ImageView) this.mCurrentView.findViewById(R.id.app_icon);
        this.mBattery = (BatteryIcon) this.mCurrentView.findViewById(R.id.BatteryIcon);
        this.mCroppedStartFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_upper);
        this.mCroppedStartFrame.setBackgroundResource(R.drawable.p_16_dd_parts_tm_start_crop_frame_normal);
        this.mCroppedEndFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame_lower);
        this.mCroppedEndFrame.setBackgroundResource(R.drawable.p_16_dd_parts_tm_end_crop_frame_normal);
        this.mStartFrame = (TextView) this.mCurrentView.findViewById(R.id.edit_start_flag_name);
        this.mEndFrame = (TextView) this.mCurrentView.findViewById(R.id.edit_end_flag_name);
        mViewArray = new View[]{this.mHeader, this.mAppIcon, this.mBattery, this.mRecMode, this.mFader, this.mMovieSize, this.mShots, this.mPlayBack, this.mNumberOfImages, this.mPlayBackTime, this.mPreviewBtn, this.mSaveBtn, this.mCroppedStartFrame, this.mCroppedEndFrame};
        setVisibility(4);
        setCroppedFrameVisibility(4);
        this.mPreviewBtn.setOnClickListener(this);
        this.mSaveBtn.setOnClickListener(this);
    }

    private void updateCroppedFrame() {
        Rect startRect = AngleShiftSetting.getInstance().getAngleShiftStartRect();
        Rect endRect = AngleShiftSetting.getInstance().getAngleShiftEndRect();
        updateCroppedFrame(startRect, endRect);
    }

    private void updateCroppedFrame(Rect startRect, Rect endRect) {
        RelativeLayout.LayoutParams paramsStart = new RelativeLayout.LayoutParams(-2, -2);
        RelativeLayout.LayoutParams paramsEnd = new RelativeLayout.LayoutParams(-2, -2);
        int width = AngleShiftSetting.getInstance().getAngleShiftImageWidth();
        int height = AngleShiftSetting.getInstance().getAngleShiftImageHeight();
        int dispHeight = AngleShiftSetting.getInstance().getSingleYUVDisplayHeight();
        Rect scaledRect = AngleShiftSetting.getInstance().getScaledCroppedRect(width, height, 640, dispHeight, startRect);
        paramsStart.height = scaledRect.height();
        paramsStart.width = scaledRect.width();
        paramsStart.leftMargin = scaledRect.left;
        paramsStart.topMargin = scaledRect.top;
        this.mCroppedStartFrame.setLayoutParams(paramsStart);
        Rect scaledRect2 = AngleShiftSetting.getInstance().getScaledCroppedRect(width, height, 640, dispHeight, endRect);
        paramsEnd.height = scaledRect2.height();
        paramsEnd.width = scaledRect2.width();
        paramsEnd.leftMargin = scaledRect2.left;
        paramsEnd.topMargin = scaledRect2.top;
        this.mCroppedEndFrame.setLayoutParams(paramsEnd);
    }

    private void setVisibility(int visibility) {
        for (int i = 0; i < mViewArray.length; i++) {
            mViewArray[i].setSelected(false);
        }
        this.mRecMode.setVisibility(visibility);
        if (AngleShiftFaderController.getInstance().isAvailable(null)) {
            this.mFader.setVisibility(visibility);
        } else {
            this.mFader.setVisibility(4);
        }
        this.mMovieSize.setVisibility(visibility);
        this.mShots.setVisibility(visibility);
        this.mNumberOfImages.setVisibility(visibility);
        this.mPlayBack.setVisibility(visibility);
        this.mPlayBackTime.setVisibility(visibility);
        this.mPreviewBtn.setVisibility(visibility);
        this.mSaveBtn.setVisibility(visibility);
        this.mStartFrame.setVisibility(visibility);
        this.mEndFrame.setVisibility(visibility);
        if (visibility == 0) {
            setFocusableView(this.mPreviewBtn);
            clearFocusedView(this.mSaveBtn);
        } else {
            clearFocusedView(this.mPreviewBtn);
            clearFocusedView(this.mSaveBtn);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCroppedFrameVisibility(int visibility) {
        this.mCroppedStartFrame.setVisibility(visibility);
        this.mCroppedEndFrame.setVisibility(visibility);
    }

    private int getThemeStringId() {
        int themeId = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        switch (themeId) {
            case 0:
                return R.string.STRID_FUNC_TIMELAPSE_EFFECT_PAN;
            case 1:
                return R.string.STRID_FUNC_TIMELAPSE_EFFECT_TILT;
            case 2:
                return 17042211;
            case 3:
                return R.string.STRID_FUNC_TIMELAPSE_EFFECT_OFF;
            case 4:
                return R.string.STRID_AMC_STR_01036;
            default:
                return R.string.STRID_FUNC_TIMELAPSE_EFFECT_PAN;
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mTestShot = (ImageView) this.mCurrentView.findViewById(R.id.testshot);
        this.mTestShot.setVisibility(4);
        initializeView();
        if (this.mHandler == null) {
            this.mHandler = new MyHandler();
        }
        this.mOptImageView = this.mCurrentView.findViewById(R.id.imageSingle);
        updateVPicDisplay(this.mOptImageView, null);
        this.mOptImageView.setOnDisplayEventListener(new OptimizedImageView.onDisplayEventListener() { // from class: com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftEditLayout.1
            public void onDisplay(int errCd) {
                AngleShiftEditLayout.this.mSetOptimizedImage = true;
            }
        });
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mSetOptimizedImage = false;
        this.mNeedReset = false;
        this.mIsLeftRotation = false;
        this.mPrevMessage = AngleShiftConstants.TAG_AS_THEME_LAYOUT;
        AngleShiftSetting.getInstance().setOptimizedImageValidness(true);
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        this.mResolver = getActivity().getContentResolver();
        pbCntl = PlayBackController.getInstance();
        pbCntl.setContentResolver(this.mResolver);
        pbCntl.intializeCursorData(ListViewLayout.listPosition);
        long start = System.currentTimeMillis();
        mOptImage = pbCntl.getPlayBackMainImage(true);
        long end = System.currentTimeMillis();
        AngleShiftImageEditor.getInstance().setPictureDecodingTime(end - start);
        AppLog.info(this.TAG, "decoding time to use estimation. = " + (end - start));
        setAngleShiftRect();
        updateCroppedFrame();
        updateOptimizedImage(mOptImage);
        showImage(TLCommonUtil.getInstance().isAngleShiftBootDone());
        this.mLayoutChangeListener = new OnLayoutModeChangeListener(this, 1);
        DisplayModeObserver.getInstance().setNotificationListener(this.mLayoutChangeListener);
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        DisplayModeObserver.getInstance().removeNotificationListener(this.mLayoutChangeListener);
        this.mLayoutChangeListener = null;
        this.mPreviewBtn.setOnClickListener(null);
        this.mPreviewBtn = null;
        this.mSaveBtn.setOnClickListener(null);
        this.mSaveBtn = null;
        releaseImageViewDrawable(this.mRecMode);
        this.mRecMode = null;
        releaseImageViewDrawable(this.mFader);
        this.mFader = null;
        releaseImageViewDrawable(this.mMovieSize);
        this.mMovieSize = null;
        this.mShots = null;
        this.mCroppedStartFrame.setBackgroundResource(0);
        this.mCroppedStartFrame = null;
        this.mCroppedEndFrame.setBackgroundResource(0);
        this.mCroppedEndFrame = null;
        if (getLayout("ID_ANGLESHIFTFRAMESELECTIONLAYOUT") != null) {
            getLayout("ID_ANGLESHIFTFRAMESELECTIONLAYOUT").closeLayout();
        }
        if (getLayout("ID_ANGLESHIFTCONFIRMEXITLAYOUT") != null) {
            getLayout("ID_ANGLESHIFTCONFIRMEXITLAYOUT").closeLayout();
        }
        if (getLayout("ID_ANGLESHIFTCONFIRMSAVINGLAYOUT") != null) {
            getLayout("ID_ANGLESHIFTCONFIRMSAVINGLAYOUT").closeLayout();
        }
        if (getLayout("ID_ANGLESHIFTFRAMESETTINGLAYOUT") != null) {
            getLayout("ID_ANGLESHIFTFRAMESETTINGLAYOUT").closeLayout();
        }
        if (getLayout("ID_ANGLESHIFTFRAMECROPPINGLAYOUT") != null) {
            getLayout("ID_ANGLESHIFTFRAMECROPPINGLAYOUT").closeLayout();
        }
        releaseOptimizedImage();
        this.mResolver = null;
        if (pbCntl != null) {
            pbCntl.releaseAllAllocatedData();
        }
        this.mNumberOfImages = null;
        this.mPlayBack = null;
        this.mPlayBackTime = null;
        this.mPreviewBtn = null;
        this.mSaveBtn = null;
        pbCntl = null;
        this.mStartFrame = null;
        this.mEndFrame = null;
        this.mHeader = null;
        this.mAppIcon = null;
        this.mBattery = null;
        mViewArray = null;
        this.mPrevMessage = null;
        this.mTheme = null;
        this.mTestShot = null;
        this.mImageView = null;
        this.mOptImageView = null;
        this.mCurrentView = null;
        super.onPause();
        System.gc();
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

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected void updateVPicDisplay(OptimizedImageView img, ContentsManager mgr) {
        RotateImageFilter.ROTATION_DEGREE degree = AngleShiftSetting.getInstance().getRotateDegree();
        if (RotateImageFilter.ROTATION_DEGREE.DEGREE_0.equals(degree)) {
            img.setDisplayRotationAngle(0);
            return;
        }
        if (RotateImageFilter.ROTATION_DEGREE.DEGREE_90.equals(degree)) {
            img.setDisplayRotationAngle(90);
        } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_180.equals(degree)) {
            img.setDisplayRotationAngle(180);
        } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_270.equals(degree)) {
            img.setDisplayRotationAngle(270);
        }
    }

    private void executeRotation(OptimizedImageView img, boolean isAntiClockwise) {
        if (isAntiClockwise) {
            RotateImageFilter.ROTATION_DEGREE degree = AngleShiftSetting.getInstance().getRotateDegree();
            if (RotateImageFilter.ROTATION_DEGREE.DEGREE_0.equals(degree)) {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_270);
            } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_270.equals(degree)) {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_180);
            } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_180.equals(degree)) {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_90);
            } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_90.equals(degree)) {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_0);
            }
        } else {
            RotateImageFilter.ROTATION_DEGREE degree2 = AngleShiftSetting.getInstance().getRotateDegree();
            if (RotateImageFilter.ROTATION_DEGREE.DEGREE_0.equals(degree2)) {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_90);
            } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_90.equals(degree2)) {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_180);
            } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_180.equals(degree2)) {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_270);
            } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_270.equals(degree2)) {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_0);
            }
        }
        AngleShiftSetting.getInstance().backupCustomSettings();
        updateVPicDisplay(img, null);
    }

    private void toggleVPicDisplay(OptimizedImageView img) {
        RotateImageFilter.ROTATION_DEGREE degree = AngleShiftSetting.getInstance().getRotateDegree();
        if (RotateImageFilter.ROTATION_DEGREE.DEGREE_0.equals(degree)) {
            AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_270);
        } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_270.equals(degree)) {
            AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_180);
        } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_180.equals(degree)) {
            AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_90);
        } else if (RotateImageFilter.ROTATION_DEGREE.DEGREE_90.equals(degree)) {
            AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_0);
        }
        updateVPicDisplay(img, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateOptimizedImageLayoutParam(OptimizedImageView imgView, ContentsManager mgr) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgView.getLayoutParams();
        params.width = AppRoot.USER_KEYCODE.DIAL2_RIGHT;
        params.height = 388;
        params.topMargin = 46;
        params.leftMargin = 112;
        Point p = new Point(params.width >> 1, params.height >> 1);
        imgView.setPivot(p);
        imgView.setLayoutParams(params);
        this.mImageView = imgView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.edit_single_image;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateDisplay() {
        updateOptimizedImage(mOptImage);
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageOkLayoutResourceId() {
        return IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageErrLayoutResourceId() {
        return IMAGE_ERR_LAYOUT_RESOURCE_ID;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_THEME_LAYOUT) || tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_OPTION_LAYOUT) || tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT) || tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_FRAMESETTING_LAYOUT) || tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CROP_LAYOUT) || tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_SHOW_DIRECT) || tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_SCALE_CROPPED_IMAGE)) {
            refresh(tag);
            return;
        }
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_BOOT_DONE)) {
            showImage(true);
            return;
        }
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_SAVE_EXEC)) {
            closeLayout();
            invalidate();
            openLayout("ID_ANGLESHIFTPROGRESSLAYOUT");
        } else if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_ROTATE)) {
            executeRotation(this.mOptImageView, this.mIsLeftRotation);
            this.mHandler.sendEmptyMessage(0);
            this.mNeedReset = true;
        }
    }

    private void refresh(String tag) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.mImageView.getLayoutParams();
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_SHOW_DIRECT)) {
            this.mHandler.sendEmptyMessage(0);
            updateVPicDisplay(this.mOptImageView, null);
            return;
        }
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_SCALE_CROPPED_IMAGE)) {
            this.mHandler.sendEmptyMessage(1);
            return;
        }
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_THEME_LAYOUT)) {
            this.mPrevMessage = AngleShiftConstants.TAG_AS_THEME_LAYOUT;
            params.width = AppRoot.USER_KEYCODE.DIAL2_RIGHT;
            params.height = 388;
            params.topMargin = 46;
            params.leftMargin = 112;
            setVisibility(4);
            setCroppedFrameVisibility(4);
        } else if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_OPTION_LAYOUT)) {
            this.mPrevMessage = AngleShiftConstants.TAG_AS_OPTION_LAYOUT;
            params.width = 640;
            params.height = 420;
            params.topMargin = 0;
            params.leftMargin = 0;
            setVisibility(4);
            setCroppedFrameVisibility(4);
        } else if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT)) {
            if (!this.mPrevMessage.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT)) {
                AngleShiftImageEditor.getInstance().checkStatus();
            }
            this.mPrevMessage = AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT;
            this.mHandler.sendEmptyMessage(0);
            params.width = 640;
            params.height = AngleShiftSetting.getInstance().getSingleYUVDisplayHeight();
            params.topMargin = 60;
            params.leftMargin = 0;
            this.mTheme.setText(getThemeStringId());
            setVisibility(0);
            setCroppedFrameVisibility(4);
        } else if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_FRAMESETTING_LAYOUT)) {
            params.width = 640;
            params.height = AngleShiftSetting.getInstance().getSingleYUVDisplayHeight();
            params.topMargin = 60;
            params.leftMargin = 0;
            setVisibility(4);
            setCroppedFrameVisibility(0);
        } else if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CROP_LAYOUT)) {
            params.width = 640;
            params.height = AngleShiftSetting.getInstance().getSingleYUVDisplayHeight();
            params.topMargin = 60;
            params.leftMargin = 0;
            setCroppedFrameVisibility(4);
        }
        Point p = new Point(params.width >> 1, params.height >> 1);
        this.mImageView.setPivot(p);
        this.mImageView.setLayoutParams(params);
        updateVPicDisplay(this.mOptImageView, null);
        refreshInfo();
    }

    private void refreshInfo() {
        AngleShiftSetting.getInstance().updateFaderIcon(this.mFader);
        AngleShiftSetting.getInstance().updateMovieSizeIcon(this.mMovieSize);
        AngleShiftSetting.getInstance().updateRecModeIcon(this.mRecMode);
        this.mPlayBackTime.setText(TLCommonUtil.getInstance().getTimeString(AngleShiftSetting.getInstance().getPlackBackDuration(), this));
        this.mNumberOfImages.setText("" + AngleShiftSetting.getInstance().getTargetNumber());
        updateCroppedFrame();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View arg0) {
        handleCenterAction();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isConfirmButtonEnable() {
        return this.mPreviewBtn.getVisibility() == 0 && this.mSaveBtn.getVisibility() == 0;
    }

    private void setFocusableView(View view) {
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        view.requestFocus();
        view.setSelected(true);
    }

    private void clearFocusedView(View view) {
        view.clearFocus();
        view.setFocusable(false);
        view.setSelected(false);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int ret = 0;
        if (event.getScanCode() == 232 && event.getRepeatCount() == 0) {
            ret = handleCenterAction();
        } else if (event.getScanCode() == 105 || event.getScanCode() == 106) {
            ret = handleRotation(event.getScanCode() == 105);
        } else if (event.getScanCode() == 103) {
            ret = handleUpAction();
        } else if (event.getScanCode() == 108) {
            ret = handleDownAction();
        } else if (event.getScanCode() == 595 || event.getScanCode() == 513) {
            ret = handleDeleteAction();
            if (1 == ret) {
                return 0;
            }
        } else if (event.getScanCode() == 589) {
            ret = -1;
        }
        if (ret == 0) {
            ret = super.onKeyDown(keyCode, event);
        }
        return ret;
    }

    private int handleRotation(boolean isAntiClockwise) {
        if (!isConfirmButtonEnable() || !AngleShiftSetting.getInstance().isValidOptimizedImage()) {
            return -1;
        }
        if (AngleShiftSetting.getInstance().getAngleShiftImageOriginalHeight() < 1920) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_CANNOT_ROTATE);
        } else if (!AngleShiftSetting.getInstance().isAngleShiftCustomDefaultRect()) {
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_RESET_BY_ROTATE);
        } else {
            CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_ROTATE);
        }
        this.mIsLeftRotation = isAntiClockwise;
        return 1;
    }

    private int handleCenterAction() {
        if (isConfirmButtonEnable()) {
            if (this.mSaveBtn.hasFocus()) {
                if (AngleShiftImageEditor.getInstance().checkStatus()) {
                    Bundle b = new Bundle();
                    b.putInt("ShootCount", AngleShiftSetting.getInstance().getTargetNumber());
                    openLayout("ID_ANGLESHIFTCONFIRMSAVINGLAYOUT", b);
                }
            } else if (this.mPreviewBtn.hasFocus()) {
                closeLayout();
                invalidate();
                openLayout("ID_ANGLESHIFTPREVIEWLAYOUT");
            }
            return 1;
        }
        return -1;
    }

    private int handleUpAction() {
        if (!isConfirmButtonEnable()) {
            return 0;
        }
        if (this.mSaveBtn.hasFocus()) {
            setFocusableView(this.mPreviewBtn);
            clearFocusedView(this.mSaveBtn);
        }
        return 1;
    }

    private int handleDownAction() {
        if (!isConfirmButtonEnable()) {
            return 0;
        }
        if (this.mPreviewBtn.hasFocus()) {
            clearFocusedView(this.mPreviewBtn);
            setFocusableView(this.mSaveBtn);
        }
        return 1;
    }

    private int handleDeleteAction() {
        return isConfirmButtonEnable() ? 1 : -1;
    }

    /* loaded from: classes.dex */
    private class MyHandler extends Handler {
        private MyHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (AngleShiftEditLayout.this.mSetOptimizedImage) {
                        AngleShiftEditLayout.this.mHandler.removeMessages(0);
                        AngleShiftEditLayout.this.mSetOptimizedImage = false;
                        AngleShiftEditLayout.pbCntl.moveToPosition(AngleShiftSetting.getInstance().getframeId());
                        AngleShiftEditLayout.this.playOneByOne();
                        AngleShiftEditLayout.this.updateVPicDisplay(AngleShiftEditLayout.this.mOptImageView, null);
                        if (AngleShiftEditLayout.this.isConfirmButtonEnable()) {
                            AngleShiftEditLayout.this.setCroppedFrameVisibility(0);
                        }
                        if (AngleShiftEditLayout.this.mNeedReset) {
                            AngleShiftEditLayout.this.mNeedReset = false;
                            AngleShiftSetting.getInstance().resetAngleShiftCustomRect();
                            return;
                        }
                        return;
                    }
                    AngleShiftEditLayout.this.mHandler.sendEmptyMessage(0);
                    return;
                case 1:
                    if (AngleShiftEditLayout.this.mSetOptimizedImage) {
                        AngleShiftEditLayout.this.mSetOptimizedImage = false;
                        AngleShiftEditLayout.pbCntl.moveToPosition(AngleShiftSetting.getInstance().getframeId());
                        AngleShiftEditLayout.this.scaleCroppedImage();
                        return;
                    }
                    AngleShiftEditLayout.this.mHandler.sendEmptyMessage(1);
                    return;
                default:
                    return;
            }
        }
    }

    private void releaseOptimizedImage() {
        if (mOptImage != null) {
            mOptImage.release();
            mOptImage = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playOneByOne() {
        AppLog.info("cursor position", "" + pbCntl.getPosition());
        releaseOptimizedImage();
        mOptImage = pbCntl.getPlayBackMainImage(true);
        this.mOptImageView.setOptimizedImage(mOptImage);
        AngleShiftSetting.getInstance().setOptimizedImageValidness(mOptImage != null);
        if (mOptImage != null) {
            setAngleShiftRect();
            updateCroppedFrame();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scaleCroppedImage() {
        Rect rect;
        AppLog.info("cursor position", "" + pbCntl.getPosition());
        CropImageFilter cropImageFilter = new CropImageFilter();
        ScaleImageFilter scaleImageFilter = new ScaleImageFilter();
        if (AngleShiftConstants.START_FRAMESETTING.equalsIgnoreCase(AngleShiftSetting.getInstance().getSelectedFrame())) {
            rect = AngleShiftSetting.getInstance().getAngleShiftCustomStartRect();
        } else {
            rect = AngleShiftSetting.getInstance().getAngleShiftCustomEndRect();
        }
        releaseOptimizedImage();
        mOptImage = pbCntl.getPlayBackMainImage(true);
        if (AngleShiftSetting.getInstance().isRotatedImage()) {
            RotateImageFilter rotateImageFilter = new RotateImageFilter();
            mOptImage = AngleShiftSetting.getInstance().getRotatedImage(rotateImageFilter, mOptImage, true);
            rotateImageFilter.release();
            this.mOptImageView.setDisplayRotationAngle(0);
        }
        if (AngleShiftSetting.getInstance().isPreRC()) {
            scaleImageFilter.setDestSize(AngleShiftSetting.getInstance().getAngleShiftImageWidth(), AngleShiftSetting.getInstance().getAngleShiftImageHeight());
            scaleImageFilter.setSource(mOptImage, true);
            boolean result = scaleImageFilter.execute();
            if (result) {
                mOptImage = scaleImageFilter.getOutput();
            }
            scaleImageFilter.clearSources();
        }
        cropImageFilter.setSrcRect(rect);
        cropImageFilter.setSource(mOptImage, true);
        boolean result2 = cropImageFilter.execute();
        if (result2) {
            mOptImage = cropImageFilter.getOutput();
        }
        cropImageFilter.clearSources();
        scaleImageFilter.setDestSize(1920, 1080);
        scaleImageFilter.setSource(mOptImage, true);
        boolean result3 = scaleImageFilter.execute();
        if (result3) {
            mOptImage = scaleImageFilter.getOutput();
        }
        scaleImageFilter.clearSources();
        this.mOptImageView.setOptimizedImage(mOptImage);
        cropImageFilter.release();
        scaleImageFilter.release();
    }

    private void setAngleShiftRect() {
        RotateImageFilter.ROTATION_DEGREE degree = AngleShiftSetting.getInstance().getRotateDegree();
        if (RotateImageFilter.ROTATION_DEGREE.DEGREE_0.equals(degree) || RotateImageFilter.ROTATION_DEGREE.DEGREE_180.equals(degree)) {
            this.mImageWidth = mOptImage.getWidth();
            this.mImageHeight = mOptImage.getHeight();
        } else {
            this.mImageWidth = mOptImage.getHeight();
            this.mImageHeight = mOptImage.getWidth();
        }
        AngleShiftSetting.getInstance().setAngleShiftRect(this.mImageWidth, this.mImageHeight);
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        if (169 == ScalarProperties.getInt("device.panel.aspect")) {
            updateCroppedFrame();
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            int result = super.onConvertedKeyDown(event, func);
            return result;
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
                int result2 = super.onConvertedKeyDown(event, func);
                return result2;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getScanCode()) {
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
