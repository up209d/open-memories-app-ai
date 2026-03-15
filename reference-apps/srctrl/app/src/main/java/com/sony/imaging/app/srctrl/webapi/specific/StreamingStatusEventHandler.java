package com.sony.imaging.app.srctrl.webapi.specific;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class StreamingStatusEventHandler {
    private static final String TAG = StreamingStatusEventHandler.class.getSimpleName();
    private static StreamingStatusEventHandler sHandler = new StreamingStatusEventHandler();
    private Status returnStatus;
    private Object sync = new Object();
    private Status status = Status.SUCCESS;

    /* loaded from: classes.dex */
    public enum Status {
        SUCCESS,
        CANCELED,
        FAILED,
        WAITING
    }

    public static StreamingStatusEventHandler getInstance() {
        return sHandler;
    }

    public Status startWaiting() {
        Status status;
        Log.v(TAG, "Start waiting for the event update...");
        synchronized (this.sync) {
            if (Status.WAITING.equals(this.status)) {
                Log.v(TAG, "Wait until previous one is stopped");
                try {
                    onDoublePollingHappened();
                    this.sync.wait(5000L);
                    Log.v(TAG, "Previous one is finished: " + this.status.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Log.v(TAG, "Event update status is " + Status.WAITING + " changed from " + this.status);
                this.status = Status.WAITING;
                Log.v(TAG, "Waiting for event update....");
                this.sync.wait(60000L);
                Log.v(TAG, "Finish waiting for event update.");
                if (Status.WAITING.equals(this.status)) {
                    Log.v(TAG, "Event update status is " + Status.SUCCESS + " changed from " + Status.WAITING);
                    setReturnStatus(Status.SUCCESS);
                }
                this.sync.notifyAll();
                Log.v(TAG, this.returnStatus.toString());
                status = this.returnStatus;
            } catch (InterruptedException e2) {
                Log.e(TAG, "InterruptedException when waiting.");
                Log.v(TAG, "Event update status is " + Status.FAILED + " changed from " + this.status);
                this.status = Status.FAILED;
                status = this.status;
            }
        }
        return status;
    }

    public void onDoublePollingHappened() {
        synchronized (this.sync) {
            Log.v(TAG, "detected double polling");
            setReturnStatus(Status.CANCELED);
            this.sync.notifyAll();
        }
    }

    public void onServerStatusChanged() {
        Log.v(TAG, "server status. " + StateController.getInstance().getServerStatus().toString());
        synchronized (this.sync) {
            Log.v(TAG, "detected change of server status.");
            setReturnStatus(Status.SUCCESS);
            this.sync.notify();
        }
    }

    public void onPauseCalled() {
        synchronized (this.sync) {
            Log.v(TAG, "detected onPause.");
            setReturnStatus(Status.SUCCESS);
            this.sync.notify();
        }
    }

    private void setReturnStatus(Status status) {
        Log.v(TAG, "The return status for StreamingStatusEventHandler is set to " + status.name());
        this.status = status;
        this.returnStatus = status;
    }
}
