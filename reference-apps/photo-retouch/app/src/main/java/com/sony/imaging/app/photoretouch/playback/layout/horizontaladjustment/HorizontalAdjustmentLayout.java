package com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.EVFOffLayout;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class HorizontalAdjustmentLayout extends EVFOffLayout implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {
    private static final int GRID_LN_ORIGINAL_POSITION_X = 288;
    private static final int GRID_LN_ORIGINAL_POSITION_Y = 148;
    private float mGridHoriLnLeft;
    private float mGridHoriLnTop;
    private float mGridVerLnLeft;
    private float mGridverLnTop;
    private int mOptImgHeight;
    private int mOptImgWidth;
    private float mOptimizedImageViewBottom;
    private float mOptimizedImageViewHeight;
    private float mOptimizedImageViewLeft;
    private float mOptimizedImageViewRight;
    private float mOptimizedImageViewTop;
    private float mOptimizedImageViewWidth;
    private float mXCenterOfOptimizedImageView;
    private final String TAG = HorizontalAdjustmentLayout.class.getSimpleName();
    private PhotoRetouchSubMenuLayout mPhotoRetouchSubMenuLayout = null;
    private OptimizedImageView mOptimizedImageView = null;
    private OptimizedImage mOptimizedImage = null;
    private double mDegreeAngle = 0.0d;
    private SeekBar mSliderBar = null;
    private float mGridInitialX = 0.0f;
    private float mGridInitialY = 0.0f;
    private float mGridNewX = 0.0f;
    private float mGridNewY = 0.0f;
    private final int SLIDER_CENTER_VALUE = 70;
    private int mCurRotate = 70;
    private int mRotationImageSelected = -1;
    private boolean isRotationImageLongClicked = false;
    private ImageView mGridHoriLn = null;
    private ImageView mGridVertLn = null;
    private View mHGridLineContainer = null;
    private View mVGridLineContainer = null;
    private View mCurrentView = null;
    private GestureDetector mFlickDetection = null;
    private final int GRID_LN_SELECTED_NO = 0;
    private final int GRID_LN_SELECTED_HORIZONTAL = 1;
    private final int GRID_LN_SELECTED_VERTICAL = 2;
    private int mGridLineSelected = 0;
    GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment.HorizontalAdjustmentLayout.1
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent event) {
            HorizontalAdjustmentLayout.this.refreshCoords();
            HorizontalAdjustmentLayout.this.isRotationImageLongClicked = false;
            HorizontalAdjustmentLayout.this.mXCenterOfOptimizedImageView = HorizontalAdjustmentLayout.this.mOptimizedImageViewLeft + (HorizontalAdjustmentLayout.this.mOptimizedImageViewWidth / 2.0f);
            Log.d(HorizontalAdjustmentLayout.this.TAG, "===center x of optimized image view= " + HorizontalAdjustmentLayout.this.mXCenterOfOptimizedImageView);
            HorizontalAdjustmentLayout.this.isRotationImageLongClicked = false;
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d("YES", "====onDoubleTapEvent");
            HorizontalAdjustmentLayout.this.setNormalIconToRotationImage();
            return super.onDoubleTapEvent(e);
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
            HorizontalAdjustmentLayout.this.isRotationImageLongClicked = true;
            Log.d("YES", "====onLongPress");
            super.onLongPress(e);
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            HorizontalAdjustmentLayout.this.setNormalIconToRotationImage();
            return true;
        }
    };
    private boolean mShowGrid = false;
    OptimizedImage mEditImage = null;

    /* loaded from: classes.dex */
    private enum RoationImageSelect {
        LEFT_TOP(0),
        LEFT_BOTTOM(1),
        RIGHT_TOP(2),
        RIGHT_BOTTOM(3);

        private int code;

        RoationImageSelect(int c) {
            this.code = c;
        }
    }

    public int getGridLineSelected() {
        return this.mGridLineSelected;
    }

    public void setGridLineSelected(int gridLineSelected) {
        this.mGridLineSelected = gridLineSelected;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNormalIconToRotationImage() {
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.horizontal_adjustment);
        this.mPhotoRetouchSubMenuLayout = (PhotoRetouchSubMenuLayout) getLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT);
        this.mOptimizedImageView = this.mCurrentView.findViewById(R.id.optimized_image);
        this.mOptimizedImageView.setOnDisplayEventListener(new OptimizedImageView.onDisplayEventListener() { // from class: com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment.HorizontalAdjustmentLayout.2
            public void onDisplay(int errCd) {
                ImageEditor.runnableCount--;
                Log.i(HorizontalAdjustmentLayout.this.TAG, "onDisplay:runnableCount" + ImageEditor.runnableCount);
                ImageEditor.stableCount--;
                if (ImageEditor.stableCount > 0) {
                    return;
                }
                boolean unused = Updater.mStable = true;
            }
        });
        this.mHGridLineContainer = this.mCurrentView.findViewById(R.id.HGridLineArea);
        this.mVGridLineContainer = this.mCurrentView.findViewById(R.id.VGridLineArea);
        this.mHGridLineContainer.setVisibility(8);
        this.mVGridLineContainer.setVisibility(8);
        this.mSliderBar = (SeekBar) this.mCurrentView.findViewById(R.id.slider);
        ImageView backGround = (ImageView) this.mCurrentView.findViewById(R.id.horizontal_background);
        backGround.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        this.mGridHoriLn = (ImageView) this.mCurrentView.findViewById(R.id.gridline_horizontal);
        this.mGridHoriLn.setVisibility(8);
        this.mGridVertLn = (ImageView) this.mCurrentView.findViewById(R.id.gridline_vertical);
        this.mGridVertLn.setVisibility(8);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.mGridHoriLn.getLayoutParams();
        lp.topMargin = GRID_LN_ORIGINAL_POSITION_Y;
        this.mGridHoriLn.setLayoutParams(lp);
        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) this.mGridVertLn.getLayoutParams();
        lp2.leftMargin = GRID_LN_ORIGINAL_POSITION_X;
        this.mGridVertLn.setLayoutParams(lp2);
        this.mShowGrid = true;
        toggleGrid();
        Drawable cursor = getResources().getDrawable(R.drawable.p_16_dd_parts_pr_horizontaladjuctment_marker);
        this.mSliderBar.setThumbOffset(cursor.getIntrinsicWidth() / 2);
        this.mSliderBar.setOnSeekBarChangeListener(this);
        this.mFlickDetection = new GestureDetector(getActivity(), this.mGestureListener);
        this.mHGridLineContainer.setOnTouchListener(this);
        this.mVGridLineContainer.setOnTouchListener(this);
        this.mOptimizedImageView.setOnTouchListener(this);
        this.mSliderBar.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment.HorizontalAdjustmentLayout.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.mOptimizedImageView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment.HorizontalAdjustmentLayout.4
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "==== onResume:optImageCount = " + ImageEditor.optImageCount);
        setParameters();
        this.mSliderBar.setProgress(70);
        Updater.initialize(this.mOptimizedImageView, this.mOptimizedImage);
        setKeyBeepPattern(0);
    }

    private void setParameters() {
        this.mOptImgHeight = ImageEditor.getImage().getHeight();
        this.mOptImgWidth = ImageEditor.getImage().getWidth();
        Log.d(this.TAG, "====height= " + this.mOptImgHeight);
        Log.d(this.TAG, "====width= " + this.mOptImgWidth);
        ScaleImageFilter scaleImageFilter = new ScaleImageFilter();
        scaleImageFilter.setSource(ImageEditor.getImage(), false);
        Log.d(this.TAG, "==================Rotation Value=================" + ImageEditor.getOrientationInfo());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        boolean isExecuted = false;
        if (ImageEditor.getAspectRatio() == 0) {
            Log.d("Yes", "============== 3:2  ====");
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                Log.d("Yes", "============== 3:2  landscape====");
                layoutParams.leftMargin = 50;
                layoutParams.topMargin = 30;
                layoutParams.width = AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL;
                layoutParams.height = 300;
            } else {
                Log.d("Yes", "============== 3:2 potra====");
                layoutParams.leftMargin = 220;
                layoutParams.topMargin = 30;
                layoutParams.width = 200;
                layoutParams.height = 300;
            }
            this.mOptimizedImageView.setLayoutParams(layoutParams);
            scaleImageFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 428);
            isExecuted = scaleImageFilter.execute();
        } else if (1 == ImageEditor.getAspectRatio()) {
            Log.d("Yes", "============== 16:9  ====");
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                Log.d("Yes", "============== 16:9 landscape====");
                layoutParams.leftMargin = 53;
                layoutParams.topMargin = 30;
                layoutParams.width = AppRoot.USER_KEYCODE.AF_MF;
                layoutParams.height = 300;
            } else {
                Log.d("Yes", "============== 16:9 portrait====");
                layoutParams.leftMargin = 236;
                layoutParams.topMargin = 30;
                layoutParams.width = 168;
                layoutParams.height = 300;
            }
            this.mOptimizedImageView.setLayoutParams(layoutParams);
            scaleImageFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 360);
            isExecuted = scaleImageFilter.execute();
        } else if (2 == ImageEditor.getAspectRatio()) {
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                layoutParams.leftMargin = 120;
                layoutParams.topMargin = 30;
                layoutParams.width = 400;
                layoutParams.height = 300;
            } else {
                layoutParams.leftMargin = AppRoot.USER_KEYCODE.PLAYBACK;
                layoutParams.topMargin = 30;
                layoutParams.width = 225;
                layoutParams.height = 300;
            }
            this.mOptimizedImageView.setLayoutParams(layoutParams);
            scaleImageFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 480);
            isExecuted = scaleImageFilter.execute();
        } else if (3 == ImageEditor.getAspectRatio()) {
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                layoutParams.leftMargin = 170;
                layoutParams.topMargin = 30;
                layoutParams.width = 300;
                layoutParams.height = 300;
            } else {
                layoutParams.leftMargin = 170;
                layoutParams.topMargin = 30;
                layoutParams.width = 300;
                layoutParams.height = 300;
            }
            this.mOptimizedImageView.setLayoutParams(layoutParams);
            scaleImageFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, AppRoot.USER_KEYCODE.WATER_HOUSING);
            isExecuted = scaleImageFilter.execute();
        } else {
            Log.e(this.TAG, "===Image Aspect Ratio not supported");
        }
        Point p = new Point(layoutParams.width >> 1, layoutParams.height >> 1);
        this.mOptimizedImageView.setPivot(p);
        this.mOptimizedImageView.setLayoutParams(layoutParams);
        if (isExecuted) {
            Log.d(this.TAG, "=== Scale Image filter executed");
            OptimizedImage optImage = scaleImageFilter.getOutput();
            ImageEditor.optImageCount++;
            this.mOptimizedImage = ImageEditor.rotateImageWRTOrientation(optImage);
            if (this.mOptimizedImage == null) {
                this.mOptimizedImage = optImage;
            } else {
                ImageEditor.releaseOptImage(optImage);
            }
            if (this.mOptimizedImage != null) {
                ImageEditor.runnableCount = 1;
                this.mOptimizedImageView.setOptimizedImage(this.mOptimizedImage);
            } else {
                Log.d(this.TAG, "=== Scale Image filter executed...but optimzed output is null");
            }
        } else {
            Log.e(this.TAG, "===Scale Image filter not executed");
        }
        scaleImageFilter.clearSources();
        scaleImageFilter.release();
    }

    private void rotateImageWithSlider() {
        if (this.mCurRotate >= 70) {
            this.mDegreeAngle = ((-1.0d) * (this.mCurRotate - 70)) / 10.0d;
        } else {
            this.mDegreeAngle = (70 - this.mCurRotate) / 10.0d;
        }
        Log.d(this.TAG, "===degree= " + this.mDegreeAngle);
        Updater.update(this.mDegreeAngle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Updater {
        protected static double mDegreeAngle;
        protected static OptimizedImage mOriginalImage;
        protected static OptimizedImage mRotatedImage;
        protected static OptimizedImageView mView;
        private static String TAG = HorizontalAdjustmentLayout.class.getSimpleName();
        protected static Handler handler = new Handler();
        private static boolean mStable = false;
        private static boolean isFirst = true;
        protected static Runnable runnable = new Runnable() { // from class: com.sony.imaging.app.photoretouch.playback.layout.horizontaladjustment.HorizontalAdjustmentLayout.Updater.1
            @Override // java.lang.Runnable
            public void run() {
                ImageEditor.releaseOptImage(Updater.mRotatedImage);
                Updater.mRotatedImage = ImageEditor.rotateImage(Updater.mOriginalImage, Updater.mDegreeAngle);
                Updater.mView.setOptimizedImage(Updater.mRotatedImage);
                if (ImageEditor.runnableCount < 0) {
                    ImageEditor.runnableCount = 0;
                }
                ImageEditor.runnableCount++;
                Log.d(Updater.TAG, "run:runnableCount" + ImageEditor.runnableCount);
            }
        };

        Updater() {
        }

        public static void initialize(OptimizedImageView view, OptimizedImage img) {
            mOriginalImage = img;
            mView = view;
            mRotatedImage = null;
            mStable = false;
            isFirst = true;
            ImageEditor.stableCount = 1;
        }

        public static void terminate() {
            mView = null;
            mOriginalImage = null;
            ImageEditor.releaseOptImage(mRotatedImage);
            handler.removeCallbacks(runnable);
        }

        public static void releaseRotatedImage() {
            ImageEditor.releaseOptImage(mRotatedImage);
        }

        public static void update(double degreeAngle) {
            if (mStable && isFirst) {
                ImageEditor.runnableCount = 0;
                isFirst = false;
            }
            mDegreeAngle = degreeAngle;
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 100L);
        }

        public static boolean isStable() {
            return mStable;
        }
    }

    private void toggleGrid() {
        this.mShowGrid = !this.mShowGrid;
        if (this.mShowGrid) {
            this.mGridHoriLn.setVisibility(0);
            this.mGridVertLn.setVisibility(0);
            this.mHGridLineContainer.setVisibility(0);
            this.mVGridLineContainer.setVisibility(0);
            refreshCoords();
            return;
        }
        this.mGridHoriLn.setVisibility(8);
        this.mGridVertLn.setVisibility(8);
        this.mHGridLineContainer.setVisibility(8);
        this.mVGridLineContainer.setVisibility(8);
    }

    private void isCorner(float x, float y) {
        Log.d(this.TAG, "===corner X= " + x + "\t Y= " + y);
        if (this.mGridInitialX <= this.mGridHoriLnLeft + 50.0f && this.mGridInitialY >= this.mGridHoriLnTop - 50.0f && this.mGridInitialY <= this.mGridHoriLnTop + 50.0f) {
            Log.d(this.TAG, "===horizontal line corner");
            setGridLineSelected(1);
        } else if (this.mGridInitialX >= this.mGridVerLnLeft - 50.0f && this.mGridInitialX <= this.mGridVerLnLeft + 50.0f && this.mGridInitialY <= this.mGridverLnTop + 100.0f) {
            Log.d(this.TAG, "===vertical line corner");
            setGridLineSelected(2);
        } else {
            setGridLineSelected(0);
        }
    }

    private void moveHrLine(float currentX, float currentY) {
        Log.d(this.TAG, "===hor line coord...x= " + currentX + "\t y= " + currentY);
        float ySpace = currentY - this.mGridInitialY;
        this.mGridInitialY = currentY;
        this.mGridHoriLnTop += ySpace;
        int height = this.mGridHoriLn.getHeight() / 2;
        if (this.mGridHoriLnTop < this.mOptimizedImageViewTop - height) {
            this.mGridHoriLnTop = (this.mOptimizedImageViewTop - height) + 1.0f;
        }
        if (this.mGridHoriLnTop > this.mOptimizedImageViewBottom - height) {
            this.mGridHoriLnTop = (this.mOptimizedImageViewBottom - height) - 1.0f;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.mGridHoriLn.getLayoutParams();
        lp.topMargin = (int) this.mGridHoriLnTop;
        this.mGridHoriLn.setLayoutParams(lp);
        Log.d(this.TAG, "====move horizontal grid line");
    }

    private void moveVrLine(float currentX, float currentY) {
        Log.d(this.TAG, "===ver line coord...x= " + currentX + "\t y= " + currentY);
        float xSpace = currentX - this.mGridInitialX;
        this.mGridInitialX = currentX;
        this.mGridVerLnLeft += xSpace;
        int width = this.mGridVertLn.getWidth() / 2;
        if (this.mGridVerLnLeft < this.mOptimizedImageViewLeft - width) {
            this.mGridVerLnLeft = (this.mOptimizedImageViewLeft - width) + 1.0f;
        }
        if (this.mGridVerLnLeft > this.mOptimizedImageViewRight - width) {
            this.mGridVerLnLeft = (this.mOptimizedImageViewRight - width) - 1.0f;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.mGridVertLn.getLayoutParams();
        lp.leftMargin = (int) this.mGridVerLnLeft;
        this.mGridVertLn.setLayoutParams(lp);
        Log.d(this.TAG, "====move vertical grid line");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshCoords() {
        this.mGridHoriLnLeft = this.mGridHoriLn.getLeft();
        this.mGridHoriLnTop = this.mGridHoriLn.getTop();
        this.mGridVerLnLeft = this.mGridVertLn.getLeft();
        this.mGridverLnTop = this.mGridVertLn.getTop();
        Log.d("YES", "====grid Hrline mGridHoriLnLeft= " + this.mGridHoriLnLeft);
        Log.d("YES", "====grid Hrline mGridHoriLnTop= " + this.mGridHoriLnTop);
        Log.d("YES", "====grid Vrline mGridVerLnLeft= " + this.mGridVerLnLeft);
        Log.d("YES", "====grid Vrline mGridverLnTop= " + this.mGridverLnTop);
        this.mOptimizedImageViewHeight = this.mOptimizedImageView.getHeight();
        this.mOptimizedImageViewWidth = this.mOptimizedImageView.getWidth();
        this.mOptimizedImageViewLeft = this.mOptimizedImageView.getLeft();
        this.mOptimizedImageViewTop = this.mOptimizedImageView.getTop();
        this.mOptimizedImageViewRight = this.mOptimizedImageView.getRight();
        this.mOptimizedImageViewBottom = this.mOptimizedImageView.getBottom();
        Log.d("YES", "===optimized image view height= " + this.mOptimizedImageViewHeight);
        Log.d("YES", "===optimized image view width= " + this.mOptimizedImageViewWidth);
        Log.d("YES", "===optimized image view left = " + this.mOptimizedImageViewLeft);
        Log.d("YES", "===optimized image view top = " + this.mOptimizedImageViewTop);
        Log.d("YES", "===optimized image view right = " + this.mOptimizedImageViewRight);
        Log.d("YES", "===optimized image view bottom = " + this.mOptimizedImageViewBottom);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.optimized_image /* 2131361817 */:
                break;
            case R.id.HGridLineArea /* 2131361863 */:
            case R.id.VGridLineArea /* 2131361864 */:
                Log.d(this.TAG, "===touch...action= " + event.getActionMasked());
                switch (event.getAction()) {
                    case 0:
                        this.mGridInitialX = event.getX();
                        this.mGridInitialY = event.getY();
                        isCorner(this.mGridInitialX, this.mGridInitialY);
                        Log.d(this.TAG, "" + this.mGridInitialX + "  " + this.mGridInitialY);
                        break;
                    case 1:
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                        refreshCoords();
                        break;
                    case 2:
                        this.mGridNewX = event.getX();
                        this.mGridNewY = event.getY();
                        if (1 == getGridLineSelected()) {
                            moveHrLine(this.mGridNewX, this.mGridNewY);
                            Log.d(this.TAG, "====move horizontal grid line");
                            return true;
                        }
                        if (2 == getGridLineSelected()) {
                            moveVrLine(this.mGridNewX, this.mGridNewY);
                            Log.d(this.TAG, "====move vertical grid line");
                            return true;
                        }
                        break;
                }
            default:
                Log.d(this.TAG, "This touch event is not considered");
                return false;
        }
        return this.mFlickDetection.onTouchEvent(event);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar bar, int progress, boolean fromTouch) {
        if (Updater.isStable()) {
            if (70 == progress) {
                this.mCurRotate = 70;
            } else {
                this.mCurRotate = progress;
            }
            Log.d(this.TAG, "===onProgressChanged..progress= " + progress);
            rotateImageWithSlider();
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar arg0) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar arg0) {
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (isFinder() || !Updater.isStable()) {
            return -1;
        }
        Log.d(this.TAG, "onKeyDown....code= " + keyCode);
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                if (this.mCurRotate == 0) {
                    return -1;
                }
                this.mCurRotate--;
                this.mSliderBar.setProgress(this.mCurRotate);
                return 1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                if (this.mCurRotate == 140) {
                    return -1;
                }
                this.mCurRotate++;
                this.mSliderBar.setProgress(this.mCurRotate);
                return 1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                if (ImageEditor.runnableCount > 0) {
                    return -1;
                }
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (PhotoRetouchSubMenuLayout.isRepeat(event) || ImageEditor.runnableCount > 0) {
                    return -1;
                }
                Log.d(this.TAG, "==========BtnSave is clicked...mOptimizedImage= " + ImageEditor.getImage());
                Updater.releaseRotatedImage();
                editImage();
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                saveImage();
                return 1;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                toggleGrid();
                return 1;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (this.mCurRotate == 140) {
                    return -1;
                }
                if (this.mCurRotate > 130) {
                    this.mCurRotate = 140;
                    this.mSliderBar.setProgress(this.mCurRotate);
                    return 1;
                }
                this.mCurRotate += 10;
                this.mSliderBar.setProgress(this.mCurRotate);
                return 1;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (this.mCurRotate == 0) {
                    return -1;
                }
                if (this.mCurRotate < 10) {
                    this.mCurRotate = 0;
                    this.mSliderBar.setProgress(this.mCurRotate);
                    return 1;
                }
                this.mCurRotate -= 10;
                this.mSliderBar.setProgress(this.mCurRotate);
                return 1;
            default:
                return 0;
        }
    }

    private void editImage() {
        ImageEditor.releaseOptImage(this.mOptimizedImage);
        if (this.mDegreeAngle != 0.0d) {
            Log.d(this.TAG, "==== editImage:optImageCount = " + ImageEditor.optImageCount);
            this.mEditImage = ImageEditor.rotateImage(ImageEditor.getImage(), this.mDegreeAngle);
        }
    }

    private void saveImage() {
        if (this.mDegreeAngle != 0.0d) {
            HorizontalAdjustmentSavingTask task = new HorizontalAdjustmentSavingTask(this.mEditImage);
            ImageEditor.executeResultReflection(task, this.mEditImage);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                Log.d(this.TAG, "UP");
                return -1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Updater.terminate();
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        ImageEditor.releaseOptImage(this.mOptimizedImage);
        deinitializeAllValues();
        Log.d(this.TAG, "==== onDestroyView:optImageCount = " + ImageEditor.optImageCount);
        super.onDestroyView();
    }

    private void deinitializeAllValues() {
        this.mOptimizedImageView = null;
        this.mDegreeAngle = 0.0d;
        this.mSliderBar = null;
        this.mGridInitialX = 0.0f;
        this.mGridInitialY = 0.0f;
        this.mGridNewX = 0.0f;
        this.mGridNewY = 0.0f;
        this.mCurRotate = 70;
        this.mRotationImageSelected = -1;
        this.isRotationImageLongClicked = false;
        this.mGridHoriLn = null;
        this.mGridVertLn = null;
        this.mHGridLineContainer = null;
        this.mVGridLineContainer = null;
        this.mCurrentView = null;
        this.mFlickDetection = null;
        this.mGridLineSelected = 0;
    }
}
