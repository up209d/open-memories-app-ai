package com.sony.imaging.app.srctrl.webapi.specific;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class RecModeTransitionHandler {
    private static final String TAG = RecModeTransitionHandler.class.getSimpleName();
    private static RecModeTransitionHandler transHandler = new RecModeTransitionHandler();
    private Object sync = new Object();
    private TransStatus status = TransStatus.FAILURE;

    /* loaded from: classes.dex */
    public enum TransStatus {
        SUCCESS,
        FAILURE
    }

    public static RecModeTransitionHandler getInstance() {
        return transHandler;
    }

    public TransStatus goToShootingState() {
        StateController stateController = StateController.getInstance();
        this.status = TransStatus.FAILURE;
        Boolean result = (Boolean) new OperationRequester().request(19, (Object) null);
        if (result == null || !result.booleanValue()) {
            return TransStatus.FAILURE;
        }
        stateController.setWaitingRecModeChange(true);
        synchronized (this.sync) {
            try {
                this.sync.wait(20000L);
            } catch (InterruptedException e) {
                Log.e(TAG, "InterruptedException while waiting.");
            }
        }
        stateController.setWaitingRecModeChange(false);
        return this.status;
    }

    public TransStatus goToNetworkState() {
        StateController stateController = StateController.getInstance();
        this.status = TransStatus.FAILURE;
        Boolean result = (Boolean) new OperationRequester().request(20, (Object) null);
        if (result == null || !result.booleanValue()) {
            return TransStatus.FAILURE;
        }
        stateController.setWaitingRecModeChange(true);
        synchronized (this.sync) {
            try {
                this.sync.wait(20000L);
            } catch (InterruptedException e) {
                Log.e(TAG, "InterruptedException while waiting.");
            }
        }
        stateController.setWaitingRecModeChange(false);
        return this.status;
    }

    public void onChangedOperatingMode() {
        Log.v(TAG, "detected changed to ShootingState/NetworkState");
        this.status = TransStatus.SUCCESS;
        synchronized (this.sync) {
            this.sync.notify();
        }
    }

    public void onPauseCalled() {
        Log.v(TAG, "detected onPause is called");
        synchronized (this.sync) {
            this.sync.notify();
        }
    }
}
