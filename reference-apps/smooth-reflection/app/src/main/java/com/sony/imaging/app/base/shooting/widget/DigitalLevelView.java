package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.util.GyroscopeObserver;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.sysutil.ScalarProperties;

/* loaded from: classes.dex */
public class DigitalLevelView extends View {
    private static final double ADJUST_POINT = 0.5d;
    private static final int ARRAY_INDEX_OFFSET = 20;
    private static final int DIRECTION_HORIZON = 0;
    private static final int DIRECTION_VERTICAL = 1;
    private static final String DISPLAY_EVF = "EVF";
    private static final int EVF_PAIR_HEIGHT = 0;
    private static final int EVF_PAIR_WIDTH = 0;
    private static final int INDICATOR_ID_NO_LEVEL = -1;
    private static final int PANEL_PAIR_HEIGHT = 0;
    private static final int PANEL_PAIR_WIDTH = 0;
    private static final double PITCH_ADJUST_XPOS_EVF = 2.0d;
    private static final double PITCH_ADJUST_XPOS_PANEL_169 = 1.6d;
    private static final double PITCH_ADJUST_XPOS_PANEL_43 = 2.0d;
    private static final double PITCH_ADJUST_YPOS = 2.0d;
    private static final String PTAG_DIGITAL_LEVEL = "digital level";
    private static final String TAG = "DigitalLevelView";
    private static final int UNUSED_ID = -1;
    private SparseArray<Point> mCenterOffsets;
    private int mDirectionFlag;
    private String mDisplayDevice;
    private NotificationListener mGyroListener;
    private GyroscopeObserver mGyroObserver;
    private int mPitch;
    private double mPitchAdjustXPos;
    private LevelIndicator mPitchInd;
    private final ResIdSet[] mResIdSets;
    private final ResIdSet[] mResIdSetsOnlyRoll;
    private int mRoll;
    private LevelIndicator mRollBase;
    private LevelIndicator mRollCenterF;
    private LevelIndicator mRollCenterN;
    private LevelIndicator mRollInd;
    private TypedArray mTypedArray;
    private static final float[] COS = {1.0f, 0.9998477f, 0.99939084f, 0.9986295f, 0.9975641f, 0.9961947f, 0.9945219f, 0.99254614f, 0.99026805f, 0.98768836f, 0.9848077f, 0.98162717f, 0.9781476f, 0.97437006f, 0.9702957f, 0.9659258f, 0.9612617f, 0.9563047f, 0.95105654f, 0.94551855f, 0.9396926f};
    private static final float[] SIN = {SmoothReflectionConstants.INVALID_APERTURE_VALUE, 0.017452406f, 0.034899496f, 0.052335955f, 0.06975647f, 0.087155744f, 0.104528464f, 0.12186934f, 0.1391731f, 0.15643446f, 0.17364818f, 0.190809f, 0.20791169f, 0.22495106f, 0.2419219f, 0.25881904f, 0.27563736f, 0.2923717f, 0.309017f, 0.32556814f, 0.34202015f};

    public DigitalLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mResIdSets = new ResIdSet[]{new ResIdSet(1, 6, 4, 5, 2, 3), new ResIdSet(8, 13, 11, 12, 9, 10)};
        this.mResIdSetsOnlyRoll = new ResIdSet[]{new ResIdSet(1, 7, -1, -1, 2, 3), new ResIdSet(8, 14, -1, -1, 9, 10)};
        this.mGyroListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.DigitalLevelView.1
            private final String[] TAGS = {GyroscopeObserver.TAG_GYROSCOPE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String arg0) {
                PTag.start(DigitalLevelView.PTAG_DIGITAL_LEVEL);
                float[] values = (float[]) DigitalLevelView.this.mGyroObserver.getValue(GyroscopeObserver.TAG_GYROSCOPE);
                if (DigitalLevelView.this.changeRad(values)) {
                    DigitalLevelView.this.refresh();
                }
                PTag.end(DigitalLevelView.PTAG_DIGITAL_LEVEL);
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAGS;
            }
        };
        this.mDisplayDevice = "";
        this.mDirectionFlag = 0;
        this.mRoll = 0;
        this.mPitch = 0;
        this.mGyroObserver = GyroscopeObserver.getInstance();
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.DigitalLevel);
        this.mCenterOffsets = new SparseArray<>();
        this.mCenterOffsets.append(1, new Point());
        this.mCenterOffsets.append(0, new Point());
        this.mRollBase = new LevelIndicator(context);
        this.mRollInd = new LevelIndicator(context);
        this.mRollCenterF = new LevelIndicator(context);
        this.mRollCenterN = new LevelIndicator(context);
        this.mPitchInd = new LevelIndicator(context);
        setDiplay();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mGyroObserver.setNotificationListener(this.mGyroListener);
        refresh();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mGyroObserver.removeNotificationListener(this.mGyroListener);
    }

    private void setDiplay() {
        this.mDisplayDevice = this.mTypedArray.getString(0);
        if (DISPLAY_EVF.equals(this.mDisplayDevice)) {
            this.mPitchAdjustXPos = 2.0d;
            this.mCenterOffsets.get(0).x = 0;
            this.mCenterOffsets.get(0).y = 0;
            this.mCenterOffsets.get(1).x = 0;
            this.mCenterOffsets.get(1).y = 0;
            return;
        }
        int aspect = DisplayModeObserver.getInstance().getActiveDeviceOsdAspect();
        if (2 == aspect) {
            this.mPitchAdjustXPos = PITCH_ADJUST_XPOS_PANEL_169;
        } else {
            this.mPitchAdjustXPos = 2.0d;
        }
        this.mCenterOffsets.get(0).x = 0;
        this.mCenterOffsets.get(0).y = 0;
        this.mCenterOffsets.get(1).x = 0;
        this.mCenterOffsets.get(1).y = 0;
    }

    void refresh() {
        ResIdSet ids;
        int resHorizonIndex = this.mRoll + 20;
        if (3 <= CameraSetting.getPfApiVersion()) {
            int isSupporeted = ScalarProperties.getInt("ui.digital.level.type");
            if (isSupporeted == 1) {
                ids = this.mResIdSets[this.mDirectionFlag];
            } else if (isSupporeted == 2) {
                ids = this.mResIdSetsOnlyRoll[this.mDirectionFlag];
            } else {
                ids = this.mResIdSets[this.mDirectionFlag];
            }
        } else {
            ids = this.mResIdSets[this.mDirectionFlag];
        }
        Point p = this.mCenterOffsets.get(this.mDirectionFlag);
        updateLevelIndicator(this.mRollBase, -1, ids.baseResId);
        this.mRollBase.setCenterOffset(p.x, p.y);
        updateLevelIndicator(this.mRollInd, ids.rollArrayId, resHorizonIndex);
        if (this.mRoll == 0) {
            updateLevelIndicator(this.mRollCenterF, -1, ids.centerResId);
            this.mRollCenterF.setCenterOffset(p.x, p.y);
        } else {
            updateLevelIndicator(this.mRollCenterN, -1, ids.centerNResId);
            this.mRollCenterN.setCenterOffset(p.x, p.y);
        }
        if (this.mPitch == 0) {
            if (ids.pitchFlatArrayId == -1) {
                this.mPitchInd.setVisibility(4);
            } else {
                updateLevelIndicator(this.mPitchInd, ids.pitchFlatArrayId, resHorizonIndex);
            }
        } else if (ids.pitchArrayId == -1) {
            this.mPitchInd.setVisibility(4);
        } else {
            updateLevelIndicator(this.mPitchInd, ids.pitchArrayId, resHorizonIndex);
            this.mPitchInd.setCenterOffset(getAjustPitchX(), getAjustPitchY());
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mRollBase.draw(canvas);
        this.mRollInd.draw(canvas);
        if (this.mRoll == 0) {
            this.mRollCenterF.draw(canvas);
        } else {
            this.mRollCenterN.draw(canvas);
        }
        this.mPitchInd.draw(canvas);
    }

    private int getAjustPitchX() {
        if (this.mDirectionFlag == 0) {
            if (this.mRoll > 0) {
                int result = (int) ((this.mPitch * this.mPitchAdjustXPos * SIN[this.mRoll]) + ADJUST_POINT);
                return result;
            }
            int result2 = (int) ((this.mPitch * (-1) * this.mPitchAdjustXPos * SIN[this.mRoll * (-1)]) + ADJUST_POINT);
            return result2;
        }
        if (this.mRoll > 0) {
            int result3 = (int) ((this.mPitch * this.mPitchAdjustXPos * COS[this.mRoll]) + ADJUST_POINT);
            return result3;
        }
        int result4 = (int) ((this.mPitch * this.mPitchAdjustXPos * COS[this.mRoll * (-1)]) + ADJUST_POINT);
        return result4;
    }

    private int getAjustPitchY() {
        if (this.mDirectionFlag == 0) {
            if (this.mRoll > 0) {
                int result = (int) ((this.mPitch * 2.0d * COS[this.mRoll]) + ADJUST_POINT);
                return result;
            }
            int result2 = (int) ((this.mPitch * 2.0d * COS[this.mRoll * (-1)]) + ADJUST_POINT);
            return result2;
        }
        if (this.mRoll > 0) {
            int result3 = (int) ((this.mPitch * (-1) * 2.0d * SIN[this.mRoll]) + ADJUST_POINT);
            return result3;
        }
        int result4 = (int) ((this.mPitch * 2.0d * SIN[this.mRoll * (-1)]) + ADJUST_POINT);
        return result4;
    }

    boolean changeRad(float[] values) {
        boolean needRefresh = false;
        int direction = 0;
        int roll = 0;
        int pitch = ((int) Math.toDegrees(values[1])) * (-1);
        double rollTmp = Math.toDegrees(values[2]) * (-1.0d);
        if (rollTmp < 0.0d) {
            roll = (int) ((Math.toDegrees(values[2]) * (-1.0d)) - ADJUST_POINT);
        } else if (rollTmp > 0.0d) {
            roll = (int) ((Math.toDegrees(values[2]) * (-1.0d)) + ADJUST_POINT);
        }
        if (roll > 0 && roll <= 45) {
            direction = 0;
        } else if (roll > 45 && roll <= 135) {
            roll -= 90;
            direction = 1;
        } else if (roll > 135 && roll <= 180) {
            roll -= 180;
            direction = 0;
        } else if (roll > -45 && roll <= 0) {
            direction = 0;
        } else if (roll > -135 && roll <= -45) {
            roll += 90;
            direction = 1;
        } else if (roll >= -180 && roll <= -135) {
            roll += 180;
            direction = 0;
        }
        int roll2 = Math.min(Math.max(roll, -20), 20);
        int pitch2 = Math.min(Math.max(pitch, -20), 20);
        if (direction != this.mDirectionFlag) {
            needRefresh = true;
            this.mDirectionFlag = direction;
        }
        if (roll2 != this.mRoll) {
            needRefresh = true;
            this.mRoll = roll2;
        }
        if (pitch2 != this.mPitch) {
            this.mPitch = pitch2;
            return true;
        }
        return needRefresh;
    }

    private void updateLevelIndicator(LevelIndicator indicator, int arrayId, int resId) {
        if (-1 == arrayId) {
            indicator.setDrawable(this.mTypedArray.getDrawable(resId));
            return;
        }
        int array = this.mTypedArray.getResourceId(arrayId, -1);
        if (-1 != array) {
            TypedArray drawables = getResources().obtainTypedArray(array);
            indicator.setDrawable(drawables.getDrawable(resId));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LevelIndicator extends View {
        private static final float CENTER = 173.0f;
        protected Drawable mIcon;
        protected Rect mRect;

        public LevelIndicator(Context context) {
            super(context);
        }

        public void setCenterOffset(int x, int y) {
            if (this.mIcon != null) {
                int width = this.mRect.right - this.mRect.left;
                int height = this.mRect.bottom - this.mRect.top;
                int rx = getLeft(x, width);
                int ry = getTop(y, height);
                this.mRect.set(rx, ry, rx + width, ry + height);
                this.mIcon.setBounds(this.mRect);
            }
        }

        public void setDrawable(Drawable d) {
            this.mIcon = d;
            int width = d.getIntrinsicWidth();
            int height = d.getIntrinsicHeight();
            int rx = getLeft(0, width);
            int ry = getTop(0, height);
            this.mRect = new Rect(rx, ry, rx + width, ry + height);
            this.mIcon.setBounds(this.mRect);
        }

        private int getLeft(int offsetX, int width) {
            return (int) ((CENTER - offsetX) - (width / 2.0d));
        }

        private int getTop(int offsetY, int height) {
            return (int) ((CENTER - offsetY) - (height / 2.0d));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.mIcon != null) {
                this.mIcon.draw(canvas);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ResIdSet {
        final int baseResId;
        final int centerNResId;
        final int centerResId;
        final int pitchArrayId;
        final int pitchFlatArrayId;
        final int rollArrayId;

        ResIdSet(int br, int ra, int pfa, int pa, int cr, int crn) {
            this.baseResId = br;
            this.rollArrayId = ra;
            this.pitchFlatArrayId = pfa;
            this.pitchArrayId = pa;
            this.centerResId = cr;
            this.centerNResId = crn;
        }
    }
}
