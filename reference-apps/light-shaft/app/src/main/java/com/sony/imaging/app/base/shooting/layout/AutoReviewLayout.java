package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.StillFolderViewMode;
import com.sony.imaging.app.base.playback.widget.Aperture;
import com.sony.imaging.app.base.playback.widget.CreativeStyle;
import com.sony.imaging.app.base.playback.widget.DROAutoHDR;
import com.sony.imaging.app.base.playback.widget.ExposureCompensation;
import com.sony.imaging.app.base.playback.widget.FlashCompensation;
import com.sony.imaging.app.base.playback.widget.FocalLength;
import com.sony.imaging.app.base.playback.widget.GraphView;
import com.sony.imaging.app.base.playback.widget.ISO;
import com.sony.imaging.app.base.playback.widget.Icon3D;
import com.sony.imaging.app.base.playback.widget.IconNDFilterFeedback;
import com.sony.imaging.app.base.playback.widget.LabelFileNumber;
import com.sony.imaging.app.base.playback.widget.MediaStatusIcon;
import com.sony.imaging.app.base.playback.widget.MeteringMode;
import com.sony.imaging.app.base.playback.widget.PictureAspect;
import com.sony.imaging.app.base.playback.widget.PictureEffect;
import com.sony.imaging.app.base.playback.widget.PictureSize;
import com.sony.imaging.app.base.playback.widget.PrintDPOF;
import com.sony.imaging.app.base.playback.widget.Protect;
import com.sony.imaging.app.base.playback.widget.RecordedDate;
import com.sony.imaging.app.base.playback.widget.SceneMode;
import com.sony.imaging.app.base.playback.widget.ShutterSpeed;
import com.sony.imaging.app.base.playback.widget.StillQuality;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.playback.widget.ViewModeIcon;
import com.sony.imaging.app.base.playback.widget.WhiteBalance;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DriveModeController;
import com.sony.imaging.app.base.shooting.camera.NDfilterController;
import com.sony.imaging.app.base.shooting.widget.StillImageSizeIconAutoReview;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.meta.Histogram;
import com.sony.scalar.meta.TakenPhotoInfo;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class AutoReviewLayout extends Layout {
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_FINDER;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private static final String LOG_HIST_ERROR = "hist is null";
    private static final String LOG_TAKEN_PHOTO_DATA_ERROR = "info.photo is null";
    private static final String LOG_TAKEN_PHOTO_INFO_ERROR = "TakenPhotoInfo.info is null";
    public static final int ND_FILTER_OFF = 0;
    public static final int ND_FILTER_ON = 1;
    private static final String TAG = "AutoReviewLayout";
    public static final int WAITING_TIME_FOR_UNMUTE = 300;
    private TouchArea mTouchArea;
    private TouchArea.OnTouchAreaListener mTouchListener;
    protected View mCurrentLayout = null;
    private NotificationListener infoListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.layout.AutoReviewLayout.1
        private String[] TAGS = {CameraNotificationManager.PICTURE_REVIEW_INFO};

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            AutoReviewLayout.this.displayInfo();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }
    };
    protected Handler handler = new Handler();
    protected UnmuteRunnable mUnmuteRunnable = new UnmuteRunnable();

    static {
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.shooting_main_sid_autoreview_info));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(1 == Environment.getVersionOfHW() ? R.layout.shooting_main_sid_autoreview_histogram_legacy_emnt : R.layout.shooting_main_sid_autoreview_histogram));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.shooting_main_sid_autoreview_noinfo));
        LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.shooting_main_sid_autoreview_info));
        LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(1 == Environment.getVersionOfHW() ? R.layout.shooting_main_sid_autoreview_histogram_legacy_emnt : R.layout.shooting_main_sid_autoreview_histogram));
        LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.shooting_main_sid_autoreview_noinfo));
        LAYOUT_LIST_FOR_HDMI = new SparseArray<>();
        LAYOUT_LIST_FOR_HDMI.append(1, Integer.valueOf(R.layout.shooting_main_sid_autoreview_info));
        LAYOUT_LIST_FOR_HDMI.append(5, Integer.valueOf(1 == Environment.getVersionOfHW() ? R.layout.shooting_main_sid_autoreview_histogram_legacy_emnt : R.layout.shooting_main_sid_autoreview_histogram));
        LAYOUT_LIST_FOR_HDMI.append(3, Integer.valueOf(R.layout.shooting_main_sid_autoreview_noinfo));
    }

    protected void setReviewInfoListener() {
        CameraNotificationManager.getInstance().setNotificationListener(this.infoListener);
    }

    protected void unsetReviewInfoListener() {
        CameraNotificationManager.getInstance().removeNotificationListener(this.infoListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayInfo() {
        DisplayModeObserver.getInstance();
        CameraNotificationManager camNtfy = CameraNotificationManager.getInstance();
        CameraEx.ReviewInfo info = (CameraEx.ReviewInfo) camNtfy.getValue(CameraNotificationManager.PICTURE_REVIEW_INFO);
        displayInfo(info);
    }

    protected void displayInfo(CameraEx.ReviewInfo info) {
        RecordedDate date;
        int isDisplay;
        if (info == null) {
            Log.w(TAG, LOG_TAKEN_PHOTO_INFO_ERROR);
            return;
        }
        TakenPhotoInfo photo = info.photo;
        if (info.photo == null) {
            Log.d(TAG, LOG_TAKEN_PHOTO_DATA_ERROR);
            return;
        }
        int dispMode = getDispMode();
        if (dispMode == 5) {
            if (info.hist != null) {
                setHistograms(info.hist);
            }
            SceneMode sceneMode = (SceneMode) this.mCurrentLayout.findViewById(R.id.SceneMode);
            if (photo.bSceneCaptureTypeValid && photo.bExposureProgramValid) {
                sceneMode.setValue(photo.mkNoteSceneSelectMode, (int) photo.exposureProgram);
            }
            ExposureCompensation expComp = (ExposureCompensation) this.mCurrentLayout.findViewById(R.id.exposurecompensation);
            if (photo.bExposureBiasValueValid) {
                expComp.setValue(photo.exposureBiasValueNumer, photo.exposureBiasValueDenom);
            }
            MeteringMode meteringMode = (MeteringMode) this.mCurrentLayout.findViewById(R.id.MeteringMode);
            if (photo.bMeteringModeValid) {
                meteringMode.setValue((int) photo.meteringMode);
            }
            FocalLength focalLength = (FocalLength) this.mCurrentLayout.findViewById(R.id.focallength);
            if (photo.bFocalLengthValid) {
                focalLength.setValue((int) photo.focalLengthNumer, (int) photo.focalLengthDenom);
            }
            WhiteBalance whiteBalance = (WhiteBalance) this.mCurrentLayout.findViewById(R.id.whitebalance);
            if (photo.bWhiteBalanceValid) {
                whiteBalance.setValue(photo.mkNoteWhiteBalance, photo.mkNoteWBTuning, photo.mkNoteWB2AxisTuningVal1, photo.mkNoteWB2AxisTuningVal2, photo.mkNoteClolorTemp);
            }
            FlashCompensation flashComp = (FlashCompensation) this.mCurrentLayout.findViewById(R.id.flashcompensation);
            if (photo.bMkNoteFlashBias && photo.bFlashValid) {
                flashComp.setValue(photo.flash, photo.mkNoteFlashBiasNumer, photo.mkNoteFlashBiasDenom);
            }
            CreativeStyle creativeStyle = (CreativeStyle) this.mCurrentLayout.findViewById(R.id.creativestyle);
            if (photo.bMkNoteCreativeStyle) {
                creativeStyle.setValue(photo.mkNoteCreativeStyle);
            }
            IconNDFilterFeedback iconNDFilterFeedback = (IconNDFilterFeedback) this.mCurrentLayout.findViewById(R.id.NDFilterFeedbackPb);
            String value = NDfilterController.getInstance().getValue(NDfilterController.STATUS_ID_NDFILTER);
            if ("on".equals(value)) {
                isDisplay = 1;
            } else {
                isDisplay = 0;
            }
            iconNDFilterFeedback.setValue(isDisplay);
        }
        if (dispMode == 1 || dispMode == 5) {
            if (photo.bExposureTimeValid) {
                ShutterSpeed ss = (ShutterSpeed) this.mCurrentLayout.findViewById(R.id.shutterspeed);
                ss.setValue((int) photo.exposureTimeNumer, (int) photo.exposureTimeDenom);
            }
            if (photo.bFNumberValid) {
                Aperture aperture = (Aperture) this.mCurrentLayout.findViewById(R.id.aperture);
                aperture.setValue((int) photo.FNumberNumer, (int) photo.FNumberDenom);
            }
            if (photo.bISOSpeedRatingsValid) {
                ISO iso = (ISO) this.mCurrentLayout.findViewById(R.id.iso);
                iso.setValue(photo.ISOSpeedRatings);
            }
            if (photo.bRecDateTimeValid && (date = (RecordedDate) this.mCurrentLayout.findViewById(R.id.recordedDate)) != null) {
                date.setValue((int) photo.recYear, (int) photo.recMonth, (int) photo.recDate, (int) photo.recHour, (int) photo.recMinute, (int) photo.recSecond);
            }
            StillQuality stillQuality = (StillQuality) this.mCurrentLayout.findViewById(R.id.stillquality);
            if (stillQuality != null && photo.bMkNoteImgQual) {
                stillQuality.setValue(photo.mkNoteImgQual);
            }
            if (photo.bStillMainSizeValid) {
                StillImageSizeIconAutoReview stillSize = (StillImageSizeIconAutoReview) this.mCurrentLayout.findViewById(R.id.StillImageSize);
                stillSize.setValue(photo.imageSize);
                if (photo.mkNoteImgQual == 0) {
                    stillSize.setVisibility(4);
                } else {
                    stillSize.setVisibility(0);
                }
                PictureAspect picAspect = (PictureAspect) this.mCurrentLayout.findViewById(R.id.pictureaspect);
                picAspect.setAutoReviewValue(photo.imageAspectRatio);
            }
            if (!MediaNotificationManager.getInstance().isNoCard()) {
                LabelFileNumber labelFileNumber = (LabelFileNumber) this.mCurrentLayout.findViewById(R.id.fileNumber);
                if (photo.bFileNoValid) {
                    labelFileNumber.setValue(false, photo.dirNo, photo.fileNo);
                }
            }
            DROAutoHDR DROHDR = (DROAutoHDR) this.mCurrentLayout.findViewById(R.id.droautohdr);
            if (photo.bMkNoteAutoHDR && photo.bMkNoteEnhanceLevel) {
                DROHDR.setValue(photo.mkNoteAutoHDR, photo.mkNoteEnhanceLevel);
            }
            PictureEffect picEffect = (PictureEffect) this.mCurrentLayout.findViewById(R.id.pictureeffect);
            if (photo.bMkNotePictureEffect) {
                picEffect.setValue((int) photo.mkNotePictureEffect, (int) photo.mkNotePictEffectError);
            }
        }
        this.mCurrentLayout.invalidate();
    }

    private void setHistograms(Histogram hist) {
        if (hist != null) {
            GraphView h = (GraphView) this.mCurrentLayout.findViewById(R.id.yGraphView);
            if (h != null && hist.Y != null) {
                h.setHistogram(hist.Y);
            }
            GraphView h2 = (GraphView) this.mCurrentLayout.findViewById(R.id.rGraphView);
            if (h2 != null && hist.R != null) {
                h2.setHistogram(hist.R);
            }
            GraphView h3 = (GraphView) this.mCurrentLayout.findViewById(R.id.gGraphView);
            if (h3 != null && hist.G != null) {
                h3.setHistogram(hist.G);
            }
            GraphView h4 = (GraphView) this.mCurrentLayout.findViewById(R.id.bGraphView);
            if (h4 != null && hist.B != null) {
                h4.setHistogram(hist.B);
                return;
            }
            return;
        }
        Log.d(TAG, LOG_HIST_ERROR);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout layout = (RelativeLayout) obtainViewFromPool(getLayoutResource());
        if (layout != this.mCurrentLayout) {
            OptimizedImageView img = layout.findViewById(R.id.imageSingle);
            if (img != null) {
                img.setVisibility(8);
            }
            ViewGroup errView = (ViewGroup) layout.findViewById(R.id.pb_parts_cmn_singlepb_image_err);
            if (errView != null) {
                errView.setVisibility(8);
            }
            Icon3D _3d = (Icon3D) layout.findViewById(R.id.icon3d);
            if (_3d != null) {
                _3d.setVisibility(8);
            }
            PrintDPOF dpof = (PrintDPOF) layout.findViewById(R.id.dpof);
            if (dpof != null) {
                dpof.setVisibility(8);
            }
            Protect protect = (Protect) layout.findViewById(R.id.protect);
            if (protect != null) {
                protect.setVisibility(8);
            }
            PictureSize pictureSize = (PictureSize) layout.findViewById(R.id.picturesize);
            if (pictureSize != null) {
                pictureSize.setVisibility(8);
            }
            ViewModeIcon viewModeIcon = (ViewModeIcon) layout.findViewById(R.id.playview);
            if (viewModeIcon != null) {
                if (MediaNotificationManager.getInstance().isNoCard()) {
                    viewModeIcon.setVisibility(8);
                } else {
                    viewModeIcon.setValue(StillFolderViewMode.class);
                }
            }
            MediaStatusIcon mediaStatusIcon = (MediaStatusIcon) layout.findViewById(R.id.pbicon_media);
            if (mediaStatusIcon != null) {
                mediaStatusIcon.setValue(MediaNotificationManager.getInstance().isNoCard() ? 0 : 1);
            }
            MediaStatusIcon mediaStatusIcon2 = (MediaStatusIcon) layout.findViewById(R.id.pbicon_nocard);
            if (mediaStatusIcon2 != null) {
                mediaStatusIcon2.setValue(MediaNotificationManager.getInstance().isNoCard() ? 0 : 1);
            }
            this.mCurrentLayout = layout;
        }
        return this.mCurrentLayout;
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        if (this.mTouchArea != null) {
            this.mTouchArea.setTouchAreaListener(null);
            this.mTouchArea = null;
        }
        this.mCurrentLayout = null;
        super.onDestroyView();
    }

    public void setOnTouchAreaListener(TouchArea.OnTouchAreaListener l) {
        this.mTouchListener = l;
        if (this.mTouchArea != null) {
            this.mTouchArea.setTouchAreaListener(l);
        }
    }

    protected int getDispMode() {
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (displayMode == 5 && DriveModeController.getInstance().isBlackoutFreeShooting()) {
            return 1;
        }
        return displayMode;
    }

    private int getLayoutResource() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = getDispMode();
        switch (device) {
            case 0:
                int ret = LAYOUT_LIST_FOR_PANEL.get(displayMode).intValue();
                return ret;
            case 1:
                int ret2 = LAYOUT_LIST_FOR_FINDER.get(displayMode).intValue();
                return ret2;
            case 2:
                int ret3 = LAYOUT_LIST_FOR_HDMI.get(displayMode).intValue();
                return ret3;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        FooterGuide guide;
        super.onResume();
        setVisibility(true);
        if (Environment.getVersionOfHW() == 1 && MediaNotificationManager.getInstance().isMounted()) {
            if (this.mCurrentLayout != null && (guide = (FooterGuide) this.mCurrentLayout.findViewById(getFooterGuideResource())) != null) {
                guide.setData(new FooterGuideDataResId(getActivity(), 0, android.R.string.heavy_weight_notification_detail));
            } else {
                return;
            }
        }
        setReviewInfoListener();
    }

    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        setVisibility(false);
        unsetReviewInfoListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class UnmuteRunnable implements Runnable {
        protected UnmuteRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
        }
    }

    private void setVisibility(boolean visible) {
        if (visible) {
            this.mCurrentLayout.setVisibility(0);
            this.handler.postDelayed(this.mUnmuteRunnable, 300L);
        } else {
            this.mCurrentLayout.setVisibility(4);
            this.handler.removeCallbacks(this.mUnmuteRunnable);
        }
    }
}
