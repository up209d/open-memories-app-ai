package com.sony.imaging.app.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.List;

/* loaded from: classes.dex */
public class GyroscopeObserver extends NotificationManager {
    static final String TAG = "GyroscopeObserver";
    public static final String TAG_GYROSCOPE = "Gyroscope";
    private static GyroscopeObserver mObserver = new GyroscopeObserver();
    private Context mContext;
    private boolean mHasDigitalLevel;
    private SensorManager mSensorManager;
    private float[] mRotationMatrix = new float[9];
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] mAttitude = new float[3];
    private SensorEventListener mSensorEventListener = new SensorEventListener() { // from class: com.sony.imaging.app.util.GyroscopeObserver.1
        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(GyroscopeObserver.TAG, "onAccuracyChanged:" + accuracy);
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case 1:
                    GyroscopeObserver.this.mGravity = (float[]) event.values.clone();
                    break;
                case 2:
                    GyroscopeObserver.this.mGeomagnetic = (float[]) event.values.clone();
                    break;
            }
            if (GyroscopeObserver.this.mGravity != null) {
                SensorManager.getRotationMatrix(GyroscopeObserver.this.mRotationMatrix, null, GyroscopeObserver.this.mGravity, GyroscopeObserver.this.mGeomagnetic);
                SensorManager.getOrientation(GyroscopeObserver.this.mRotationMatrix, GyroscopeObserver.this.mAttitude);
                if (event.sensor.getType() == 1) {
                    GyroscopeObserver.this.mNotification.onDeviceChanged(GyroscopeObserver.TAG_GYROSCOPE, GyroscopeObserver.this.mAttitude);
                    GyroscopeObserver.this.notify(GyroscopeObserver.TAG_GYROSCOPE);
                }
            }
        }
    };
    private ValueMapNotificationManager mNotification = new ValueMapNotificationManager(false);

    private GyroscopeObserver() {
    }

    public static GyroscopeObserver getInstance() {
        return mObserver;
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    public Object getValue(String tag) {
        return this.mNotification.getValue(tag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.util.NotificationManager
    public void onFirstListenerSet(String tag) {
        if (this.mHasDigitalLevel) {
            this.mSensorManager.registerListener(this.mSensorEventListener, this.mSensorManager.getDefaultSensor(1), 1);
            this.mSensorManager.registerListener(this.mSensorEventListener, this.mSensorManager.getDefaultSensor(2), 1);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationManager
    protected void onAllListenerRemoved(String tag) {
        this.mSensorManager.unregisterListener(this.mSensorEventListener);
    }

    public void setContext(Context context) {
        this.mContext = context;
        this.mSensorManager = (SensorManager) this.mContext.getSystemService("sensor");
        this.mHasDigitalLevel = false;
        List<Sensor> list = this.mSensorManager.getSensorList(-1);
        if (list != null) {
            for (Sensor s : list) {
                if (s.getType() == 1) {
                    this.mHasDigitalLevel = true;
                    return;
                }
            }
        }
    }

    public boolean hasDigitalLevel() {
        return this.mHasDigitalLevel;
    }
}
