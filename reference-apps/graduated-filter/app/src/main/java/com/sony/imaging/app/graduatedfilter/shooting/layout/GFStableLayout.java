package com.sony.imaging.app.graduatedfilter.shooting.layout;

import android.os.Looper;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.AntiHandBlurController;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.sa.GFHistgramTask;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.graduatedfilter.shooting.base.GFDisplayModeObserver;
import com.sony.imaging.app.graduatedfilter.shooting.widget.BorderView;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class GFStableLayout extends StableLayout implements NotificationListener {
    protected static final SparseArray<Integer> LAYOUT_LIST_FOR_FINDER;
    protected static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    private static String[] TAGS;
    private static final String TAG = AppLog.getClassName();
    protected static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();

    static {
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.shooting_main_sid_info_on_panel));
        LAYOUT_LIST_FOR_PANEL.append(12, Integer.valueOf(R.layout.shooting_main_sid_info_off_panel));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_panel));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_panel));
        LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_panel));
        LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.shooting_main_sid_basic_info_evf));
        LAYOUT_LIST_FOR_FINDER.append(12, Integer.valueOf(R.layout.shooting_main_sid_info_off_evf));
        LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_evf));
        LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.shooting_main_sid_histogram_evf));
        LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.shooting_main_sid_digitallevel_evf));
        LAYOUT_LIST_FOR_HDMI = LAYOUT_LIST_FOR_PANEL;
        TAGS = new String[]{DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, CameraNotificationManager.HISTOGRAM_UPDATE};
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        GFDisplayModeObserver.getInstance().setNotificationListener(this);
        DisplayModeObserver.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        setAntiBlurOff();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        GFDisplayModeObserver.getInstance().removeNotificationListener(this);
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        GFHistgramTask.getInstance().stopHistgramUpdating();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (device == 0) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 1) {
            baseViewId = R.layout.shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(device, displayMode);
        } else if (device == 2) {
            baseViewId = R.layout.shooting_base_sid_basic_info_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        }
        if (-1 != baseViewId && -1 != baseFooterViewId && -1 != mainViewId && (baseViewId != this.mCurrentBaseViewId || baseFooterViewId != this.mCurrentBaseFooterViewId || mainViewId != this.mCurrentMainViewId)) {
            detachView();
            this.mCurrentBaseViewId = baseViewId;
            this.mCurrentMainViewId = mainViewId;
            this.mCurrentBaseFooterViewId = baseFooterViewId;
            this.mCurrentView = obtainViewFromPool(baseViewId);
            this.mCurrentBaseView = (ViewGroup) this.mCurrentView;
            View baseFooterView = obtainViewFromPool(baseFooterViewId);
            this.mCurrentBaseFooterView = baseFooterView;
            this.mCurrentBaseView.addView(baseFooterView);
            if (baseFooterView != null) {
                this.mUserChangableViews[0] = (DigitView) baseFooterView.findViewById(R.id.aperture_view);
                this.mUserChangableViews[1] = (DigitView) baseFooterView.findViewById(R.id.shutterspeed_view);
                this.mUserChangableViews[2] = (DigitView) baseFooterView.findViewById(R.id.exposure_and_meteredmanual_view);
                this.mUserChangableViews[3] = (DigitView) baseFooterView.findViewById(R.id.iso_sensitivity_view);
            }
            Looper.myQueue().addIdleHandler(this.idleHandler);
        }
        ExposureModeIconView icon1 = (ExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1);
        AppIconView icon2 = (AppIconView) this.mCurrentView.findViewById(R.id.app_icon);
        if (device == 1) {
            icon1.setAutoDisappear(false);
            if (1 == displayMode) {
                icon2.setAutoDisappear(false);
            } else {
                icon2.setAutoDisappear(true);
            }
        } else if (1 == displayMode) {
            icon1.setAutoDisappear(false);
            icon2.setAutoDisappear(false);
        } else {
            icon1.setAutoDisappear(true);
            icon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                TouchArea.OnTouchAreaListener touchListener = getOnTouchListener();
                this.mTouchArea.setTouchAreaListener(touchListener);
                if (touchListener instanceof TouchArea.OnTouchAreaDoubleTapListener) {
                    this.mTouchArea.setTouchAreaDoubleTapListener((TouchArea.OnTouchAreaDoubleTapListener) touchListener);
                }
            }
        }
        handleGraduatedFilterIcon();
    }

    private void handleGraduatedFilterIcon() {
        this.mCurrentMainView = obtainViewFromPool(this.mCurrentMainViewId);
        GFEffectParameters.getInstance().getParameters();
        int displayMode = GFDisplayModeObserver.getInstance().getActiveDispMode(0);
        if (this.mCurrentMainView != null && 5 == displayMode) {
            GFHistgramTask.getInstance().startHistgramUpdating();
        } else {
            GFHistgramTask.getInstance().stopHistgramUpdating();
        }
        BorderView borderView = (BorderView) this.mCurrentMainView.findViewById(R.id.border_view);
        if (3 == displayMode) {
            borderView.setVisibility(4);
        } else {
            borderView.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        if (device == 0) {
            int layout = LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
            return layout;
        }
        if (device == 1) {
            int layout2 = LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
            return layout2;
        }
        if (device != 2) {
            return -1;
        }
        int layout3 = LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        return layout3;
    }

    private void setAntiBlurOff() {
        if (!"off".equals(AntiHandBlurController.getInstance().getValue(AntiHandBlurController.STILL))) {
            AntiHandBlurController.getInstance().setValue(AntiHandBlurController.STILL, "off");
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        CameraEx cameraEx;
        if (CameraNotificationManager.HISTOGRAM_UPDATE.equalsIgnoreCase(tag) && (cameraEx = CameraSetting.getInstance().getCamera()) != null) {
            AppLog.info(TAG, "inhibits listener-NOT-found histoupdate message.");
            cameraEx.setPreviewAnalizeListener((CameraEx.PreviewAnalizeListener) null);
        }
    }
}
