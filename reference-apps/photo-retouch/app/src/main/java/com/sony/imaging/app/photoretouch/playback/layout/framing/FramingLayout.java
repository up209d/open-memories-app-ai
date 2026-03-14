package com.sony.imaging.app.photoretouch.playback.layout.framing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.CustomGallery;
import com.sony.imaging.app.photoretouch.common.EVFOffLayout;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.common.widget.EachButton;
import com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout;
import com.sony.imaging.app.photoretouch.playback.control.YuvToRgbConversion;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.scalar.graphics.ImageAnalyzer;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.CropImageFilter;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.meta.FaceDetailScalarA;
import com.sony.scalar.sysutil.ScalarProperties;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class FramingLayout extends EVFOffLayout implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final int FACE_FRAME_SIZE = 80;
    public static final int MAX_NUMBER_OF_DETECTED_FACES = 8;
    public static final int MAX_NUMBER_OF_FACES = 5;
    private static final float PANEL_SCALING;
    private ImageAdapter mAdapter;
    private ImageAnalyzer.AnalyzedFace[] mAnalyzedFaces;
    private int mAspectRatio;
    private ImageButton[] mBtnFaces;
    private CustomGallery mCustomGallery;
    private int mFaceNum;
    private ImageAnalyzer.AnalyzedFace[] mFaces;
    private View mLastView;
    private RotateImageFilter.ROTATION_DEGREE[] mRotateDegreeTable;
    private RelativeLayout.LayoutParams[] paramsfaceButtons;
    private final String TAG = FramingLayout.class.getSimpleName();
    private View mCurrentView = null;
    private int[] mOddImageMargin = {88, 184, 280, 376, 472};
    private int[] mEvenImageMargin = {124, 220, 316, 412};
    private int mLeftMarginOffset = 96;
    private int mTopValue = 0;
    private int galleryPosition = 0;
    private OptimizedImage mOptImageScaled = null;
    private LinkedList<Rect> faceRectList = null;
    private LinkedList<Bitmap> mFaceBitmapList = null;
    private LinkedList<Bitmap> mGalleryBitmapList = null;
    private LinkedList<Object> mGalIndexList = null;
    LinkedList<Integer> mBitmapIndexList = null;
    YuvToRgbConversion yuvToRgb = null;
    private Bitmap mOrigBitmap = null;
    PhotoRetouchSubMenuLayout mPhotoRetouchSubMenuLayout = null;
    private FrameInfo mFrameInfo = null;
    private CropImageFilter cropFilter = null;
    private ScaleImageFilter scaleFilter = null;
    private RotateImageFilter rotateFilter = null;
    int[] mfaceResId = {R.id.Btn_face1, R.id.Btn_face2, R.id.Btn_face3, R.id.Btn_face4, R.id.Btn_face5};
    int panel = 0;
    OptimizedImage mEditImage = null;

    /* loaded from: classes.dex */
    public enum Orientation {
        LANDSCAPE,
        PORTRAIT
    }

    public native int joinKozu(int[] iArr, int[] iArr2);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ButtonHandler implements EachButton.Callback {
        ButtonHandler() {
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onDown(EachButton view) {
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onUp(EachButton view) {
            if (FramingLayout.this.mFaceNum != 0) {
                switch (view.getId()) {
                    case R.id.Btn_face1 /* 2131361853 */:
                    case R.id.Btn_face2 /* 2131361854 */:
                    case R.id.Btn_face3 /* 2131361855 */:
                    case R.id.Btn_face4 /* 2131361856 */:
                    case R.id.Btn_face5 /* 2131361857 */:
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                        FramingLayout.this.setButtonImage(view.getId());
                        return;
                    default:
                        return;
                }
            }
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onLongPress(EachButton view) {
        }
    }

    static {
        if (169 == ScalarProperties.getInt("device.panel.aspect")) {
            PANEL_SCALING = 0.75f;
        } else {
            PANEL_SCALING = 1.0f;
        }
        System.loadLibrary("faceKozu_Jni");
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.e("", "BUNDLE DATA = " + this.data);
        if (this.mCurrentView == null) {
            this.mCurrentView = inflater.inflate(R.layout.framing, (ViewGroup) null);
        }
        this.mPhotoRetouchSubMenuLayout = (PhotoRetouchSubMenuLayout) getLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT);
        ImageView backGround = (ImageView) this.mCurrentView.findViewById(R.id.framing_background);
        backGround.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        if (169 == ScalarProperties.getInt("device.panel.aspect")) {
            this.panel = 169;
        } else {
            this.panel = 43;
        }
        Log.d("YES", "=========================================onCreate of Framing");
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "==== onResume:optImageCount = " + ImageEditor.optImageCount);
        reInitialize();
        if (this.mCustomGallery == null) {
            this.mCustomGallery = (CustomGallery) this.mCurrentView.findViewById(R.id.gallery);
        }
        if (getLayout(Constant.ID_MANUALFRAMING).getView() != null) {
            getLayout(Constant.ID_MANUALFRAMING).closeLayout();
        }
        setKeyBeepPattern(0);
        makeRotationTable();
        Handler mHandler = new Handler();
        Runnable mRun = new Runnable() { // from class: com.sony.imaging.app.photoretouch.playback.layout.framing.FramingLayout.1
            @Override // java.lang.Runnable
            public void run() {
                FramingLayout.this.createImageFilter();
                FramingLayout.this.createFaceLayout();
                FramingLayout.this.createKozuLayout();
                FramingLayout.this.releaseImageFilter();
            }
        };
        mHandler.post(mRun);
        this.mCustomGallery.setOnItemSelectedListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createImageFilter() {
        this.scaleFilter = new ScaleImageFilter();
        this.cropFilter = new CropImageFilter();
        this.rotateFilter = new RotateImageFilter();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseImageFilter() {
        if (this.rotateFilter != null) {
            this.rotateFilter.clearSources();
            this.rotateFilter.release();
        }
        if (this.cropFilter != null) {
            this.cropFilter.clearSources();
            this.cropFilter.release();
        }
        if (this.scaleFilter != null) {
            this.scaleFilter.clearSources();
            this.scaleFilter.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createFaceLayout() {
        this.scaleFilter.setSource(ImageEditor.getImage(), false);
        boolean isExecuted = false;
        if (ImageEditor.getAspectRatio() == 0) {
            this.mAspectRatio = 1;
            this.scaleFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 428);
            isExecuted = this.scaleFilter.execute();
        } else if (1 == ImageEditor.getAspectRatio()) {
            this.mAspectRatio = 2;
            this.scaleFilter.setDestSize(AppRoot.USER_KEYCODE.MODE_DIAL_MOVIE, 308);
            isExecuted = this.scaleFilter.execute();
        } else if (2 == ImageEditor.getAspectRatio()) {
            this.mAspectRatio = 3;
            this.scaleFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 480);
            isExecuted = this.scaleFilter.execute();
        } else if (3 == ImageEditor.getAspectRatio()) {
            this.mAspectRatio = 4;
            this.scaleFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, AppRoot.USER_KEYCODE.WATER_HOUSING);
            isExecuted = this.scaleFilter.execute();
        } else {
            this.mAspectRatio = -1;
            Log.d(this.TAG, "===Aspect ratio is not 3:2 or 16:9");
        }
        if (isExecuted) {
            this.mOptImageScaled = this.scaleFilter.getOutput();
            ImageEditor.optImageCount++;
            createOriginalImageBitmap();
            ImageAnalyzer mImgAnalyzer = new ImageAnalyzer();
            this.mAnalyzedFaces = new ImageAnalyzer.AnalyzedFace[8];
            this.mFaces = new ImageAnalyzer.AnalyzedFace[5];
            this.mFaceNum = mImgAnalyzer.findFaces(ImageEditor.getImage(), this.mAnalyzedFaces);
            if (this.mFaceNum > 0) {
                setFaceRectList();
                callKozu(this.mOptImageScaled.getWidth(), this.mOptImageScaled.getHeight());
            }
            if (this.mFaceNum <= 0) {
                addOrigRect();
            }
            mImgAnalyzer.release();
            return;
        }
        Log.d(this.TAG, "Scale filter not executed");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createKozuLayout() {
        if (this.mFaceNum > 0) {
            createGalleryBitmapList();
            this.galleryPosition = (1073741823 - (1073741823 % this.mGalleryBitmapList.size())) - 1;
            Log.e("galleryPosition", "" + this.galleryPosition);
        } else {
            this.mGalleryBitmapList.add(this.mOrigBitmap);
            this.galleryPosition = 0;
            Log.e("galleryPosition", "" + this.galleryPosition);
        }
        this.mAdapter = new ImageAdapter(getActivity().getApplicationContext(), this.mGalleryBitmapList);
        this.mAdapter.setContentInfo(ImageEditor.getAspectRatio(), 1, this.panel);
        this.mCustomGallery.setAdapter((SpinnerAdapter) this.mAdapter);
        this.mCustomGallery.setSpacing(-100);
        this.mCustomGallery.setSelection(this.galleryPosition);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (isFinder()) {
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
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (this.mFaceNum <= 0) {
                    return -1;
                }
                setGallerySelection(false);
                return 1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (this.mFaceNum <= 0) {
                    return -1;
                }
                setGallerySelection(true);
                return 1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (PhotoRetouchSubMenuLayout.isRepeat(event)) {
                    return -1;
                }
                FrameInfo frameInfo = getFrameInfoBundle(this.galleryPosition);
                editImage(frameInfo);
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                saveImage(frameInfo);
                return 1;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                GalObjectList info = (GalObjectList) this.mGalIndexList.get(this.galleryPosition % this.mGalleryBitmapList.size());
                Rect rect = info.r;
                if (info.orgPos == -1) {
                    this.mPhotoRetouchSubMenuLayout.mRect = null;
                } else {
                    this.mPhotoRetouchSubMenuLayout.mRect = rect;
                }
                releaseBitmaps();
                openLayout(Constant.ID_MANUALFRAMING, this.data);
                return 1;
            default:
                return 0;
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

    private void setGallerySelection(boolean nextPos) {
        Log.e("galleryPosition", "" + this.galleryPosition);
        if (nextPos) {
            this.galleryPosition++;
        } else {
            this.galleryPosition--;
        }
        this.mCustomGallery.setSelection(this.galleryPosition);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setButtonImage(int id) {
        int len = this.mfaceResId.length;
        for (int i = 0; i < len; i++) {
            if (id == this.mfaceResId[i]) {
                int position = getSelectedIndex(i);
                this.mCustomGallery.setSelection(position + (1073741823 - (1073741823 % this.mGalleryBitmapList.size())));
                handleLevelBtnImage(i);
            }
        }
    }

    /* loaded from: classes.dex */
    public class FrameInfo {
        protected Rect cropCoordinates;
        protected int orientation;
        protected RotateImageFilter.ROTATION_DEGREE rotateDegree;

        public FrameInfo(RotateImageFilter.ROTATION_DEGREE degree, int orientation, Rect rect) {
            this.rotateDegree = degree;
            this.orientation = orientation;
            this.cropCoordinates = rect;
        }
    }

    private FrameInfo getFrameInfoBundle(int imgIndex) {
        GalObjectList info = (GalObjectList) this.mGalIndexList.get(this.galleryPosition % this.mGalleryBitmapList.size());
        if (info.orgPos == -1) {
            return null;
        }
        int faceIndex = info.orgPos;
        int faceAngle = this.mFaces[faceIndex].face.faceAngle.angle;
        Rect rect = info.r;
        float scaleW = ImageEditor.getImage().getWidth() / this.mOptImageScaled.getWidth();
        float scaleH = ImageEditor.getImage().getHeight() / this.mOptImageScaled.getHeight();
        rect.left = (int) (rect.left * scaleW);
        rect.top = (int) (rect.top * scaleH);
        rect.right = (int) (rect.right * scaleW);
        rect.bottom = (int) (rect.bottom * scaleH);
        ImageEditor.releaseOptImage(this.mOptImageScaled);
        RotateImageFilter.ROTATION_DEGREE rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        int newOrientation = 1;
        if (rect.width() > rect.height()) {
            switch (faceAngle) {
                case 0:
                    newOrientation = 1;
                    break;
                case 1:
                    newOrientation = 6;
                    break;
                case 2:
                    newOrientation = 8;
                    break;
            }
            rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        } else if (rect.height() > rect.width()) {
            switch (faceAngle) {
                case 0:
                    rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_270;
                    newOrientation = 6;
                    break;
                case 1:
                    rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_90;
                    newOrientation = 1;
                    break;
                case 2:
                    rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_270;
                    newOrientation = 1;
                    break;
            }
        }
        return new FrameInfo(rotateDegree, newOrientation, rect);
    }

    private void editImage(FrameInfo frameInfo) {
        if (frameInfo != null) {
            Log.d(this.TAG, "==== editImage:optImageCount = " + ImageEditor.optImageCount);
            this.mEditImage = ImageEditor.getEditFrame(ImageEditor.getImage(), frameInfo.cropCoordinates, frameInfo.rotateDegree);
        }
    }

    private void saveImage(FrameInfo frameInfo) {
        if (frameInfo != null) {
            FramingSavingTask task = new FramingSavingTask(frameInfo, this.mEditImage);
            ImageEditor.executeResultReflection(task, this.mEditImage);
            Log.d(this.TAG, "===execute saving task");
        }
    }

    private int getSelectedIndex(int faceNum) {
        int size = this.mGalIndexList.size();
        for (int i = 0; i < size; i++) {
            GalObjectList g = (GalObjectList) this.mGalIndexList.get(i);
            if (g.orgPos == faceNum && g.orientation == 2) {
                return i;
            }
        }
        return -1;
    }

    private void showFindFace(int faceNumber) {
        this.mTopValue = 322;
        new ButtonHandler();
        if (this.mFaceNum > 0) {
            int leftMargin = 0;
            this.mBtnFaces = new ImageButton[this.mFaceNum];
            this.paramsfaceButtons = new RelativeLayout.LayoutParams[this.mFaceNum];
            switch (this.mFaceNum) {
                case 1:
                    leftMargin = this.mOddImageMargin[2];
                    break;
                case 2:
                    leftMargin = this.mEvenImageMargin[1];
                    break;
                case 3:
                    leftMargin = this.mOddImageMargin[1];
                    break;
                case 4:
                    leftMargin = this.mEvenImageMargin[0];
                    break;
                case 5:
                    leftMargin = this.mOddImageMargin[0];
                    break;
                default:
                    Log.d("TAG", "showFindFace  error");
                    break;
            }
            for (int i = 0; i < this.mFaceNum; i++) {
                this.mBtnFaces[i] = (ImageButton) this.mCurrentView.findViewById(this.mfaceResId[i]);
                this.paramsfaceButtons[i] = (RelativeLayout.LayoutParams) this.mBtnFaces[i].getLayoutParams();
                int aspect = DisplayModeObserver.getInstance().getOsdAspect(0);
                if (2 == aspect) {
                    Log.e("", "showFindFace 16:9");
                    this.paramsfaceButtons[i].width = 60;
                    this.paramsfaceButtons[i].height = 80;
                } else {
                    Log.e("", "showFindFace 4:3");
                    this.paramsfaceButtons[i].width = 80;
                    this.paramsfaceButtons[i].height = 80;
                }
                this.paramsfaceButtons[i].setMargins(leftMargin, this.mTopValue, 0, 0);
                this.mBtnFaces[i].setLayoutParams(this.paramsfaceButtons[i]);
                this.mBtnFaces[i].setPadding(6, 8, 6, 8);
                this.mBtnFaces[i].setVisibility(0);
                this.mBtnFaces[i].setImageDrawable(new BitmapDrawable(this.mFaceBitmapList.get(i)));
                leftMargin += this.mLeftMarginOffset;
            }
        }
    }

    /* loaded from: classes.dex */
    public class ImageAdapter extends com.sony.imaging.app.photoretouch.common.ImageAdapter {
        int size;

        public ImageAdapter(Context c, LinkedList<? extends Object> galObjectList) {
            super(c, galObjectList);
            this.size = 0;
            this.size = galObjectList.size();
        }

        @Override // com.sony.imaging.app.photoretouch.common.ImageAdapter, android.widget.Adapter
        public int getCount() {
            return FramingLayout.this.mFaceNum == 0 ? 1 : Integer.MAX_VALUE;
        }

        @Override // com.sony.imaging.app.photoretouch.common.ImageAdapter, android.widget.Adapter
        public Object getItem(int position) {
            if (position >= this.size && this.size > 0) {
                position %= this.size;
            }
            return Integer.valueOf(position);
        }

        @Override // com.sony.imaging.app.photoretouch.common.ImageAdapter, android.widget.Adapter
        public long getItemId(int position) {
            if (position >= this.size && this.size > 0) {
                position %= this.size;
            }
            return position;
        }

        @Override // com.sony.imaging.app.photoretouch.common.ImageAdapter
        public Bitmap getBitmap(int position) {
            Bitmap bmp = (Bitmap) this.mGalObjectList.get(position);
            return bmp;
        }

        @Override // com.sony.imaging.app.photoretouch.common.ImageAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= this.size && this.size > 0) {
                position %= this.size;
            }
            return super.getView(position, convertView, parent);
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (this.mFaceNum > 0) {
            this.galleryPosition = position;
            Log.e("galleryPosition-- on item selected", "" + this.galleryPosition);
            if (position >= this.mGalleryBitmapList.size()) {
                position %= this.mGalleryBitmapList.size();
            }
            if (position % 2 == 1 || (position == this.mGalleryBitmapList.size() - 1 && (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()))) {
                this.mAdapter.setContentInfo(ImageEditor.getAspectRatio(), 1, this.panel);
                this.mCustomGallery.setSpacing(-100);
            } else {
                this.mAdapter.setContentInfo(ImageEditor.getAspectRatio(), 6, this.panel);
                this.mCustomGallery.setSpacing(50);
            }
            int i = getFaceMovePos(position);
            if (this.mLastView != null) {
                remove(this.mLastView);
                this.mLastView.setBackgroundResource(R.drawable.p_16_dd_parts_pr_shadow);
            }
            this.mLastView = view;
            remove(view);
            if (position < this.mGalleryBitmapList.size() - 1) {
                view.setBackgroundResource(R.drawable.p_16_dd_parts_pr_croppingframe_divi9);
            }
            Log.d("TAG", "###" + i);
            handleLevelBtnImage(i);
        }
    }

    private int getFaceMovePos(int position) {
        GalObjectList g = (GalObjectList) this.mGalIndexList.get(position);
        return g.orgPos;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private void callKozu(int width, int height) {
        int orientation0;
        int orientation1;
        int priorityFaceIsYawValid;
        int[][] outputArray = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, this.mFaceNum * 2, 5);
        int[] inputArray = new int[25];
        float scaleW = width / 2000.0f;
        float scaleH = height / 2000.0f;
        this.mFaceNum = this.faceRectList.size();
        inputArray[0] = width;
        inputArray[1] = height;
        inputArray[2] = this.mAspectRatio;
        inputArray[3] = 0;
        inputArray[4] = 0;
        inputArray[5] = 1;
        inputArray[6] = 0;
        inputArray[10] = 0;
        inputArray[16] = 0;
        inputArray[17] = 0;
        inputArray[18] = 0;
        inputArray[19] = 0;
        inputArray[20] = 0;
        inputArray[24] = 1;
        if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
            orientation0 = 2;
            orientation1 = 1;
        } else {
            orientation0 = 1;
            orientation1 = 2;
        }
        for (int i = 0; i < this.mFaceNum; i++) {
            FaceDetailScalarA detailFace = (FaceDetailScalarA) this.mFaces[i].detail;
            int priorityFaceRight = (int) ((this.mFaces[i].face.rect.right + Constant.TRANSITION_INDEXPB) * scaleW);
            int priorityFaceLeft = (int) ((this.mFaces[i].face.rect.left + Constant.TRANSITION_INDEXPB) * scaleW);
            int priorityFacePosY = (int) ((this.mFaces[i].face.rect.top + Constant.TRANSITION_INDEXPB) * scaleH);
            int priorityFaceSize = Math.abs(priorityFaceLeft - priorityFaceRight);
            int priorityFaceAngle = this.mFaces[i].face.faceAngle.angle;
            int priorityFaceNode = detailFace.node;
            boolean is_yaw_valid = detailFace.attributeValid;
            if (is_yaw_valid) {
                priorityFaceIsYawValid = 1;
            } else {
                priorityFaceIsYawValid = 0;
            }
            int priorityFaceYawScore = detailFace.rawFaceScore;
            inputArray[7] = priorityFaceLeft;
            inputArray[8] = priorityFacePosY;
            inputArray[9] = priorityFaceSize;
            inputArray[11] = priorityFaceLeft;
            inputArray[12] = priorityFacePosY;
            inputArray[13] = priorityFaceSize;
            inputArray[14] = priorityFaceAngle;
            inputArray[15] = priorityFaceNode;
            inputArray[21] = priorityFaceIsYawValid;
            inputArray[22] = priorityFaceYawScore;
            inputArray[23] = 2;
            joinKozu(inputArray, outputArray[i * 2]);
            this.mGalIndexList.add(i * 2, getGalIndexObj(outputArray, i * 2, i, orientation0));
            inputArray[23] = 1;
            joinKozu(inputArray, outputArray[(i * 2) + 1]);
            this.mGalIndexList.add((i * 2) + 1, getGalIndexObj(outputArray, (i * 2) + 1, i, orientation1));
        }
        addOrigRect();
    }

    private void setFaceRectList() {
        int scaledImgWidth = this.mOptImageScaled.getWidth();
        int scaledImgHeight = this.mOptImageScaled.getHeight();
        float scaleX = scaledImgWidth / 2000.0f;
        float scaleY = scaledImgHeight / 2000.0f;
        int j = 0;
        int faceNum = this.mFaceNum;
        int mFaceFrameSize = getFaceFrameSize();
        for (int i = 0; i < faceNum; i++) {
            float left = (this.mAnalyzedFaces[i].face.rect.left + Constant.TRANSITION_INDEXPB) * scaleX;
            float top = (this.mAnalyzedFaces[i].face.rect.top + Constant.TRANSITION_INDEXPB) * scaleY;
            float right = (this.mAnalyzedFaces[i].face.rect.right + Constant.TRANSITION_INDEXPB) * scaleX;
            int width = ((int) (right - left)) & (-4);
            Rect face = new Rect();
            face.left = (int) left;
            face.top = (int) top;
            face.right = ((int) left) + width;
            face.bottom = ((int) top) + width;
            if (width > 20) {
                if (width < mFaceFrameSize) {
                    int faceCenterX = (face.left + face.right) / 2;
                    int faceCenterY = (face.top + face.bottom) / 2;
                    face.left = faceCenterX - (mFaceFrameSize / 2);
                    face.top = faceCenterY - (mFaceFrameSize / 2);
                    face.right = (mFaceFrameSize / 2) + faceCenterX;
                    face.bottom = (mFaceFrameSize / 2) + faceCenterY;
                    if (face.left < 0) {
                        face.left = 0;
                        face.right = face.left + mFaceFrameSize;
                    }
                    if (face.top < 0) {
                        face.top = 0;
                        face.bottom = face.top + mFaceFrameSize;
                    }
                    if (face.right > scaledImgWidth) {
                        face.right = scaledImgWidth;
                        face.left = scaledImgWidth - mFaceFrameSize;
                    }
                    if (face.bottom > scaledImgHeight) {
                        face.bottom = scaledImgHeight;
                        face.top = scaledImgHeight - mFaceFrameSize;
                    }
                }
                this.faceRectList.add(face);
                this.mFaceNum = this.faceRectList.size();
                this.mFaces[j] = this.mAnalyzedFaces[i];
                j++;
                if (this.mFaceNum == 5) {
                    break;
                }
            }
        }
        this.mFaceNum = this.faceRectList.size();
        createFaceImages();
    }

    private int getFaceFrameSize() {
        float faceRatio;
        if (ImageEditor.getAspectRatio() == 0) {
            faceRatio = this.mOptImageScaled.getHeight() / 360.0f;
            if (6 == ImageEditor.getOrientationInfo() || 8 == ImageEditor.getOrientationInfo()) {
                faceRatio *= 1.5f;
            }
        } else {
            faceRatio = this.mOptImageScaled.getHeight() / 304.0f;
            if (6 == ImageEditor.getOrientationInfo() || 8 == ImageEditor.getOrientationInfo()) {
                faceRatio *= 1.7777778f;
            }
        }
        int frameSize = (int) (80.0f * faceRatio);
        return (frameSize + 16) & (-16);
    }

    private void createFaceImages() {
        OptimizedImage optImageRotate;
        int aspect = DisplayModeObserver.getInstance().getOsdAspect(0);
        if (2 == aspect) {
            Log.e("", "createFaceImages 16:9");
            this.scaleFilter.setDestSize(60, 80);
        } else {
            Log.e("", "createFaceImages 4:3");
            this.scaleFilter.setDestSize(80, 80);
        }
        for (int i = 0; i < this.mFaceNum; i++) {
            RotateImageFilter.ROTATION_DEGREE rotateDegree = this.mRotateDegreeTable[this.mFaces[i].face.faceAngle.angle];
            OptimizedImage optImageCrop = getCropImage(this.faceRectList.get(i));
            if (RotateImageFilter.ROTATION_DEGREE.DEGREE_0 != rotateDegree) {
                optImageRotate = getRotateImage(optImageCrop, rotateDegree);
                ImageEditor.releaseOptImage(optImageCrop);
            } else {
                optImageRotate = optImageCrop;
            }
            this.scaleFilter.setSource(optImageRotate, false);
            OptimizedImage optImage = null;
            boolean isExecuted = this.scaleFilter.execute();
            ImageEditor.releaseOptImage(optImageRotate);
            if (isExecuted) {
                optImage = this.scaleFilter.getOutput();
                ImageEditor.optImageCount++;
                Bitmap bmp = this.yuvToRgb.yuv2rgb_main(optImage);
                this.mFaceBitmapList.add(bmp);
            } else {
                Log.e("Framing Layout", "===scale image filter is not executed");
            }
            ImageEditor.releaseOptImage(optImage);
        }
        if (this.mFaceNum > 0) {
            showFindFace(this.mFaceNum);
        }
    }

    private void makeRotationTable() {
        this.mRotateDegreeTable = new RotateImageFilter.ROTATION_DEGREE[3];
        this.mRotateDegreeTable[0] = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        this.mRotateDegreeTable[1] = RotateImageFilter.ROTATION_DEGREE.DEGREE_90;
        this.mRotateDegreeTable[2] = RotateImageFilter.ROTATION_DEGREE.DEGREE_270;
    }

    private OptimizedImage getCropImage(Rect rect) {
        this.cropFilter.setSrcRect(rect);
        this.cropFilter.setSource(this.mOptImageScaled, false);
        boolean isExecuted = this.cropFilter.execute();
        if (!isExecuted) {
            return null;
        }
        OptimizedImage cropImg = this.cropFilter.getOutput();
        ImageEditor.optImageCount++;
        return cropImg;
    }

    private OptimizedImage getScaleImage(OptimizedImage optimizedImage, int width, int height) {
        this.scaleFilter.setSource(optimizedImage, true);
        this.scaleFilter.setDestSize(width, height);
        boolean isExecuted = this.scaleFilter.execute();
        if (isExecuted) {
            OptimizedImage scaleImg = this.scaleFilter.getOutput();
            return scaleImg;
        }
        Log.e("Framing Layout", "===scale image filter is not executed");
        return null;
    }

    private OptimizedImage getRotateImage(OptimizedImage optimizedImage, RotateImageFilter.ROTATION_DEGREE degree) {
        this.rotateFilter.setTrimMode(3);
        this.rotateFilter.setRotation(degree);
        this.rotateFilter.setSource(optimizedImage, true);
        boolean isRotateExecuted = this.rotateFilter.execute();
        if (isRotateExecuted) {
            OptimizedImage optImage = this.rotateFilter.getOutput();
            return optImage;
        }
        Log.e("Framing Layout", "===rotate image filter is not executed");
        return null;
    }

    public void releaseAllBitmap(LinkedList<Bitmap> bitmapList) {
        int size = bitmapList.size();
        for (int i = 0; i < size; i++) {
            Bitmap bmp = bitmapList.get(i);
            Log.e("", "releaseAllBitmap 1 " + bmp);
            bmp.recycle();
            Log.e("", "releaseAllBitmap 2 " + ((Object) null));
        }
        bitmapList.clear();
        System.gc();
    }

    private void addGalleryBitmapList(int pos) {
        int width;
        int height;
        OptimizedImage optImage;
        if (pos % 2 == 1) {
            if (ImageEditor.getAspectRatio() == 0) {
                width = AppRoot.USER_KEYCODE.MODE_DIAL_HQAUTO;
                height = 350;
            } else if (ImageEditor.getAspectRatio() == 1) {
                width = AppRoot.USER_KEYCODE.MODE_DIAL_HQAUTO;
                height = 290;
            } else if (ImageEditor.getAspectRatio() == 2) {
                width = AppRoot.USER_KEYCODE.MODE_DIAL_HQAUTO;
                height = 402;
            } else {
                width = 300;
                height = 300;
            }
        } else if (ImageEditor.getAspectRatio() == 0) {
            width = AppRoot.USER_KEYCODE.CENTER;
            height = 358;
        } else if (ImageEditor.getAspectRatio() == 1) {
            width = 196;
            height = 360;
        } else if (ImageEditor.getAspectRatio() == 2) {
            width = 270;
            height = 360;
        } else {
            width = 300;
            height = 300;
        }
        RotateImageFilter.ROTATION_DEGREE rotation_degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        OptimizedImage optImageCrop = getCropImage(((GalObjectList) this.mGalIndexList.get(pos)).r);
        RotateImageFilter.ROTATION_DEGREE rotateDegree = this.mRotateDegreeTable[this.mFaces[pos / 2].face.faceAngle.angle];
        if (RotateImageFilter.ROTATION_DEGREE.DEGREE_0 != rotateDegree) {
            OptimizedImage optImageRotate = getRotateImage(optImageCrop, rotateDegree);
            ImageEditor.releaseOptImage(optImageCrop);
            optImage = getScaleImage(optImageRotate, width, height);
        } else {
            optImage = getScaleImage(optImageCrop, width, height);
        }
        Bitmap bmp = getBitmap(optImage);
        ImageEditor.releaseOptImage(optImage);
        this.mGalleryBitmapList.add(bmp);
    }

    public void createGalleryBitmapList() {
        for (int i = 0; i < this.mFaceNum * 2; i++) {
            addGalleryBitmapList(i);
        }
        this.mGalleryBitmapList.add(this.mOrigBitmap);
    }

    private GalObjectList getGalIndexObj(int[][] outputArray, int rectPos, int pos, int orientation) {
        GalObjectList gal = new GalObjectList();
        gal.r = getRect(outputArray, rectPos);
        gal.orgPos = (short) pos;
        gal.orientation = (byte) orientation;
        return gal;
    }

    private Rect getRect(int[][] outputArray, int position) {
        int width;
        int height;
        int width2 = 0;
        int height2 = 0;
        int dispHeight = 0;
        Rect r = new Rect();
        float optImgWidth = this.mOptImageScaled.getWidth();
        float optImgHeight = this.mOptImageScaled.getHeight();
        float scaleX = optImgWidth / 2000.0f;
        float scaleY = optImgHeight / 2000.0f;
        if (ImageEditor.getAspectRatio() == 0) {
            dispHeight = 332;
        } else if (ImageEditor.getAspectRatio() == 1) {
            dispHeight = 276;
        }
        if (outputArray[position][0] == 1) {
            width2 = outputArray[position][3] & (-4);
            height2 = outputArray[position][4] & (-2);
            if (height2 > dispHeight) {
                int selectedFace = position / 2;
                int faceTop = (int) ((this.mFaces[selectedFace].face.rect.top + Constant.TRANSITION_INDEXPB) * scaleY);
                int faceBottom = (int) ((this.mFaces[selectedFace].face.rect.bottom + Constant.TRANSITION_INDEXPB) * scaleY);
                int facesize = faceBottom - faceTop;
                if (facesize <= 80 || facesize / 80.0f <= height2 / dispHeight) {
                    outputArray[position][0] = 0;
                }
            }
        }
        if (outputArray[position][0] == 1) {
            r.left = outputArray[position][1];
            r.right = r.left + width2;
            r.top = outputArray[position][2];
            r.bottom = r.top + height2;
        } else {
            int selectedFace2 = position / 2;
            int faceLeft = (int) ((this.mFaces[selectedFace2].face.rect.left + Constant.TRANSITION_INDEXPB) * scaleX);
            int faceTop2 = (int) ((this.mFaces[selectedFace2].face.rect.top + Constant.TRANSITION_INDEXPB) * scaleY);
            int faceRight = (int) ((this.mFaces[selectedFace2].face.rect.right + Constant.TRANSITION_INDEXPB) * scaleX);
            int faceBottom2 = (int) ((this.mFaces[selectedFace2].face.rect.bottom + Constant.TRANSITION_INDEXPB) * scaleY);
            int faceCenterX = (faceLeft + faceRight) / 2;
            int faceCenterY = (faceTop2 + faceBottom2) / 2;
            if (isPortrait(this.mFaces[selectedFace2].face.faceAngle.angle, position)) {
                height = ((int) optImgWidth) / 2;
                width = ((int) optImgHeight) / 2;
            } else {
                width = ((int) optImgWidth) / 2;
                height = ((int) optImgHeight) / 2;
            }
            int width3 = width & (-4);
            int height3 = height & (-2);
            r.left = faceCenterX - (width3 / 2);
            r.top = faceCenterY - (height3 / 2);
            r.right = (width3 / 2) + faceCenterX;
            r.bottom = (height3 / 2) + faceCenterY;
            if (r.left < 0) {
                r.left = 0;
                r.right = r.left + width3;
            }
            if (r.top < 0) {
                r.top = 0;
                r.bottom = r.top + height3;
            }
            if (r.right > optImgWidth) {
                r.right = (int) optImgWidth;
                r.left = (int) (optImgWidth - width3);
            }
            if (r.bottom > optImgHeight) {
                r.bottom = (int) optImgHeight;
                r.top = (int) (optImgHeight - height3);
            }
        }
        return r;
    }

    private boolean isPortrait(int faceAngle, int position) {
        if (faceAngle == 0 && position % 2 == 0) {
            return true;
        }
        return faceAngle != 0 && position % 2 == 1;
    }

    private Bitmap getBitmap(OptimizedImage optImage) {
        boolean isTrue;
        float originalWidth = optImage.getWidth();
        int scaledWidth = (int) (PANEL_SCALING * originalWidth);
        int scaledHeight = optImage.getHeight();
        int scaledWidth2 = scaledWidth & (-4);
        int scaledHeight2 = scaledHeight & (-2);
        OptimizedImage optImage_3_4_scaled = null;
        if (optImage.equals(this.mOptImageScaled)) {
            isTrue = false;
        } else {
            isTrue = true;
            ImageEditor.optImageCount--;
        }
        this.scaleFilter.setSource(optImage, isTrue);
        this.scaleFilter.setDestSize(scaledWidth2, scaledHeight2);
        boolean isExecuted = this.scaleFilter.execute();
        if (isExecuted) {
            optImage_3_4_scaled = this.scaleFilter.getOutput();
            ImageEditor.optImageCount++;
        }
        Bitmap bmp = this.yuvToRgb.yuv2rgb_main(optImage_3_4_scaled);
        ImageEditor.releaseOptImage(optImage_3_4_scaled);
        return bmp;
    }

    private void createGalIndexList() {
        int size = this.mGalIndexList.size();
        if (this.mFaceNum != 0) {
            if (this.mFaceNum == 1) {
                Collections.swap(this.mGalIndexList, 0, 1);
                ((GalObjectList) this.mGalIndexList.get(0)).swapPos = (short) 1;
                ((GalObjectList) this.mGalIndexList.get(1)).swapPos = (short) 0;
                return;
            }
            for (int i = 0; i < size / 2; i++) {
                int swapPos = (size / 2) + i;
                Collections.swap(this.mGalIndexList, i, swapPos);
                ((GalObjectList) this.mGalIndexList.get(i)).swapPos = (short) swapPos;
                ((GalObjectList) this.mGalIndexList.get(swapPos)).swapPos = (short) i;
            }
        }
    }

    private void createOriginalImageBitmap() {
        OptimizedImage optImg = ImageEditor.rotateImageWRTOrientation(this.mOptImageScaled);
        if (optImg != null) {
            this.mOrigBitmap = getBitmap(optImg);
        } else {
            this.mOrigBitmap = getBitmap(this.mOptImageScaled);
        }
        ImageEditor.releaseOptImage(optImg);
    }

    private void addOrigRect() {
        Rect r = new Rect();
        r.left = -1;
        r.right = -1;
        r.bottom = -1;
        r.top = -1;
        GalObjectList g = new GalObjectList();
        g.r = r;
        g.orientation = (byte) -1;
        g.orgPos = (short) -1;
        g.swapPos = (short) -1;
        this.mGalIndexList.addLast(g);
    }

    public void handleLevelBtnImage(int position) {
        Log.d("", "INSIDE handleLevelBtnImage");
        int size = this.mFaceBitmapList.size();
        for (int i = 0; i < size; i++) {
            if (i == position) {
                this.mBtnFaces[i].setBackgroundResource(R.drawable.p_16_dd_parts_pr_facechecker);
            } else {
                this.mBtnFaces[i].setBackgroundResource(R.drawable.p_16_dd_parts_pr_facechecker2);
            }
        }
    }

    /* loaded from: classes.dex */
    public class GalObjectList {
        short orgPos;
        byte orientation;
        Rect r;
        short swapPos;

        public GalObjectList() {
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        ImageEditor.releaseOptImage(this.mOptImageScaled);
        if (this.yuvToRgb != null) {
            this.yuvToRgb.releaseYuvToRgbResources();
        }
        ImageEditor.releaseYUVDSP();
        deInitialize();
        Log.d(this.TAG, "==== onDestroyView:optImageCount = " + ImageEditor.optImageCount);
        super.onDestroyView();
    }

    private void reInitialize() {
        this.faceRectList = new LinkedList<>();
        this.mFaceBitmapList = new LinkedList<>();
        this.mGalleryBitmapList = new LinkedList<>();
        this.mGalIndexList = new LinkedList<>();
        this.mBitmapIndexList = new LinkedList<>();
        this.yuvToRgb = new YuvToRgbConversion(ImageEditor.useYuvToRGBDsp());
    }

    private void releaseBitmaps() {
        int size = this.mFaceBitmapList.size();
        for (int i = 0; i < size; i++) {
            remove(this.mBtnFaces[i]);
            this.mBtnFaces[i].setBackgroundResource(R.drawable.p_16_dd_parts_pr_facechecker2);
            this.mBtnFaces[i].setVisibility(8);
            this.mBtnFaces[i] = null;
            this.paramsfaceButtons[i] = null;
        }
        if (this.mGalleryBitmapList != null) {
            releaseAllBitmap(this.mGalleryBitmapList);
        }
        if (this.mFaceBitmapList != null) {
            releaseAllBitmap(this.mFaceBitmapList);
        }
        if (this.mOrigBitmap != null) {
            this.mOrigBitmap.recycle();
            this.mOrigBitmap = null;
        }
    }

    private void deInitialize() {
        this.mTopValue = 0;
        this.galleryPosition = 0;
        this.mFaceNum = 0;
        this.panel = 0;
        if (this.mFaces != null) {
            int len = this.mFaces.length;
            for (int i = 0; i < len; i++) {
                this.mFaces[i] = null;
            }
            this.mFaces = null;
        }
        if (this.mAnalyzedFaces != null) {
            int len2 = this.mAnalyzedFaces.length;
            for (int i2 = 0; i2 < len2; i2++) {
                this.mAnalyzedFaces[i2] = null;
            }
            this.mAnalyzedFaces = null;
        }
        int size = this.mFaceBitmapList.size();
        for (int i3 = 0; i3 < size; i3++) {
            remove(this.mBtnFaces[i3]);
            this.mBtnFaces[i3].setBackgroundResource(R.drawable.p_16_dd_parts_pr_facechecker2);
            this.mBtnFaces[i3].setVisibility(8);
            this.mBtnFaces[i3] = null;
            this.paramsfaceButtons[i3] = null;
        }
        this.mAspectRatio = 0;
        if (this.mGalleryBitmapList != null) {
            releaseAllBitmap(this.mGalleryBitmapList);
        }
        if (this.mFaceBitmapList != null) {
            releaseAllBitmap(this.mFaceBitmapList);
        }
        if (this.mOrigBitmap != null) {
            this.mOrigBitmap.recycle();
            this.mOrigBitmap = null;
        }
        if (this.mGalIndexList != null) {
            this.mGalIndexList.clear();
            this.mGalIndexList = null;
        }
        if (this.faceRectList != null) {
            this.faceRectList.clear();
            this.faceRectList = null;
        }
        if (this.mBitmapIndexList != null) {
            this.mBitmapIndexList.clear();
            this.mBitmapIndexList = null;
        }
        this.mAdapter.notifyDataSetChanged();
        this.mAdapter = null;
        this.mFrameInfo = null;
        if (this.mLastView != null) {
            this.mLastView.setBackgroundResource(0);
        }
        remove(this.mLastView);
        this.mLastView = null;
        this.mCustomGallery.setAdapter((SpinnerAdapter) null);
        this.mCustomGallery = null;
        this.yuvToRgb = null;
        this.mCurrentView = null;
        System.gc();
    }

    private void remove(ImageButton b) {
        b.buildDrawingCache();
        Bitmap bmp = b.getDrawingCache();
        Log.e("", "used bitmap : " + bmp);
        if (bmp != null) {
            bmp.recycle();
            Log.e("", "recycled bitmap : " + ((Object) null));
        }
        b.destroyDrawingCache();
        System.gc();
    }

    private void remove(View v) {
        if (v != null) {
            Bitmap bmp = v.getDrawingCache();
            if (bmp != null) {
                bmp.recycle();
            }
            System.gc();
        }
    }
}
