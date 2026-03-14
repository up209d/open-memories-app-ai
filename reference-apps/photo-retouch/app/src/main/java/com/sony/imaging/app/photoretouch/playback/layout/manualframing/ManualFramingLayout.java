package com.sony.imaging.app.photoretouch.playback.layout.manualframing;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.EVFOffLayout;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout;
import com.sony.imaging.app.photoretouch.playback.control.YuvToRgbConversion;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;
import com.sony.scalar.graphics.imagefilter.ScaleImageFilter;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class ManualFramingLayout extends EVFOffLayout implements View.OnClickListener {
    private static final float PANEL_SCALING;
    private static final int TOUCH_GAP_IN = 30;
    private static final int TOUCH_GAP_OUT = 50;
    private static long mKeyDownTime = 0;
    private FrameLayout mBlackBoard;
    private int mBottomBoundary;
    private ImageView mDarkImage;
    private FrameLayout.LayoutParams mDarkImageLayoutParams;
    private ImageView mFrame;
    private float mFrameBottom;
    private int mFrameHeight;
    private FrameLayout mFrameLayout;
    private float mFrameLeft;
    private float mFrameRight;
    private float mFrameTop;
    private int mFrameWidth;
    private float mInitialX;
    private float mInitialY;
    private RelativeLayout.LayoutParams mLayoutParams;
    private int mLeftBoundary;
    private ImageView mOneImage;
    private OptimizedImage mOriginalImage;
    private FrameLayout.LayoutParams mParams;
    private FrameLayout.LayoutParams mParamsNew;
    private int mRightBoundary;
    private int mRotateInfo;
    private int mTopBoundary;
    private final String TAG = ManualFramingLayout.class.getSimpleName();
    private char mTappedCorner = 'E';
    private boolean mIsCornerTouched = false;
    private boolean mTouchedInside = false;
    private int mMinFrameWidth = 0;
    private int mMinFrameHeight = 0;
    private boolean mOnce = true;
    private double mAspectRatio = 0.0d;
    private View mCurrentView = null;
    private OptimizedImage mScaledImage = null;
    private Bitmap mOrigBitmap = null;
    private PhotoRetouchSubMenuLayout mPhotoRetouchSubMenuLayout = null;
    private boolean isKeyDown = false;
    YuvToRgbConversion yuvToRgb = null;
    OptimizedImage mEditImage = null;

    static {
        if (169 == ScalarProperties.getInt("device.panel.aspect")) {
            PANEL_SCALING = 0.75f;
        } else {
            PANEL_SCALING = 1.0f;
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = inflater.inflate(R.layout.manual_framing, (ViewGroup) null);
        }
        this.mPhotoRetouchSubMenuLayout = (PhotoRetouchSubMenuLayout) getLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT);
        ImageView backGround = (ImageView) this.mCurrentView.findViewById(R.id.manual_background);
        backGround.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        this.mFrame = (ImageView) this.mCurrentView.findViewById(R.id.cropping_frame);
        this.mOneImage = (ImageView) this.mCurrentView.findViewById(R.id.one_image);
        this.mDarkImage = (ImageView) this.mCurrentView.findViewById(R.id.dark_image);
        this.mDarkImage.setAlpha(85);
        this.mBlackBoard = (FrameLayout) this.mCurrentView.findViewById(R.id.blackboard);
        this.mFrameLayout = (FrameLayout) this.mCurrentView.findViewById(R.id.frame_layout);
        this.mLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
        this.mParams = new FrameLayout.LayoutParams(-2, -2);
        this.mDarkImageLayoutParams = new FrameLayout.LayoutParams(-2, -2);
        this.mParamsNew = new FrameLayout.LayoutParams(-2, -2);
        this.mFrame.setBackgroundResource(R.drawable.p_16_dd_parts_pr_croppingframe_divi9);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "==== onResume:optImageCount = " + ImageEditor.optImageCount);
        Log.d("INSIDE", "ONRESUME");
        reInitialize();
        this.mScaledImage = getScaledImage();
        if (this.mScaledImage != null) {
            Log.d("INSIDE", "getting Images");
            this.mOrigBitmap = getRgbFromYuv(this.mScaledImage);
            this.mDarkImage.setImageBitmap(this.mOrigBitmap);
            this.mOneImage.setImageBitmap(giveNewBitmap(this.mOrigBitmap));
            this.mBlackBoard.setBackgroundColor(Color.argb(255, 0, 0, 0));
        }
        this.mDarkImage.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.photoretouch.playback.layout.manualframing.ManualFramingLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (ManualFramingLayout.this.mOnce) {
                    ManualFramingLayout.this.refreshFrameCoords();
                    ManualFramingLayout.this.mOnce = false;
                }
                switch (event.getAction()) {
                    case 0:
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                        ManualFramingLayout.this.mInitialX = (int) event.getX();
                        ManualFramingLayout.this.mInitialY = (int) event.getY();
                        ManualFramingLayout.this.isInside(ManualFramingLayout.this.mInitialX, ManualFramingLayout.this.mInitialY);
                        Log.v("IsInside", "" + ManualFramingLayout.this.mTouchedInside);
                        ManualFramingLayout.this.mTappedCorner = ManualFramingLayout.this.isCorner(ManualFramingLayout.this.mInitialX, ManualFramingLayout.this.mInitialY);
                        Log.v("Tapped Corner", "" + ManualFramingLayout.this.mTappedCorner);
                        Log.v("mIsCornerTouched", "" + ManualFramingLayout.this.mIsCornerTouched);
                        return true;
                    case 1:
                        ManualFramingLayout.this.refreshFrameCoords();
                        ManualFramingLayout.this.mOneImage.setImageBitmap(ManualFramingLayout.this.giveNewBitmap(ManualFramingLayout.this.mOrigBitmap));
                        ManualFramingLayout.this.moveRgb();
                        return true;
                    case 2:
                        ManualFramingLayout.this.refreshFrameCoords();
                        if (ManualFramingLayout.this.mIsCornerTouched) {
                            ManualFramingLayout.this.adjustFrame(event);
                        } else if (ManualFramingLayout.this.mTouchedInside) {
                            ManualFramingLayout.this.moveFrame(event);
                        }
                        ManualFramingLayout.this.updateImageLayout();
                        return true;
                    default:
                        return false;
                }
            }
        });
        if (getLayout(Constant.ID_FRAMINGLAYOUT).getView() != null) {
            getLayout(Constant.ID_FRAMINGLAYOUT).closeLayout();
        }
        openLayout(Constant.ID_MANUALSTARTUPMESSAGE);
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Constant.ACTION_LAYOUT_EXIT_BY_MENU);
        setKeyBeepPattern(0);
    }

    public OptimizedImage getScaledImage() {
        this.mOriginalImage = ImageEditor.getImage();
        setAreaFigLayoutParams();
        ScaleImageFilter scaleImageFilter = new ScaleImageFilter();
        scaleImageFilter.setSource(this.mOriginalImage, false);
        if (ImageEditor.getAspectRatio() == 0) {
            scaleImageFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 428);
        } else if (ImageEditor.getAspectRatio() == 1) {
            scaleImageFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 360);
        } else if (ImageEditor.getAspectRatio() == 2) {
            scaleImageFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, 480);
        } else if (ImageEditor.getAspectRatio() == 3) {
            scaleImageFilter.setDestSize(AppRoot.USER_KEYCODE.WATER_HOUSING, AppRoot.USER_KEYCODE.WATER_HOUSING);
        }
        OptimizedImage optImageScaled = null;
        boolean isExecuted = scaleImageFilter.execute();
        if (isExecuted) {
            optImageScaled = scaleImageFilter.getOutput();
            ImageEditor.optImageCount++;
            Log.e("YES", "filter is executed");
        } else {
            Log.e("NO", "filter is not executed");
        }
        scaleImageFilter.release();
        OptimizedImage optImageRotated = ImageEditor.rotateImageWRTOrientation(optImageScaled);
        if (optImageRotated != null) {
            ImageEditor.releaseOptImage(optImageScaled);
            optImageScaled = optImageRotated;
        }
        float originalWidth = optImageScaled.getWidth();
        int scaledWidth = (int) (PANEL_SCALING * originalWidth);
        int scaledHeight = optImageScaled.getHeight();
        OptimizedImage optImage_3_4_scaled = null;
        ScaleImageFilter scaleFilter3by4 = new ScaleImageFilter();
        scaleFilter3by4.setSource(optImageScaled, false);
        scaleFilter3by4.setDestSize(scaledWidth & (-4), scaledHeight & (-2));
        boolean isWidthReduced = scaleFilter3by4.execute();
        ImageEditor.releaseOptImage(optImageScaled);
        if (isWidthReduced) {
            optImage_3_4_scaled = scaleFilter3by4.getOutput();
            ImageEditor.optImageCount++;
        } else {
            Log.d("ERROR", "scaleFilter3by4 not executed");
        }
        scaleFilter3by4.release();
        return optImage_3_4_scaled;
    }

    public char isCorner(float x, float y) {
        Log.v("INSIDE", "isCorner");
        if (this.mInitialX >= this.mFrameLeft - 50.0f && this.mInitialX <= this.mFrameLeft + 30.0f && this.mInitialY >= this.mFrameTop - 50.0f && this.mInitialY <= this.mFrameTop + 30.0f) {
            this.mIsCornerTouched = true;
            return 'A';
        }
        if (this.mInitialX >= this.mFrameRight - 30.0f && this.mInitialX <= this.mFrameRight + 50.0f && this.mInitialY >= this.mFrameTop - 50.0f && this.mInitialY <= this.mFrameTop + 30.0f) {
            this.mIsCornerTouched = true;
            return 'B';
        }
        if (this.mInitialX >= (this.mFrameRight - 10.0f) - 40.0f && this.mInitialX <= (this.mFrameRight - 10.0f) + 50.0f && this.mInitialY >= (this.mFrameBottom - 10.0f) - 40.0f && this.mInitialY <= (this.mFrameBottom - 10.0f) + 50.0f) {
            this.mIsCornerTouched = true;
            return 'C';
        }
        if (this.mInitialX >= this.mFrameLeft - 50.0f && this.mInitialX <= this.mFrameLeft + 30.0f && this.mInitialY >= this.mFrameBottom - 30.0f && this.mInitialY <= this.mFrameBottom + 50.0f) {
            this.mIsCornerTouched = true;
            return 'D';
        }
        this.mIsCornerTouched = false;
        return 'E';
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void isInside(float x, float y) {
        Log.v("INSIDE", "isInside");
        Log.v("TOUCH POINT", "" + x + ExposureModeController.SOFT_SNAP + y);
        if (x > this.mFrameLeft && x < this.mFrameRight && y > this.mFrameTop && y < this.mFrameBottom) {
            this.mTouchedInside = true;
        } else {
            this.mTouchedInside = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveFrame(MotionEvent event) {
        Log.v("INSIDE", "moveFrame");
        float currentX = event.getX();
        float currentY = event.getY();
        float xSpace = currentX - this.mInitialX;
        float ySpace = currentY - this.mInitialY;
        this.mInitialX = currentX;
        this.mInitialY = currentY;
        this.mFrameLeft += xSpace;
        this.mFrameRight += xSpace;
        this.mFrameTop += ySpace;
        this.mFrameBottom += ySpace;
        this.mFrameWidth = (int) (this.mFrameRight - this.mFrameLeft);
        if (this.mFrameLeft < this.mLeftBoundary) {
            this.mFrameLeft = this.mLeftBoundary;
            this.mFrameRight = this.mFrameLeft + this.mFrameWidth;
        }
        if (this.mFrameRight > this.mRightBoundary) {
            this.mFrameRight = this.mRightBoundary;
            this.mFrameLeft = this.mFrameRight - this.mFrameWidth;
        }
        this.mFrameHeight = (int) (this.mFrameBottom - this.mFrameTop);
        if (this.mFrameTop < this.mTopBoundary) {
            this.mFrameTop = this.mTopBoundary;
            this.mFrameBottom = this.mFrameTop + this.mFrameHeight;
        }
        if (this.mFrameBottom > this.mBottomBoundary) {
            this.mFrameBottom = this.mBottomBoundary;
            this.mFrameTop = this.mFrameBottom - this.mFrameHeight;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustFrame(MotionEvent event) {
        Log.v("INSIDE", "adjustFrame");
        Log.v("Minimum", "" + this.mMinFrameWidth + ExposureModeController.SOFT_SNAP + this.mMinFrameHeight);
        switch (this.mTappedCorner) {
            case 'A':
                this.mFrameTop = event.getY();
                Log.v("mFrameTop", "" + this.mFrameTop);
                if (this.mFrameBottom - this.mFrameTop < this.mMinFrameHeight) {
                    this.mFrameTop = this.mFrameBottom - this.mMinFrameHeight;
                }
                if (this.mFrameTop < this.mTopBoundary) {
                    this.mFrameTop = this.mTopBoundary;
                }
                this.mFrameHeight = (int) (this.mFrameBottom - this.mFrameTop);
                this.mFrameWidth = ((int) ((this.mFrameHeight - 26) * this.mAspectRatio)) + 20;
                this.mFrameLeft = this.mFrameRight - this.mFrameWidth;
                if (this.mFrameLeft < this.mLeftBoundary) {
                    this.mFrameLeft = this.mLeftBoundary;
                    this.mFrameWidth = (int) (this.mFrameRight - this.mFrameLeft);
                    return;
                }
                return;
            case 'B':
                this.mFrameTop = event.getY();
                if (this.mFrameBottom - this.mFrameTop < this.mMinFrameHeight) {
                    this.mFrameTop = this.mFrameBottom - this.mMinFrameHeight;
                }
                if (this.mFrameTop < this.mTopBoundary) {
                    this.mFrameTop = this.mTopBoundary;
                }
                this.mFrameHeight = (int) (this.mFrameBottom - this.mFrameTop);
                this.mFrameWidth = ((int) ((this.mFrameHeight - 26) * this.mAspectRatio)) + 20;
                this.mFrameRight = this.mFrameLeft + this.mFrameWidth;
                if (this.mFrameRight > this.mRightBoundary) {
                    this.mFrameRight = this.mRightBoundary;
                    this.mFrameWidth = (int) (this.mFrameRight - this.mFrameLeft);
                    return;
                }
                return;
            case 'C':
                this.mFrameBottom = event.getY();
                if (this.mFrameBottom - this.mFrameTop < this.mMinFrameHeight) {
                    this.mFrameBottom = this.mFrameTop + this.mMinFrameHeight;
                }
                if (this.mFrameBottom > this.mBottomBoundary) {
                    this.mFrameBottom = this.mBottomBoundary;
                }
                this.mFrameHeight = (int) (this.mFrameBottom - this.mFrameTop);
                this.mFrameWidth = ((int) ((this.mFrameHeight - 26) * this.mAspectRatio)) + 20;
                this.mFrameRight = this.mFrameLeft + this.mFrameWidth;
                if (this.mFrameRight > this.mRightBoundary) {
                    this.mFrameRight = this.mRightBoundary;
                    this.mFrameWidth = (int) (this.mFrameRight - this.mFrameLeft);
                    return;
                }
                return;
            case 'D':
                this.mFrameBottom = event.getY();
                if (this.mFrameBottom - this.mFrameTop < this.mMinFrameHeight) {
                    this.mFrameBottom = this.mFrameTop + this.mMinFrameHeight;
                }
                if (this.mFrameBottom > this.mBottomBoundary) {
                    this.mFrameBottom = this.mBottomBoundary;
                }
                this.mFrameHeight = (int) (this.mFrameBottom - this.mFrameTop);
                this.mFrameWidth = ((int) ((this.mFrameHeight - 26) * this.mAspectRatio)) + 20;
                this.mFrameLeft = this.mFrameRight - this.mFrameWidth;
                if (this.mFrameLeft < this.mLeftBoundary) {
                    this.mFrameLeft = this.mLeftBoundary;
                    this.mFrameWidth = (int) (this.mFrameRight - this.mFrameLeft);
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshFrameCoords() {
        Log.v("INSIDE", "refreshFrameCoords");
        this.mFrameLeft = this.mFrame.getLeft();
        this.mFrameTop = this.mFrame.getTop();
        this.mFrameRight = this.mFrame.getRight();
        this.mFrameBottom = this.mFrame.getBottom();
        this.mFrameWidth = this.mFrame.getWidth();
        this.mFrameHeight = this.mFrame.getHeight();
        Log.d("IMAGE W/H", "" + this.mLayoutParams.width + ExposureModeController.SOFT_SNAP + this.mLayoutParams.height);
        Log.d("FRAME W/H", "" + this.mFrameWidth + ExposureModeController.SOFT_SNAP + this.mFrameHeight);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
    }

    private Bitmap getRgbFromYuv(OptimizedImage optScaledImage) {
        return this.yuvToRgb.yuv2rgb_main(optScaledImage);
    }

    public void setAreaFigLayoutParams() {
        this.mRotateInfo = ImageEditor.getOrientationInfo();
        if (ImageEditor.getAspectRatio() == 0) {
            if (1 == this.mRotateInfo || 3 == this.mRotateInfo) {
                this.mLayoutParams.leftMargin = 88;
                this.mLayoutParams.topMargin = 6;
                this.mLayoutParams.width = 469;
                this.mLayoutParams.height = 425;
            } else {
                this.mLayoutParams.leftMargin = 212;
                this.mLayoutParams.topMargin = 6;
                this.mLayoutParams.width = 220;
                this.mLayoutParams.height = 425;
            }
        } else if (1 == ImageEditor.getAspectRatio()) {
            if (1 == this.mRotateInfo || 3 == this.mRotateInfo) {
                this.mLayoutParams.leftMargin = 88;
                this.mLayoutParams.topMargin = 37;
                this.mLayoutParams.width = 469;
                this.mLayoutParams.height = 363;
            } else {
                this.mLayoutParams.leftMargin = 228;
                this.mLayoutParams.topMargin = 6;
                this.mLayoutParams.width = 188;
                this.mLayoutParams.height = 425;
            }
        } else if (2 == ImageEditor.getAspectRatio()) {
            if (1 == this.mRotateInfo || 3 == this.mRotateInfo) {
                this.mLayoutParams.leftMargin = 113;
                this.mLayoutParams.topMargin = 6;
                this.mLayoutParams.width = 419;
                this.mLayoutParams.height = 425;
            } else {
                this.mLayoutParams.leftMargin = 200;
                this.mLayoutParams.topMargin = 6;
                this.mLayoutParams.width = 244;
                this.mLayoutParams.height = 425;
            }
        } else if (3 == ImageEditor.getAspectRatio()) {
            if (1 == this.mRotateInfo || 3 == this.mRotateInfo) {
                this.mLayoutParams.leftMargin = 162;
                this.mLayoutParams.topMargin = 6;
                this.mLayoutParams.width = 319;
                this.mLayoutParams.height = 425;
            } else {
                this.mLayoutParams.leftMargin = 162;
                this.mLayoutParams.topMargin = 6;
                this.mLayoutParams.width = 319;
                this.mLayoutParams.height = 425;
            }
        }
        Log.d(this.TAG, "@@Test mLayoutParams.leftMargin(origin):" + this.mLayoutParams.leftMargin);
        Log.d(this.TAG, "@@Test mLayoutParams.width(origin):" + this.mLayoutParams.width);
        int newWidth = (int) ((((this.mLayoutParams.width - 20) / 0.75f) * PANEL_SCALING) + 20.0f);
        this.mLayoutParams.leftMargin = (int) (r1.leftMargin - ((newWidth - this.mLayoutParams.width) / 2.0f));
        this.mLayoutParams.width = newWidth;
        Log.d(this.TAG, "@@TestnewWidth:" + newWidth);
        Log.d(this.TAG, "@@Test mLayoutParams.leftMargin:" + this.mLayoutParams.leftMargin);
        Log.d(this.TAG, "@@Test mLayoutParams.width:" + this.mLayoutParams.width);
        this.mFrameLayout.setLayoutParams(this.mLayoutParams);
        this.mDarkImageLayoutParams.width = this.mLayoutParams.width - 20;
        this.mDarkImageLayoutParams.height = this.mLayoutParams.height - 26;
        this.mDarkImageLayoutParams.setMargins(8, 11, 0, 0);
        this.mDarkImageLayoutParams.gravity = 0;
        this.mDarkImage.setLayoutParams(this.mDarkImageLayoutParams);
        this.mBlackBoard.setLayoutParams(this.mDarkImageLayoutParams);
        Log.e("INSIDE", "refreshImageCoords");
        this.mMinFrameWidth = (this.mDarkImageLayoutParams.width / 4) + 20;
        this.mMinFrameHeight = (this.mDarkImageLayoutParams.height / 4) + 26;
        this.mLeftBoundary = 0;
        this.mTopBoundary = 0;
        this.mRightBoundary = this.mLayoutParams.width;
        this.mBottomBoundary = this.mLayoutParams.height;
        this.mAspectRatio = this.mDarkImageLayoutParams.width / this.mDarkImageLayoutParams.height;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (isFinder()) {
            return -1;
        }
        int customhandled = 0;
        long keyDownTime = event.getDownTime();
        int keyCode2 = event.getScanCode();
        if (this.mOnce) {
            refreshFrameCoords();
            this.mOnce = false;
        }
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.UP /* 103 */:
                refreshFrameCoords();
                if (this.isKeyDown) {
                    this.mFrameTop -= 8.0f;
                } else {
                    this.mFrameTop -= 2.0f;
                }
                this.mFrameBottom = this.mFrameTop + this.mFrameHeight;
                if (this.mFrameTop < this.mTopBoundary) {
                    this.mFrameTop = this.mTopBoundary;
                    this.mFrameBottom = this.mFrameTop + this.mFrameHeight;
                }
                updateImageLayout();
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                refreshFrameCoords();
                if (this.isKeyDown) {
                    this.mFrameLeft -= 8.0f;
                } else {
                    this.mFrameLeft -= 2.0f;
                }
                this.mFrameRight = this.mFrameLeft + this.mFrameWidth;
                if (this.mFrameLeft < this.mLeftBoundary) {
                    this.mFrameLeft = this.mLeftBoundary;
                    this.mFrameRight = this.mFrameLeft + this.mFrameWidth;
                }
                updateImageLayout();
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                refreshFrameCoords();
                if (this.isKeyDown) {
                    this.mFrameRight += 8.0f;
                } else {
                    this.mFrameRight += 2.0f;
                }
                this.mFrameLeft = this.mFrameRight - this.mFrameWidth;
                if (this.mFrameRight > this.mRightBoundary) {
                    this.mFrameRight = this.mRightBoundary;
                    this.mFrameLeft = this.mFrameRight - this.mFrameWidth;
                }
                updateImageLayout();
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                refreshFrameCoords();
                if (this.isKeyDown) {
                    this.mFrameBottom += 8.0f;
                } else {
                    this.mFrameBottom += 2.0f;
                }
                this.mFrameTop = this.mFrameBottom - this.mFrameHeight;
                if (this.mFrameBottom > this.mBottomBoundary) {
                    this.mFrameBottom = this.mBottomBoundary;
                    this.mFrameTop = this.mFrameBottom - this.mFrameHeight;
                }
                updateImageLayout();
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                FrameInfo frameInfo = getFrameInfoBundle();
                editImage(frameInfo);
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                saveImage(frameInfo);
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                rotationOfFrame();
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                refreshFrameCoords();
                int space = (int) (this.mBottomBoundary - this.mFrameBottom);
                Log.d("SPACE", "" + space);
                if (space >= 3) {
                    if (mKeyDownTime + 400 > keyDownTime) {
                        this.mFrameHeight += 16;
                    } else {
                        this.mFrameHeight += 3;
                    }
                } else if (space == 2) {
                    Log.d("space = ", "2");
                    this.mFrameHeight += 2;
                } else if (space == 1) {
                    Log.d("space = ", "1");
                    this.mFrameHeight++;
                }
                if (this.mFrameHeight > ((int) (this.mBottomBoundary - this.mFrameTop))) {
                    this.mFrameHeight = (int) (this.mBottomBoundary - this.mFrameTop);
                }
                this.mFrameWidth = ((int) ((this.mFrameHeight - 26) * this.mAspectRatio)) + 20;
                if (this.mFrameWidth > ((int) (this.mRightBoundary - this.mFrameLeft))) {
                    this.mFrameWidth = (int) (this.mRightBoundary - this.mFrameLeft);
                    this.mFrameHeight = ((int) ((this.mFrameWidth - 20) / this.mAspectRatio)) + 26;
                }
                updateImageLayout();
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                refreshFrameCoords();
                if (mKeyDownTime + 400 > keyDownTime) {
                    this.mFrameHeight -= 16;
                } else {
                    this.mFrameHeight -= 3;
                }
                if (this.mFrameHeight < this.mMinFrameHeight) {
                    Log.d("Height", "getting less");
                    this.mFrameHeight = this.mMinFrameHeight;
                }
                this.mFrameWidth = ((int) ((this.mFrameHeight - 26) * this.mAspectRatio)) + 20;
                updateImageLayout();
                customhandled = 1;
                break;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
        }
        this.isKeyDown = true;
        mKeyDownTime = keyDownTime;
        return customhandled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateImageLayout() {
        this.mParams.height = this.mFrameHeight;
        this.mParams.width = this.mFrameWidth;
        this.mFrameRight = this.mFrameLeft + this.mFrameWidth;
        this.mFrameBottom = this.mFrameTop + this.mFrameHeight;
        this.mParams.setMargins((int) this.mFrameLeft, (int) this.mFrameTop, 0, 0);
        this.mParams.gravity = 0;
        this.mFrame.setLayoutParams(this.mParams);
        this.mOneImage.setImageBitmap(giveNewBitmap(this.mOrigBitmap));
        moveRgb();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        this.isKeyDown = false;
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            default:
                return 0;
        }
    }

    private void rotationOfFrame() {
        Log.d("TAG", "/////////////inside rotation//////////////");
        refreshFrameCoords();
        float rotatedWidth = ((this.mFrameHeight - 26) * PANEL_SCALING) + 20.0f;
        float rotatedHeiht = ((this.mFrameWidth - 20) / PANEL_SCALING) + 26.0f;
        if (rotatedWidth > this.mRightBoundary) {
            float ratio = rotatedHeiht / rotatedWidth;
            rotatedWidth = this.mRightBoundary;
            rotatedHeiht = rotatedWidth * ratio;
        }
        if (rotatedHeiht > this.mBottomBoundary) {
            float ratio2 = rotatedWidth / rotatedHeiht;
            rotatedHeiht = this.mBottomBoundary;
            rotatedWidth = rotatedHeiht * ratio2;
        }
        if (this.mFrameLeft + rotatedWidth > this.mRightBoundary) {
            this.mFrameLeft = this.mRightBoundary - rotatedWidth;
        }
        if (this.mFrameTop + rotatedHeiht > this.mBottomBoundary) {
            this.mFrameTop = this.mBottomBoundary - rotatedHeiht;
        }
        this.mFrameWidth = (int) rotatedWidth;
        this.mFrameHeight = (int) rotatedHeiht;
        this.mFrameRight = this.mFrameLeft + this.mFrameWidth;
        this.mFrameBottom = this.mFrameTop + this.mFrameHeight;
        updateImageLayout();
        Log.d("after rotation", "" + this.mFrameLeft + ExposureModeController.SOFT_SNAP + this.mFrameTop + ExposureModeController.SOFT_SNAP + this.mFrameRight + ExposureModeController.SOFT_SNAP + this.mFrameBottom);
        this.mAspectRatio /= PANEL_SCALING;
        this.mAspectRatio = 1.0d / this.mAspectRatio;
        this.mAspectRatio *= PANEL_SCALING;
        int temp2 = this.mMinFrameWidth;
        this.mMinFrameWidth = this.mMinFrameHeight;
        this.mMinFrameHeight = temp2;
    }

    public Bitmap giveNewBitmap(Bitmap bmp) {
        Log.e("INSIDE", "giveNewBitmap");
        Log.e("FRAME 1", "" + this.mFrameWidth + ExposureModeController.SOFT_SNAP + this.mFrameHeight + ExposureModeController.SOFT_SNAP + this.mFrameLeft + ExposureModeController.SOFT_SNAP + this.mFrameTop);
        if (this.mFrameWidth == 0 && this.mFrameHeight == 0) {
            Log.e("INSIDE", "INITIAL STAGE");
            if (this.mPhotoRetouchSubMenuLayout.mRect != null) {
                Log.d("RECT", "" + this.mPhotoRetouchSubMenuLayout.mRect);
                Rect myRect = this.mPhotoRetouchSubMenuLayout.mRect;
                if (ImageEditor.getAspectRatio() == 1) {
                    myRect.left = (int) (myRect.left * 1.1764706f);
                    myRect.right = (int) (myRect.right * 1.1764706f);
                    myRect.top = (int) (myRect.top * 1.1688311f);
                    myRect.bottom = (int) (myRect.bottom * 1.1688311f);
                }
                Rect newRect = new Rect();
                Log.v("RECTPOS 1", "" + myRect.width() + ExposureModeController.SOFT_SNAP + myRect.height() + ExposureModeController.SOFT_SNAP + myRect.left + ExposureModeController.SOFT_SNAP + myRect.top);
                if (1 == this.mRotateInfo) {
                    newRect.left = myRect.left;
                    newRect.right = myRect.right;
                    newRect.top = myRect.top;
                    newRect.bottom = myRect.bottom;
                } else if (6 == this.mRotateInfo) {
                    Log.d("", "ROTATE 90");
                    newRect.left = ((int) (this.mScaledImage.getWidth() / PANEL_SCALING)) - myRect.bottom;
                    newRect.right = ((int) (this.mScaledImage.getWidth() / PANEL_SCALING)) - myRect.top;
                    newRect.top = myRect.left;
                    newRect.bottom = myRect.right;
                } else if (8 == this.mRotateInfo) {
                    newRect.left = myRect.top;
                    newRect.right = myRect.bottom;
                    newRect.top = this.mScaledImage.getHeight() - myRect.right;
                    newRect.bottom = this.mScaledImage.getHeight() - myRect.left;
                } else if (3 == this.mRotateInfo) {
                    newRect.left = ((int) (this.mScaledImage.getWidth() / PANEL_SCALING)) - myRect.right;
                    newRect.right = ((int) (this.mScaledImage.getWidth() / PANEL_SCALING)) - myRect.left;
                    newRect.top = this.mScaledImage.getHeight() - myRect.bottom;
                    newRect.bottom = this.mScaledImage.getHeight() - myRect.top;
                }
                float scaleH = (this.mLayoutParams.height - 26) / this.mScaledImage.getHeight();
                float scaleW = ((this.mLayoutParams.width - 20) / this.mScaledImage.getWidth()) * PANEL_SCALING;
                this.mFrameLeft = (int) (newRect.left * scaleW);
                this.mFrameTop = (int) (newRect.top * scaleH);
                int right = (int) (newRect.right * scaleW);
                int bottom = (int) (newRect.bottom * scaleH);
                int width = right - ((int) this.mFrameLeft);
                int height = bottom - ((int) this.mFrameTop);
                this.mFrameWidth = width + 20;
                this.mFrameHeight = height + 26;
                this.mAspectRatio = width / height;
                Log.d("right bottom", "" + this.mFrameRight + ExposureModeController.SOFT_SNAP + this.mFrameBottom);
                Log.v("RECTPOS 3", "" + this.mFrameWidth + ExposureModeController.SOFT_SNAP + this.mFrameHeight + ExposureModeController.SOFT_SNAP + this.mFrameLeft + ExposureModeController.SOFT_SNAP + this.mFrameTop);
                this.mParams.width = this.mFrameWidth;
                this.mParams.height = this.mFrameHeight;
                this.mParams.setMargins((int) this.mFrameLeft, (int) this.mFrameTop, 0, 0);
                this.mParams.gravity = 0;
                this.mFrame.setLayoutParams(this.mParams);
                moveRgb();
            } else {
                Log.d("RECT", "null");
                int width2 = this.mLayoutParams.width;
                int height2 = this.mLayoutParams.height;
                this.mFrameWidth = ((width2 - 20) / 2) + 20;
                this.mFrameHeight = ((height2 - 26) / 2) + 26;
                this.mFrameLeft = (width2 - 20) / 4;
                this.mFrameTop = (height2 - 26) / 4;
                Log.d("Rect AP 2", "" + (this.mFrameWidth / this.mFrameHeight));
                this.mParams.width = this.mFrameWidth;
                this.mParams.height = this.mFrameHeight;
                this.mParams.setMargins((int) this.mFrameLeft, (int) this.mFrameTop, 0, 0);
                this.mParams.gravity = 0;
                this.mFrame.setLayoutParams(this.mParams);
                moveRgb();
            }
        }
        Log.d("FRAME 2", "" + this.mFrameWidth + ExposureModeController.SOFT_SNAP + this.mFrameHeight + ExposureModeController.SOFT_SNAP + this.mFrameLeft + ExposureModeController.SOFT_SNAP + this.mFrameTop);
        double widthScale = this.mOrigBitmap.getWidth() / this.mDarkImageLayoutParams.width;
        double heightScale = this.mOrigBitmap.getHeight() / this.mDarkImageLayoutParams.height;
        Log.v("SCALE", "" + widthScale + ExposureModeController.SOFT_SNAP + heightScale);
        int width3 = (int) ((this.mFrameWidth - 20.0d) * widthScale);
        int height3 = (int) ((this.mFrameHeight - 26.0d) * heightScale);
        int left = (int) (this.mFrameLeft * widthScale);
        int top = (int) (this.mFrameTop * heightScale);
        Log.v("NEW DIMENSIONS", "" + widthScale + ExposureModeController.SOFT_SNAP + heightScale);
        Log.d("orig Bitmap", "" + bmp.getWidth() + ExposureModeController.SOFT_SNAP + bmp.getHeight());
        if (left + width3 > this.mOrigBitmap.getWidth()) {
            width3 = this.mOrigBitmap.getWidth() - left;
        }
        if (top + height3 > this.mOrigBitmap.getHeight()) {
            height3 = this.mOrigBitmap.getHeight() - top;
        }
        Bitmap cropBitmap = Bitmap.createBitmap(bmp, left, top, width3, height3);
        double newWidthScale = 1.0d / widthScale;
        double newHeightScale = 1.0d / heightScale;
        int newScaledWidth = (int) (cropBitmap.getWidth() * newWidthScale);
        int newScaledHeight = (int) (cropBitmap.getHeight() * newHeightScale);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropBitmap, newScaledWidth, newScaledHeight, false);
        Log.d("BITMAP dimensions 2", "width = " + scaledBitmap.getWidth() + ExposureModeController.SOFT_SNAP + "height = " + scaledBitmap.getHeight());
        cropBitmap.recycle();
        return scaledBitmap;
    }

    public void moveRgb() {
        Log.e("INSIDE", "moveRgb");
        Log.e("FRAME WIDTH HEIGHT 2", "" + this.mFrameWidth + ExposureModeController.SOFT_SNAP + this.mFrameHeight);
        this.mParamsNew.width = -2;
        this.mParamsNew.height = -2;
        this.mParamsNew.setMargins(((int) this.mFrameLeft) + 8, ((int) this.mFrameTop) + 11, 0, 0);
        this.mParamsNew.gravity = 0;
        this.mOneImage.setLayoutParams(this.mParamsNew);
    }

    private int getFrameOrientation() {
        return this.mFrameWidth > this.mFrameHeight ? 0 : 1;
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

    private FrameInfo getFrameInfoBundle() {
        int left;
        int top;
        int cropImageHeight;
        int cropImageWidth;
        int newOrientation;
        RotateImageFilter.ROTATION_DEGREE rotateDegree;
        float scaleOrig;
        refreshFrameCoords();
        RotateImageFilter.ROTATION_DEGREE rotation_degree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
        Rect rect = new Rect();
        int cWidth = this.mFrameWidth - 20;
        int cHeight = this.mFrameHeight - 26;
        int cLeft = (int) this.mFrameLeft;
        int cTop = (int) this.mFrameTop;
        if (1 == this.mRotateInfo) {
            left = (int) (cLeft / PANEL_SCALING);
            top = cTop;
            cropImageHeight = cHeight;
            cropImageWidth = (int) (cWidth / PANEL_SCALING);
            if (getFrameOrientation() == 0) {
                newOrientation = 1;
                rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
            } else {
                newOrientation = 6;
                rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_270;
            }
            scaleOrig = this.mOriginalImage.getHeight() / this.mDarkImageLayoutParams.height;
        } else if (3 == this.mRotateInfo) {
            left = (int) (((this.mDarkImageLayoutParams.width - cLeft) - cWidth) / PANEL_SCALING);
            top = (this.mDarkImageLayoutParams.height - cTop) - cHeight;
            cropImageHeight = cHeight;
            cropImageWidth = (int) (cWidth / PANEL_SCALING);
            if (getFrameOrientation() == 0) {
                newOrientation = 3;
                rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
            } else {
                newOrientation = 8;
                rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_270;
            }
            scaleOrig = this.mOriginalImage.getHeight() / this.mDarkImageLayoutParams.height;
        } else if (6 == this.mRotateInfo) {
            left = cTop;
            top = (int) ((this.mDarkImageLayoutParams.width - (cLeft + cWidth)) / PANEL_SCALING);
            cropImageHeight = (int) (cWidth / PANEL_SCALING);
            cropImageWidth = cHeight;
            if (getFrameOrientation() == 0) {
                newOrientation = 1;
                rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_90;
            } else {
                newOrientation = 6;
                rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_0;
            }
            scaleOrig = this.mOriginalImage.getWidth() / this.mDarkImageLayoutParams.height;
        } else {
            left = this.mDarkImageLayoutParams.height - (cTop + cHeight);
            top = (int) (cLeft / PANEL_SCALING);
            cropImageHeight = (int) (cWidth / PANEL_SCALING);
            cropImageWidth = cHeight;
            if (getFrameOrientation() == 0) {
                newOrientation = 1;
                rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_270;
            } else {
                newOrientation = 6;
                rotateDegree = RotateImageFilter.ROTATION_DEGREE.DEGREE_180;
            }
            scaleOrig = this.mOriginalImage.getWidth() / this.mDarkImageLayoutParams.height;
        }
        rect.left = (int) (left * scaleOrig);
        rect.top = (int) (top * scaleOrig);
        int cropImageWidth2 = ((int) (cropImageWidth * scaleOrig)) & (-4);
        int cropImageHeight2 = ((int) (cropImageHeight * scaleOrig)) & (-2);
        if (rect.left < 0) {
            rect.left = 0;
        }
        if (rect.top < 0) {
            rect.top = 0;
        }
        rect.right = rect.left + cropImageWidth2;
        rect.bottom = rect.top + cropImageHeight2;
        if (rect.right > this.mOriginalImage.getWidth()) {
            rect.right = this.mOriginalImage.getWidth();
            rect.left = this.mOriginalImage.getWidth() - cropImageWidth2;
            if (rect.left < 0) {
                rect.left = 0;
            }
        }
        if (rect.bottom > this.mOriginalImage.getHeight()) {
            rect.bottom = this.mOriginalImage.getHeight();
            rect.top = this.mOriginalImage.getHeight() - cropImageHeight2;
            if (rect.top < 0) {
                rect.top = 0;
            }
        }
        return new FrameInfo(rotateDegree, newOrientation, rect);
    }

    private void editImage(FrameInfo frameInfo) {
        ImageEditor.releaseOptImage(this.mScaledImage);
        Log.d(this.TAG, "==== editImage:optImageCount = " + ImageEditor.optImageCount);
        this.mEditImage = ImageEditor.getEditFrame(ImageEditor.getImage(), frameInfo.cropCoordinates, frameInfo.rotateDegree);
    }

    private void saveImage(FrameInfo frameInfo) {
        ManualSavingTask task = new ManualSavingTask(frameInfo, this.mEditImage);
        ImageEditor.executeResultReflection(task, this.mEditImage);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        ImageEditor.releaseOptImage(this.mScaledImage);
        this.yuvToRgb.releaseYuvToRgbResources();
        this.yuvToRgb = null;
        ImageEditor.releaseYUVDSP();
        if (this.mOrigBitmap != null) {
            this.mOrigBitmap.recycle();
            this.mOrigBitmap = null;
        }
        deInitialize();
        Log.d(this.TAG, "==== onDestroyView:optImageCount = " + ImageEditor.optImageCount);
        super.onDestroyView();
    }

    private void reInitialize() {
        if (this.yuvToRgb == null) {
            this.yuvToRgb = new YuvToRgbConversion(ImageEditor.useYuvToRGBDsp());
        }
        if (this.mOrigBitmap != null) {
            this.mOrigBitmap.recycle();
            this.mOrigBitmap = null;
        }
    }

    private void deInitialize() {
        this.mPhotoRetouchSubMenuLayout.deAllocateImageView(this.mFrame);
        this.mFrame = null;
        this.mPhotoRetouchSubMenuLayout.deAllocateImageView(this.mOneImage);
        this.mOneImage = null;
        this.mPhotoRetouchSubMenuLayout.deAllocateImageView(this.mDarkImage);
        this.mDarkImage = null;
        this.mBlackBoard = null;
        this.mFrameWidth = 0;
        this.mFrameHeight = 0;
        this.mFrameLeft = PANEL_SCALING;
        this.mFrameBottom = PANEL_SCALING;
        this.mFrameRight = PANEL_SCALING;
        this.mFrameTop = PANEL_SCALING;
        this.mFrameLeft = PANEL_SCALING;
        this.mInitialY = PANEL_SCALING;
        this.mInitialX = PANEL_SCALING;
        this.mTappedCorner = 'E';
        this.mTouchedInside = false;
        this.mIsCornerTouched = false;
        this.mMinFrameHeight = 0;
        this.mMinFrameWidth = 0;
        this.mParamsNew = null;
        this.mParams = null;
        this.mOnce = true;
        this.mAspectRatio = 0.0d;
        this.mLayoutParams = null;
        this.mFrameLayout = null;
        this.mScaledImage = null;
        this.mOrigBitmap = null;
        this.mRotateInfo = 0;
        this.isKeyDown = false;
        mKeyDownTime = 0L;
        this.yuvToRgb = null;
        this.mFrameRight = PANEL_SCALING;
        this.mFrameTop = PANEL_SCALING;
        this.mFrameBottom = PANEL_SCALING;
        this.mAspectRatio = 0.0d;
        this.mCurrentView = null;
    }
}
