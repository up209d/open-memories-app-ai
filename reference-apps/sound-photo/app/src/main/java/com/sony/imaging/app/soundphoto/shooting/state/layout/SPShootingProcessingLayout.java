package com.sony.imaging.app.soundphoto.shooting.state.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SPShootingProcessingLayout extends Layout implements NotificationListener {
    public static final String TAG = "SPShootingProcessingLayout";
    private static final String[] TAGS = {SPConstants.TAG_PROCESSING_LAYOUT};
    private static final long TWO_SECOND_DELAY = 2000;
    private View mCurrentView;
    private RunnableTask disappearTriggerTask = null;
    Handler _handler = new Handler();

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.sp_shooting_processing_layout);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (this.mCurrentView != null) {
            this.mCurrentView.setVisibility(8);
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(SPConstants.TAG_PROCESSING_LAYOUT)) {
            if (this.mCurrentView != null) {
                this.mCurrentView.setVisibility(0);
            }
            cancelDelayTimerTask();
            startDelayTimerTask();
        }
    }

    protected void cancelDelayTimerTask() {
        if (this.disappearTriggerTask != null && this._handler != null) {
            this._handler.removeCallbacks(this.disappearTriggerTask);
            this.disappearTriggerTask = null;
        }
    }

    protected void startDelayTimerTask() {
        if (this.disappearTriggerTask == null) {
            this.disappearTriggerTask = new RunnableTask();
        }
        this._handler.postDelayed(this.disappearTriggerTask, TWO_SECOND_DELAY);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class RunnableTask implements Runnable {
        RunnableTask() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SPShootingProcessingLayout.this.mCurrentView != null) {
                SPShootingProcessingLayout.this.mCurrentView.setVisibility(8);
            }
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        super.onDestroyView();
    }
}
