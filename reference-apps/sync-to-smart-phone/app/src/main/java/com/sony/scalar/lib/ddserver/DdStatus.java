package com.sony.scalar.lib.ddserver;

/* loaded from: classes.dex */
public enum DdStatus {
    OK,
    ERROR_SERVICE_START,
    ERROR_SERVICE_NOT_FOUND,
    ERROR_NETWORK,
    ERROR_FROM_SERVICE,
    ERROR_DEVICE_NAME;

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static DdStatus[] valuesCustom() {
        DdStatus[] valuesCustom = values();
        int length = valuesCustom.length;
        DdStatus[] ddStatusArr = new DdStatus[length];
        System.arraycopy(valuesCustom, 0, ddStatusArr, 0, length);
        return ddStatusArr;
    }
}
