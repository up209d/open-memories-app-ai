package com.sony.imaging.app.photoretouch.playback.layout.softskin;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.EVFOffLayout;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.common.widget.EachButton;
import com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.scalar.graphics.ImageAnalyzer;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.FaceNRImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.widget.OptimizedImageView;
import java.util.Vector;

/* loaded from: classes.dex */
public class SoftSkinLayout extends EVFOffLayout {
    private int mColorSelected;
    private int mColorUnSelected;
    private int mCurrentX;
    private int mCurrentY;
    private int mFaceNum;
    private int mIsoInfo;
    private int mLevelSelected;
    private final String TAG = SoftSkinLayout.class.getSimpleName();
    private EachButton mLevelHighBtn = null;
    private EachButton mLevelMidBtn = null;
    private EachButton mLevelLowBtn = null;
    private EachButton mLevelOffBtn = null;
    private EachButton[] buttonArray = null;
    private int mCounter = 0;
    private int mButtonSelection = 1;
    private OptimizedImage mOptImageOriginal = null;
    private OptimizedImage mOptImageScaled = null;
    private OptimizedImage mOptImageNR = null;
    private OptimizedImageView mSoftSkinImage = null;
    private RelativeLayout.LayoutParams mSoftSkinImageLayoutParam = null;
    private DrawFaceFrameView[] mDrawView = null;
    private ImageAnalyzer.AnalyzedFace[] mFaces = null;
    private FrameLayout mFrameLayout = null;
    private FooterGuide mFooterGuide = null;
    private ContentsManager mContentManager = null;
    private ContentInfo mContentInfo = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Level {
        HIGH(1),
        MID(2),
        LOW(3),
        OFF(4),
        DEFAULT(0);

        private int code;

        Level(int c) {
            this.code = c;
        }
    }

    /* loaded from: classes.dex */
    class ButtonHandler implements EachButton.Callback {
        ButtonHandler() {
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onDown(EachButton view) {
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onLongPress(EachButton view) {
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onUp(EachButton view) {
            if (SoftSkinLayout.this.mFaceNum != 0) {
                boolean frameSelected = false;
                switch (view.getId()) {
                    case R.id.beauty_skin_0 /* 2131362194 */:
                        SoftSkinLayout.this.handleLevelBtnImage(Level.HIGH);
                        Log.e(SoftSkinLayout.this.TAG, "mLevelHighBtn Button Tapped");
                        SoftSkinLayout.this.mLevelSelected = Level.HIGH.code;
                        SoftSkinLayout.this.mButtonSelection = 0;
                        int i = 0;
                        while (true) {
                            if (i < SoftSkinLayout.this.mFaceNum) {
                                if (SoftSkinLayout.this.mColorSelected == SoftSkinLayout.this.mDrawView[i].getColor()) {
                                    frameSelected = true;
                                } else {
                                    frameSelected = false;
                                    i++;
                                }
                            }
                        }
                        if (frameSelected) {
                            SoftSkinLayout.this.applyNRImageFilter(SoftSkinLayout.this.mLevelSelected);
                            return;
                        }
                        return;
                    case R.id.beauty_skin_1 /* 2131362195 */:
                        SoftSkinLayout.this.handleLevelBtnImage(Level.MID);
                        Log.e(SoftSkinLayout.this.TAG, "mLevelMidBtn Button Tapped");
                        SoftSkinLayout.this.mLevelSelected = Level.MID.code;
                        SoftSkinLayout.this.mButtonSelection = 1;
                        int i2 = 0;
                        while (true) {
                            if (i2 < SoftSkinLayout.this.mFaceNum) {
                                if (SoftSkinLayout.this.mColorSelected == SoftSkinLayout.this.mDrawView[i2].getColor()) {
                                    frameSelected = true;
                                } else {
                                    frameSelected = false;
                                    i2++;
                                }
                            }
                        }
                        if (frameSelected) {
                            SoftSkinLayout.this.applyNRImageFilter(SoftSkinLayout.this.mLevelSelected);
                            return;
                        }
                        return;
                    case R.id.beauty_skin_2 /* 2131362196 */:
                        SoftSkinLayout.this.handleLevelBtnImage(Level.LOW);
                        Log.e(SoftSkinLayout.this.TAG, "mLevelLowBtn Button Tapped");
                        SoftSkinLayout.this.mLevelSelected = Level.LOW.code;
                        SoftSkinLayout.this.mButtonSelection = 2;
                        int i3 = 0;
                        while (true) {
                            if (i3 < SoftSkinLayout.this.mFaceNum) {
                                if (SoftSkinLayout.this.mColorSelected == SoftSkinLayout.this.mDrawView[i3].getColor()) {
                                    frameSelected = true;
                                } else {
                                    frameSelected = false;
                                    i3++;
                                }
                            }
                        }
                        if (frameSelected) {
                            SoftSkinLayout.this.applyNRImageFilter(SoftSkinLayout.this.mLevelSelected);
                            return;
                        }
                        return;
                    case R.id.beauty_skin_3 /* 2131362197 */:
                        SoftSkinLayout.this.handleLevelBtnImage(Level.OFF);
                        Log.e(SoftSkinLayout.this.TAG, "mLevelOffBtn Button Tapped");
                        SoftSkinLayout.this.mLevelSelected = Level.OFF.code;
                        SoftSkinLayout.this.mButtonSelection = 3;
                        SoftSkinLayout.this.mSoftSkinImage.setOptimizedImage(SoftSkinLayout.this.mOptImageScaled);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View softSkinView = obtainViewFromPool(R.layout.soft_skin);
        ImageView backGround = (ImageView) softSkinView.findViewById(R.id.softskin_background);
        backGround.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        this.mSoftSkinImage = softSkinView.findViewById(R.id.onePhoto);
        this.mSoftSkinImageLayoutParam = (RelativeLayout.LayoutParams) this.mSoftSkinImage.getLayoutParams();
        this.mLevelHighBtn = (EachButton) softSkinView.findViewById(R.id.beauty_skin_0);
        this.mLevelMidBtn = (EachButton) softSkinView.findViewById(R.id.beauty_skin_1);
        this.mLevelLowBtn = (EachButton) softSkinView.findViewById(R.id.beauty_skin_2);
        this.mLevelOffBtn = (EachButton) softSkinView.findViewById(R.id.beauty_skin_3);
        this.buttonArray = new EachButton[]{this.mLevelHighBtn, this.mLevelMidBtn, this.mLevelLowBtn, this.mLevelOffBtn};
        this.mColorSelected = Color.parseColor("#FFF39800");
        this.mColorUnSelected = Color.parseColor("#FFDDDDDD");
        this.mOptImageOriginal = ImageEditor.getImage();
        this.mContentManager = ContentsManager.getInstance();
        this.mContentInfo = this.mContentManager.getContentInfo(this.mContentManager.getContentsId());
        this.mIsoInfo = this.mContentInfo.getInt("ISOSpeedRatings");
        this.mFaces = new ImageAnalyzer.AnalyzedFace[8];
        this.mFrameLayout = (FrameLayout) softSkinView.findViewById(R.id.frameLayout);
        ButtonHandler bHandler = new ButtonHandler();
        this.mLevelHighBtn.setEventListener(bHandler);
        this.mLevelMidBtn.setEventListener(bHandler);
        this.mLevelLowBtn.setEventListener(bHandler);
        this.mLevelOffBtn.setEventListener(bHandler);
        this.mFooterGuide = (FooterGuide) softSkinView.findViewById(R.id.footer_guide);
        return softSkinView;
    }

    protected void rotateOptImageView(OptimizedImageView img) {
        int angle = ImageEditor.getOrientationInfo();
        switch (angle) {
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

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "==== onResume:optImageCount = " + ImageEditor.optImageCount);
        ScaleImageFilter scaleFilter = new ScaleImageFilter();
        scaleFilter.setSource(this.mOptImageOriginal, false);
        this.mLevelSelected = 2;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        if (ImageEditor.getAspectRatio() == 0) {
            scaleFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 428);
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                layoutParams.leftMargin = 50;
                layoutParams.topMargin = 30;
                layoutParams.width = AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL;
                layoutParams.height = 300;
            } else {
                layoutParams.leftMargin = 220;
                layoutParams.topMargin = 30;
                layoutParams.width = 200;
                layoutParams.height = 300;
            }
            this.mSoftSkinImageLayoutParam = layoutParams;
            this.mFrameLayout.setLayoutParams(layoutParams);
        } else if (1 == ImageEditor.getAspectRatio()) {
            scaleFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 360);
            if (1 == ImageEditor.getOrientationInfo() || 3 == ImageEditor.getOrientationInfo()) {
                layoutParams.leftMargin = 53;
                layoutParams.topMargin = 30;
                layoutParams.width = AppRoot.USER_KEYCODE.AF_MF;
                layoutParams.height = 300;
            } else {
                layoutParams.leftMargin = 236;
                layoutParams.topMargin = 30;
                layoutParams.width = 168;
                layoutParams.height = 300;
            }
            this.mSoftSkinImageLayoutParam = layoutParams;
            this.mFrameLayout.setLayoutParams(layoutParams);
        } else if (2 == ImageEditor.getAspectRatio()) {
            scaleFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 480);
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
            this.mSoftSkinImageLayoutParam = layoutParams;
            this.mFrameLayout.setLayoutParams(layoutParams);
        } else if (3 == ImageEditor.getAspectRatio()) {
            scaleFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, AppRoot.USER_KEYCODE.WATER_HOUSING);
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
            this.mSoftSkinImageLayoutParam = layoutParams;
            this.mFrameLayout.setLayoutParams(layoutParams);
        }
        Point p = new Point(layoutParams.width >> 1, layoutParams.height >> 1);
        this.mSoftSkinImage.setPivot(p);
        this.mSoftSkinImage.setLayoutParams(layoutParams);
        boolean isExecuted = scaleFilter.execute();
        if (isExecuted) {
            Log.e(this.TAG, "Scale Image filter executed");
            this.mOptImageScaled = scaleFilter.getOutput();
            ImageEditor.optImageCount++;
            rotateOptImageView(this.mSoftSkinImage);
            this.mSoftSkinImage.setOptimizedImage(this.mOptImageScaled);
        } else {
            Log.e(this.TAG, "Scale Image filter not executed");
        }
        scaleFilter.clearSources();
        scaleFilter.release();
        if (this.mOptImageScaled != null) {
            drawFaceFrames();
            applyNRImageFilter(this.mLevelSelected);
        }
        setKeyBeepPattern(0);
        if (this.mFaceNum > 0) {
            handleLevelBtnImage(Level.DEFAULT);
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity().getApplicationContext(), R.string.STRID_FOOTERGUIDE_SELECTFACE_CANCEL_BY_MENU, R.string.STRID_FOOTERGUIDE_SELECTFACE_CANCEL_FOR_SK));
        } else {
            setAllLevelsUnselected();
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.date_picker_decrement_day_button, android.R.string.hardware));
        }
    }

    private void setAllLevelsUnselected() {
        this.mLevelHighBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_hi_unselected);
        this.mLevelMidBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_mid_unselected);
        this.mLevelLowBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_low_unselected);
        this.mLevelOffBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_off_unselected);
    }

    public void setListenerOnFaceFrames() {
        Log.d(this.TAG, "setListenerOnFaceFrames called");
        this.mDrawView[0].setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.photoretouch.playback.layout.softskin.SoftSkinLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent event) {
                boolean tappedRect;
                Log.d(SoftSkinLayout.this.TAG, "onTouchEvent called");
                switch (event.getAction()) {
                    case 0:
                        SoftSkinLayout.this.mCurrentX = (int) event.getX();
                        SoftSkinLayout.this.mCurrentY = (int) event.getY();
                        Log.e(SoftSkinLayout.this.TAG, " ACTION_DOWN ");
                        int selectedCount = 0;
                        for (int i = 0; i < SoftSkinLayout.this.mFaceNum; i++) {
                            if (SoftSkinLayout.this.mColorSelected == SoftSkinLayout.this.mDrawView[i].getColor()) {
                                selectedCount++;
                            }
                        }
                        for (int i2 = 0; i2 < SoftSkinLayout.this.mFaceNum; i2++) {
                            if (SoftSkinLayout.this.mCurrentX > SoftSkinLayout.this.mDrawView[i2].mLeft && SoftSkinLayout.this.mCurrentX < SoftSkinLayout.this.mDrawView[i2].mRight && SoftSkinLayout.this.mCurrentY > SoftSkinLayout.this.mDrawView[i2].mTop && SoftSkinLayout.this.mCurrentY < SoftSkinLayout.this.mDrawView[i2].mBottom) {
                                Log.d(SoftSkinLayout.this.TAG, "----------------Inside Rectangle " + (i2 + 1));
                                tappedRect = true;
                            } else {
                                tappedRect = false;
                            }
                            if (tappedRect) {
                                if (SoftSkinLayout.this.mColorSelected != SoftSkinLayout.this.mDrawView[i2].getColor()) {
                                    if (SoftSkinLayout.this.mColorUnSelected == SoftSkinLayout.this.mDrawView[i2].getColor()) {
                                        selectedCount++;
                                        SoftSkinLayout.this.mDrawView[i2].setColor(SoftSkinLayout.this.mColorSelected);
                                    }
                                } else if (selectedCount - 1 != 0) {
                                    SoftSkinLayout.this.mDrawView[i2].setColor(SoftSkinLayout.this.mColorUnSelected);
                                    selectedCount--;
                                }
                                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                                if (Level.OFF.code != SoftSkinLayout.this.mLevelSelected) {
                                    SoftSkinLayout.this.applyNRImageFilter(SoftSkinLayout.this.mLevelSelected);
                                } else {
                                    SoftSkinLayout.this.mSoftSkinImage.setOptimizedImage(SoftSkinLayout.this.mOptImageScaled);
                                }
                            }
                        }
                        break;
                    default:
                        return true;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyNRImageFilter(int level) {
        Vector<Rect> faceList = getFaceList();
        if (faceList.size() != 0) {
            FaceNRImageFilter nRFilter = new FaceNRImageFilter();
            nRFilter.setFaceList(faceList);
            nRFilter.setISOValue(this.mIsoInfo);
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
            nRFilter.setSource(this.mOptImageScaled, false);
            ImageEditor.releaseOptImage(this.mOptImageNR);
            boolean isExecuted = nRFilter.execute();
            if (isExecuted) {
                Log.e(this.TAG, "NRImage filter executed");
                this.mOptImageNR = nRFilter.getOutput();
                this.mSoftSkinImage.setOptimizedImage(this.mOptImageNR);
                ImageEditor.optImageCount++;
            } else {
                Log.e(this.TAG, "NRImage filter not executed");
            }
            nRFilter.clearSources();
            nRFilter.release();
        }
    }

    public void handleLevelBtnImage(Level level) {
        this.mLevelHighBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_hi_unselected);
        this.mLevelMidBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_mid_unselected);
        this.mLevelLowBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_low_unselected);
        this.mLevelOffBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_off_unselected);
        switch (level) {
            case HIGH:
                this.mLevelHighBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_hi_selected);
                return;
            case MID:
                this.mLevelMidBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_mid_selected);
                return;
            case LOW:
                this.mLevelLowBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_low_selected);
                return;
            case OFF:
                this.mLevelOffBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_off_selected);
                return;
            default:
                this.mLevelMidBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_mid_selected);
                return;
        }
    }

    private void setLevelBtnEnable() {
        this.mLevelHighBtn.setBackgroundResource(R.drawable.btnimage_focused);
        this.mLevelMidBtn.setBackgroundResource(R.drawable.btnimage_focused);
        this.mLevelLowBtn.setBackgroundResource(R.drawable.btnimage_focused);
        this.mLevelOffBtn.setBackgroundResource(R.drawable.btnimage_focused);
        this.mLevelHighBtn.setOnTouchListener(null);
        this.mLevelMidBtn.setOnTouchListener(null);
        this.mLevelLowBtn.setOnTouchListener(null);
        this.mLevelOffBtn.setOnTouchListener(null);
    }

    private void setLevelBtnDisable() {
        this.mLevelHighBtn.setBackgroundResource(0);
        this.mLevelMidBtn.setBackgroundResource(0);
        this.mLevelLowBtn.setBackgroundResource(0);
        this.mLevelOffBtn.setBackgroundResource(0);
        this.mLevelHighBtn.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        this.mLevelMidBtn.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        this.mLevelLowBtn.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        this.mLevelOffBtn.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
    }

    private void drawFaceFrames() {
        float scaleW;
        float scaleH;
        ImageAnalyzer imgAnalyzer = new ImageAnalyzer();
        this.mFaceNum = imgAnalyzer.findFaces(this.mOptImageOriginal, this.mFaces);
        imgAnalyzer.release();
        if (this.mFaceNum < 1) {
            openLayout(Constant.ID_MESSAGENOFACE);
            this.mLevelHighBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_hi_unselected);
            this.mLevelMidBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_mid_unselected);
            this.mLevelLowBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_low_unselected);
            this.mLevelOffBtn.setBackgroundResource(R.drawable.layer_list_soft_skin_off_unselected);
            setLevelBtnDisable();
            return;
        }
        setLevelBtnEnable();
        this.mCounter = this.mFaceNum;
        OptimizedImageView.LayoutInfo info = this.mSoftSkinImage.getLayoutInfo();
        float viewWidth = info.drawSize.width();
        float viewHeight = info.drawSize.height();
        int leftMargin = info.drawSize.left;
        int topMargin = info.drawSize.top;
        DisplayManager.DeviceInfo deviceInfo = DisplayModeObserver.getInstance().getActiveDeviceInfo();
        if (deviceInfo != null && 2 == deviceInfo.aspect && 43 == ScalarProperties.getInt("device.panel.aspect")) {
            float newWidth = viewWidth / 0.75f;
            leftMargin = (int) (leftMargin - ((newWidth - viewWidth) / 2.0f));
            viewWidth = newWidth;
        }
        float scaledImgWidth = this.mOptImageScaled.getWidth();
        float scaledImgHeight = this.mOptImageScaled.getHeight();
        float scaleX = 2000.0f / scaledImgWidth;
        float scaleY = 2000.0f / scaledImgHeight;
        if (this.mDrawView != null) {
            int c = this.mDrawView.length;
            for (int i = 0; i < c; i++) {
                this.mFrameLayout.removeView(this.mDrawView[i]);
            }
        }
        this.mDrawView = new DrawFaceFrameView[this.mFaceNum];
        Rect face = new Rect();
        for (int i2 = 0; i2 < this.mFaceNum; i2++) {
            this.mDrawView[i2] = new DrawFaceFrameView(getActivity().getApplicationContext());
            float left = (this.mFaces[i2].face.rect.left + Constant.TRANSITION_INDEXPB) / scaleX;
            float top = (this.mFaces[i2].face.rect.top + Constant.TRANSITION_INDEXPB) / scaleY;
            float right = (this.mFaces[i2].face.rect.right + Constant.TRANSITION_INDEXPB) / scaleX;
            float bottom = (this.mFaces[i2].face.rect.bottom + Constant.TRANSITION_INDEXPB) / scaleY;
            if (6 == ImageEditor.getOrientationInfo()) {
                face.left = (int) (scaledImgHeight - bottom);
                face.top = (int) left;
                face.right = (int) (scaledImgHeight - top);
                face.bottom = (int) right;
                scaleW = viewWidth / scaledImgHeight;
                scaleH = viewHeight / scaledImgWidth;
            } else if (3 == ImageEditor.getOrientationInfo()) {
                face.left = (int) (scaledImgWidth - right);
                face.top = (int) (scaledImgHeight - bottom);
                face.right = (int) (scaledImgWidth - left);
                face.bottom = (int) (scaledImgHeight - top);
                scaleW = viewWidth / scaledImgWidth;
                scaleH = viewHeight / scaledImgHeight;
            } else if (8 == ImageEditor.getOrientationInfo()) {
                face.left = (int) top;
                face.top = (int) (scaledImgWidth - right);
                face.right = (int) bottom;
                face.bottom = (int) (scaledImgWidth - left);
                scaleW = viewWidth / scaledImgHeight;
                scaleH = viewHeight / scaledImgWidth;
            } else {
                face.left = (int) left;
                face.top = (int) top;
                face.right = (int) right;
                face.bottom = (int) bottom;
                scaleW = viewWidth / scaledImgWidth;
                scaleH = viewHeight / scaledImgHeight;
            }
            this.mDrawView[i2].mLeft = ((int) (face.left * scaleW)) + leftMargin;
            this.mDrawView[i2].mTop = ((int) (face.top * scaleH)) + topMargin;
            this.mDrawView[i2].mRight = ((int) (face.right * scaleW)) + leftMargin;
            this.mDrawView[i2].mBottom = ((int) (face.bottom * scaleH)) + topMargin;
            this.mFrameLayout.addView(this.mDrawView[i2]);
            this.mDrawView[i2].setColor(this.mColorSelected);
            Log.e("coordinates face :" + i2, LogHelper.MSG_OPEN_BRACKET + this.mFaces[i2].face.rect.left + AbstractSupportedChecker.SEPARATOR + this.mFaces[i2].face.rect.top + ")(" + this.mFaces[i2].face.rect.right + AbstractSupportedChecker.SEPARATOR + this.mFaces[i2].face.rect.bottom + LogHelper.MSG_CLOSE_BRACKET);
            Log.e("coordinates after conversion", LogHelper.MSG_OPEN_BRACKET + this.mDrawView[i2].mLeft + AbstractSupportedChecker.SEPARATOR + this.mDrawView[i2].mTop + ")(" + this.mDrawView[i2].mRight + AbstractSupportedChecker.SEPARATOR + this.mDrawView[i2].mBottom + LogHelper.MSG_CLOSE_BRACKET);
        }
        if (this.mFaceNum > 0) {
            setListenerOnFaceFrames();
        }
    }

    private void saveImage() {
        ImageEditor.releaseOptImage(this.mOptImageNR);
        ImageEditor.releaseOptImage(this.mOptImageScaled);
        Vector<Rect> faceList = getFaceList();
        if (faceList.size() != 0 && this.mLevelSelected != Level.OFF.code) {
            Log.d(this.TAG, "==== editImage:optImageCount = " + ImageEditor.optImageCount);
            SoftSkinSavingTask task = new SoftSkinSavingTask(faceList, this.mIsoInfo, this.mLevelSelected);
            ImageEditor.executeResultReflection(task, this.mOptImageOriginal);
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        ImageEditor.releaseOptImage(this.mOptImageNR);
        ImageEditor.releaseOptImage(this.mOptImageScaled);
        if (this.mSoftSkinImage != null) {
            this.mSoftSkinImage.release();
            this.mSoftSkinImage = null;
        }
        deInitialize();
        Log.d(this.TAG, "==== onDestroyView:optImageCount = " + ImageEditor.optImageCount);
        super.onDestroyView();
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

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode1, KeyEvent event) {
        if (isFinder()) {
            return -1;
        }
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (this.mFaceNum > 0) {
                    this.mButtonSelection--;
                    if (this.mButtonSelection < 0) {
                        this.mButtonSelection = this.buttonArray.length - 1;
                    }
                    leftRightSelection();
                    return 1;
                }
                return -1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (this.mFaceNum > 0) {
                    this.mButtonSelection = (this.mButtonSelection + 1) % this.buttonArray.length;
                    leftRightSelection();
                    return 1;
                }
                return -1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (PhotoRetouchSubMenuLayout.isRepeat(event)) {
                    return -1;
                }
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                saveImage();
                return 1;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (this.mFaceNum == 0) {
                    return -1;
                }
                setListenerOnFaceFramesONSK2();
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

    void leftRightSelection() {
        boolean frameSelected = false;
        switch (this.mButtonSelection) {
            case 0:
                handleLevelBtnImage(Level.HIGH);
                Log.e(this.TAG, "mLevelHighBtn Button Tapped");
                this.mLevelSelected = Level.HIGH.code;
                int i = 0;
                while (true) {
                    if (i < this.mFaceNum) {
                        if (this.mColorSelected == this.mDrawView[i].getColor()) {
                            frameSelected = true;
                        } else {
                            frameSelected = false;
                            i++;
                        }
                    }
                }
                if (frameSelected) {
                    applyNRImageFilter(this.mLevelSelected);
                    return;
                }
                return;
            case 1:
                handleLevelBtnImage(Level.MID);
                Log.e(this.TAG, "mLevelMidBtn Button Tapped");
                this.mLevelSelected = Level.MID.code;
                int i2 = 0;
                while (true) {
                    if (i2 < this.mFaceNum) {
                        if (this.mColorSelected == this.mDrawView[i2].getColor()) {
                            frameSelected = true;
                        } else {
                            frameSelected = false;
                            i2++;
                        }
                    }
                }
                if (frameSelected) {
                    applyNRImageFilter(this.mLevelSelected);
                    return;
                }
                return;
            case 2:
                handleLevelBtnImage(Level.LOW);
                Log.e(this.TAG, "mLevelLowBtn Button Tapped");
                this.mLevelSelected = Level.LOW.code;
                int i3 = 0;
                while (true) {
                    if (i3 < this.mFaceNum) {
                        if (this.mColorSelected == this.mDrawView[i3].getColor()) {
                            frameSelected = true;
                        } else {
                            frameSelected = false;
                            i3++;
                        }
                    }
                }
                if (frameSelected) {
                    applyNRImageFilter(this.mLevelSelected);
                    return;
                }
                return;
            case 3:
                handleLevelBtnImage(Level.OFF);
                Log.e(this.TAG, "mLevelOffBtn Button Tapped");
                this.mLevelSelected = Level.OFF.code;
                this.mSoftSkinImage.setOptimizedImage(this.mOptImageScaled);
                return;
            default:
                return;
        }
    }

    private void setListenerOnFaceFramesONSK2() {
        this.mCounter++;
        if (this.mFaceNum < this.mCounter) {
            this.mCounter = 0;
        }
        for (int i = 0; i < this.mFaceNum; i++) {
            if (i == this.mCounter || this.mFaceNum == this.mCounter) {
                this.mDrawView[i].setColor(this.mColorSelected);
            } else {
                this.mDrawView[i].setColor(this.mColorUnSelected);
            }
        }
        if (Level.OFF.code != this.mLevelSelected) {
            applyNRImageFilter(this.mLevelSelected);
        }
    }

    private void deInitialize() {
        int len = this.buttonArray.length;
        for (int i = 0; i < len; i++) {
            this.buttonArray[i] = null;
        }
        this.buttonArray = null;
        this.mLevelHighBtn = null;
        this.mLevelMidBtn = null;
        this.mLevelLowBtn = null;
        this.mLevelOffBtn = null;
        this.mCounter = 0;
        this.mButtonSelection = 1;
        if (this.mSoftSkinImage != null) {
            this.mSoftSkinImage.release();
            this.mSoftSkinImage = null;
        }
        this.mSoftSkinImageLayoutParam = null;
        for (int i2 = 0; i2 < this.mFaceNum; i2++) {
            this.mFrameLayout.removeView(this.mDrawView[i2]);
            this.mDrawView[i2] = null;
        }
        this.mDrawView = null;
        this.mDrawView = null;
        if (this.mFaces != null) {
            int len2 = this.mFaces.length;
            for (int i3 = 0; i3 < len2; i3++) {
                this.mFaces[i3] = null;
            }
        }
        this.mFooterGuide = null;
        this.mIsoInfo = 0;
        this.mFrameLayout = null;
        this.mCurrentX = 0;
        this.mCurrentY = 0;
        this.mFaceNum = 0;
        this.mLevelSelected = 2;
        this.mContentManager = null;
        this.mContentInfo = null;
        this.mColorSelected = Color.parseColor("#FFF39800");
        this.mColorUnSelected = Color.parseColor("#FFDDDDDD");
    }

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        if (device != 1) {
            if (this.mDrawView != null) {
                drawFaceFrames();
                if (this.mFaceNum > 0) {
                    switch (this.mButtonSelection) {
                        case 0:
                            handleLevelBtnImage(Level.HIGH);
                            break;
                        case 1:
                            handleLevelBtnImage(Level.MID);
                            break;
                        case 2:
                            handleLevelBtnImage(Level.LOW);
                            break;
                        case 3:
                            handleLevelBtnImage(Level.OFF);
                            break;
                    }
                } else {
                    setAllLevelsUnselected();
                }
            }
        } else {
            Log.e("", "EVF");
        }
        super.onLayoutModeChanged(device, displayMode);
    }
}
