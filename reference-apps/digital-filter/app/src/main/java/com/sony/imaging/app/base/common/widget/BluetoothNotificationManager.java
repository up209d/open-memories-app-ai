package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.Log;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.sysutil.didep.Bluetooth;

/* loaded from: classes.dex */
public class BluetoothNotificationManager extends NotificationManager {
    public static final String BLUETOOTH_POWER = "BluetoothPower";
    public static final String BLUETOOTH_STATE = "BluetoothState";
    public static final String TAG_BLUETOOTH_CHANGED = "BluetoothStateChanged";
    private static BluetoothNotificationManager mInstance = new BluetoothNotificationManager();
    private BluetoothInfoChangedListener mBluetoothInfoListener;
    private String TAG = BluetoothNotificationManager.class.getSimpleName();
    protected Bluetooth mBluetooth = null;
    private Status mStatus = Status.Unknown;
    private Status mPower = Status.Unknown;

    /* loaded from: classes.dex */
    public enum Status {
        Unknown,
        PowerOff,
        PowerOn,
        Connect,
        Disconnect
    }

    public static BluetoothNotificationManager getInstance() {
        return mInstance;
    }

    public void resume(Context c) {
        if (this.mBluetooth == null) {
            this.mBluetooth = new Bluetooth();
            if (this.mBluetoothInfoListener == null) {
                this.mBluetoothInfoListener = new BluetoothInfoChangedListener();
                this.mBluetooth.setBluetoothInfoListener(this.mBluetoothInfoListener);
            }
        }
    }

    public void pause(Context c) {
        if (this.mBluetooth != null) {
            if (this.mBluetoothInfoListener != null) {
                this.mBluetooth.releaseBluetoothInfoListener();
                this.mBluetoothInfoListener = null;
            }
            this.mBluetooth.release();
            this.mBluetooth = null;
            this.mPower = Status.Unknown;
            this.mStatus = Status.Unknown;
        }
    }

    /* loaded from: classes.dex */
    private static class BluetoothInfoChangedListener implements Bluetooth.BluetoothInfoListener {
        private BluetoothInfoChangedListener() {
        }

        public void onChanged(Bluetooth.BluetoothInfo info) {
            BluetoothNotificationManager.getInstance().updateBluetoothInfo(info);
        }
    }

    protected void updateBluetoothInfo(Bluetooth.BluetoothInfo info) {
        if (info.power == 0) {
            this.mPower = Status.PowerOff;
        } else if (info.power == 1) {
            this.mPower = Status.PowerOn;
        }
        if (info.connect == 1) {
            this.mStatus = Status.Connect;
        } else if (info.connect == 0) {
            this.mStatus = Status.Disconnect;
        }
        notify(TAG_BLUETOOTH_CHANGED);
        Log.i(this.TAG, "onChanged : Power = " + this.mPower + ", Status = " + this.mStatus);
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        Status ret = Status.Unknown;
        if (tag == BLUETOOTH_POWER) {
            Status ret2 = this.mPower;
            return ret2;
        }
        if (tag == BLUETOOTH_STATE) {
            Status ret3 = this.mStatus;
            return ret3;
        }
        return ret;
    }

    public int getBluetoothInfo(String tag) {
        if (this.mBluetooth == null) {
            return 0;
        }
        Bluetooth.BluetoothInfo info = this.mBluetooth.getBluetoothInfo();
        if (tag.equals(BLUETOOTH_POWER)) {
            int i = info.power;
            return i;
        }
        if (tag.equals(BLUETOOTH_STATE)) {
            int i2 = info.connect;
            return i2;
        }
        Log.w(this.TAG, "Not Supported TAG :" + tag);
        return 0;
    }
}
