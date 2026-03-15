package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class CompensationWheelView extends RelativeLayout {
    public static final int COMPBAR_HEIGHT = 34;
    public static final int COMPBAR_SET_ARR_CENTER = 1;
    public static final int COMPBAR_SET_ARR_COUNT = 3;
    public static final int COMPBAR_SET_ARR_LEFT = 0;
    public static final int COMPBAR_SET_ARR_RIGHT = 2;
    public static final int COMPIND_ARR_COUNT = 2;
    public static final int COMPIND_ARR_LOWER = 1;
    public static final int COMPIND_ARR_UPPER = 0;
    private static final int COMPIND_CENTER_POS = 320;
    private static final int COMPIND_COMPBAR_GAP = 1;
    private static final int COMPIND_HEIGHT = 20;
    private static final int COMPIND_LOWER_MARGIN = 56;
    private static final int COMPIND_TOP_MARGIN = 0;
    private static final int COMPIND_WIDTH = 20;
    private static final int COMP_RANGE_PULAS_MINUS_2 = 13;
    private static final int COMP_RANGE_PULAS_MINUS_3 = 19;
    private static final int COMP_TYPE_EV = 1;
    private static final int COMP_TYPE_FLASH = 2;
    private static final String LOG_MSG_INVALID_RANGE = "setSliderResources range is invalid";
    private static final String LOG_MSG_START_MOVE = "start touch move";
    private static final String TAG = "CompensationWheelView";
    private static final float TOUCH_SLOP = ViewConfiguration.getTouchSlop();
    private boolean isMoveStarted;
    int mCompindLeftMargin;
    int mCompindMoveOffset;
    int mCompindMoveWidth;
    private boolean mDrawPanel;
    float[] mExposureCompIndPosition_13;
    float[] mFlashIndPosition_13_Evf;
    float[] mFlashIndPosition_13_Panel;
    float[] mFlashIndPosition_19_Evf;
    float[] mFlashIndPosition_19_Panel;
    private KnobCanvas mKnobCanvas;
    int mLevel;
    private int mMaxValue;
    private int mMinValue;
    boolean mPressed;
    private float mTouchOffset;
    private float mTouchStartPos;
    private int mWheelRange;
    private ImageView[] mcompbarSet;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class KnobCanvas extends View {
        private Bitmap mCompindLower;
        private Bitmap mCompindUpper;
        private int mLevelLastDrawn;
        private boolean mPressedLastDrawn;

        public KnobCanvas(Context context) {
            super(context);
            this.mPressedLastDrawn = false;
            this.mLevelLastDrawn = 0;
        }

        void invalidate(boolean force) {
            boolean isChanged = (this.mPressedLastDrawn == CompensationWheelView.this.mPressed && this.mLevelLastDrawn == CompensationWheelView.this.mLevel) ? false : true;
            if (force || isChanged) {
                super.invalidate();
            }
        }

        void setKnobImages(int[] compind) {
            this.mCompindUpper = null;
            this.mCompindLower = null;
            if (compind[0] != -1) {
                this.mCompindUpper = BitmapFactory.decodeResource(getResources(), compind[0]);
            }
            if (compind[1] != -1) {
                this.mCompindLower = BitmapFactory.decodeResource(getResources(), compind[1]);
            }
        }

        int getCompType() {
            if (this.mCompindUpper != null) {
                return 1;
            }
            if (this.mCompindLower == null) {
                return -1;
            }
            return 2;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            canvas.save();
            Bitmap indUpper = this.mCompindUpper;
            Bitmap indLower = this.mCompindLower;
            if (indUpper != null) {
                canvas.drawBitmap(indUpper, CompensationWheelView.this.calcCompindPosX(CompensationWheelView.this.mLevel) - (indUpper.getWidth() / 2), CompensationWheelView.this.calcCompindPosY(CompensationWheelView.this.mLevel), (Paint) null);
                Log.i(CompensationWheelView.TAG, "EV mLevel=" + CompensationWheelView.this.mLevel);
            }
            if (indLower != null) {
                canvas.drawBitmap(indLower, CompensationWheelView.this.calcCompindPosX(CompensationWheelView.this.mLevel) - (indLower.getWidth() / 2), CompensationWheelView.this.calcCompindPosY(CompensationWheelView.this.mLevel) + 56.0f, (Paint) null);
                Log.i(CompensationWheelView.TAG, "Flash mLevel=" + CompensationWheelView.this.mLevel);
            }
            canvas.restore();
            this.mPressedLastDrawn = CompensationWheelView.this.mPressed;
            this.mLevelLastDrawn = CompensationWheelView.this.mLevel;
            super.onDraw(canvas);
        }
    }

    public CompensationWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchStartPos = SmoothReflectionConstants.INVALID_APERTURE_VALUE;
        this.isMoveStarted = false;
        this.mDrawPanel = true;
        this.mExposureCompIndPosition_13 = new float[]{186.0f, 208.3f, 230.7f, 253.0f, 275.3f, 297.7f, 320.0f, 342.3f, 364.7f, 387.0f, 409.3f, 431.7f, 454.0f};
        this.mFlashIndPosition_13_Panel = new float[]{32.0f, 56.5f, 81.0f, 106.0f, 131.0f, 156.5f, 182.0f, 207.5f, 233.0f, 257.5f, 282.0f, 307.0f, 332.0f};
        this.mFlashIndPosition_19_Panel = new float[]{32.0f, 50.0f, 62.0f, 81.0f, 100.0f, 112.0f, 131.0f, 150.0f, 162.0f, 182.0f, 200.0f, 212.0f, 233.0f, 250.0f, 262.0f, 282.0f, 300.0f, 312.0f, 332.0f};
        this.mFlashIndPosition_13_Evf = new float[]{27.0f, 48.5f, 70.0f, 91.0f, 112.0f, 134.0f, 156.0f, 177.5f, 199.0f, 220.5f, 242.0f, 263.0f, 284.0f};
        this.mFlashIndPosition_19_Evf = new float[]{27.0f, 43.0f, 53.0f, 70.0f, 86.0f, 96.0f, 112.0f, 129.0f, 139.0f, 156.0f, 171.0f, 182.0f, 199.0f, 214.0f, 224.0f, 242.0f, 257.0f, 267.0f, 284.0f};
        this.mPressed = false;
        this.mWheelRange = 0;
        this.mLevel = 0;
        this.mMaxValue = 0;
        this.mMinValue = 0;
        this.mcompbarSet = new ImageView[3];
        for (int i = 0; i < 3; i++) {
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(0, 0);
            this.mcompbarSet[i] = new ImageView(context);
            addView(this.mcompbarSet[i], param);
        }
        RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(-1, -1);
        this.mKnobCanvas = new KnobCanvas(context);
        addView(this.mKnobCanvas, param2);
    }

    public void setResources(int wheelRange, int[] compindIds, int compindOffset, int[] compbarIds, int[] compbarWidth, int compbarHeight, int compbarLeftMargin, int compbarTopMargin, boolean drawType) {
        Pair<Integer, Integer> availableRange = new Pair<>(Integer.valueOf(wheelRange - 1), 0);
        setResources(wheelRange, availableRange, compindIds, compindOffset, compbarIds, compbarWidth, compbarHeight, compbarLeftMargin, compbarTopMargin, drawType);
    }

    public void setResources(int wheelRange, Pair<Integer, Integer> availableRange, int[] compindIds, int compindOffset, int[] compbarIds, int[] compbarWidth, int compbarHeight, int compbarLeftMargin, int compbarTopMargin, boolean drawType) {
        if (wheelRange < 1) {
            Log.e(TAG, LOG_MSG_INVALID_RANGE);
            return;
        }
        this.mWheelRange = wheelRange;
        if (availableRange != null) {
            this.mMaxValue = ((Integer) availableRange.first).intValue();
            this.mMinValue = ((Integer) availableRange.second).intValue();
        } else {
            this.mMaxValue = wheelRange - 1;
            this.mMinValue = 0;
        }
        this.mKnobCanvas.setKnobImages(compindIds);
        this.mCompindLeftMargin = compbarLeftMargin;
        this.mCompindMoveWidth = 0;
        this.mCompindMoveOffset = compindOffset;
        Log.i(TAG, "wheelRange=" + wheelRange + " compindOffset=" + compindOffset + " compbarHeight=" + compbarHeight + " compbarLeftMargin=" + compbarLeftMargin + " compbarTopMargin=" + compbarTopMargin);
        for (int i = 0; i < 3; i++) {
            if (-1 != compbarIds[i]) {
                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) this.mcompbarSet[i].getLayoutParams();
                param.width = compbarWidth[i];
                param.height = compbarHeight;
                param.leftMargin = compbarLeftMargin;
                for (int j = 0; j < i; j++) {
                    param.leftMargin += compbarWidth[j];
                }
                param.topMargin = compbarTopMargin;
                this.mcompbarSet[i].setLayoutParams(param);
                this.mcompbarSet[i].setImageResource(compbarIds[i]);
                this.mCompindMoveWidth += compbarWidth[i];
            }
        }
        this.mDrawPanel = drawType;
        refreshKnobCanvas(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float calcCompindPosX(int rangePos) {
        int center = (this.mWheelRange - 1) / 2;
        float pos = 320.0f;
        if (this.mKnobCanvas.getCompType() == 1) {
            if (this.mWheelRange == 13) {
                return this.mExposureCompIndPosition_13[rangePos];
            }
            if (rangePos != center) {
                return 320.0f - ((center - rangePos) * ((this.mCompindMoveWidth - (this.mCompindMoveOffset * 2)) / (this.mWheelRange - 1)));
            }
            return 320.0f;
        }
        if (this.mKnobCanvas.getCompType() != 2) {
            return 320.0f;
        }
        if (this.mDrawPanel) {
            if (this.mWheelRange == 13) {
                pos = this.mFlashIndPosition_13_Panel[rangePos];
            } else if (this.mWheelRange == 19) {
                pos = this.mFlashIndPosition_19_Panel[rangePos];
            }
        } else if (this.mWheelRange == 13) {
            pos = this.mFlashIndPosition_13_Evf[rangePos];
        } else if (this.mWheelRange == 19) {
            pos = this.mFlashIndPosition_19_Evf[rangePos];
        }
        return pos + this.mCompindLeftMargin;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float calcCompindPosY(int rangePos) {
        return SmoothReflectionConstants.INVALID_APERTURE_VALUE;
    }

    public void setPosition(int value) {
        this.mLevel = value;
        refreshKnobCanvas(false);
    }

    public int moveUp() {
        this.mLevel = Math.min(this.mLevel + 1, this.mMaxValue);
        refreshKnobCanvas(false);
        return this.mLevel;
    }

    public int moveDown() {
        this.mLevel = Math.max(this.mLevel - 1, this.mMinValue);
        refreshKnobCanvas(false);
        return this.mLevel;
    }

    public int onTouch(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case 0:
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_TAP);
                this.mPressed = true;
                this.mTouchOffset = x - calcCompindPosX(this.mLevel);
                this.mTouchStartPos = x;
                this.isMoveStarted = false;
                refreshKnobCanvas(true);
                break;
            case 1:
                this.mPressed = false;
                this.mTouchOffset = SmoothReflectionConstants.INVALID_APERTURE_VALUE;
                refreshKnobCanvas(true);
                break;
            case 2:
                if (TOUCH_SLOP > Math.abs(this.mTouchStartPos - x) && !this.isMoveStarted) {
                    refreshKnobCanvas(false);
                    this.mTouchOffset = x - calcCompindPosX(this.mLevel);
                    break;
                } else {
                    if (!this.isMoveStarted) {
                        this.isMoveStarted = true;
                        Log.i(TAG, LOG_MSG_START_MOVE);
                    }
                    float x2 = x - this.mTouchOffset;
                    int level = 0;
                    float min = Float.MAX_VALUE;
                    int length = this.mWheelRange;
                    for (int i = 0; i < length; i++) {
                        float diff = Math.abs(x2 - calcCompindPosX(i));
                        if (min > diff) {
                            min = (int) diff;
                            level = i;
                        }
                    }
                    this.mLevel = level;
                    refreshKnobCanvas(false);
                    break;
                }
                break;
        }
        return this.mLevel;
    }

    private void refreshKnobCanvas(boolean force) {
        this.mKnobCanvas.invalidate(force);
    }
}
