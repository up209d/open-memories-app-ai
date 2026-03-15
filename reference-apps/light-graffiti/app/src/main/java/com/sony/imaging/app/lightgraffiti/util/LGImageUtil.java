package com.sony.imaging.app.lightgraffiti.util;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.PictureSizeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.lightgraffiti.shooting.controller.LGPictureSizeController;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;

/* loaded from: classes.dex */
public class LGImageUtil {
    private static final int SFR11Height = 640;
    private static final int SFR11Width = 640;
    private static final int SFR169Height = 576;
    private static final int SFR169Width = 1024;
    private static final int SFR32Height = 684;
    private static final int SFR32Width = 1024;
    private static final int SFRDefaultHeight = 768;
    private static final int SFRDefaultWidth = 1024;
    private static final String TAG = LGImageUtil.class.getSimpleName();
    private static LGImageUtil sInstance = null;

    private LGImageUtil() {
    }

    public static LGImageUtil getInstance() {
        if (sInstance == null) {
            sInstance = new LGImageUtil();
        }
        return sInstance;
    }

    public DeviceBuffer copyOptimizedImageToDeviceBuffer(OptimizedImage optImg) {
        MemoryUtil memoryUtil = new MemoryUtil();
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        int optImgAddr = dsp.getPropertyAsInt(optImg, "memory-address");
        int optImgSize = dsp.getPropertyAsInt(optImg, "memory-size");
        int canvasWidth = dsp.getPropertyAsInt(optImg, "image-canvas-width");
        int width = optImg.getWidth();
        int height = optImg.getHeight();
        DeviceBuffer devBuf = dsp.createBuffer(optImg.getWidth() * optImg.getHeight() * 2);
        int devBufAddr = dsp.getPropertyAsInt(devBuf, "memory-address");
        int devBufSize = dsp.getPropertyAsInt(devBuf, "memory-size");
        Log.d("DSPTEST", "LGImageUtil(memcpy) Input <<<<<<<<<<<");
        Log.d("DSPTEST", "IMAGE_CANVAS_HEIGHT = " + dsp.getPropertyAsInt(optImg, "image-canvas-height"));
        Log.d("DSPTEST", "IMAGE_CANVAS_WIDTH = " + dsp.getPropertyAsInt(optImg, "image-canvas-width"));
        Log.d("DSPTEST", "IMAGE_DATA_OFFSET = " + dsp.getPropertyAsInt(optImg, "image-data-offset"));
        Log.d("DSPTEST", "IMAGE_TARGET_HEIGHT = " + dsp.getPropertyAsInt(optImg, "image-target-height"));
        Log.d("DSPTEST", "IMAGE_TARGET_WIDTH = " + dsp.getPropertyAsInt(optImg, "image-target-width"));
        Log.d("DSPTEST", "IMAGE_TARGET_X_OFFSET = " + dsp.getPropertyAsInt(optImg, "image-target-x-offset"));
        Log.d("DSPTEST", "IMAGE_TARGET_Y_OFFSET = " + dsp.getPropertyAsInt(optImg, "image-target-y-offset"));
        Log.d("DSPTEST", "MEMORY_ADDRESS = " + dsp.getPropertyAsInt(optImg, "memory-address"));
        Log.d("DSPTEST", "MEMORY_SIZE = " + dsp.getPropertyAsInt(optImg, "memory-size"));
        Log.d("DSPTEST", ">>>>>>>>>>> LGImageUtil(memcpy) Input");
        dsp.release();
        memoryUtil.memoryCopyOpitimizedImageToDeviceBuffer(optImgAddr, devBufAddr, optImgSize, devBufSize, canvasWidth, width, height);
        return devBuf;
    }

    public void copyDeviceBufferToOptimizedImage(DeviceBuffer devBuf, OptimizedImage optImgOut) {
        MemoryUtil memoryUtil = new MemoryUtil();
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        int optImgAddr = dsp.getPropertyAsInt(optImgOut, "memory-address");
        int optImgSize = dsp.getPropertyAsInt(optImgOut, "memory-size");
        int canvasWidth = dsp.getPropertyAsInt(optImgOut, "image-canvas-width");
        int width = optImgOut.getWidth();
        int height = optImgOut.getHeight();
        int devBufAddr = dsp.getPropertyAsInt(devBuf, "memory-address");
        int devBufSize = dsp.getPropertyAsInt(devBuf, "memory-size");
        Log.d("DSPTEST", "LGImageUtil(memcpy) Input <<<<<<<<<<<");
        Log.d("DSPTEST", "IMAGE_CANVAS_HEIGHT = " + dsp.getPropertyAsInt(optImgOut, "image-canvas-height"));
        Log.d("DSPTEST", "IMAGE_CANVAS_WIDTH = " + dsp.getPropertyAsInt(optImgOut, "image-canvas-width"));
        Log.d("DSPTEST", "IMAGE_DATA_OFFSET = " + dsp.getPropertyAsInt(optImgOut, "image-data-offset"));
        Log.d("DSPTEST", "IMAGE_TARGET_HEIGHT = " + dsp.getPropertyAsInt(optImgOut, "image-target-height"));
        Log.d("DSPTEST", "IMAGE_TARGET_WIDTH = " + dsp.getPropertyAsInt(optImgOut, "image-target-width"));
        Log.d("DSPTEST", "IMAGE_TARGET_X_OFFSET = " + dsp.getPropertyAsInt(optImgOut, "image-target-x-offset"));
        Log.d("DSPTEST", "IMAGE_TARGET_Y_OFFSET = " + dsp.getPropertyAsInt(optImgOut, "image-target-y-offset"));
        Log.d("DSPTEST", "MEMORY_ADDRESS = " + dsp.getPropertyAsInt(optImgOut, "memory-address"));
        Log.d("DSPTEST", "MEMORY_SIZE = " + dsp.getPropertyAsInt(optImgOut, "memory-size"));
        Log.d("DSPTEST", ">>>>>>>>>>> LGImageUtil(memcpy) Input");
        dsp.release();
        memoryUtil.memoryCopyDeviceBufferToOpitimizedImage(devBufAddr, optImgAddr, devBufSize, optImgSize, canvasWidth, width, height);
    }

    public OptimizedImage getSFRImage(OptimizedImage optImg, boolean release) {
        int sfrWidth;
        int sfrHeight;
        int scaleHeight;
        int scaleWidth;
        int right;
        int bottom;
        OptimizedImage croppedImg = null;
        OptimizedImage scaledImg = null;
        int origWidth = optImg.getWidth();
        int origHeight = optImg.getHeight();
        Log.d(TAG, "crop: original width : " + origWidth + " height : " + origHeight);
        int aspect = getCensorAspectRatio();
        boolean isCropTopBottm = true;
        switch (aspect) {
            case 11:
                sfrWidth = AppRoot.USER_KEYCODE.WATER_HOUSING;
                sfrHeight = AppRoot.USER_KEYCODE.WATER_HOUSING;
                isCropTopBottm = false;
                break;
            case 32:
                sfrWidth = 1024;
                sfrHeight = SFR32Height;
                break;
            case 169:
                sfrWidth = 1024;
                sfrHeight = SFR169Height;
                break;
            default:
                sfrWidth = 1024;
                sfrHeight = SFRDefaultHeight;
                isCropTopBottm = false;
                break;
        }
        if (isCropTopBottm) {
            scaleWidth = sfrWidth;
            scaleHeight = ((((int) ((sfrWidth / origWidth) * origHeight)) + 1) / 2) * 2;
            if (scaleHeight < sfrHeight) {
                scaleHeight = sfrHeight;
            }
            sfrHeight = Math.min(sfrHeight, scaleHeight);
        } else {
            scaleHeight = sfrHeight;
            scaleWidth = ((((int) ((sfrHeight / origHeight) * origWidth)) + 2) / 4) * 4;
            if (scaleWidth < sfrWidth) {
                scaleWidth = sfrWidth;
            }
        }
        Log.d(TAG, "crop: temp width : " + scaleWidth + "  height: " + scaleHeight);
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(optImg, release);
        scaleFilter.setDestSize(scaleWidth, scaleHeight);
        if (scaleFilter.execute()) {
            scaledImg = scaleFilter.getOutput();
        }
        scaleFilter.release();
        int left = 0;
        int top = 0;
        if (isCropTopBottm) {
            right = sfrWidth;
            top = (scaleHeight - sfrHeight) / 2;
            bottom = top + sfrHeight;
        } else {
            left = (scaleWidth - sfrWidth) / 2;
            right = left + sfrWidth;
            bottom = sfrHeight;
        }
        Log.d(TAG, "crop: left " + left + ": top " + top + ": right " + right + ": bottom " + bottom);
        CropImageFilter cropImageFilter = new CropImageFilter();
        cropImageFilter.setSrcRect(left, top, right, bottom);
        cropImageFilter.setSource(scaledImg, true);
        boolean resultCrop = cropImageFilter.execute();
        if (resultCrop) {
            croppedImg = cropImageFilter.getOutput();
        }
        if (cropImageFilter != null) {
            cropImageFilter.clearSources();
            cropImageFilter.release();
        }
        return croppedImg;
    }

    public int getCensorAspectRatio() {
        String cameraAspectRatio = LGPictureSizeController.getInstance().getValue(PictureSizeController.MENU_ITEM_ID_ASPECT_RATIO);
        Log.d(TAG, "getCensorAspectRatio aspectRatio ::169");
        Log.d(TAG, "getCensorAspectRatio cameraAspectRatio::" + cameraAspectRatio);
        if (PictureSizeController.ASPECT_16_9.equalsIgnoreCase(cameraAspectRatio)) {
            return 169;
        }
        if (PictureSizeController.ASPECT_4_3.equalsIgnoreCase(cameraAspectRatio)) {
            return 43;
        }
        if (PictureSizeController.ASPECT_1_1.equalsIgnoreCase(cameraAspectRatio)) {
            return 11;
        }
        if (!PictureSizeController.ASPECT_3_2.equalsIgnoreCase(cameraAspectRatio)) {
            return 169;
        }
        return 32;
    }
}
