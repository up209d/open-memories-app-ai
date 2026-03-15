package com.sony.imaging.app.srctrl.shooting.layout;

import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.srctrl.R;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class SRCtrlStableLayout extends StableLayout {
    private static final String tag = SRCtrlStableLayout.class.getName();
    private View attachedWifiImgLayoutView = null;
    private View attachedNfcImgLayoutView = null;
    private ViewGroup currentAppNameExclusiveView = null;
    private _SRCtrlNotifyCameraSetupFinishedIdleHandler mNotifyCameraSetupFinishedIdleHandler = new _SRCtrlNotifyCameraSetupFinishedIdleHandler();

    public SRCtrlStableLayout() {
        this.idleHandler = new _SRCtrlDelayAttachView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    public void detachView() {
        removeViews();
        super.detachView();
        Looper.myQueue().removeIdleHandler(this.mNotifyCameraSetupFinishedIdleHandler);
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        removeViews();
        addViews();
        super.onReopened();
    }

    /* loaded from: classes.dex */
    protected class _SRCtrlDelayAttachView extends StableLayout.DelayAttachView {
        protected _SRCtrlDelayAttachView() {
            super();
        }

        @Override // com.sony.imaging.app.base.shooting.layout.StableLayout.DelayAttachView, android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            StateController sc = StateController.getInstance();
            sc.setDuringCameraSetupRoutine(true);
            Looper.myQueue().removeIdleHandler(SRCtrlStableLayout.this.mNotifyCameraSetupFinishedIdleHandler);
            Looper.myQueue().addIdleHandler(SRCtrlStableLayout.this.mNotifyCameraSetupFinishedIdleHandler);
            boolean ret = super.queueIdle();
            SRCtrlStableLayout.this.addViews();
            return ret;
        }
    }

    /* loaded from: classes.dex */
    private class _SRCtrlNotifyCameraSetupFinishedIdleHandler implements MessageQueue.IdleHandler {
        private _SRCtrlNotifyCameraSetupFinishedIdleHandler() {
        }

        @Override // android.os.MessageQueue.IdleHandler
        public boolean queueIdle() {
            StateController sc = StateController.getInstance();
            sc.setDuringCameraSetupRoutine(false);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addViews() {
        if (!isMainLayoutChanging()) {
            addWiFiImageIcon();
            addNfcImageIcon();
        } else {
            Log.w(tag, "Maybe main layout is changing now, so don't call addViews()");
        }
    }

    private void removeViews() {
        removeWiFiImageIcon();
        removeNfcImageIcon();
    }

    private boolean isMainLayoutChanging() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(0);
        return this.mCurrentMainViewId != getLayout(device, displayMode);
    }

    private void addWiFiImageIcon() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        if (device == 1) {
            this.attachedWifiImgLayoutView = obtainViewFromPool(R.layout.srctrl_parts_cmn_wifi_img_evf);
            if (2 == ExecutorCreator.getInstance().getRecordingMode()) {
                this.attachedNfcImgLayoutView = obtainViewFromPool(R.layout.srctrl_parts_cmn_nfc_img_evf);
            }
        } else if (2 == ExecutorCreator.getInstance().getRecordingMode()) {
            this.attachedNfcImgLayoutView = obtainViewFromPool(R.layout.srctrl_parts_cmn_nfc_img_panel);
            this.attachedWifiImgLayoutView = obtainViewFromPool(R.layout.srctrl_parts_cmn_wifi_img_panel_movie);
        } else {
            this.attachedWifiImgLayoutView = obtainViewFromPool(R.layout.srctrl_parts_cmn_wifi_img_panel);
        }
        if (this.attachedWifiImgLayoutView != null) {
            if (this.mCurrentMainView != null) {
                this.currentAppNameExclusiveView = (ViewGroup) this.mCurrentMainView.findViewById(R.id.exclusive_with_appname);
            }
            if (this.currentAppNameExclusiveView != null) {
                this.currentAppNameExclusiveView.addView(this.attachedWifiImgLayoutView);
            }
        }
    }

    private void addNfcImageIcon() {
        if (this.mCurrentBaseView != null && this.attachedNfcImgLayoutView != null) {
            this.mCurrentBaseView.addView(this.attachedNfcImgLayoutView);
        }
    }

    private void removeWiFiImageIcon() {
        if (this.mCurrentBaseView != null && this.attachedWifiImgLayoutView != null && this.currentAppNameExclusiveView != null) {
            this.currentAppNameExclusiveView.removeView(this.attachedWifiImgLayoutView);
            this.currentAppNameExclusiveView = null;
            this.attachedWifiImgLayoutView = null;
        }
    }

    private void removeNfcImageIcon() {
        if (this.mCurrentBaseView != null && this.attachedNfcImgLayoutView != null) {
            this.mCurrentBaseView.removeView(this.attachedNfcImgLayoutView);
            this.attachedNfcImgLayoutView = null;
        }
    }
}
