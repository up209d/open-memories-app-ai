package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public abstract class AbstractAFView extends RelativeLayout {
    protected static final int FOCUS_INVISIBLE = 3;
    protected static final int FOCUS_LOCK = 1;
    protected static final int FOCUS_UNLOCK = 2;
    protected static final int ILLUMINATOR_FOCUS_INFO_INDEX = 0;
    private static final String LOG_AREAINDEX = " areaIndex:";
    private static final String LOG_CANNOT_GET_FOCUSAREAINFOS = "Cannot get FocusAreaInfos";
    private static final String LOG_FOCUSLIGHTSTATUS_IS_NULL = "FocusLightStateInfo is null";
    private static final String LOG_ONCHANGEYUV = "onChangeYUV";
    private static final String LOG_ONCONTINUOUS = "onContinuous";
    private static final String LOG_ONFACELOCKED = "onFaceLocked";
    private static final String LOG_ONFOCUSAREAINFO_CHANGED = "onFocusAreaInfoChanged";
    private static final String LOG_ONFOCUSED = "onFocused";
    private static final String LOG_ONLOCKWARM = "onLockWarm";
    private static final String LOG_ONNEUTRAL = "onNeutral";
    private static final String LOG_ONSTARTAUTOFOCUS = "onStartAutoFocus";
    private static final String LOG_ONZOOMINFO_CHANGED = "onZoomInfoChanged";
    private static final String LOG_ON_ATTACHED_TO_WINDOW = "onAttachedToWindow";
    private static final String LOG_STATUS = " status:";
    protected static final double SCALAR_HEIGHT = 2000.0d;
    protected static final double SCALAR_WIDTH = 2000.0d;
    protected static final double SCALAR_X_MAX = 1000.0d;
    protected static final double SCALAR_X_MIN = -1000.0d;
    protected static final double SCALAR_Y_MAX = 1000.0d;
    protected static final double SCALAR_Y_MIN = -1000.0d;
    private static final String TAG = "AbstractAFView";
    private static StringBuilder builder = new StringBuilder();
    private Handler handler;
    private final ViewTreeObserver.OnPreDrawListener l;
    private NotificationListener mCameraListener;
    protected final CameraNotificationManager mCameraNotifier;
    private DigitalZoomController mDigitalZoomController;
    private NotificationListener mDisplayModeListener;
    protected final DisplayModeObserver mDisplayModeNotifier;
    private FocusAreaController mFocusAreaController;
    protected AFFrameIlluminator mFrameIlluminator;
    private boolean mIsFocusedAsIlluminator;

    protected abstract String getAfAreaMode();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class CameraNotificationListener implements NotificationListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public CameraNotificationListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return new String[]{CameraNotificationManager.START_AUTO_FOCUS, CameraNotificationManager.DONE_AUTO_FOCUS, CameraNotificationManager.AUTO_FOCUS_AREA, CameraNotificationManager.FOCUS_AREA_INFO, CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.FOCUS_POINT};
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.DONE_AUTO_FOCUS.equals(tag)) {
                CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) AbstractAFView.this.mCameraNotifier.getValue(tag);
                AbstractAFView.this.mIsFocusedAsIlluminator = false;
                if (onFocusInfo != null) {
                    switch (onFocusInfo.status) {
                        case 0:
                            AbstractAFView.this.onNeutral(AbstractAFView.this.isShowIlluminator());
                            return;
                        case 1:
                            if (onFocusInfo.area != null) {
                                int[] arr$ = onFocusInfo.area;
                                int len$ = arr$.length;
                                int i$ = 0;
                                while (true) {
                                    if (i$ < len$) {
                                        int index = arr$[i$];
                                        if (index == 0) {
                                            AbstractAFView.this.mIsFocusedAsIlluminator = true;
                                        } else {
                                            i$++;
                                        }
                                    }
                                }
                                AbstractAFView.this.onFocused(onFocusInfo.area, AbstractAFView.this.isShowIlluminator());
                                return;
                            }
                            AbstractAFView.this.onFaceLocked();
                            return;
                        case 2:
                            AbstractAFView.this.onLockWarm(AbstractAFView.this.isShowIlluminator());
                            return;
                        case 3:
                        default:
                            return;
                        case 4:
                            if (onFocusInfo.area != null) {
                                AbstractAFView.this.onContinuous(onFocusInfo.area, AbstractAFView.this.isShowIlluminator());
                                return;
                            } else {
                                int[] empty_array = new int[0];
                                AbstractAFView.this.onContinuous(empty_array, AbstractAFView.this.isShowIlluminator());
                                return;
                            }
                    }
                }
                return;
            }
            if (CameraNotificationManager.START_AUTO_FOCUS.equals(tag)) {
                CameraNotificationManager.FocusLightStateInfo info = (CameraNotificationManager.FocusLightStateInfo) AbstractAFView.this.mCameraNotifier.getValue(CameraNotificationManager.FOCUS_LIGHT_STATE);
                if (info != null) {
                    AbstractAFView.this.onStartAutoFocus(info.ready || AbstractAFView.this.isShowIlluminator());
                    return;
                } else {
                    Log.e(AbstractAFView.TAG, AbstractAFView.LOG_FOCUSLIGHTSTATUS_IS_NULL);
                    return;
                }
            }
            if (CameraNotificationManager.AUTO_FOCUS_AREA.equals(tag) || CameraNotificationManager.FOCUS_AREA_INFO.equals(tag)) {
                AbstractAFView.this.onFocusAreaInfoChanged((DisplayManager.VideoRect) AbstractAFView.this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
            } else if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag)) {
                AbstractAFView.this.onZoomInfoChanged((DisplayManager.VideoRect) AbstractAFView.this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE), AbstractAFView.this.isShowIlluminator());
            } else if (CameraNotificationManager.FOCUS_POINT.equals(tag)) {
                AbstractAFView.this.onChangeYUV((DisplayManager.VideoRect) AbstractAFView.this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFaceLocked() {
        Log.d(TAG, LOG_ONFACELOCKED);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFocused(int[] index, boolean illuminator) {
        Log.d(TAG, LOG_ONFOCUSED);
        switchIlluminatorVisibility(1, illuminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNeutral(boolean illuminator) {
        Log.d(TAG, LOG_ONNEUTRAL);
        switchIlluminatorVisibility(2, illuminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onContinuous(int[] index, boolean illuminator) {
        Log.d(TAG, LOG_ONCONTINUOUS);
        switchIlluminatorVisibility(1, illuminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onLockWarm(boolean illuminator) {
        Log.d(TAG, LOG_ONLOCKWARM);
        if (this.mFocusAreaController.getSensorType() == 1) {
            switchIlluminatorVisibility(2, illuminator);
        } else if (this.mFocusAreaController.getSensorType() == 2) {
            switchIlluminatorVisibility(3, illuminator);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onStartAutoFocus(boolean illuminator) {
        Log.d(TAG, LOG_ONSTARTAUTOFOCUS);
        switchIlluminatorVisibility(2, illuminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onChangeYUV(DisplayManager.VideoRect videoRect) {
        Log.d(TAG, LOG_ONCHANGEYUV);
        updateRect(videoRect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onZoomInfoChanged(DisplayManager.VideoRect videoRect, boolean illuminator) {
        Log.d(TAG, LOG_ONZOOMINFO_CHANGED);
        updateRect(videoRect);
        if (isAFFocused()) {
            switchIlluminatorVisibility(1, illuminator);
        } else {
            switchIlluminatorVisibility(2, illuminator);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFocusAreaInfoChanged(DisplayManager.VideoRect videoRect) {
        Log.d(TAG, LOG_ONFOCUSAREAINFO_CHANGED);
        updateRect(videoRect);
    }

    public AbstractAFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsFocusedAsIlluminator = false;
        this.mFrameIlluminator = null;
        this.l = new ViewTreeObserver.OnPreDrawListener() { // from class: com.sony.imaging.app.base.shooting.widget.AbstractAFView.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                AbstractAFView.this.switchIlluminatorVisibilityOnPreDraw();
                AbstractAFView.this.getViewTreeObserver().removeOnPreDrawListener(AbstractAFView.this.l);
                return true;
            }
        };
        this.mDisplayModeListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.AbstractAFView.2
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                AbstractAFView.this.onChangeYUV((DisplayManager.VideoRect) AbstractAFView.this.mDisplayModeNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return new String[]{DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE};
            }
        };
        this.handler = new Handler() { // from class: com.sony.imaging.app.base.shooting.widget.AbstractAFView.3
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                AbstractAFView.this.mCameraListener.onNotify(CameraNotificationManager.DONE_AUTO_FOCUS);
            }
        };
        this.mCameraNotifier = CameraNotificationManager.getInstance();
        this.mDisplayModeNotifier = DisplayModeObserver.getInstance();
        this.mCameraListener = getCameraNotificationListener();
        this.mFocusAreaController = FocusAreaController.getInstance();
        this.mDigitalZoomController = DigitalZoomController.getInstance();
    }

    private void updateRect(DisplayManager.VideoRect videoRect) {
        Rect yuvFocusRect;
        CameraEx.FocusAreaInfos info = FocusAreaController.getInstance().getCurrentFocusAreaInfos(getAfAreaMode());
        if (info != null) {
            yuvFocusRect = convertScalartoOSD(videoRect, info.rectInfos[0].rect);
        } else {
            yuvFocusRect = new Rect(0, 0, 0, 0);
        }
        if (this.mFrameIlluminator == null) {
            this.mFrameIlluminator = (AFFrameIlluminator) inflate(getContext(), R.layout.af_frame_illuminator, null);
            this.mFrameIlluminator.setFocusRect(yuvFocusRect);
            addView(this.mFrameIlluminator);
            return;
        }
        this.mFrameIlluminator.setFocusRect(yuvFocusRect);
    }

    protected CameraNotificationListener getCameraNotificationListener() {
        return new CameraNotificationListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        Log.d(TAG, LOG_ON_ATTACHED_TO_WINDOW);
        super.onAttachedToWindow();
        updateRect((DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE));
        this.mFrameIlluminator.setUnFocus();
        getViewTreeObserver().addOnPreDrawListener(this.l);
        this.mIsFocusedAsIlluminator = false;
        this.mCameraNotifier.setNotificationListener(this.mCameraListener);
        this.handler.sendEmptyMessage(0);
        this.mDisplayModeNotifier.setNotificationListener(this.mDisplayModeListener);
        DisplayModeObserver.getInstance().notifyViewPattern();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.handler.removeCallbacksAndMessages(null);
        this.mDisplayModeNotifier.removeNotificationListener(this.mDisplayModeListener);
        this.mCameraNotifier.removeNotificationListener(this.mCameraListener);
    }

    public static Rect convertScalartoOSD(DisplayManager.VideoRect yuvArea, Rect scalarRect) {
        int yuvAreaWidth = yuvArea.pxRight - yuvArea.pxLeft;
        int yuvAreaHeight = yuvArea.pxBottom - yuvArea.pxTop;
        double leftRate = (scalarRect.left - (-1000.0d)) / 2000.0d;
        int left = ((int) (yuvAreaWidth * leftRate)) + yuvArea.pxLeft;
        double topRate = (scalarRect.top - (-1000.0d)) / 2000.0d;
        int top = ((int) (yuvAreaHeight * topRate)) + yuvArea.pxTop;
        double rightRate = (scalarRect.right - (-1000.0d)) / 2000.0d;
        int right = ((int) (yuvAreaWidth * rightRate)) + yuvArea.pxLeft;
        double bottomRate = (scalarRect.bottom - (-1000.0d)) / 2000.0d;
        int bottom = ((int) (yuvAreaHeight * bottomRate)) + yuvArea.pxTop;
        return new Rect(left, top, right, bottom);
    }

    public static Rect convertImagertoYUV(DisplayManager.VideoRect yuvArea, Rect imagerFocusArea) {
        return convertScalartoOSD(yuvArea, imagerFocusArea);
    }

    public static Rect convertOSDtoScalar(Rect osdRect) {
        DisplayManager.VideoRect yuvArea = (DisplayManager.VideoRect) DisplayModeObserver.getInstance().getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        int yuvAreaWidth = yuvArea.pxRight - yuvArea.pxLeft;
        int yuvAreaHeight = yuvArea.pxBottom - yuvArea.pxTop;
        double hRate = 2000.0d / yuvAreaWidth;
        double vRate = 2000.0d / yuvAreaHeight;
        int left = (int) (((osdRect.left - yuvArea.pxLeft) * hRate) - 1000.0d);
        int top = (int) (((osdRect.top - yuvArea.pxTop) * vRate) - 1000.0d);
        int right = (int) (((osdRect.right - yuvArea.pxLeft) * hRate) - 1000.0d);
        int bottom = (int) (((osdRect.bottom - yuvArea.pxTop) * vRate) - 1000.0d);
        return new Rect(left, top, right, bottom);
    }

    public static Rect convertYUVtoImager(Rect yuvRect) {
        return convertOSDtoScalar(yuvRect);
    }

    private void switchIlluminatorVisibility(int status, boolean isShowIlluminator) {
        if (status == 1) {
            if (isShowIlluminator) {
                this.mFrameIlluminator.setOnFocus();
                return;
            } else {
                this.mFrameIlluminator.setInvisible();
                return;
            }
        }
        if (status == 2) {
            if (isShowIlluminator) {
                this.mFrameIlluminator.setUnFocus();
                return;
            } else {
                this.mFrameIlluminator.setInvisible();
                return;
            }
        }
        if (status == 3) {
            this.mFrameIlluminator.setInvisible();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isShowIlluminator() {
        if (this.mIsFocusedAsIlluminator) {
            return true;
        }
        if (this.mFocusAreaController.getSensorType() == 2) {
            if (!this.mDigitalZoomController.isDigitalZoomStatus()) {
                return false;
            }
            return true;
        }
        if (this.mFocusAreaController.getSensorType() != 1 || !this.mFocusAreaController.isDigitalZoomMagOverAFAreaChangeToCenter()) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchIlluminatorVisibilityOnPreDraw() {
        if (isShowIlluminator()) {
            if (isAFFocused()) {
                this.mFrameIlluminator.setOnFocus();
                return;
            } else {
                this.mFrameIlluminator.setUnFocus();
                return;
            }
        }
        this.mFrameIlluminator.setInvisible();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAFFocused() {
        CameraNotificationManager.OnFocusInfo onFocusInfo = (CameraNotificationManager.OnFocusInfo) this.mCameraNotifier.getValue(CameraNotificationManager.DONE_AUTO_FOCUS);
        return onFocusInfo != null && (onFocusInfo.status == 1 || onFocusInfo.status == 4);
    }
}
