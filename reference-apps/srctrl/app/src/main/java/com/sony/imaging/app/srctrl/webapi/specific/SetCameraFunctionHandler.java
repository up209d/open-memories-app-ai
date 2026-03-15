package com.sony.imaging.app.srctrl.webapi.specific;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.StateController;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class SetCameraFunctionHandler {
    public static final String REQ_FAILURE = "Failure";
    public static final String REQ_SAME_MODE = "SameMode";
    public static final String REQ_SUCCESS = "Success";
    private static final String TAG = SetCameraFunctionHandler.class.getSimpleName();
    private static SetCameraFunctionHandler handler = null;
    private static boolean mbDoneRecovery = false;
    protected CountDownLatch mPlaybackLatch = null;
    private CountDownLatch mShootingLatch = null;
    protected SetCameraFuncStatus status = SetCameraFuncStatus.FAILURE;

    /* loaded from: classes.dex */
    public enum SetCameraFuncStatus {
        SUCCESS,
        FAILURE,
        SAME_MODE
    }

    public static SetCameraFunctionHandler getInstance() {
        if (handler == null) {
            new SetCameraFunctionHandler();
        }
        return handler;
    }

    private static void setInstance(SetCameraFunctionHandler instance) {
        if (handler == null) {
            handler = instance;
        }
    }

    protected SetCameraFunctionHandler() {
        setInstance(this);
    }

    public SetCameraFuncStatus goToPlaybackState() {
        StateController stateController = StateController.getInstance();
        this.status = SetCameraFuncStatus.FAILURE;
        this.mPlaybackLatch = new CountDownLatch(1);
        String result = (String) new OperationRequester().request(41, (Object) null);
        if (REQ_SAME_MODE.equals(result)) {
            this.mPlaybackLatch = null;
            return SetCameraFuncStatus.SAME_MODE;
        }
        if (REQ_FAILURE.equals(result)) {
            this.mPlaybackLatch = null;
            return SetCameraFuncStatus.FAILURE;
        }
        stateController.setWaitingCameraFunctionChange(true);
        try {
            this.mPlaybackLatch.await(20000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException while waiting : " + e.getMessage());
        }
        stateController.setWaitingCameraFunctionChange(false);
        this.mPlaybackLatch = null;
        return this.status;
    }

    public SetCameraFuncStatus goToShootingState(boolean isDoneRecovery) {
        StateController stateController = StateController.getInstance();
        this.status = SetCameraFuncStatus.FAILURE;
        mbDoneRecovery = isDoneRecovery;
        this.mShootingLatch = new CountDownLatch(1);
        String result = (String) new OperationRequester().request(57, (Object) null);
        if (REQ_SAME_MODE.equals(result)) {
            this.mShootingLatch = null;
            return SetCameraFuncStatus.SAME_MODE;
        }
        if (REQ_FAILURE.equals(result)) {
            this.mShootingLatch = null;
            return SetCameraFuncStatus.FAILURE;
        }
        stateController.setWaitingCameraFunctionChange(true);
        try {
            this.mShootingLatch.await(20000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException while waiting : " + e.getMessage());
        }
        stateController.setWaitingCameraFunctionChange(false);
        this.mShootingLatch = null;
        return this.status;
    }

    public void onChangedPlaybackMode() {
        Log.v(TAG, "detected changed to PlaybackState");
        this.status = SetCameraFuncStatus.SUCCESS;
        if (this.mPlaybackLatch != null) {
            ParamsGenerator.updateCameraFunctionResultParams(REQ_SUCCESS);
            this.mPlaybackLatch.countDown();
        }
    }

    public void onPauseCalled() {
        Log.v(TAG, "detected onPause is called");
        if (this.mPlaybackLatch != null) {
            this.mPlaybackLatch.countDown();
        }
    }

    public void onChangedShootingMode() {
        Log.v(TAG, "detected changed to ShootingState");
        if (this.mShootingLatch != null) {
            if (mbDoneRecovery) {
                this.status = SetCameraFuncStatus.FAILURE;
                ParamsGenerator.updateCameraFunctionResultParams(REQ_FAILURE);
            } else {
                this.status = SetCameraFuncStatus.SUCCESS;
                ParamsGenerator.updateCameraFunctionResultParams(REQ_SUCCESS);
            }
            this.mShootingLatch.countDown();
        }
    }

    public void onPauseShootingCalled() {
        Log.v(TAG, "detected onPause is called");
        if (this.mShootingLatch != null) {
            this.mShootingLatch.countDown();
        }
    }
}
