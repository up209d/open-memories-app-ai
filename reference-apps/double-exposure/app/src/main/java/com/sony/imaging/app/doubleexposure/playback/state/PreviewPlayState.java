package com.sony.imaging.app.doubleexposure.playback.state;

import android.os.Handler;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.playback.base.PlayStateBase;
import com.sony.imaging.app.doubleexposure.common.AppLog;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureUtil;
import com.sony.imaging.app.doubleexposure.menu.controller.SecondShootingPriorityController;
import com.sony.imaging.app.doubleexposure.menu.controller.ThemeSelectionController;
import com.sony.imaging.app.fw.AppRoot;

/* loaded from: classes.dex */
public class PreviewPlayState extends PlayStateBase {
    public static final String ID_PREVIEWLAYOUT = "ID_PREVIEWLAYOUT";
    public static final int TRANSIT_TO_PREVIEWSTATE = 101;
    private String TAG = AppLog.getClassName();
    private RunnableTask mRunnableTask = null;

    @Override // com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        openLayout(ID_PREVIEWLAYOUT);
        if (this.mRunnableTask == null) {
            this.mRunnableTask = new RunnableTask();
        }
        this.mRunnableTask.execute();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        if (getLayout(ID_PREVIEWLAYOUT).getView() != null) {
            getLayout(ID_PREVIEWLAYOUT).closeLayout();
        }
        this.mRunnableTask = null;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        super.onDestroy();
    }

    /* loaded from: classes.dex */
    private class RunnableTask {
        private final int DELAY_TIME;
        private Handler mHandler;
        private Runnable mRunnable;

        private RunnableTask() {
            this.DELAY_TIME = 2000;
            this.mHandler = new Handler();
            this.mRunnable = new Runnable() { // from class: com.sony.imaging.app.doubleexposure.playback.state.PreviewPlayState.RunnableTask.1
                @Override // java.lang.Runnable
                public void run() {
                    AppLog.enter(PreviewPlayState.this.TAG, AppLog.getMethodName());
                    String secondShootingPriority = null;
                    try {
                        secondShootingPriority = SecondShootingPriorityController.getInstance().getValue(SecondShootingPriorityController.TAG_SECONDSHOOTING_PRIORITY);
                    } catch (IController.NotSupportedException e) {
                        AppLog.error(PreviewPlayState.this.TAG, e.toString());
                    }
                    if (secondShootingPriority != null && secondShootingPriority.equalsIgnoreCase("Off")) {
                        DoubleExposureUtil.getInstance().setCurrentShootingScreen(DoubleExposureConstant.FIRST_SHOOTING);
                        String selectedTheme = ThemeSelectionController.getInstance().getValue(ThemeSelectionController.THEMESELECTION);
                        if (!selectedTheme.equalsIgnoreCase("Manual")) {
                            DoubleExposureUtil.getInstance().setInitFocusMode(true);
                        }
                    } else {
                        DoubleExposureUtil.getInstance().setCurrentShootingScreen(DoubleExposureConstant.SECOND_SHOOTING);
                    }
                    AppRoot root = (AppRoot) PreviewPlayState.this.getActivity();
                    if (root != null && root.getData(PlayRootContainer.PROP_ID_APP_ROOT) != null) {
                        PreviewPlayState.this.transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
                    }
                    RunnableTask.this.removeCallbacks();
                    AppLog.exit(PreviewPlayState.this.TAG, AppLog.getMethodName());
                }
            };
        }

        public void execute() {
            AppLog.enter(PreviewPlayState.this.TAG, AppLog.getMethodName());
            this.mHandler.postDelayed(this.mRunnable, 2000L);
            AppLog.exit(PreviewPlayState.this.TAG, AppLog.getMethodName());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeCallbacks() {
            AppLog.enter(PreviewPlayState.this.TAG, AppLog.getMethodName());
            if (this.mHandler != null) {
                this.mHandler.removeCallbacks(this.mRunnable);
                this.mHandler = null;
            }
            if (this.mRunnable != null) {
                this.mRunnable = null;
            }
            AppLog.exit(PreviewPlayState.this.TAG, AppLog.getMethodName());
        }
    }
}
