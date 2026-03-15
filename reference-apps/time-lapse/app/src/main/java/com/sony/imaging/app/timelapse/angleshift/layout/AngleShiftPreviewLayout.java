package com.sony.imaging.app.timelapse.angleshift.layout;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
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
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftFrameRateController;
import com.sony.imaging.app.timelapse.caution.TimelapseInfo;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.timelapse.playback.layout.ListViewLayout;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class AngleShiftPreviewLayout extends OptimizedImageLayoutBase implements NotificationListener {
    private static final int PAUSE = 1;
    private static final int PLAY = 0;
    private static final int SHOW_CURRENT = 3;
    private static final int SHOW_LAST = 2;
    private ViewGroup mCurrentView;
    private Handler mHandler;
    private OptimizedImageView mOptImageView;
    private static PlayBackController pbCntl = null;
    private static OptimizedImage mOptImage = null;
    private static OptimizedImage[] mOptImageArray = {null, null};
    private static OptimizedImage mOptImageBeforeRotate = null;
    private static int PREVIEW_IDLE = 0;
    private static int PREVIEW_PLAY = 1;
    private static int PREVIEW_PAUSE = 2;
    private static int PREVIEW_DONE = 3;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF = new int[0];
    private static final int[] IMAGE_ERR_LAYOUT_RESOURCE_ID = {R.id.pb_parts_cmn_singlepb_image_err};
    private ImageView mTestShot = null;
    private ImageView mPlayBack = null;
    private ImageView mPlayCenter = null;
    private TextView mNumOfImages = null;
    private TextView mCurrentTime = null;
    private FooterGuide mFooterGuide = null;
    private boolean mSetOptimizedImage = false;
    public ContentResolver mResolver = null;
    private int previewStatus = PREVIEW_IDLE;
    private boolean mCancelProcess = false;
    private int totalTarget = 0;
    private int[] mPlayBackSpeed = null;
    private int mSpeedIndex = 0;
    private RotateImageFilter mRotateImageFilter = null;
    private int mPreProcId = 0;
    private int mPostProcId = 0;
    private boolean mSkipFirstCalling = true;
    private boolean isMultiThread = false;
    private int mCounter = 0;
    private int mCurrentPosition = 0;
    private boolean isCreatingStarted = false;
    private String[] TAGS = {AngleShiftConstants.TAG_AS_CANCEL_PREVIEW};

    static /* synthetic */ int access$1708(AngleShiftPreviewLayout x0) {
        int i = x0.mPreProcId;
        x0.mPreProcId = i + 1;
        return i;
    }

    static /* synthetic */ int access$1772(AngleShiftPreviewLayout x0, int x1) {
        int i = x0.mPreProcId & x1;
        x0.mPreProcId = i;
        return i;
    }

    static /* synthetic */ int access$1808(AngleShiftPreviewLayout x0) {
        int i = x0.mPostProcId;
        x0.mPostProcId = i + 1;
        return i;
    }

    static /* synthetic */ int access$1872(AngleShiftPreviewLayout x0, int x1) {
        int i = x0.mPostProcId & x1;
        x0.mPostProcId = i;
        return i;
    }

    static /* synthetic */ int access$2012(AngleShiftPreviewLayout x0, int x1) {
        int i = x0.mCounter + x1;
        x0.mCounter = i;
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

    private void initializeView() {
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.previewStatus = PREVIEW_IDLE;
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mTestShot = (ImageView) this.mCurrentView.findViewById(R.id.testshot);
        this.mTestShot.setVisibility(4);
        this.mPreProcId = 0;
        this.mPostProcId = 0;
        this.isCreatingStarted = false;
        initializeView();
        if (this.mHandler == null) {
            this.mHandler = new MyHandler();
        }
        TLCommonUtil.getInstance().setMemoryMapConfiguration();
        this.mOptImageView = this.mCurrentView.findViewById(R.id.imageSingle);
        this.mOptImageView.setOnDisplayEventListener(new OptimizedImageView.onDisplayEventListener() { // from class: com.sony.imaging.app.timelapse.angleshift.layout.AngleShiftPreviewLayout.1
            public void onDisplay(int errCd) {
                AngleShiftPreviewLayout.this.mSetOptimizedImage = true;
            }
        });
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_preview);
        this.mFooterGuide.setVisibility(4);
        this.mPlayBack = (ImageView) this.mCurrentView.findViewById(R.id.playback_speed);
        this.mPlayBack.setVisibility(4);
        this.mPlayCenter = (ImageView) this.mCurrentView.findViewById(R.id.playcenter);
        this.mPlayCenter.setVisibility(0);
        this.mNumOfImages = (TextView) this.mCurrentView.findViewById(R.id.num_of_images);
        this.mCurrentTime = (TextView) this.mCurrentView.findViewById(R.id.playback_value);
        this.mCancelProcess = false;
        this.mPlayBackSpeed = getPlayBackSpeed();
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.mSpeedIndex = 0;
        this.mSetOptimizedImage = false;
        this.mCounter = 0;
        mOptImageArray[0] = null;
        mOptImageArray[1] = null;
        this.mRotateImageFilter = new RotateImageFilter();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
        AngleShiftImageEditor.getInstance().prepare();
        pbCntl = PlayBackController.getInstance();
        this.mResolver = getActivity().getContentResolver();
        pbCntl = PlayBackController.getInstance();
        pbCntl.setContentResolver(this.mResolver);
        pbCntl.intializeCursorData(ListViewLayout.listPosition);
        pbCntl.moveToPosition(AngleShiftSetting.getInstance().getStartPosIndex());
        this.totalTarget = AngleShiftSetting.getInstance().getTargetNumber();
        if (getPlayBackSpeed()[0] > this.totalTarget) {
            this.isMultiThread = false;
        } else {
            this.isMultiThread = true;
        }
        this.isMultiThread = false;
        if (this.isMultiThread) {
            this.mSkipFirstCalling = true;
            int msg = createFirstFrame();
            if (!this.mCancelProcess) {
                this.mHandler.sendEmptyMessage(msg);
                return;
            } else {
                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_ILLEGAL_INPUT_PREVIEW);
                return;
            }
        }
        playOneByOne();
        if (!this.mCancelProcess) {
            refresh();
            updateOptimizedImage(mOptImage);
            showImage(this.mCancelProcess ? false : true);
            return;
        }
        CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_ILLEGAL_INPUT_PREVIEW);
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mTestShot = null;
        this.mPlayBack.setBackgroundResource(0);
        this.mPlayBack = null;
        this.mPlayCenter = null;
        this.mNumOfImages = null;
        this.mCurrentTime = null;
        this.mFooterGuide = null;
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler = null;
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
        this.mOptImageView = null;
        this.mResolver = null;
        if (pbCntl != null) {
            pbCntl.releaseAllAllocatedData();
            pbCntl = null;
        }
        if (this.mRotateImageFilter != null) {
            this.mRotateImageFilter.release();
            this.mRotateImageFilter = null;
        }
        this.mPlayBackSpeed = null;
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        this.mCurrentView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
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
        return R.layout.as_preview;
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
    public int pushedSK2Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        cancelPreview();
        return 1;
    }

    private void cancelPreview() {
        closeLayout();
        openLayout("NORMAL_PB");
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_CONFIRM_LAYOUT);
    }

    @SuppressLint({"HandlerLeak"})
    /* loaded from: classes.dex */
    private class MyHandler extends Handler {
        private MyHandler() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0009. Please report as an issue. */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (AngleShiftPreviewLayout.this.mSetOptimizedImage) {
                        if (AngleShiftPreviewLayout.this.isMultiThread) {
                            boolean isAllImageDecoded = AngleShiftPreviewLayout.this.createOneFrame();
                            if (AngleShiftPreviewLayout.this.mCancelProcess) {
                                AngleShiftPreviewLayout.this.mHandler.removeCallbacksAndMessages(null);
                                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_ILLEGAL_INPUT_PREVIEW);
                                return;
                            }
                            AngleShiftPreviewLayout.this.mSetOptimizedImage = false;
                            AngleShiftPreviewLayout.this.updateNumOfImages();
                            AngleShiftPreviewLayout.this.updateImage(AngleShiftPreviewLayout.mOptImage);
                            if (isAllImageDecoded) {
                                AngleShiftPreviewLayout.this.mHandler.sendEmptyMessage(2);
                            } else {
                                AngleShiftPreviewLayout.this.mHandler.sendEmptyMessage(0);
                            }
                        } else {
                            AngleShiftPreviewLayout.this.playOneByOne();
                            AngleShiftPreviewLayout.this.isCreatingStarted = true;
                            if (AngleShiftPreviewLayout.this.mCancelProcess) {
                                AngleShiftPreviewLayout.this.mHandler.removeCallbacksAndMessages(null);
                                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_ILLEGAL_INPUT_PREVIEW);
                                return;
                            } else if (AngleShiftPreviewLayout.pbCntl.moveToNext(AngleShiftPreviewLayout.this.mPlayBackSpeed[AngleShiftPreviewLayout.this.mSpeedIndex], AngleShiftSetting.getInstance().getEndPosIndex())) {
                                AngleShiftPreviewLayout.this.mHandler.sendEmptyMessage(0);
                            } else {
                                AngleShiftPreviewLayout.this.mHandler.sendEmptyMessage(2);
                            }
                        }
                    } else {
                        AngleShiftPreviewLayout.this.mHandler.sendEmptyMessage(0);
                    }
                    AngleShiftPreviewLayout.this.refresh();
                    return;
                case 1:
                    AngleShiftPreviewLayout.this.mHandler.removeMessages(0);
                    AngleShiftPreviewLayout.this.mHandler.sendEmptyMessage(3);
                    AngleShiftPreviewLayout.this.refresh();
                    return;
                case 2:
                    if (!AngleShiftPreviewLayout.this.mSetOptimizedImage) {
                        AngleShiftPreviewLayout.this.mHandler.sendEmptyMessage(2);
                    } else {
                        AngleShiftPreviewLayout.this.previewStatus = AngleShiftPreviewLayout.PREVIEW_DONE;
                        if (AngleShiftPreviewLayout.this.isMultiThread) {
                            AngleShiftPreviewLayout.this.createLastFrame();
                            AngleShiftPreviewLayout.this.mSetOptimizedImage = false;
                            AngleShiftPreviewLayout.this.updateNumOfImages();
                            AngleShiftPreviewLayout.this.updateImage(AngleShiftPreviewLayout.mOptImage);
                            AngleShiftPreviewLayout.this.isCreatingStarted = false;
                        } else {
                            AngleShiftPreviewLayout.pbCntl.moveToPosition(AngleShiftSetting.getInstance().getEndPosIndex());
                            AngleShiftPreviewLayout.this.playOneByOne();
                            AngleShiftPreviewLayout.this.isCreatingStarted = false;
                            if (AngleShiftPreviewLayout.this.mCancelProcess) {
                                AngleShiftPreviewLayout.this.mHandler.removeCallbacksAndMessages(null);
                                CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_ILLEGAL_INPUT_PREVIEW);
                                return;
                            }
                        }
                    }
                    AngleShiftPreviewLayout.this.refresh();
                    return;
                case 3:
                    if (AngleShiftPreviewLayout.this.mSetOptimizedImage) {
                        AngleShiftPreviewLayout.this.mHandler.removeMessages(3);
                        if (AngleShiftPreviewLayout.this.isMultiThread) {
                            AngleShiftPreviewLayout.this.mSkipFirstCalling = true;
                            AngleShiftPreviewLayout.this.mPreProcId = 0;
                            AngleShiftPreviewLayout.this.mPostProcId = 0;
                            for (int i = 0; i < 2; i++) {
                                if (AngleShiftPreviewLayout.mOptImageArray[i] != null) {
                                    AngleShiftPreviewLayout.mOptImageArray[i].release();
                                    AngleShiftPreviewLayout.mOptImageArray[i] = null;
                                }
                            }
                        }
                        AngleShiftPreviewLayout.this.mCounter = AngleShiftPreviewLayout.pbCntl.getPosition() - AngleShiftSetting.getInstance().getStartPosIndex();
                        AngleShiftPreviewLayout.this.playOneByOne();
                        if (AngleShiftPreviewLayout.this.mCancelProcess) {
                            AngleShiftPreviewLayout.this.mHandler.removeCallbacksAndMessages(null);
                            CautionUtilityClass.getInstance().requestTrigger(TimelapseInfo.CAUTION_ID_DLAPP_AS_ILLEGAL_INPUT_PREVIEW);
                            return;
                        }
                    } else {
                        AngleShiftPreviewLayout.this.mHandler.sendEmptyMessage(3);
                    }
                    AngleShiftPreviewLayout.this.refresh();
                    return;
                default:
                    AngleShiftPreviewLayout.this.refresh();
                    return;
            }
        }
    }

    private int createFirstFrame() {
        boolean isAllImageDecoded = createOneFrame();
        if (isAllImageDecoded) {
            createLastFrame();
            this.mSetOptimizedImage = false;
            updateNumOfImages();
            updateImage(mOptImage);
            return -1;
        }
        boolean isAllImageDecoded2 = createOneFrame();
        this.mSetOptimizedImage = false;
        updateNumOfImages();
        updateImage(mOptImage);
        if (!isAllImageDecoded2) {
            return -1;
        }
        return 2;
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
        boolean isAllImageDecoded = pbCntl.getPosition() == AngleShiftSetting.getInstance().getEndPosIndex();
        if (!isAllImageDecoded) {
            pbCntl.moveToNext(this.mPlayBackSpeed[this.mSpeedIndex], AngleShiftSetting.getInstance().getEndPosIndex());
        }
        return isAllImageDecoded;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createLastFrame() {
        rotationThread rotateTh = new rotationThread();
        rotateTh.start();
        try {
            rotateTh.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        postProcThread postTh = new postProcThread();
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
            boolean z = true;
            if (AngleShiftPreviewLayout.mOptImageArray[AngleShiftPreviewLayout.this.mPreProcId] == null) {
                OptimizedImage unused = AngleShiftPreviewLayout.mOptImageBeforeRotate = AngleShiftPreviewLayout.pbCntl.getPlayBackMainImage(AngleShiftSetting.getInstance().isRotatedImage());
                AngleShiftPreviewLayout.this.isCreatingStarted = true;
                AngleShiftPreviewLayout angleShiftPreviewLayout = AngleShiftPreviewLayout.this;
                if (AngleShiftPreviewLayout.mOptImageBeforeRotate != null && !AngleShiftPreviewLayout.this.checkInputImage(AngleShiftPreviewLayout.mOptImageBeforeRotate.getWidth(), AngleShiftPreviewLayout.mOptImageBeforeRotate.getHeight())) {
                    z = false;
                }
                angleShiftPreviewLayout.mCancelProcess = z;
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
            if (AngleShiftPreviewLayout.mOptImageArray[AngleShiftPreviewLayout.this.mPreProcId] == null) {
                int position = AngleShiftPreviewLayout.pbCntl.getPosition() - AngleShiftSetting.getInstance().getStartPosIndex();
                if (!AngleShiftPreviewLayout.this.mCancelProcess && AngleShiftSetting.getInstance().isRotatedImage()) {
                    OptimizedImage unused = AngleShiftPreviewLayout.mOptImageBeforeRotate = AngleShiftSetting.getInstance().getRotatedImage(AngleShiftPreviewLayout.this.mRotateImageFilter, AngleShiftPreviewLayout.mOptImageBeforeRotate, true);
                }
                if (!AngleShiftPreviewLayout.this.mCancelProcess) {
                    AngleShiftPreviewLayout.mOptImageArray[AngleShiftPreviewLayout.this.mPreProcId] = AngleShiftImageEditor.getInstance().getInputFrame(AngleShiftPreviewLayout.mOptImageBeforeRotate, position);
                    AngleShiftPreviewLayout.access$1708(AngleShiftPreviewLayout.this);
                    AngleShiftPreviewLayout.access$1772(AngleShiftPreviewLayout.this, 1);
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
            if (AngleShiftPreviewLayout.this.mSkipFirstCalling || AngleShiftPreviewLayout.this.mCancelProcess || AngleShiftPreviewLayout.mOptImageArray[AngleShiftPreviewLayout.this.mPostProcId] == null) {
                AngleShiftPreviewLayout.this.mSkipFirstCalling = false;
                return;
            }
            AngleShiftPreviewLayout.this.releaseOptimizedImage(AngleShiftPreviewLayout.mOptImage);
            OptimizedImage unused = AngleShiftPreviewLayout.mOptImage = AngleShiftImageEditor.getInstance().getRCFrame(AngleShiftPreviewLayout.mOptImageArray[AngleShiftPreviewLayout.this.mPostProcId], AngleShiftPreviewLayout.this.mCounter);
            if (AngleShiftPreviewLayout.mOptImageArray[AngleShiftPreviewLayout.this.mPostProcId] != null) {
                AngleShiftPreviewLayout.mOptImageArray[AngleShiftPreviewLayout.this.mPostProcId].release();
                AngleShiftPreviewLayout.mOptImageArray[AngleShiftPreviewLayout.this.mPostProcId] = null;
            }
            AngleShiftPreviewLayout.this.mCurrentPosition = AngleShiftPreviewLayout.this.mCounter;
            AngleShiftPreviewLayout.access$2012(AngleShiftPreviewLayout.this, AngleShiftPreviewLayout.this.mPlayBackSpeed[AngleShiftPreviewLayout.this.mSpeedIndex]);
            if (AngleShiftPreviewLayout.this.mCounter >= AngleShiftSetting.getInstance().getTargetNumber()) {
                AngleShiftPreviewLayout.this.mCounter = AngleShiftSetting.getInstance().getTargetNumber() - 1;
            }
            AngleShiftPreviewLayout.access$1808(AngleShiftPreviewLayout.this);
            AngleShiftPreviewLayout.access$1872(AngleShiftPreviewLayout.this, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playOneByOne() {
        AppLog.info("cursor position", "" + pbCntl.getPosition());
        int position = pbCntl.getPosition() - AngleShiftSetting.getInstance().getStartPosIndex();
        this.mCurrentPosition = position;
        releaseOptimizedImage(mOptImage);
        mOptImage = getOptImage(this.mRotateImageFilter, true);
        if (!this.mCancelProcess) {
            mOptImage = AngleShiftImageEditor.getInstance().getFrame(mOptImage, position);
            this.mSetOptimizedImage = false;
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

    public void startPlayback() {
        if (pbCntl.isDataExist() && pbCntl.getCount() > 0) {
            releaseOptimizedImage(mOptImage);
            this.mSpeedIndex = 0;
            updatePlaySpeedIcon();
            this.mPlayBack.setVisibility(0);
            this.mHandler.sendEmptyMessage(0);
        }
    }

    public void cancelPlayback() {
        this.mHandler.sendEmptyMessage(1);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (!pbCntl.isDataExist() || pbCntl.getCount() <= 0) {
            return -1;
        }
        handleEnterButton();
        return 1;
    }

    private void handleEnterButton() {
        if (PREVIEW_IDLE == this.previewStatus) {
            this.mPlayCenter.setVisibility(4);
            startPlayback();
            this.previewStatus = PREVIEW_PLAY;
        } else if (PREVIEW_PLAY == this.previewStatus) {
            cancelPlayback();
            this.previewStatus = PREVIEW_PAUSE;
        } else if (PREVIEW_PAUSE == this.previewStatus) {
            startPlayback();
            this.previewStatus = PREVIEW_PLAY;
        } else if (PREVIEW_DONE == this.previewStatus) {
            this.mCounter = 0;
            pbCntl.moveToPosition(AngleShiftSetting.getInstance().getStartPosIndex());
            startPlayback();
            this.previewStatus = PREVIEW_PLAY;
        }
    }

    private void setDefaultFooterGuide() {
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_PREVIEW_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_PREVIEW_FOOTER_GUIDE_SK));
    }

    private void setPreiviewFooterGuide() {
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_TIMELAPSE_EFFECT_PREVIE_PLAY_FOOTER_GUIDE, R.string.STRID_FUNC_TIMELAPSE_EFFECT_PREVIE_PLAY_FOOTER_GUIDE_SK));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNumOfImages() {
        String numOfImages = "" + (this.mCurrentPosition + 1) + "/" + this.totalTarget;
        this.mNumOfImages.setText(numOfImages);
        this.mCurrentTime.setText(TLCommonUtil.getInstance().getRecordingTime(getCurrentTime(this.mCurrentPosition + 1)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        if (PREVIEW_IDLE == this.previewStatus) {
            this.mPlayBack.setVisibility(4);
            setDefaultFooterGuide();
        } else if (PREVIEW_PLAY == this.previewStatus) {
            this.mPlayBack.setVisibility(0);
            updatePlaySpeedIcon();
            setPreiviewFooterGuide();
        } else if (PREVIEW_PAUSE == this.previewStatus) {
            this.mPlayBack.setVisibility(0);
            updatePlaySpeedIcon();
            setDefaultFooterGuide();
        } else if (PREVIEW_DONE == this.previewStatus) {
            this.mPlayBack.setVisibility(4);
            setDefaultFooterGuide();
        }
        updateNumOfImages();
    }

    private void updatePlaySpeedIcon() {
        int resId = R.drawable.p_16_dd_parts_tm_playstatus_001;
        if (PREVIEW_PAUSE == this.previewStatus) {
            resId = R.drawable.p_16_dd_parts_tm_playstatus_002;
        } else if (PREVIEW_PLAY == this.previewStatus) {
            if (this.mSpeedIndex == 0) {
                resId = R.drawable.p_16_dd_parts_tm_playstatus_001;
            } else if (this.mSpeedIndex == 1) {
                resId = R.drawable.p_16_dd_parts_tm_playstatus_003;
            } else if (this.mSpeedIndex == 2) {
                resId = R.drawable.p_16_dd_parts_tm_playstatus_004;
            } else if (this.mSpeedIndex == 3) {
                resId = R.drawable.p_16_dd_parts_tm_playstatus_005;
            } else if (this.mSpeedIndex == 4) {
                resId = R.drawable.p_16_dd_parts_tm_playstatus_006;
            }
        }
        this.mPlayBack.setBackgroundResource(resId);
    }

    private int getCurrentTime(int frameNum) {
        if ("framerate-24p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            int currentTime = frameNum / 24;
            return currentTime;
        }
        int currentTime2 = frameNum / 30;
        return currentTime2;
    }

    private int[] getPlayBackSpeed() {
        if ("framerate-24p".equalsIgnoreCase(AngleShiftFrameRateController.getInstance().getValue())) {
            int[] playbackSpeed = AngleShiftConstants.PLAYBACK_SPEED_24P;
            return playbackSpeed;
        }
        int[] playbackSpeed2 = AngleShiftConstants.PLAYBACK_SPEED_30P;
        return playbackSpeed2;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        if (PREVIEW_IDLE == this.previewStatus && this.mCurrentPosition == 0) {
            pbCntl.moveToPosition(AngleShiftSetting.getInstance().getStartPosIndex());
        }
        if (PREVIEW_PLAY == this.previewStatus) {
            this.mSpeedIndex++;
            if (this.mSpeedIndex < this.mPlayBackSpeed.length) {
                return 1;
            }
            this.mSpeedIndex = this.mPlayBackSpeed.length - 1;
            return 1;
        }
        if (pbCntl.isDataExist() && pbCntl.getCount() > 0) {
            pbCntl.moveToNext(this.mPlayBackSpeed[0], AngleShiftSetting.getInstance().getEndPosIndex());
            this.previewStatus = PREVIEW_PAUSE;
            this.mPlayCenter.setVisibility(4);
            this.mHandler.sendEmptyMessage(3);
            if (pbCntl.getPosition() != AngleShiftSetting.getInstance().getEndPosIndex()) {
                return 1;
            }
            this.previewStatus = PREVIEW_DONE;
            this.mPlayBack.setVisibility(4);
            return 1;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        if (PREVIEW_IDLE == this.previewStatus && this.mCurrentPosition == 0) {
            pbCntl.moveToPosition(AngleShiftSetting.getInstance().getStartPosIndex());
        }
        if (PREVIEW_PLAY == this.previewStatus) {
            this.mSpeedIndex--;
            if (this.mSpeedIndex >= 0) {
                return 1;
            }
            this.mSpeedIndex = 0;
            return 1;
        }
        if (pbCntl.isDataExist() && pbCntl.getCount() > 0) {
            pbCntl.moveToPrevious(this.mPlayBackSpeed[0], AngleShiftSetting.getInstance().getStartPosIndex());
            this.previewStatus = PREVIEW_PAUSE;
            this.mPlayCenter.setVisibility(4);
            this.mHandler.sendEmptyMessage(3);
            if (pbCntl.getPosition() != AngleShiftSetting.getInstance().getStartPosIndex()) {
                return 1;
            }
            this.previewStatus = PREVIEW_IDLE;
            this.mPlayBack.setVisibility(4);
            return 1;
        }
        return -1;
    }

    private int turnDialToLeft() {
        if (PREVIEW_IDLE == this.previewStatus && this.mCurrentPosition == 0) {
            pbCntl.moveToPosition(AngleShiftSetting.getInstance().getStartPosIndex());
        }
        if (!pbCntl.isDataExist() || pbCntl.getCount() <= 0 || PREVIEW_PLAY == this.previewStatus) {
            return -1;
        }
        if (!pbCntl.moveToPrevious(1, AngleShiftSetting.getInstance().getStartPosIndex())) {
            return 1;
        }
        this.previewStatus = PREVIEW_PAUSE;
        this.mPlayCenter.setVisibility(4);
        this.mHandler.sendEmptyMessage(3);
        if (pbCntl.getPosition() != AngleShiftSetting.getInstance().getStartPosIndex()) {
            return 1;
        }
        this.previewStatus = PREVIEW_IDLE;
        this.mPlayBack.setVisibility(4);
        return 1;
    }

    private int turnDialToRight() {
        if (PREVIEW_IDLE == this.previewStatus && this.mCurrentPosition == 0) {
            pbCntl.moveToPosition(AngleShiftSetting.getInstance().getStartPosIndex());
        }
        if (!pbCntl.isDataExist() || pbCntl.getCount() <= 0 || PREVIEW_PLAY == this.previewStatus) {
            return -1;
        }
        if (!pbCntl.moveToNext(1, AngleShiftSetting.getInstance().getEndPosIndex())) {
            return 1;
        }
        this.previewStatus = PREVIEW_PAUSE;
        this.mPlayCenter.setVisibility(4);
        this.mHandler.sendEmptyMessage(3);
        if (pbCntl.getPosition() != AngleShiftSetting.getInstance().getEndPosIndex()) {
            return 1;
        }
        this.previewStatus = PREVIEW_DONE;
        this.mPlayBack.setVisibility(4);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToRight() {
        return turnDialToRight();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToLeft() {
        return turnDialToLeft();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return turnDialToLeft();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return turnDialToRight();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToLeft(KeyEvent event) {
        return turnDialToLeft();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial1TurnedToRight(KeyEvent event) {
        return turnDialToRight();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToLeft(KeyEvent event) {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial2TurnedToRight(KeyEvent event) {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToLeft(KeyEvent event) {
        return turnDialToLeft();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToRight(KeyEvent event) {
        return turnDialToRight();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToLeft() {
        return turnedShuttleToLeft();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial1ToRight() {
        return turnedShuttleToRight();
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 589) {
            return -1;
        }
        int result = super.onKeyDown(keyCode, event);
        return result;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_CANCEL_PREVIEW)) {
            cancelPreview();
        }
    }
}
