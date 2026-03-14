package com.sony.imaging.app.portraitbeauty.common;

import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.pbsa.SA_PortraitLighting;
import com.sony.imaging.app.pbsa.SA_SoftFocus;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.graphics.ImageAnalyzer;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.FaceNRImageFilter;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.Vector;

/* loaded from: classes.dex */
public class PortraitBeautySAWrapper {
    private static SA_PortraitLighting mSA_pl = null;
    private static SA_SoftFocus mSA_sftfcs = null;
    private static PortraitBeautySAWrapper instance = new PortraitBeautySAWrapper();
    private static int RESOLUTION_16M = 16;
    private static int RESOLUTION_20M = 20;
    private static int RESOLUTION_24M = 24;
    private static int RESOLUTION_36M = 36;
    private static int IMAGERSIZE_16M = 0;
    private static int IMAGERSIZE_20M = 1;
    private static int IMAGERSIZE_24M = 2;
    private static int IMAGERSIZE_36M = 3;
    private static int IMAGESIZE_XGA = 4;
    private final String TAG = AppLog.getClassName();
    private OptimizedImage mInputImage = null;
    private OptimizedImage mOutputImage = null;
    private ImageAnalyzer.AnalyzedFace[] mFaces = null;
    private int mFaceNumbers = 0;
    private final int MAX_FACES = 8;
    private Pair<Integer, Integer> mFocusPoint = null;
    private int mFocusPointIndex = 0;
    private boolean mIsMediaAvailable = false;

    private PortraitBeautySAWrapper() {
    }

    public static PortraitBeautySAWrapper getInstance() {
        return instance;
    }

    public void initialize() {
        mSA_pl = new SA_PortraitLighting(AppContext.getAppContext().getPackageName());
        mSA_sftfcs = new SA_SoftFocus(AppContext.getAppContext().getPackageName());
        mSA_pl.open();
        mSA_sftfcs.open();
    }

    public void terminate() {
        if (this.mInputImage != null) {
            this.mInputImage = null;
        }
        if (this.mOutputImage != null) {
            this.mOutputImage = null;
        }
        if (mSA_pl != null) {
            mSA_pl.close();
            mSA_pl = null;
        }
        if (mSA_sftfcs != null) {
            mSA_sftfcs.close();
            mSA_sftfcs = null;
        }
        if (this.mFaces != null) {
            int len = this.mFaces.length;
            for (int i = 0; i < len; i++) {
                this.mFaces[i] = null;
            }
            this.mFaces = null;
        }
    }

    public void setOptimizeImage(OptimizedImage originalImg) {
        this.mOutputImage = originalImg;
        if (this.mOutputImage != null) {
            mSA_sftfcs.setSAWorkSize(this.mOutputImage.getWidth(), this.mOutputImage.getHeight());
        }
    }

    public void setOptimizeImage(OptimizedImage originalImg, OptimizedImage effectedImg) {
        this.mInputImage = originalImg;
        this.mOutputImage = effectedImg;
        if (this.mInputImage != null) {
            mSA_sftfcs.setSAWorkSize(this.mInputImage.getWidth(), this.mInputImage.getHeight());
        }
    }

    public OptimizedImage executeSA() {
        int isoValue = getISOValue();
        Log.d("hcl....", "mSA_pl:" + mSA_pl);
        Log.d("hcl....", "mSA_sftfcs:" + mSA_sftfcs);
        mSA_pl.initialize();
        mSA_sftfcs.initialize();
        if (this.mFaces != null) {
            mSA_pl.setFace(this.mFaceNumbers, this.mFaces);
        }
        mSA_pl.setIsoValue(isoValue);
        setPortraitLightingEffectLevels();
        if (mSA_sftfcs.execute(this.mOutputImage, this.mOutputImage) && mSA_pl.execute(this.mOutputImage)) {
            AppLog.info(this.TAG, "PortraitBeautySAWrapper.execute retunred true");
        }
        return this.mOutputImage;
    }

    public OptimizedImage executeSA_onlyPortraitLighting() {
        int isoValue = getISOValue();
        Log.d("hcl....", "mSA_pl:" + mSA_pl);
        mSA_pl.initialize();
        mSA_sftfcs.initialize();
        if (this.mFaces != null) {
            mSA_pl.setFace(this.mFaceNumbers, this.mFaces);
        }
        mSA_pl.setIsoValue(isoValue);
        setPortraitLightingEffectLevels();
        if (mSA_pl.execute(this.mOutputImage)) {
            AppLog.info(this.TAG, "PortraitBeautySAWrapper.execute retunred true");
        }
        return this.mOutputImage;
    }

    private int getISOValue() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        int isoValue = ((CameraEx.ParametersModifier) params.second).getISOSensitivity();
        if (isoValue == 0) {
            isoValue = CameraSetting.getInstance().getISOSensitivityAuto();
        }
        AppLog.info(this.TAG, "ISO value: " + isoValue);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return isoValue;
    }

    private boolean getFocusPoint() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        this.mFocusPoint = FocusAreaController.getInstance().getFocusPoint();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return ((CameraEx.ParametersModifier) params.second).getFocusAreaMode().equals(FocusAreaController.FLEX);
    }

    private boolean getFocusPointIndex() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params = CameraSetting.getInstance().getParameters();
        this.mFocusPointIndex = ((CameraEx.ParametersModifier) params.second).getFocusPointIndex();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return ((CameraEx.ParametersModifier) params.second).getFocusAreaMode().equals(FocusAreaController.LOCAL);
    }

    public int getFaces(OptimizedImage optimizedImage) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ImageAnalyzer imgAnalyzer = new ImageAnalyzer();
        this.mFaces = new ImageAnalyzer.AnalyzedFace[8];
        this.mFaceNumbers = imgAnalyzer.findFaces(optimizedImage, this.mFaces);
        imgAnalyzer.release();
        AppLog.info(this.TAG, "Number of Faces:" + this.mFaceNumbers);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mFaceNumbers;
    }

    private int getSelectedLevel() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int levelValue = -1;
        try {
            int whiteSkinValue = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_LEVEL_OF_WHITE_SKIN, 4);
            AppLog.info(this.TAG, "IMDLAPP8-902 Face brightening Skin level = " + whiteSkinValue);
            if (whiteSkinValue > 7) {
                whiteSkinValue = 7;
            }
            if (whiteSkinValue < 1) {
                whiteSkinValue = 1;
            }
            levelValue = whiteSkinValue;
        } catch (NumberFormatException e) {
            AppLog.error(this.TAG, e.toString());
        }
        AppLog.info(this.TAG, "Level Value: " + levelValue);
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return levelValue;
    }

    private void setPortraitLightingEffectLevels() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int level = getSelectedLevel() - 1;
        mSA_pl.setLevel(level);
        AppLog.info(this.TAG, "FACE BRIGHTING Level for mSA_pl is : " + level);
        mSA_sftfcs.setLevel(level);
        AppLog.info(this.TAG, "SOFT Focus Level for mSA_sftfcs is : " + level);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    public OptimizedImage setSoftSkinEffect(OptimizedImage optimizedImage, int faceNumbers, int softSkinLevel, int isoValue, boolean isOrgImgReqToRelease) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, "faceNumbers: " + faceNumbers + " softSkinLevel: " + softSkinLevel + " isoValue: " + isoValue);
        FaceNRImageFilter faceNRImageFilter = new FaceNRImageFilter();
        faceNRImageFilter.setSource(optimizedImage, isOrgImgReqToRelease);
        Vector<Rect> faceList = new Vector<>();
        for (int i = 0; i < faceNumbers; i++) {
            faceList.add(this.mFaces[i].face.rect);
        }
        faceNRImageFilter.setFaceList(faceList);
        faceNRImageFilter.setISOValue(isoValue);
        faceNRImageFilter.setNRLevel(softSkinLevel);
        boolean isExecuted = faceNRImageFilter.execute();
        AppLog.info(this.TAG, "Is Face NR Image Filter executed: " + isExecuted);
        OptimizedImage optImage = null;
        if (isExecuted) {
            optImage = faceNRImageFilter.getOutput();
        } else {
            AppLog.info(this.TAG, "Face NR Image Filter is not executed");
        }
        if (faceList != null) {
            faceList.clear();
        }
        if (faceNRImageFilter != null) {
            faceNRImageFilter.clearSources();
            faceNRImageFilter.release();
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return optImage;
    }

    private int getSoftSkinLevel() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, "level: -1");
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return -1;
    }

    public void setSize(boolean shooting) {
        int num;
        int rowImageSize = ScalarProperties.getInt("mem.rawimage.size.in.mega.pixel");
        if (shooting) {
            if (rowImageSize <= RESOLUTION_16M) {
                num = IMAGERSIZE_16M;
            } else if (RESOLUTION_16M < rowImageSize && rowImageSize <= RESOLUTION_20M) {
                num = IMAGERSIZE_20M;
            } else if (RESOLUTION_20M < rowImageSize && rowImageSize <= RESOLUTION_24M) {
                num = IMAGERSIZE_24M;
            } else {
                num = IMAGERSIZE_36M;
            }
        } else {
            num = IMAGESIZE_XGA;
        }
        mSA_sftfcs.setSize(num);
    }
}
