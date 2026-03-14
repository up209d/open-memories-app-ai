package com.sony.imaging.app.portraitbeauty.playback.catchlight.layout;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsIdentifier;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.lib_sa_rectcopy_fillcolor_imagefilter.Sa_RectCopy_FillColor_ImageFilter;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.common.AppContext;
import com.sony.imaging.app.portraitbeauty.common.AppLog;
import com.sony.imaging.app.portraitbeauty.common.EVFOffLayout;
import com.sony.imaging.app.portraitbeauty.common.ImageEditor;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyBackUpKey;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyConstants;
import com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.effect.CatchLightLayoutEyeFrame;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.effect.DrawFaceFrameView;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.state.ZoomModeState;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.imaging.app.portraitbeauty.shooting.widget.EachButton;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.Environment;
import com.sony.scalar.graphics.ImageAnalyzer;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.meta.FaceDetailScalarA;
import com.sony.scalar.widget.OptimizedImageView;
import java.util.LinkedList;
import java.util.Vector;

/* loaded from: classes.dex */
public class CatchLightPlayBackLayout extends EVFOffLayout implements PortraitBeautyUtil.SaveCallback, CatchLightEffectChangeListener {
    public static final String FLG_ALWAYS__S_SIZE = "FLG_ALWAYS__S_SIZE";
    public static final String FLG_ALWAYS__S_SIZE_3VS2_RATIO = "FLG_ALWAYS__S_SIZE_3VS2_RATIO";
    public static final String FLG_ALWAYS__S_SIZE_3VS2_RATIO_LANDSCAPE = "FLG_ALWAYS__S_SIZE_3VS2_RATIO_LANDSCAPE";
    public static final String FLG_ALWAYS__S_SIZE_LANDSCAPE = "FLG_ALWAYS__S_SIZE_LANDSCAPE";
    public static final String FLG_USE_ORIGINAL_IMAGE = "FLG_USE_ORIGINAL_IMAGE";
    public static ImageView mCatchlight_focus_arrow = null;
    public static final int mLayoutParams_faceZoom__height = 480;
    public static final int mLayoutParams_faceZoom__leftMargin = 40;
    public static final int mLayoutParams_faceZoom__topMargin = 0;
    public static final int mLayoutParams_faceZoom__width = 500;
    public static final int mOutput_LeftEye_left = 42;
    public static final int mOutput_RightEye_left = 274;
    public static final int mOutput_eye_height = 122;
    public static final int mOutput_eye_top = 234;
    public static final int mOutput_eye_width = 232;
    public static boolean reImaging_running;
    private int mAngle;
    private int mCurrentX;
    private int mCurrentY;
    private int mFaceNum;
    private int mIsoInfo;
    private ViewGroup.MarginLayoutParams mLightArrowsPos;
    private int mModeSelected;
    public static String IMAGE_SETTING = "FLG_ALWAYS__S_SIZE";
    public static OptimizedImage sSelectedOptimizedImage = null;
    public static boolean bTransitToShooting = false;
    public static boolean isEVFShown = false;
    public static boolean isHDMIShown = false;
    public static boolean isreturningfromPreview = false;
    public static int comingFromEVF = 0;
    public static int switchToEVFFromPanel = 0;
    public static boolean mIsLeftEyeSelected = true;
    public static boolean mCatchLightLayoutEyeFrame_draw = true;
    public static boolean mCatchLightEyeFrame_draw_black = true;
    public static int mPatternBriteness = 192;
    public static int mGauzeCurlevel = 1;
    public static int mMaxGauzebarLevel = 12;
    private final String TAG = AppLog.getClassName();
    private View mCurrentView = null;
    private FooterGuide mFooterGuide = null;
    private String imageAspectRatio = null;
    private String imageSize = null;
    private TextView mTxtVwTitle = null;
    double mFace_show_width_ratio = 3.0d;
    final int LIGHT_ARROW_VISIBLE_MOMENT_INITIAL = mLayoutParams_faceZoom__width;
    final int LIGHT_ARROW_VISIBLE_MOMENT_NORMAL = PortraitBeautyConstants.THREE_SECOND_MILLIS;
    Sa_RectCopy_FillColor_ImageFilter mSA_RectCopy = null;
    private EachButton mLeftEyeBtn = null;
    private EachButton mRightEyeBtn = null;
    public CatchLightLayoutEyeFrame mCatchLightLayoutEyeFrame = null;
    private int mFaceSelection = 0;
    private int mLightPatternSelection = 0;
    private OptimizedImage mOptImageOriginal = null;
    private int mOriginalWidth = 0;
    private int mOriginalHeight = 0;
    private OptimizedImage mOptImageCL = null;
    private OptimizedImage mOptImageCropedToShow = null;
    private OptimizedImage mOptImageEyepat = null;
    private OptimizedImageView mCatchLightImageView = null;
    private RelativeLayout.LayoutParams mCatchLightImageLayoutParam = null;
    private DrawFaceFrameView[] mDrawView = null;
    private DrawFaceFrameView[] mDrawViewL = null;
    private DrawFaceFrameView[] mDrawViewR = null;
    private ImageAnalyzer.AnalyzedFace[] mFaces = null;
    private Rect[] mLeftEyeRects = null;
    private Rect[] mRightEyeRects = null;
    private Point[] mLeftEyePoints = null;
    private Point[] mRightEyePoints = null;
    private int[] mLeftEyeSizes = null;
    private int[] mRightEyeSizes = null;
    private int[] mLeftEyeUnits = null;
    private int[] mRightEyeUnits = null;
    private Rect[] mFaceRects_org = null;
    private Rect[] mFaceRects_crop = null;
    private Rect[] mFaceRects_rotated = null;
    private int[] mGauzebarUnit = null;
    public int[] sGuzebarCurrentLevel = null;
    RelativeLayout.LayoutParams layoutParams_fullImage = new RelativeLayout.LayoutParams(-2, -2);
    RelativeLayout.LayoutParams layoutParams_faceZoom = new RelativeLayout.LayoutParams(-2, -2);
    private FrameLayout mFrameLayout = null;
    private FrameLayout mFrameLayoutEye = null;
    public int mTotalGauzeLengthNum = 0;
    private ContentsManager mContentManager = null;
    private ContentInfo mContentInfo = null;
    private int mColorSelected = Color.parseColor("#FFDD8800");
    private int mColorUnSelected = Color.parseColor("#CCBBBBBB");
    private LinkedList<Bitmap> mLeftEyeBitmapList = null;
    private LinkedList<Bitmap> mRightEyeBitmapList = null;
    private LinkedList<Bitmap> mLeftEyeBitmapListOrg = null;
    private LinkedList<Bitmap> mRightEyeBitmapListOrg = null;
    int gc_execute_counter = 0;
    int gc_execute_timing = 5;
    int scaleFactor = 1;
    private Handler handler = new Handler();
    private final Runnable delayFunc = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout.1
        @Override // java.lang.Runnable
        public void run() {
            Log.d(CatchLightPlayBackLayout.this.TAG, "mCatchlight_focus_arrow.setVisibility(View.VISIBLE)");
        }
    };
    Handler myHandler = null;
    ReImagingRunnable reImagingTask = null;

    /* loaded from: classes.dex */
    public interface SaveCallback {
        void onFail();

        void onSuccess();
    }

    public native void copyYuv(int[] iArr, int[] iArr2, int[] iArr3);

    public native void setLightPattern(int[] iArr, int[] iArr2, int[] iArr3);

    public native void umekomiYuv(int[] iArr, int[] iArr2, int[] iArr3);

    static {
        System.loadLibrary("MyFilterYuv_Jni");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Mode {
        OFF(1),
        MODE1_SELECT_TARGET_FACES(4),
        MODE2_CL_ADJUST(5),
        MODE3_CL_ADJUST_CL_INVISIBLE(6),
        MODE4_CHECK_WHOLE_IMAGE(7),
        MODE5_CHECK_WHOLE_IMAGE_CL_INVISIBLE(8),
        DEFAULT(0);

        private int code;

        Mode(int c) {
            this.code = c;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = obtainViewFromPool(R.layout.catchlight_playback_layout);
        }
        initializeView();
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    private void releaseMemory() {
        deInitializeReImagingTask();
        if (this.mCatchLightImageView != null) {
            this.mCatchLightImageView.release();
            this.mCatchLightImageView = null;
        }
        this.mCatchLightImageLayoutParam = null;
        for (int i = 0; i < this.mFaceNum; i++) {
            this.mFrameLayout.removeView(this.mDrawView[i]);
            this.mDrawView[i] = null;
            this.mFrameLayout.removeView(this.mDrawViewL[i]);
            this.mDrawViewL[i] = null;
            this.mFrameLayout.removeView(this.mDrawViewR[i]);
            this.mDrawViewR[i] = null;
        }
        if (mCatchlight_focus_arrow != null) {
            mCatchlight_focus_arrow = null;
        }
        if (this.mFaces != null) {
            int len = this.mFaces.length;
            for (int i2 = 0; i2 < len; i2++) {
                this.mFaces[i2] = null;
            }
        }
        if (this.mLeftEyeRects != null) {
            int len2 = this.mLeftEyeRects.length;
            for (int i3 = 0; i3 < len2; i3++) {
                this.mLeftEyeRects[i3] = null;
            }
        }
        if (this.mRightEyeRects != null) {
            int len3 = this.mRightEyeRects.length;
            for (int i4 = 0; i4 < len3; i4++) {
                this.mRightEyeRects[i4] = null;
            }
        }
        releaseAllBitmap(this.mLeftEyeBitmapList);
        releaseAllBitmap(this.mRightEyeBitmapList);
        releaseAllBitmap(this.mLeftEyeBitmapListOrg);
        releaseAllBitmap(this.mRightEyeBitmapListOrg);
        ImageEditor.releaseOptImage(sSelectedOptimizedImage);
        if (this.mSA_RectCopy != null) {
            this.mSA_RectCopy.releaseResources();
            this.mSA_RectCopy = null;
        }
        ImageEditor.releaseOptImage(this.mOptImageOriginal);
        ImageEditor.releaseOptImage(this.mOptImageCL);
        ImageEditor.releaseOptImage(this.mOptImageCropedToShow);
        ImageEditor.releaseOptImage(this.mOptImageEyepat);
        if (this.mCatchLightImageView != null) {
            this.mCatchLightImageView.release();
        }
    }

    private void initializeView() {
        releaseMemory();
        this.mCatchLightImageView = this.mCurrentView.findViewById(R.id.opt_img_view);
        this.mCatchLightImageLayoutParam = (RelativeLayout.LayoutParams) this.mCatchLightImageView.getLayoutParams();
        this.mLeftEyeBtn = (EachButton) this.mCurrentView.findViewById(R.id.btn_left_eye);
        this.mRightEyeBtn = (EachButton) this.mCurrentView.findViewById(R.id.btn_right_eye);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        mCatchlight_focus_arrow = (ImageView) this.mCurrentView.findViewById(R.id.catchlight_focus_arrows);
        if (mCatchlight_focus_arrow != null) {
            mCatchlight_focus_arrow.setVisibility(4);
        }
        this.mLightArrowsPos = (ViewGroup.MarginLayoutParams) mCatchlight_focus_arrow.getLayoutParams();
        getOptimizedImage();
        if (this.mOptImageOriginal != null) {
            this.mOptImageOriginal = ImageEditor.getImage();
            this.mOriginalWidth = this.mOptImageOriginal.getWidth();
            this.mOriginalHeight = this.mOptImageOriginal.getHeight();
            PortraitBeautyUtil.retrieveAspectRatio(this.mOptImageOriginal.getWidth(), this.mOptImageOriginal.getHeight());
            AppLog.info(this.TAG, "resizeOriginalImage_ForAB() returnd original image is: " + this.mOptImageOriginal);
            this.mContentManager = ContentsManager.getInstance();
            this.mContentInfo = this.mContentManager.getContentInfo(this.mContentManager.getContentsId());
            this.mAngle = this.mContentManager.getContentInfo(this.mContentManager.getContentsId()).getInt("Orientation");
            this.mFaces = new ImageAnalyzer.AnalyzedFace[8];
            ImageAnalyzer imgAnalyzer = new ImageAnalyzer();
            Log.d(this.TAG, "fineFaces in");
            this.mFaceNum = imgAnalyzer.findFaces(this.mOptImageOriginal, this.mFaces);
            Log.i(this.TAG, "facenum = " + this.mFaceNum);
            Log.d(this.TAG, "fineFaces out");
            imgAnalyzer.release();
            switch (this.mContentManager.getContentInfo(this.mContentManager.getContentsId()).getInt("Orientation")) {
                case 3:
                    this.mOptImageOriginal = getRotateImage(this.mOptImageOriginal, RotateImageFilter.ROTATION_DEGREE.DEGREE_180);
                    break;
                case 4:
                case 5:
                case 7:
                default:
                    Log.e(this.TAG, "==== error at rotating");
                    break;
                case 6:
                    this.mOptImageOriginal = getRotateImage(this.mOptImageOriginal, RotateImageFilter.ROTATION_DEGREE.DEGREE_90);
                    break;
                case 8:
                    this.mOptImageOriginal = getRotateImage(this.mOptImageOriginal, RotateImageFilter.ROTATION_DEGREE.DEGREE_270);
                    break;
            }
            this.mIsoInfo = this.mContentInfo.getInt("ISOSpeedRatings");
            this.mOptImageCL = getCopyImage2(this.mOptImageOriginal);
            Rect tmp = new Rect();
            tmp.left = 0;
            tmp.top = 0;
            tmp.right = 80;
            tmp.bottom = 80;
            this.mOptImageEyepat = getCropImage(this.mOptImageCL, tmp);
            this.mLeftEyeRects = new Rect[8];
            this.mRightEyeRects = new Rect[8];
            this.mLeftEyePoints = new Point[8];
            this.mRightEyePoints = new Point[8];
            this.mLeftEyeSizes = new int[8];
            this.mRightEyeSizes = new int[8];
            this.mLeftEyeUnits = new int[8];
            this.mRightEyeUnits = new int[8];
            this.mFaceRects_rotated = new Rect[8];
            this.mFaceRects_org = new Rect[8];
            this.mFaceRects_crop = new Rect[8];
            for (int i = 0; i < 8; i++) {
                this.mLeftEyeRects[i] = new Rect();
                this.mRightEyeRects[i] = new Rect();
                this.mLeftEyePoints[i] = new Point();
                this.mRightEyePoints[i] = new Point();
            }
            this.mGauzebarUnit = new int[8];
            this.sGuzebarCurrentLevel = new int[8];
            this.mFrameLayout = (FrameLayout) this.mCurrentView.findViewById(R.id.frameLayout);
            this.mFrameLayoutEye = (FrameLayout) this.mCurrentView.findViewById(R.id.frameLayoutEye);
            this.mCatchLightLayoutEyeFrame = new CatchLightLayoutEyeFrame(getActivity().getApplicationContext());
            this.mFrameLayoutEye.addView(this.mCatchLightLayoutEyeFrame);
        }
    }

    protected void rotateOptImageView(OptimizedImageView img) {
        Log.d(this.TAG, "rotateOptImageView called");
        switch (this.mAngle) {
            case 3:
                img.setDisplayRotationAngle(180);
                return;
            case 4:
            case 5:
            case 7:
            default:
                img.setDisplayRotationAngle(0);
                return;
            case 6:
                img.setDisplayRotationAngle(90);
                return;
            case 8:
                img.setDisplayRotationAngle(270);
                return;
        }
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        if (this.mOptImageOriginal != null) {
            initializeReImagingTask();
            initializeData();
        }
    }

    private void initializeData() {
        ZoomModeState.mChangeListener = this;
        this.mSA_RectCopy = new Sa_RectCopy_FillColor_ImageFilter("com.sony.imaging.app.portraitbeauty");
        Log.d(this.TAG, "==== onResume:optImageCount\t= " + ImageEditor.optImageCount);
        reImaging_running = false;
        mIsLeftEyeSelected = true;
        this.mModeSelected = Mode.OFF.code;
        this.mLightPatternSelection = 0;
        mPatternBriteness = BackUpUtil.getInstance().getPreferenceInt(PortraitBeautyBackUpKey.KEY_CATCHLIGHT_BRIGHTNESS, 192);
        changeLightPattern();
        this.layoutParams_faceZoom.leftMargin = 40;
        this.layoutParams_faceZoom.topMargin = 0;
        this.layoutParams_faceZoom.width = mLayoutParams_faceZoom__width;
        this.layoutParams_faceZoom.height = mLayoutParams_faceZoom__height;
        Log.i(this.TAG, "aspect = " + PortraitBeautyUtil.getAspectRatio() + " angle = " + this.mAngle);
        if (3 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
            if (PortraitBeautyUtil.getAspectRatio() == 0) {
                switch (this.mAngle) {
                    case 1:
                    case 3:
                        this.layoutParams_fullImage.leftMargin = 5;
                        this.layoutParams_fullImage.topMargin = 0;
                        this.layoutParams_fullImage.width = AppRoot.USER_KEYCODE.WATER_HOUSING;
                        this.layoutParams_fullImage.height = 420;
                        break;
                    case 2:
                    default:
                        this.layoutParams_fullImage.leftMargin = 180;
                        this.layoutParams_fullImage.topMargin = 0;
                        this.layoutParams_fullImage.width = 280;
                        this.layoutParams_fullImage.height = 420;
                        break;
                }
            } else if (1 == PortraitBeautyUtil.getAspectRatio()) {
                switch (this.mAngle) {
                    case 1:
                    case 3:
                        this.layoutParams_fullImage.leftMargin = 0;
                        this.layoutParams_fullImage.topMargin = 60;
                        this.layoutParams_fullImage.width = AppRoot.USER_KEYCODE.WATER_HOUSING;
                        this.layoutParams_fullImage.height = 360;
                        break;
                    case 2:
                    default:
                        this.layoutParams_fullImage.leftMargin = IntervalRecExecutor.INTVL_REC_STARTED;
                        this.layoutParams_fullImage.topMargin = 0;
                        this.layoutParams_fullImage.width = 236;
                        this.layoutParams_fullImage.height = 420;
                        break;
                }
            } else if (2 == PortraitBeautyUtil.getAspectRatio()) {
                switch (this.mAngle) {
                    case 1:
                    case 3:
                        this.layoutParams_fullImage.leftMargin = 40;
                        this.layoutParams_fullImage.topMargin = 0;
                        this.layoutParams_fullImage.width = 560;
                        this.layoutParams_fullImage.height = 420;
                        break;
                    case 2:
                    default:
                        this.layoutParams_fullImage.leftMargin = 162;
                        this.layoutParams_fullImage.topMargin = 0;
                        this.layoutParams_fullImage.width = 315;
                        this.layoutParams_fullImage.height = 420;
                        break;
                }
            } else {
                Log.d(this.TAG, "None of the four Aspect Ratios Matched");
            }
        } else if (PortraitBeautyUtil.getAspectRatio() == 0) {
            switch (this.mAngle) {
                case 1:
                case 3:
                    this.layoutParams_fullImage.leftMargin = 84;
                    this.layoutParams_fullImage.topMargin = 0;
                    this.layoutParams_fullImage.width = 472;
                    this.layoutParams_fullImage.height = 420;
                    break;
                case 2:
                default:
                    this.layoutParams_fullImage.leftMargin = 215;
                    this.layoutParams_fullImage.topMargin = 0;
                    this.layoutParams_fullImage.width = 210;
                    this.layoutParams_fullImage.height = 420;
                    break;
            }
        } else if (1 == PortraitBeautyUtil.getAspectRatio()) {
            switch (this.mAngle) {
                case 1:
                case 3:
                    this.layoutParams_fullImage.leftMargin = 40;
                    this.layoutParams_fullImage.topMargin = 0;
                    this.layoutParams_fullImage.width = 559;
                    this.layoutParams_fullImage.height = 420;
                    break;
                case 2:
                default:
                    this.layoutParams_fullImage.leftMargin = 231;
                    this.layoutParams_fullImage.topMargin = 0;
                    this.layoutParams_fullImage.width = 177;
                    this.layoutParams_fullImage.height = 420;
                    break;
            }
        } else if (2 == PortraitBeautyUtil.getAspectRatio()) {
            switch (this.mAngle) {
                case 1:
                case 3:
                    this.layoutParams_fullImage.leftMargin = DigitalZoomController.ZOOM_INIT_VALUE_MODIFIED;
                    this.layoutParams_fullImage.topMargin = 0;
                    this.layoutParams_fullImage.width = 420;
                    this.layoutParams_fullImage.height = 420;
                    break;
                case 2:
                default:
                    this.layoutParams_fullImage.leftMargin = IntervalRecExecutor.INTVL_REC_STARTED;
                    this.layoutParams_fullImage.topMargin = 0;
                    this.layoutParams_fullImage.width = 236;
                    this.layoutParams_fullImage.height = 420;
                    break;
            }
        } else {
            Log.d(this.TAG, "None of the four Aspect Ratios Matched");
        }
        this.mCatchLightImageLayoutParam = this.layoutParams_fullImage;
        this.mFrameLayout.setLayoutParams(this.layoutParams_fullImage);
        Log.d(this.TAG, "layoutParams leftMargin:" + this.layoutParams_fullImage.leftMargin + " topMargin:" + this.layoutParams_fullImage.topMargin + " width:" + this.layoutParams_fullImage.width + " height:" + this.layoutParams_fullImage.height);
        if (this.mOptImageCL != null) {
            Log.d(this.TAG, "mOptImageCL:" + this.mOptImageCL);
            drawFaceFrames();
            if (this.data != null && this.data.getBoolean("menu_pressed", false)) {
                int rightEyeSize = BackUpUtil.getInstance().getPreferenceInt("Left_Eye_Size", this.mLeftEyeSizes[this.mFaceSelection]);
                int rightEyeX = BackUpUtil.getInstance().getPreferenceInt("RightEyeX", this.mRightEyePoints[this.mFaceSelection].x);
                int rightEyeY = BackUpUtil.getInstance().getPreferenceInt("RightEyeY", this.mRightEyePoints[this.mFaceSelection].y);
                int leftEyeSize = BackUpUtil.getInstance().getPreferenceInt("Right_Eye_Size", this.mRightEyeSizes[this.mFaceSelection]);
                int leftEyeX = BackUpUtil.getInstance().getPreferenceInt("LeftEyeX", this.mLeftEyePoints[this.mFaceSelection].x);
                int leftEyeY = BackUpUtil.getInstance().getPreferenceInt("LeftEyeY", this.mLeftEyePoints[this.mFaceSelection].y);
                this.mLeftEyeSizes[this.mFaceSelection] = leftEyeSize;
                this.mRightEyeSizes[this.mFaceSelection] = rightEyeSize;
                this.mLeftEyePoints[this.mFaceSelection].x = leftEyeX;
                this.mLeftEyePoints[this.mFaceSelection].y = leftEyeY;
                this.mRightEyePoints[this.mFaceSelection].x = rightEyeX;
                this.mRightEyePoints[this.mFaceSelection].y = rightEyeY;
                this.mRightEyeRects[this.mFaceSelection].left = BackUpUtil.getInstance().getPreferenceInt("RightEyeRectLeft", this.mRightEyeRects[this.mFaceSelection].left);
                this.mRightEyeRects[this.mFaceSelection].right = BackUpUtil.getInstance().getPreferenceInt("RightEyeRectRight", this.mRightEyeRects[this.mFaceSelection].right);
                this.mRightEyeRects[this.mFaceSelection].top = BackUpUtil.getInstance().getPreferenceInt("RightEyeRectTop", this.mRightEyeRects[this.mFaceSelection].top);
                this.mRightEyeRects[this.mFaceSelection].bottom = BackUpUtil.getInstance().getPreferenceInt("RightEyeRectBottom", this.mRightEyeRects[this.mFaceSelection].bottom);
                this.mLeftEyeRects[this.mFaceSelection].left = BackUpUtil.getInstance().getPreferenceInt("LeftEyeRectLeft", this.mRightEyeRects[this.mFaceSelection].left);
                this.mLeftEyeRects[this.mFaceSelection].right = BackUpUtil.getInstance().getPreferenceInt("LeftEyeRectRight", this.mRightEyeRects[this.mFaceSelection].right);
                this.mLeftEyeRects[this.mFaceSelection].top = BackUpUtil.getInstance().getPreferenceInt("LeftEyeRectTop", this.mRightEyeRects[this.mFaceSelection].top);
                this.mLeftEyeRects[this.mFaceSelection].bottom = BackUpUtil.getInstance().getPreferenceInt("LeftEyeRectBottom", this.mRightEyeRects[this.mFaceSelection].bottom);
                reImaging_otherThread();
                update_arrow_position();
                lightArrow_hide_and_delay_shower(PortraitBeautyConstants.THREE_SECOND_MILLIS);
            }
            if (this.mFaceNum == 0) {
                this.mLeftEyeBtn.setVisibility(4);
                this.mRightEyeBtn.setVisibility(4);
                PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY = true;
                PortraitBeautyConstants.isNoFaceCautionVisible = true;
            } else if (this.mFaceNum > 0) {
                if (this.mFaceNum == 1) {
                    PortraitBeautyConstants.FACE_MORE_THAN_ONE = false;
                    reModeChangeView(Mode.MODE2_CL_ADJUST);
                    PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY = false;
                } else {
                    PortraitBeautyConstants.FACE_MORE_THAN_ONE = true;
                    reModeChangeView(Mode.MODE1_SELECT_TARGET_FACES);
                    PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY = true;
                    if (this.mFooterGuide != null) {
                        setFooterGuide();
                    }
                }
            }
            setKeyBeepPattern(0);
            AppLog.exit(this.TAG, AppLog.getMethodName());
        }
    }

    private void getOptimizedImage() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        ContentsManager contentManager = ContentsManager.getInstance();
        ContentsIdentifier contentsIdentifier = contentManager.getContentsId();
        long j = contentsIdentifier._id;
        this.mOptImageOriginal = ContentsManager.getInstance().getOptimizedImageWithoutCache(contentsIdentifier, 1);
        ContentInfo info = contentManager.getContentInfo(contentManager.getContentsId());
        ImageEditor.setImageQuality((int) info.getLong("MkNoteImgQual"));
        setAdjustEffectPreviewImage();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void setAdjustEffectPreviewImage() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mOptImageOriginal != null) {
            AppLog.checkIf(this.TAG, "Preview image created");
            this.imageAspectRatio = PortraitBeautyUtil.getInstance().getAspectRatio(this.mOptImageOriginal.getWidth(), this.mOptImageOriginal.getHeight());
            if (this.imageAspectRatio != null) {
                this.imageSize = PortraitBeautyUtil.getInstance().getPictureSize(this.imageAspectRatio, this.mOptImageOriginal.getWidth(), this.mOptImageOriginal.getHeight());
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mCatchLightImageView.getLayoutParams();
            Point p = new Point(params.width >> 1, params.height >> 1);
            this.mCatchLightImageView.setPivot(p);
            this.mCatchLightImageView.setLayoutParams(params);
            this.mCatchLightImageView.setOptimizedImage(this.mOptImageOriginal);
            ImageEditor.setImage(this.mOptImageOriginal);
        } else {
            AppLog.checkIf(this.TAG, "Preview image is null");
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onPause();
        ZoomModeState.mChangeListener = null;
        if (this.mOptImageOriginal != null) {
            BackUpUtil.getInstance().setPreference("FACE_SELECTION", Integer.valueOf(this.mFaceSelection));
            BackUpUtil.getInstance().setPreference("Right_Eye_Size", Integer.valueOf(this.mRightEyeSizes[this.mFaceSelection]));
            BackUpUtil.getInstance().setPreference("Left_Eye_Size", Integer.valueOf(this.mLeftEyeSizes[this.mFaceSelection]));
            BackUpUtil.getInstance().setPreference("RightEyeX", Integer.valueOf(this.mRightEyePoints[this.mFaceSelection].x));
            BackUpUtil.getInstance().setPreference("RightEyeY", Integer.valueOf(this.mRightEyePoints[this.mFaceSelection].y));
            BackUpUtil.getInstance().setPreference("RightEyeRectLeft", Integer.valueOf(this.mRightEyeRects[this.mFaceSelection].left));
            BackUpUtil.getInstance().setPreference("RightEyeRectRight", Integer.valueOf(this.mRightEyeRects[this.mFaceSelection].right));
            BackUpUtil.getInstance().setPreference("RightEyeRectTop", Integer.valueOf(this.mRightEyeRects[this.mFaceSelection].top));
            BackUpUtil.getInstance().setPreference("RightEyeRectBottom", Integer.valueOf(this.mRightEyeRects[this.mFaceSelection].bottom));
            BackUpUtil.getInstance().setPreference("LeftEyeRectLeft", Integer.valueOf(this.mLeftEyeRects[this.mFaceSelection].left));
            BackUpUtil.getInstance().setPreference("LeftEyeRectRight", Integer.valueOf(this.mLeftEyeRects[this.mFaceSelection].right));
            BackUpUtil.getInstance().setPreference("LeftEyeRectTop", Integer.valueOf(this.mLeftEyeRects[this.mFaceSelection].top));
            BackUpUtil.getInstance().setPreference("LeftEyeRectBottom", Integer.valueOf(this.mLeftEyeRects[this.mFaceSelection].bottom));
            BackUpUtil.getInstance().setPreference("LeftEyeX", Integer.valueOf(this.mLeftEyePoints[this.mFaceSelection].x));
            BackUpUtil.getInstance().setPreference("LeftEyeY", Integer.valueOf(this.mLeftEyePoints[this.mFaceSelection].y));
            if (this.mSA_RectCopy != null) {
                this.mSA_RectCopy.releaseResources();
            }
            release_mOptImageCropedToShow();
            AppLog.exit(this.TAG, AppLog.getMethodName());
        }
    }

    public void reImaging_otherThread() {
        if (this.reImagingTask != null) {
            this.reImagingTask.execute(20);
        }
    }

    public void reImaging() {
        reImaging(true);
    }

    public void reImaging(boolean showImageWithCatchlight) {
        int out_width;
        Log.d(this.TAG, "reImaging-- called");
        Log.d(this.TAG, "==== reImaging In:optImageCount\t= " + ImageEditor.optImageCount);
        this.scaleFactor = 1;
        if (this.mOptImageOriginal != null) {
            if (this.reImagingTask != null) {
                this.reImagingTask.setReImagingFlag(false);
            }
            if (this.gc_execute_counter >= this.gc_execute_timing) {
                Log.d(this.TAG, "reImaging     execute gc");
                System.gc();
                this.gc_execute_counter = 0;
            } else {
                Log.d(this.TAG, "reImaging     skip gc");
                this.gc_execute_counter++;
            }
            Log.d(this.TAG, "reImaging release mOptImageCL");
            ImageEditor.releaseOptImage(this.mOptImageCL);
            if (this.mOptImageCropedToShow != null) {
                release_mOptImageCropedToShow();
            }
            Log.d(this.TAG, "reImaging copy original image to mOptImageCL");
            this.mOptImageCL = getCopyImage2(this.mOptImageOriginal);
            if (this.mOptImageCL == null) {
                Log.e(this.TAG, "reImaging mOptImageCL is null");
            }
            Log.d(this.TAG, "reImaging make catch lighted image");
            if (showImageWithCatchlight) {
                applyCLImageFilter(this.mModeSelected);
            }
            Log.d(this.TAG, "image width:" + this.mOptImageCL.getWidth() + " height:" + this.mOptImageCL.getHeight() + "  crop:" + this.mFaceRects_crop[this.mFaceSelection]);
            if (this.mOptImageCL.getWidth() > 3000) {
                this.scaleFactor = 2;
            }
            if (this.mOptImageCL.getHeight() > 3000) {
                this.scaleFactor = 2;
            }
            OptimizedImage mOptImageCLResized = getResizeImage(this.mOptImageCL, this.mOptImageCL.getWidth() / this.scaleFactor, this.mOptImageCL.getHeight() / this.scaleFactor);
            this.mOptImageCL.release();
            Log.d(this.TAG, "reImaging crop face");
            Log.d(this.TAG, "reImaging mFaceRects_crop[mFaceSelection]:" + this.mFaceRects_crop[this.mFaceSelection]);
            Rect miniRect = getResizedRect(this.mFaceRects_crop[this.mFaceSelection], this.scaleFactor);
            OptimizedImage cropped_optImage = getCropImage(mOptImageCLResized, rotate_rect(miniRect, this.mOriginalWidth / this.scaleFactor, this.mOriginalHeight / this.scaleFactor), false);
            if (cropped_optImage == null) {
                Log.e(this.TAG, "getCropImage(mOptImageCL, actual_crop_rect); was null!!");
            }
            mOptImageCLResized.release();
            this.mOptImageCL.release();
            Log.d(this.TAG, "aaaaaaaaa   mOptImageCL.getWidth()" + this.mOptImageCL.getWidth());
            Log.d(this.TAG, "reImaging spin face");
            OptimizedImage optImg_face_cropped_rotated = getRotateImage(cropped_optImage, RotateImageFilter.ROTATION_DEGREE.DEGREE_0);
            ImageEditor.releaseOptImage(cropped_optImage);
            Log.d(this.TAG, "reImaging scale face");
            int out_height = this.layoutParams_faceZoom.height;
            if (2 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                out_width = (int) (this.layoutParams_faceZoom.width * 1.3333333333333333d);
            } else {
                int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
                if (device == 2 && 3 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                    out_width = this.layoutParams_faceZoom.width + 30;
                } else {
                    out_width = this.layoutParams_faceZoom.width;
                }
            }
            Log.d(this.TAG, "reImaging scale face to width:" + out_width + " height:" + out_height);
            if (optImg_face_cropped_rotated != null) {
                ImageEditor.releaseOptImage(this.mOptImageCropedToShow);
                this.mOptImageCropedToShow = getResizeImage(optImg_face_cropped_rotated, out_width, out_height);
            }
            if (this.mOptImageCropedToShow == null) {
                Log.e(this.TAG, "getResizeImage(rotation_fixed_optImage, out_width, out_height); was null!!");
            }
            Log.d(this.TAG, "reImaging preparing  ---  LEFT ---- eye");
            reImaging____helperFunction_prepareEye(optImg_face_cropped_rotated, getResizedRect(this.mLeftEyeRects[this.mFaceSelection], this.scaleFactor), 42);
            Log.d(this.TAG, "reImaging preparing  ---  RIGHT ---- eye");
            reImaging____helperFunction_prepareEye(optImg_face_cropped_rotated, getResizedRect(this.mRightEyeRects[this.mFaceSelection], this.scaleFactor), mOutput_RightEye_left);
            ImageEditor.releaseOptImage(optImg_face_cropped_rotated);
            this.mCatchLightImageView.setDisplayRotationAngle(0);
            this.mCatchLightImageView.setOptimizedImage(this.mOptImageCropedToShow);
            if (this.reImagingTask != null) {
                this.reImagingTask.setReImagingFlag(true);
            }
            Log.d(this.TAG, "reImaging-- end");
        }
    }

    private Rect getResizedRect(Rect rectIn, int scale) {
        return new Rect(rectIn.left / scale, rectIn.top / scale, rectIn.right / scale, rectIn.bottom / scale);
    }

    public void reImaging____helperFunction_prepareEye(OptimizedImage optImg_face_cropped_rotated, Rect eye_rect, int output_eye_left) {
        int resize_to_width;
        int output_eye_left_drawTo;
        Log.d(this.TAG, "reImaging crop eye");
        Rect eyeRects_rotated = rotate_rect(eye_rect, this.mOptImageCL.getWidth() / this.scaleFactor, this.mOptImageCL.getHeight() / this.scaleFactor);
        Rect actual_crop_rect_rotated = rotate_rect(getResizedRect(this.mFaceRects_crop[this.mFaceSelection], this.scaleFactor), this.mOptImageCL.getWidth() / this.scaleFactor, this.mOptImageCL.getHeight() / this.scaleFactor);
        if (eyeRects_rotated.width() < 16) {
            int diff = 16 - eyeRects_rotated.width();
            int diff_left = diff / 2;
            int diff_right = diff - diff_left;
            eyeRects_rotated.left -= diff_left;
            eyeRects_rotated.right += diff_right;
            if (eyeRects_rotated.left < actual_crop_rect_rotated.left) {
                int expanded_width = eyeRects_rotated.width();
                eyeRects_rotated.left = actual_crop_rect_rotated.left;
                eyeRects_rotated.right = actual_crop_rect_rotated.left + expanded_width;
            }
            if (actual_crop_rect_rotated.right < eyeRects_rotated.right) {
                int expanded_width2 = eyeRects_rotated.width();
                eyeRects_rotated.right = actual_crop_rect_rotated.right;
                eyeRects_rotated.left = eyeRects_rotated.right - expanded_width2;
            }
        }
        if (eyeRects_rotated.height() < 16) {
            int diff2 = 16 - eyeRects_rotated.height();
            int diff_top = diff2 / 2;
            int diff_bottom = diff2 - diff_top;
            eyeRects_rotated.top -= diff_top;
            eyeRects_rotated.bottom += diff_bottom;
            if (eyeRects_rotated.top < actual_crop_rect_rotated.top) {
                int expanded_height = eyeRects_rotated.height();
                eyeRects_rotated.top = actual_crop_rect_rotated.top;
                eyeRects_rotated.bottom = actual_crop_rect_rotated.top + expanded_height;
            }
            if (actual_crop_rect_rotated.bottom < eyeRects_rotated.bottom) {
                int expanded_height2 = eyeRects_rotated.height();
                eyeRects_rotated.bottom = actual_crop_rect_rotated.bottom;
                eyeRects_rotated.top = eyeRects_rotated.bottom - expanded_height2;
            }
        }
        eyeRects_rotated.offset(-actual_crop_rect_rotated.left, -actual_crop_rect_rotated.top);
        Log.d(this.TAG, "reImaging  crop  eye  eyeRects_rotated:" + eyeRects_rotated);
        OptimizedImage optImageCropEye = getCropImage(optImg_face_cropped_rotated, eyeRects_rotated);
        if (optImageCropEye == null) {
            Log.e(this.TAG, "getCropImage(rotation_fixed_optImage, cropping_rect) was null!!");
        }
        Log.d(this.TAG, "reImaging resize eye");
        if (2 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
            resize_to_width = 309;
        } else {
            resize_to_width = 232;
        }
        Log.d(this.TAG, "reImaging resize eye  resize from w:" + optImageCropEye.getWidth() + " h:" + optImageCropEye.getHeight() + "   to w:" + resize_to_width + " h:" + mOutput_eye_height);
        OptimizedImage optImageEye = getResizeImage(optImageCropEye, resize_to_width, mOutput_eye_height);
        if (optImageEye == null) {
            Log.e(this.TAG, "getResizeImage(optImageCropEye,     ); was null!!");
        }
        ImageEditor.releaseOptImage(optImageCropEye);
        Log.d(this.TAG, "reImaging  copy  eye");
        if (2 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
            output_eye_left_drawTo = (((int) (((output_eye_left - this.layoutParams_faceZoom.leftMargin) * 4) / 3.0d)) >> 2) << 2;
        } else {
            output_eye_left_drawTo = ((output_eye_left - this.layoutParams_faceZoom.leftMargin) >> 2) << 2;
        }
        int output_eye_top_drawTo = 234 - this.layoutParams_faceZoom.topMargin;
        Log.d(this.TAG, "reImaging    output_eye_left_drawTo:" + output_eye_left_drawTo + "  output_eye_top_drawTo:" + output_eye_top_drawTo);
        this.mSA_RectCopy.rectCopy_setParam(optImageEye, this.mOptImageCropedToShow, 0, 0, output_eye_left_drawTo, output_eye_top_drawTo, optImageEye.getWidth(), optImageEye.getHeight());
        if (this.mSA_RectCopy.execute(true) != 0) {
            Log.e(this.TAG, "sA_RectCopy.execute was error!!");
        }
        optImageEye.release();
    }

    public void reEyeChangeView() {
        Log.d(this.TAG, "reEyeChangeView  mIsLeftEyeSelected:" + mIsLeftEyeSelected);
        if (mIsLeftEyeSelected) {
            mIsLeftEyeSelected = false;
        } else {
            mIsLeftEyeSelected = true;
        }
    }

    public void reFaceChangeView(int move_amount) {
        Log.d(this.TAG, "reFaceChangeView:mFaceSelection_pre=" + this.mFaceSelection + " mFaceNum=" + this.mFaceNum);
        this.mFaceSelection += move_amount;
        if (this.mFaceSelection >= this.mFaceNum) {
            this.mFaceSelection = 0;
        }
        if (this.mFaceSelection < 0) {
            this.mFaceSelection = this.mFaceNum - 1;
        }
        mGauzeCurlevel = this.sGuzebarCurrentLevel[this.mFaceSelection];
        Log.d(this.TAG, "reFaceChangeView:mFaceSelection_aft=" + this.mFaceSelection + " mFaceNum=" + this.mFaceNum);
        if (this.mFaceNum > 0) {
            for (int i = 0; i < this.mFaceNum; i++) {
                if (i == this.mFaceSelection) {
                    this.mDrawView[i].setColor(this.mColorSelected);
                    this.mDrawView[i].setWidth(3);
                    if (this.mFaceNum == 1) {
                        this.mDrawView[i].setShow(false);
                    } else {
                        this.mDrawView[i].setShow(true);
                    }
                } else {
                    this.mDrawView[i].setColor(this.mColorUnSelected);
                    this.mDrawView[i].setWidth(2);
                    this.mDrawView[i].setShow(false);
                }
            }
        }
    }

    public void reModeChangeView(Mode mode) {
        switch (mode) {
            case MODE1_SELECT_TARGET_FACES:
                this.mModeSelected = Mode.MODE1_SELECT_TARGET_FACES.code;
                this.mCatchLightImageLayoutParam = this.layoutParams_fullImage;
                Point p = new Point(this.mCatchLightImageLayoutParam.width >> 1, this.mCatchLightImageLayoutParam.height >> 1);
                this.mCatchLightImageView.setPivot(p);
                this.mCatchLightImageView.setLayoutParams(this.layoutParams_fullImage);
                this.mFrameLayoutEye.setVisibility(4);
                this.mLeftEyeBtn.setVisibility(4);
                this.mRightEyeBtn.setVisibility(4);
                this.mFrameLayout.setVisibility(0);
                PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY = true;
                this.mCatchLightImageView.setDisplayType(OptimizedImageView.DisplayType.DISPLAY_TYPE_CENTER_INNER);
                this.mCatchLightImageView.setOptimizedImage(this.mOptImageOriginal);
                return;
            case MODE2_CL_ADJUST:
                this.mModeSelected = Mode.MODE2_CL_ADJUST.code;
                this.mFrameLayout.setVisibility(4);
                this.mFrameLayoutEye.setVisibility(0);
                this.mCatchLightImageView.setDisplayType(OptimizedImageView.DisplayType.DISPLAY_TYPE_FIT_EXPAND);
                this.mCatchLightImageLayoutParam = this.layoutParams_faceZoom;
                Point p2 = new Point(this.mCatchLightImageLayoutParam.width >> 1, this.mCatchLightImageLayoutParam.height >> 1);
                this.mCatchLightImageView.setPivot(p2);
                this.mCatchLightImageView.setLayoutParams(this.mCatchLightImageLayoutParam);
                this.mLeftEyeBtn.setVisibility(4);
                this.mRightEyeBtn.setVisibility(4);
                if (this.reImagingTask != null) {
                    this.reImagingTask.execute(20);
                }
                update_arrow_position();
                lightArrow_hide_and_delay_shower(mLayoutParams_faceZoom__width);
                return;
            case MODE3_CL_ADJUST_CL_INVISIBLE:
                this.mModeSelected = Mode.MODE3_CL_ADJUST_CL_INVISIBLE.code;
                if (this.reImagingTask != null) {
                    this.reImagingTask.execute(20);
                }
                this.handler.removeCallbacks(this.delayFunc);
                return;
            case MODE4_CHECK_WHOLE_IMAGE:
                this.mModeSelected = Mode.MODE4_CHECK_WHOLE_IMAGE.code;
                this.mCatchLightImageLayoutParam = this.layoutParams_fullImage;
                this.mCatchLightImageView.setLayoutParams(this.mCatchLightImageLayoutParam);
                this.mFrameLayoutEye.setVisibility(4);
                this.handler.removeCallbacks(this.delayFunc);
                ImageEditor.releaseOptImage(this.mOptImageCL);
                this.mOptImageCL = getCopyImage2(this.mOptImageOriginal);
                if (this.mOptImageCL == null) {
                    Log.e(this.TAG, "reImaging mOptImageCL is null");
                }
                applyCLImageFilter(this.mModeSelected);
                rotateOptImageView(this.mCatchLightImageView);
                this.mCatchLightImageView.setOptimizedImage(this.mOptImageCL);
                return;
            case MODE5_CHECK_WHOLE_IMAGE_CL_INVISIBLE:
                this.mModeSelected = Mode.MODE5_CHECK_WHOLE_IMAGE_CL_INVISIBLE.code;
                this.mCatchLightImageLayoutParam = this.layoutParams_fullImage;
                this.mCatchLightImageView.setLayoutParams(this.mCatchLightImageLayoutParam);
                rotateOptImageView(this.mCatchLightImageView);
                release_mOptImageCropedToShow();
                this.mCatchLightImageView.setOptimizedImage(this.mOptImageOriginal);
                return;
            default:
                return;
        }
    }

    private void release_mOptImageCropedToShow() {
        if (this.mOptImageCropedToShow != null) {
            this.mOptImageCropedToShow.release();
            this.mOptImageCropedToShow = null;
        }
    }

    private void applyCLImageFilter(int mode) {
        int eyeSize;
        int eyePosX;
        int eyePosY;
        int eyeSize2;
        int eyePosX2;
        int eyePosY2;
        Log.d(this.TAG, "applyCLImageFilter called");
        OptimizedImage optImageCLPatern = this.mOptImageEyepat;
        if (this.data == null || this.data.getBoolean("menu_pressed", false)) {
        }
        if (true == isreturningfromPreview) {
            this.mFaceSelection = BackUpUtil.getInstance().getPreferenceInt("FACE_SELECTION", 0);
            eyeSize = BackUpUtil.getInstance().getPreferenceInt("Left_Eye_Size", this.mLeftEyeSizes[this.mFaceSelection]);
        } else {
            eyeSize = this.mLeftEyeSizes[this.mFaceSelection];
        }
        Log.i(this.TAG, "Left Eye");
        Log.i(this.TAG, "before eyePosX = 0 eyePosY = 0");
        if (6 == this.mAngle) {
            eyePosX = this.mOptImageCL.getWidth() - this.mLeftEyePoints[this.mFaceSelection].y;
            eyePosY = this.mLeftEyePoints[this.mFaceSelection].x;
        } else if (8 == this.mAngle) {
            eyePosX = this.mLeftEyePoints[this.mFaceSelection].y;
            eyePosY = this.mOptImageCL.getHeight() - this.mLeftEyePoints[this.mFaceSelection].x;
        } else if (3 == this.mAngle) {
            eyePosX = this.mOptImageCL.getWidth() - this.mLeftEyePoints[this.mFaceSelection].x;
            eyePosY = this.mOptImageCL.getHeight() - this.mLeftEyePoints[this.mFaceSelection].y;
        } else {
            eyePosX = this.mLeftEyePoints[this.mFaceSelection].x;
            eyePosY = this.mLeftEyePoints[this.mFaceSelection].y;
        }
        Log.i(this.TAG, "eyePosX = " + eyePosX + " eyePosY = " + eyePosY);
        OptimizedImage mOptImageCLPaternScaled = getResizeImage(optImageCLPatern, eyeSize, eyeSize);
        addPattern(this.mOptImageCL, mOptImageCLPaternScaled, eyePosX - (eyeSize / 2), eyePosY - (eyeSize / 2), mPatternBriteness);
        ImageEditor.releaseOptImage(mOptImageCLPaternScaled);
        Log.i(this.TAG, "Right Eye");
        Log.i(this.TAG, "before eyePosX = " + eyePosX + " eyePosY = " + eyePosY);
        if (true == isreturningfromPreview) {
            eyeSize2 = BackUpUtil.getInstance().getPreferenceInt("Right_Eye_Size", this.mRightEyeSizes[this.mFaceSelection]);
        } else {
            eyeSize2 = this.mRightEyeSizes[this.mFaceSelection];
        }
        if (6 == this.mAngle) {
            eyePosX2 = this.mOptImageCL.getWidth() - this.mRightEyePoints[this.mFaceSelection].y;
            eyePosY2 = this.mRightEyePoints[this.mFaceSelection].x;
        } else if (8 == this.mAngle) {
            eyePosX2 = this.mRightEyePoints[this.mFaceSelection].y;
            eyePosY2 = this.mOptImageCL.getHeight() - this.mRightEyePoints[this.mFaceSelection].x;
        } else if (3 == this.mAngle) {
            eyePosX2 = this.mOptImageCL.getWidth() - this.mRightEyePoints[this.mFaceSelection].x;
            eyePosY2 = this.mOptImageCL.getHeight() - this.mRightEyePoints[this.mFaceSelection].y;
        } else {
            eyePosX2 = this.mRightEyePoints[this.mFaceSelection].x;
            eyePosY2 = this.mRightEyePoints[this.mFaceSelection].y;
        }
        Log.i(this.TAG, "yePosX = " + eyePosX2 + " eyePosY = " + eyePosY2);
        OptimizedImage mOptImageCLPaternScaled2 = getResizeImage(optImageCLPatern, eyeSize2, eyeSize2);
        addPattern(this.mOptImageCL, mOptImageCLPaternScaled2, eyePosX2 - (eyeSize2 / 2), eyePosY2 - (eyeSize2 / 2), mPatternBriteness);
        ImageEditor.releaseOptImage(mOptImageCLPaternScaled2);
    }

    private void lightArrow_hide_and_delay_shower(int delay_time) {
        Log.d(this.TAG, "lightArrow_hide_and_delay_shower");
        if (this.handler == null) {
            this.handler = new Handler();
        }
        this.handler.removeCallbacks(this.delayFunc);
        this.handler.postDelayed(this.delayFunc, delay_time);
    }

    private void drawFaceFrames() {
        float scaleW;
        float scaleH;
        int rect_height_check_intersectRect_bottom;
        int rect_width_check_intersectRect_right;
        int adjust_height;
        int min_EyeRects_top;
        int max_EyeRects_bottom;
        int adjust_height2;
        int min_EyeRects_left;
        int max_EyeRects_right;
        Log.d(this.TAG, "drawFaceFrames called");
        createEyeRects();
        if (this.mFaceNum < 1) {
            this.mLightArrowsPos.setMargins(300, IntervalRecExecutor.INTVL_REC_INITIALIZED, 0, 0);
            mCatchlight_focus_arrow.setLayoutParams(this.mLightArrowsPos);
            mCatchLightLayoutEyeFrame_draw = false;
            this.mCatchLightLayoutEyeFrame.invalidate();
            openLayout(PortraitBeautyUtil.ID_MESSAGENOFACE);
            return;
        }
        mCatchLightLayoutEyeFrame_draw = true;
        mCatchLightEyeFrame_draw_black = true;
        this.mCatchLightLayoutEyeFrame.invalidate();
        float viewWidth = this.mCatchLightImageLayoutParam.width;
        float viewHeight = this.mCatchLightImageLayoutParam.height;
        float scaleX = 2000.0f / this.mOriginalWidth;
        float scaleY = 2000.0f / this.mOriginalHeight;
        this.mDrawView = new DrawFaceFrameView[this.mFaceNum];
        this.mDrawViewL = new DrawFaceFrameView[this.mFaceNum];
        this.mDrawViewR = new DrawFaceFrameView[this.mFaceNum];
        for (int i = 0; i < this.mFaceNum; i++) {
            this.mDrawView[i] = new DrawFaceFrameView(getActivity().getApplicationContext());
            float left = (this.mFaces[i].face.rect.left + 1000) / scaleX;
            float top = (this.mFaces[i].face.rect.top + 1000) / scaleY;
            float right = (this.mFaces[i].face.rect.right + 1000) / scaleX;
            float bottom = (this.mFaces[i].face.rect.bottom + 1000) / scaleY;
            this.mFaceRects_org[i] = new Rect((int) left, (int) top, (int) right, (int) bottom);
            if (6 == this.mAngle) {
                scaleW = viewWidth / this.mOriginalHeight;
                scaleH = viewHeight / this.mOriginalWidth;
            } else if (3 == this.mAngle) {
                scaleW = viewWidth / this.mOriginalWidth;
                scaleH = viewHeight / this.mOriginalHeight;
            } else if (8 == this.mAngle) {
                scaleW = viewWidth / this.mOriginalHeight;
                scaleH = viewHeight / this.mOriginalWidth;
            } else {
                scaleW = viewWidth / this.mOriginalWidth;
                scaleH = viewHeight / this.mOriginalHeight;
            }
            this.mFaceRects_rotated[i] = rotate_rect(this.mFaceRects_org[i], this.mOriginalWidth, this.mOriginalHeight);
            this.mDrawView[i].mLeft = (int) (this.mFaceRects_rotated[i].left * scaleW);
            this.mDrawView[i].mTop = (int) (this.mFaceRects_rotated[i].top * scaleH);
            this.mDrawView[i].mRight = (int) (this.mFaceRects_rotated[i].right * scaleW);
            this.mDrawView[i].mBottom = (int) (this.mFaceRects_rotated[i].bottom * scaleH);
            this.mFrameLayout.addView(this.mDrawView[i]);
            if (i == 0) {
                this.mDrawView[i].setColor(this.mColorSelected);
                this.mDrawView[i].setWidth(3);
                if (this.mFaceNum == 1) {
                    this.mDrawView[i].setShow(false);
                } else {
                    this.mDrawView[i].setShow(true);
                }
            } else {
                this.mDrawView[i].setColor(this.mColorUnSelected);
                this.mDrawView[i].setWidth(2);
                this.mDrawView[i].setShow(false);
            }
            Log.d("coordinates face :" + i, LogHelper.MSG_OPEN_BRACKET + this.mFaces[i].face.rect.left + "," + this.mFaces[i].face.rect.top + ")(" + this.mFaces[i].face.rect.right + "," + this.mFaces[i].face.rect.bottom + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("coordinates leftEye :" + i, LogHelper.MSG_OPEN_BRACKET + this.mFaces[i].face.leftEye.x + "," + this.mFaces[i].face.leftEye.y + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("coordinates rightEye :" + i, LogHelper.MSG_OPEN_BRACKET + this.mFaces[i].face.rightEye.x + "," + this.mFaces[i].face.rightEye.y + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("coordinates mouth :" + i, LogHelper.MSG_OPEN_BRACKET + this.mFaces[i].face.mouth.x + "," + this.mFaces[i].face.mouth.y + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("coordinates mouthLeft :" + i, LogHelper.MSG_OPEN_BRACKET + this.mFaces[i].face.mouthLeft.x + "," + this.mFaces[i].face.mouthLeft.y + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("coordinates mouthRight :" + i, LogHelper.MSG_OPEN_BRACKET + this.mFaces[i].face.mouthRight.x + "," + this.mFaces[i].face.mouthRight.y + LogHelper.MSG_CLOSE_BRACKET);
            this.mFaceRects_crop[i] = rotate_rect(this.mFaceRects_org[i], this.mOriginalWidth, this.mOriginalHeight);
            int adding_area = (((int) ((this.mFaceRects_crop[i].width() * (this.mFace_show_width_ratio - 1.0d)) / 2.0d)) >> 1) << 1;
            this.mFaceRects_crop[i].left -= adding_area;
            this.mFaceRects_crop[i].right += adding_area;
            if (2 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                this.mFaceRects_crop[i].bottom = this.mFaceRects_crop[i].top + ((int) ((this.layoutParams_faceZoom.height / ((this.layoutParams_faceZoom.width * 4.0d) / 3.0d)) * this.mFaceRects_crop[i].width()));
            } else {
                this.mFaceRects_crop[i].bottom = this.mFaceRects_crop[i].top + ((int) ((this.layoutParams_faceZoom.height / this.layoutParams_faceZoom.width) * this.mFaceRects_crop[i].width()));
            }
            Log.d(this.TAG, "mFaceRects_crop[i] before spin_rect_inv:" + this.mFaceRects_crop[i]);
            this.mFaceRects_crop[i] = rotate_rect_inv(this.mFaceRects_crop[i], this.mOriginalWidth, this.mOriginalHeight);
            Log.d(this.TAG, "mFaceRects_crop[i] after spin_rect_inv:" + this.mFaceRects_crop[i]);
            Rect actual_crop_rect = new Rect(this.mFaceRects_crop[i]);
            Log.d(this.TAG, " mFaceRects_crop[i]:" + this.mFaceRects_crop[i]);
            actual_crop_rect.intersect(0, 0, this.mOriginalWidth, this.mOriginalHeight);
            Log.d(this.TAG, " actual_crop_rect before rotate:" + actual_crop_rect);
            Rect actual_crop_rect2 = rotate_rect(actual_crop_rect, this.mOriginalWidth, this.mOriginalHeight);
            Log.d(this.TAG, " actual_crop_rect before XY check:" + actual_crop_rect2);
            if (2 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                rect_height_check_intersectRect_bottom = (int) (actual_crop_rect2.top + ((this.layoutParams_faceZoom.height / ((this.layoutParams_faceZoom.width * 4) / 3.0d)) * actual_crop_rect2.width()));
            } else {
                rect_height_check_intersectRect_bottom = actual_crop_rect2.top + ((this.layoutParams_faceZoom.height / this.layoutParams_faceZoom.width) * actual_crop_rect2.width());
            }
            Rect rect_height_check_intersectRect = new Rect(actual_crop_rect2.left, actual_crop_rect2.top, actual_crop_rect2.right, rect_height_check_intersectRect_bottom);
            Log.d(this.TAG, " rect_height_check_intersectRect:" + rect_height_check_intersectRect);
            actual_crop_rect2.intersect(rect_height_check_intersectRect);
            int i2 = actual_crop_rect2.right;
            int rect_width_check_intersectRect_left = actual_crop_rect2.left;
            if (2 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                rect_width_check_intersectRect_right = (int) (actual_crop_rect2.left + ((((this.layoutParams_faceZoom.width * 4) / 3.0d) / this.layoutParams_faceZoom.height) * actual_crop_rect2.height()));
            } else {
                rect_width_check_intersectRect_right = (int) (actual_crop_rect2.left + ((this.layoutParams_faceZoom.width / this.layoutParams_faceZoom.height) * actual_crop_rect2.height()));
            }
            Rect rect_width_check_intersectRect = new Rect(rect_width_check_intersectRect_left, actual_crop_rect2.top, rect_width_check_intersectRect_right, actual_crop_rect2.bottom);
            Log.d(this.TAG, " rect_width_check_intersectRect:" + rect_width_check_intersectRect);
            actual_crop_rect2.intersect(rect_width_check_intersectRect);
            Rect actual_crop_rect3 = rotate_rect_inv(actual_crop_rect2, this.mOriginalWidth, this.mOriginalHeight);
            if (1 == this.mAngle || 3 == this.mAngle) {
                int adjust_width = actual_crop_rect3.width();
                int half_adjust_width = adjust_width / 2;
                int center_xpos_eyes = (this.mLeftEyePoints[i].x + this.mRightEyePoints[i].x) / 2;
                actual_crop_rect3.left = center_xpos_eyes - half_adjust_width;
                Log.d(this.TAG, "  LeftEye_X = " + this.mLeftEyePoints[i].x + " RightEye_x  = " + this.mRightEyePoints[i].x);
                Log.d(this.TAG, "  CenterEye_X = " + center_xpos_eyes);
                if (actual_crop_rect3.left < 0) {
                    actual_crop_rect3.left = 0;
                }
                if (actual_crop_rect3.left > this.mLeftEyeRects[i].left) {
                    actual_crop_rect3.left = this.mLeftEyeRects[i].left;
                }
                actual_crop_rect3.right = actual_crop_rect3.left + adjust_width;
                if (actual_crop_rect3.right >= this.mOriginalWidth) {
                    actual_crop_rect3.right = this.mOriginalWidth;
                    actual_crop_rect3.left = actual_crop_rect3.right - adjust_width;
                }
                if (actual_crop_rect3.right < this.mRightEyeRects[i].right) {
                    actual_crop_rect3.right = this.mRightEyeRects[i].right;
                    actual_crop_rect3.left = actual_crop_rect3.right - adjust_width;
                }
                if (3 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                    adjust_height = (int) ((this.layoutParams_faceZoom.height / this.layoutParams_faceZoom.width) * actual_crop_rect3.width());
                } else {
                    adjust_height = (int) ((((this.layoutParams_faceZoom.height / this.layoutParams_faceZoom.width) * actual_crop_rect3.width()) * 3.0d) / 4.0d);
                }
                int center_ypos_eyes = (this.mLeftEyePoints[i].y + this.mRightEyePoints[i].y) / 2;
                if (1 == this.mAngle) {
                    actual_crop_rect3.top = center_ypos_eyes - (adjust_height / 3);
                } else {
                    actual_crop_rect3.top = center_ypos_eyes - ((adjust_height * 2) / 3);
                }
                actual_crop_rect3.bottom = actual_crop_rect3.top + adjust_height;
                if (actual_crop_rect3.top < 0) {
                    actual_crop_rect3.top = 0;
                    actual_crop_rect3.bottom = actual_crop_rect3.top + adjust_height;
                }
                if (this.mLeftEyeRects[i].top < this.mRightEyeRects[i].top) {
                    min_EyeRects_top = this.mLeftEyeRects[i].top;
                } else {
                    min_EyeRects_top = this.mRightEyeRects[i].top;
                }
                if (min_EyeRects_top < actual_crop_rect3.top) {
                    actual_crop_rect3.top = min_EyeRects_top;
                    actual_crop_rect3.bottom = actual_crop_rect3.top + adjust_height;
                }
                if (this.mOriginalHeight < actual_crop_rect3.bottom) {
                    actual_crop_rect3.bottom = this.mOriginalHeight;
                    actual_crop_rect3.top = actual_crop_rect3.bottom - adjust_height;
                }
                if (this.mLeftEyeRects[i].bottom < this.mRightEyeRects[i].bottom) {
                    max_EyeRects_bottom = this.mRightEyeRects[i].bottom;
                } else {
                    max_EyeRects_bottom = this.mLeftEyeRects[i].bottom;
                }
                if (actual_crop_rect3.bottom < max_EyeRects_bottom) {
                    actual_crop_rect3.bottom = max_EyeRects_bottom;
                    actual_crop_rect3.top = actual_crop_rect3.bottom - adjust_height;
                }
            } else {
                int adjust_width2 = actual_crop_rect3.height();
                int half_adjust_width2 = adjust_width2 / 2;
                actual_crop_rect3.top = ((this.mLeftEyePoints[i].y + this.mRightEyePoints[i].y) / 2) - half_adjust_width2;
                if (actual_crop_rect3.top < 0) {
                    actual_crop_rect3.top = 0;
                }
                if (actual_crop_rect3.top > this.mRightEyeRects[i].top) {
                    actual_crop_rect3.top = this.mRightEyeRects[i].top;
                }
                actual_crop_rect3.bottom = actual_crop_rect3.top + adjust_width2;
                if (actual_crop_rect3.bottom >= this.mOriginalHeight) {
                    actual_crop_rect3.bottom = this.mOriginalHeight;
                    actual_crop_rect3.top = actual_crop_rect3.bottom - adjust_width2;
                }
                if (actual_crop_rect3.bottom < this.mRightEyeRects[i].bottom) {
                    actual_crop_rect3.bottom = this.mRightEyeRects[i].bottom;
                    actual_crop_rect3.top = actual_crop_rect3.bottom - adjust_width2;
                }
                if (3 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                    adjust_height2 = (int) ((this.layoutParams_faceZoom.height / this.layoutParams_faceZoom.width) * actual_crop_rect3.height());
                } else {
                    adjust_height2 = (int) ((((this.layoutParams_faceZoom.height / this.layoutParams_faceZoom.width) * actual_crop_rect3.height()) * 3.0d) / 4.0d);
                }
                int center_ypos_eyes2 = (this.mLeftEyePoints[i].x + this.mRightEyePoints[i].x) / 2;
                if (6 == this.mAngle) {
                    actual_crop_rect3.left = center_ypos_eyes2 - (adjust_height2 / 3);
                } else {
                    actual_crop_rect3.left = center_ypos_eyes2 - ((adjust_height2 * 2) / 3);
                }
                actual_crop_rect3.right = actual_crop_rect3.left + adjust_height2;
                if (actual_crop_rect3.left < 0) {
                    actual_crop_rect3.left = 0;
                    actual_crop_rect3.right = actual_crop_rect3.left + adjust_height2;
                }
                if (this.mLeftEyeRects[i].left < this.mRightEyeRects[i].left) {
                    min_EyeRects_left = this.mLeftEyeRects[i].left;
                } else {
                    min_EyeRects_left = this.mRightEyeRects[i].left;
                }
                if (min_EyeRects_left < actual_crop_rect3.left) {
                    actual_crop_rect3.left = min_EyeRects_left;
                    actual_crop_rect3.right = actual_crop_rect3.left + adjust_height2;
                }
                if (this.mOriginalWidth < actual_crop_rect3.right) {
                    actual_crop_rect3.right = this.mOriginalWidth;
                    actual_crop_rect3.left = actual_crop_rect3.right - adjust_height2;
                }
                if (this.mLeftEyeRects[i].right < this.mRightEyeRects[i].right) {
                    max_EyeRects_right = this.mRightEyeRects[i].right;
                } else {
                    max_EyeRects_right = this.mLeftEyeRects[i].right;
                }
                if (actual_crop_rect3.right < max_EyeRects_right) {
                    actual_crop_rect3.right = max_EyeRects_right;
                    actual_crop_rect3.left = actual_crop_rect3.right - adjust_height2;
                }
            }
            Log.d(this.TAG, "  after: actual_crop_rect.left = " + actual_crop_rect3.left + "  actual_crop_rect.right = " + actual_crop_rect3.right);
            this.mFaceRects_crop[i] = actual_crop_rect3;
            Log.d(this.TAG, "mFaceRects i:" + i);
            Log.d(this.TAG, "mFaceRects_org area:" + this.mFaceRects_org[i]);
            Log.d(this.TAG, "mFaceRects_crop area:" + this.mFaceRects_crop[i]);
            Log.d(this.TAG, "mFaceRects_rotated area:" + this.mFaceRects_rotated[i]);
            Log.d(this.TAG, "mFaceRects_org rotated area:" + rotate_rect(this.mFaceRects_org[i], this.mOriginalWidth, this.mOriginalHeight));
            Log.d(this.TAG, "mFaceRects_crop  rotated area:" + rotate_rect(this.mFaceRects_crop[i], this.mOriginalWidth, this.mOriginalHeight));
        }
        reFaceChangeView(BackUpUtil.getInstance().getPreferenceInt("FACE_SELECTION", 0));
    }

    public Rect rotate_rect(Rect rect_in, int width, int height) {
        Rect rect = new Rect();
        int bottom = rect_in.bottom;
        int left = rect_in.left;
        int top = rect_in.top;
        int right = rect_in.right;
        Log.i(this.TAG, "before  bottom = " + bottom + " left = " + left + " top = " + top + "right = " + right);
        if (6 == this.mAngle) {
            rect.left = height - bottom;
            rect.top = left;
            rect.right = height - top;
            rect.bottom = right;
        } else if (3 == this.mAngle) {
            rect.left = width - right;
            rect.top = height - bottom;
            rect.right = width - left;
            rect.bottom = height - top;
        } else if (8 == this.mAngle) {
            rect.left = top;
            rect.top = width - right;
            rect.right = bottom;
            rect.bottom = width - left;
        } else {
            rect.left = left;
            rect.top = top;
            rect.right = right;
            rect.bottom = bottom;
        }
        Log.i(this.TAG, "before  bottom = " + rect.bottom + " left = " + rect.left + " top = " + rect.top + "right = " + rect.right);
        return rect;
    }

    public Rect rotate_rect_inv(Rect rect_in, int width_original, int height_original) {
        Rect rect = new Rect();
        int bottom = rect_in.bottom;
        int left = rect_in.left;
        int top = rect_in.top;
        int right = rect_in.right;
        if (6 == this.mAngle) {
            Log.d(this.TAG, "spin_rect_inv rotate 90");
            rect.bottom = height_original - left;
            rect.left = top;
            rect.top = height_original - right;
            rect.right = bottom;
        } else if (3 == this.mAngle) {
            Log.d(this.TAG, "spin_rect_inv rotate 180");
            rect.right = width_original - left;
            rect.bottom = height_original - top;
            rect.left = width_original - right;
            rect.top = height_original - bottom;
        } else if (8 == this.mAngle) {
            Log.d(this.TAG, "spin_rect_inv rotate 270");
            rect.top = left;
            rect.right = width_original - top;
            rect.bottom = right;
            rect.left = width_original - bottom;
        } else {
            rect.left = left;
            rect.top = top;
            rect.right = right;
            rect.bottom = bottom;
        }
        return rect;
    }

    private void setFooterGuide() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (true == PortraitBeautyConstants.ZOOM_MODE_LAYOUT_VISIBILITY) {
            if (1 == Environment.getVersionOfHW()) {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_SELECT_PERSON_FGUIDE_EYCS));
            } else {
                this.mFooterGuide.setData(new FooterGuideDataResId(AppContext.getAppContext(), R.string.STRID_FUNC_SELFIE_SELECT_PERSON_FGUIDE_PJONE));
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mOptImageOriginal != null) {
            deInitializeReImagingTask();
            closeLayout();
            PortraitBeautyConstants.isNoFaceCautionVisible = false;
            ImageEditor.releaseOptImage(this.mOptImageCL);
            this.mOptImageCL = getCopyImage2(this.mOptImageOriginal);
            if (this.mOptImageCL == null) {
                Log.e(this.TAG, "reImaging mOptImageCL is null");
            }
            applyCLImageFilter(this.mModeSelected);
            ImageEditor.releaseOptImage(this.mOptImageEyepat);
            ImageEditor.releaseOptImage(this.mOptImageCropedToShow);
            ImageEditor.releaseOptImage(this.mOptImageOriginal);
            release_mOptImageCropedToShow();
            deInitialize();
            Log.d(this.TAG, "==== onDestroyView:optImageCount\t= " + ImageEditor.optImageCount);
            Log.d(this.TAG, "onDestroyView end");
            sSelectedOptimizedImage = this.mOptImageCL;
            if (PortraitBeautyUtil.getInstance().isPowerKeyPressed() && this.mOptImageCL != null) {
                this.mOptImageCL.release();
                this.mOptImageCL = null;
            }
        }
        System.gc();
        reImaging_running = false;
        this.mFooterGuide = null;
        this.mCurrentView = null;
        isreturningfromPreview = false;
        super.onDestroy();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private Vector<Rect> getFaceList() {
        Vector<Rect> faceList = new Vector<>();
        for (int i = 0; i < this.mFaceNum; i++) {
            if (this.mColorSelected == this.mDrawView[i].getColor()) {
                faceList.add(this.mFaces[i].face.rect);
            }
        }
        return faceList;
    }

    private void move_CL_position_oneUnit_Org(String direction) {
        int unit;
        if (mIsLeftEyeSelected) {
            unit = this.mLeftEyeUnits[this.mFaceSelection];
        } else {
            unit = this.mRightEyeUnits[this.mFaceSelection];
        }
        if (direction.equals("up")) {
            if (mIsLeftEyeSelected) {
                this.mLeftEyePoints[this.mFaceSelection].y -= unit;
                if (this.mLeftEyePoints[this.mFaceSelection].y < this.mLeftEyeRects[this.mFaceSelection].top) {
                    this.mLeftEyePoints[this.mFaceSelection].y = this.mLeftEyeRects[this.mFaceSelection].top;
                }
            } else {
                this.mRightEyePoints[this.mFaceSelection].y -= unit;
                if (this.mRightEyePoints[this.mFaceSelection].y < this.mRightEyeRects[this.mFaceSelection].top) {
                    this.mRightEyePoints[this.mFaceSelection].y = this.mRightEyeRects[this.mFaceSelection].top;
                }
            }
        }
        if (direction.equals("down")) {
            if (mIsLeftEyeSelected) {
                this.mLeftEyePoints[this.mFaceSelection].y += unit;
                if (this.mLeftEyePoints[this.mFaceSelection].y > this.mLeftEyeRects[this.mFaceSelection].bottom) {
                    this.mLeftEyePoints[this.mFaceSelection].y = this.mLeftEyeRects[this.mFaceSelection].bottom;
                }
            } else {
                this.mRightEyePoints[this.mFaceSelection].y += unit;
                if (this.mRightEyePoints[this.mFaceSelection].y > this.mRightEyeRects[this.mFaceSelection].bottom) {
                    this.mRightEyePoints[this.mFaceSelection].y = this.mRightEyeRects[this.mFaceSelection].bottom;
                }
            }
        }
        if (direction.equals(PictureEffectController.MINIATURE_LEFT)) {
            if (mIsLeftEyeSelected) {
                this.mLeftEyePoints[this.mFaceSelection].x -= unit;
                if (this.mLeftEyePoints[this.mFaceSelection].x < this.mLeftEyeRects[this.mFaceSelection].left) {
                    this.mLeftEyePoints[this.mFaceSelection].x = this.mLeftEyeRects[this.mFaceSelection].left;
                }
            } else {
                this.mRightEyePoints[this.mFaceSelection].x -= unit;
                if (this.mRightEyePoints[this.mFaceSelection].x < this.mRightEyeRects[this.mFaceSelection].left) {
                    this.mRightEyePoints[this.mFaceSelection].x = this.mRightEyeRects[this.mFaceSelection].left;
                }
            }
        }
        if (direction.equals(PictureEffectController.MINIATURE_RIGHT)) {
            if (mIsLeftEyeSelected) {
                this.mLeftEyePoints[this.mFaceSelection].x += unit;
                if (this.mLeftEyePoints[this.mFaceSelection].x > this.mLeftEyeRects[this.mFaceSelection].right) {
                    this.mLeftEyePoints[this.mFaceSelection].x = this.mLeftEyeRects[this.mFaceSelection].right;
                }
            } else {
                this.mRightEyePoints[this.mFaceSelection].x += unit;
                if (this.mRightEyePoints[this.mFaceSelection].x > this.mRightEyeRects[this.mFaceSelection].right) {
                    this.mRightEyePoints[this.mFaceSelection].x = this.mRightEyeRects[this.mFaceSelection].right;
                }
            }
        }
        reImaging_otherThread();
        update_arrow_position();
        lightArrow_hide_and_delay_shower(PortraitBeautyConstants.THREE_SECOND_MILLIS);
    }

    private void move_CL_position_oneUnit(String direction) {
        int height;
        if (1 == this.mAngle || 3 == this.mAngle) {
            this.mOptImageOriginal.getWidth();
            height = this.mOptImageOriginal.getHeight();
        } else {
            height = this.mOptImageOriginal.getWidth();
            this.mOptImageOriginal.getHeight();
        }
        int unit = mIsLeftEyeSelected ? this.mLeftEyeUnits[this.mFaceSelection] : this.mRightEyeUnits[this.mFaceSelection];
        Rect target_eye_rect = new Rect(mIsLeftEyeSelected ? this.mLeftEyeRects[this.mFaceSelection] : this.mRightEyeRects[this.mFaceSelection]);
        Log.d(this.TAG, "move_CL_position_oneUnit: mFaceRects_org[ mFaceSelection]:" + this.mFaceRects_org[this.mFaceSelection] + "  target_eye_rect:" + target_eye_rect);
        if (!this.mFaceRects_crop[this.mFaceSelection].contains(target_eye_rect.left - ((direction.equals(PictureEffectController.MINIATURE_LEFT) ? 1 : 0) * unit), target_eye_rect.top - ((direction.equals("up") ? 1 : 0) * unit), ((direction.equals(PictureEffectController.MINIATURE_RIGHT) ? 1 : 0) * unit) + target_eye_rect.right, ((direction.equals("down") ? 1 : 0) * unit) + target_eye_rect.bottom)) {
            Log.w(this.TAG, "move_CL_position_oneUnit  skipped");
            return;
        }
        if (mIsLeftEyeSelected) {
            if (direction.equals("up")) {
                this.mLeftEyePoints[this.mFaceSelection].y -= unit;
                if (this.mLeftEyePoints[this.mFaceSelection].y < this.mLeftEyeRects[this.mFaceSelection].top) {
                    this.mLeftEyePoints[this.mFaceSelection].y = this.mLeftEyeRects[this.mFaceSelection].top;
                }
                if (this.mLeftEyeRects[this.mFaceSelection].top > unit) {
                    this.mLeftEyeRects[this.mFaceSelection].top -= unit;
                    this.mLeftEyeRects[this.mFaceSelection].bottom -= unit;
                }
            }
            if (direction.equals("down")) {
                this.mLeftEyePoints[this.mFaceSelection].y += unit;
                if (this.mLeftEyePoints[this.mFaceSelection].y > this.mLeftEyeRects[this.mFaceSelection].bottom) {
                    this.mLeftEyePoints[this.mFaceSelection].y = this.mLeftEyeRects[this.mFaceSelection].bottom;
                }
                if (this.mLeftEyeRects[this.mFaceSelection].bottom + unit < height) {
                    this.mLeftEyeRects[this.mFaceSelection].top += unit;
                    this.mLeftEyeRects[this.mFaceSelection].bottom += unit;
                }
            }
            if (direction.equals(PictureEffectController.MINIATURE_LEFT)) {
                this.mLeftEyePoints[this.mFaceSelection].x -= unit;
                if (this.mLeftEyeRects[this.mFaceSelection].left > this.mFaceRects_crop[this.mFaceSelection].left) {
                    this.mLeftEyeRects[this.mFaceSelection].left -= unit;
                    this.mLeftEyeRects[this.mFaceSelection].right -= unit;
                } else {
                    this.mLeftEyePoints[this.mFaceSelection].x += unit;
                }
            }
            if (direction.equals(PictureEffectController.MINIATURE_RIGHT)) {
                this.mLeftEyePoints[this.mFaceSelection].x += unit;
                if (this.mLeftEyeRects[this.mFaceSelection].right + unit < this.mFaceRects_crop[this.mFaceSelection].right) {
                    this.mLeftEyeRects[this.mFaceSelection].left += unit;
                    this.mLeftEyeRects[this.mFaceSelection].right += unit;
                } else {
                    this.mLeftEyePoints[this.mFaceSelection].x -= unit;
                }
            }
        } else {
            if (direction.equals("up")) {
                this.mRightEyePoints[this.mFaceSelection].y -= unit;
                if (this.mRightEyePoints[this.mFaceSelection].y < this.mRightEyeRects[this.mFaceSelection].top) {
                    this.mRightEyePoints[this.mFaceSelection].y = this.mRightEyeRects[this.mFaceSelection].top;
                }
                if (this.mRightEyeRects[this.mFaceSelection].top > unit) {
                    this.mRightEyeRects[this.mFaceSelection].top -= unit;
                    this.mRightEyeRects[this.mFaceSelection].bottom -= unit;
                }
            }
            if (direction.equals("down")) {
                this.mRightEyePoints[this.mFaceSelection].y += unit;
                if (this.mRightEyePoints[this.mFaceSelection].y > this.mRightEyeRects[this.mFaceSelection].bottom) {
                    this.mRightEyePoints[this.mFaceSelection].y = this.mRightEyeRects[this.mFaceSelection].bottom;
                }
                if (this.mRightEyeRects[this.mFaceSelection].bottom + unit < height) {
                    this.mRightEyeRects[this.mFaceSelection].top += unit;
                    this.mRightEyeRects[this.mFaceSelection].bottom += unit;
                }
            }
            if (direction.equals(PictureEffectController.MINIATURE_LEFT)) {
                this.mRightEyePoints[this.mFaceSelection].x -= unit;
                if (this.mRightEyeRects[this.mFaceSelection].left > this.mFaceRects_crop[this.mFaceSelection].left) {
                    this.mRightEyeRects[this.mFaceSelection].left -= unit;
                    this.mRightEyeRects[this.mFaceSelection].right -= unit;
                } else {
                    this.mRightEyePoints[this.mFaceSelection].x += unit;
                }
            }
            if (direction.equals(PictureEffectController.MINIATURE_RIGHT)) {
                this.mRightEyePoints[this.mFaceSelection].x += unit;
                if (this.mRightEyeRects[this.mFaceSelection].right + unit < this.mFaceRects_crop[this.mFaceSelection].right) {
                    this.mRightEyeRects[this.mFaceSelection].left += unit;
                    this.mRightEyeRects[this.mFaceSelection].right += unit;
                } else {
                    this.mRightEyePoints[this.mFaceSelection].x -= unit;
                }
            }
        }
        reImaging_otherThread();
        update_arrow_position();
        lightArrow_hide_and_delay_shower(PortraitBeautyConstants.THREE_SECOND_MILLIS);
    }

    private void update_arrow_position() {
        int mLightArrowsPos_left;
        int mLightArrowsPos_top;
        int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
        if (this.mFaceNum > 0) {
            if (mIsLeftEyeSelected) {
                mLightArrowsPos_left = 0 + 81;
                mLightArrowsPos_top = 0 + mOutput_eye_top;
                if (device == 2 && 3 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                    mLightArrowsPos_left -= 30;
                }
            } else {
                mLightArrowsPos_left = 0 + 312;
                mLightArrowsPos_top = 0 + mOutput_eye_top;
                if (device == 2 && 3 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                    mLightArrowsPos_left -= 10;
                }
            }
            this.mLightArrowsPos.setMargins(mLightArrowsPos_left, mLightArrowsPos_top, 0, 0);
            mCatchlight_focus_arrow.setLayoutParams(this.mLightArrowsPos);
        }
    }

    private void deInitialize() {
        this.mFaceSelection = 0;
        if (this.mCatchLightImageView != null) {
            this.mCatchLightImageView.release();
            this.mCatchLightImageView = null;
        }
        this.mCatchLightImageLayoutParam = null;
        for (int i = 0; i < this.mFaceNum; i++) {
            this.mFrameLayout.removeView(this.mDrawView[i]);
            this.mDrawView[i] = null;
            this.mFrameLayout.removeView(this.mDrawViewL[i]);
            this.mDrawViewL[i] = null;
            this.mFrameLayout.removeView(this.mDrawViewR[i]);
            this.mDrawViewR[i] = null;
        }
        if (mCatchlight_focus_arrow != null) {
            mCatchlight_focus_arrow = null;
        }
        this.mDrawView = null;
        this.mDrawViewL = null;
        this.mDrawViewR = null;
        if (this.mFaces != null) {
            int len = this.mFaces.length;
            for (int i2 = 0; i2 < len; i2++) {
                this.mFaces[i2] = null;
            }
        }
        if (this.mLeftEyeRects != null) {
            int len2 = this.mLeftEyeRects.length;
            for (int i3 = 0; i3 < len2; i3++) {
                this.mLeftEyeRects[i3] = null;
            }
        }
        if (this.mRightEyeRects != null) {
            int len3 = this.mRightEyeRects.length;
            for (int i4 = 0; i4 < len3; i4++) {
                this.mRightEyeRects[i4] = null;
            }
        }
        if (this.handler != null) {
            this.handler.removeCallbacks(this.delayFunc);
            this.handler = null;
        }
        this.mIsoInfo = 0;
        this.mFrameLayout = null;
        this.mCurrentX = 0;
        this.mCurrentY = 0;
        this.mFaceNum = 0;
        this.mModeSelected = Mode.OFF.code;
        this.mContentManager = null;
        this.mContentInfo = null;
        if (this.mLeftEyeBitmapList != null) {
            releaseAllBitmap(this.mLeftEyeBitmapList);
        }
        if (this.mRightEyeBitmapList != null) {
            releaseAllBitmap(this.mRightEyeBitmapList);
        }
        if (this.mLeftEyeBitmapListOrg != null) {
            releaseAllBitmap(this.mLeftEyeBitmapListOrg);
        }
        if (this.mRightEyeBitmapListOrg != null) {
            releaseAllBitmap(this.mRightEyeBitmapListOrg);
        }
    }

    public void releaseAllBitmap(LinkedList<Bitmap> bitmapList) {
        Log.d(this.TAG, "createEyeImages-> releaseAllBitmap called");
        if (bitmapList != null) {
            int size = bitmapList.size();
            for (int i = 0; i < size; i++) {
                Bitmap bmp = bitmapList.get(i);
                if (!bmp.isRecycled()) {
                    bmp.recycle();
                }
            }
            bitmapList.clear();
            Log.d(this.TAG, "createEyeImages-> releaseAllBitmap 5");
            System.gc();
        }
        Log.d(this.TAG, "createEyeImages-> releaseAllBitmap 6");
    }

    private void createEyeRects() {
        float originalImgWidth;
        float originalImgHeight;
        int size_x;
        int size_y;
        Log.d(this.TAG, "createEyeRects called");
        if (this.mAngle == 6 || this.mAngle == 8) {
            originalImgWidth = this.mOptImageOriginal.getHeight();
            originalImgHeight = this.mOptImageOriginal.getWidth();
        } else {
            originalImgWidth = this.mOptImageOriginal.getWidth();
            originalImgHeight = this.mOptImageOriginal.getHeight();
        }
        float scaleX = 2000.0f / originalImgWidth;
        float scaleY = 2000.0f / originalImgHeight;
        new Rect();
        Log.d(this.TAG, "createEyeRects mFaceNum:" + this.mFaceNum);
        for (int i = 0; i < this.mFaceNum; i++) {
            FaceDetailScalarA detailFace = (FaceDetailScalarA) this.mFaces[i].detail;
            int activeShapeNum = detailFace.activeShapeNum;
            boolean attributeValid = detailFace.attributeValid;
            boolean shapeValid = detailFace.shapeValid;
            Log.d("createEyeRects activeShapeNum:" + i, "[" + activeShapeNum + "]");
            Log.d("createEyeRects attributeValid:" + i, "[" + attributeValid + "]");
            Log.d("createEyeRects shapeValid:" + i, "[" + shapeValid + "]");
            float f = detailFace.shapePoint[32];
            float f2 = detailFace.shapePoint[33];
            float f3 = detailFace.shapePoint[34];
            float f4 = detailFace.shapePoint[35];
            float p18x = detailFace.shapePoint[36];
            float p18y = detailFace.shapePoint[37];
            float p19x = detailFace.shapePoint[38];
            float p19y = detailFace.shapePoint[39];
            float p20x = detailFace.shapePoint[40];
            float p20y = detailFace.shapePoint[41];
            float p21x = detailFace.shapePoint[42];
            float p21y = detailFace.shapePoint[43];
            float p22x = detailFace.shapePoint[44];
            float p22y = detailFace.shapePoint[45];
            float p23x = detailFace.shapePoint[46];
            float p23y = detailFace.shapePoint[47];
            float f5 = detailFace.shapePoint[48];
            float f6 = detailFace.shapePoint[49];
            float f7 = detailFace.shapePoint[50];
            float f8 = detailFace.shapePoint[51];
            float p26x = detailFace.shapePoint[52];
            float p26y = detailFace.shapePoint[53];
            float p27x = detailFace.shapePoint[54];
            float p27y = detailFace.shapePoint[55];
            float p28x = detailFace.shapePoint[56];
            float p28y = detailFace.shapePoint[57];
            float p29x = detailFace.shapePoint[58];
            float p29y = detailFace.shapePoint[59];
            float p30x = detailFace.shapePoint[60];
            float p30y = detailFace.shapePoint[61];
            float p31x = detailFace.shapePoint[62];
            float p31y = detailFace.shapePoint[63];
            float l_size_x = ((Math.abs(p23x - p22x) + Math.abs(p21x - p20x)) + Math.abs(p21x - p20x)) / 3.0f;
            float r_size_x = ((Math.abs(p27x - p26x) + Math.abs(p29x - p28x)) + Math.abs(p31x - p30x)) / 3.0f;
            float l_size_y = ((Math.abs(p23y - p22y) + Math.abs(p21y - p20y)) + Math.abs(p21y - p20y)) / 3.0f;
            float r_size_y = ((Math.abs(p27y - p26y) + Math.abs(p29y - p28y)) + Math.abs(p31y - p30y)) / 3.0f;
            float l_center_x = (1000.0f + ((((((p18x + p20x) + p22x) + p19x) + p21x) + p23x) / 6.0f)) / scaleX;
            float l_center_y = (1000.0f + ((((((p18y + p19y) + p20y) + p21y) + p22y) + p23y) / 6.0f)) / scaleY;
            float r_center_x = (1000.0f + ((((((p26x + p28x) + p30x) + p27x) + p29x) + p31x) / 6.0f)) / scaleX;
            float r_center_y = (1000.0f + ((((((p26y + p27y) + p28y) + p29y) + p30y) + p31y) / 6.0f)) / scaleY;
            float l_size_y2 = l_size_y / scaleY;
            Log.d("coordinates org leftC,S  :" + i, LogHelper.MSG_OPEN_BRACKET + ((int) l_center_x) + "," + ((int) l_center_y) + LogHelper.MSG_CLOSE_BRACKET + "[" + ((int) (l_size_x / scaleX)) + "," + ((int) l_size_y2) + "]");
            Log.d("coordinates org rightC,S :" + i, LogHelper.MSG_OPEN_BRACKET + ((int) r_center_x) + "," + ((int) r_center_y) + LogHelper.MSG_CLOSE_BRACKET + "[" + ((int) (r_size_x / scaleX)) + "," + ((int) l_size_y2) + "]");
            float eyesize = (float) (Math.max(Math.max(Math.max(l_size_y2, r_size_y / scaleY), r23), r65) * 0.9d);
            Log.d("coordinates size         :" + i, "[" + ((int) eyesize) + "]");
            float f_left = (this.mFaces[i].face.rect.left + 1000) / scaleX;
            float f_right = (this.mFaces[i].face.rect.right + 1000) / scaleX;
            float f_top = (this.mFaces[i].face.rect.top + 1000) / scaleY;
            float f_bottom = (this.mFaces[i].face.rect.bottom + 1000) / scaleY;
            Log.d("coordinates face org     :" + i, LogHelper.MSG_OPEN_BRACKET + ((int) f_left) + "," + ((int) f_top) + ")-(" + ((int) f_right) + "," + ((int) f_bottom) + LogHelper.MSG_CLOSE_BRACKET);
            int errflag = 0;
            if (l_center_x < f_left || l_center_x > f_right || l_center_y < f_top || l_center_y > f_bottom) {
                errflag = 1;
            }
            if (r_center_x < f_left || r_center_x > f_right || r_center_y < f_top || r_center_y > f_bottom) {
                errflag = 1;
            }
            if (eyesize > (f_right - f_left) / 3.0f) {
                errflag = 1;
            }
            if (!attributeValid) {
                errflag = 1;
            }
            Log.d("coordinates errflag      :" + i, LogHelper.MSG_OPEN_BRACKET + errflag + LogHelper.MSG_CLOSE_BRACKET);
            if (errflag == 1) {
                float l_size_x2 = Math.abs(f_right - f_left) / 16.0f;
                float r_size_x2 = Math.abs(f_right - f_left) / 16.0f;
                float l_size_y3 = Math.abs(f_bottom - f_top) / 16.0f;
                float r_size_y2 = Math.abs(f_bottom - f_top) / 16.0f;
                eyesize = (float) (Math.max(Math.max(Math.max(l_size_y3, r_size_y2), l_size_x2), r_size_x2) * 0.9d);
            }
            if (errflag == 1) {
                switch (this.mAngle) {
                    case 1:
                        l_center_x = f_left + (((f_right - f_left) / 32.0f) * 10.0f);
                        r_center_x = f_left + (((f_right - f_left) / 32.0f) * 20.0f);
                        l_center_y = f_top + (((f_bottom - f_top) / 32.0f) * 12.0f);
                        r_center_y = l_center_y;
                        Log.d("orient 0                 :" + i, "[]");
                        break;
                    case 6:
                        r_center_x = f_left + (((f_right - f_left) / 32.0f) * 12.0f);
                        l_center_x = r_center_x;
                        r_center_y = f_top + (((f_bottom - f_top) / 32.0f) * 5.0f);
                        l_center_y = f_top + (((f_bottom - f_top) / 32.0f) * 18.0f);
                        Log.d("orient 90                 :" + i, "[]");
                        break;
                    case 8:
                        r_center_x = f_left + (((f_right - f_left) / 32.0f) * 20.0f);
                        l_center_x = r_center_x;
                        r_center_y = f_top + (((f_bottom - f_top) / 32.0f) * 18.0f);
                        l_center_y = f_top + (((f_bottom - f_top) / 32.0f) * 5.0f);
                        Log.d("orient 180                 :" + i, "[]");
                        break;
                    default:
                        l_center_x = f_left + (((f_right - f_left) / 32.0f) * 25.0f);
                        r_center_x = f_left + (((f_right - f_left) / 32.0f) * 15.0f);
                        l_center_y = f_top + (((f_bottom - f_top) / 32.0f) * 12.0f);
                        r_center_y = l_center_y;
                        Log.d("orient 180                 :" + i, "[]");
                        break;
                }
            }
            Log.d("coordinates aft size     :" + i, "[" + ((int) eyesize) + "]");
            Log.d("coordinates aft lefteyeC :" + i, LogHelper.MSG_OPEN_BRACKET + ((int) l_center_x) + "," + ((int) l_center_y) + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("coordinates aft righteyeC:" + i, LogHelper.MSG_OPEN_BRACKET + ((int) r_center_x) + "," + ((int) r_center_y) + LogHelper.MSG_CLOSE_BRACKET);
            if (1 == this.mAngle || 3 == this.mAngle) {
                size_x = ((((int) (4.0f * eyesize)) / 4) + 1) * 4;
                size_y = size_x / 2;
            } else {
                int size_y2 = (int) (4.0f * eyesize);
                size_y = ((size_y2 / 4) + 1) * 4;
                size_x = size_y / 2;
            }
            float l_left = (((l_center_x - (size_x / 2)) / 4.0f) + 1.0f) * 4.0f;
            float l_right = l_left + size_x;
            float l_top = l_center_y - (size_y / 2);
            float l_bottom = l_top + size_y;
            float r_left = r_center_x - (size_x / 2);
            float r_right = r_left + size_x;
            float r_top = r_center_y - (size_y / 2);
            float r_bottom = r_top + size_y;
            Log.d("real lefteyerect         :" + i, LogHelper.MSG_OPEN_BRACKET + ((int) l_left) + "," + ((int) l_top) + ")-(" + ((int) l_right) + "," + ((int) l_bottom) + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("real righteyerect        :" + i, LogHelper.MSG_OPEN_BRACKET + ((int) r_left) + "," + ((int) r_top) + ")-(" + ((int) r_right) + "," + ((int) r_bottom) + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("real eyesize             :" + i, LogHelper.MSG_OPEN_BRACKET + eyesize + LogHelper.MSG_CLOSE_BRACKET);
            if (l_right - l_left < 24.0f) {
                l_left = l_center_x - 12.0f;
                l_right = l_center_x + 12.0f;
            }
            if (r_right - r_left < 24.0f) {
                r_left = r_center_x - 12.0f;
                r_right = r_center_x + 12.0f;
            }
            if (l_bottom - l_top < 16.0f) {
                l_top = l_center_y - 8.0f;
                l_bottom = l_center_y + 8.0f;
            }
            if (r_bottom - r_top < 16.0f) {
                r_top = r_center_y - 8.0f;
                r_bottom = r_center_y + 8.0f;
            }
            Log.d("last lefteyerect         :" + i, LogHelper.MSG_OPEN_BRACKET + ((int) l_left) + "," + ((int) l_top) + ")-(" + ((int) l_right) + "," + ((int) l_bottom) + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("last righteyerect        :" + i, LogHelper.MSG_OPEN_BRACKET + ((int) r_left) + "," + ((int) r_top) + ")-(" + ((int) r_right) + "," + ((int) r_bottom) + LogHelper.MSG_CLOSE_BRACKET);
            this.mLeftEyeSizes[i] = ((((int) eyesize) / 16) + 1) * 16;
            this.mRightEyeSizes[i] = this.mLeftEyeSizes[i];
            this.mLeftEyeUnits[i] = this.mLeftEyeSizes[i] / 16;
            this.mRightEyeUnits[i] = this.mLeftEyeUnits[i];
            if (this.mLeftEyeUnits[i] == 1) {
                this.mGauzebarUnit[i] = 12;
            } else if (this.mLeftEyeUnits[i] == 2 || this.mLeftEyeUnits[i] == 3) {
                this.mGauzebarUnit[i] = 14;
            } else {
                this.mGauzebarUnit[i] = 15;
            }
            this.sGuzebarCurrentLevel[i] = (this.mLeftEyeSizes[i] - 16) / (this.mLeftEyeUnits[i] * 4);
            if (this.sGuzebarCurrentLevel[i] <= 0) {
                this.sGuzebarCurrentLevel[i] = 0;
            }
            this.mLeftEyeRects[i].left = (int) l_left;
            this.mLeftEyeRects[i].top = (int) l_top;
            this.mLeftEyeRects[i].right = (int) l_right;
            this.mLeftEyeRects[i].bottom = (int) l_bottom;
            this.mLeftEyePoints[i].x = (int) l_center_x;
            this.mLeftEyePoints[i].y = (int) l_center_y;
            this.mRightEyeRects[i].left = (int) r_left;
            this.mRightEyeRects[i].top = (int) r_top;
            this.mRightEyeRects[i].right = (int) r_right;
            this.mRightEyeRects[i].bottom = (int) r_bottom;
            this.mRightEyePoints[i].x = (int) r_center_x;
            this.mRightEyePoints[i].y = (int) r_center_y;
            if (2 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                if (1 == this.mAngle || 3 == this.mAngle) {
                    int diff = this.mLeftEyeRects[i].width() / 3;
                    int diff_left = diff / 2;
                    int diff_right = diff - diff_left;
                    this.mLeftEyeRects[i].left -= diff_left;
                    this.mLeftEyeRects[i].right += diff_right;
                    if (this.mLeftEyeRects[i].left < 0) {
                        this.mLeftEyeRects[i].right = this.mLeftEyeRects[i].width();
                        this.mLeftEyeRects[i].left = 0;
                    }
                    if (this.mOriginalWidth < this.mLeftEyeRects[i].right) {
                        this.mLeftEyeRects[i].left = this.mOriginalWidth - this.mLeftEyeRects[i].width();
                        this.mLeftEyeRects[i].right = this.mOriginalWidth;
                    }
                } else {
                    int diff2 = this.mLeftEyeRects[i].height() / 3;
                    int diff_top = diff2 / 2;
                    int diff_bottom = diff2 - diff_top;
                    this.mLeftEyeRects[i].top -= diff_top;
                    this.mLeftEyeRects[i].bottom += diff_bottom;
                    if (this.mLeftEyeRects[i].top < 0) {
                        this.mLeftEyeRects[i].bottom = this.mLeftEyeRects[i].width();
                        this.mLeftEyeRects[i].top = 0;
                    }
                    if (this.mOriginalHeight < this.mLeftEyeRects[i].bottom) {
                        this.mLeftEyeRects[i].top = this.mOriginalHeight - this.mLeftEyeRects[i].width();
                        this.mLeftEyeRects[i].bottom = this.mOriginalHeight;
                    }
                }
                if (1 == this.mAngle || 3 == this.mAngle) {
                    int diff3 = this.mRightEyeRects[i].width() / 3;
                    int diff_left2 = diff3 / 2;
                    int diff_right2 = diff3 - diff_left2;
                    this.mRightEyeRects[i].left -= diff_left2;
                    this.mRightEyeRects[i].right += diff_right2;
                    if (this.mRightEyeRects[i].left < 0) {
                        this.mRightEyeRects[i].right = this.mRightEyeRects[i].width();
                        this.mRightEyeRects[i].left = 0;
                    }
                    if (this.mOriginalWidth < this.mRightEyeRects[i].right) {
                        this.mRightEyeRects[i].left = this.mOriginalWidth - this.mRightEyeRects[i].width();
                        this.mRightEyeRects[i].right = this.mOriginalWidth;
                    }
                } else {
                    int diff4 = this.mRightEyeRects[i].height() / 3;
                    int diff_top2 = diff4 / 2;
                    int diff_bottom2 = diff4 - diff_top2;
                    this.mRightEyeRects[i].top -= diff_top2;
                    this.mRightEyeRects[i].bottom += diff_bottom2;
                    if (this.mRightEyeRects[i].top < 0) {
                        this.mRightEyeRects[i].bottom = this.mRightEyeRects[i].width();
                        this.mRightEyeRects[i].top = 0;
                    }
                    if (this.mOriginalHeight < this.mRightEyeRects[i].bottom) {
                        this.mRightEyeRects[i].top = this.mOriginalHeight - this.mRightEyeRects[i].width();
                        this.mRightEyeRects[i].bottom = this.mOriginalHeight;
                    }
                }
            }
            Log.d("coordinates lefteye      :" + i, LogHelper.MSG_OPEN_BRACKET + this.mLeftEyeRects[i].left + "," + this.mLeftEyeRects[i].top + ")-(" + this.mLeftEyeRects[i].right + "," + this.mLeftEyeRects[i].bottom + LogHelper.MSG_CLOSE_BRACKET);
            Log.d("coordinates righteye     :" + i, LogHelper.MSG_OPEN_BRACKET + this.mRightEyeRects[i].left + "," + this.mRightEyeRects[i].top + ")-(" + this.mRightEyeRects[i].right + "," + this.mRightEyeRects[i].bottom + LogHelper.MSG_CLOSE_BRACKET);
        }
    }

    private OptimizedImage getCopyImage(OptimizedImage image) {
        OptimizedImage cropImg = null;
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = image.getWidth();
        rect.bottom = image.getHeight();
        CropImageFilter cropFilter = new CropImageFilter();
        cropFilter.setSource(image, false);
        cropFilter.setSrcRect(rect);
        boolean isExecuted = cropFilter.execute();
        if (isExecuted) {
            cropImg = cropFilter.getOutput();
            ImageEditor.optImageCount++;
        }
        cropFilter.clearSources();
        cropFilter.release();
        return cropImg;
    }

    private OptimizedImage getCopyImage2(OptimizedImage image) {
        OptimizedImage copyImg = null;
        int hsize = image.getWidth();
        int vsize = image.getHeight();
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(image, false);
        scaleFilter.setDestSize(hsize, vsize);
        boolean isExecuted = scaleFilter.execute();
        if (isExecuted) {
            copyImg = scaleFilter.getOutput();
            ImageEditor.optImageCount++;
        }
        scaleFilter.clearSources();
        scaleFilter.release();
        return copyImg;
    }

    private OptimizedImage getCropImage(OptimizedImage image, Rect rect) {
        OptimizedImage cropImg = null;
        CropImageFilter cropFilter = new CropImageFilter();
        cropFilter.setSource(image, false);
        cropFilter.setSrcRect(rect);
        boolean isExecuted = cropFilter.execute();
        if (isExecuted) {
            cropImg = cropFilter.getOutput();
            ImageEditor.optImageCount++;
        }
        cropFilter.clearSources();
        cropFilter.release();
        return cropImg;
    }

    private OptimizedImage getCropImage(OptimizedImage image, Rect rect, boolean releaseResource) {
        OptimizedImage cropImg = null;
        CropImageFilter cropFilter = new CropImageFilter();
        cropFilter.setSource(image, releaseResource);
        cropFilter.setSrcRect(rect);
        boolean isExecuted = cropFilter.execute();
        if (isExecuted) {
            cropImg = cropFilter.getOutput();
            ImageEditor.optImageCount++;
        }
        cropFilter.clearSources();
        cropFilter.release();
        return cropImg;
    }

    private OptimizedImage getResizeImage(OptimizedImage image, int hsize, int vsize) {
        OptimizedImage resizeImg = null;
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(image, false);
        scaleFilter.setDestSize(hsize, vsize);
        boolean isExecuted = scaleFilter.execute();
        if (isExecuted) {
            resizeImg = scaleFilter.getOutput();
            ImageEditor.optImageCount++;
        }
        scaleFilter.clearSources();
        scaleFilter.release();
        return resizeImg;
    }

    private OptimizedImage getRotateImage(OptimizedImage image, RotateImageFilter.ROTATION_DEGREE degree) {
        OptimizedImage rotateImage = null;
        RotateImageFilter rotateFilter = new RotateImageFilter();
        rotateFilter.setTrimMode(1);
        rotateFilter.setRotation(degree);
        rotateFilter.setSource(image, true);
        boolean isRotateExecuted = rotateFilter.execute();
        if (isRotateExecuted) {
            rotateImage = rotateFilter.getOutput();
            ImageEditor.optImageCount++;
        }
        rotateFilter.clearSources();
        rotateFilter.release();
        return rotateImage;
    }

    private void changeLightPattern() {
        Log.d(this.TAG, "changeLightPattern called");
        int[] inputArray3 = new int[2];
        Log.d(this.TAG, "dsp will make");
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        Log.d(this.TAG, "dsp is maked");
        int[] inputArray1 = {dsp.getPropertyAsInt(this.mOptImageEyepat, "memory-address"), this.mOptImageEyepat.getWidth(), this.mOptImageEyepat.getHeight(), dsp.getPropertyAsInt(this.mOptImageEyepat, "image-canvas-width")};
        int[] inputArray2 = {this.mLightPatternSelection, 0, 0};
        Log.d(this.TAG, "setLightPattern will execute");
        setLightPattern(inputArray1, inputArray2, inputArray3);
        Log.d(this.TAG, "setLightPattern is complete");
        OptimizedImage optImageCLPaternRotated = getRotateImage(this.mOptImageEyepat, RotateImageFilter.ROTATION_DEGREE.DEGREE_0);
        if (optImageCLPaternRotated != null) {
            ImageEditor.releaseOptImage(this.mOptImageEyepat);
            this.mOptImageEyepat = optImageCLPaternRotated;
        }
        if (dsp != null) {
            dsp.release();
        }
    }

    private void addPattern(OptimizedImage image1, OptimizedImage image2, int startx, int starty, int briteness) {
        Log.d(this.TAG, "dsp will make");
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        Log.d(this.TAG, "dsp is maked");
        int[] inputArray1 = {dsp.getPropertyAsInt(image1, "memory-address"), image1.getWidth(), image1.getHeight(), dsp.getPropertyAsInt(image1, "image-canvas-width")};
        int[] inputArray2 = {dsp.getPropertyAsInt(image2, "memory-address"), image2.getWidth(), image2.getHeight(), dsp.getPropertyAsInt(image2, "image-canvas-width")};
        int[] inputArray3 = {startx, starty, briteness};
        Log.d(this.TAG, "==== inputArray1[0] image1 adrs    \t= " + inputArray1[0]);
        Log.d(this.TAG, "==== inputArray1[1] image1 sizeh   \t= " + inputArray1[1]);
        Log.d(this.TAG, "==== inputArray1[2] image1 sizev   \t= " + inputArray1[2]);
        Log.d(this.TAG, "==== inputArray1[3] image1 linesize\t= " + inputArray1[3]);
        Log.d(this.TAG, "==== inputArray2[0] image2 adrs    \t= " + inputArray2[0]);
        Log.d(this.TAG, "==== inputArray2[1] image2 sizeh   \t= " + inputArray2[1]);
        Log.d(this.TAG, "==== inputArray2[2] image2 sizev   \t= " + inputArray2[2]);
        Log.d(this.TAG, "==== inputArray2[3] image2 linesize\t= " + inputArray2[3]);
        Log.d(this.TAG, "==== inputArray3[0] startx         \t= " + inputArray3[0]);
        Log.d(this.TAG, "==== inputArray3[1] starty         \t= " + inputArray3[1]);
        Log.d(this.TAG, "==== inputArray3[2] briteness      \t= " + inputArray3[2]);
        Log.d(this.TAG, "umekomiYuv will execute");
        umekomiYuv(inputArray1, inputArray2, inputArray3);
        Log.d(this.TAG, "umekomiYuv is complete");
        if (dsp != null) {
            dsp.release();
        }
    }

    private void copyImage(OptimizedImage image1, OptimizedImage image2) {
        Log.d(this.TAG, "dsp will make");
        DSP dsp = DSP.createProcessor("sony-di-dsp");
        Log.d(this.TAG, "dsp is maked");
        int[] inputArray1 = {dsp.getPropertyAsInt(image1, "memory-address"), image1.getWidth(), image1.getHeight(), dsp.getPropertyAsInt(image1, "image-canvas-width")};
        int[] inputArray2 = {dsp.getPropertyAsInt(image2, "memory-address"), image2.getWidth(), image2.getHeight(), dsp.getPropertyAsInt(image2, "image-canvas-width")};
        int[] inputArray3 = {0, 0, 0};
        Log.d(this.TAG, "==== inputArray1[0] image1 adrs    \t= " + inputArray1[0]);
        Log.d(this.TAG, "==== inputArray1[1] image1 sizeh   \t= " + inputArray1[1]);
        Log.d(this.TAG, "==== inputArray1[2] image1 sizev   \t= " + inputArray1[2]);
        Log.d(this.TAG, "==== inputArray1[3] image1 linesize\t= " + inputArray1[3]);
        Log.d(this.TAG, "==== inputArray2[0] image2 adrs    \t= " + inputArray2[0]);
        Log.d(this.TAG, "==== inputArray2[1] image2 sizeh   \t= " + inputArray2[1]);
        Log.d(this.TAG, "==== inputArray2[2] image2 sizev   \t= " + inputArray2[2]);
        Log.d(this.TAG, "==== inputArray2[3] image2 linesize\t= " + inputArray2[3]);
        Log.d(this.TAG, "copyYuv will execute");
        copyYuv(inputArray1, inputArray2, inputArray3);
        Log.d(this.TAG, "copyYuv is complete");
        if (dsp != null) {
            dsp.release();
        }
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil.SaveCallback
    public void onSuccess() {
        getLayout(PortraitBeautyUtil.ID_SAVINGLAYOUT).closeLayout();
        ImageEditor.refreshIndexView();
        getHandler().sendEmptyMessage(1000);
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.PortraitBeautyUtil.SaveCallback
    public void onFail() {
        Log.d(this.TAG, "Save failed  ");
        getLayout(PortraitBeautyUtil.ID_SAVINGLAYOUT).closeLayout();
        Bundle bundle = new Bundle();
        bundle.putString(PortraitBeautyUtil.ALERT_MESSAGE, "Temporary Message");
        bundle.putBoolean(PortraitBeautyUtil.ALERT_SWITCHTOINDEXVIEW, true);
        openLayout(PortraitBeautyUtil.ID_MESSAGEALERT, bundle);
    }

    private void closeOpenedLayout() {
        if (getLayout(PortraitBeautyUtil.ID_MESSAGEALERT).getView() != null) {
            getLayout(PortraitBeautyUtil.ID_MESSAGEALERT).closeLayout();
        } else if (getLayout(PortraitBeautyUtil.ID_MESSAGENOFACE).getView() != null) {
            getLayout(PortraitBeautyUtil.ID_MESSAGENOFACE).closeLayout();
        } else if (getLayout(PortraitBeautyUtil.ID_SAVINGLAYOUT).getView() != null) {
            getLayout(PortraitBeautyUtil.ID_SAVINGLAYOUT).closeLayout();
        } else if (getLayout(PortraitBeautyUtil.ID_MANUALSTARTUPMESSAGE).getView() != null) {
            getLayout(PortraitBeautyUtil.ID_MANUALSTARTUPMESSAGE).closeLayout();
        }
        if (getLayout(PortraitBeautyUtil.ID_CONFIRMSAVINGLAYOUT).getView() != null) {
            getLayout(PortraitBeautyUtil.ID_CONFIRMSAVINGLAYOUT).closeLayout();
        }
        this.handler.removeCallbacks(this.delayFunc);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (this.mTxtVwTitle != null) {
            this.mTxtVwTitle.setVisibility(0);
        }
        super.onReopened();
        initializeView();
        initializeReImagingTask();
        initializeData();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode1, KeyEvent event) {
        event.getScanCode();
        return 0;
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onChangePattern(int pattern) {
        applyPatternEffect(pattern);
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onChangeSize(int size) {
        resizePattern(size);
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onChangeBrightness(int brightness) {
        changeBrightness(brightness);
    }

    private int brightness_to_mPatternBriteness_converter(int brightness) {
        if (brightness == 0) {
            return 0;
        }
        if (brightness == 1) {
            return 25;
        }
        if (brightness == 2) {
            return 50;
        }
        if (brightness == 3) {
            return 75;
        }
        if (brightness == 4) {
            return 100;
        }
        if (brightness == 5) {
            return 125;
        }
        if (brightness == 6) {
            return 150;
        }
        if (brightness == 7) {
            return 175;
        }
        if (brightness == 8) {
            return IntervalRecExecutor.INTVL_REC_INITIALIZED;
        }
        if (brightness == 9) {
            return 225;
        }
        if (brightness == 10) {
            return 250;
        }
        if (brightness == 11) {
            return 275;
        }
        if (brightness == 12) {
            return 300;
        }
        if (brightness == 13) {
            return 325;
        }
        return IntervalRecExecutor.INTVL_REC_INITIALIZED;
    }

    private void changeBrightness(int brightness) {
        int int_real_mbrittness = brightness_to_mPatternBriteness_converter(brightness);
        if (this.mModeSelected != Mode.MODE1_SELECT_TARGET_FACES.code) {
            if (this.mModeSelected != Mode.MODE2_CL_ADJUST.code) {
                if (this.mModeSelected == Mode.MODE3_CL_ADJUST_CL_INVISIBLE.code || this.mModeSelected == Mode.MODE4_CHECK_WHOLE_IMAGE.code || this.mModeSelected == Mode.MODE5_CHECK_WHOLE_IMAGE_CL_INVISIBLE.code) {
                }
            } else {
                mPatternBriteness = int_real_mbrittness;
                reImaging_otherThread();
                lightArrow_hide_and_delay_shower(PortraitBeautyConstants.THREE_SECOND_MILLIS);
                BackUpUtil.getInstance().setPreference(PortraitBeautyBackUpKey.KEY_CATCHLIGHT_BRIGHTNESS, Integer.valueOf(mPatternBriteness));
            }
        }
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onChangeLeftPosition(int leftPosition) {
        moveSelection();
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onChangeRightPosition(int rightPosition) {
        moveSelection();
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onChangeCenter(String centerEvent) {
        if (centerEvent.equals(PortraitBeautyConstants.LEFT_ON_CENTER)) {
            if (this.mAngle == 6) {
                move_CL_position_oneUnit(PortraitBeautyConstants.DOWN_ON_CENTER);
                return;
            }
            if (this.mAngle == 3) {
                move_CL_position_oneUnit(PortraitBeautyConstants.RIGHT_ON_CENTER);
                return;
            } else if (this.mAngle == 8) {
                move_CL_position_oneUnit(PortraitBeautyConstants.UP_ON_CENTER);
                return;
            } else {
                move_CL_position_oneUnit(PortraitBeautyConstants.LEFT_ON_CENTER);
                return;
            }
        }
        if (centerEvent.equals(PortraitBeautyConstants.RIGHT_ON_CENTER)) {
            if (this.mAngle == 6) {
                move_CL_position_oneUnit(PortraitBeautyConstants.UP_ON_CENTER);
                return;
            }
            if (this.mAngle == 3) {
                move_CL_position_oneUnit(PortraitBeautyConstants.LEFT_ON_CENTER);
                return;
            } else if (this.mAngle == 8) {
                move_CL_position_oneUnit(PortraitBeautyConstants.DOWN_ON_CENTER);
                return;
            } else {
                move_CL_position_oneUnit(PortraitBeautyConstants.RIGHT_ON_CENTER);
                return;
            }
        }
        if (centerEvent.equals(PortraitBeautyConstants.UP_ON_CENTER)) {
            if (this.mAngle == 6) {
                move_CL_position_oneUnit(PortraitBeautyConstants.LEFT_ON_CENTER);
                return;
            }
            if (this.mAngle == 3) {
                move_CL_position_oneUnit(PortraitBeautyConstants.DOWN_ON_CENTER);
                return;
            } else if (this.mAngle == 8) {
                move_CL_position_oneUnit(PortraitBeautyConstants.RIGHT_ON_CENTER);
                return;
            } else {
                move_CL_position_oneUnit(PortraitBeautyConstants.UP_ON_CENTER);
                return;
            }
        }
        if (centerEvent.equals(PortraitBeautyConstants.DOWN_ON_CENTER)) {
            if (this.mAngle == 6) {
                move_CL_position_oneUnit(PortraitBeautyConstants.RIGHT_ON_CENTER);
                return;
            }
            if (this.mAngle == 3) {
                move_CL_position_oneUnit(PortraitBeautyConstants.UP_ON_CENTER);
                return;
            } else if (this.mAngle == 8) {
                move_CL_position_oneUnit(PortraitBeautyConstants.LEFT_ON_CENTER);
                return;
            } else {
                move_CL_position_oneUnit(PortraitBeautyConstants.DOWN_ON_CENTER);
                return;
            }
        }
        if (centerEvent.equals(PortraitBeautyConstants.FOCUS_ARROW_VISIBLE)) {
            mCatchlight_focus_arrow.setVisibility(0);
        } else if (centerEvent.equals(PortraitBeautyConstants.FOCUS_ARROW_INVISIBLE)) {
            mCatchlight_focus_arrow.setVisibility(4);
        }
    }

    private void applyPatternEffect(int pattern) {
        switch (pattern) {
            case 0:
                changePattern(0);
                return;
            case 1:
                changePattern(1);
                return;
            case 2:
                changePattern(2);
                return;
            case 3:
                changePattern(3);
                return;
            case 4:
                changePattern(4);
                return;
            case 5:
                changePattern(5);
                return;
            case 6:
                changePattern(6);
                return;
            default:
                return;
        }
    }

    private void moveSelection() {
        if (this.mModeSelected != Mode.MODE2_CL_ADJUST.code) {
            if (this.mModeSelected == Mode.MODE3_CL_ADJUST_CL_INVISIBLE.code || this.mModeSelected == Mode.MODE4_CHECK_WHOLE_IMAGE.code || this.mModeSelected == Mode.MODE5_CHECK_WHOLE_IMAGE_CL_INVISIBLE.code) {
            }
        } else {
            update_arrow_position();
            this.mCatchLightLayoutEyeFrame.invalidate();
        }
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onChangeSingleFace() {
        if (this.mModeSelected == Mode.MODE1_SELECT_TARGET_FACES.code) {
            reModeChangeView(Mode.MODE2_CL_ADJUST);
            this.mFooterGuide.setVisibility(0);
        }
    }

    private void resizePattern(int i_temp_size) {
        int unit;
        if (mIsLeftEyeSelected) {
            unit = this.mLeftEyeUnits[this.mFaceSelection];
        } else {
            unit = this.mRightEyeUnits[this.mFaceSelection];
        }
        if (unit == 3) {
            unit = 2;
        }
        if (this.mModeSelected != Mode.MODE2_CL_ADJUST.code) {
            if (this.mModeSelected == Mode.MODE3_CL_ADJUST_CL_INVISIBLE.code || this.mModeSelected == Mode.MODE4_CHECK_WHOLE_IMAGE.code || this.mModeSelected != Mode.MODE5_CHECK_WHOLE_IMAGE_CL_INVISIBLE.code) {
            }
            return;
        }
        if (PortraitBeautyConstants.Resize_Key_Up) {
            int[] iArr = this.mLeftEyeSizes;
            int i = this.mFaceSelection;
            iArr[i] = iArr[i] - (unit * 4);
            if (this.mLeftEyeSizes[this.mFaceSelection] < 16) {
                this.mLeftEyeSizes[this.mFaceSelection] = 16;
            }
            int[] iArr2 = this.mRightEyeSizes;
            int i2 = this.mFaceSelection;
            iArr2[i2] = iArr2[i2] - (unit * 4);
            if (this.mRightEyeSizes[this.mFaceSelection] < 16) {
                this.mRightEyeSizes[this.mFaceSelection] = 16;
            }
            Log.d(this.TAG, "==== unit\t= " + unit);
            reImaging_otherThread();
            update_arrow_position();
            lightArrow_hide_and_delay_shower(PortraitBeautyConstants.THREE_SECOND_MILLIS);
            return;
        }
        int[] iArr3 = this.mLeftEyeSizes;
        int i3 = this.mFaceSelection;
        iArr3[i3] = iArr3[i3] + (unit * 4);
        if (this.mLeftEyeSizes[this.mFaceSelection] > unit * 64) {
            this.mLeftEyeSizes[this.mFaceSelection] = unit * 64;
        }
        int[] iArr4 = this.mRightEyeSizes;
        int i4 = this.mFaceSelection;
        iArr4[i4] = iArr4[i4] + (unit * 4);
        if (this.mRightEyeSizes[this.mFaceSelection] > unit * 64) {
            this.mRightEyeSizes[this.mFaceSelection] = unit * 64;
        }
        Log.d(this.TAG, "==== unit\t= " + unit);
        reImaging_otherThread();
        update_arrow_position();
        lightArrow_hide_and_delay_shower(PortraitBeautyConstants.THREE_SECOND_MILLIS);
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onChangeFaceFocusFrame(String Focus) {
        if (this.mModeSelected == Mode.MODE1_SELECT_TARGET_FACES.code) {
            if (Focus.equals(PortraitBeautyConstants.RIGHT_ON_CENTER)) {
                reFaceChangeView(1);
            } else if (Focus.equals(PortraitBeautyConstants.LEFT_ON_CENTER)) {
                reFaceChangeView(-1);
            }
        }
    }

    private void changePattern(int patternID) {
        if (this.mModeSelected != Mode.MODE1_SELECT_TARGET_FACES.code) {
            if (this.mModeSelected != Mode.MODE2_CL_ADJUST.code) {
                if (this.mModeSelected == Mode.MODE3_CL_ADJUST_CL_INVISIBLE.code || this.mModeSelected == Mode.MODE4_CHECK_WHOLE_IMAGE.code || this.mModeSelected == Mode.MODE5_CHECK_WHOLE_IMAGE_CL_INVISIBLE.code) {
                }
            } else {
                this.mLightPatternSelection = patternID;
                changeLightPattern();
                reImaging_otherThread();
                update_arrow_position();
                lightArrow_hide_and_delay_shower(PortraitBeautyConstants.THREE_SECOND_MILLIS);
            }
        }
    }

    OptimizedImage setImageOrientation(OptimizedImage image, ContentsManager mgr) {
        boolean isSpinnedImage = mgr.getContentInfo(mgr.getContentsId()).getInt("Orientation") != 1;
        if (isSpinnedImage) {
            switch (mgr.getContentInfo(mgr.getContentsId()).getInt("Orientation")) {
                case 3:
                    Log.d("", "Rotate CASE = 180");
                    OptimizedImage rotatedImage = ImageEditor.rotateImage(image, RotateImageFilter.ROTATION_DEGREE.DEGREE_180);
                    return rotatedImage;
                case 4:
                case 5:
                case 7:
                default:
                    Log.d("", " Not Rotate   ");
                    return image;
                case 6:
                    Log.d("", "Rotate CASE = 90");
                    OptimizedImage rotatedImage2 = ImageEditor.rotateImage(image, RotateImageFilter.ROTATION_DEGREE.DEGREE_90);
                    return rotatedImage2;
                case 8:
                    Log.d("", "Rotate CASE = 270");
                    OptimizedImage rotatedImage3 = ImageEditor.rotateImage(image, RotateImageFilter.ROTATION_DEGREE.DEGREE_270);
                    return rotatedImage3;
            }
        }
        return image;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfAelKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onRingTurnedClockwise(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onRingTurnedCounterClockwise(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.portraitbeauty.common.EVFOffLayout, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        if (device == 1) {
            isEVFShown = true;
            comingFromEVF = 1;
        } else if (device == 2) {
            isEVFShown = false;
            comingFromEVF = 0;
            isHDMIShown = true;
        } else if (device == 0) {
            if (comingFromEVF == 1) {
                switchToEVFFromPanel = 1;
            }
            isEVFShown = false;
        } else {
            isEVFShown = false;
        }
        super.onLayoutModeChanged(device, displayMode);
    }

    private void initializeReImagingTask() {
        if (this.myHandler == null) {
            this.myHandler = new Handler();
        }
        if (this.reImagingTask == null) {
            this.reImagingTask = new ReImagingRunnable(this.myHandler);
            this.reImagingTask.setReImagingFlag(true);
        }
    }

    private void deInitializeReImagingTask() {
        if (this.reImagingTask != null) {
            this.reImagingTask.removeCallbacks();
            this.reImagingTask = null;
        }
    }

    /* loaded from: classes.dex */
    public class ReImagingRunnable {
        Handler handler;
        boolean isReImaging = false;
        Runnable r = new Runnable() { // from class: com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout.ReImagingRunnable.1
            @Override // java.lang.Runnable
            public void run() {
                CatchLightPlayBackLayout.this.reImaging(true);
            }
        };

        public ReImagingRunnable(Handler hnd) {
            this.handler = hnd;
        }

        public void setReImagingFlag(boolean reImaging) {
            this.isReImaging = reImaging;
        }

        public void execute(int time) {
            if (this.handler != null && this.isReImaging) {
                this.handler.postDelayed(this.r, 0L);
            }
        }

        public void removeCallbacks() {
            if (this.handler != null) {
                this.handler.removeCallbacks(this.r);
                this.r = null;
                this.handler = null;
            }
        }
    }

    @Override // com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightEffectChangeListener
    public void onDrawBlackEyeFrame() {
        this.mCatchLightLayoutEyeFrame.invalidate();
    }
}
