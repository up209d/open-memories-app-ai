package com.sony.imaging.app.srctrl.util;

import android.util.Log;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.sysutil.didep.Nfc;
import java.util.Collection;

/* loaded from: classes.dex */
public class NfcCtrlManager extends NotificationManager {
    public static final String TAG_NFC_TOUCH_ENABLE = "nfcTouchEnable";
    private Nfc nfcController;
    private static final String TAG = NfcCtrlManager.class.getSimpleName();
    private static NfcCtrlManager nfcTouchManager = new NfcCtrlManager();

    public NfcCtrlManager() {
        this.nfcController = null;
    }

    public NfcCtrlManager(boolean notifyOnAdded, boolean notPostIfAlreadyQueued) {
        super(notifyOnAdded, notPostIfAlreadyQueued);
        this.nfcController = null;
    }

    public NfcCtrlManager(boolean notifyOnAdded, Collection<String> tagsThinnedOut) {
        super(notifyOnAdded, tagsThinnedOut);
        this.nfcController = null;
    }

    public NfcCtrlManager(boolean notifyOnAdded) {
        super(notifyOnAdded);
        this.nfcController = null;
    }

    public static NfcCtrlManager getInstance() {
        return nfcTouchManager;
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        Log.e(TAG, "getValue(): this api is not supported.");
        return null;
    }

    public void init() {
        if (ModelInfo.getInstance().isNfcSupported()) {
            this.nfcController = new Nfc();
            Log.d(TAG, "init(): create Nfc instance.");
        } else {
            this.nfcController = null;
        }
    }

    public void release() {
        stopNfcTouchStandby();
        Log.d(TAG, "release(): release Nfc instance.");
        if (this.nfcController != null) {
            this.nfcController.release();
            this.nfcController = null;
        }
    }

    private void fireNotifyNfcTouchStandby() {
        notify(TAG_NFC_TOUCH_ENABLE);
        Log.v(TAG, "notify: NFC touch is " + (isNfcTouchEnable() ? "Enable" : "Disable"));
    }

    public void startNfcTouchStandby() {
        if (this.nfcController != null && !this.nfcController.isEnabled()) {
            this.nfcController.start();
            Log.v(TAG, "start() is called.");
            fireNotifyNfcTouchStandby();
        }
    }

    public void stopNfcTouchStandby() {
        if (this.nfcController != null && this.nfcController.isEnabled()) {
            this.nfcController.stop();
            Log.v(TAG, "stop() is called.");
            fireNotifyNfcTouchStandby();
        }
    }

    public boolean isNfcTouchEnable() {
        if (this.nfcController == null) {
            return false;
        }
        boolean ret = this.nfcController.isEnabled();
        return ret;
    }
}
