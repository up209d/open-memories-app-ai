package com.sony.imaging.app.srctrl.util.cameraproxy;

import com.sony.imaging.app.srctrl.util.HandoffOperationInfo;
import com.sony.imaging.app.srctrl.util.OperationRequester;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.StorageInformationParams;

/* loaded from: classes.dex */
public class CameraProxyStorageInformation {
    private static String TAG = CameraProxyStorageInformation.class.getSimpleName();

    public static StorageInformationParams[] get() {
        StorageInformationParams[] result = (StorageInformationParams[]) new OperationRequester().request(23, HandoffOperationInfo.CameraSettings.STORAGE_INFORMATION);
        return result;
    }
}
