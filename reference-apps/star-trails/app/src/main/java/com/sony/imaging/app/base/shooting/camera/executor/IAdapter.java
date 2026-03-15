package com.sony.imaging.app.base.shooting.camera.executor;

import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.CameraSequence;

/* loaded from: classes.dex */
public interface IAdapter {
    public static final String TAG = "IAdapter";

    boolean enableHalt(boolean z);

    void enableNextCapture(int i);

    String getName();

    boolean isSpecial();

    void lockCancelTakePicture(boolean z);

    void onProgress(int i, int i2);

    void onShutter(int i, CameraEx cameraEx);

    void prepare();

    void setExecutor(ShootingExecutor shootingExecutor);

    void setOptions(CameraSequence.Options options);

    void setProcess(IProcess iProcess);

    void takePicture();

    void terminate();
}
