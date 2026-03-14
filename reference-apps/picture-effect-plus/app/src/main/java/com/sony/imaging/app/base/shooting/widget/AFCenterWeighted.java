package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class AFCenterWeighted extends AbstractAFView {
    private final ViewTreeObserver.OnPreDrawListener l;
    private AFFrame mFrame;

    public AFCenterWeighted(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFrame = null;
        this.l = new ViewTreeObserver.OnPreDrawListener() { // from class: com.sony.imaging.app.base.shooting.widget.AFCenterWeighted.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                AFCenterWeighted.this.switchVisibilityOnPreDraw();
                AFCenterWeighted.this.getViewTreeObserver().removeOnPreDrawListener(AFCenterWeighted.this.l);
                return true;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateRect((DisplayManager.VideoRect) this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
        getViewTreeObserver().addOnPreDrawListener(this.l);
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
    public void onZoomInfoChanged(DisplayManager.VideoRect videoRect, boolean illuminator) {
        super.onZoomInfoChanged(videoRect, illuminator);
        updateRect(videoRect);
        if (isAFFocused()) {
            switchFrameVisibility(1, illuminator);
        } else {
            switchFrameVisibility(2, illuminator);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFocused(int[] index, boolean illuminator) {
        super.onFocused(index, illuminator);
        switchFrameVisibility(1, illuminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onContinuous(int[] index, boolean illuminator) {
        super.onContinuous(index, illuminator);
        switchFrameVisibility(1, illuminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onNeutral(boolean illuminator) {
        super.onNeutral(illuminator);
        switchFrameVisibility(2, illuminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onStartAutoFocus(boolean illuminator) {
        super.onStartAutoFocus(illuminator);
        switchFrameVisibility(2, illuminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onLockWarm(boolean illuminator) {
        super.onLockWarm(illuminator);
        switchFrameVisibility(2, illuminator);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected String getAfAreaMode() {
        return FocusAreaController.CENTER_WEIGHTED;
    }

    private void updateRect(DisplayManager.VideoRect videoRect) {
        CameraEx.FocusAreaInfos areaInfos = FocusAreaController.getInstance().getCurrentFocusAreaInfos(getAfAreaMode());
        if (areaInfos != null) {
            Rect yuvFocusRect = convertScalartoOSD(videoRect, areaInfos.rectInfos[1].rect);
            if (this.mFrame == null) {
                this.mFrame = (AFFrame) inflate(getContext(), R.layout.af_contrast_frame, null);
                addView(this.mFrame);
            }
            this.mFrame.setFocusRect(yuvFocusRect);
        }
    }

    private void switchFrameVisibility(int status, boolean isShowIlluminator) {
        if (status == 1) {
            if (isShowIlluminator) {
                this.mFrame.setInvisible();
                return;
            } else {
                this.mFrame.setOnFocus();
                return;
            }
        }
        if (status == 2) {
            if (isShowIlluminator) {
                this.mFrame.setInvisible();
                return;
            } else {
                this.mFrame.setUnFocus();
                return;
            }
        }
        if (status == 3) {
            this.mFrame.setInvisible();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchVisibilityOnPreDraw() {
        if (isShowIlluminator()) {
            this.mFrame.setInvisible();
        } else {
            this.mFrame.setUnFocus();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFaceLocked() {
        super.onFaceLocked();
        this.mFrame.setInvisible();
    }
}
