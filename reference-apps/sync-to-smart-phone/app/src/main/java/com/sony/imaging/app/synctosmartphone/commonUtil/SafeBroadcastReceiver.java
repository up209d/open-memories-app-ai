package com.sony.imaging.app.synctosmartphone.commonUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/* loaded from: classes.dex */
public abstract class SafeBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = SafeBroadcastReceiver.class.getSimpleName();
    private boolean isReceiverAvailable = false;

    public abstract void checkedOnReceive(Context context, Intent intent);

    /* loaded from: classes.dex */
    private class BlockingDetector extends Thread {
        private BlockingDetector() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                Thread.sleep(8000L);
                Log.e(SafeBroadcastReceiver.TAG, "onReceive hasn't finished for 8 sec. Now throw exception.");
            } catch (InterruptedException e) {
            }
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (this.isReceiverAvailable) {
            BlockingDetector detector = new BlockingDetector();
            detector.start();
            checkedOnReceive(context, intent);
            detector.interrupt();
            return;
        }
        Log.e(TAG, "Receiver is not available. onReceive is ignored.");
    }

    public void registerThis(Activity activity, IntentFilter iFilter) {
        if (activity != null) {
            activity.registerReceiver(this, iFilter);
            this.isReceiverAvailable = true;
        }
    }

    public void unregisterThis(Activity activity) {
        this.isReceiverAvailable = false;
        if (activity != null) {
            activity.unregisterReceiver(this);
        }
    }
}
