package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class AFFixToCenter extends AbstractAFView {
    private AFFramePhaseDiff mFrame;

    public AFFixToCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFrame = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateRect((DisplayManager.VideoRect) this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
        this.mFrame.setOnFocusReady();
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
    public void onFocused(int[] index, boolean illuminator) {
        super.onFocused(index, illuminator);
        this.mFrame.setOnFocus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onContinuous(int[] index, boolean illuminator) {
        super.onContinuous(index, illuminator);
        this.mFrame.setOnFocus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onNeutral(boolean illuminator) {
        super.onNeutral(illuminator);
        this.mFrame.setOnFocusReady();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onStartAutoFocus(boolean illuminator) {
        super.onStartAutoFocus(illuminator);
        this.mFrame.setOnFocusReady();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onLockWarm(boolean illuminator) {
        super.onLockWarm(illuminator);
        this.mFrame.setOnFocusReady();
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected String getAfAreaMode() {
        return FocusAreaController.FIX_CENTER;
    }

    private void updateRect(DisplayManager.VideoRect videoRect) {
        CameraEx.FocusAreaInfos areaInfos = FocusAreaController.getInstance().getCurrentFocusAreaInfos(getAfAreaMode());
        if (areaInfos != null) {
            Rect yuvFocusRect = convertScalartoOSD(videoRect, areaInfos.rectInfos[1].rect);
            if (this.mFrame == null) {
                this.mFrame = (AFFramePhaseDiff) inflate(getContext(), R.layout.af_phase_diff_w34h34, null);
                addView(this.mFrame);
            }
            this.mFrame.setFocusRect(yuvFocusRect);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFaceLocked() {
        super.onFaceLocked();
        this.mFrame.setInvisible();
    }
}
