package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.widget.FrameLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusAreaController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.avio.DisplayManager;
import java.util.HashMap;

/* loaded from: classes.dex */
public class AFViews extends FrameLayout implements NotificationListener {
    private static final String LOG_CANNOT_FIND_LAYOUTID = "Cannot find layoutId. Not change.";
    private static final String LOG_UNKNOWN_FOCUSAREA = "Unknown FocusArea. Invisible FocusView.";
    private static final String TAG = "AFViews";
    private static final String[] TAGS;
    private static final HashMap<String, Integer> VIEWS_LAYOUT = new HashMap<>();
    private CameraNotificationManager mCamNtfy;
    private CameraSetting mCamSet;
    private AbstractAFView mCurrentAFView;
    private FocusAreaController mFocusAreaController;
    private FocusModeController mFocusModeController;
    private SparseArray<AbstractAFView> mViews;

    static {
        VIEWS_LAYOUT.put(FocusAreaController.CENTER_WEIGHTED, Integer.valueOf(R.layout.af_center_weighted));
        VIEWS_LAYOUT.put(FocusAreaController.FIX_CENTER, Integer.valueOf(R.layout.af_fix_to_center));
        VIEWS_LAYOUT.put(FocusAreaController.FLEX, Integer.valueOf(R.layout.af_flexible_spot));
        VIEWS_LAYOUT.put(FocusAreaController.LOCAL, Integer.valueOf(R.layout.af_local));
        VIEWS_LAYOUT.put(FocusAreaController.MULTI, Integer.valueOf(R.layout.af_multi));
        VIEWS_LAYOUT.put(FocusAreaController.WIDE, Integer.valueOf(R.layout.af_wide));
        VIEWS_LAYOUT.put(FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME, Integer.valueOf(R.layout.af_fixed_to_illuminator_frame));
        TAGS = new String[]{CameraNotificationManager.AUTO_FOCUS_AREA, CameraNotificationManager.FOCUS_CHANGE, CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.DEVICE_LENS_CHANGED};
    }

    public AFViews(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mViews = new SparseArray<>();
        this.mCamNtfy = CameraNotificationManager.getInstance();
        this.mFocusAreaController = FocusAreaController.getInstance();
        this.mFocusModeController = FocusModeController.getInstance();
        this.mCamSet = CameraSetting.getInstance();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mCamNtfy.setNotificationListener(this);
        changeAFAreaView();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mCamNtfy.removeNotificationListener(this);
    }

    private AbstractAFView getAFView(Integer layoutId) {
        AbstractAFView view = this.mViews.get(layoutId.intValue());
        if (view == null) {
            AbstractAFView view2 = (AbstractAFView) inflate(getContext(), layoutId.intValue(), null);
            this.mViews.append(layoutId.intValue(), view2);
            return view2;
        }
        return view;
    }

    private void changeAF(String value) {
        if (value != null) {
            Integer layoutId = VIEWS_LAYOUT.get(value);
            if (layoutId == null) {
                Log.e(TAG, LOG_CANNOT_FIND_LAYOUTID);
                return;
            }
            AbstractAFView afViewAfter = getAFView(layoutId);
            if (afViewAfter != null && afViewAfter != this.mCurrentAFView) {
                removeView(this.mCurrentAFView);
                this.mCurrentAFView = afViewAfter;
                addView(this.mCurrentAFView);
                return;
            }
            return;
        }
        removeAllViews();
        this.mCurrentAFView = null;
    }

    private void changeAFAreaView() {
        String focusMode = this.mFocusModeController.getValue();
        CameraEx.LensInfo lensinfo = this.mCamSet.getLensInfo();
        int sensorType = this.mFocusAreaController.getSensorType();
        if (lensinfo == null || focusMode == null) {
            changeAF(null);
            return;
        }
        try {
            String afArea = this.mFocusAreaController.getValue();
            DisplayManager.DeviceStatus deviceStatus = DisplayModeObserver.getInstance().getActiveDeviceStatus();
            int viewPattern = deviceStatus == null ? 4 : deviceStatus.viewPattern;
            CameraEx.FocusAreaInfos fais = this.mCamSet.getFocusAreaRectInfos(afArea, -1, viewPattern);
            if (fais == null) {
                changeAF(null);
                return;
            }
            if (CameraSetting.getPfApiVersion() < 2) {
                if (focusMode.equals(FocusModeController.MANUAL)) {
                    changeAF(null);
                    return;
                }
                if (sensorType == 1 && this.mFocusAreaController.isDigitalZoomMagOverAFAreaChangeToCenter() && !FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME.equals(afArea)) {
                    afArea = FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME;
                }
                changeAF(afArea);
                return;
            }
            if (2 <= CameraSetting.getPfApiVersion()) {
                if (sensorType == 1) {
                    if (FocusModeController.MANUAL.equals(focusMode)) {
                        changeAF(null);
                        return;
                    }
                    if (this.mFocusAreaController.isDigitalZoomMagOverAFAreaChangeToCenter() && !FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME.equals(afArea)) {
                        afArea = FocusAreaController.AF_FIXED_TO_ILLUINATOR_FRAME;
                    }
                    changeAF(afArea);
                    return;
                }
                if (sensorType == 2) {
                    if (FocusModeController.MANUAL.equals(focusMode)) {
                        changeAF(null);
                        return;
                    } else {
                        changeAF(afArea);
                        return;
                    }
                }
                changeAF(null);
                return;
            }
            changeAF(null);
        } catch (IController.NotSupportedException e) {
            changeAF(null);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (CameraNotificationManager.FOCUS_CHANGE.equals(tag) || CameraNotificationManager.AUTO_FOCUS_AREA.equals(tag) || CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag)) {
            changeAFAreaView();
        }
    }
}
