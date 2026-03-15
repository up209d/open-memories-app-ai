package com.sony.imaging.app.soundphoto.playback.delete.single.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sony.imaging.app.base.playback.layout.DeleteSingleProcessingLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.playback.delete.single.executor.IFinishListener;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class SPDeleteSingleProcessingLayout extends DeleteSingleProcessingLayout {
    private IFinishListener mIFinishListener;
    boolean isNotified = false;
    private final int FINAL_STEP = 100;
    private ProgressBar mProgressBar = null;
    private View mCurrentView = null;
    private Runnable sDelayedEndProcess = new Runnable() { // from class: com.sony.imaging.app.soundphoto.playback.delete.single.layout.SPDeleteSingleProcessingLayout.1
        @Override // java.lang.Runnable
        public void run() {
            if (SPDeleteSingleProcessingLayout.this.mProgressBar != null) {
                SPDeleteSingleProcessingLayout.this.mProgressBar.setProgress(100);
            }
            SPDeleteSingleProcessingLayout.this.notifyEnd();
        }
    };

    @Override // com.sony.imaging.app.base.playback.layout.DeleteSingleProcessingLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = super.onCreateView(inflater, container, savedInstanceState);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, "onResume");
        this.isNotified = true;
        super.onResume();
        initializeView();
        AppLog.exit(this.TAG, "onResume");
    }

    private void initializeView() {
        String message;
        AppLog.enter(this.TAG, "initializeView()");
        if (this.mCurrentView != null) {
            TextView messageText = (TextView) this.mCurrentView.findViewById(R.id.dialog_string_4lines);
            if (messageText != null) {
                if (SPUtil.getInstance().isSoundDataDeletePerforming()) {
                    message = getActivity().getResources().getString(android.R.string.deleteText);
                } else {
                    message = getActivity().getResources().getString(android.R.string.dial_number_using);
                    CameraNotificationManager.getInstance().requestNotify(SPConstants.DELETE_IMAGE_DATABASE_UPDATE);
                }
                messageText.setText(message);
            }
            this.mProgressBar = (ProgressBar) this.mCurrentView.findViewById(getProgressBarResource());
            showProgressForSoundPhoto();
        }
        AppLog.exit(this.TAG, "initializeView()");
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(this.TAG, "onPause()");
        deinitializeView();
        super.onPause();
        AppLog.exit(this.TAG, "onPause()");
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(this.TAG, "closeLayout()");
        deinitializeView();
        super.closeLayout();
        AppLog.exit(this.TAG, "closeLayout()");
    }

    private void showProgressForSoundPhoto() {
        AppLog.enter(this.TAG, "showProgressForSoundPhoto");
        Handler handler = getHandler();
        handler.postDelayed(this.sDelayedEndProcess, 500L);
        AppLog.exit(this.TAG, "showProgressForSoundPhoto");
    }

    public void deinitializeView() {
        AppLog.enter(this.TAG, "deinitializeView");
        if (this.isNotified) {
            this.isNotified = false;
        }
        this.mProgressBar = null;
        this.mCurrentView = null;
        AppLog.exit(this.TAG, "deinitializeView");
    }

    public void notifyEnd() {
        AppLog.enter(this.TAG, "notifyEnd()");
        if (this.mIFinishListener != null) {
            this.mIFinishListener.finishTransition();
        }
        AppLog.exit(this.TAG, "notifyEnd()");
    }

    public void setSoundPhotoDeleteFinishTrigger(IFinishListener spDeleteSingleExecutor) {
        this.mIFinishListener = spDeleteSingleExecutor;
    }
}
