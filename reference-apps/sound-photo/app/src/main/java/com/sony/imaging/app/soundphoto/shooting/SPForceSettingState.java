package com.sony.imaging.app.soundphoto.shooting;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Pair;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.base.shooting.ForceSettingState;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class SPForceSettingState extends ForceSettingState {
    private static final String TAG = "SPForceSettingState";
    private static final long TWO_SECOND_DELAY = 2000;
    private RunnableTask disappearTriggerTask = null;
    Handler _handler = new Handler();

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected boolean isForceSetting() {
        return true;
    }

    @Override // com.sony.imaging.app.base.shooting.ForceSettingState
    protected Pair<Camera.Parameters, CameraEx.ParametersModifier> onSetForceSetting(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params2;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> params3 = CameraSetting.getInstance().getParameters();
        if (((CameraEx.ParametersModifier) params3.second).getSoftSkinEffect().equals("off")) {
            ((CameraEx.ParametersModifier) params3.second).setSoftSkinEffect("off");
        }
        if (((CameraEx.ParametersModifier) params3.second).getMultiShootNRMode()) {
            params2 = CameraSetting.getInstance().getEmptyParameters();
            ((CameraEx.ParametersModifier) params2.second).setMultiShootNRMode(false);
        } else {
            params2 = CameraSetting.getInstance().getEmptyParameters();
        }
        displayMovieRecCaution();
        return params2;
    }

    private void displayMovieRecCaution() {
        if (this.data != null && this.data.containsKey("keyCode")) {
            String from = this.data.getString(PlayRootContainer.BUNDLE_TRANSITION_FROM);
            if (BaseApp.APP_PLAY.equals(from)) {
                int keycode = this.data.getInt("keyCode");
                if (515 == keycode) {
                    openRecDisabledCaution();
                }
            }
        }
    }

    public void openRecDisabledCaution() {
        AppLog.info(TAG, "Movie rec caution displayed");
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
        SPUtil.getInstance().setMovieRecordingCautionShown(true);
        cancelDelayTimerTask();
        startDelayTimerTask();
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
            if (SPUtil.getInstance().isMovieRecordingCautionShown()) {
                CautionUtilityClass.getInstance().disapperTrigger(Info.CAUTION_ID_DLAPP_CANNOT_REC_MOVIE);
            }
        }
    }
}
