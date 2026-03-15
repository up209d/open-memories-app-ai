package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.KeyStatus;
import com.sony.scalar.sysutil.ScalarInput;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AFLocal extends AbstractAFView {
    protected static final String LOG_CANNOT_SPECIFY_INDEX = "Cannot specify index";
    private static final String LOG_INVALID_FOCUS = "Invalid focus frame index. Sets mFocusReadyFrameIndex to 1";
    private static final String TAG = "AFLocal";
    protected AFFramePhaseDiffRectInfo mAFLocalRectInfo;
    protected CameraEx.FocusAreaInfos mAreaInfo;
    protected int mFocusReadyFrameIndex;
    private int[] mIndex;

    public AFLocal(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAFLocalRectInfo = null;
        this.mIndex = null;
        this.mAFLocalRectInfo = new AFFramePhaseDiffRectInfo();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
        updateRect((DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow");
        this.mIndex = null;
        super.onDetachedFromWindow();
    }

    protected int getEnableClosestFrameIndex(int currentFrameIndex) {
        List<Integer> framesIndexList = new ArrayList<>();
        for (int i = 0; i < this.mAFLocalRectInfo.getSize(); i++) {
            int frameIndex = this.mAFLocalRectInfo.getIndexOfEnableAFFrameAtOrder(i);
            if (frameIndex != currentFrameIndex) {
                boolean isEnable = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(frameIndex);
                if (isEnable) {
                    framesIndexList.add(Integer.valueOf(frameIndex));
                }
            }
        }
        List<Integer> closeFrameIndexlist = getMinimumDistanceFramesIndex(currentFrameIndex, framesIndexList);
        if (closeFrameIndexlist.size() == 1) {
            return closeFrameIndexlist.get(0).intValue();
        }
        Log.w(TAG, LOG_CANNOT_SPECIFY_INDEX);
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Integer> getMinimumDistanceFramesIndex(int currentFrameIndex, List<Integer> framesIndex) {
        AFFramePhaseDiff currentFrame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(currentFrameIndex);
        ViewGroup.MarginLayoutParams currentParams = (ViewGroup.MarginLayoutParams) currentFrame.getLayoutParams();
        int minimumDistance = Integer.MAX_VALUE;
        List<Integer> minimumDistanceFramesIndex = new ArrayList<>();
        for (Integer index : framesIndex) {
            boolean isEnableOtherFrame = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index.intValue());
            if (isEnableOtherFrame) {
                View frame = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(index.intValue());
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) frame.getLayoutParams();
                int xGap = Math.abs(currentParams.leftMargin - params.leftMargin);
                int yGap = Math.abs(currentParams.topMargin - params.topMargin);
                int distance = (int) (Math.pow(xGap, 2.0d) + Math.pow(yGap, 2.0d));
                if (minimumDistance >= distance) {
                    if (minimumDistance != distance) {
                        minimumDistanceFramesIndex.clear();
                    }
                    minimumDistance = distance;
                    minimumDistanceFramesIndex.add(index);
                }
            }
        }
        return minimumDistanceFramesIndex;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onChangeYUV(DisplayManager.VideoRect rect) {
        Log.d(TAG, "onChangeYUV");
        super.onChangeYUV(rect);
        updateRect(rect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFocusAreaInfoChanged(DisplayManager.VideoRect videoRect) {
        Log.d(TAG, "onFocusAreaInfoChanged");
        super.onFocusAreaInfoChanged(videoRect);
        updateRect(videoRect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onStartAutoFocus(boolean illuminator) {
        Log.d(TAG, "onStartAutoFocus");
        this.mIndex = null;
        super.onStartAutoFocus(illuminator);
        if (2 == CameraSetting.getInstance().getCurrentMode()) {
            setAllFrameUnFocus();
        } else {
            setAllFrameUnFocus();
            setAllFrameInvisible();
        }
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        if (1 == status.status) {
            setAllFrameInvisible();
        }
        this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(this.mFocusReadyFrameIndex).setOnFocusReady();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFocused(int[] index, boolean illuminator) {
        Log.d(TAG, "onFocused");
        if (2 == CameraSetting.getInstance().getCurrentMode()) {
            setAllFrameUnFocus();
        } else {
            setAllFrameInvisible();
        }
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        if (1 == status.status) {
            setAllFrameInvisible();
        }
        this.mIndex = index;
        for (int frameIndex : index) {
            boolean isEnable = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(frameIndex);
            if (isEnable) {
                AFFramePhaseDiff f = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(frameIndex);
                if (f != null) {
                    f.setOnFocus();
                } else {
                    Log.e(TAG, "Unknown index.");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onLockWarm(boolean illuminator) {
        Log.d(TAG, "onLockWarm");
        this.mIndex = null;
        if (2 == CameraSetting.getInstance().getCurrentMode()) {
            setAllFrameUnFocus();
        } else {
            setAllFrameInvisible();
        }
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        if (1 == status.status) {
            setAllFrameInvisible();
        }
        this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(this.mFocusReadyFrameIndex).setOnFocusReady();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onContinuous(int[] index, boolean illuminator) {
        Log.d(TAG, "onContinuous");
        super.onContinuous(index, illuminator);
        if (2 == CameraSetting.getInstance().getCurrentMode()) {
            setAllFrameUnFocus();
        } else {
            setAllFrameInvisible();
        }
        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
        if (1 == status.status) {
            setAllFrameInvisible();
        }
        for (int frameIndex : index) {
            if (this.mAFLocalRectInfo.isAFFrameEnableAtIndex(frameIndex)) {
                this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(frameIndex).setOnFocus();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onNeutral(boolean illuminator) {
        Log.d(TAG, "onNeutral");
        setAllFrameUnFocus();
        this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(this.mFocusReadyFrameIndex).setOnFocusReady();
        this.mIndex = null;
    }

    private void setAllFrameInvisible() {
        int size = this.mAFLocalRectInfo.getSize();
        for (int i = 0; i < size; i++) {
            if (this.mAFLocalRectInfo.isAFFrameEnableAtOrder(i)) {
                this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtOrder(i).setInvisible();
            }
        }
    }

    private void setAllFrameUnFocus() {
        int size = this.mAFLocalRectInfo.getSize();
        for (int i = 0; i < size; i++) {
            if (this.mAFLocalRectInfo.isAFFrameEnableAtOrder(i)) {
                this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtOrder(i).setUnFocus();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public String getAfAreaMode() {
        return FocusAreaController.LOCAL;
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
                        f = (AFFramePhaseDiff) inflate(getContext(), R.layout.af_phase_diff_w34h34, null);
                        addView(f, new RelativeLayout.LayoutParams(yuvFocusRect.right - yuvFocusRect.left, yuvFocusRect.bottom - yuvFocusRect.top));
                        f.setFocusRect(yuvFocusRect);
                        this.mAFLocalRectInfo.putAFFramePhaseDiffFactorAtIndex(rectInfo.index, f);
                    } else {
                        f.setFocusRect(yuvFocusRect);
                        this.mAFLocalRectInfo.putAFFramePhaseDiffFactorAtIndex(rectInfo.index, f);
                    }
                    boolean enable = true;
                    if (isPF11Over) {
                        enable = rectInfo.enable;
                    }
                    if (enable) {
                        KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
                        if (1 == status.status) {
                            f.setInvisible();
                        } else {
                            f.setUnFocus();
                        }
                    } else {
                        f.setInvisible();
                    }
                    this.mAFLocalRectInfo.putAFFrameEnableAtIndex(rectInfo.index, enable);
                }
            }
        }
        this.mFocusReadyFrameIndex = FocusAreaController.getInstance().getFocusIndex();
        if (!this.mAFLocalRectInfo.isAFFrameEnableAtIndex(this.mFocusReadyFrameIndex)) {
            int index = this.mFocusReadyFrameIndex;
            int frameNum = 0;
            while (true) {
                if (frameNum >= this.mAFLocalRectInfo.getSize()) {
                    break;
                }
                index = getEnableClosestFrameIndex(index);
                boolean isEnable = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(index);
                if (!isEnable) {
                    frameNum++;
                } else {
                    FocusAreaController.getInstance().setFocusIndex(index);
                    this.mFocusReadyFrameIndex = FocusAreaController.getInstance().getFocusIndex();
                    break;
                }
            }
        }
        if (this.mFocusReadyFrameIndex < 1) {
            Log.e(TAG, LOG_INVALID_FOCUS);
            this.mFocusReadyFrameIndex = 1;
        }
        this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(this.mFocusReadyFrameIndex).setOnFocusReady();
        if (this.mIndex != null) {
            int[] arr$2 = this.mIndex;
            for (int frameIndex : arr$2) {
                boolean isEnable2 = this.mAFLocalRectInfo.isAFFrameEnableAtIndex(frameIndex);
                if (isEnable2) {
                    AFFramePhaseDiff f2 = this.mAFLocalRectInfo.getAFFramePhaseDiffFactorAtIndex(frameIndex);
                    if (f2 != null) {
                        f2.setOnFocus();
                    } else {
                        Log.e(TAG, "Unknown index.");
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFaceLocked() {
        super.onFaceLocked();
        setAllFrameInvisible();
    }
}
