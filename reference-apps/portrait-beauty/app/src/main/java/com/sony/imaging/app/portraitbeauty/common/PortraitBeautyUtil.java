package com.sony.imaging.app.portraitbeauty.common;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.ShootingExecutor;
import com.sony.imaging.app.portraitbeauty.menu.controller.SelfTimerIntervalPriorityController;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyAdjustEffectState;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyExecutorCreater;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.DatabaseUtil;
import com.sony.scalar.graphics.JpegExporter;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class PortraitBeautyUtil {
    public static final String ALERT_MESSAGE = "ALERT_MESSAGE";
    public static final String ALERT_SWITCHTOINDEXVIEW = "ALERT_SWITCHTOINDEXVIEW";
    public static final int ASPECT_RATIO_11 = 3;
    public static final int ASPECT_RATIO_169 = 1;
    public static final int ASPECT_RATIO_32 = 0;
    public static final int ASPECT_RATIO_43 = 2;
    public static final int BRIGHTNESS = 8;
    public static final String ID_CONFIRMSAVINGLAYOUT = "ID_CONFIRMSAVINGLAYOUT";
    public static final String ID_MANUALSTARTUPMESSAGE = "ID_MANUALSTARTUPMESSAGE";
    public static final String ID_MESSAGEALERT = "ID_MESSAGEALERT";
    public static final String ID_MESSAGENOFACE = "ID_MESSAGENOFACE";
    public static final String ID_SAVINGLAYOUT = "ID_SAVINGLAYOUT";
    public static final int PATTERN = 1;
    public static final int POSITION_LEFT = 4;
    public static final int POSITION_RIGHT = 5;
    public static final int SAVE = 6;
    public static final int SIZE = 2;
    private static final long SIZE_IMAGE_LARGE = 42551296;
    public static final int TRANSITION_INDEXPB = 1000;
    private static int mOrientationInfo;
    public static boolean bIsAdjustModeGuide = false;
    private static final String TAG = AppLog.getClassName();
    private static PortraitBeautyUtil sInstance = null;
    private static int mAspectRatio = 0;
    private static boolean isSpaceAvailableInMemoryCard = false;
    private static List<String> mSupportedApertureList = null;
    public static int runnableCount = 0;
    public static View.OnTouchListener blockTouchEvent = new View.OnTouchListener() { // from class: com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil.1
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };
    public boolean isFirstTimeLaunched = true;
    private boolean isLensAttachedOnShootingScreen = false;
    private boolean isPowerKeyPressed = false;
    private List<String> getAvailableValue = null;

    /* loaded from: classes.dex */
    public interface SaveCallback {
        void onFail();

        void onSuccess();
    }

    public boolean isPowerKeyPressed() {
        return this.isPowerKeyPressed;
    }

    public void setPowerKeyPressed(boolean isPowerKeyPressed) {
        this.isPowerKeyPressed = isPowerKeyPressed;
    }

    public static PortraitBeautyUtil getInstance() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (sInstance == null) {
            sInstance = new PortraitBeautyUtil();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return sInstance;
    }

    public String getCameraApertureValues() {
        AppLog.enter(TAG, AppLog.getMethodName());
        CameraSetting camSetting = CameraSetting.getInstance();
        String convertedAperture = PortraitBeautyConstants.INVALID_APERTURE_STRING;
        if (camSetting.getApertureInfo() != null) {
            int currentAperture = camSetting.getApertureInfo().currentAperture;
            convertedAperture = getInstance().convertApertureValueFormat(currentAperture / 100.0f);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return convertedAperture;
    }

    public String convertApertureValueFormat(float value) {
        String displayValue;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (value == PortraitBeautyConstants.INVALID_APERTURE_VALUE) {
            displayValue = PortraitBeautyConstants.INVALID_APERTURE_STRING;
        } else if (value < 10.0f) {
            displayValue = String.format(PortraitBeautyConstants.FORMAT_ONE_DIGIT, Float.valueOf(value));
        } else {
            displayValue = String.format(PortraitBeautyConstants.FORMAT_BIG_DIGIT, Float.valueOf(value));
        }
        String displayValue2 = displayValue.replace(',', '.');
        AppLog.exit(TAG, AppLog.getMethodName());
        return displayValue2;
    }

    public String getMaxApetureValue() {
        CameraEx.ApertureInfo info = CameraSetting.getInstance().getApertureInfo();
        int maxAperture = info.currentAvailableMax;
        String maxApertureConvertedValue = getInstance().convertApertureValueFormat(maxAperture / 100.0f);
        return maxApertureConvertedValue;
    }

    public static void setVirtualMediaIds() {
        UtilPFWorkaround.setVirtualMediaIds(true);
        CautionUtilityClass.getInstance().setMedia(AvindexStore.getVirtualMediaIds());
    }

    public static void setActualMediaIds() {
        ExecutorCreator creator = ExecutorCreator.getInstance();
        creator.stableSequence();
        ShootingExecutor executor = (ShootingExecutor) creator.getSequence();
        String[] ids = AvindexStore.getExternalMediaIds();
        if (ids != null && ids[0] != null) {
            executor.setRecordingMedia(ids[0], null);
        }
        creator.updateSequence();
    }

    public static int getAspectRatio() {
        return mAspectRatio;
    }

    public static void setAspectRatio(int aspectRatio) {
        mAspectRatio = aspectRatio;
    }

    public static void retrieveAspectRatio(float optImgWidth, float optImgHeight) {
        float aspectValue = optImgWidth / optImgHeight;
        if (aspectValue >= 1.48f && aspectValue <= 1.52f) {
            setAspectRatio(0);
            return;
        }
        if (aspectValue >= 1.75f && aspectValue <= 1.8f) {
            setAspectRatio(1);
            return;
        }
        if (aspectValue >= 1.31f && aspectValue <= 1.35f) {
            setAspectRatio(2);
        } else if (aspectValue >= 0.98f && aspectValue <= 1.02f) {
            setAspectRatio(3);
        } else {
            Log.e(TAG, "===Image Aspect Ratio not supported");
        }
    }

    public static int getOrientationInfo() {
        return mOrientationInfo;
    }

    public static void setOrientationInfo(int orientationInfo) {
        mOrientationInfo = orientationInfo;
    }

    public static void setSpaceAvailableInMemoryCard(boolean isSpaceAvailableInMemoryCard2) {
        isSpaceAvailableInMemoryCard = isSpaceAvailableInMemoryCard2;
    }

    public void saveImage(OptimizedImage optimizedImage) {
        AppLog.enter(TAG, AppLog.getMethodName());
        JpegExporter jpegExporter = new JpegExporter();
        JpegExporter.Options option = new JpegExporter.Options();
        if (PictureQualityController.PICTURE_QUALITY_FINE != 0 && option != null) {
            if (!PictureQualityController.PICTURE_QUALITY_FINE.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_FINE) && !PictureQualityController.PICTURE_QUALITY_FINE.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_RAWJPEG)) {
                if (!PictureQualityController.PICTURE_QUALITY_FINE.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_STANDARD)) {
                    if (PictureQualityController.PICTURE_QUALITY_FINE.equalsIgnoreCase(PictureQualityController.PICTURE_QUALITY_EXTRAFINE)) {
                        option.quality = 3;
                    }
                } else {
                    option.quality = 1;
                }
            } else {
                option.quality = 2;
            }
        }
        if (DatabaseUtil.MediaStatus.MOUNTED == DatabaseUtil.checkMediaStatus() && jpegExporter != null) {
            jpegExporter.encode(optimizedImage, AvindexStore.getExternalMediaIds()[0], option);
            ContentsManager mgr = ContentsManager.getInstance();
            String[] externalMedias = AvindexStore.getExternalMediaIds();
            AvindexStore.Images.waitAndUpdateDatabase(ContentsManager.getInstance().getContentResolver(), externalMedias[0]);
            mgr.requeryData();
            mgr.moveToEntryPosition();
        } else {
            AppLog.info(TAG, "Media is not inserted");
        }
        if (jpegExporter != null) {
            jpegExporter.release();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    public String getAspectRatio(float width, float height) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String aspectRatio = null;
        float aspect = width / height;
        if (1.4d <= aspect && aspect < 1.6d) {
            aspectRatio = PictureSizeController.ASPECT_3_2;
        } else if (1.6d <= aspect && aspect < 1.8d) {
            aspectRatio = PictureSizeController.ASPECT_16_9;
        } else if (1.2d <= aspect && aspect < 1.4d) {
            aspectRatio = PictureSizeController.ASPECT_4_3;
        }
        AppLog.info(TAG, "Aspect Ratio: " + aspectRatio);
        AppLog.exit(TAG, AppLog.getMethodName());
        return aspectRatio;
    }

    public String getPictureSize(String aspectRatio, int width, int height) {
        String imageSize;
        AppLog.enter(TAG, AppLog.getMethodName());
        int indexApectRatio = getAspectRatioIndex(aspectRatio);
        if (-1 == indexApectRatio) {
            return null;
        }
        List<ScalarProperties.PictureSize> pictureSizeList = ScalarProperties.getSupportedPictureSizes();
        ScalarProperties.PictureSize pictureSizeS = null;
        ScalarProperties.PictureSize pictureSizeM = null;
        ScalarProperties.PictureSize pictureSizeL = null;
        if (pictureSizeList != null) {
            ScalarProperties.PictureSize pictureSizeL2 = pictureSizeList.get((indexApectRatio * 3) + 0);
            pictureSizeL = pictureSizeL2;
            ScalarProperties.PictureSize pictureSizeM2 = pictureSizeList.get((indexApectRatio * 3) + 1);
            pictureSizeM = pictureSizeM2;
            ScalarProperties.PictureSize pictureSizeS2 = pictureSizeList.get((indexApectRatio * 3) + 2);
            pictureSizeS = pictureSizeS2;
        }
        if (width <= pictureSizeS.width && height <= pictureSizeS.height) {
            imageSize = PictureSizeController.SIZE_S;
        } else if (width <= pictureSizeM.width && height <= pictureSizeM.height) {
            imageSize = PictureSizeController.SIZE_M;
        } else if (width <= pictureSizeL.width && height <= pictureSizeL.height) {
            imageSize = PictureSizeController.SIZE_L;
        } else {
            imageSize = null;
        }
        AppLog.info(TAG, "Picutre Size: " + imageSize);
        AppLog.exit(TAG, AppLog.getMethodName());
        return imageSize;
    }

    private int getAspectRatioIndex(String aspectRatio) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int aspectRatioIndex = -1;
        if (PictureSizeController.ASPECT_3_2.equals(aspectRatio)) {
            aspectRatioIndex = 0;
        } else if (PictureSizeController.ASPECT_16_9.equals(aspectRatio)) {
            aspectRatioIndex = 1;
        } else if (PictureSizeController.ASPECT_4_3.equals(aspectRatio)) {
            aspectRatioIndex = 2;
        } else if (PictureSizeController.ASPECT_1_1.equals(aspectRatio)) {
            aspectRatioIndex = 3;
        }
        AppLog.info(TAG, "Aspect Ratio Index: " + aspectRatioIndex);
        AppLog.exit(TAG, AppLog.getMethodName());
        return aspectRatioIndex;
    }

    public boolean isFirstTimeLaunched() {
        return this.isFirstTimeLaunched;
    }

    public void setFirstTimeLaunched(boolean isFirstTimeLaunched) {
        this.isFirstTimeLaunched = isFirstTimeLaunched;
    }

    public boolean isLensAttachedOnShootingScreen() {
        return this.isLensAttachedOnShootingScreen;
    }

    public void setLensAttachedOnShootingScreen(boolean isLensAttachedOnShootingScreen) {
        this.isLensAttachedOnShootingScreen = isLensAttachedOnShootingScreen;
    }

    public int getTime() {
        String SelfTimerSetting = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, SelfTimerIntervalPriorityController.SELFTIMERON5);
        if (SelfTimerSetting == null) {
            SelfTimerSetting = BackUpUtil.getInstance().getPreferenceString(PortraitBeautyBackUpKey.KEY_SELECTED_SELF_TIMER, SelfTimerIntervalPriorityController.SELFTIMERON5);
        }
        if (SelfTimerIntervalPriorityController.SELFTIMERON2.equals(SelfTimerSetting)) {
            return PortraitBeautyConstants.THREE_SECOND_MILLIS;
        }
        if (SelfTimerIntervalPriorityController.SELFTIMERON5.equals(SelfTimerSetting)) {
            return PortraitBeautyConstants.SIX_SECOND_MILLIS;
        }
        if (!SelfTimerIntervalPriorityController.SELFTIMERON10.equals(SelfTimerSetting)) {
            return 0;
        }
        return PortraitBeautyConstants.ELEVEN_SECOND_MILLIS;
    }

    public void playBEEPfunction() {
        if (!PortraitBeautyExecutorCreater.isHalt) {
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELFTIMER);
        }
    }

    public boolean isSpaceAvailableInMemoryCard() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String[] externalMediaIds = AvindexStore.getExternalMediaIds();
        boolean bFullForStillImages = AvailableInfo.isFactor("INH_FACTOR_CONTENT_FULL_FOR_STILL", externalMediaIds[0]);
        boolean bFolderFullForStillImages = AvailableInfo.isFactor("INH_FACTOR_FOLDER_FULL_FOR_STILL", externalMediaIds[0]);
        boolean bErrorAvindex = AvailableInfo.isFactor("INH_FACTOR_NEED_REPAIR_AVINDEX", externalMediaIds[0]);
        boolean bIsAvailable = false;
        AvailableInfo.update();
        if (bFullForStillImages || bErrorAvindex || bFolderFullForStillImages) {
            bIsAvailable = false;
        } else {
            try {
                StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
                long blockSize = statFs.getBlockSize();
                long availableBlocks = statFs.getAvailableBlocks();
                long availableSize = availableBlocks * blockSize;
                AppLog.checkIf(TAG, "Available Size: " + availableSize);
                if (availableSize > 82790400) {
                    bIsAvailable = true;
                }
            } catch (Exception e) {
                AppLog.checkIf(TAG, "Exception: " + e.toString());
                return false;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return bIsAvailable;
    }

    public void releaseAllStaticOptimizedImage() {
        ImageEditor.releaseOptImage(PortraitBeautyAdjustEffectState.sOptimizedImage);
        ImageEditor.releaseOptImage(CatchLightPlayBackLayout.sSelectedOptimizedImage);
    }

    public void readAvailableValue_AspectRatios() {
        this.getAvailableValue = PictureSizeController.getInstance().getAvailableValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
    }

    public List<String> getAvailableValue_AspectRatios() {
        if (this.getAvailableValue != null) {
            return this.getAvailableValue;
        }
        return null;
    }

    public String aspectRatioComparer(int aspect) {
        if (aspect == 0) {
            return PictureSizeController.ASPECT_4_3;
        }
        if (aspect == 1) {
            return PictureSizeController.ASPECT_16_9;
        }
        if (aspect == 2) {
            return PictureSizeController.ASPECT_1_1;
        }
        if (aspect != 10) {
            return null;
        }
        return PictureSizeController.ASPECT_3_2;
    }
}
