package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.widget.AbstractAFView;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AFMulti extends AbstractAFView {
    private static final String LOG_CANNOT_GET_FACE_DETECTION = "Cannot get face detection setting.";
    private static final String LOG_ERROR_UNKNOWN_INDEX = "Fatal Error:: Unknown index:";
    private static final String TAG = "AFMulti";
    private AFFrame mFocusArea;
    private SparseArray<AFFrame> mFrames;

    public AFMulti(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFrames = new SparseArray<>();
        this.mFocusArea = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFocusAreaVisibility() {
        try {
            String fdValue = FaceDetectionController.getInstance().getValue();
            if ("off".equals(fdValue)) {
                this.mFocusArea.setUnFocus();
            } else {
                this.mFocusArea.setInvisible();
            }
        } catch (IController.NotSupportedException e) {
            Log.w(TAG, LOG_CANNOT_GET_FACE_DETECTION);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected String getAfAreaMode() {
        return FocusAreaController.MULTI;
    }

    private void updateRect(DisplayManager.VideoRect videoRect) {
        CameraEx.FocusAreaInfos areaInfo = FocusAreaController.getInstance().getCurrentFocusAreaInfos(getAfAreaMode());
        if (areaInfo != null) {
            CameraEx.FocusAreaRectInfo[] arr$ = areaInfo.rectInfos;
            for (CameraEx.FocusAreaRectInfo rectInfo : arr$) {
                Rect yuvFocusRect = convertScalartoOSD(videoRect, rectInfo.rect);
                if (rectInfo.index == 0) {
                    if (this.mFocusArea == null) {
                        this.mFocusArea = (AFFrame) inflate(getContext(), R.layout.af_contrast_frame, null);
                        addView(this.mFocusArea);
                    }
                    this.mFocusArea.setFocusRect(yuvFocusRect);
                } else {
                    AFFrame f = this.mFrames.get(rectInfo.index);
                    if (f == null) {
                        AFFrame f2 = (AFFrame) inflate(getContext(), R.layout.af_contrast_frame, null);
                        this.mFrames.append(rectInfo.index, f2);
                        f2.setFocusRect(yuvFocusRect);
                        addView(f2);
                    } else {
                        f.setFocusRect(yuvFocusRect);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateRect((DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
        updateFocusAreaVisibility();
        setAllFrameInvisible();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
        this.mFocusArea.setInvisible();
        setAllFrameInvisible();
        for (int i : index) {
            AFFrame f = this.mFrames.get(i);
            if (f != null) {
                f.setOnFocus();
            } else {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                Log.e(TAG, builder.replace(0, LOG_ERROR_UNKNOWN_INDEX.length(), LOG_ERROR_UNKNOWN_INDEX).append(i).toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onContinuous(int[] index, boolean illuminator) {
        super.onContinuous(index, illuminator);
        setAllFrameInvisible();
        this.mFocusArea.setInvisible();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onNeutral(boolean illuminator) {
        super.onNeutral(illuminator);
        setAllFrameInvisible();
        updateFocusAreaVisibility();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onLockWarm(boolean illuminator) {
        super.onLockWarm(illuminator);
        setAllFrameInvisible();
        updateFocusAreaVisibility();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onStartAutoFocus(boolean illuminator) {
        super.onStartAutoFocus(illuminator);
        if (illuminator) {
            this.mFocusArea.setInvisible();
        } else {
            setAllFrameInvisible();
            updateFocusAreaVisibility();
        }
    }

    private void setAllFrameInvisible() {
        int size = this.mFrames.size();
        for (int i = 0; i < size; i++) {
            this.mFrames.valueAt(i).setInvisible();
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    protected AbstractAFView.CameraNotificationListener getCameraNotificationListener() {
        return new AFMultiCameraNotificationListener();
    }

    /* loaded from: classes.dex */
    protected class AFMultiCameraNotificationListener extends AbstractAFView.CameraNotificationListener {
        protected AFMultiCameraNotificationListener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView.CameraNotificationListener, com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            List<String> tags = new ArrayList<>();
            String[] arr$ = super.getTags();
            for (String s : arr$) {
                tags.add(s);
            }
            tags.add(CameraNotificationManager.FACE_DETECTION_MODE);
            return (String[]) tags.toArray(new String[0]);
        }

        @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView.CameraNotificationListener, com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            super.onNotify(tag);
            if (CameraNotificationManager.FACE_DETECTION_MODE.equals(tag)) {
                AFMulti.this.updateFocusAreaVisibility();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.AbstractAFView
    public void onFaceLocked() {
        super.onFaceLocked();
    }
}
