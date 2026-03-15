package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ProgressLayout extends Layout {
    private NotificationListener progressListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.layout.ProgressLayout.1
        private final String[] tags = {CameraNotificationManager.PROCESSING_PROGRESS};

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            double progress = ((Double) CameraNotificationManager.getInstance().getValue(tag)).doubleValue();
            if (progress == 0.0d) {
                ProgressLayout.this.setVisibility(true);
            } else if (progress == 1.0d) {
                ProgressLayout.this.setVisibility(false);
            }
        }
    };
    protected Handler handler = new Handler();
    protected UnmuteRunnable mUnmuteRunnable = new UnmuteRunnable();

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (Environment.isNewBizDeviceActionCam()) {
            View view = obtainViewFromPool(R.layout.shooting_main_sid_sublcd_wait);
            return view;
        }
        View view2 = obtainViewFromPool(R.layout.shooting_main_progress);
        return view2;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setVisibility(false);
        CameraNotificationManager.getInstance().setNotificationListener(this.progressListener);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        setVisibility(false);
        CameraNotificationManager.getInstance().removeNotificationListener(this.progressListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class UnmuteRunnable implements Runnable {
        protected UnmuteRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            DisplayModeObserver.getInstance().controlGraphicsOutputAll(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVisibility(boolean dispOn) {
        if (dispOn) {
            getView().setVisibility(0);
            this.handler.postDelayed(this.mUnmuteRunnable, 300L);
        } else {
            getView().setVisibility(4);
            this.handler.removeCallbacks(this.mUnmuteRunnable);
        }
    }
}
