package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class AFFlexible extends AbstractAFView {
    protected static final int FLEXIBLE_FRAME_INFO_INDEX = 2;
    protected static final int FLEXIBLE_MOVE_AREA_INDEX = 1;
    protected AFFrame mFrame;

    public AFFlexible(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFrame = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFocused(int[] index, boolean illuminator) {
        super.onFocused(index, illuminator);
        if (illuminator) {
            this.mFrame.setInvisible();
        } else {
            this.mFrame.setOnFocus();
        }
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
        this.mFrame.setUnFocus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onLockWarm(boolean illuminator) {
        super.onLockWarm(illuminator);
        this.mFrame.setUnFocus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateFrame((DisplayManager.VideoRect) this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
        this.mFrame.setUnFocus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onChangeYUV(DisplayManager.VideoRect rect) {
        super.onChangeYUV(rect);
        updateFrame(rect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFocusAreaInfoChanged(DisplayManager.VideoRect rect) {
        super.onFocusAreaInfoChanged(rect);
        updateFrame(rect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onStartAutoFocus(boolean illuminator) {
        super.onStartAutoFocus(illuminator);
        if (illuminator) {
            this.mFrame.setInvisible();
        } else {
            this.mFrame.setUnFocus();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected String getAfAreaMode() {
        return "flex-spot";
    }

    private void updateFrame(DisplayManager.VideoRect rect) {
        CameraEx.FocusAreaInfos areaInfo = FocusAreaController.getInstance().getCurrentFocusAreaInfos(getAfAreaMode());
        if (areaInfo != null) {
            Pair<Integer, Integer> fPoint = FocusAreaController.getInstance().getFocusPoint();
            Rect areaRect = areaInfo.rectInfos[1].rect;
            Rect frameRect = areaInfo.rectInfos[2].rect;
            int scalarWidth = frameRect.right - frameRect.left;
            int scalarHeight = frameRect.bottom - frameRect.top;
            int scalarLeft = ((Integer) fPoint.first).intValue() - (scalarWidth / 2);
            int scalarRight = scalarLeft + scalarWidth;
            int scalarTop = ((Integer) fPoint.second).intValue() - (scalarHeight / 2);
            int scalarBottom = scalarTop + scalarHeight;
            if (scalarLeft < areaRect.left) {
                scalarLeft = areaRect.left;
                scalarRight = scalarLeft + scalarWidth;
            }
            if (scalarTop < areaRect.top) {
                scalarTop = areaRect.top;
                scalarBottom = scalarTop + scalarHeight;
            }
            if (scalarRight > areaRect.right) {
                scalarRight = areaRect.right;
                scalarLeft = scalarRight - scalarWidth;
            }
            if (scalarBottom > areaRect.bottom) {
                scalarBottom = areaRect.bottom;
                scalarTop = scalarBottom - scalarHeight;
            }
            Rect yuvFocusRect = convertScalartoOSD(rect, new Rect(scalarLeft, scalarTop, scalarRight, scalarBottom));
            if (this.mFrame == null) {
                this.mFrame = (AFFrame) inflate(getContext(), R.layout.af_contrast_frame, null);
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
