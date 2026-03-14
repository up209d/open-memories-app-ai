package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
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

/* loaded from: classes.dex */
public class AFWide extends AbstractAFView {
    private static final String TAG = "AFWide";
    private AFFramePhaseDiffRectInfo mAFWideRectInfo;
    private int[] mIndex;

    public AFWide(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAFWideRectInfo = null;
        this.mIndex = null;
        this.mAFWideRectInfo = new AFFramePhaseDiffRectInfo();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFocused(int[] index, boolean illuminator) {
        super.onFocused(index, illuminator);
        if (2 == CameraSetting.getInstance().getCurrentMode()) {
            setAllFrameUnFocus();
        } else {
            setAllFrameInvisible();
        }
        this.mIndex = index;
        for (int frameIndex : index) {
            boolean isEnable = this.mAFWideRectInfo.isAFFrameEnableAtIndex(frameIndex);
            if (isEnable) {
                AFFramePhaseDiff f = this.mAFWideRectInfo.getAFFramePhaseDiffFactorAtIndex(frameIndex);
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
    public void onContinuous(int[] index, boolean illuminator) {
        super.onContinuous(index, illuminator);
        setAllFrameUnFocus();
        for (int frameIndex : index) {
            if (this.mAFWideRectInfo.isAFFrameEnableAtIndex(frameIndex)) {
                this.mAFWideRectInfo.getAFFramePhaseDiffFactorAtIndex(frameIndex).setOnFocus();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onNeutral(boolean illuminator) {
        super.onNeutral(illuminator);
        setAllFrameUnFocus();
        this.mIndex = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onChangeYUV(DisplayManager.VideoRect videoRect) {
        super.onChangeYUV(videoRect);
        updateRect(videoRect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFocusAreaInfoChanged(DisplayManager.VideoRect videoRect) {
        super.onFocusAreaInfoChanged(videoRect);
        updateRect(videoRect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onStartAutoFocus(boolean illuminator) {
        super.onStartAutoFocus(illuminator);
        if (illuminator) {
            setAllFrameInvisible();
        } else {
            setAllFrameUnFocus();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateRect((DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
        setAllFrameUnFocus();
    }

    private void setAllFrameUnFocus() {
        int size = this.mAFWideRectInfo.getSize();
        for (int i = 0; i < size; i++) {
            if (this.mAFWideRectInfo.isAFFrameEnableAtOrder(i)) {
                this.mAFWideRectInfo.getAFFramePhaseDiffFactorAtOrder(i).setUnFocus();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void setAllFrameInvisible() {
        int size = this.mAFWideRectInfo.getSize();
        for (int i = 0; i < size; i++) {
            if (this.mAFWideRectInfo.isAFFrameEnableAtOrder(i)) {
                this.mAFWideRectInfo.getAFFramePhaseDiffFactorAtOrder(i).setInvisible();
            }
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected String getAfAreaMode() {
        return FocusAreaController.WIDE;
    }

    private void updateRect(DisplayManager.VideoRect videoRect) {
        CameraEx.FocusAreaInfos areaInfo = FocusAreaController.getInstance().getCurrentFocusAreaInfos(getAfAreaMode());
        if (areaInfo != null) {
            boolean isPF11Over = false;
            if (1 <= CameraSetting.getPfApiVersion()) {
                isPF11Over = true;
            }
            CameraEx.FocusAreaRectInfo[] arr$ = areaInfo.rectInfos;
            for (CameraEx.FocusAreaRectInfo rectInfo : arr$) {
                Rect yuvFocusRect = convertScalartoOSD(videoRect, rectInfo.rect);
                if (rectInfo.index != 0) {
                    AFFramePhaseDiff f = this.mAFWideRectInfo.getAFFramePhaseDiffFactorAtIndex(rectInfo.index);
                    if (f == null) {
                        f = (AFFramePhaseDiff) inflate(getContext(), R.layout.af_phase_diff_w34h34, null);
                        this.mAFWideRectInfo.putAFFramePhaseDiffFactorAtIndex(rectInfo.index, f);
                        addView(f, new RelativeLayout.LayoutParams(yuvFocusRect.right - yuvFocusRect.left, yuvFocusRect.bottom - yuvFocusRect.top));
                    }
                    f.setFocusRect(yuvFocusRect);
                    boolean enable = true;
                    if (isPF11Over) {
                        enable = rectInfo.enable;
                    }
                    if (enable) {
                        if (2 == CameraSetting.getInstance().getCurrentMode()) {
                            f.setUnFocus();
                        } else {
                            KeyStatus status = ScalarInput.getKeyStatus(AppRoot.USER_KEYCODE.S1_ON);
                            if (1 == status.status) {
                                f.setInvisible();
                            } else {
                                f.setUnFocus();
                            }
                        }
                    } else {
                        f.setInvisible();
                    }
                    this.mAFWideRectInfo.putAFFrameEnableAtIndex(rectInfo.index, enable);
                }
            }
            if (this.mIndex != null) {
                int[] arr$2 = this.mIndex;
                for (int frameIndex : arr$2) {
                    boolean isEnable = this.mAFWideRectInfo.isAFFrameEnableAtIndex(frameIndex);
                    if (isEnable) {
                        AFFramePhaseDiff f2 = this.mAFWideRectInfo.getAFFramePhaseDiffFactorAtIndex(frameIndex);
                        if (f2 != null) {
                            f2.setOnFocus();
                        } else {
                            Log.e(TAG, "Unknown index.");
                        }
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
