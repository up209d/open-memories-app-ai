package com.sony.imaging.app.srctrl.webapi.specific;

import android.util.Log;
import com.sony.imaging.app.srctrl.util.StateController;

/* loaded from: classes.dex */
public class ServerEventHandler {
    private static final String TAG = ServerEventHandler.class.getSimpleName();
    private static ServerEventHandler sHandler = new ServerEventHandler();
    private boolean isStatusChanged;
    private Status returnStatus;
    private boolean isForceEnd = false;
    private boolean transaction = false;
    private boolean update = false;
    private Object sync = new Object();
    private Status status = Status.SUCCESS;

    /* loaded from: classes.dex */
    public enum Status {
        SUCCESS,
        CANCELED,
        FAILED,
        WAITING,
        FORCEEND
    }

    public static ServerEventHandler getInstance() {
        return sHandler;
    }

    public Status startWaiting() {
        return startWaiting(15000L);
    }

    public Status startWaiting(long waitMillis) {
        Status status;
        Log.v(TAG, "Start waiting for the event update...");
        synchronized (this.sync) {
            this.isForceEnd = false;
            if (Status.WAITING.equals(this.status)) {
                Log.v(TAG, "Wait until previous one is stopped");
                try {
                    onDoublePollingHappened();
                    this.sync.wait(5000L);
                    Log.v(TAG, "Previous one is finished: " + this.status.toString());
                    if (this.isForceEnd) {
                        Log.v(TAG, "isForceEnd");
                        status = Status.FORCEEND;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setStatusChanged(false);
            try {
                Log.v(TAG, "Event update status is " + Status.WAITING + " changed from " + this.status);
                this.status = Status.WAITING;
                Log.v(TAG, "Waiting for event update....");
                this.sync.wait(waitMillis);
                Log.v(TAG, "Finish waiting for event update.");
                setReturnStatus(this.status);
                setStatusChanged(false);
                if (Status.WAITING.equals(this.status)) {
                    Log.v(TAG, "Event update status is " + Status.SUCCESS + " changed from " + Status.WAITING);
                    this.status = Status.SUCCESS;
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

    public void beginServerStatusChanged() {
        synchronized (this.sync) {
            Log.v(TAG, "beginServerStatusChanged.");
            this.transaction = true;
        }
    }

    public void commitServerStatusChanged() {
        synchronized (this.sync) {
            Log.v(TAG, "commitServerStatusChanged.");
            this.transaction = false;
        }
        if (this.update) {
            onServerStatusChanged();
            this.update = false;
        }
    }

    public void onServerStatusChanged(boolean isForce) {
        Log.v(TAG, "server status. " + StateController.getInstance().getServerStatus().toString());
        synchronized (this.sync) {
            if (!isForce) {
                if (this.transaction) {
                    Log.v(TAG, "onServerStatusChanged wait...");
                    this.update = true;
                }
            }
            Log.v(TAG, "detected change of server status.");
            setReturnStatus(Status.SUCCESS);
            setStatusChanged(true);
            this.sync.notify();
        }
    }

    public void onServerStatusChanged() {
        onServerStatusChanged(false);
    }

    public void onPauseCalled() {
        synchronized (this.sync) {
            Log.v(TAG, "detected onPause.");
            setReturnStatus(Status.SUCCESS);
            this.sync.notify();
        }
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean isStatusChanged() {
        return this.isStatusChanged;
    }

    public void setStatusChanged(boolean bool) {
        Log.v(TAG, "The updated flag for GetEvent is set to " + bool);
        this.isStatusChanged = bool;
    }

    private void setReturnStatus(Status status) {
        Log.v(TAG, "The return status for GetEvent is set to " + status.name());
        this.status = status;
        this.returnStatus = status;
    }

    public void resetWaiting() {
        synchronized (this.sync) {
            Log.v(TAG, "reset Waiting.");
            this.isForceEnd = true;
            setReturnStatus(Status.FORCEEND);
            this.sync.notifyAll();
        }
    }
}
