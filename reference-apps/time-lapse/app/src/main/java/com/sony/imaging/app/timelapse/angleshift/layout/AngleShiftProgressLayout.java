package com.sony.imaging.app.timelapse.angleshift.layout;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sony.imaging.app.avi.AviExporter;
import com.sony.imaging.app.avi.DatabaseUtil;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftImageEditor;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFrameRateController;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftMovieController;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseAdapter;
import com.sony.imaging.app.timelapse.databaseutil.DataBaseOperations;
import com.sony.imaging.app.timelapse.databaseutil.TimeLapseBO;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.timelapse.playback.layout.ListViewLayout;
import com.sony.imaging.app.timelapse.shooting.controller.TimelapseMonitorBrightnessController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.PlainCalendar;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class AngleShiftProgressLayout extends OptimizedImageLayoutBase implements NotificationListener {
    private static final int DONE = 2;
    private static final int ENC = 0;
    private static final int ENC_LAST_FRAME = 1;
    private ViewGroup mCurrentView;
    private Handler mHandler;
    private OptimizedImageView mOptImageView;
    private static final String TAG = AngleShiftProgressLayout.class.getName();
    private static PlayBackController pbCntl = null;
    private static OptimizedImage mOptImage = null;
    private static OptimizedImage[] mOptImageArray = {null, null};
    private static OptimizedImage mOptImageBeforeRotate = null;
    private static int BRIGHTNESS_PANEL_OFF = 2;
    private static int BRIGHTNESS_DIM = 1;
    private static int BRIGHTNESS_NORMAL = 0;
    private static int mCounter = 0;
    private static int mCounterPerFile = 0;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF = new int[0];
    private static final int[] IMAGE_ERR_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_singlepb_image_err};
    private ImageView mTestShot = null;
    private TextView mNumOfImages = null;
    private TextView mEstTime = null;
    private ProgressBar mProgressBar = null;
    private FooterGuide mFooterGuide = null;
    private boolean mSetOptimizedImage = false;
    public ContentResolver mResolver = null;
    private int totalTarget = 0;
    private int mCurrMonitorBrightness = 0;
    private AviExporter mAviExporter = null;
    private boolean mStoreThumbnail = true;
    private RotateImageFilter mRotateImageFilter = null;
    private boolean mCancelProcess = false;
    private boolean mFinished = false;
    private int mPreProcId = 0;
    private int mPostProcId = 0;
    private boolean mSkipFirstCalling = true;
    private boolean isMultiThread = false;
    private boolean isCanceledByError = false;
    private boolean isCanceledByMenu = false;
    private int origlistPosition = 0;
    private boolean isCreatingStarted = false;
    private boolean isClosed = false;
    View mImageErrView = null;
    private int mDisplayedEstTime = -1;
    private float mAverageTime = TimeLapseConstants.INVALID_APERTURE_VALUE;
    private long mPrevTime = 0;
    private boolean mRestart = false;
    private TimeLapseBO mTimelapseBo = null;
    private String[] TAGS = {AngleShiftConstants.TAG_AS_CANCEL_SAVING_BY_ERROR, AngleShiftConstants.TAG_AS_CANCEL_SAVING_BY_MENU, AngleShiftConstants.TAG_AS_CONTINUE_SAVING};

    static /* synthetic */ int access$2308(AngleShiftProgressLayout x0) {
        int i = x0.mPreProcId;
        x0.mPreProcId = i + 1;
        return i;
    }

    static /* synthetic */ int access$2372(AngleShiftProgressLayout x0, int x1) {
        int i = x0.mPreProcId & x1;
        x0.mPreProcId = i;
        return i;
    }

    static /* synthetic */ int access$2808(AngleShiftProgressLayout x0) {
        int i = x0.mPostProcId;
        x0.mPostProcId = i + 1;
        return i;
    }

    static /* synthetic */ int access$2872(AngleShiftProgressLayout x0, int x1) {
        int i = x0.mPostProcId & x1;
        x0.mPostProcId = i;
        return i;
    }

    static /* synthetic */ int access$908() {
        int i = mCounter;
        mCounter = i + 1;
        return i;
    }

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

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mTestShot = (ImageView) this.mCurrentView.findViewById(R.id.testshot);
        this.mTestShot.setVisibility(4);
        this.mPreProcId = 0;
        this.mPostProcId = 0;
        this.isCreatingStarted = false;
        if (this.mHandler == null) {
            this.mHandler = new MyHandler();
        }
        TLCommonUtil.getInstance().setMemoryMapConfiguration();
        this.mOptImageView = this.mCurrentView.findViewById(R.id.imageSingle);
        this.mOptImageView.setOnDisplayEventListener(new OptimizedImageView.onDisplayEventListener() { // from class: com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftProgressLayout.1
            public void onDisplay(int errCd) {
                AngleShiftProgressLayout.this.mSetOptimizedImage = true;
            }
        });
        this.mNumOfImages = (TextView) this.mCurrentView.findViewById(R.id.num_of_images);
        this.mEstTime = (TextView) this.mCurrentView.findViewById(R.id.est_time);
        this.mProgressBar = (ProgressBar) this.mCurrentView.findViewById(R.id.progressBar);
        this.totalTarget = AngleShiftSetting.getInstance().getTargetNumber();
        this.mProgressBar.setMax(this.totalTarget);
        this.mProgressBar.setProgress(0);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_preview);
        if (!TimelapseMonitorBrightnessController.getInstance().isSupported()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_EFFECT_SAVING_FOOTER_GUIDE2, R.string.STRID_FUNC_TIMELAPSE_EFFECT_SAVING_FOOTER_GUIDE2_SK));
        }
        this.mImageErrView = this.mCurrentView.findViewById(R.id.pb_parts_cmn_singlepb_image_err);
        this.mImageErrView.setVisibility(4);
        this.isCanceledByError = false;
        this.isCanceledByMenu = false;
        this.origlistPosition = ListViewLayout.listPosition;
        resetBrightness();
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mSetOptimizedImage = false;
        this.mCancelProcess = false;
        this.mFinished = false;
        this.mRestart = false;
        mOptImageArray[0] = null;
        mOptImageArray[1] = null;
        mCounter = 0;
        mCounterPerFile = 0;
        this.mStoreThumbnail = true;
        this.mTimelapseBo = null;
        this.isClosed = false;
        this.mRotateImageFilter = new RotateImageFilter();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
        this.mImageErrView.setVisibility(4);
        AngleShiftImageEditor.getInstance().prepare();
        pbCntl = PlayBackController.getInstance();
        this.mResolver = getActivity().getContentResolver();
        pbCntl = PlayBackController.getInstance();
        pbCntl.setContentResolver(this.mResolver);
        pbCntl.intializeCursorData(ListViewLayout.listPosition);
        pbCntl.moveToPosition(AngleShiftSetting.getInstance().getStartPosIndex());
        this.mDisplayedEstTime = (this.totalTarget * AngleShiftImageEditor.getInstance().getProcessingTime()) / 1000;
        updateNumOfImages();
        Handler mHandler2 = new Handler();
        Runnable mRun = new Runnable() { // from class: com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftProgressLayout.2
            @Override // java.lang.Runnable
            public void run() {
                AngleShiftProgressLayout.this.storeThumbnail();
                AngleShiftProgressLayout.this.isMultiThread = true;
                AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(0);
            }
        };
        mHandler2.post(mRun);
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
        if (!this.isClosed) {
            closingCreation();
        }
        int runStatus = RunStatus.getStatus();
        if (2 == runStatus) {
            AppLog.info(TAG, "Movie Creation is canceled by Power Off.");
            ListViewLayout.listPosition = -1;
        }
        resetBrightness();
        if (this.isCanceledByError || this.isCanceledByMenu) {
            ListViewLayout.listPosition = this.origlistPosition;
        }
        this.mTestShot = null;
        this.mNumOfImages = null;
        this.mEstTime = null;
        this.mProgressBar = null;
        this.mFooterGuide = null;
        this.mOptImageView = null;
        this.mImageErrView = null;
        this.mCurrentView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
        int runStatus = RunStatus.getStatus();
        if (!this.isCanceledByError && !this.isCanceledByMenu && 2 != runStatus) {
            openLayout("ID_ANGLESHIFTSAVINGDONWLAYOUT");
            TLCommonUtil.getInstance().setSavingDoneLayout(getLayout("ID_ANGLESHIFTSAVINGDONWLAYOUT"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closingCreation() {
        if (!this.isClosed) {
            this.isClosed = true;
            CameraNotificationManager.getInstance().removeNotificationListener(this);
            if (mCounter != 0) {
                saveTimeLapseBOData();
            }
            this.mTimelapseBo = null;
            if (this.mAviExporter != null) {
                try {
                    this.mAviExporter.close();
                } catch (Exception e) {
                    AppLog.error(TAG, e.toString());
                }
                this.mAviExporter = null;
            }
            AngleShiftImageEditor.getInstance().terminate();
            releaseOptimizedImage(mOptImage);
            mOptImage = null;
            releaseOptimizedImage(mOptImageBeforeRotate);
            mOptImageBeforeRotate = null;
            releaseOptimizedImage(mOptImageArray[0]);
            releaseOptimizedImage(mOptImageArray[1]);
            mOptImageArray[0] = null;
            mOptImageArray[1] = null;
            this.mResolver = null;
            if (pbCntl != null) {
                pbCntl.releaseAllAllocatedData();
                pbCntl = null;
            }
            if (this.mRotateImageFilter != null) {
                this.mRotateImageFilter.release();
                this.mRotateImageFilter = null;
            }
            DataBaseOperations.getInstance().refreshDatabase();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseOptimizedImage(OptimizedImage optImage) {
        if (optImage != null) {
            optImage.release();
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected void updateVPicDisplay(OptimizedImageView img, ContentsManager mgr) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public void updateOptimizedImageLayoutParam(OptimizedImageView imgView, ContentsManager mgr) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgView.getLayoutParams();
        params.width = 640;
        params.height = 360;
        params.topMargin = 60;
        params.leftMargin = 0;
        Point p = new Point(params.width >> 1, params.height >> 1);
        imgView.setPivot(p);
        imgView.setLayoutParams(params);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.as_progress;
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.isCreatingStarted && mCounter > 0) {
            this.mHandler.removeMessages(0);
            resetBrightness();
            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_CANCEL_SAVING);
            this.mRestart = true;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS2Key() {
        return -1;
    }

    @SuppressLint({"HandlerLeak"})
    /* loaded from: classes.dex */
    private class MyHandler extends Handler {
        private MyHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (!AngleShiftProgressLayout.this.mFinished) {
                switch (msg.what) {
                    case 0:
                        if (AngleShiftImageEditor.getInstance().getRemainingSpace() <= 0) {
                            AngleShiftProgressLayout.this.mHandler.removeCallbacksAndMessages(null);
                            AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(2);
                            return;
                        }
                        if (AngleShiftProgressLayout.this.mSetOptimizedImage) {
                            if (AngleShiftProgressLayout.this.isMultiThread) {
                                boolean isAllImageDecoded = AngleShiftProgressLayout.this.createOneFrame();
                                if (AngleShiftProgressLayout.this.mCancelProcess) {
                                    AngleShiftProgressLayout.this.mHandler.removeCallbacksAndMessages(null);
                                    AngleShiftProgressLayout.this.resetBrightness();
                                    CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_ILLEGAL_INPUT);
                                    return;
                                }
                                AngleShiftProgressLayout.this.mSetOptimizedImage = false;
                                AngleShiftProgressLayout.this.mProgressBar.setProgress(AngleShiftProgressLayout.mCounter);
                                AngleShiftProgressLayout.this.updateNumOfImages();
                                AngleShiftProgressLayout.this.updateImage(AngleShiftProgressLayout.mOptImage);
                                AngleShiftProgressLayout.this.isCreatingStarted = true;
                                if (isAllImageDecoded) {
                                    AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(1);
                                    return;
                                } else {
                                    AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(0);
                                    return;
                                }
                            }
                            AngleShiftProgressLayout.this.playOneByOne();
                            AngleShiftProgressLayout.this.isCreatingStarted = true;
                            if (AngleShiftProgressLayout.this.mCancelProcess) {
                                AngleShiftProgressLayout.this.mHandler.removeCallbacksAndMessages(null);
                                AngleShiftProgressLayout.this.resetBrightness();
                                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_ILLEGAL_INPUT);
                                return;
                            } else if (AngleShiftProgressLayout.pbCntl.moveToNext(1, AngleShiftSetting.getInstance().getEndPosIndex())) {
                                AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(0);
                                return;
                            } else {
                                AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(2);
                                return;
                            }
                        }
                        AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(0);
                        return;
                    case 1:
                        if (AngleShiftImageEditor.getInstance().getRemainingSpace() <= 0) {
                            AngleShiftProgressLayout.this.mHandler.removeCallbacksAndMessages(null);
                            AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(2);
                            return;
                        } else {
                            if (AngleShiftProgressLayout.this.mSetOptimizedImage) {
                                AngleShiftProgressLayout.this.createLastFrame();
                                AngleShiftProgressLayout.this.mSetOptimizedImage = false;
                                AngleShiftProgressLayout.this.mProgressBar.setProgress(AngleShiftProgressLayout.mCounter);
                                AngleShiftProgressLayout.this.updateNumOfImages();
                                AngleShiftProgressLayout.this.updateImage(AngleShiftProgressLayout.mOptImage);
                                AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(2);
                                return;
                            }
                            AngleShiftProgressLayout.this.mHandler.sendEmptyMessage(1);
                            return;
                        }
                    case 2:
                        AngleShiftProgressLayout.this.mFinished = true;
                        AngleShiftProgressLayout.this.closingCreation();
                        System.gc();
                        if (!AngleShiftProgressLayout.this.isCanceledByMenu) {
                            AngleShiftProgressLayout.this.resetBrightness();
                            AngleShiftProgressLayout.this.closeLayout();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean createOneFrame() {
        preProcThread preTh = new preProcThread();
        rotationThread rotateTh = new rotationThread();
        postProcThread postTh = new postProcThread();
        AppLog.error("ProcThread", "start");
        if (this.isCreatingStarted) {
            rotateTh.start();
            try {
                rotateTh.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        preTh.start();
        postTh.start();
        try {
            preTh.join();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        try {
            postTh.join();
        } catch (InterruptedException e3) {
            e3.printStackTrace();
        }
        return !pbCntl.moveToNext(1, AngleShiftSetting.getInstance().getEndPosIndex());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createLastFrame() {
        postProcThread postTh = new postProcThread();
        rotationThread rotateTh = new rotationThread();
        rotateTh.start();
        try {
            rotateTh.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        postTh.start();
        try {
            postTh.join();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class preProcThread extends Thread {
        private preProcThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (AngleShiftProgressLayout.mOptImageArray[AngleShiftProgressLayout.this.mPreProcId] == null) {
                OptimizedImage unused = AngleShiftProgressLayout.mOptImageBeforeRotate = AngleShiftProgressLayout.pbCntl.getPlayBackMainImage(AngleShiftSetting.getInstance().isRotatedImage());
                AngleShiftProgressLayout.this.mCancelProcess = AngleShiftProgressLayout.mOptImageBeforeRotate == null || AngleShiftProgressLayout.this.checkInputImage(AngleShiftProgressLayout.mOptImageBeforeRotate.getWidth(), AngleShiftProgressLayout.mOptImageBeforeRotate.getHeight());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class rotationThread extends Thread {
        private rotationThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (AngleShiftProgressLayout.mOptImageArray[AngleShiftProgressLayout.this.mPreProcId] == null) {
                int position = AngleShiftProgressLayout.pbCntl.getPosition() - AngleShiftSetting.getInstance().getStartPosIndex();
                if (!AngleShiftProgressLayout.this.mCancelProcess && AngleShiftSetting.getInstance().isRotatedImage()) {
                    OptimizedImage unused = AngleShiftProgressLayout.mOptImageBeforeRotate = AngleShiftSetting.getInstance().getRotatedImage(AngleShiftProgressLayout.this.mRotateImageFilter, AngleShiftProgressLayout.mOptImageBeforeRotate, true);
                }
                if (!AngleShiftProgressLayout.this.mCancelProcess) {
                    AngleShiftProgressLayout.mOptImageArray[AngleShiftProgressLayout.this.mPreProcId] = AngleShiftImageEditor.getInstance().getInputFrame(AngleShiftProgressLayout.mOptImageBeforeRotate, position);
                    AngleShiftProgressLayout.access$2308(AngleShiftProgressLayout.this);
                    AngleShiftProgressLayout.access$2372(AngleShiftProgressLayout.this, 1);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class postProcThread extends Thread {
        private postProcThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (AngleShiftProgressLayout.this.mSkipFirstCalling || AngleShiftProgressLayout.this.mCancelProcess || AngleShiftProgressLayout.mOptImageArray[AngleShiftProgressLayout.this.mPostProcId] == null) {
                AngleShiftProgressLayout.this.mSkipFirstCalling = false;
                return;
            }
            long start = System.currentTimeMillis();
            AngleShiftProgressLayout.this.releaseOptimizedImage(AngleShiftProgressLayout.mOptImage);
            OptimizedImage unused = AngleShiftProgressLayout.mOptImage = AngleShiftImageEditor.getInstance().getRCFrame(AngleShiftProgressLayout.mOptImageArray[AngleShiftProgressLayout.this.mPostProcId], AngleShiftProgressLayout.mCounter);
            long t0 = System.currentTimeMillis();
            if (AngleShiftProgressLayout.mOptImageArray[AngleShiftProgressLayout.this.mPostProcId] != null) {
                AngleShiftProgressLayout.mOptImageArray[AngleShiftProgressLayout.this.mPostProcId].release();
                AngleShiftProgressLayout.mOptImageArray[AngleShiftProgressLayout.this.mPostProcId] = null;
            }
            AngleShiftProgressLayout.access$908();
            AngleShiftProgressLayout.this.storeFrame_Thumbnail(AngleShiftProgressLayout.mOptImage);
            AngleShiftProgressLayout.access$2808(AngleShiftProgressLayout.this);
            AngleShiftProgressLayout.access$2872(AngleShiftProgressLayout.this, 1);
            long end = System.currentTimeMillis();
            AppLog.info(AngleShiftProgressLayout.TAG, "Performance SA: " + (t0 - start));
            AppLog.info(AngleShiftProgressLayout.TAG, "Performance postProcThread: " + (end - start));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void storeThumbnail() {
        int position = pbCntl.getPosition() - AngleShiftSetting.getInstance().getStartPosIndex();
        mOptImage = getOptImage(this.mRotateImageFilter, true);
        if (!this.mCancelProcess) {
            mOptImage = AngleShiftImageEditor.getInstance().getThubnailImage(mOptImage, position);
            if (mOptImage != null) {
                if (this.mTimelapseBo == null) {
                    createTimeLapseBOData();
                }
                if (this.mStoreThumbnail) {
                    createAviExporterOptionData(true);
                    if (!this.mAviExporter.storeThumbnail(mOptImage, true)) {
                        AppLog.info(TAG, "Failed storeThumbnail!");
                    }
                    this.mStoreThumbnail = false;
                }
            }
        }
    }

    private void checkNumberOfAviFile() {
        mCounterPerFile++;
        if (getFrameNumberPerAviFile() < mCounterPerFile) {
            mCounterPerFile = 0;
            this.mStoreThumbnail = true;
            try {
                this.mAviExporter.close();
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private int getFrameNumberPerAviFile() {
        if (!"framerate-30p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            return TLCommonUtil.FRAME_NUMBER_PER_24P_AVI;
        }
        return TLCommonUtil.FRAME_NUMBER_PER_30P_AVI;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playOneByOne() {
        AppLog.info("cursor position", "" + pbCntl.getPosition());
        int position = pbCntl.getPosition() - AngleShiftSetting.getInstance().getStartPosIndex();
        releaseOptimizedImage(mOptImage);
        long outTime = 0;
        long inTime = System.currentTimeMillis();
        mOptImage = getOptImage(this.mRotateImageFilter, true);
        long inTime2 = System.currentTimeMillis() - inTime;
        if (!this.mCancelProcess) {
            mOptImage = AngleShiftImageEditor.getInstance().getFrame(mOptImage, position);
            mCounter++;
            if (mOptImage != null) {
                long outTime2 = System.currentTimeMillis();
                storeFrame_Thumbnail(mOptImage);
                outTime = System.currentTimeMillis() - outTime2;
            }
            AppLog.error("profile media access", "IN: " + inTime2 + " OUT: " + outTime + " Total: " + (inTime2 + outTime));
            this.mSetOptimizedImage = false;
            this.mProgressBar.setProgress(position + 1);
            updateNumOfImages();
            this.mOptImageView.setOptimizedImage(mOptImage);
        }
    }

    private OptimizedImage getOptImage(RotateImageFilter filter, boolean isReleased) {
        OptimizedImage optImage = pbCntl.getPlayBackMainImage(isReleased);
        this.mCancelProcess = optImage == null || checkInputImage(optImage.getWidth(), optImage.getHeight());
        if (!this.mCancelProcess && AngleShiftSetting.getInstance().isRotatedImage()) {
            return AngleShiftSetting.getInstance().getRotatedImage(filter, optImage, true);
        }
        return optImage;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkInputImage(int width, int height) {
        return (width == AngleShiftSetting.getInstance().getAngleShiftFirstImageWidth() && height == AngleShiftSetting.getInstance().getAngleShiftFirstImageHeight()) ? false : true;
    }

    public void createMovie() {
        if (pbCntl.isDataExist() && pbCntl.getCount() > 0) {
            if (pbCntl.moveToNext(1, AngleShiftSetting.getInstance().getEndPosIndex())) {
                this.mHandler.sendEmptyMessage(0);
            } else {
                this.mHandler.sendEmptyMessage(2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNumOfImages() {
        String numOfImages = "" + mCounter + "/" + this.totalTarget;
        this.mNumOfImages.setText(numOfImages);
        int remainNum = this.totalTarget - mCounter;
        int estTime = (int) Math.ceil(remainNum * getAverageTime(mCounter));
        this.mDisplayedEstTime = Math.min(this.mDisplayedEstTime, estTime);
        if (mCounter > 5) {
            if (remainNum >= 800) {
                if (estTime >= this.mDisplayedEstTime + 80) {
                    this.mDisplayedEstTime = estTime;
                }
            } else if (remainNum >= 600) {
                if (estTime >= this.mDisplayedEstTime + 60) {
                    this.mDisplayedEstTime = estTime;
                }
            } else if (remainNum >= 400) {
                if (estTime >= this.mDisplayedEstTime + 40) {
                    this.mDisplayedEstTime = estTime;
                }
            } else if (estTime >= this.mDisplayedEstTime + 20) {
                this.mDisplayedEstTime = estTime;
            }
        }
        AppLog.info(TAG, "updateNumOfImages totalTarget :" + this.totalTarget);
        AppLog.info(TAG, "updateNumOfImages mCounter :" + mCounter);
        AppLog.info(TAG, "updateNumOfImages estTime :" + estTime);
        AppLog.info(TAG, "updateNumOfImages Displayed EstTime :" + this.mDisplayedEstTime);
        this.mEstTime.setText(TLCommonUtil.getInstance().getRecordingTime(this.mDisplayedEstTime));
    }

    private float getAverageTime(int count) {
        long currentTime = System.currentTimeMillis();
        if (count == 1) {
            this.mAverageTime = AngleShiftImageEditor.getInstance().getProcessingTime() / 1000.0f;
        } else if (!this.mRestart) {
            this.mAverageTime = (this.mAverageTime + (((float) (currentTime - this.mPrevTime)) / 1000.0f)) / 2.0f;
        } else {
            this.mRestart = false;
        }
        this.mPrevTime = currentTime;
        AppLog.info(TAG, "Performance Average Processing Time: " + this.mAverageTime);
        return this.mAverageTime;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 103) {
            if (DisplayModeObserver.getInstance().getActiveDevice() == 2) {
                return 1;
            }
            toggleBrightness();
            return 1;
        }
        if (event.getScanCode() == 589) {
            return -1;
        }
        int result = super.onKeyDown(keyCode, event);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetBrightness() {
        DisplayManager dispManager = new DisplayManager();
        if (TimelapseMonitorBrightnessController.getInstance().isSupportedDispOffMode()) {
            dispManager.switchDisplayOutputTo("DEVICE_ID_SETTING_RELEASE");
        } else if (TimelapseMonitorBrightnessController.getInstance().isSupportedSavingBatteryMode()) {
            dispManager.setSavingBatteryMode("SAVING_BATTERY_OFF");
        }
        dispManager.finish();
        this.mCurrMonitorBrightness = BRIGHTNESS_NORMAL;
    }

    private void toggleBrightness() {
        this.mCurrMonitorBrightness++;
        DisplayManager dispManager = new DisplayManager();
        if (TimelapseMonitorBrightnessController.getInstance().isSupportedDispOffMode()) {
            if (this.mCurrMonitorBrightness == BRIGHTNESS_PANEL_OFF) {
                dispManager.switchDisplayOutputTo("DEVICE_ID_NONE");
                dispManager.setSavingBatteryMode("SAVING_BATTERY_OFF");
            } else if (this.mCurrMonitorBrightness == BRIGHTNESS_DIM) {
                dispManager.setSavingBatteryMode("SAVING_BATTERY_ON");
            } else {
                this.mCurrMonitorBrightness = BRIGHTNESS_NORMAL;
                dispManager.switchDisplayOutputTo("DEVICE_ID_SETTING_RELEASE");
            }
        } else if (TimelapseMonitorBrightnessController.getInstance().isSupportedSavingBatteryMode()) {
            if (this.mCurrMonitorBrightness == BRIGHTNESS_DIM) {
                dispManager.setSavingBatteryMode("SAVING_BATTERY_ON");
            } else {
                this.mCurrMonitorBrightness = BRIGHTNESS_NORMAL;
                dispManager.setSavingBatteryMode("SAVING_BATTERY_OFF");
            }
        }
        dispManager.finish();
    }

    public void createTimeLapseBOData() {
        TimeLapseBO timelapseBo = new TimeLapseBO();
        TLCommonUtil cUtil = TLCommonUtil.getInstance();
        cUtil.pinCalendar();
        PlainCalendar calendar = cUtil.getPinedCalender();
        timelapseBo.setStartDate(cUtil.getDateFormat(calendar));
        timelapseBo.setStartTime(cUtil.getTimeFormat(calendar));
        timelapseBo.setStartUtcDateTime(cUtil.getUTCTime());
        timelapseBo.setThemeName(pbCntl.getmTimeLapseBO().getThemeName());
        int frameRate = 24;
        if ("framerate-24p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            timelapseBo.setMovieMode(TimeLapseConstants.TIME_LAPSE_SHOOT_MODE_MPEG24);
        } else {
            timelapseBo.setMovieMode(TimeLapseConstants.TIME_LAPSE_SHOOT_MODE_MPEG30);
            frameRate = 30;
        }
        timelapseBo.setAspectRatio(1);
        int width = 1920;
        int height = 1080;
        if (AngleShiftMovieController.MOVIE_1280x720.equalsIgnoreCase(AngleShiftMovieController.getInstance().getValue())) {
            width = 1280;
            height = 720;
        }
        String filePath = cUtil.getAviFilePathName(calendar);
        cUtil.createFolder(calendar);
        timelapseBo.setStartFullPathFileName(filePath);
        timelapseBo.setWidth(width);
        timelapseBo.setHeight(height);
        timelapseBo.setFps(frameRate);
        timelapseBo.setShootingMode("MOVIE");
        this.mTimelapseBo = timelapseBo;
    }

    public void saveTimeLapseBOData() {
        DataBaseOperations dataBaseOperations = DataBaseOperations.getInstance();
        TLCommonUtil cUtil = TLCommonUtil.getInstance();
        if (this.mTimelapseBo != null) {
            try {
            } catch (Exception e) {
                AppLog.error(TAG, "Database opeartion failed in AngleShift Addon.");
            }
            if (DatabaseUtil.DbResult.DB_ERROR == dataBaseOperations.importDatabase()) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
                return;
            }
            this.mTimelapseBo.setEndUtcDateTime(cUtil.getUTCTime());
            this.mTimelapseBo.setShootingNumber(mCounter);
            dataBaseOperations.saveJpegGroup(this.mTimelapseBo);
            DatabaseUtil.DbResult result = dataBaseOperations.exportDatabase();
            AppLog.info(TAG, "saveTimeLapseBOData" + result);
            if (DatabaseUtil.DbResult.SUCCEEDED == result) {
                dataBaseOperations.getTimeLapseBOList().add(this.mTimelapseBo);
            } else if (DatabaseUtil.DbResult.DB_ERROR == result) {
                DataBaseAdapter.getInstance().setDBCorruptStatus(true);
            } else if (DatabaseUtil.DbResult.SUCCEEDED != result) {
                dataBaseOperations.importDatabase();
            }
            ListViewLayout.listPosition = dataBaseOperations.getTimeLapseBOList().size() - 1;
            this.mTimelapseBo = null;
        }
    }

    private void createAviExporterOptionData(boolean isFirstPicture) {
        AviExporter.Options options = new AviExporter.Options();
        TLCommonUtil cUtil = TLCommonUtil.getInstance();
        options.width = 1920;
        options.height = 1080;
        if (AngleShiftMovieController.MOVIE_1280x720.equalsIgnoreCase(AngleShiftMovieController.getInstance().getValue())) {
            options.width = 1280;
            options.height = 720;
        }
        if ("framerate-24p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            options.fps = 24;
        } else {
            options.fps = 30;
        }
        options.isAdobeRGB = false;
        if (isFirstPicture) {
            options.fullPathFileName = this.mTimelapseBo.getStartFullPathFileName();
        } else {
            String filePath = cUtil.getAviFilePathNameSequel();
            this.mTimelapseBo.setSequelFullPathFileName(filePath);
            options.fullPathFileName = this.mTimelapseBo.getSequelFullPathFileName();
        }
        options.libpath = TimeLapseConstants.LIB_PATH;
        if (this.mAviExporter == null) {
            this.mAviExporter = new AviExporter();
        }
        try {
            this.mAviExporter.open(options);
        } catch (Exception e) {
            AppLog.error(TAG, e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void storeFrame_Thumbnail(OptimizedImage inImage) {
        checkNumberOfAviFile();
        if (this.mStoreThumbnail) {
            createAviExporterOptionData(false);
            if (!this.mAviExporter.storeThumbnail(inImage, false)) {
                AppLog.info(TAG, "Failed storeThumbnail!");
            }
            this.mStoreThumbnail = false;
        }
        try {
            if (!this.mAviExporter.storeFrame(inImage, false)) {
                AppLog.info(TAG, "Failed StoreFrame!(try)");
            }
        } catch (Exception e) {
            AppLog.info(TAG, "Failed StoreFrame!(catch)");
        }
    }

    private void cancelSavingByError() {
        closeLayout();
        openLayout("NORMAL_PB");
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT);
        this.isCanceledByError = true;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFocusModeDial() {
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

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CANCEL_SAVING_BY_MENU)) {
            cancelSaving();
            if (this.mHandler != null) {
                this.mHandler.sendEmptyMessage(2);
                return;
            }
            return;
        }
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CONTINUE_SAVING)) {
            if (this.mHandler != null) {
                this.mHandler.sendEmptyMessage(0);
            }
        } else if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CANCEL_SAVING_BY_ERROR)) {
            cancelSavingByError();
        }
    }

    private void cancelSaving() {
        this.mFinished = true;
        this.isCanceledByMenu = true;
        closeLayout();
        openLayout("NORMAL_PB");
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT);
    }
}
