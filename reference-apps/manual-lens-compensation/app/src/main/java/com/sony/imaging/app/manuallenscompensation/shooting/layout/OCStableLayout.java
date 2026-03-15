package com.sony.imaging.app.manuallenscompensation.shooting.layout;

import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EVDialDetector;
import com.sony.imaging.app.base.playback.widget.TouchArea;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.base.shooting.widget.DigitView;
import com.sony.imaging.app.base.shooting.widget.ExposureModeIconView;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;

/* loaded from: classes.dex */
public class OCStableLayout extends StableLayout {
    private MLCOnEETouchListener mFocuMagnifyTouchListener = null;

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected TouchArea.OnTouchAreaListener getOnTouchListener() {
        if (this.mFocuMagnifyTouchListener == null) {
            this.mFocuMagnifyTouchListener = new MLCOnEETouchListener();
        }
        return this.mFocuMagnifyTouchListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MLCOnEETouchListener extends StableLayout.OnEETouchListener {
        MLCOnEETouchListener() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.OnEETouchListener, com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
        public boolean onTouchDown(MotionEvent e) {
            String mfAssistOff = FocusMagnificationController.getInstance().getValue(FocusMagnificationController.TAG_IS_MFASSIST_AVAILABLE_IN_SETTINGS);
            if (!"off".equals(mfAssistOff)) {
                boolean ovfMode = OCUtil.getInstance().isDiademOVfMode();
                OCUtil.getInstance().setCameraPreviewMode("off", ovfMode);
            }
            return super.onTouchDown(e);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        updateExposureComp();
        super.onResume();
    }

    private void updateExposureComp() {
        if (CameraSetting.getPfApiVersion() >= 2 && EVDialDetector.hasEVDial()) {
            int ev = EVDialDetector.getEVDialPosition();
            if (ev != 0) {
                ExposureCompensationController.getInstance().setEvDialValue(EVDialDetector.getEVDialPosition());
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mFocuMagnifyTouchListener = null;
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected void createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
        int baseViewId = -1;
        int mainViewId = -1;
        int baseFooterViewId = -1;
        int touchAreaId = -1;
        if (device == 0 || device == 2) {
            baseViewId = R.layout.shooting_base_sid_basic_info_oc_panel;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_panel;
            mainViewId = getLayout(device, displayMode);
            touchAreaId = R.id.touchArea;
        } else if (device == 1) {
            baseViewId = R.layout.shooting_base_sid_basic_info_evf;
            baseFooterViewId = R.layout.shooting_base_sid_basic_footer_evf;
            mainViewId = getLayout(device, displayMode);
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
        ExposureModeIconView icon1_movie = (ExposureModeIconView) this.mCurrentView.findViewById(R.id.i10_1_movie);
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
            icon1_movie.setAutoDisappear(false);
            icon2.setAutoDisappear(false);
        } else {
            icon1.setAutoDisappear(true);
            icon1_movie.setAutoDisappear(true);
            icon2.setAutoDisappear(true);
        }
        if (-1 != touchAreaId) {
            this.mTouchArea = (TouchArea) this.mCurrentView.findViewById(touchAreaId);
            if (this.mTouchArea != null) {
                this.mTouchArea.setTouchAreaListener(getOnTouchListener());
            }
        }
    }
}
