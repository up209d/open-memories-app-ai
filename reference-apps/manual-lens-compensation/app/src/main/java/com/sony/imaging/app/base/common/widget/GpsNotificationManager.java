package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.Log;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.sysutil.didep.Gps;

/* loaded from: classes.dex */
public class GpsNotificationManager extends NotificationManager {
    public static final int API_VERSION_GPS_SUPPORTED = 12;
    public static final String GPS_POWER = "GpsPower";
    public static final String GPS_STATE = "GpsState";
    public static final String GPS_STATE_CHANGED = "GpsStateChanged";
    private static final String TAG = "GpsNotificationManager";
    private static Gps.gpsInfo mGpsInfo = null;
    private static GpsNotificationManager mInstance;
    private GpsInfoChangedListener mGpsInfoListener;
    private Gps mGps = null;
    private Status mStatus = Status.Unknown;
    private Status mPower = Status.Unknown;

    /* loaded from: classes.dex */
    public enum Status {
        Unknown,
        Ok,
        Inh,
        Ng,
        Off,
        PowerOff,
        PowerON
    }

    private GpsNotificationManager() {
    }

    public static GpsNotificationManager getInstance() {
        if (mInstance == null) {
            mInstance = new GpsNotificationManager();
        }
        return mInstance;
    }

    /* loaded from: classes.dex */
    private static class GpsInfoChangedListener implements Gps.GpsInfoListener {
        private GpsInfoChangedListener() {
        }

        public void onChanged(Gps.gpsInfo arg0) {
            GpsNotificationManager.getInstance().updateGpsInfo(arg0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGpsInfo(Gps.gpsInfo info) {
        mGpsInfo = info;
        if (mGpsInfo.state == 3) {
            this.mStatus = Status.Ok;
        } else if (mGpsInfo.state == 2) {
            this.mStatus = Status.Inh;
        } else if (mGpsInfo.state == 0) {
            this.mStatus = Status.Ng;
        } else if (mGpsInfo.state == 1) {
            this.mStatus = Status.Off;
        }
        if (mGpsInfo.power == 0) {
            this.mPower = Status.PowerOff;
        } else if (mGpsInfo.power == 1) {
            this.mPower = Status.PowerON;
        }
        notify(GPS_STATE_CHANGED);
        Log.i(TAG, "onChanged : Power = " + this.mPower + ", Status = " + this.mStatus);
    }

    public void resume(Context c) {
        if (isGpsSupportedPF()) {
            if (this.mGps == null) {
                this.mGps = new Gps();
            }
            if (this.mGpsInfoListener == null) {
                this.mGpsInfoListener = new GpsInfoChangedListener();
            }
            this.mGps.setGpsInfoListener(this.mGpsInfoListener);
        }
    }

    public void pause(Context c) {
        if (isGpsSupportedPF() && this.mGps != null) {
            if (this.mGpsInfoListener != null) {
                this.mGps.releaseGpsInfoListener();
                this.mGpsInfoListener = null;
            }
            this.mGps.release();
            this.mGps = null;
        }
        this.mStatus = Status.Unknown;
        this.mPower = Status.Unknown;
        mGpsInfo = null;
    }

    protected static boolean isGpsSupportedPF() {
        if (12 > Environment.getVersionPfAPI() || ScalarProperties.getInt("device.gps.supported") == 0) {
            return false;
        }
        return true;
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        Status ret = Status.Unknown;
        if (isGpsSupportedPF()) {
            if (tag == GPS_POWER) {
                return this.mPower;
            }
            if (tag == GPS_STATE) {
                return this.mStatus;
            }
            return ret;
        }
        return ret;
    }
}
