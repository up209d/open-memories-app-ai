package com.sony.imaging.app.startrails.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.menu.controller.STSelfTimerMenuController;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.startrails.util.STUtility;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SelfTimerDisplayLayout extends Layout implements NotificationListener {
    public static final String TAG = "SelfTimerDisplayLayout";
    private static String[] TAGS = {STConstants.REMOVE_CAPTURE_CALLBACK};
    private View mCurrentView = null;
    private Handler mHandlerOneSecond = null;
    private Handler mHandlerDisapear = null;
    private final long ONE_SEC_PASS = 1000;
    private ImageView mImgVwSelfTimer = null;
    private OneSecIconDisplayer mOneSecIconDisplayer = null;
    private DisappearIconDisplayer mDisappearIconDisplayer = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(R.layout.startrails_self_timer_two_second_display_layout);
        if (this.mCurrentView != null) {
            this.mImgVwSelfTimer = (ImageView) this.mCurrentView.findViewById(R.id.self_timer_view);
            if (this.mImgVwSelfTimer != null) {
                this.mImgVwSelfTimer.setImageResource(R.drawable.p_16_dd_parts_tm_timer_2sec_during_countdown);
                this.mImgVwSelfTimer.setVisibility(4);
            }
        }
        return this.mCurrentView;
    }

    private void startProgress() {
        AppLog.enter(TAG, "startProgress() ");
        this.mOneSecIconDisplayer = new OneSecIconDisplayer();
        this.mHandlerOneSecond = new Handler();
        this.mHandlerOneSecond.postDelayed(this.mOneSecIconDisplayer, 1000L);
        AppLog.exit(TAG, "startProgress() ");
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
        super.onResume();
        handleSelfTimerOnOFF();
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }

    private void handleSelfTimerOnOFF() {
        AppLog.enter(TAG, "handleSelfTimerOnOFF");
        if (STSelfTimerMenuController.getInstance().isSelfTimer()) {
            STUtility.getInstance().setSelfTimerStatus(true);
            if (this.mImgVwSelfTimer != null) {
                this.mImgVwSelfTimer.setVisibility(0);
                startProgress();
            }
        } else {
            STUtility.getInstance().setSelfTimerStatus(false);
            CameraNotificationManager.getInstance().requestNotify(STConstants.CAPTURING_TWO_SEC_FINISH);
        }
        AppLog.exit(TAG, "handleSelfTimerOnOFF");
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        deinitializeView();
        super.closeLayout();
    }

    private void deinitializeView() {
        releaseHandler();
        releaseImageViewDrawable(this.mImgVwSelfTimer);
        this.mImgVwSelfTimer = null;
        this.mCurrentView = null;
    }

    private void releaseHandler() {
        if (this.mHandlerOneSecond != null) {
            if (this.mOneSecIconDisplayer != null) {
                this.mHandlerOneSecond.removeCallbacks(this.mOneSecIconDisplayer);
                this.mOneSecIconDisplayer = null;
            }
            this.mHandlerOneSecond = null;
        }
        if (this.mHandlerDisapear != null) {
            if (this.mDisappearIconDisplayer != null) {
                this.mHandlerDisapear.removeCallbacks(this.mDisappearIconDisplayer);
                this.mDisappearIconDisplayer = null;
            }
            this.mHandlerDisapear = null;
        }
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        deinitializeView();
        super.onPause();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class OneSecIconDisplayer implements Runnable {
        OneSecIconDisplayer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            AppLog.enter(SelfTimerDisplayLayout.TAG, "OneSecIconDisplayer run() ");
            if (SelfTimerDisplayLayout.this.mImgVwSelfTimer != null) {
                if (!STUtility.getInstance().isSelfTimerProcessing()) {
                    SelfTimerDisplayLayout.this.mImgVwSelfTimer.setVisibility(4);
                } else {
                    SelfTimerDisplayLayout.this.mHandlerDisapear = new Handler();
                    SelfTimerDisplayLayout.this.mDisappearIconDisplayer = new DisappearIconDisplayer();
                    SelfTimerDisplayLayout.this.mHandlerDisapear.postDelayed(SelfTimerDisplayLayout.this.mDisappearIconDisplayer, 1000L);
                    SelfTimerDisplayLayout.this.mImgVwSelfTimer.setImageResource(R.drawable.p_16_dd_parts_tm_timer_1sec_during_countdown);
                    SelfTimerDisplayLayout.this.mImgVwSelfTimer.setVisibility(0);
                }
            }
            AppLog.exit(SelfTimerDisplayLayout.TAG, "OneSecIconDisplayer run() ");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class DisappearIconDisplayer implements Runnable {
        DisappearIconDisplayer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            AppLog.enter(SelfTimerDisplayLayout.TAG, "DisappearIconDisplayer run() ");
            if (SelfTimerDisplayLayout.this.mImgVwSelfTimer != null) {
                SelfTimerDisplayLayout.this.mImgVwSelfTimer.setVisibility(8);
                STUtility.getInstance().setSelfTimerStatus(false);
                CameraNotificationManager.getInstance().requestNotify(STConstants.CAPTURING_TWO_SEC_FINISH);
            }
            AppLog.exit(SelfTimerDisplayLayout.TAG, "DisappearIconDisplayer run() ");
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (STUtility.getInstance().isSelfTimerProcessing()) {
            closeLayout();
        }
    }
}
