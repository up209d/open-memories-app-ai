package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.playback.PortraitBeautyAdjustEffectState;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.Oscillator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class BarExposureView extends ActiveLayout {
    private static final int BAR_MAX_VALUE_2 = 6;
    private static final int BAR_MAX_VALUE_3 = 9;
    private static final int BAR_MAX_VALUE_5_BY_3 = 15;
    private static final int BAR_MAX_VALUE_M = 6;
    private static final int BAR_MIN_VALUE_M = -6;
    private static final int BRACKET_CONT_0_3_EV = 1;
    private static final int BRACKET_CONT_0_7_EV = 2;
    private static final int BRACKET_CONT_1_0_EV = 3;
    private static final int BRACKET_CONT_2_0_EV = 6;
    private static final int BRACKET_CONT_3_0_EV = 9;
    private static final int BRACKET_CONT_NONE = 0;
    private static final int EDGE_TRIANGLE_BOTH_BLINK = 2;
    private static final int EDGE_TRIANGLE_BOTH_INVISIBLE = 0;
    private static final int EDGE_TRIANGLE_BOTH_VISIBLE = 1;
    private static final int EDGE_TRIANGLE_LEFT_BLINK = 6;
    private static final int EDGE_TRIANGLE_LEFT_VISIBLE = 5;
    private static final int EDGE_TRIANGLE_RIGHT_BLINK = 4;
    private static final int EDGE_TRIANGLE_RIGHT_VISIBLE = 3;
    private static final int MAX_BRACKET_SHOOT_NUM = 5;
    private static final int MODE_EXPOSURE = 0;
    private static final int MODE_MANUAL = 1;
    private static final int MODE_OTHER = 2;
    private static final int ORANGE_TIME_OUT = 2000;
    private static final String TRIANGLE_LEFT = "triangleLeft";
    private static final String TRIANGLE_RIGHT = "triangleRight";
    private static final int VALUE_EVF_BAR_POSX_15 = 233;
    private static final int VALUE_EVF_BAR_POSX_6 = 257;
    private static final int VALUE_EVF_BAR_POSX_9 = 233;
    private static final int VALUE_EVF_BAR_WIDTH_15 = 218;
    private static final int VALUE_EVF_BAR_WIDTH_6 = 171;
    private static final int VALUE_EVF_BAR_WIDTH_9 = 218;
    private static final HashMap<Integer, Integer> VALUE_EVF_POSX_15_BY_2;
    private static final HashMap<Integer, Integer> VALUE_EVF_POSX_15_BY_3;
    private static final HashMap<Integer, Integer> VALUE_EVF_POSX_6;
    private static final HashMap<Integer, Integer> VALUE_EVF_POSX_9 = new HashMap<>();
    private static final int VALUE_EVF_POSY = 0;
    private static final int VALUE_EVF_UPPER_TRIANGLE_HEIGHT = 8;
    private static final int VALUE_EVF_UPPER_TRIANGLE_WIDTH = 8;
    private HashMap<String, ImageView> blinkIconHashMap;
    private Handler handlerOrangeTimeout;
    private boolean isOnFinishInflate;
    private int lastIndexValue;
    private int lastMode;
    private ImageView mBarCenter;
    private ImageView mBarLeft;
    private ImageView mBarRight;
    private int mBracketShootNum;
    private Context mContext;
    private int mIndexValue;
    private boolean mIsOrange;
    private int mMode;
    private Oscillator.OnPeriodListener mTimerListener;
    private ImageView mTriangleLeft;
    private ImageView mTriangleRight;
    private ImageView[] mTriangleUpper;
    private TypedArray mTypedArray;
    private int maxIndexValue;
    private int minIndexValue;
    private HashMap<Integer, Integer> valueEvfPosX;
    private int visibleTriangleNum;

    static {
        VALUE_EVF_POSX_9.put(-9, 18);
        VALUE_EVF_POSX_9.put(-8, 29);
        VALUE_EVF_POSX_9.put(-7, 39);
        VALUE_EVF_POSX_9.put(Integer.valueOf(BAR_MIN_VALUE_M), 48);
        VALUE_EVF_POSX_9.put(-5, 59);
        VALUE_EVF_POSX_9.put(-4, 69);
        VALUE_EVF_POSX_9.put(-3, 77);
        VALUE_EVF_POSX_9.put(-2, 89);
        VALUE_EVF_POSX_9.put(-1, 99);
        VALUE_EVF_POSX_9.put(0, Integer.valueOf(PortraitBeautyAdjustEffectState.TRANSIT_TO_SHOOTING_FROM_ADJUSTEFFECT));
        VALUE_EVF_POSX_9.put(1, 118);
        VALUE_EVF_POSX_9.put(2, 128);
        VALUE_EVF_POSX_9.put(3, 137);
        VALUE_EVF_POSX_9.put(4, 148);
        VALUE_EVF_POSX_9.put(5, 158);
        VALUE_EVF_POSX_9.put(6, 169);
        VALUE_EVF_POSX_9.put(7, 178);
        VALUE_EVF_POSX_9.put(8, 188);
        VALUE_EVF_POSX_9.put(9, 198);
        VALUE_EVF_POSX_15_BY_3 = new HashMap<>();
        VALUE_EVF_POSX_15_BY_3.put(-15, 15);
        VALUE_EVF_POSX_15_BY_3.put(-14, 22);
        VALUE_EVF_POSX_15_BY_3.put(-13, 28);
        VALUE_EVF_POSX_15_BY_3.put(-12, 34);
        VALUE_EVF_POSX_15_BY_3.put(-11, 41);
        VALUE_EVF_POSX_15_BY_3.put(-10, 47);
        VALUE_EVF_POSX_15_BY_3.put(-9, 53);
        VALUE_EVF_POSX_15_BY_3.put(-8, 59);
        VALUE_EVF_POSX_15_BY_3.put(-7, 65);
        VALUE_EVF_POSX_15_BY_3.put(Integer.valueOf(BAR_MIN_VALUE_M), 71);
        VALUE_EVF_POSX_15_BY_3.put(-5, 78);
        VALUE_EVF_POSX_15_BY_3.put(-4, 84);
        VALUE_EVF_POSX_15_BY_3.put(-3, 90);
        VALUE_EVF_POSX_15_BY_3.put(-2, 97);
        VALUE_EVF_POSX_15_BY_3.put(-1, 103);
        VALUE_EVF_POSX_15_BY_3.put(0, Integer.valueOf(PortraitBeautyAdjustEffectState.TRANSIT_TO_SHOOTING_FROM_ADJUSTEFFECT));
        VALUE_EVF_POSX_15_BY_3.put(1, 115);
        VALUE_EVF_POSX_15_BY_3.put(2, 121);
        VALUE_EVF_POSX_15_BY_3.put(3, 127);
        VALUE_EVF_POSX_15_BY_3.put(4, 134);
        VALUE_EVF_POSX_15_BY_3.put(5, 140);
        VALUE_EVF_POSX_15_BY_3.put(6, 147);
        VALUE_EVF_POSX_15_BY_3.put(7, 153);
        VALUE_EVF_POSX_15_BY_3.put(8, 159);
        VALUE_EVF_POSX_15_BY_3.put(9, 166);
        VALUE_EVF_POSX_15_BY_3.put(10, 172);
        VALUE_EVF_POSX_15_BY_3.put(11, 178);
        VALUE_EVF_POSX_15_BY_3.put(12, 185);
        VALUE_EVF_POSX_15_BY_3.put(13, 191);
        VALUE_EVF_POSX_15_BY_3.put(14, 197);
        VALUE_EVF_POSX_15_BY_3.put(15, 203);
        VALUE_EVF_POSX_15_BY_2 = new HashMap<>();
        VALUE_EVF_POSX_15_BY_2.put(-10, 15);
        VALUE_EVF_POSX_15_BY_2.put(-9, 24);
        VALUE_EVF_POSX_15_BY_2.put(-8, 34);
        VALUE_EVF_POSX_15_BY_2.put(-7, 43);
        VALUE_EVF_POSX_15_BY_2.put(Integer.valueOf(BAR_MIN_VALUE_M), 52);
        VALUE_EVF_POSX_15_BY_2.put(-5, 61);
        VALUE_EVF_POSX_15_BY_2.put(-4, 70);
        VALUE_EVF_POSX_15_BY_2.put(-3, 80);
        VALUE_EVF_POSX_15_BY_2.put(-2, 90);
        VALUE_EVF_POSX_15_BY_2.put(-1, 99);
        VALUE_EVF_POSX_15_BY_2.put(0, Integer.valueOf(AppRoot.USER_KEYCODE.DOWN));
        VALUE_EVF_POSX_15_BY_2.put(1, 118);
        VALUE_EVF_POSX_15_BY_2.put(2, 127);
        VALUE_EVF_POSX_15_BY_2.put(3, 137);
        VALUE_EVF_POSX_15_BY_2.put(4, 146);
        VALUE_EVF_POSX_15_BY_2.put(5, 156);
        VALUE_EVF_POSX_15_BY_2.put(6, 165);
        VALUE_EVF_POSX_15_BY_2.put(7, 175);
        VALUE_EVF_POSX_15_BY_2.put(8, 185);
        VALUE_EVF_POSX_15_BY_2.put(9, 194);
        VALUE_EVF_POSX_15_BY_2.put(10, 203);
        VALUE_EVF_POSX_6 = new HashMap<>();
        VALUE_EVF_POSX_6.put(Integer.valueOf(BAR_MIN_VALUE_M), 19);
        VALUE_EVF_POSX_6.put(-5, 31);
        VALUE_EVF_POSX_6.put(-4, 42);
        VALUE_EVF_POSX_6.put(-3, 51);
        VALUE_EVF_POSX_6.put(-2, 64);
        VALUE_EVF_POSX_6.put(-1, 75);
        VALUE_EVF_POSX_6.put(0, 85);
        VALUE_EVF_POSX_6.put(1, 96);
        VALUE_EVF_POSX_6.put(2, 107);
        VALUE_EVF_POSX_6.put(3, 117);
        VALUE_EVF_POSX_6.put(4, 129);
        VALUE_EVF_POSX_6.put(5, 140);
        VALUE_EVF_POSX_6.put(6, 152);
    }

    public BarExposureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBracketShootNum = 3;
        this.mMode = 0;
        this.mIndexValue = 0;
        this.mIsOrange = false;
        this.visibleTriangleNum = 0;
        this.lastIndexValue = 0;
        this.lastMode = 2;
        this.maxIndexValue = 0;
        this.minIndexValue = 0;
        this.valueEvfPosX = null;
        this.blinkIconHashMap = null;
        this.isOnFinishInflate = false;
        this.mTriangleUpper = null;
        this.mTriangleRight = null;
        this.mTriangleLeft = null;
        this.mBarLeft = null;
        this.mBarCenter = null;
        this.mBarRight = null;
        this.handlerOrangeTimeout = new Handler() { // from class: com.sony.imaging.app.base.shooting.widget.BarExposureView.2
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                BarExposureView.this.setIsOrange(false);
                BarExposureView.this.refreshTriangleIcons();
            }
        };
        this.mContext = context;
        this.blinkIconHashMap = new HashMap<>();
        this.mTimerListener = new Oscillator.OnPeriodListener() { // from class: com.sony.imaging.app.base.shooting.widget.BarExposureView.1
            @Override // com.sony.imaging.app.util.Oscillator.OnPeriodListener
            public void onPeriod(int Hz, boolean highlow) {
                if (8 == Hz) {
                    int visibility = highlow ? 0 : 4;
                    if (BarExposureView.this.blinkIconHashMap.size() != 0) {
                        ImageView iv_right = (ImageView) BarExposureView.this.blinkIconHashMap.get(BarExposureView.TRIANGLE_RIGHT);
                        ImageView iv_left = (ImageView) BarExposureView.this.blinkIconHashMap.get(BarExposureView.TRIANGLE_LEFT);
                        if (iv_right != null) {
                            iv_right.setVisibility(visibility);
                        }
                        if (iv_left != null) {
                            iv_left.setVisibility(visibility);
                        }
                    }
                }
            }
        };
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(-2, -2);
        setLayoutParams(layout);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.BarExposureView);
    }

    private void setImageToTriangle(ImageView triangleLeft, ImageView triangleRight, ImageView[] triangleUpper) {
        boolean isOrange = getIsOrange();
        if (isOrange) {
            triangleRight.setImageDrawable(this.mTypedArray.getDrawable(17));
            triangleLeft.setImageDrawable(this.mTypedArray.getDrawable(16));
            for (int i = 0; i < 5; i++) {
                triangleUpper[i].setImageDrawable(this.mTypedArray.getDrawable(18));
            }
            return;
        }
        triangleRight.setImageDrawable(this.mTypedArray.getDrawable(14));
        triangleLeft.setImageDrawable(this.mTypedArray.getDrawable(13));
        for (int i2 = 0; i2 < 5; i2++) {
            triangleUpper[i2].setImageDrawable(this.mTypedArray.getDrawable(15));
        }
    }

    void setValue(float value) {
    }

    void setMode(int mode) {
        setBlink(false, null, null);
        Oscillator.detach(8, this.mTimerListener);
        if (this.mTriangleLeft != null) {
            this.mTriangleLeft.setVisibility(8);
        }
        if (this.mTriangleRight != null) {
            this.mTriangleRight.setVisibility(8);
        }
        this.mMode = mode;
        if (this.mMode == 0) {
            this.mListener.onNotify("ExposureCompensation");
            return;
        }
        if (this.mMode == 1) {
            this.mListener.onNotify(CameraNotificationManager.METERED_MANUAL);
        } else if (this.isOnFinishInflate) {
            int exposureIndexValue = ExposureCompensationController.getInstance().getExposureCompensationIndex();
            setIndexValue(exposureIndexValue);
            refreshBarExposureView();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected void refresh() {
        getExposureMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshBarExposureView() {
        refreshBarType(this.mMode, this.mBarLeft, this.mBarCenter, this.mBarRight);
        refreshTriangleIcons();
    }

    private void refreshBarType(int mode, ImageView barLeft, ImageView barCenter, ImageView barRight) {
        checkExposureMaxValue();
        ViewGroup.MarginLayoutParams layout = (ViewGroup.MarginLayoutParams) getLayoutParams();
        if (this.maxIndexValue > 9) {
            if (mode == 1) {
                barLeft.setImageDrawable(this.mTypedArray.getDrawable(5));
                barCenter.setImageDrawable(this.mTypedArray.getDrawable(1));
                barRight.setImageDrawable(this.mTypedArray.getDrawable(10));
            } else {
                boolean isEvDialUse = false;
                if (CameraSetting.getPfApiVersion() >= 2) {
                    boolean hasEVDial = EVDialDetector.hasEVDial();
                    if (hasEVDial) {
                        int ev = EVDialDetector.getEVDialPosition();
                        if (ev != 0) {
                            isEvDialUse = true;
                        }
                    }
                }
                if (!isMovieMode()) {
                    if (!isEvDialUse) {
                        barLeft.setImageDrawable(this.mTypedArray.getDrawable(3));
                        barCenter.setImageDrawable(this.mTypedArray.getDrawable(1));
                        barRight.setImageDrawable(this.mTypedArray.getDrawable(8));
                    } else {
                        barLeft.setImageDrawable(this.mTypedArray.getDrawable(6));
                        barCenter.setImageDrawable(this.mTypedArray.getDrawable(1));
                        barRight.setImageDrawable(this.mTypedArray.getDrawable(11));
                    }
                } else {
                    barLeft.setImageDrawable(this.mTypedArray.getDrawable(5));
                    barCenter.setImageDrawable(this.mTypedArray.getDrawable(1));
                    barRight.setImageDrawable(this.mTypedArray.getDrawable(10));
                }
            }
            layout.leftMargin = 233;
            layout.width = 218;
        } else if (this.maxIndexValue == 9) {
            if (mode == 1) {
                barLeft.setImageDrawable(this.mTypedArray.getDrawable(4));
                barCenter.setImageDrawable(this.mTypedArray.getDrawable(0));
                barRight.setImageDrawable(this.mTypedArray.getDrawable(9));
            } else if (!isMovieMode()) {
                barLeft.setImageDrawable(this.mTypedArray.getDrawable(2));
                barCenter.setImageDrawable(this.mTypedArray.getDrawable(0));
                barRight.setImageDrawable(this.mTypedArray.getDrawable(7));
            } else {
                barLeft.setImageDrawable(this.mTypedArray.getDrawable(4));
                barCenter.setImageDrawable(this.mTypedArray.getDrawable(0));
                barRight.setImageDrawable(this.mTypedArray.getDrawable(9));
            }
            layout.leftMargin = 233;
            layout.width = 218;
        } else {
            barCenter.setImageDrawable(this.mTypedArray.getDrawable(12));
            layout.leftMargin = VALUE_EVF_BAR_POSX_6;
            layout.width = VALUE_EVF_BAR_WIDTH_6;
        }
        setLayoutParams(layout);
        if (this.maxIndexValue >= 9) {
            barLeft.setVisibility(0);
            barCenter.setVisibility(0);
            barRight.setVisibility(0);
        } else {
            barLeft.setVisibility(4);
            barCenter.setVisibility(0);
            barRight.setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshTriangleIcons() {
        int edgeTriangleStatus;
        if (this.mMode == 2 && getIsInRange()) {
            disappearUpperTriangle();
            setBlink(false, TRIANGLE_LEFT, this.mTriangleLeft);
            setBlink(false, TRIANGLE_RIGHT, this.mTriangleRight);
            this.mTriangleRight.setVisibility(8);
            this.mTriangleLeft.setVisibility(8);
            return;
        }
        if (0 != 0) {
            edgeTriangleStatus = judgeTriangleIconsBracket(0, this.mMode, getIndexValue(), this.mTriangleUpper);
        } else {
            edgeTriangleStatus = judgeTriangleIcons(this.mMode, getIndexValue(), this.mTriangleUpper);
        }
        if (!getIsInRange()) {
            edgeTriangleStatus = 2;
            setIsOrange(false);
        }
        setImageToTriangle(this.mTriangleLeft, this.mTriangleRight, this.mTriangleUpper);
        if (this.visibleTriangleNum != 0) {
            for (int i = 0; i < this.visibleTriangleNum; i++) {
                this.mTriangleUpper[i].setVisibility(0);
            }
            for (int i2 = this.visibleTriangleNum; i2 < 5; i2++) {
                this.mTriangleUpper[i2].setVisibility(8);
            }
        } else {
            for (int i3 = 0; i3 < 5; i3++) {
                this.mTriangleUpper[i3].setVisibility(8);
            }
        }
        Oscillator.detach(8, this.mTimerListener);
        switch (edgeTriangleStatus) {
            case 1:
                this.mTriangleLeft.setVisibility(0);
                this.mTriangleRight.setVisibility(0);
                setBlink(false, TRIANGLE_LEFT, this.mTriangleLeft);
                setBlink(false, TRIANGLE_RIGHT, this.mTriangleRight);
                break;
            case 2:
                this.mTriangleLeft.setVisibility(0);
                this.mTriangleRight.setVisibility(0);
                setBlink(true, TRIANGLE_LEFT, this.mTriangleLeft);
                setBlink(true, TRIANGLE_RIGHT, this.mTriangleRight);
                Oscillator.attach(8, this.mTimerListener);
                break;
            case 3:
                this.mTriangleLeft.setVisibility(8);
                this.mTriangleRight.setVisibility(0);
                setBlink(false, TRIANGLE_LEFT, this.mTriangleLeft);
                setBlink(false, TRIANGLE_RIGHT, this.mTriangleRight);
                break;
            case 4:
                this.mTriangleLeft.setVisibility(8);
                this.mTriangleRight.setVisibility(0);
                setBlink(false, TRIANGLE_LEFT, this.mTriangleLeft);
                setBlink(true, TRIANGLE_RIGHT, this.mTriangleRight);
                Oscillator.attach(8, this.mTimerListener);
                break;
            case 5:
                this.mTriangleLeft.setVisibility(0);
                this.mTriangleRight.setVisibility(8);
                setBlink(false, TRIANGLE_LEFT, this.mTriangleLeft);
                setBlink(false, TRIANGLE_RIGHT, this.mTriangleRight);
                break;
            case 6:
                this.mTriangleLeft.setVisibility(0);
                this.mTriangleRight.setVisibility(8);
                setBlink(true, TRIANGLE_LEFT, this.mTriangleLeft);
                setBlink(false, TRIANGLE_RIGHT, this.mTriangleRight);
                Oscillator.attach(8, this.mTimerListener);
                break;
            default:
                this.mTriangleLeft.setVisibility(8);
                this.mTriangleRight.setVisibility(8);
                setBlink(false, TRIANGLE_LEFT, this.mTriangleLeft);
                setBlink(false, TRIANGLE_RIGHT, this.mTriangleRight);
                break;
        }
        if (getIsOrange()) {
            this.handlerOrangeTimeout.removeMessages(0);
            this.handlerOrangeTimeout.sendEmptyMessageDelayed(0, 2000L);
        }
    }

    private void setBlink(boolean enable, String key, ImageView image) {
        if (enable) {
            this.blinkIconHashMap.put(key, image);
        } else if (image == null) {
            this.blinkIconHashMap.clear();
        } else {
            this.blinkIconHashMap.remove(key);
        }
    }

    private void disappearUpperTriangle() {
        if (this.mTriangleUpper != null) {
            for (int i = 0; i < 5; i++) {
                if (this.mTriangleUpper[i] != null) {
                    this.mTriangleUpper[i].setVisibility(8);
                }
            }
        }
    }

    private int judgeInRange(int mode, int indexValue) {
        int barMinValue;
        int barMaxValue;
        if (mode == 1) {
            barMinValue = BAR_MIN_VALUE_M;
            barMaxValue = 6;
        } else {
            barMinValue = this.minIndexValue;
            barMaxValue = this.maxIndexValue;
        }
        if (barMinValue <= indexValue && indexValue <= barMaxValue) {
            return 0;
        }
        if (indexValue == barMinValue - 1) {
            return 5;
        }
        if (indexValue == barMaxValue + 1) {
            return 3;
        }
        if (indexValue < barMinValue - 1) {
            return 6;
        }
        if (indexValue <= barMaxValue + 1) {
            return 0;
        }
        return 4;
    }

    private int judgeTriangleIcons(int mode, int indexValue, ImageView[] triangleUpper) {
        this.visibleTriangleNum = 0;
        if (indexValue != this.lastIndexValue) {
            if (this.lastMode == mode) {
                setIsOrange(true);
            }
            disappearUpperTriangle();
        }
        int edgeTriangleStatus = setTriangleIcon(mode, indexValue, triangleUpper[0]);
        if (edgeTriangleStatus == 0) {
            this.visibleTriangleNum++;
        }
        this.lastIndexValue = indexValue;
        this.lastMode = mode;
        return edgeTriangleStatus;
    }

    private int judgeTriangleIconsBracket(int bracketMode, int mode, int centerIndexValue, ImageView[] triangleUpper) {
        int edgeTriangleStatus = 0;
        if (centerIndexValue != this.lastIndexValue) {
            if (this.lastMode == mode) {
                setIsOrange(true);
            }
            disappearUpperTriangle();
        }
        int indexStep = 1;
        if (bracketMode == 2) {
            indexStep = 2;
        } else if (bracketMode == 3) {
            indexStep = 3;
        } else if (bracketMode == 6) {
            indexStep = 6;
        } else if (bracketMode == 9) {
            indexStep = 9;
        }
        this.visibleTriangleNum = 0;
        int start = centerIndexValue - ((this.mBracketShootNum / 2) * indexStep);
        for (int i = 0; i < this.mBracketShootNum; i++) {
            int index = start + (i * indexStep);
            ImageView view = triangleUpper[this.visibleTriangleNum];
            int status = setTriangleIcon(mode, index, view);
            if (status == 0) {
                this.visibleTriangleNum++;
            } else {
                edgeTriangleStatus = status;
            }
        }
        this.lastIndexValue = centerIndexValue;
        this.lastMode = mode;
        return edgeTriangleStatus;
    }

    private int setTriangleIcon(int mode, int indexValue, ImageView view) {
        int edgeTriangleStatus = judgeInRange(mode, indexValue);
        if (edgeTriangleStatus == 0) {
            RelativeLayout.LayoutParams pCenter = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (this.valueEvfPosX != null) {
                int x_posi = this.valueEvfPosX.get(Integer.valueOf(indexValue)).intValue();
                pCenter.setMargins(x_posi - 4, 0, 8, 8);
                view.setLayoutParams(pCenter);
            }
        }
        return edgeTriangleStatus;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIsOrange(boolean isOrange) {
        this.mIsOrange = isOrange;
    }

    private boolean getIsOrange() {
        return this.mIsOrange;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIndexValue(int indexValue) {
        this.mIndexValue = indexValue;
    }

    private int getIndexValue() {
        return this.mIndexValue;
    }

    private boolean getIsInRange() {
        return CameraSetting.getInstance().isMeteringInRange();
    }

    private void checkExposureMaxValue() {
        List<String> strList;
        if (!isMovieMode()) {
            strList = ExposureCompensationController.getInstance().getSupportedValue(null);
        } else {
            strList = ExposureCompensationController.getInstance().getSupportedValueInMode(1);
        }
        if (strList != null) {
            List<Integer> intList = new ArrayList<>(strList.size());
            for (int i = 0; i < strList.size(); i++) {
                intList.add(Integer.valueOf(Integer.parseInt(strList.get(i))));
            }
            Collections.sort(intList);
            this.maxIndexValue = intList.get(intList.size() - 1).intValue();
            this.minIndexValue = intList.get(0).intValue();
            if (this.maxIndexValue == 9) {
                this.valueEvfPosX = VALUE_EVF_POSX_9;
                return;
            }
            if (this.maxIndexValue == 6) {
                this.valueEvfPosX = VALUE_EVF_POSX_6;
            } else if (this.maxIndexValue == 15) {
                this.valueEvfPosX = VALUE_EVF_POSX_15_BY_3;
            } else {
                this.valueEvfPosX = VALUE_EVF_POSX_15_BY_2;
            }
        }
    }

    /* loaded from: classes.dex */
    class BarExposureViewListener implements NotificationListener {
        private String[] TAGS = {"ExposureCompensation", CameraNotificationManager.SCENE_MODE, CameraNotificationManager.METERED_MANUAL, CameraNotificationManager.METERING_RANGE, CameraNotificationManager.REC_MODE_CHANGED, CameraNotificationManager.ISO_SENSITIVITY};

        BarExposureViewListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.TAGS;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (tag.equals("ExposureCompensation")) {
                if (BarExposureView.this.mMode == 0) {
                    int exposureIndexValue = ExposureCompensationController.getInstance().getExposureCompensationIndex();
                    BarExposureView.this.setIndexValue(exposureIndexValue);
                    BarExposureView.this.refreshBarExposureView();
                    return;
                }
                return;
            }
            if (tag.equals(CameraNotificationManager.METERED_MANUAL)) {
                if (BarExposureView.this.mMode == 1) {
                    int meteredManualIndexValue = CameraSetting.getInstance().getMeteredManualIndex();
                    BarExposureView.this.setIndexValue(meteredManualIndexValue);
                    BarExposureView.this.refreshBarExposureView();
                    return;
                }
                return;
            }
            if (tag.equals(CameraNotificationManager.METERING_RANGE)) {
                BarExposureView.this.refreshBarExposureView();
            } else if (tag.equals(CameraNotificationManager.SCENE_MODE) || tag.equals(CameraNotificationManager.REC_MODE_CHANGED) || tag.equals(CameraNotificationManager.ISO_SENSITIVITY)) {
                BarExposureView.this.getExposureMode();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getExposureMode() {
        int mode;
        String expMode = ExposureModeController.getInstance().getValue(null);
        if (ExposureCompensationController.getInstance().isExposureCompensationAvailable()) {
            mode = 0;
        } else if (ExposureModeController.MANUAL_MODE.equals(expMode) || ExposureModeController.MOVIE_MANUAL_MODE.equals(expMode)) {
            mode = 1;
        } else {
            mode = 2;
        }
        setMode(mode);
    }

    private void initImageView() {
        if (this.mBarLeft == null) {
            this.mBarLeft = new ImageView(this.mContext);
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(-2, -2);
            layout.addRule(12);
            layout.addRule(9);
            this.mBarLeft.setLayoutParams(layout);
            addView(this.mBarLeft);
        }
        if (this.mBarCenter == null) {
            this.mBarCenter = new ImageView(this.mContext);
            RelativeLayout.LayoutParams layout2 = new RelativeLayout.LayoutParams(-2, -2);
            layout2.addRule(12);
            layout2.addRule(14);
            this.mBarCenter.setLayoutParams(layout2);
            addView(this.mBarCenter);
        }
        if (this.mBarRight == null) {
            this.mBarRight = new ImageView(this.mContext);
            RelativeLayout.LayoutParams layout3 = new RelativeLayout.LayoutParams(-2, -2);
            layout3.addRule(12);
            layout3.addRule(11);
            this.mBarRight.setLayoutParams(layout3);
            addView(this.mBarRight);
        }
        if (this.mTriangleUpper == null) {
            this.mTriangleUpper = new ImageView[5];
            for (int i = 0; i < 5; i++) {
                this.mTriangleUpper[i] = new ImageView(this.mContext);
                addView(this.mTriangleUpper[i]);
            }
        }
        if (this.mTriangleRight == null) {
            this.mTriangleRight = new ImageView(this.mContext);
            RelativeLayout.LayoutParams layout4 = new RelativeLayout.LayoutParams(-2, -2);
            layout4.addRule(10);
            layout4.addRule(11);
            this.mTriangleRight.setLayoutParams(layout4);
            addView(this.mTriangleRight);
        }
        if (this.mTriangleLeft == null) {
            this.mTriangleLeft = new ImageView(this.mContext);
            RelativeLayout.LayoutParams layout5 = new RelativeLayout.LayoutParams(-2, -2);
            layout5.addRule(10);
            layout5.addRule(9);
            this.mTriangleLeft.setLayoutParams(layout5);
            addView(this.mTriangleLeft);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (ExposureCompensationController.getInstance().getSupportedValue(null) != null) {
            initImageView();
            refreshBarType(this.mMode, this.mBarLeft, this.mBarCenter, this.mBarRight);
            this.isOnFinishInflate = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.handlerOrangeTimeout.removeCallbacksAndMessages(null);
        setBlink(false, null, null);
        Oscillator.detach(8, this.mTimerListener);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null && ExposureCompensationController.getInstance().getSupportedValue(null) != null) {
            this.mListener = new BarExposureViewListener();
        }
        return this.mListener;
    }

    protected boolean isMovieMode() {
        return Environment.isMovieAPISupported() && 2 == CameraSetting.getInstance().getCurrentMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveLayout
    public boolean isVisible() {
        if (ExposureCompensationController.getInstance().getSupportedValue(null) != null) {
            return true;
        }
        return false;
    }
}
