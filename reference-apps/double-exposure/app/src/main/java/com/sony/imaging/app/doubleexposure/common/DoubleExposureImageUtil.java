package com.sony.imaging.app.doubleexposure.common;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.doubleexposure.menu.controller.ReverseController;
import com.sony.imaging.app.doubleexposure.menu.controller.RotationController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.scalar.graphics.JpegExporter;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceMemory;
import com.sony.scalar.provider.AvindexStore;

/* loaded from: classes.dex */
public class DoubleExposureImageUtil {
    private static final String TAG = AppLog.getClassName();
    private static DoubleExposureImageUtil sInstance = null;
    private FirstImage mFirstImage;
    private final int EE_IMAGE_SIZE = 1572864;
    private ContentsIdentifier mFirstImageContentsIdentifier = null;

    private DoubleExposureImageUtil() {
        this.mFirstImage = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mFirstImage = new FirstImage();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public static DoubleExposureImageUtil getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new DoubleExposureImageUtil();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    public int getWidth() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mFirstImage.mWidth;
    }

    public int getHeight() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mFirstImage.mHeight;
    }

    public int getCanvasWidth() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mFirstImage.mCanvasWidth;
    }

    public int getCanvasHeight() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mFirstImage.mCanvasHeight;
    }

    public void setFirstImageContentsIdentifier(ContentsIdentifier contentsIdentifier) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mFirstImageContentsIdentifier = contentsIdentifier;
        BackUpUtil.getInstance().setPreference(DoubleExposureBackUpKey.KEY_FIRST_IMAGE_ID, Long.valueOf(contentsIdentifier._id));
        BackUpUtil.getInstance().setPreference(DoubleExposureBackUpKey.KEY_FIRST_IMAGE_DATA, contentsIdentifier.data);
        BackUpUtil.getInstance().setPreference(DoubleExposureBackUpKey.KEY_FIRST_IMAGE_MEDIA_ID, contentsIdentifier.mediaId);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public ContentsIdentifier getFirstImageContentsIdentifier() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        long Id = BackUpUtil.getInstance().getPreferenceLong(DoubleExposureBackUpKey.KEY_FIRST_IMAGE_ID, 0L);
        String data = BackUpUtil.getInstance().getPreferenceString(DoubleExposureBackUpKey.KEY_FIRST_IMAGE_DATA, null);
        String mediaId = BackUpUtil.getInstance().getPreferenceString(DoubleExposureBackUpKey.KEY_FIRST_IMAGE_MEDIA_ID, null);
        this.mFirstImageContentsIdentifier = new ContentsIdentifier(Id, data, mediaId);
        return this.mFirstImageContentsIdentifier;
    }

    public void saveImage(OptimizedImage optimizedImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String imageQuality = DoubleExposureUtil.getInstance().getImageQuality();
        JpegExporter jpegExporter = new JpegExporter();
        JpegExporter.Options option = new JpegExporter.Options();
        if (imageQuality != null && option != null) {
            if (imageQuality.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_FINE) || imageQuality.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_RAWJPEG)) {
                option.quality = 2;
            } else if (imageQuality.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_STANDARD)) {
                option.quality = 1;
            } else if (imageQuality.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_EXTRAFINE)) {
                option.quality = 3;
            }
        }
        if (DatabaseUtil.MediaStatus.MOUNTED == DatabaseUtil.checkMediaStatus() && jpegExporter != null) {
            jpegExporter.encode(optimizedImage, AvindexStore.getExternalMediaIds()[0], option);
        } else {
            AppLog.info(TAG, "Media is not inserted");
        }
        if (jpegExporter != null) {
            jpegExporter.release();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public void changeImageOrientation(int orientation) {
        AppLog.enter(TAG, AppLog.getMethodName());
        ContentResolver contentResolver = ContentsManager.getInstance().getContentResolver();
        String[] mediaIds = AvindexStore.getExternalMediaIds();
        AvindexStore.Images.waitAndUpdateDatabase(contentResolver, mediaIds[0]);
        Uri baseUri = AvindexStore.Images.Media.getContentUri(mediaIds[0]);
        Cursor cursor = contentResolver.query(baseUri, null, null, null, null);
        long origId = -1;
        if (cursor != null && cursor.moveToLast()) {
            origId = cursor.getLong(cursor.getColumnIndex("_id"));
            cursor.close();
        }
        AppLog.info(TAG, "Orientation: " + orientation);
        int rotate = -1;
        switch (orientation) {
            case 3:
                rotate = 1;
                break;
            case 6:
                rotate = 0;
                break;
            case 8:
                rotate = 2;
                break;
        }
        AvindexStore.Images.Media.rotateImage(contentResolver, baseUri, origId, rotate);
        AppLog.enter(TAG, AppLog.getMethodName());
    }

    public OptimizedImage copyOptimizedImage(OptimizedImage inOptimizedImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int width = 0;
        int height = 0;
        if (inOptimizedImage != null) {
            width = inOptimizedImage.getWidth();
            height = inOptimizedImage.getHeight();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return getScaledOptimizedImage(inOptimizedImage, false, width, height);
    }

    public void storeOptimizedImage(OptimizedImage optimizedImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mFirstImage.setParam(optimizedImage);
        MemoryUtil memoryUtil = new MemoryUtil();
        memoryUtil.memoryCopyDiademToApplication(this.mFirstImage.mAddr, this.mFirstImage.mbArray, this.mFirstImage.mSize);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public DeviceMemory memoryCopyApplicationToDiadem(DSP dsp) {
        AppLog.enter(TAG, AppLog.getMethodName());
        DeviceMemory eeBuffer = dsp.createImage(this.mFirstImage.mCanvasWidth, this.mFirstImage.mHeight);
        if (eeBuffer == null && this.mFirstImage.mCanvasWidth == 0 && this.mFirstImage.mHeight == 0) {
            DoubleExposureUtil.getInstance().setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
            return null;
        }
        MemoryUtil memoryUtil = new MemoryUtil();
        memoryUtil.memoryCopyApplicationToDiadem(this.mFirstImage.mbArray, dsp.getPropertyAsInt(eeBuffer, "memory-address"), this.mFirstImage.mSize);
        AppLog.exit(TAG, AppLog.getMethodName());
        return eeBuffer;
    }

    public void memoryCopyApplicationToDiadem(OptimizedImage optImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        MemoryUtil memoryUtil = new MemoryUtil();
        memoryUtil.memoryCopyApplicationToDiadem(this.mFirstImage.mbArray, dsp.getPropertyAsInt(optImage, "memory-address"), this.mFirstImage.mSize);
        dsp.release();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public OptimizedImage getOptimizedImage(ContentsIdentifier contentsIdentifier) {
        AppLog.enter(TAG, AppLog.getMethodName());
        OptimizedImage optimizedImage = null;
        if (contentsIdentifier != null) {
            optimizedImage = ContentsManager.getInstance().getOptimizedImageWithoutCache(contentsIdentifier, 1);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return optimizedImage;
    }

    public OptimizedImage getScaledOptimizedImage(OptimizedImage inOptimizedImage, boolean releaseSource) {
        AppLog.enter(TAG, AppLog.getMethodName());
        OptimizedImage outOptimizedImage = null;
        String aspectRatio = DoubleExposureUtil.getInstance().getFirstImageAspectRatio();
        if (aspectRatio == null) {
            aspectRatio = DoubleExposureUtil.getInstance().getCameraSettingsAspectRatio();
        }
        AppLog.info(TAG, "Aspect Ratio: " + aspectRatio);
        if (aspectRatio != null) {
            if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_1_1)) {
                outOptimizedImage = getScaledOptimizedImage(inOptimizedImage, releaseSource, AppRoot.USER_KEYCODE.WATER_HOUSING, AppRoot.USER_KEYCODE.WATER_HOUSING);
            } else if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_3_2)) {
                outOptimizedImage = getScaledOptimizedImage(inOptimizedImage, releaseSource, 1024, 684);
            } else if (aspectRatio.equalsIgnoreCase(PictureSizeController.ASPECT_16_9)) {
                outOptimizedImage = getScaledOptimizedImage(inOptimizedImage, releaseSource, 1024, 576);
            } else {
                outOptimizedImage = getScaledOptimizedImage(inOptimizedImage, releaseSource, 1024, 768);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return outOptimizedImage;
    }

    public OptimizedImage getScaledOptimizedImage(OptimizedImage inOptimizedImage, boolean releaseSource, int width, int height) {
        AppLog.enter(TAG, AppLog.getMethodName());
        OptimizedImage outOptimizedImage = null;
        boolean bExecuted = false;
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        if (DatabaseUtil.MediaStatus.MOUNTED == DatabaseUtil.checkMediaStatus()) {
            scaleFilter.setSource(inOptimizedImage, releaseSource);
            scaleFilter.setDestSize(width, height);
            bExecuted = scaleFilter.execute();
        } else {
            AppLog.info(TAG, "Media is not inserted");
        }
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

    public OptimizedImage getRotatedOptimizedImage(OptimizedImage inOptimizedImage) {
        String rotation;
        AppLog.enter(TAG, AppLog.getMethodName());
        OptimizedImage outOptimizedImage = null;
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (selectedTheme != null && "Manual".equals(selectedTheme)) {
            rotation = RotationController.getInstance().getValue("Rotation");
        } else {
            rotation = DoubleExposureUtil.getInstance().getRotationFirstShooting();
        }
        if (rotation != null && rotation.equalsIgnoreCase("On")) {
            RotateImageFilter rotateImageFilter = new RotateImageFilter();
            rotateImageFilter.setSource(inOptimizedImage, true);
            rotateImageFilter.setRotation(RotateImageFilter.ROTATION_DEGREE.DEGREE_180);
            boolean bExecuted = false;
            if (DatabaseUtil.MediaStatus.MOUNTED == DatabaseUtil.checkMediaStatus()) {
                bExecuted = rotateImageFilter.execute();
            } else {
                AppLog.info(TAG, "Media is not inserted");
            }
            if (bExecuted) {
                AppLog.info(TAG, "Rotation filter executed successfully");
                outOptimizedImage = rotateImageFilter.getOutput();
            } else {
                AppLog.info(TAG, "Rotation filter executed failed");
            }
            rotateImageFilter.release();
        } else {
            AppLog.info(TAG, "Rotation filter is off");
            outOptimizedImage = inOptimizedImage;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return outOptimizedImage;
    }

    public OptimizedImage getReversedOptimizedImage(OptimizedImage inOptimizedImage) {
        String reverse;
        OptimizedImage outOptimizedImage;
        AppLog.enter(TAG, AppLog.getMethodName());
        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
        if (selectedTheme != null && "Manual".equals(selectedTheme)) {
            reverse = ReverseController.getInstance().getValue(ReverseController.TAG_REVERSE);
        } else {
            reverse = DoubleExposureUtil.getInstance().getReverseFirstShooting();
        }
        if ((reverse != null && reverse.equalsIgnoreCase(ReverseController.HORIZONTAL)) || reverse.equalsIgnoreCase(ReverseController.VERTICAL)) {
            DESAPre mDESAPre = DESAPre.getInstance();
            mDESAPre.setPackageName(AppContext.getAppContext().getPackageName());
            mDESAPre.intialize();
            if (reverse.equalsIgnoreCase(ReverseController.HORIZONTAL)) {
                mDESAPre.setRotationMode(0);
            } else if (reverse.equalsIgnoreCase(ReverseController.VERTICAL)) {
                mDESAPre.setRotationMode(1);
            }
            outOptimizedImage = copyOptimizedImage(inOptimizedImage);
            if (DatabaseUtil.MediaStatus.MOUNTED == DatabaseUtil.checkMediaStatus()) {
                mDESAPre.execute(inOptimizedImage, outOptimizedImage);
            } else {
                AppLog.info(TAG, "Media is not inserted");
            }
            if (inOptimizedImage != null) {
                inOptimizedImage.release();
            }
            mDESAPre.terminate();
        } else {
            outOptimizedImage = inOptimizedImage;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return outOptimizedImage;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FirstImage {
        private int mAddr;
        private int mCanvasHeight;
        private int mCanvasWidth;
        private int mHeight;
        private int mSize;
        private int mWidth;
        private byte[] mbArray;

        private FirstImage() {
            this.mbArray = new byte[1572864];
        }

        public void setParam(OptimizedImage optimizedImage) {
            DSP dsp = DSP.createProcessor("sony-di-dsp");
            this.mAddr = dsp.getPropertyAsInt(optimizedImage, "memory-address");
            this.mAddr += dsp.getPropertyAsInt(optimizedImage, "image-data-offset");
            this.mWidth = optimizedImage.getWidth();
            this.mHeight = optimizedImage.getHeight();
            this.mCanvasWidth = dsp.getPropertyAsInt(optimizedImage, "image-canvas-width");
            this.mCanvasHeight = dsp.getPropertyAsInt(optimizedImage, "image-canvas-height");
            this.mSize = this.mCanvasWidth * this.mHeight * 2;
            dsp.release();
        }
    }
}
