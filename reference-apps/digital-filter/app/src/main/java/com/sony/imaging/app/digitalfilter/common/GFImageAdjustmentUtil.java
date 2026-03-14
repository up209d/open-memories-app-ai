package com.sony.imaging.app.digitalfilter.common;

import android.graphics.Rect;
import android.os.Handler;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.digitalfilter.sa.NDSA2;
import com.sony.imaging.app.digitalfilter.sa.NDSA2Multi;
import com.sony.imaging.app.digitalfilter.sa.PRESA;
import com.sony.imaging.app.digitalfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public class GFImageAdjustmentUtil {
    private Object waitEffectRunnableObject = new Object();
    private static final String TAG = AppLog.getClassName();
    private static GFImageAdjustmentUtil mInstance = null;
    private static PRESA mPRESA = null;
    private static OptimizedImage mLiveViewImage = null;
    private static Handler mHandler = null;
    private static Runnable mEffectRunnable = null;
    private static boolean mIsEffectRunning = false;
    private static boolean mIsEffectQued = false;
    private static boolean mIsTerminated = false;
    private static boolean mIsConformedToUI = true;

    public static GFImageAdjustmentUtil getInstance() {
        if (mInstance == null) {
            mInstance = new GFImageAdjustmentUtil();
        }
        return mInstance;
    }

    public void initialize() {
        mIsEffectRunning = false;
        mIsEffectQued = false;
        mIsTerminated = false;
        mIsConformedToUI = true;
        if (mHandler == null) {
            mHandler = new Handler();
        }
    }

    public void startAdjustmentPreview() {
        mPRESA = PRESA.getInstance();
        mPRESA.open();
        mPRESA.execute(getLiveViewImage());
    }

    public void updateAdjustmentPreview() {
        AppLog.enter(TAG, "updateAdjustmentPreview");
        mEffectRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.common.GFImageAdjustmentUtil.1
            @Override // java.lang.Runnable
            public void run() {
                AppLog.enter(GFImageAdjustmentUtil.TAG, "updateAdjustmentPreview:run");
                if (GFImageAdjustmentUtil.mIsTerminated) {
                    boolean unused = GFImageAdjustmentUtil.mIsEffectRunning = false;
                    boolean unused2 = GFImageAdjustmentUtil.mIsEffectQued = false;
                }
                if (GFImageAdjustmentUtil.mIsEffectRunning || GFImageAdjustmentUtil.mIsTerminated) {
                    boolean unused3 = GFImageAdjustmentUtil.mIsEffectQued = true;
                } else {
                    boolean unused4 = GFImageAdjustmentUtil.mIsEffectRunning = true;
                    if (GFConstants.NDSA_MULTI_UNIT) {
                        NDSA2Multi.getInstance().update();
                    } else {
                        NDSA2.getInstance().update();
                    }
                    if (!GFImageAdjustmentUtil.mIsTerminated) {
                        GFImageAdjustmentUtil.mPRESA.update(GFImageAdjustmentUtil.this.getLiveViewImage());
                    }
                    boolean unused5 = GFImageAdjustmentUtil.mIsEffectRunning = false;
                    if (!GFImageAdjustmentUtil.mIsTerminated) {
                        if (GFImageAdjustmentUtil.mIsEffectQued) {
                            boolean unused6 = GFImageAdjustmentUtil.mIsEffectQued = false;
                            if (GFImageAdjustmentUtil.mHandler != null) {
                                GFImageAdjustmentUtil.mHandler.post(GFImageAdjustmentUtil.mEffectRunnable);
                            }
                        } else {
                            boolean unused7 = GFImageAdjustmentUtil.mIsConformedToUI = true;
                            CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
                        }
                    }
                }
                synchronized (GFImageAdjustmentUtil.this.waitEffectRunnableObject) {
                    AppLog.info(GFImageAdjustmentUtil.TAG, "Lock release reqest of waitEffectRunnableObject");
                    GFImageAdjustmentUtil.this.waitEffectRunnableObject.notifyAll();
                }
                AppLog.exit(GFImageAdjustmentUtil.TAG, "updateAdjustmentPreview");
            }
        };
        if (!mIsEffectRunning && !mIsEffectQued) {
            mIsConformedToUI = false;
            mHandler.post(mEffectRunnable);
            CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
        } else if (mIsEffectRunning) {
            mIsConformedToUI = false;
            mIsEffectQued = true;
        }
        AppLog.exit(TAG, "updateAdjustmentPreview");
    }

    public void terminateAdjustmentPreview() {
        AppLog.enter(TAG, "terminateAdjustmentPreview");
        mIsEffectQued = false;
        mIsTerminated = true;
        if (GFConstants.NDSA_MULTI_UNIT) {
            NDSA2Multi.getInstance().cancel();
        } else {
            NDSA2.getInstance().cancel();
        }
        if (mIsEffectRunning) {
            synchronized (this.waitEffectRunnableObject) {
                AppLog.info(TAG, "Lock started by waitEffectRunnableObject");
                try {
                    this.waitEffectRunnableObject.wait(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AppLog.info(TAG, "Lock ended by waitEffectRunnableObject");
            }
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(mEffectRunnable);
            mHandler = null;
        }
        if (mPRESA != null) {
            mPRESA.close();
            mPRESA = null;
        }
        if (mLiveViewImage != null) {
            mLiveViewImage.release();
            mLiveViewImage = null;
        }
        mIsConformedToUI = true;
        AppLog.exit(TAG, "terminateAdjustmentPreview");
    }

    public boolean isConformedToUISetting() {
        AppLog.info(TAG, "isConformedToUISetting : " + mIsConformedToUI);
        return mIsConformedToUI;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public OptimizedImage getLiveViewImage() {
        CameraSequence.RawData raw = GFCompositProcess.getCompositRaw();
        OptimizedImage optImage = getDevelopImage(raw, false);
        mLiveViewImage = getScaledOptimizedImage(optImage, true);
        return mLiveViewImage;
    }

    private OptimizedImage getDevelopImage(CameraSequence.RawData raw, boolean releaseSource) {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraSequence.DefaultDevelopFilter filter = new CameraSequence.DefaultDevelopFilter();
        filter.setSource(raw, releaseSource);
        filter.execute();
        OptimizedImage optImage = filter.getOutput();
        AppLog.info(TAG, "DefaultDevelopFilter(raw): " + raw);
        AppLog.info(TAG, "DefaultDevelopFilter(optImage): " + optImage);
        filter.clearSources();
        filter.release();
        AppLog.exit(TAG, AppLog.getMethodName());
        return optImage;
    }

    private OptimizedImage getScaledOptimizedImage(OptimizedImage inOptimizedImage, boolean releaseSource) {
        boolean isCroppedTopBottom;
        int cropWidth;
        int cropHeight;
        int scaledHeight;
        int scaledWidth;
        AppLog.enter(TAG, AppLog.getMethodName());
        OptimizedImage outOptimizedImage = null;
        String aspectRatio = PictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        AppLog.info(TAG, "Aspect Ratio: " + aspectRatio);
        if (aspectRatio != null) {
            Rect cropRect = new Rect();
            int width = inOptimizedImage.getWidth();
            int height = inOptimizedImage.getHeight();
            float inputAspectRatio = height / width;
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                isCroppedTopBottom = 1.0f < inputAspectRatio;
                cropHeight = AppRoot.USER_KEYCODE.WATER_HOUSING;
                cropWidth = 640;
            } else if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_3_2)) {
                isCroppedTopBottom = 0.6666667f < inputAspectRatio;
                cropWidth = 1024;
                cropHeight = 684;
            } else if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_16_9)) {
                isCroppedTopBottom = 0.5625f < inputAspectRatio;
                cropWidth = 1024;
                cropHeight = 576;
            } else {
                isCroppedTopBottom = 0.75f < inputAspectRatio;
                cropWidth = 1024;
                cropHeight = 768;
            }
            if (isCroppedTopBottom) {
                scaledWidth = cropWidth;
                scaledHeight = Math.max(((height * scaledWidth) / width) & (-8), cropHeight);
                cropRect.left = 0;
                cropRect.right = cropWidth;
                cropRect.top = (scaledHeight - cropHeight) / 2;
                cropRect.bottom = cropRect.top + cropHeight;
            } else {
                scaledHeight = cropHeight;
                scaledWidth = Math.max(((width * scaledHeight) / height) & (-8), cropWidth);
                cropRect.top = 0;
                cropRect.bottom = cropHeight;
                cropRect.left = (scaledWidth - cropWidth) / 2;
                cropRect.right = cropRect.left + cropWidth;
            }
            OptimizedImage outOptimizedImage2 = getScaledOptimizedImage(inOptimizedImage, releaseSource, scaledWidth, scaledHeight);
            outOptimizedImage = getCroppedOptimizedImage(outOptimizedImage2, releaseSource, cropRect);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return outOptimizedImage;
    }

    private OptimizedImage getScaledOptimizedImage(OptimizedImage inOptimizedImage, boolean releaseSource, int width, int height) {
        AppLog.enter(TAG, AppLog.getMethodName());
        OptimizedImage outOptimizedImage = null;
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(inOptimizedImage, releaseSource);
        scaleFilter.setDestSize(width, height);
        boolean bExecuted = scaleFilter.execute();
        if (bExecuted) {
            outOptimizedImage = scaleFilter.getOutput();
        } else {
            AppLog.info(TAG, "Image filter executed failed");
        }
        scaleFilter.clearSources();
        scaleFilter.release();
        AppLog.exit(TAG, AppLog.getMethodName());
        return outOptimizedImage;
    }

    private OptimizedImage getCroppedOptimizedImage(OptimizedImage inOptimizedImage, boolean releaseSource, Rect cropRect) {
        AppLog.enter(TAG, AppLog.getMethodName());
        OptimizedImage outOptimizedImage = null;
        CropImageFilter cropFilter = new CropImageFilter();
        cropFilter.setSource(inOptimizedImage, releaseSource);
        cropFilter.setSrcRect(cropRect);
        boolean bExecuted = cropFilter.execute();
        if (bExecuted) {
            outOptimizedImage = cropFilter.getOutput();
        } else {
            AppLog.info(TAG, "Image filter executed failed");
        }
        cropFilter.clearSources();
        cropFilter.release();
        AppLog.exit(TAG, AppLog.getMethodName());
        return outOptimizedImage;
    }
}
