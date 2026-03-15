package com.sony.scalar.webapi.service.camera.v1_0.storageinformation;

import com.sony.mexi.orb.service.Callbacks;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.StorageInformationParams;

/* loaded from: classes.dex */
public interface GetStorageInformationCallback extends Callbacks {
    void returnCb(StorageInformationParams[] storageInformationParamsArr);
}
