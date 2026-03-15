package com.sony.imaging.app.srctrl.webapi.specific;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class DeletingHandler {
    private static final String TAG = DeletingHandler.class.getSimpleName();
    private static DeletingHandler handler = new DeletingHandler();
    private Object sync = new Object();
    private DeletingStatus status = DeletingStatus.FAILURE;

    /* loaded from: classes.dex */
    public enum DeletingStatus {
        SUCCESS,
        FAILURE
    }

    public static DeletingHandler getInstance() {
        return handler;
    }

    public DeletingStatus goToDeletingState(String[] uri) {
        StateController stateController = StateController.getInstance();
        this.status = DeletingStatus.FAILURE;
        Boolean result = (Boolean) new OperationRequester().request(46, uri);
        if (result == null || !result.booleanValue()) {
            return DeletingStatus.FAILURE;
        }
        stateController.setWaitingDeletingStateChange(true);
        synchronized (this.sync) {
            try {
                this.sync.wait(30000L);
            } catch (InterruptedException e) {
                Log.e(TAG, "InterruptedException while waiting.");
            }
        }
        stateController.setWaitingDeletingStateChange(false);
        return this.status;
    }

    public void onFinishDeleting(int result) {
        Log.v(TAG, "Finished deleting");
        this.status = result == 0 ? DeletingStatus.SUCCESS : DeletingStatus.FAILURE;
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
