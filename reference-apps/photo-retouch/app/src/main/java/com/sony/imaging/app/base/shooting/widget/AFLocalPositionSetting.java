package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.widget.AbstractAFView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IConvertibleKeyHandler;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.InnerViewKeyHandler;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AFLocalPositionSetting extends AFLocal implements IConvertibleKeyHandler {
    private static final int CLOCKWISE_ANTI = 6;
    private static final int CLOCKWISE_RIGHT = 5;
    private static final int HORIZONAL = 2;
    private static final String LOG_AFFLEXIBLEPOSITIONSETTING = "AFFlexiblePositionSetting";
    private static final String LOG_CANNOT_SPECIFY_NEUTRAL_POS = "###ERROR: Can't s specify neutral position";
    private static final String LOG_CENTERGAP = "  centerGap: ";
    private static final String LOG_DISPATCHKEYEVENT = "dispatchKeyEvent";
    private static final String LOG_GAP = "  gap: ";
    private static final String LOG_GAP_DISTANCE = "gapDistance: ";
    private static final String LOG_ONKEYDOWN = "onKeyDown";
    private static final String LOG_UNKNOWN = "もうわかりません(^ω^; わからないんでとりあえず最後に残った候補の１つ返します(^ω^;";
    private static final String LOG_VALUE = "value: ";
    private static final String LOG_XGAP = "  xGap: ";
    private static final String LOG_YGAP = "  yGap: ";
    private static final String TAG = "AFLocalPositionSetting";
    private static final int TODOWN = 4;
    private static final int TOLEFT = 2;
    private static final int TORIGHT = 1;
    private static final int TOUP = 3;
    private static final int VERTICAL = 1;
    private FocusAreaController mFocusAreaController;
    protected InnerViewKeyHandler mInnerKeyHandler;
    private CameraEx.LensInfo mLensInfo;
    private PositionListener mListener;
    private static StringBuilder builder = new StringBuilder();
    private static int mNeutralIndex = -1;
    private static final int[] DIAL_ORDER_GPMA2_15 = {9, 2, 3, 12, 11, 8, 15, 1, 13, 4, 10, 14, 7, 6, 5};
    private static final int[] DIAL_ORDER_GPMA2_11 = {9, 3, 12, 8, 15, 1, 13, 4, 14, 7, 5};

    /* loaded from: classes.dex */
    public interface PositionListener {
        void onPositionChanged(int i);
    }

    public AFLocalPositionSetting(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFocusAreaController = FocusAreaController.getInstance();
        this.mInnerKeyHandler = new InnerViewKeyHandler(this, ICustomKey.CATEGORY_FOCUS_SETTING);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected AbstractAFView.CameraNotificationListener getCameraNotificationListener() {
        return new AbstractAFView.CameraNotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.AFLocalPositionSetting.1
            @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView.CameraNotificationListener, com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AFLocal, com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLensInfo = CameraSetting.getInstance().getLensInfo();
        int size = this.mAFLocalRectInfo.getSize();
        for (int i = 0; i < size; i++) {
            boolean isEnable = this.mAFLocalRectInfo.isAFFrameEnableAtOrder(i);
            if (isEnable) {
                this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtOrder(i).setUnSelected();
            }
        }
        this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(this.mFocusReadyFrameIndex).setSelected();
    }

    public void setPositionListener(PositionListener l) {
        this.mListener = l;
    }

    private List<Integer> getDirectionFramesIndex(int direction, int currentFrameIndex, List<Integer> framesIndex) {
        AFFramePhaseDiff currentFrame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(currentFrameIndex);
        this.mAFLocalRectInfo.isAFFrameEnableAtIndex(currentFrameIndex);
        List<Integer> resultFramesIndex = new ArrayList<>();
        switch (direction) {
            case 1:
                for (Integer index : framesIndex) {
                    boolean isEnableOtherFrame = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index.intValue());
                    if (isEnableOtherFrame) {
                        AFFramePhaseDiff f = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index.intValue());
                        if (currentFrame.getLeft() < f.getLeft()) {
                            resultFramesIndex.add(index);
                        }
                    }
                }
                return resultFramesIndex;
            case 2:
                for (Integer index2 : framesIndex) {
                    boolean isEnableOtherFrame2 = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index2.intValue());
                    if (isEnableOtherFrame2) {
                        AFFramePhaseDiff f2 = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index2.intValue());
                        if (currentFrame.getLeft() > f2.getLeft()) {
                            resultFramesIndex.add(index2);
                        }
                    }
                }
                return resultFramesIndex;
            case 3:
                for (Integer index3 : framesIndex) {
                    boolean isEnableOtherFrame3 = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index3.intValue());
                    if (isEnableOtherFrame3) {
                        AFFramePhaseDiff f3 = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index3.intValue());
                        if (currentFrame.getTop() > f3.getTop()) {
                            resultFramesIndex.add(index3);
                        }
                    }
                }
                return resultFramesIndex;
            case 4:
                for (Integer index4 : framesIndex) {
                    boolean isEnableOtherFrame4 = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index4.intValue());
                    if (isEnableOtherFrame4) {
                        AFFramePhaseDiff f4 = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index4.intValue());
                        if (currentFrame.getTop() < f4.getTop()) {
                            resultFramesIndex.add(index4);
                        }
                    }
                }
                return resultFramesIndex;
            default:
                Log.d(TAG, "Invalid direction");
                return resultFramesIndex;
        }
    }

    private List<Integer> getUnfairMinimumGapFrameIndex(int currentFrameIndex, List<Integer> framesIndex, int angle, int unfair) {
        AFFramePhaseDiff currentFrame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(currentFrameIndex);
        int minimumGap = Integer.MAX_VALUE;
        List<Integer> minimumGapFramesIndex = new ArrayList<>();
        for (Integer index : framesIndex) {
            boolean isEnableOtherFrame = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index.intValue());
            if (isEnableOtherFrame) {
                int gap = 0;
                View frame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index.intValue());
                if (angle == 1) {
                    gap = Math.abs(currentFrame.getTop() - frame.getTop());
                } else if (angle == 2) {
                    gap = Math.abs(currentFrame.getLeft() - frame.getLeft());
                }
                if (gap < unfair) {
                    gap = 0;
                }
                if (minimumGap >= gap) {
                    if (minimumGap != gap) {
                        minimumGapFramesIndex.clear();
                    }
                    minimumGap = gap;
                    minimumGapFramesIndex.add(index);
                }
            }
        }
        return minimumGapFramesIndex;
    }

    private List<Integer> getMinimumDistanceFramesIndexClosestToCenter(DisplayManager.VideoRect yuvRect, int currentFrameIndex, List<Integer> framesIndex, int angle) {
        AFFramePhaseDiff currentFrame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(currentFrameIndex);
        int centerY = ((yuvRect.pxBottom - yuvRect.pxTop) / 2) + yuvRect.pxTop;
        int centerX = ((yuvRect.pxRight - yuvRect.pxLeft) / 2) + yuvRect.pxLeft;
        int minimum = Integer.MAX_VALUE;
        List<Integer> minimumFramesIndex = new ArrayList<>();
        for (Integer index : framesIndex) {
            boolean isEnableOtherFrame = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index.intValue());
            if (isEnableOtherFrame) {
                View frame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index.intValue());
                int xGap = Math.abs(currentFrame.getLeft() - frame.getLeft());
                int yGap = Math.abs(currentFrame.getTop() - frame.getTop());
                int eachDistance = (int) (Math.pow(xGap, 2.0d) + Math.pow(yGap, 2.0d));
                int centerGap = (int) Math.pow(angle == 1 ? Math.abs(centerY - (frame.getTop() + (frame.getHeight() / 2))) : Math.abs(centerX - (frame.getLeft() + (frame.getWidth() / 2))), 2.0d);
                int currentValue = eachDistance + centerGap;
                Log.d(TAG, builder.replace(0, LOG_VALUE.length(), LOG_VALUE).append(currentValue).append(LOG_CENTERGAP).append(centerGap).append(LOG_XGAP).append(Math.pow(xGap, 2.0d)).append(LOG_YGAP).append(Math.pow(yGap, 2.0d)).toString());
                if (minimum >= currentValue) {
                    if (minimum != currentValue) {
                        minimumFramesIndex.clear();
                    }
                    minimum = currentValue;
                    minimumFramesIndex.add(index);
                }
            }
        }
        return minimumFramesIndex;
    }

    private List<Integer> getGapFrameIndex(int currentFrameIndex, List<Integer> framesIndex, int specifiedGap, int angle) {
        AFFramePhaseDiff currentFrame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(currentFrameIndex);
        int minimumGap = specifiedGap < 0 ? Integer.MAX_VALUE : specifiedGap;
        List<Integer> minimumGapFramesIndex = new ArrayList<>();
        for (Integer index : framesIndex) {
            boolean isEnableOtherFrame = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index.intValue());
            if (isEnableOtherFrame) {
                View frame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index.intValue());
                int gap = 0;
                if (angle == 1) {
                    gap = Math.abs(currentFrame.getTop() - frame.getTop());
                } else if (angle == 2) {
                    gap = Math.abs(currentFrame.getLeft() - frame.getLeft());
                }
                if (minimumGap >= gap) {
                    if (minimumGap != gap) {
                        minimumGapFramesIndex.clear();
                    }
                    minimumGap = gap;
                    minimumGapFramesIndex.add(index);
                }
            }
        }
        return minimumGapFramesIndex;
    }

    private List<Integer> getFramesIndexClosestToCenter(DisplayManager.VideoRect yuvRect, List<Integer> framesIndex) {
        int centerY = ((yuvRect.pxBottom - yuvRect.pxTop) / 2) + yuvRect.pxTop;
        int centerX = ((yuvRect.pxRight - yuvRect.pxLeft) / 2) + yuvRect.pxLeft;
        int minimumDistanceToCenter = Integer.MAX_VALUE;
        List<Integer> framesIndexClosestToCenter = new ArrayList<>();
        for (Integer index : framesIndex) {
            boolean isEnableOtherFrame = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index.intValue());
            if (isEnableOtherFrame) {
                View frame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index.intValue());
                int yGap = Math.abs(centerY - (frame.getTop() + (frame.getHeight() / 2)));
                int xGap = Math.abs(centerX - (frame.getLeft() + (frame.getWidth() / 2)));
                int distance = (int) (Math.pow(xGap, 2.0d) + Math.pow(yGap, 2.0d));
                if (minimumDistanceToCenter >= distance) {
                    if (minimumDistanceToCenter != distance) {
                        framesIndexClosestToCenter.clear();
                    }
                    minimumDistanceToCenter = distance;
                    framesIndexClosestToCenter.add(index);
                }
            }
        }
        return framesIndexClosestToCenter;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0044  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.util.List<java.lang.Integer> getFarthestPointFramesIndex(int r11, java.util.List<java.lang.Integer> r12) {
        /*
            r10 = this;
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r8 = 3
            if (r11 == r8) goto Lb
            r8 = 2
            if (r11 != r8) goto L4c
        Lb:
            r7 = 1
        Lc:
            if (r7 == 0) goto L4e
            r1 = 2147483647(0x7fffffff, float:NaN)
        L11:
            java.util.Iterator r2 = r12.iterator()
        L15:
            boolean r8 = r2.hasNext()
            if (r8 == 0) goto L6e
            java.lang.Object r3 = r2.next()
            java.lang.Integer r3 = (java.lang.Integer) r3
            com.sony.imaging.app.base.shooting.widget.AFFramePhaseDiffRectInfo r8 = r10.mAFLocalRectInfo
            int r9 = r3.intValue()
            boolean r4 = r8.isAFFrameEnableAtIndex(r9)
            if (r4 == 0) goto L15
            com.sony.imaging.app.base.shooting.widget.AFFramePhaseDiffRectInfo r8 = r10.mAFLocalRectInfo
            int r9 = r3.intValue()
            com.sony.imaging.app.base.shooting.widget.AFFramePhaseDiff r0 = r8.getAFFramePhaseDiffFactorAtIndex(r9)
            r8 = 3
            if (r11 != r8) goto L51
            int r6 = r0.getTop()
        L3e:
            if (r7 == 0) goto L6b
            if (r6 > r1) goto L15
        L42:
            if (r1 == r6) goto L47
            r5.clear()
        L47:
            r1 = r6
            r5.add(r3)
            goto L15
        L4c:
            r7 = 0
            goto Lc
        L4e:
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            goto L11
        L51:
            r8 = 4
            if (r11 != r8) goto L59
            int r6 = r0.getBottom()
            goto L3e
        L59:
            r8 = 2
            if (r11 != r8) goto L61
            int r6 = r0.getLeft()
            goto L3e
        L61:
            r8 = 1
            if (r11 != r8) goto L69
            int r6 = r0.getRight()
            goto L3e
        L69:
            r6 = 0
            goto L3e
        L6b:
            if (r6 < r1) goto L15
            goto L42
        L6e:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.base.shooting.widget.AFLocalPositionSetting.getFarthestPointFramesIndex(int, java.util.List):java.util.List");
    }

    protected int getNextFrameIndex(int keyDirection, int currentFrameIndex) {
        int ret;
        int i;
        int i2;
        if (this.mLensInfo == null) {
            return this.mFocusReadyFrameIndex;
        }
        List<Integer> framesIndex = new ArrayList<>();
        CameraEx.FocusAreaRectInfo[] arr$ = this.mAreaInfo.rectInfos;
        for (CameraEx.FocusAreaRectInfo i3 : arr$) {
            if (i3.index != 0) {
                framesIndex.add(Integer.valueOf(i3.index));
            }
        }
        if (keyDirection == 5 || keyDirection == 6) {
            if (FocusAreaController.PHASE_SHIFT_SENSOR_15POINT.equals(this.mLensInfo.PhaseShiftSensor)) {
                if (this.mFocusAreaController.isDigitalZoomMagAFFrameNumberChangeTo11()) {
                    int i4 = 0;
                    while (i4 < DIAL_ORDER_GPMA2_11.length && DIAL_ORDER_GPMA2_11[i4] != currentFrameIndex) {
                        i4++;
                    }
                    if (keyDirection == 5) {
                        i2 = i4 + 1 == DIAL_ORDER_GPMA2_11.length ? 0 : i4 + 1;
                    } else {
                        i2 = i4 + (-1) < 0 ? DIAL_ORDER_GPMA2_11.length - 1 : i4 - 1;
                    }
                    ret = DIAL_ORDER_GPMA2_11[i2];
                } else {
                    int i5 = 0;
                    while (i5 < DIAL_ORDER_GPMA2_15.length && DIAL_ORDER_GPMA2_15[i5] != currentFrameIndex) {
                        i5++;
                    }
                    if (keyDirection == 5) {
                        i = i5 + 1 == DIAL_ORDER_GPMA2_15.length ? 0 : i5 + 1;
                    } else {
                        i = i5 + (-1) < 0 ? DIAL_ORDER_GPMA2_15.length - 1 : i5 - 1;
                    }
                    ret = DIAL_ORDER_GPMA2_15[i];
                }
            } else {
                List<Integer> tmpList = getMinimumDistanceFramesIndex(currentFrameIndex, getGapFrameIndex(currentFrameIndex, getDirectionFramesIndex(keyDirection == 5 ? 1 : 2, currentFrameIndex, framesIndex), 0, 1));
                if (tmpList.size() >= 1) {
                    if (tmpList.size() != 1) {
                        Log.e(TAG, "Cannot specify index");
                    }
                    ret = tmpList.get(0).intValue();
                } else {
                    List<Integer> tmpList2 = getFarthestPointFramesIndex(keyDirection == 5 ? 2 : 1, getGapFrameIndex(currentFrameIndex, getDirectionFramesIndex(keyDirection == 5 ? 4 : 3, currentFrameIndex, framesIndex), -1, 1));
                    if (tmpList2.size() >= 1) {
                        if (tmpList2.size() != 1) {
                            Log.e(TAG, "Cannot specify index");
                        }
                    } else {
                        tmpList2 = getFarthestPointFramesIndex(keyDirection == 5 ? 3 : 4, framesIndex);
                    }
                    ret = tmpList2.get(0).intValue();
                }
            }
            return ret;
        }
        List<Integer> tmpList3 = getDirectionFramesIndex(keyDirection, currentFrameIndex, framesIndex);
        if (tmpList3.size() == 1) {
            return tmpList3.get(0).intValue();
        }
        if (tmpList3.size() != 0) {
            List<Integer> tmpList4 = getUnfairMinimumGapFrameIndex(currentFrameIndex, tmpList3, (keyDirection == 4 || keyDirection == 3) ? 2 : 1, (keyDirection == 4 || keyDirection == 3) ? this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(currentFrameIndex).getWidth() : this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(currentFrameIndex).getHeight());
            if (tmpList4.size() == 1) {
                return tmpList4.get(0).intValue();
            }
            if (tmpList4.size() != 0) {
                DisplayManager.VideoRect yuvRect = (DisplayManager.VideoRect) this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
                List<Integer> tmpList5 = getMinimumDistanceFramesIndexClosestToCenter(yuvRect, currentFrameIndex, tmpList4, (keyDirection == 4 || keyDirection == 3) ? 2 : 1);
                if (tmpList5.size() >= 1) {
                    if (tmpList5.size() != 1) {
                        Log.w(TAG, LOG_UNKNOWN);
                    }
                    return tmpList5.get(0).intValue();
                }
                Log.w(TAG, "Cannot specify index");
                return currentFrameIndex;
            }
            return currentFrameIndex;
        }
        return currentFrameIndex;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, LOG_DISPATCHKEYEVENT);
        if (event.getAction() != 0) {
            return false;
        }
        boolean ret = onKeyDown(event.getKeyCode(), event);
        return ret;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mInnerKeyHandler.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        Log.d(LOG_AFFLEXIBLEPOSITIONSETTING, LOG_ONKEYDOWN);
        int device = this.mDisplayModeNotifier.getActiveDevice();
        DisplayManager.DeviceStatus deviceStatus = this.mDisplayModeNotifier.getActiveDeviceStatus();
        boolean ret = false;
        if (device == -1) {
            device = 0;
        }
        int panelViewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
        CustomizableFunction customFunc = (CustomizableFunction) func;
        int scanCode = event.getScanCode();
        switch (customFunc) {
            case MainPrev:
            case SubPrev:
            case ThirdPrev:
                if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                    moveToClockWise();
                } else {
                    moveToAntiClockWise();
                }
                ret = true;
                break;
            case MainNext:
            case SubNext:
            case ThirdNext:
                if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                    moveToAntiClockWise();
                } else {
                    moveToClockWise();
                }
                ret = true;
                break;
            case Reset:
                setNeutralPosition();
                ret = true;
                break;
            default:
                switch (scanCode) {
                    case AppRoot.USER_KEYCODE.UP /* 103 */:
                        moveToUp();
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                        if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                            moveToRight();
                        } else {
                            moveToLeft();
                        }
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                        if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                            moveToLeft();
                        } else {
                            moveToRight();
                        }
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                        moveToDown();
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.RIGHT_UP /* 591 */:
                        if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                            moveToLeftUp();
                        } else {
                            moveToRightUp();
                        }
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.RIGHT_DOWN /* 592 */:
                        if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                            moveToLeftDown();
                        } else {
                            moveToRightDown();
                        }
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.LEFT_UP /* 593 */:
                        if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                            moveToRightUp();
                        } else {
                            moveToLeftUp();
                        }
                        ret = true;
                        break;
                    case AppRoot.USER_KEYCODE.LEFT_DOWN /* 594 */:
                        if (device == 0 && (panelViewPattern == 1 || panelViewPattern == 4)) {
                            moveToRightDown();
                        } else {
                            moveToLeftDown();
                        }
                        ret = true;
                        break;
                }
        }
        return ret ? 1 : 0;
    }

    private void setNeutralPosition() {
        if (mNeutralIndex == -1) {
            List<Integer> framesIndex = new ArrayList<>();
            CameraEx.FocusAreaRectInfo[] arr$ = this.mAreaInfo.rectInfos;
            for (CameraEx.FocusAreaRectInfo i : arr$) {
                if (i.index != 0) {
                    framesIndex.add(Integer.valueOf(i.index));
                }
            }
            DisplayManager.VideoRect yuvRect = (DisplayManager.VideoRect) this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
            List<Integer> list = getFramesIndexClosestToCenter(yuvRect, framesIndex);
            if (list.size() == 1) {
                mNeutralIndex = list.get(0).intValue();
            } else {
                Log.e(TAG, LOG_CANNOT_SPECIFY_NEUTRAL_POS);
            }
        }
        if (this.mFocusReadyFrameIndex != mNeutralIndex) {
            moveExecution(mNeutralIndex);
        }
    }

    private void moveToClockWise() {
        int nextIndex = getNextFrameIndex(5, this.mFocusReadyFrameIndex);
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToAntiClockWise() {
        int nextIndex = getNextFrameIndex(6, this.mFocusReadyFrameIndex);
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToRight() {
        int nextIndex = getNextFrameIndex(1, this.mFocusReadyFrameIndex);
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToLeft() {
        int nextIndex = getNextFrameIndex(2, this.mFocusReadyFrameIndex);
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToDown() {
        int nextIndex = getNextFrameIndex(4, this.mFocusReadyFrameIndex);
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToUp() {
        int nextIndex = getNextFrameIndex(3, this.mFocusReadyFrameIndex);
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToRightUp() {
        int nextIndex = getNextFrameIndex(3, getNextFrameIndex(1, this.mFocusReadyFrameIndex));
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToRightDown() {
        int nextIndex = getNextFrameIndex(4, getNextFrameIndex(1, this.mFocusReadyFrameIndex));
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToLeftUp() {
        int nextIndex = getNextFrameIndex(3, getNextFrameIndex(2, this.mFocusReadyFrameIndex));
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveToLeftDown() {
        int nextIndex = getNextFrameIndex(4, getNextFrameIndex(2, this.mFocusReadyFrameIndex));
        if (nextIndex != this.mFocusReadyFrameIndex) {
            moveExecution(nextIndex);
        }
    }

    private void moveExecution(int nextIndex) {
        this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(this.mFocusReadyFrameIndex).setUnSelected();
        this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(nextIndex).setSelected();
        this.mFocusReadyFrameIndex = nextIndex;
        this.mListener.onPositionChanged(nextIndex);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AFLocal, com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onChangeYUV(DisplayManager.VideoRect rect) {
        updateRect(rect);
    }

    private void updateRect(DisplayManager.VideoRect videoRect) {
        this.mAreaInfo = FocusAreaController.getInstance().getCurrentFocusAreaInfos(getAfAreaMode());
        if (this.mAreaInfo != null) {
            boolean isPF11Over = false;
            if (1 <= CameraSetting.getPfApiVersion()) {
                isPF11Over = true;
            }
            CameraEx.FocusAreaRectInfo[] arr$ = this.mAreaInfo.rectInfos;
            for (CameraEx.FocusAreaRectInfo rectInfo : arr$) {
                Rect yuvFocusRect = convertScalartoOSD(videoRect, rectInfo.rect);
                if (rectInfo.index != 0) {
                    AFFramePhaseDiff f = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(rectInfo.index);
                    if (f == null) {
                        AFFramePhaseDiff f2 = (AFFramePhaseDiff) inflate(getContext(), R.layout.af_phase_diff_w34h34, null);
                        addView(f2, new RelativeLayout.LayoutParams(yuvFocusRect.right - yuvFocusRect.left, yuvFocusRect.bottom - yuvFocusRect.top));
                        f2.setFocusRect(yuvFocusRect);
                        this.mAFLocalRectInfo.putAFFramePhaseDiffFactorAtIndex(rectInfo.index, f2);
                    } else {
                        f.setFocusRect(yuvFocusRect);
                        this.mAFLocalRectInfo.putAFFramePhaseDiffFactorAtIndex(rectInfo.index, f);
                    }
                    boolean enable = true;
                    if (isPF11Over) {
                        enable = rectInfo.enable;
                    }
                    this.mAFLocalRectInfo.putAFFrameEnableAtIndex(rectInfo.index, enable);
                }
            }
        }
    }
}
