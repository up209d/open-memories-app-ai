package com.sony.imaging.app.util;

import android.util.Log;
import com.sony.imaging.app.base.playback.LogHelper;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ValueMapNotificationManager extends NotificationManager {
    private static final String TAG = "ValueMapNotificationManager";
    private HashMap<String, Object> mValues;

    public ValueMapNotificationManager() {
        this.mValues = new HashMap<>();
    }

    public ValueMapNotificationManager(boolean notifyOnAdded) {
        super(notifyOnAdded);
        this.mValues = new HashMap<>();
    }

    public void onDeviceChanged(String tag, Object value) {
        Log.d(TAG, "onCallback " + tag + LogHelper.MSG_COLON + value);
        boolean isValueChanged = true;
        if (this.mValues.containsKey(tag)) {
            Object current = this.mValues.remove(tag);
            Log.d(TAG, "old value : " + current);
            isValueChanged = !current.equals(value);
        }
        this.mValues.put(tag, value);
        if (isValueChanged) {
            notify(tag);
        } else {
            Log.i(TAG, "NOT changed");
        }
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        if (this.mValues.containsKey(tag)) {
            return this.mValues.get(tag);
        }
        return null;
    }
}
