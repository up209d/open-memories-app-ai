package com.sony.imaging.app.photoretouch.common;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.StatFs;
import android.util.Log;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.photoretouch.playback.control.LCEControl;
import com.sony.imaging.app.photoretouch.playback.layout.brightness.BrightnessControlLayout;
import com.sony.imaging.app.photoretouch.playback.layout.contrast.ContrastControlLayout;
import com.sony.imaging.app.photoretouch.playback.layout.resize.ResizeLayout;
import com.sony.imaging.app.photoretouch.playback.layout.saturation.SaturationControlLayout;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.scalar.graphics.JpegExporter;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.FaceNRImageFilter;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.provider.AvindexStore;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.Vector;

/* loaded from: classes.dex */
public class ImageEditor {
    private static final String INH_1 = "INH_FACTOR_CONTENT_FULL_FOR_STILL";
    private static final String INH_2 = "INH_FACTOR_FOLDER_FULL_FOR_STILL";
    private static final String INH_3 = "INH_FACTOR_NEED_REPAIR_AVINDEX";
    private static final String INH_ID_RECORD_IMAGE = "INH_FEATURE_RECORD_IMAGE";
    private static final long SIZE_IMAGE_LARGE = 42551296;
    protected static SaveTask executeHandler;
    private static Runnable mExecutor;
    private static OptimizedImage mImage;
    private static int mOrientationInfo;
    private static int mPrevTotalCount;
    private static ResultReflection mResultReflection;
    public static OptimizedImage[] optimizedImages;
    private static final String TAG = ImageEditor.class.getSimpleName();
    private static Activity mAct = null;
    private static ContentResolver mResolver = null;
    private static boolean mIsSaved = true;
    private static Callback mCallback = null;
    public static int optImageCount = 0;
    public static int runnableCount = 0;
    public static int stableCount = 0;
    private static boolean isSpaceAvailableInMemoryCard = false;
    private static Handler handler = new Handler() { // from class: com.sony.imaging.app.photoretouch.common.ImageEditor.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            ImageEditor.changeSaveStatus(true);
        }
    };
    private static int mAspectRatio = 0;
    private static DSP mLceDsp = null;
    private static DSP mYuvToRGBDsp = null;
    private static LCEControl mLceControl = null;

    /* loaded from: classes.dex */
    public interface Callback {
        void onSaveStatusChanged();
    }

    /* loaded from: classes.dex */
    public interface SaveCallback {
        void onFail();

        void onSuccess();
    }

    static /* synthetic */ boolean access$400() {
        return isSavedCompletely();
    }

    public static void init(Activity act) {
        mAct = act;
        mResolver = act.getContentResolver();
    }

    public static void term() {
        if (mResultReflection != null) {
            mResultReflection.cancel();
            mResultReflection = null;
        }
        if (executeHandler != null) {
            executeHandler.immediateExecute();
            executeHandler = null;
        }
        setImage(null);
        mResolver = null;
        mAct = null;
        mCallback = null;
        optImageCount = 0;
    }

    public static boolean isSaved() {
        return mIsSaved;
    }

    public static void changeExifOrientation(int orientation) {
        String[] mId = AvindexStore.getExternalMediaIds();
        Uri uri = AvindexStore.Images.Media.getContentUri(mId[0]);
        AvindexStore.Images.waitAndUpdateDatabase(mResolver, mId[0]);
        Cursor cursor = mResolver.query(uri, null, null, null, null);
        long id = 0;
        if (cursor.moveToLast()) {
            id = cursor.getLong(cursor.getColumnIndex("_id"));
            cursor.close();
        }
        AvindexStore.Images.Media.rotateImage(mResolver, uri, id, orientation);
    }

    public static boolean isSpaceAvailableInMemoryCard() {
        return isSpaceAvailableInMemoryCard;
    }

    public static void setSpaceAvailableInMemoryCard(boolean isSpaceAvailableInMemoryCard2) {
        isSpaceAvailableInMemoryCard = isSpaceAvailableInMemoryCard2;
    }

    public static void updateSpaceAvailableInMemoryCard() {
        boolean available;
        AvailableInfo.update();
        String[] media = AvindexStore.getExternalMediaIds();
        boolean fullForStill = AvailableInfo.isFactor(INH_1, media[0]);
        boolean folderFullForStill = AvailableInfo.isFactor(INH_2, media[0]);
        boolean errorAvindex = AvailableInfo.isFactor(INH_3, media[0]);
        if (fullForStill || errorAvindex || folderFullForStill) {
            available = false;
        } else {
            try {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                long availableSize = availableBlocks * blockSize;
                Log.d(TAG, "==================Available Size=================" + availableSize);
                if (availableSize < SIZE_IMAGE_LARGE) {
                    available = false;
                } else {
                    available = true;
                }
            } catch (Exception e) {
                available = false;
            }
        }
        setSpaceAvailableInMemoryCard(available);
    }

    public static void updateSpaceAvailableAfterSaving() {
        updateSpaceAvailableInMemoryCard();
        if (true == isSpaceAvailableInMemoryCard()) {
            String[] media = AvindexStore.getExternalMediaIds();
            if (true == AvailableInfo.isInhibition(INH_ID_RECORD_IMAGE, media[0])) {
                setSpaceAvailableInMemoryCard(false);
            }
        }
    }

    public static void refreshIndexView() {
        ContentsManager mgr = ContentsManager.getInstance();
        AvindexStore.Images.waitAndUpdateDatabase(ContentsManager.getInstance().getContentResolver(), getMediaId());
        mgr.requeryData();
        mgr.moveToEntryPosition();
        updateSpaceAvailableInMemoryCard();
    }

    public static void setOnSaveStatusChanged(Callback c) {
        mCallback = c;
        if (c != null) {
            c.onSaveStatusChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void changeSaveStatus(boolean b) {
        if (mIsSaved != b) {
            mIsSaved = b;
            if (mCallback != null) {
                mCallback.onSaveStatusChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setPrevContentsTotalCount() {
        ContentsManager mgr = ContentsManager.getInstance();
        mPrevTotalCount = mgr.getContentsTotalCount();
        Log.d(TAG, "mPrevTotalCount = " + mPrevTotalCount);
    }

    private static boolean isSavedCompletely() {
        refreshIndexView();
        ContentsManager mgr = ContentsManager.getInstance();
        int currentTotalCount = mgr.getContentsTotalCount();
        return mPrevTotalCount != currentTotalCount;
    }

    /* loaded from: classes.dex */
    protected static class SaveTask implements MessageQueue.IdleHandler {
        private Callback mCallback;
        private int mOrientation;
        private SaveTask own = this;

        /* loaded from: classes.dex */
        public interface Callback {
            void onFinish(int i);
        }

        public SaveTask(int orientation, Callback callback) {
            this.mOrientation = orientation;
            this.mCallback = callback;
        }

        public void execute() {
            Looper.myQueue().addIdleHandler(this.own);
        }

        public void immediateExecute() {
            if (this.own != null) {
                Looper.myQueue().removeIdleHandler(this.own);
                queueIdle();
            }
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            this.own = null;
            if (ImageEditor.isSpaceAvailableInMemoryCard()) {
                ImageEditor.setPrevContentsTotalCount();
                JpegExporter exporter = new JpegExporter();
                JpegExporter.Options option = new JpegExporter.Options();
                option.quality = 2;
                Log.d(ImageEditor.TAG, "start encoding");
                exporter.encode(ImageEditor.mImage, ImageEditor.getMediaId(), option);
                exporter.release();
                if (!ImageEditor.access$400()) {
                    Log.d(ImageEditor.TAG, "end encoding NOT completely");
                    this.mCallback.onFinish(-1);
                } else {
                    switch (this.mOrientation) {
                        case 3:
                            ImageEditor.changeExifOrientation(1);
                            break;
                        case 6:
                            ImageEditor.changeExifOrientation(0);
                            break;
                        case 8:
                            ImageEditor.changeExifOrientation(2);
                            break;
                    }
                    Log.d(ImageEditor.TAG, "end encoding completely");
                    ImageEditor.handler.sendEmptyMessage(0);
                    this.mCallback.onFinish(0);
                }
            } else {
                this.mCallback.onFinish(-1);
            }
            return false;
        }
    }

    public static void saveEditedImage(int orientation, final SaveCallback callback) {
        executeHandler = new SaveTask(orientation, new SaveTask.Callback() { // from class: com.sony.imaging.app.photoretouch.common.ImageEditor.2
            @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.SaveTask.Callback
            public void onFinish(int err) {
                ImageEditor.executeHandler = null;
                if (err == 0) {
                    SaveCallback.this.onSuccess();
                } else {
                    SaveCallback.this.onFail();
                }
            }
        });
        executeHandler.execute();
    }

    public static String getMediaId() {
        String[] externalMedias = AvindexStore.getExternalMediaIds();
        return externalMedias[0];
    }

    public static OptimizedImage getEditFrame(OptimizedImage optimg, Rect cropRect, RotateImageFilter.ROTATION_DEGREE rotateDegree) {
        int optWidth = optimg.getWidth();
        int optHeight = optimg.getHeight();
        OptimizedImage cropImg = cropImageFilter(optimg, cropRect);
        releaseOptImage(optimg);
        OptimizedImage rotImg = rotateImage(cropImg, rotateDegree);
        releaseOptImage(cropImg);
        OptimizedImage scaleImg = null;
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(rotImg, true);
        scaleFilter.setDestSize(optWidth, optHeight);
        boolean isExecuted = scaleFilter.execute();
        if (isExecuted) {
            scaleImg = scaleFilter.getOutput();
        }
        scaleFilter.clearSources();
        scaleFilter.release();
        return scaleImg;
    }

    public static OptimizedImage scaleImageWithResizeParam(OptimizedImage optimizedImg, int resizeParam) {
        int width = 0;
        int height = 0;
        OptimizedImage out = null;
        if (2 == resizeParam) {
            if (getAspectRatio() == 0) {
                Log.d(TAG, "====32....M");
                width = ResizeLayout.M32.width;
                height = ResizeLayout.M32.height;
            } else if (1 == getAspectRatio()) {
                Log.d(TAG, "====169....M");
                width = ResizeLayout.M169.width;
                height = ResizeLayout.M169.height;
            } else if (2 == getAspectRatio()) {
                Log.d(TAG, "====43....M");
                width = ResizeLayout.M43.width;
                height = ResizeLayout.M43.height;
            } else if (3 == getAspectRatio()) {
                Log.d(TAG, "====11....M");
                width = ResizeLayout.M11.width;
                height = ResizeLayout.M11.height;
            }
        } else if (1 == resizeParam) {
            if (getAspectRatio() == 0) {
                Log.d(TAG, "====32....S");
                width = ResizeLayout.S32.width;
                height = ResizeLayout.S32.height;
            } else if (1 == getAspectRatio()) {
                Log.d(TAG, "====169....S");
                width = ResizeLayout.S169.width;
                height = ResizeLayout.S169.height;
            } else if (2 == getAspectRatio()) {
                Log.d(TAG, "====43....M");
                width = ResizeLayout.S43.width;
                height = ResizeLayout.S43.height;
            } else if (3 == getAspectRatio()) {
                Log.d(TAG, "====11....M");
                width = ResizeLayout.S11.width;
                height = ResizeLayout.S11.height;
            }
            Log.d(TAG, "start filter execute");
        } else {
            Log.d(TAG, "resize parameter is not set");
        }
        Log.d(TAG, "===Resize from width" + optimizedImg.getWidth());
        Log.d(TAG, "===Resize from Height" + optimizedImg.getHeight());
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(optimizedImg, false);
        scaleFilter.setDestSize(width, height);
        boolean isExecuted = scaleFilter.execute();
        if (isExecuted) {
            Log.d(TAG, "===resize..scaleImageWithResizeParam...filter executed");
            out = scaleFilter.getOutput();
            optImageCount++;
            Log.d(TAG, "===Resize to width" + width);
            Log.d(TAG, "===Resize to Height" + height);
            Log.d(TAG, "===Width of Output" + out.getWidth());
            Log.d(TAG, "===Height of Output" + out.getHeight());
        } else {
            Log.e(TAG, "===resize..scaleImageWithResizeParam...filter not executed");
        }
        scaleFilter.clearSources();
        scaleFilter.release();
        return out;
    }

    public static OptimizedImage makeLceImage(Layout layout, int controlValue) {
        LCEControl lceControl = useLceControl();
        if (layout instanceof ContrastControlLayout) {
            lceControl.setContrast(controlValue);
        } else if (layout instanceof BrightnessControlLayout) {
            lceControl.setBrightness(controlValue);
        } else if (layout instanceof SaturationControlLayout) {
            lceControl.setSaturation(controlValue);
        }
        Log.d(TAG, "======makeLceImage +");
        OptimizedImage output = getControlImage(getImage(), lceControl);
        Log.d(TAG, "======makeLceImage -");
        releaseLceControl();
        return output;
    }

    public static OptimizedImage getControlImage(OptimizedImage optimizedImage, LCEControl lceControl) {
        OptimizedImage optImage = null;
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(optimizedImage, false);
        scaleFilter.setDestSize(optimizedImage.getWidth(), optimizedImage.getHeight());
        boolean isExecuted = scaleFilter.execute();
        if (isExecuted) {
            Log.e(TAG, "Scale Image filter executed");
            optImage = scaleFilter.getOutput();
            optImageCount++;
        } else {
            Log.e(TAG, "Scale Image filter failed to execute");
        }
        scaleFilter.clearSources();
        scaleFilter.release();
        lceControl.createYUVImageUsingDSP(optimizedImage, optImage);
        return optImage;
    }

    public static OptimizedImage getImage() {
        return mImage;
    }

    public static void setImage(OptimizedImage image) {
        if (mImage != null) {
            if (mImage.isValid()) {
                optImageCount--;
            }
            mImage.release();
            changeSaveStatus(false);
        }
        mImage = image;
    }

    public static void clearImage() {
        changeSaveStatus(true);
        if (mImage != null) {
            if (mImage.isValid()) {
                optImageCount--;
            }
            mImage.release();
            mImage = null;
        }
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

    public static int getAspectRatio() {
        return mAspectRatio;
    }

    public static int getOrientationInfo() {
        return mOrientationInfo;
    }

    public static void setOrientationInfo(int orientationInfo) {
        mOrientationInfo = orientationInfo;
    }

    public static OptimizedImage cropImageFilter(OptimizedImage optimizedImage, Rect cropRect) {
        CropImageFilter filter = new CropImageFilter();
        filter.setSrcRect(cropRect);
        filter.setSource(optimizedImage, false);
        boolean isExec = filter.execute();
        OptimizedImage cropImg = null;
        if (isExec) {
            cropImg = filter.getOutput();
            optImageCount++;
        }
        filter.clearSources();
        filter.release();
        return cropImg;
    }

    public static OptimizedImage rotateImageWRTOrientation(OptimizedImage optImg) {
        if (6 == getOrientationInfo()) {
            OptimizedImage out = rotateImage(optImg, RotateImageFilter.ROTATION_DEGREE.DEGREE_90);
            return out;
        }
        if (8 == getOrientationInfo()) {
            OptimizedImage out2 = rotateImage(optImg, RotateImageFilter.ROTATION_DEGREE.DEGREE_270);
            return out2;
        }
        if (3 != getOrientationInfo()) {
            return null;
        }
        OptimizedImage out3 = rotateImage(optImg, RotateImageFilter.ROTATION_DEGREE.DEGREE_180);
        return out3;
    }

    public static OptimizedImage rotateImage(OptimizedImage optimizedImage, RotateImageFilter.ROTATION_DEGREE degree) {
        RotateImageFilter rotate = new RotateImageFilter();
        rotate.setTrimMode(3);
        rotate.setRotation(degree);
        rotate.setSource(optimizedImage, false);
        boolean isRotateExecuted = rotate.execute();
        OptimizedImage optImage = null;
        if (isRotateExecuted) {
            Log.d(TAG, "===rotate image filter executed");
            optImage = rotate.getOutput();
            optImageCount++;
        } else {
            Log.e(TAG, "===rotate image filter is not executed");
        }
        rotate.clearSources();
        rotate.release();
        return optImage;
    }

    public static OptimizedImage rotateImage(OptimizedImage optimizedImage, double angle) {
        RotateImageFilter rotate = new RotateImageFilter();
        rotate.setTrimMode(3);
        rotate.setRotation((-1.0d) * angle);
        rotate.setSource(optimizedImage, false);
        boolean isRotateExecuted = rotate.execute();
        OptimizedImage optImage = null;
        if (isRotateExecuted) {
            Log.d(TAG, "===horizontal adjustment rotate image filter executed");
            optImage = rotate.getOutput();
            optImageCount++;
        } else {
            Log.e(TAG, "===horizontal adjustment rotate image filter is not executed");
        }
        rotate.clearSources();
        rotate.release();
        return optImage;
    }

    public static OptimizedImage applySkinLevel(OptimizedImage optimizedImage, Vector<Rect> faceList, int iso, int level) {
        OptimizedImage mOptImageNR;
        FaceNRImageFilter nRFilter = new FaceNRImageFilter();
        nRFilter.setFaceList(faceList);
        nRFilter.setISOValue(iso);
        switch (level) {
            case 0:
                nRFilter.setNRLevel(5);
                break;
            case 1:
                nRFilter.setNRLevel(4);
                break;
            case 2:
                nRFilter.setNRLevel(3);
                break;
            case 3:
                nRFilter.setNRLevel(2);
                break;
            case 4:
                nRFilter.setNRLevel(1);
                break;
        }
        nRFilter.setSource(optimizedImage, false);
        boolean isExecuted = nRFilter.execute();
        if (isExecuted) {
            Log.e(TAG, "soft skin...NRImage filter executed in Saving Image");
            mOptImageNR = nRFilter.getOutput();
            optimizedImage.release();
        } else {
            Log.e(TAG, "soft skin...NRImage filter not executed in Saving Image");
            mOptImageNR = optimizedImage;
        }
        nRFilter.clearSources();
        nRFilter.release();
        return mOptImageNR;
    }

    public static void releaseOptImage(OptimizedImage optImage) {
        if (optImage != null && optImage.isValid()) {
            optImageCount--;
            optImage.release();
        }
    }

    public static DSP useLceDSP() {
        createLceDsp();
        return mLceDsp;
    }

    public static void releaseLceDSP() {
        if (mLceDsp != null) {
            Log.d(TAG, "===releaseLceDSPLibrary +..mLceDsp= " + mLceDsp);
            mLceDsp.clearProgram();
            mLceDsp.release();
            mLceDsp = null;
            Log.d(TAG, "===releaseLceDSPLibrary -");
        }
    }

    protected static void createLceDsp() {
        String filePath;
        if (mLceDsp == null) {
            mLceDsp = DSP.createProcessor("sony-di-dsp");
            String version = ScalarProperties.getString("version.platform");
            int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
            if (pfMajorVersion == 1) {
                filePath = "/android/data/lib/" + mAct.getPackageName() + "/lib/libsa_lce.so";
            } else {
                filePath = "/android/data/lib/" + mAct.getPackageName() + "/lib/libsa_lce_musashi.so";
            }
            Log.i(TAG, "lce path" + filePath);
            mLceDsp.setProgram(filePath);
        }
    }

    public static DSP useYuvToRGBDsp() {
        createYuvToRGBDsp();
        return mYuvToRGBDsp;
    }

    public static void releaseYUVDSP() {
        if (mYuvToRGBDsp != null) {
            Log.d(TAG, "===releaseYUVDSPLibrary +...mYuvToRGBDsp= " + mYuvToRGBDsp);
            mYuvToRGBDsp.clearProgram();
            mYuvToRGBDsp.release();
            mYuvToRGBDsp = null;
            Log.d(TAG, "===releaseYUVDSPLibrary -");
        }
    }

    protected static void createYuvToRGBDsp() {
        String filePath;
        if (mYuvToRGBDsp == null) {
            mYuvToRGBDsp = DSP.createProcessor("sony-di-dsp");
            String version = ScalarProperties.getString("version.platform");
            int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
            if (pfMajorVersion == 1) {
                filePath = "/android/data/lib/" + mAct.getPackageName() + "/lib/libsa_pack_yuv2rgb.so";
            } else {
                filePath = "/android/data/lib/" + mAct.getPackageName() + "/lib/libsa_yuv2rgb_musashi_03RGBA.so";
            }
            Log.i(TAG, "yuv2rgb path" + filePath);
            mYuvToRGBDsp.setProgram(filePath);
        }
    }

    public static LCEControl useLceControl() {
        if (mLceControl == null) {
            DSP lce = useLceDSP();
            DSP yuv = useYuvToRGBDsp();
            mLceControl = new LCEControl(lce, yuv);
            mLceControl.createYuvToRgbConversionInstance(yuv);
        }
        return mLceControl;
    }

    public static void releaseLceControl() {
        if (mLceControl != null) {
            releaseLceDSP();
            releaseYUVDSP();
            mLceControl.releaseLCEResources();
            mLceControl = null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ResultReflection {
        Runnable r;

        protected abstract Runnable getExecutor();

        protected void execute(OptimizedImage... images) {
            ImageEditor.optimizedImages = images;
            this.r = getExecutor();
            ImageEditor.handler.post(this.r);
        }

        protected void cancel() {
            if (ImageEditor.optimizedImages != null) {
                ImageEditor.optimizedImages[0].release();
                ImageEditor.optimizedImages = null;
                ImageEditor.releaseLceControl();
            }
            ImageEditor.handler.removeCallbacks(this.r);
        }
    }

    public static void executeResultReflection(ResultReflection rr, OptimizedImage... images) {
        mResultReflection = rr;
        rr.execute(images);
    }
}
